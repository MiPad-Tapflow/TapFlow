package com.xiaomi.mslgrdp.multwindow;

import android.content.Context;
import android.util.Log;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.xiaomi.mslgrdp.multwindow.base.Module;
import com.xiaomi.mslgrdp.multwindow.imp.FreerdpEventListenerAdapter;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.MslgLogger;
import com.xiaomi.mslgrdp.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes6.dex */
public class SessionManager implements Module {
    private static final int STATE_BINDING_SERVICE = 6;
    private static final int STATE_CONNECTED = 2;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECT_FAILED = 4;
    private static final int STATE_DISCONNECT = 3;
    private static final int STATE_IDL = 0;
    private static final int STATE_PRE_CONNECT = 5;
    private static final String TAG = "SessionManager";
    private static Map<Long, SessionState> sessionMap;
    private SessionState currentSession;
    private HashSet<IConnectListener> connectListeners = new HashSet<>();
    private volatile int mState = 0;
    private Object lock = new Object();

    public void addConnectListener(IConnectListener connectListener) {
        if (connectListener != null) {
            this.connectListeners.add(connectListener);
        }
    }

    public void removeConnectListener(IConnectListener connectListener) {
        if (connectListener != null) {
            this.connectListeners.remove(connectListener);
        }
    }

    public SessionState getCurrentSession() {
        return this.currentSession;
    }

    public SessionState createSession(Context context) {
        SessionState session = new SessionState(LibFreeRDP.newInstance(context));
        sessionMap.put(Long.valueOf(session.getInstance()), session);
        this.currentSession = session;
        return session;
    }

    public boolean canConnect() {
        boolean z;
        synchronized (this.lock) {
            z = true;
            if (this.mState == 1 || this.mState == 2 || this.mState == 6) {
                z = false;
            }
        }
        return z;
    }

    public boolean isConnected() {
        boolean z;
        synchronized (this.lock) {
            z = this.mState == 2;
        }
        return z;
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

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void init() {
        sessionMap = Collections.synchronizedMap(new HashMap());
        LibFreeRDP.setEventListener(new FreerdpConnectStateListener());
    }

    public void connectReady(int screenWidth, int screenHeight) {
        if (screenHeight > screenWidth) {
            screenHeight = screenWidth;
            screenWidth = screenHeight;
        }
        Constances.SCREEN_WIDTH = screenWidth;
        Constances.SCREEN_HEIGHT = screenHeight;
        MslgLogger.LOGD(TAG, "connectServer width = " + Constances.SCREEN_WIDTH + " height = " + Constances.SCREEN_HEIGHT, false);
    }

    public SessionState connectServer(Context context) {
        if (this.mState == 1 || this.mState == 2) {
            return this.currentSession;
        }
        this.mState = 1;
        MslgLogger.LOGD(TAG, " connectServer-----", true);
        final SessionState session = createSession(context);
        session.addUIEventListener(MultiWindowManager.getManager().getUiEventListener());
        Thread thread = new Thread(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.SessionManager.1
            @Override // java.lang.Runnable
            public void run() {
                session.connect();
            }
        });
        thread.start();
        return session;
    }

    public void bindingService() {
        synchronized (this.lock) {
            this.mState = 6;
        }
    }

    public SessionState connectServer(Context context, LibFreeRDP.UIEventListener uiEventListener) {
        synchronized (this.lock) {
            if (this.mState != 1 && this.mState != 2) {
                this.mState = 1;
                MslgLogger.LOGD(TAG, " connectServer-----", true);
                final SessionState session = createSession(context);
                session.addUIEventListener(uiEventListener);
                Thread thread = new Thread(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.SessionManager.2
                    @Override // java.lang.Runnable
                    public void run() {
                        session.connect();
                    }
                });
                thread.start();
                return session;
            }
            return this.currentSession;
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void destroy() {
        SessionState sessionState = this.currentSession;
        if (sessionState != null) {
            LibFreeRDP.disconnect(sessionState.getInstance());
            freeSession(this.currentSession.getInstance());
            this.currentSession = null;
        }
    }

    public void setCurrentSession(SessionState session) {
        synchronized (this.lock) {
            this.currentSession = session;
            this.mState = session != null ? 2 : 0;
        }
    }

    /* loaded from: classes6.dex */
    class FreerdpConnectStateListener extends FreerdpEventListenerAdapter {
        FreerdpConnectStateListener() {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
        public void OnConnectionSuccess(final long instance) {
            Log.v(SessionManager.TAG, "OnConnectionSuccess");
            SessionManager.this.mState = 2;
            MultiWindowManager.getManager().getMainHandler().post(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.SessionManager.FreerdpConnectStateListener.1
                @Override // java.lang.Runnable
                public void run() {
                    if (SessionManager.this.connectListeners != null) {
                        Iterator it = SessionManager.this.connectListeners.iterator();
                        while (it.hasNext()) {
                            IConnectListener item = (IConnectListener) it.next();
                            item.onConnectSuccess(instance);
                        }
                    }
                }
            });
            LinuxInputMethod.getInstance();
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
        public void OnConnectionFailure(final long instance) {
            Log.v(SessionManager.TAG, "OnConnectionFailure");
            Utils.setProperty("sys.mslg.isalive", "false");
            Utils.setProperty("sys.mslg.caj.isalive", "false");
            SessionManager.this.mState = 4;
            MultiWindowManager.getManager().getMainHandler().post(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.SessionManager.FreerdpConnectStateListener.2
                @Override // java.lang.Runnable
                public void run() {
                    Iterator it = SessionManager.this.connectListeners.iterator();
                    while (it.hasNext()) {
                        IConnectListener item = (IConnectListener) it.next();
                        item.onConnectFailure(instance);
                    }
                }
            });
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
        public void OnDisconnecting(final long instance) {
            super.OnDisconnecting(instance);
            SessionManager.this.mState = 4;
            MultiWindowManager.getManager().getMainHandler().post(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.SessionManager.FreerdpConnectStateListener.3
                @Override // java.lang.Runnable
                public void run() {
                    Iterator it = SessionManager.this.connectListeners.iterator();
                    while (it.hasNext()) {
                        IConnectListener item = (IConnectListener) it.next();
                        item.onDisconnecting(instance);
                    }
                }
            });
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
        public void OnDisconnected(final long instance) {
            Log.v(SessionManager.TAG, "OnDisconnected");
            Utils.setProperty("sys.mslg.isalive", "false");
            Utils.setProperty("sys.mslg.caj.isalive", "false");
            SessionManager.this.mState = 3;
            MultiWindowManager.getManager().getMainHandler().post(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.SessionManager.FreerdpConnectStateListener.4
                @Override // java.lang.Runnable
                public void run() {
                    Iterator it = SessionManager.this.connectListeners.iterator();
                    while (it.hasNext()) {
                        IConnectListener item = (IConnectListener) it.next();
                        item.onDisconnected(instance);
                    }
                }
            });
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
        public void OnPreConnect(long instance) {
            super.OnPreConnect(instance);
            Log.v(SessionManager.TAG, "OnPreConnect");
        }
    }

    /* loaded from: classes6.dex */
    public static abstract class IConnectListener {
        public abstract void onConnectSuccess(long j);

        public void onConnectFailure(long sessionID) {
        }

        public void onDisconnected(long sessionID) {
        }

        public void onDisconnecting(long sessionID) {
        }
    }
}