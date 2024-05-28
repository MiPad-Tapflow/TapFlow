package com.xiaomi.mslgrdp.multwindow;

import android.R;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.view.WindowMetrics;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.xiaomi.mslgrdp.multwindow.IServer;
import com.xiaomi.mslgrdp.multwindow.SessionManager;
import com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter;
import com.xiaomi.mslgrdp.presentation.CajViewerActivity;
import com.xiaomi.mslgrdp.presentation.LinuxVirtualActivity;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.MslgLogger;
import com.xiaomi.mslgrdp.utils.Utils;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/* loaded from: classes6.dex */
public class MultiWindowService extends Service {
    private static final String CHANNEL_ID = "com.xiaomi.mslgrdp";
    private static final String Caj_Image_File_Name = "apps";
    private static final String Copy_File_Path = "/data/rootfs";
    private static final int NOTIFICATION_ID = 999;
    private static final String NOTIFICATION_NAME = "MultiWindowService";
    private static final String Wps_Image_File_Name = "kingsoft";
    private static List<File> mFolders = Utils.traverseFolder(Environment.getExternalStorageDirectory());
    private LibFreeRDPBroadcastReceiver libFreeRDPBroadcastReceiver;
    private String mFileUrl;
    private NotificationManager mNotificationManager;
    private String mOpenApp;
    private final String TAG = NOTIFICATION_NAME;
    private HandlerThread mHandlerThread = new HandlerThread("server");
    private RdpServiceHandler mHandler = null;
    private FreerdpUiEventListener uiEventListener = new FreerdpUiEventListener();
    private SessionBinder sessionBinder = new SessionBinder();
    private RemoteCallbackList<IAppClient> appClients = new RemoteCallbackList<>();
    private SessionManager sessionManager = MultiWindowManager.getSessionManager();
    private Object lock = new Object();
    private boolean isSend = false;
    private boolean isRailReady = false;
    private String mLastCilpData = "";
    private int mCurrentAppType = -1;
    private boolean isStartFileObserver = false;
    private final File wpsimg = new File("/data/rootfs/mslgkingsoftimg");
    private final File cajimg = new File("/data/rootfs/mslgappsimg");
    private RDPConnectListener mRDPConnectListener = new RDPConnectListener();
    private WPSFileObserver mWpsFileObserver = new WPSFileObserver(mFolders, 256);
    private LinuxInputMethod.IMActivateListener imActivateListener = new LinuxInputMethod.IMActivateListener() { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowService.1
        @Override // com.freerdp.freerdpcore.services.LinuxInputMethod.IMActivateListener
        public void imActivate(int appType) {
            MultiWindowService.this.toggleImActivator(true, appType);
        }

        @Override // com.freerdp.freerdpcore.services.LinuxInputMethod.IMActivateListener
        public void imDeactivate(int appType) {
            MultiWindowService.this.toggleImActivator(false, appType);
        }

        @Override // com.freerdp.freerdpcore.services.LinuxInputMethod.IMActivateListener
        public void imCursorRect(int appType, int left, int top, int width, int height) {
            RemoteCallbackList remoteCallbackList;
            synchronized (MultiWindowService.this.lock) {
                int N = MultiWindowService.this.appClients.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        try {
                            int type = ((Integer) MultiWindowService.this.appClients.getBroadcastCookie(i)).intValue();
                            if (type == MultiWindowService.this.mCurrentAppType) {
                                ((IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i)).UpdateCursorRect(appType, left, top, width, height);
                            }
                        } catch (RemoteException e) {
                            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "imCursorRect error", true);
                            remoteCallbackList = MultiWindowService.this.appClients;
                        }
                    } catch (Throwable th) {
                        MultiWindowService.this.appClients.finishBroadcast();
                        throw th;
                    }
                }
                remoteCallbackList = MultiWindowService.this.appClients;
                remoteCallbackList.finishBroadcast();
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public void toggleImActivator(boolean activate, int appType) {
        RemoteCallbackList<IAppClient> remoteCallbackList;
        synchronized (this.lock) {
            int N = this.appClients.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    try {
                        int type = ((Integer) this.appClients.getBroadcastCookie(i)).intValue();
                        if (type == this.mCurrentAppType) {
                            this.appClients.getBroadcastItem(i).inputMethodActivate(activate);
                        }
                    } catch (Throwable th) {
                        this.appClients.finishBroadcast();
                        throw th;
                    }
                } catch (RemoteException e) {
                    MslgLogger.LOGD(NOTIFICATION_NAME, "toggleImActivator error", true);
                    remoteCallbackList = this.appClients;
                }
            }
            remoteCallbackList = this.appClients;
            remoteCallbackList.finishBroadcast();
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        this.libFreeRDPBroadcastReceiver = new LibFreeRDPBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_DATA_CLEARED");
        filter.addDataScheme("package");
        registerReceiver(this.libFreeRDPBroadcastReceiver, filter, Context.RECEIVER_EXPORTED);
        this.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.mHandlerThread.start();
        this.mHandler = new RdpServiceHandler(this.mHandlerThread.getLooper());
        MultiWindowManager.getManager().addIMActivateListener(this.imActivateListener);
        RdpServiceHandler rdpServiceHandler = this.mHandler;
        if (rdpServiceHandler != null) {
            rdpServiceHandler.sendEmptyMessageDelayed(100, 0L);
        }
    }

    /* loaded from: classes6.dex */
    private final class RdpServiceHandler extends Handler {
        private static final int MSG_CLEAR_USERDATA = 105;
        private static final int MSG_CONNECT_RDP = 100;
        private static final int MSG_DELETE_IMAGE = 103;
        private static final int MSG_MSLG_RESTART = 104;
        private static final int MSG_SNED_CLIP = 101;
        private static final int MSG_SNED_REMOTE_APP = 102;

        private RdpServiceHandler(Looper looper) {
            super(looper);
            Log.i(MultiWindowService.NOTIFICATION_NAME, "RdpServiceHandler ");
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 100:
                    WindowManager wm = (WindowManager) MultiWindowService.this.getSystemService(WINDOW_SERVICE);
                    WindowMetrics windowMetrics = wm.getMaximumWindowMetrics();
                    int width = windowMetrics.getBounds().width();
                    int height = windowMetrics.getBounds().height();
                    MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "connectServer width = " + width + " height = " + height, false);
                    MultiWindowManager.getSessionManager().connectReady(width, height);
                    SessionManager sessionManager = MultiWindowService.this.sessionManager;
                    MultiWindowService multiWindowService = MultiWindowService.this;
                    sessionManager.connectServer(multiWindowService, multiWindowService.uiEventListener);
                    MultiWindowService.this.sessionManager.addConnectListener(MultiWindowService.this.mRDPConnectListener);
                    return;
                case 101:
                    String data = MultiWindowService.this.getClipData();
                    MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "OnRailChannelReady onClipboardChanged: " + data, false);
                    if (!data.isEmpty()) {
                        MultiWindowService.this.mLastCilpData = data;
                        SessionState state = MultiWindowService.this.sessionManager.getCurrentSession();
                        if (state != null) {
                            LibFreeRDP.sendClipboardData(state.getInstance(), data);
                            return;
                        }
                        return;
                    }
                    return;
                case 102:
                    MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "MSG_SNED_REMOTE_APP isRailReady  " + MultiWindowService.this.isRailReady, false);
                    if (MultiWindowService.this.isRailReady) {
                        String str = MultiWindowService.this.mFileUrl;
                        MultiWindowService multiWindowService2 = MultiWindowService.this;
                        Utils.sendPath(false, str, multiWindowService2, multiWindowService2.mOpenApp);
                        MultiWindowService.this.mFileUrl = null;
                        MultiWindowService.this.mOpenApp = null;
                        return;
                    }
                    if (MultiWindowService.this.mHandler != null) {
                        MultiWindowService.this.mHandler.sendEmptyMessageDelayed(102, 150L);
                        return;
                    }
                    return;
                case 103:
                    int appType = ((Integer) msg.obj).intValue();
                    MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "MSG_DELETE_IMAGE   appType  " + appType, false);
                    if (appType == 1) {
                        Utils.setProperty("sys.uninstall.optimg", null);
                        Utils.setProperty("sys.uninstall.optimg", MultiWindowService.Wps_Image_File_Name);
                        MultiWindowService.this.wpsimg.delete();
                        Utils.setProperty("sys.mslg.wps.clearuserdata", null);
                        Utils.setProperty("sys.mslg.wps.clearuserdata", "1");
                    }
                    if (appType == 3) {
                        Utils.setProperty("sys.uninstall.optimg", null);
                        Utils.setProperty("sys.uninstall.optimg", MultiWindowService.Caj_Image_File_Name);
                        MultiWindowService.this.cajimg.delete();
                        Utils.setProperty("sys.mslg.caj.clearuserdata", null);
                        Utils.setProperty("sys.mslg.caj.clearuserdata", "1");
                        return;
                    }
                    return;
                case 104:
                    MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "setProperty mslg restart 1", false);
                    Utils.setProperty(Constances.VENDOR_MSLG_RESTART, "1");
                    if (!MultiWindowService.this.isRailReady) {
                        new Thread(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowService.RdpServiceHandler.1
                            @Override // java.lang.Runnable
                            public void run() {
                                try {
                                    Thread.sleep(300L);
                                } catch (InterruptedException e) {
                                    MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, " MSG_MSLG_RESTART " + e.toString(), false);
                                }
                                MultiWindowService.this.sessionManager.getCurrentSession().connect();
                            }
                        }).start();
                        return;
                    }
                    MultiWindowService multiWindowService3 = MultiWindowService.this;
                    multiWindowService3.appFinishandExit(multiWindowService3.sessionManager.getCurrentSession().getInstance(), 3);
                    MultiWindowService multiWindowService4 = MultiWindowService.this;
                    multiWindowService4.appFinishandExit(multiWindowService4.sessionManager.getCurrentSession().getInstance(), 1);
                    System.exit(0);
                    return;
                case 105:
                    int appType2 = ((Integer) msg.obj).intValue();
                    MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "MSG_CLEAR_USERDATA appType " + appType2, false);
                    if (appType2 == 1) {
                        Utils.setProperty("sys.mslg.wps.clearuserdata", null);
                        Utils.setProperty("sys.mslg.wps.clearuserdata", "1");
                    }
                    if (appType2 == 3) {
                        Utils.setProperty("sys.mslg.caj.clearuserdata", null);
                        Utils.setProperty("sys.mslg.caj.clearuserdata", "1");
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes6.dex */
    private class RDPConnectListener extends SessionManager.IConnectListener {
        private int mRetryCount;

        private RDPConnectListener() {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.SessionManager.IConnectListener
        public void onConnectSuccess(long sessionID) {
            RemoteCallbackList remoteCallbackList;
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onConnectSuccess-----", true);
            synchronized (MultiWindowService.this.lock) {
                int N = MultiWindowService.this.appClients.beginBroadcast();
                try {
                    try {
                        MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "updateSession client count = " + N, false);
                        for (int i = 0; i < N; i++) {
                            ((IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i)).updateSession(MultiWindowService.this.sessionManager.getCurrentSession());
                        }
                        remoteCallbackList = MultiWindowService.this.appClients;
                    } catch (RemoteException e) {
                        MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "updateSession error", true);
                        remoteCallbackList = MultiWindowService.this.appClients;
                    }
                    remoteCallbackList.finishBroadcast();
                } catch (Throwable th) {
                    MultiWindowService.this.appClients.finishBroadcast();
                    throw th;
                }
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.SessionManager.IConnectListener
        public void onConnectFailure(long sessionID) {
            super.onConnectFailure(sessionID);
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onConnectFailure-----", true);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.SessionManager.IConnectListener
        public void onDisconnecting(long sessionID) {
            super.onDisconnecting(sessionID);
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onDisconnecting-----", true);
            if (MultiWindowService.this.mHandler != null && this.mRetryCount <= 2 && !MultiWindowService.this.isRailReady) {
                MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onConnectFailure mRetryCount = " + this.mRetryCount, false);
                this.mRetryCount++;
                new Thread(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowService.RDPConnectListener.1
                    @Override // java.lang.Runnable
                    public void run() {
                        MultiWindowService.this.sessionManager.getCurrentSession().connect();
                    }
                }).start();
            } else {
                this.mRetryCount = 0;
                MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "mslg restart", true);
                Utils.setProperty(Constances.VENDOR_MSLG_RESTART, "0");
                MultiWindowService.this.mHandler.sendEmptyMessageDelayed(104, 300L);
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.SessionManager.IConnectListener
        public void onDisconnected(long sessionID) {
            super.onDisconnected(sessionID);
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onDisconnected-----", true);
        }
    }

    /* loaded from: classes6.dex */
    private class LibFreeRDPBroadcastReceiver extends BroadcastReceiver {
        private LibFreeRDPBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri uri = intent.getData();
            if (uri == null) {
                return;
            }
            String pkgName = uri.getSchemeSpecificPart();
            if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "ACTION_PACKAGE_REMOVED   = " + pkgName, false);
                if (pkgName.equals("com.xiaomi.wpslauncher") && MultiWindowService.this.wpsimg.exists()) {
                    SessionState state = MultiWindowService.this.sessionManager.getCurrentSession();
                    if (state != null) {
                        LibFreeRDP.sendKillappEvent(state.getInstance(), 1);
                        MultiWindowService multiWindowService = MultiWindowService.this;
                        multiWindowService.appFinishandExit(multiWindowService.sessionManager.getCurrentSession().getInstance(), 1);
                    }
                    if (MultiWindowService.this.mHandler != null) {
                        Message msg = MultiWindowService.this.mHandler.obtainMessage(103);
                        msg.obj = 1;
                        MultiWindowService.this.mHandler.sendMessageDelayed(msg, 2000L);
                    }
                }
                if (pkgName.equals("com.xiaomi.cajlauncher") && MultiWindowService.this.cajimg.exists()) {
                    SessionState state2 = MultiWindowService.this.sessionManager.getCurrentSession();
                    if (state2 != null) {
                        LibFreeRDP.sendKillappEvent(state2.getInstance(), 3);
                        MultiWindowService multiWindowService2 = MultiWindowService.this;
                        multiWindowService2.appFinishandExit(multiWindowService2.sessionManager.getCurrentSession().getInstance(), 3);
                    }
                    if (MultiWindowService.this.mHandler != null) {
                        Message msg2 = MultiWindowService.this.mHandler.obtainMessage(103);
                        msg2.obj = 3;
                        MultiWindowService.this.mHandler.sendMessageDelayed(msg2, 2000L);
                        return;
                    }
                    return;
                }
                return;
            }
            if (action.equals("android.intent.action.PACKAGE_DATA_CLEARED")) {
                MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "ACTION_PACKAGE_DATA_CLEARED = " + pkgName, false);
                if (pkgName.equals("com.xiaomi.wpslauncher") && MultiWindowService.this.wpsimg.exists()) {
                    SessionState state3 = MultiWindowService.this.sessionManager.getCurrentSession();
                    if (state3 != null) {
                        LibFreeRDP.sendKillappEvent(state3.getInstance(), 1);
                        MultiWindowService multiWindowService3 = MultiWindowService.this;
                        multiWindowService3.appFinishandExit(multiWindowService3.sessionManager.getCurrentSession().getInstance(), 1);
                    }
                    if (MultiWindowService.this.mHandler != null) {
                        Message msg3 = MultiWindowService.this.mHandler.obtainMessage(105);
                        msg3.obj = 1;
                        MultiWindowService.this.mHandler.sendMessageDelayed(msg3, 2000L);
                    }
                }
                if (pkgName.equals("com.xiaomi.cajlauncher") && MultiWindowService.this.cajimg.exists()) {
                    SessionState state4 = MultiWindowService.this.sessionManager.getCurrentSession();
                    if (state4 != null) {
                        LibFreeRDP.sendKillappEvent(state4.getInstance(), 3);
                        MultiWindowService multiWindowService4 = MultiWindowService.this;
                        multiWindowService4.appFinishandExit(multiWindowService4.sessionManager.getCurrentSession().getInstance(), 3);
                    }
                    if (MultiWindowService.this.mHandler != null) {
                        Message msg4 = MultiWindowService.this.mHandler.obtainMessage(105);
                        msg4.obj = 3;
                        MultiWindowService.this.mHandler.sendMessageDelayed(msg4, 2000L);
                    }
                }
            }
        }
    }

    public boolean appFinishandExit(long inst, int appType) {
        synchronized (this.lock) {
            int N = this.appClients.beginBroadcast();
            for (int i = 0; i < N; i++) {
                try {
                    try {
                        int type = ((Integer) this.appClients.getBroadcastCookie(i)).intValue();
                        if (type == appType) {
                            this.appClients.getBroadcastItem(i).appFinishandExit(inst, appType);
                        }
                    } catch (RemoteException e) {
                        MslgLogger.LOGD(NOTIFICATION_NAME, "appFinishandExit error", true);
                        this.appClients.finishBroadcast();
                        return true;
                    }
                } catch (Throwable th) {
                    this.appClients.finishBroadcast();
                    return true;
                }
            }
            this.appClients.finishBroadcast();
        }
        return true;
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, configureNotification());
        return Service.START_STICKY;
    }

    private Notification configureNotification() {
        this.mNotificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH));
        Intent intentChange = new Intent();
        intentChange.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intentChange.setData(uri);
        PendingIntent pendingIntentChange = PendingIntent.getActivity(this, 0, intentChange, PendingIntent.FLAG_IMMUTABLE); //0x04000000
        Notification notification = new Notification.Builder(this, CHANNEL_ID).setOngoing(true).setContentTitle("PC 框架").setContentText("服务正在运行").setSmallIcon(R.drawable.sym_def_app_icon).setContentIntent(pendingIntentChange).build();
        return notification;
    }

    public boolean isForeground() {
        ComponentName topActivity;
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(1);
        if (runningTasks != null && runningTasks.size() > 0 && (topActivity = runningTasks.get(0).topActivity) != null && (topActivity.getClassName().equals(LinuxVirtualActivity.class.getName()) || topActivity.getClassName().equals(CajViewerActivity.class.getName()))) {
            return true;
        }
        return false;
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return this.sessionBinder;
    }

    /* loaded from: classes6.dex */
    class FreerdpUiEventListener extends FreerdpUIEventListenerAdapter {
        FreerdpUiEventListener() {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public void OnRailChannelReady(boolean ready) {
            RemoteCallbackList remoteCallbackList;
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, " OnRailChannelReady", true);
            MultiWindowService.this.isRailReady = true;
            synchronized (MultiWindowService.this.lock) {
                int N = MultiWindowService.this.appClients.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        try {
                            ((IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i)).OnRailChannelReady(ready);
                        } catch (RemoteException e) {
                            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "OnRailChannelReady error", true);
                            remoteCallbackList = MultiWindowService.this.appClients;
                        }
                    } catch (Throwable th) {
                        MultiWindowService.this.appClients.finishBroadcast();
                        throw th;
                    }
                }
                if (MultiWindowService.this.mHandler != null) {
                    MultiWindowService.this.mHandler.sendEmptyMessageDelayed(101, 2000L);
                }
                remoteCallbackList = MultiWindowService.this.appClients;
                remoteCallbackList.finishBroadcast();
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int b_width, int b_height, int left, int top, int dirty_w, int dirty_h, int stride, String file_name, int size, boolean isPopWindow, boolean isAlpha, int appType, boolean isModal, boolean isMaximized) {
            RemoteCallbackList remoteCallbackList;
            FreerdpUiEventListener freerdpUiEventListener;
            boolean z;
            int N;
            FreerdpUiEventListener freerdpUiEventListener2 = this;
            int i = appType;
            synchronized (MultiWindowService.this.lock) {
                int N2 = MultiWindowService.this.appClients.beginBroadcast();
                int i2 = 0;
                while (i2 < N2) {
                    try {
                        int type = ((Integer) MultiWindowService.this.appClients.getBroadcastCookie(i2)).intValue();
                        MslgLogger.LOGD(MultiWindowManager.TAG,"type=="+type,false);
                        type=i;
                        if (type == i) {
                            MultiWindowService.this.mCurrentAppType = i;
                            MslgLogger.LOGD(MultiWindowManager.TAG, "invoke app OnGraphicsUpdateMultiWindow windid = " + windowId, true);
                            IAppClient iAppClient = (IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i2);
                            z = true;
                            N = N2;
                            try {
                                try {
                                    iAppClient.OnGraphicsUpdateMultiWindow(inst, windowId, x, y, b_width, b_height, left, top, dirty_w, dirty_h, stride, file_name, size, isPopWindow, isAlpha, appType, isModal, isMaximized);
                                } catch (Throwable th) {
                                    th = th;
                                    freerdpUiEventListener = this;
                                    MultiWindowService.this.appClients.finishBroadcast();
                                    throw th;
                                }
                            } catch (RemoteException e) {
                                MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "OnGraphicsUpdateMultiWindow error", z);
                                remoteCallbackList = MultiWindowService.this.appClients;
                                remoteCallbackList.finishBroadcast();
                            }
                        } else {
                            N = N2;
                        }
                        i2++;
                        freerdpUiEventListener2 = this;
                        i = appType;
                        N2 = N;
                    } catch (RemoteException e2) {
                        z = true;
                    } catch (Throwable th2) {
                        freerdpUiEventListener = this;
                        MultiWindowService.this.appClients.finishBroadcast();
                    }
                }
                remoteCallbackList = MultiWindowService.this.appClients;
                remoteCallbackList.finishBroadcast();
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public void onWindowResize(long inst, int windowId, int appType) {
            RemoteCallbackList remoteCallbackList;
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onWindowResize windowId = " + windowId + " appType = " + appType, false);
            synchronized (MultiWindowService.this.lock) {
                MultiWindowManager.getBitmapPool().removeBitmap(windowId);
                int N = MultiWindowService.this.appClients.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        try {
                            MultiWindowService.this.mCurrentAppType = appType;
                            ((IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i)).onWindowResize(inst, windowId, appType);
                        } catch (RemoteException e) {
                            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onWindowResize error", true);
                            remoteCallbackList = MultiWindowService.this.appClients;
                        }
                    } catch (Throwable th) {
                        MultiWindowService.this.appClients.finishBroadcast();
                        throw th;
                    }
                }
                remoteCallbackList = MultiWindowService.this.appClients;
                remoteCallbackList.finishBroadcast();
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public void onWindowClosed(long inst, int windowId, int appType) {
            RemoteCallbackList remoteCallbackList;
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onWindowClosed windowId = " + windowId + " appType = " + appType, false);
            synchronized (MultiWindowService.this.lock) {
                MultiWindowManager.getBitmapPool().removeBitmap(windowId);
                int N = MultiWindowService.this.appClients.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        try {
                            MultiWindowService.this.mCurrentAppType = appType;
                            ((IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i)).onWindowClosed(inst, windowId, appType);
                        } catch (RemoteException e) {
                            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onWindowClosed error", true);
                            remoteCallbackList = MultiWindowService.this.appClients;
                        }
                    } catch (Throwable th) {
                        MultiWindowService.this.appClients.finishBroadcast();
                        throw th;
                    }
                }
                remoteCallbackList = MultiWindowService.this.appClients;
                remoteCallbackList.finishBroadcast();
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public void OnDeleteOptimg(int appType) {
            RemoteCallbackList remoteCallbackList;
            super.OnDeleteOptimg(appType);
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, appType + "OnDeleteOptimg", false);
            synchronized (MultiWindowService.this.lock) {
                int N = MultiWindowService.this.appClients.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        try {
                            ((IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i)).OnDeleteOptimg(appType);
                        } catch (RemoteException e) {
                            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "OnDeleteOptimg error", true);
                            remoteCallbackList = MultiWindowService.this.appClients;
                        }
                    } catch (Throwable th) {
                        MultiWindowService.this.appClients.finishBroadcast();
                        throw th;
                    }
                }
                remoteCallbackList = MultiWindowService.this.appClients;
                remoteCallbackList.finishBroadcast();
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public boolean OnMinimizeRequested(int appType, boolean minimized) {
            synchronized (MultiWindowService.this.lock) {
                int N = MultiWindowService.this.appClients.beginBroadcast();
                for (int i = 0; i < N; i++) {
                    try {
                        try {
                            int type = ((Integer) MultiWindowService.this.appClients.getBroadcastCookie(i)).intValue();
                            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "OnMinimizeRequested type = " + type, false);
                            if (type == appType) {
                                ((IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i)).OnMinimizeRequested(MultiWindowService.this.sessionManager.getCurrentSession().getInstance(), appType, minimized);
                            }
                        } catch (RemoteException e) {
                            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "OnMinimizeRequested error", true);
                            MultiWindowService.this.appClients.finishBroadcast();
                            return true;
                        }
                    } catch (Throwable th) {
                        MultiWindowService.this.appClients.finishBroadcast();
                        return true;
                    }
                }
                MultiWindowService.this.appClients.finishBroadcast();
            }
            return true;
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public void OnRemoteClipboardChanged(String data) {
            MultiWindowManager.getManager().remoteClipboardChanged(data);
        }

        @Override
        public void OnUpdatePointerIcon(long inst, int width, int height, int hotSpotX, int hotSpotY) {
            RemoteCallbackList remoteCallbackList;
            int N;
            Throwable th;
            synchronized (MultiWindowService.this.lock) {
                try {
                    try {
                    } catch (Throwable th0) {
                        th = th0;
                    }
                    try {
                        Bitmap bitmap_pointer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        boolean result = LibFreeRDP.updatePointerIcon(inst, bitmap_pointer);
                        if (result) {
                            int N2 = MultiWindowService.this.appClients.beginBroadcast();
                            int i = 0;
                            while (i < N2) {
                                try {
                                    int type = ((Integer) MultiWindowService.this.appClients.getBroadcastCookie(i)).intValue();
                                    if (type == MultiWindowService.this.mCurrentAppType) {
                                        N = N2;
                                        try {
                                            try {
                                                ((IAppClient) MultiWindowService.this.appClients.getBroadcastItem(i)).OnUpdatePointerIcon(inst, width, height, hotSpotX, hotSpotY, bitmap_pointer);
                                            } catch (Throwable th2) {
                                                MultiWindowService.this.appClients.finishBroadcast();
                                                throw th2;
                                            }
                                        } catch (RemoteException e) {
                                            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "OnUpdatePointerIcon error", true);
                                            remoteCallbackList = MultiWindowService.this.appClients;
                                            remoteCallbackList.finishBroadcast();
                                            return;
                                        }
                                    } else {
                                        N = N2;
                                    }
                                    i++;
                                    N2 = N;
                                } catch (Throwable th3) {
                                    MultiWindowService.this.appClients.finishBroadcast();
                                    throw th3;
                                }
                            }
                            remoteCallbackList = MultiWindowService.this.appClients;
                            remoteCallbackList.finishBroadcast();
                            return;
                        }
                        MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "OnUpdatePointerIcon error", false);
                    } catch (Throwable th4) {
                        throw th4;
                    }
                } catch (Throwable th5) {
                    th = th5;
                }
            }
        }
    }

    /* loaded from: classes6.dex */
    class ClientDeathRecipient implements IBinder.DeathRecipient {
        public int appType;

        public ClientDeathRecipient(int appType) {
            this.appType = -1;
            this.appType = appType;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, " DeathRecipient appType = " + this.appType, false);
            SessionState state = MultiWindowService.this.sessionManager.getCurrentSession();
            if (state != null) {
                MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, " DeathRecipient sendKillappEvent appType = " + this.appType, false);
                LibFreeRDP.sendKillappEvent(state.getInstance(), this.appType);
                if (this.appType == 1) {
                    Utils.setProperty("sys.mslg.isalive", "false");
                }
                if (this.appType == 3) {
                    Utils.setProperty("sys.mslg.caj.isalive", "false");
                }
            }
            if (this.appType == 1) {
                MultiWindowService.this.stopFileObserver();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getClipData() {
        ClipboardManager clipboard = null;
        ClipData clipData;
        CharSequence label;
        try {
            clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!clipboard.hasPrimaryClip() || (clipData = clipboard.getPrimaryClip()) == null || clipData.getItemCount() <= 0 || ((label = clipData.getDescription().getLabel()) != null && label.equals("rdp-clipboard"))) {
            return "";
        }
        ClipData.Item item = clipData.getItemAt(0);
        if (item.getText() != null) {
            String text = item.getText().toString();
            return text;
        }
        return "";
    }

    /* loaded from: classes6.dex */
    class SessionBinder extends IServer.Stub {
        SessionBinder() {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void addClient(IAppClient client, int type) throws RemoteException {
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, " addClient ", false);
            MultiWindowService.this.appClients.register(client, Integer.valueOf(type));
            if (MultiWindowService.this.sessionManager.getCurrentSession() != null) {
                client.updateSession(MultiWindowService.this.sessionManager.getCurrentSession());
            }
            client.asBinder().linkToDeath(new ClientDeathRecipient(type), 0);
            if (type == 1) {
                MultiWindowService.this.startFileObserver();
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void removeClient(IAppClient client, int type) throws RemoteException {
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, " removeClient ", false);
            MultiWindowService.this.appClients.unregister(client);
            if (type == 1) {
                MultiWindowService.this.stopFileObserver();
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public boolean sendCursorEvent(long inst, int x, int y, int flags) throws RemoteException {
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "sendCursorEvent ", false);
            return LibFreeRDP.sendCursorEvent(inst, x, y, flags);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public boolean sendKeyEvent(long inst, int keycode, boolean down) throws RemoteException {
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "sendKeyEvent ", false);
            return LibFreeRDP.sendKeyEvent(inst, keycode, down);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public boolean sendUnicodeKeyEvent(long inst, int keycode, boolean down) throws RemoteException {
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "sendUnicodeKeyEvent ", false);
            return LibFreeRDP.sendUnicodeKeyEvent(inst, keycode, down);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendTypeUrl(long inst, String url, String app) throws RemoteException {
            if (!MultiWindowService.this.isRailReady) {
                if (MultiWindowService.this.mHandler != null) {
                    MultiWindowService.this.mHandler.sendEmptyMessageDelayed(102, 150L);
                    return;
                }
                return;
            }
            Utils.sendPath(true, url, MultiWindowService.this, app);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void setTypeUrl(String url, String app) throws RemoteException {
            MultiWindowService.this.mFileUrl = url;
            MultiWindowService.this.mOpenApp = app;
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendStringTo(String data) throws RemoteException {
            LinuxInputMethod.getInstance().sendStringToLinux(data);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void activityOnResumed(long inst, boolean resume) throws RemoteException {
            String data = MultiWindowService.this.getClipData();
            if (data.equals(MultiWindowService.this.mLastCilpData)) {
                return;
            }
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "onClipboardChanged: " + data, false);
            if (!data.isEmpty()) {
                MultiWindowService.this.mLastCilpData = data;
                LibFreeRDP.sendClipboardData(inst, data);
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendWindowEvent(long inst, int windId, int cmdId) throws RemoteException {
            LibFreeRDP.sendWindowEvent(inst, windId, cmdId);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendWindowFocusEvent(long inst, int windId, boolean focus, int appType) throws RemoteException {
            MultiWindowService.this.mCurrentAppType = appType;
            LibFreeRDP.sendWindowFocusEvent(inst, windId, focus);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void sendKillAppProcess(long inst, int appType) throws RemoteException {
            MslgLogger.LOGD(MultiWindowService.NOTIFICATION_NAME, "sendKillAppProcess appType = " + appType, true);
            LibFreeRDP.sendKillappEvent(inst, appType);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IServer
        public void backgroundOrNot(boolean bg, int appType) {
            if (appType == 1) {
                if (bg) {
                    MultiWindowService.this.stopFileObserver();
                } else {
                    MultiWindowService.this.startFileObserver();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startFileObserver() {
        if (!this.isStartFileObserver) {
            this.mWpsFileObserver.startWatching();
            this.isStartFileObserver = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopFileObserver() {
        if (this.isStartFileObserver) {
            this.mWpsFileObserver.stopWatching();
            this.isStartFileObserver = false;
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        MslgLogger.LOGD(NOTIFICATION_NAME, "onDestroy", false);
        super.onDestroy();
        this.isSend = false;
        this.isRailReady = false;
        LinuxInputMethod.getInstance().dispose();
        LibFreeRDPBroadcastReceiver libFreeRDPBroadcastReceiver = this.libFreeRDPBroadcastReceiver;
        if (libFreeRDPBroadcastReceiver != null) {
            unregisterReceiver(libFreeRDPBroadcastReceiver);
            this.libFreeRDPBroadcastReceiver = null;
        }
        if (this.isStartFileObserver) {
            stopFileObserver();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void insertMediaProvider(String file) {
        ContentValues values = new ContentValues();
        values.put("_data", file);
        ContentResolver resolver = getContentResolver();
        resolver.insert(MediaStore.Files.getContentUri("external"), values);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes6.dex */
    public class WPSFileObserver extends FileObserver {
        List<File> folders;

        public WPSFileObserver(List<File> files, int mask) {
            super(files, mask);
            this.folders = files;
        }

        @Override // android.os.FileObserver
        public void onEvent(int i, String fileName) {
            int event = i & 4095;
            switch (event) {
                case 256:
                    if (Utils.isWPSFile(fileName)) {
                        for (File file : this.folders) {
                            Path path = Paths.get(file.getPath(), fileName);
                            File f = path.toFile();
                            if (!f.isDirectory() && f.exists() && !f.isHidden()) {
                                MultiWindowService.this.insertMediaProvider(file.getPath() + File.separator + fileName);
                            }
                        }
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }
}