package com.xiaomi.mslgrdp.multwindow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.xiaomi.mslgrdp.application.GlobalApp;
import com.xiaomi.mslgrdp.application.SessionState;
import com.xiaomi.mslgrdp.domain.BookmarkBase;
import com.xiaomi.mslgrdp.multwindow.base.Module;
import com.xiaomi.mslgrdp.multwindow.imp.FreerdpEventListenerAdapter;
import com.xiaomi.mslgrdp.services.BookmarkDB;
import com.xiaomi.mslgrdp.services.ManualBookmarkGateway;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes5.dex */
public class SessionManager implements Module {
    private static final String TAG = "SessionManager";
    private static BookmarkDB bookmarkDB;
    private static ManualBookmarkGateway manualBookmarkGateway;
    private static Map<Long, SessionState> sessionMap;
    WeakReference<Context> app = null;

    public ManualBookmarkGateway getManualBookmarkGateway() {
        return manualBookmarkGateway;
    }

    public SessionState createSession(BookmarkBase bookmark, Context context) {
        SessionState session = new SessionState(LibFreeRDP.newInstance(context), bookmark);
        sessionMap.put(Long.valueOf(session.getInstance()), session);
        return session;
    }

    public SessionState createSession(Uri openUri, Context context) {
        SessionState session = new SessionState(LibFreeRDP.newInstance(context), openUri);
        sessionMap.put(Long.valueOf(session.getInstance()), session);
        return session;
    }

    public SessionState getSession(long instance) {
        return sessionMap.get(Long.valueOf(instance));
    }

    public ArrayList<SessionState> getSessions() {
        return new ArrayList<>(sessionMap.values());
    }

    public void freeSession(long instance) {
        if (sessionMap.containsKey(Long.valueOf(instance))) {
            sessionMap.remove(Long.valueOf(instance));
            LibFreeRDP.freeInstance(instance);
        }
    }

    public void init(Context context) {
        sessionMap = Collections.synchronizedMap(new HashMap());
        LibFreeRDP.setEventListener(new FreerdpConnectStateListener());
        BookmarkDB bookmarkDB2 = new BookmarkDB(context);
        bookmarkDB = bookmarkDB2;
        manualBookmarkGateway = new ManualBookmarkGateway(bookmarkDB2);
        this.app = new WeakReference<>(context);
    }

    public void sendRDPNotification(int type, long param) {
        Intent intent = new Intent(GlobalApp.ACTION_EVENT_FREERDP);
        intent.putExtra(GlobalApp.EVENT_TYPE, type);
        intent.putExtra(GlobalApp.EVENT_PARAM, param);
        WeakReference<Context> weakReference = this.app;
        if (weakReference != null && weakReference.get() != null) {
            this.app.get().sendBroadcast(intent);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
        Collection<SessionState> sessions = GlobalApp.getSessions();
        for (SessionState session : sessions) {
            LibFreeRDP.disconnect(session.getInstance());
            GlobalApp.freeSession(session.getInstance());
        }
        sessions.clear();
    }



    public class FreerdpConnectStateListener extends FreerdpEventListenerAdapter {
        FreerdpConnectStateListener() {
        }

        @Override
        public void OnConnectionSuccess(long instance) {
            Log.v(SessionManager.TAG, "OnConnectionSuccess");
            SessionManager.this.sendRDPNotification(1, instance);
        }

        @Override
        public void OnConnectionFailure(long instance) {
            Log.v(SessionManager.TAG, "OnConnectionFailure");
            SessionManager.this.sendRDPNotification(2, instance);
        }

        @Override
        public void OnDisconnected(long instance) {
            Log.v(SessionManager.TAG, "OnDisconnected");
            SessionManager.this.sendRDPNotification(3, instance);
        }

        @Override
        public void OnPreConnect(long instance) {
            super.OnPreConnect(instance);
            Log.v(SessionManager.TAG, "OnPreConnect");
            if (SessionManager.this.app != null && SessionManager.this.app.get() != null) {
                LinuxInputMethod.getInstance(SessionManager.this.app.get());
            }
        }
    }
}