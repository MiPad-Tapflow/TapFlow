package com.xiaomi.mslgrdp.application;

import android.app.Application;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;

/* loaded from: classes6.dex */
public class GlobalApp extends Application {
    public static final String ACTION_EVENT_FREERDP = "com.freerdp.freerdp.event.freerdp";
    public static final String EVENT_ERROR = "EVENT_ERROR";
    public static final String EVENT_PARAM = "EVENT_PARAM";
    public static final String EVENT_STATUS = "EVENT_STATUS";
    public static final String EVENT_TYPE = "EVENT_TYPE";
    public static final int FREERDP_EVENT_CONNECTION_FAILURE = 2;
    public static final int FREERDP_EVENT_CONNECTION_SUCCESS = 1;
    public static final int FREERDP_EVENT_DISCONNECTED = 3;
    private static final String TAG = "GlobalApp";
    public static Application application;

    public static Application getApplication() {
        return application;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        application = this;
        registerActivityLifecycleCallbacks(MultiWindowManager.getManager().getActivityLifecycleCallbacks());
        MultiWindowManager.getManager().init();
    }

    @Override // android.app.Application
    public void onTerminate() {
        super.onTerminate();
    }
}