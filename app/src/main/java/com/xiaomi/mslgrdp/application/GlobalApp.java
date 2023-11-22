package com.xiaomi.mslgrdp.application;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.topjohnwu.superuser.Shell;
import com.xiaomi.mslgrdp.domain.BookmarkBase;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.services.ManualBookmarkGateway;
import java.util.Collection;
import java.util.TimerTask;

import cn.ljlVink.Tapflow.FileSystemInfo;

/* loaded from: classes5.dex */
public class GlobalApp extends Application implements LibFreeRDP.EventListener {
    public static final String ACTION_EVENT_FREERDP = "com.freerdp.freerdp.event.freerdp";
    public static final String EVENT_ERROR = "EVENT_ERROR";
    public static final String EVENT_PARAM = "EVENT_PARAM";
    public static final String EVENT_STATUS = "EVENT_STATUS";
    public static final String EVENT_TYPE = "EVENT_TYPE";
    public static final int FREERDP_EVENT_CONNECTION_FAILURE = 2;
    public static final int FREERDP_EVENT_CONNECTION_SUCCESS = 1;
    public static final int FREERDP_EVENT_DISCONNECTED = 3;
    private static final String TAG = "GlobalApp";
    public FileSystemInfo info_usr = null;
    public FileSystemInfo info_opt = null;

    public static ManualBookmarkGateway getManualBookmarkGateway() {
        return MultiWindowManager.getSessionManager().getManualBookmarkGateway();
    }

    public static SessionState createSession(BookmarkBase bookmark, Context context) {
        return MultiWindowManager.getSessionManager().createSession(bookmark, context);
    }

    public static SessionState createSession(Uri openUri, Context context) {
        return MultiWindowManager.getSessionManager().createSession(openUri, context);
    }

    public static SessionState getSession(long instance) {
        return MultiWindowManager.getSessionManager().getSession(instance);
    }

    public static Collection<SessionState> getSessions() {
        return MultiWindowManager.getSessionManager().getSessions();
    }

    public static void freeSession(long instance) {
        MultiWindowManager.getSessionManager().freeSession(instance);
    }

    @Override // android.app.Application
    public void onCreate() {

        super.onCreate();
        Shell.getShell();

        registerActivityLifecycleCallbacks(MultiWindowManager.getManager().getActivityLifecycleCallbacks());
        MultiWindowManager.getSessionManager().init(this);
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
    public void OnPreConnect(long instance) {
        Log.v(TAG, "OnPreConnect. Init LinuxInputMethod.");
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
    public void OnConnectionSuccess(long instance) {
        Log.v(TAG, "OnConnectionSuccess");
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
    public void OnConnectionFailure(long instance) {
        Log.v(TAG, "OnConnectionFailure");
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
    public void OnDisconnecting(long instance) {
        Log.v(TAG, "OnDisconnecting");
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.EventListener
    public void OnDisconnected(long instance) {
        Log.v(TAG, "OnDisconnected");
    }

    /* loaded from: classes5.dex */
    private static class DisconnectTask extends TimerTask {
        private DisconnectTask() {
        }

        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
            Log.v("DisconnectTask", "Doing action");
            Collection<SessionState> sessions = GlobalApp.getSessions();
            for (SessionState session : sessions) {
                LibFreeRDP.disconnect(session.getInstance());
            }
        }
    }

    @Override // android.app.Application
    public void onTerminate() {
        super.onTerminate();
        LinuxInputMethod.getInstance(this).dispose();
    }
}