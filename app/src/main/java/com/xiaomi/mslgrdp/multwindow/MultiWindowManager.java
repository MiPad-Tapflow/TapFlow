package com.xiaomi.mslgrdp.multwindow;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArraySet;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.widget.Toast;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LibGraphicsUpdate;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import cn.ljlVink.Tapflow.R;
import com.xiaomi.mslgrdp.application.GlobalApp;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.IAppClient;
import com.xiaomi.mslgrdp.multwindow.IServer;
import com.xiaomi.mslgrdp.multwindow.base.BaseActivity;
import com.xiaomi.mslgrdp.multwindow.base.ISurface;
import com.xiaomi.mslgrdp.multwindow.base.Module;
import com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter;
import com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter;
import com.xiaomi.mslgrdp.presentation.CajDialogActivity;
import com.xiaomi.mslgrdp.presentation.CajViewerActivity;
import com.xiaomi.mslgrdp.presentation.LinuxVirtualActivity;
import com.xiaomi.mslgrdp.presentation.WPSDialogActivity;
import com.xiaomi.mslgrdp.utils.ClipboardManagerProxy;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.MslgLogger;
import com.xiaomi.mslgrdp.utils.Utils;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.LogManager;

/* loaded from: classes6.dex */
public class MultiWindowManager implements Module, ClipboardManagerProxy.OnClipboardChangedListener {
    public static final String TAG = "MultiWindowManager";
    public static volatile AtomicBoolean isDragging;
    private static HashMap<String, Module> modules;
    private Bitmap bitmap_pointer;
    protected IServer iSession;
    private ClipboardManagerProxy mClipboardManager;
    private String mFileUrl;
    private String mOpenApp;
    private PointerIcon pointerIcon;
    private static final MultiWindowManager manager = new MultiWindowManager();
    public static final Object lock = new Object();
    public boolean mIsAPPStarted = false;
    private FreerdpUiEventListener uiEventListener = new FreerdpUiEventListener();
    private WeakReference<LinuxInputMethod.IMActivateListener> imActivateListenerWR = null;
    private int appType = 1;
    private int modalWindowId = -1;
    private int modalTaskId = -1;
    private boolean fromMslg = false;
    private MultiWindowActivityLifecycleCallbacks activityLifecycleCallbacks = new MultiWindowActivityLifecycleCallbacks();
    private Handler mainHandler = new Handler(Looper.getMainLooper()) { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            MslgLogger.LOGD(MultiWindowManager.TAG, "msg.what = " + msg.what, false);
            switch (msg.what) {
                case 10000:
                    MslSurfaceInfo surfaceInfo = (MslSurfaceInfo) msg.obj;
                    MslSurfaceInfo origin = (MslSurfaceInfo) MultiWindowManager.this.mslSurfaceInfos.get(Integer.valueOf(surfaceInfo.id));
                    MultiWindowManager.this.mslSurfaceInfos.put(Integer.valueOf(surfaceInfo.id), surfaceInfo);
                    WeakReference<ISurface> surfaceWR = (WeakReference) MultiWindowManager.this.surfaceHashMap.get(Integer.valueOf(surfaceInfo.id));
                    MultiWindowManager.this.showWindow(surfaceWR, surfaceInfo, origin != null);
                    return;
                case Constances.MSG_REMOVE_SURFACE_INFO /* 10001 */:
                    int id = ((Integer) msg.obj).intValue();
                    MultiWindowManager.this.surfaceHashMap.remove(Integer.valueOf(id));
                    MultiWindowManager.this.mslSurfaceInfos.remove(Integer.valueOf(id));
                    MultiWindowManager.this.pendingCloseWindow.remove(Integer.valueOf(id));
                    MultiWindowManager.getBitmapPool().removeBitmap(id);
                    return;
                case Constances.MSG_ADD_SURFACE /* 10002 */:
                case Constances.MSG_REMOVE_SURFACE /* 10003 */:
                case Constances.MSG_REFRESH_UI /* 10004 */:
                case Constances.MSG_DISPLAY_TOAST /* 10007 */:
                case Constances.MSG_SHOW_PERMISSION_DIALOG /* 10008 */:
                case Constances.MSG_HOVER_MOVE /* 10009 */:
                default:
                    return;
                case Constances.MSG_LONG_PRESS /* 10005 */:
                    MslgLogger.LOGD("MslDragLayout.smali", "long press true", false);
                    MultiWindowManager.isDragging.set(true);
                    return;
                case Constances.MSG_UOPDATE_MOUSE_ICON /* 10006 */:
                    MslgLogger.LOGD("MslDragLayout.smali", "MSG_UOPDATE_MOUSE_ICON" + (MultiWindowManager.this.pointerIcon == null), false);
                    Activity top = MultiWindowManager.this.activityLifecycleCallbacks.getTopActivity();
                    LinuxVirtualActivity linuxVirtualActivity = (LinuxVirtualActivity) top;
                    linuxVirtualActivity.setPointerIcon(MultiWindowManager.this.pointerIcon);
                    return;
                case Constances.MSG_RECONNECT_SERVER /* 10010 */:
                    MultiWindowManager.this.bindConnectServer();
                    return;
                case Constances.MSG_SEND_PATH /* 10011 */:
                    if (MultiWindowManager.this.mainHandler != null && MultiWindowManager.this.mainHandler.hasMessages(Constances.MSG_SEND_PATH)) {
                        removeMessages(Constances.MSG_SEND_PATH);
                    }
                    if (MultiWindowManager.this.iSession != null) {
                        try {
                            MultiWindowManager.this.iSession.setTypeUrl(MultiWindowManager.this.mFileUrl, MultiWindowManager.this.mOpenApp);
                            if (MultiWindowManager.getSessionManager().getCurrentSession() != null) {
                                MultiWindowManager.this.iSession.sendTypeUrl(MultiWindowManager.getSessionManager().getCurrentSession().getInstance(), MultiWindowManager.this.mFileUrl, MultiWindowManager.this.mOpenApp);
                            } else {
                                MultiWindowManager.this.mainHandler.sendMessageDelayed(MultiWindowManager.this.mainHandler.obtainMessage(Constances.MSG_SEND_PATH), 200L);
                            }
                            return;
                        } catch (RemoteException e) {
                            return;
                        }
                    }
                    MultiWindowManager.this.mainHandler.sendMessageDelayed(MultiWindowManager.this.mainHandler.obtainMessage(Constances.MSG_SEND_PATH), 200L);
                    return;
                case Constances.MSG_ACTIVITY_RESUME /* 10012 */:
                    if (hasMessages(Constances.MSG_ACTIVITY_RESUME)) {
                        removeMessages(Constances.MSG_ACTIVITY_RESUME);
                    }
                    boolean isResume = ((Boolean) msg.obj).booleanValue();
                    if (MultiWindowManager.this.iSession != null) {
                        try {
                            if (MultiWindowManager.getSessionManager().getCurrentSession() != null) {
                                MultiWindowManager.this.iSession.activityOnResumed(MultiWindowManager.getSessionManager().getCurrentSession().getInstance(), isResume);
                                return;
                            }
                            return;
                        } catch (RemoteException e2) {
                            return;
                        }
                    }
                    return;
                case 10013:
                    Toast.makeText(GlobalApp.getApplication(), R.string.exit_app_again, Toast.LENGTH_LONG).show();
                    MultiWindowManager.this.appFinishedExit();
                    return;
            }
        }
    };
    private volatile Map<Integer, Set<Integer>> activity2PopupWindows = Collections.synchronizedMap(new HashMap());
    private volatile Map<Integer, MslSurfaceInfo> mslSurfaceInfos = Collections.synchronizedMap(new HashMap());
    private volatile Map<Integer, WeakReference<ISurface>> surfaceHashMap = Collections.synchronizedMap(new HashMap());
    private volatile Set<Integer> pendingCloseWindow = Collections.synchronizedSet(new HashSet());
    private int mCurrentWindowId = -1;
    private int mAppType = 0;
    private IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowManager$$ExternalSyntheticLambda0
        @Override // android.os.IBinder.DeathRecipient
        public final void binderDied() {
            MultiWindowManager.lambda$new$1();
        }
    };
    private IAppClient appClient = new IAppClient.Stub() { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowManager.3
        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void updateSession(SessionState session) throws RemoteException {
            MslgLogger.LOGD(MultiWindowManager.TAG, "aidl updateSession---", false);
            MultiWindowManager.getSessionManager().setCurrentSession(session);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnRailChannelReady(boolean ready) throws RemoteException {
            Activity activity;
            if (ready && (activity = MultiWindowManager.this.getActivityLifecycleCallbacks().getTopActivity()) != null && (activity instanceof LinuxVirtualActivity)) {
                LinuxVirtualActivity linuxVirtualActivity = (LinuxVirtualActivity) activity;
                linuxVirtualActivity.OnRailChannelReady(ready);
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int b_width, int b_height, int left, int top, int dirty_w, int dirty_h, int stride, String file_name, int size, boolean isPopWindow, boolean isAlpha, int appType, boolean isModal, boolean isMaximized) throws RemoteException {
            synchronized (MultiWindowManager.lock) {
                Throwable th;
                try {
                    try {
                        MslgLogger.LOGD(MultiWindowManager.TAG, "OnGraphicsUpdateMultiWindow client file_name = " + file_name + " width = " + b_width + " height = " + b_height, true);
                        try {
                            MslSurfaceInfo surfaceInfo = new MslSurfaceInfo(inst, windowId, x, y, b_width, b_height, isPopWindow, isAlpha, appType, isModal, isMaximized);
                            if (!MultiWindowManager.isDragging.get()) {
                                Bitmap bitmap = MultiWindowManager.getBitmapPool().getBitmapWithWinId(windowId, b_width, b_height, file_name, size);
                                LibGraphicsUpdate memory = MultiWindowManager.getBitmapPool().getMemoryWithWinId(windowId);
                                boolean result = memory.updateGraphics(bitmap, b_width, b_height, stride, left, top, dirty_w, dirty_h);
                                if (!result) {
                                    MslgLogger.LOGD(MultiWindowManager.TAG, "bitmap fill error", true);
                                    return;
                                }
                                MultiWindowManager.getBitmapPool().putBitmap(windowId, bitmap);
                            }
                            MultiWindowManager.this.setAppType(appType);
                            MultiWindowManager.this.processAidl(surfaceInfo);
                        } catch (Throwable th0) {
                            throw th0;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void onWindowClosed(long inst, int windowId, int appType) throws RemoteException {
            synchronized (MultiWindowManager.lock) {
                LibFreeRDP.onWindowClosed(inst, windowId, appType);
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnMinimizeRequested(long inst, int appType, boolean minimized) throws RemoteException {
            LibFreeRDP.OnMinimizeRequested(inst, appType, minimized);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void appFinishandExit(long inst, int appType) throws RemoteException {
            MultiWindowManager.this.appFinishedExit();
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnDeleteOptimg(int appType) throws RemoteException {
            if (appType == MultiWindowManager.this.getAppType()) {
                MslgLogger.LOGD(MultiWindowManager.TAG, "OnDeleteOptimg appType = " + appType, false);
                MultiWindowManager.this.mainHandler.sendEmptyMessage(10013);
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void inputMethodActivate(final boolean activate) throws RemoteException {
            synchronized (MultiWindowManager.lock) {
                LinuxInputMethod.mInputActivate = activate;
                MultiWindowManager.this.getMainHandler().post(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowManager.3.1
                    @Override // java.lang.Runnable
                    public void run() {
                        if (MultiWindowManager.this.getImActivateListener() != null) {
                            if (activate) {
                                MultiWindowManager.this.getImActivateListener().imActivate(MultiWindowManager.this.appType);
                            } else {
                                MultiWindowManager.this.getImActivateListener().imDeactivate(MultiWindowManager.this.appType);
                            }
                        }
                    }
                });
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void OnUpdatePointerIcon(long inst, int width, int height, int hotSpotX, int hotSpotY, Bitmap bitmap) throws RemoteException {
            MultiWindowManager.this.OnUpdateMousePointer(inst, width, height, hotSpotX, hotSpotY, bitmap);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void UpdateCursorRect(int appType, int left, int top, int width, int height) throws RemoteException {
            if (MultiWindowManager.this.getImActivateListener() != null) {
                MultiWindowManager.this.getImActivateListener().imCursorRect(appType, left, top, width, height);
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.IAppClient
        public void onWindowResize(long inst, int windowId, int appType) throws RemoteException {
            synchronized (MultiWindowManager.lock) {
                MslgLogger.LOGD(MultiWindowManager.TAG, "onWindowResize windowId =" + windowId, false);
                MultiWindowManager.getBitmapPool().removeBitmap(windowId);
            }
        }
    };

    static {
        HashMap<String, Module> hashMap = new HashMap<>();
        modules = hashMap;
        hashMap.put(BitmapsPool.class.getSimpleName(), new BitmapsPool());
        modules.put(SessionManager.class.getSimpleName(), new SessionManager());
        modules.put(MouseManager.class.getSimpleName(), new MouseManager());
        isDragging = new AtomicBoolean(false);
    }

    public void setmIsAPPStarted(boolean mIsAPPStarted) {
        this.mIsAPPStarted = mIsAPPStarted;
    }

    public int getModalWindowId() {
        return this.modalWindowId;
    }

    public void setModalWindowId(int modalWindowId) {
        this.modalWindowId = modalWindowId;
    }

    public int getModalTaskId() {
        return this.modalTaskId;
    }

    public boolean isFromMslg() {
        return this.fromMslg;
    }

    public void setFromMslg(boolean fromMslg) {
        this.fromMslg = fromMslg;
    }

    public boolean ismIsAPPStarted() {
        return this.mIsAPPStarted;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public int getAppType() {
        return this.appType;
    }

    public LinuxInputMethod.IMActivateListener getImActivateListener() {
        WeakReference<LinuxInputMethod.IMActivateListener> weakReference = this.imActivateListenerWR;
        if (weakReference != null && weakReference.get() != null) {
            return this.imActivateListenerWR.get();
        }
        return null;
    }

    public void addIMActivateListener(LinuxInputMethod.IMActivateListener listener) {
        this.imActivateListenerWR = new WeakReference<>(listener);
    }

    public FreerdpUiEventListener getUiEventListener() {
        return this.uiEventListener;
    }

    public static BitmapsPool getBitmapPool() {
        return (BitmapsPool) modules.get(BitmapsPool.class.getSimpleName());
    }

    public static SessionManager getSessionManager() {
        return (SessionManager) modules.get(SessionManager.class.getSimpleName());
    }

    public static MouseManager getMouseManager() {
        return (MouseManager) modules.get(MouseManager.class.getSimpleName());
    }

    public void sendMouseEvent2Native(MotionEvent e) {
        MouseManager mouseManager = (MouseManager) modules.get(MouseManager.class.getSimpleName());
        mouseManager.getMouseThreadHandler().obtainMessage(Constances.MSG_HOVER_MOVE, e).sendToTarget();
    }

    public boolean sendPath(String url, String app) {
        this.mFileUrl = url;
        this.mOpenApp = app;
        MslgLogger.LOGD("mslg",mFileUrl+" "+mOpenApp,true);
        Message msg = this.mainHandler.obtainMessage(Constances.MSG_SEND_PATH);
        this.mainHandler.sendMessage(msg);
        return true;
    }

    public boolean sendString(String data) {
        if (this.iSession != null) {
            try {
                if (getSessionManager().getCurrentSession() != null) {
                    this.iSession.sendStringTo(data);
                    return true;
                }
                return true;
            } catch (RemoteException e) {
                return true;
            }
        }
        return true;
    }

    public boolean activityOnResume(Boolean resume) {
        Message msg = this.mainHandler.obtainMessage(Constances.MSG_ACTIVITY_RESUME);
        msg.obj = resume;
        this.mainHandler.sendMessageDelayed(msg, 2000L);
        return true;
    }

    public static MultiWindowManager getManager() {
        return manager;
    }

    public MultiWindowActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
        return this.activityLifecycleCallbacks;
    }

    public void runOnUiThread(Runnable runnable) {
        this.mainHandler.post(runnable);
    }

    public void setCurrentWindowId(int id) {
        this.mCurrentWindowId = id;
    }

    public int getmAppType() {
        return this.mAppType;
    }

    public void setmAppType(int mAppType) {
        this.mAppType = mAppType;
    }

    public boolean surfaceEmpty() {
        return this.surfaceHashMap.size() == 0;
    }

    public void addSurface(int id, ISurface surface) {
        this.surfaceHashMap.put(Integer.valueOf(id), new WeakReference<>(surface));
    }

    public boolean hasSurface(int windowId) {
        return this.surfaceHashMap.get(Integer.valueOf(windowId)) != null;
    }

    public void addSurfaceRecord(int id, MslSurfaceInfo surfaceInfo) {
        MslgLogger.LOGD(TAG, " surfaceInfo = " + surfaceInfo, false);
        Message msg = this.mainHandler.obtainMessage(10000);
        msg.obj = surfaceInfo;
        this.mainHandler.sendMessage(msg);
    }

    public void removeSurface(int id) {
        Message msg = this.mainHandler.obtainMessage(Constances.MSG_REMOVE_SURFACE_INFO);
        msg.obj = Integer.valueOf(id);
        this.mainHandler.sendMessage(msg);
    }

    public Handler getMainHandler() {
        return this.mainHandler;
    }

    public MslSurfaceInfo getSurfaceInfo(int windowId) {
        return this.mslSurfaceInfos.get(Integer.valueOf(windowId));
    }

    public synchronized void process(long inst, int windowId, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha, int appType) {
    }

    public synchronized void processAidl(MslSurfaceInfo surfaceInfo) {
        MslgLogger.LOGD(TAG, "processAidl Thread Name = " + Thread.currentThread().getName(), false);
        addSurfaceRecord(surfaceInfo.id, surfaceInfo);
    }

    public void OnUpdateMousePointer(long inst, int width, int height, int hotSpotX, int hotSpotY, Bitmap bitmap) {
        MslgLogger.LOGD(TAG, "OnUpdateMousePointer", false);
        this.bitmap_pointer = getMouseManager().move_mouse_pixel(bitmap, hotSpotX, hotSpotY);
        Activity top = this.activityLifecycleCallbacks.getTopActivity();
        View decorView = getControllerDecorView();
        if (decorView != null) {
            this.pointerIcon = getMouseManager().createPointerIcon(top, width, height, hotSpotX, hotSpotY, this.bitmap_pointer);
            Handler handler = this.mainHandler;
            if (handler != null) {
                handler.sendEmptyMessage(Constances.MSG_UOPDATE_MOUSE_ICON);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWindow(WeakReference<ISurface> surfaceWR, MslSurfaceInfo surfaceInfo, boolean update) {
        BaseActivity focusActivity;
        if (!update) {
            MslgLogger.LOGD(TAG, "process addWindow---", true);
            BaseActivity top = (BaseActivity) this.activityLifecycleCallbacks.getTopActivity();
            WeakReference<ISurface> focusSurfaceWeakReference = this.surfaceHashMap.get(Integer.valueOf(LibFreeRDP.mActivateWindowId));
            if (focusSurfaceWeakReference != null && focusSurfaceWeakReference.get() != null) {
                ISurface surface = focusSurfaceWeakReference.get();
                if ((surface instanceof BaseActivity) && (focusActivity = (BaseActivity) surface) != null && focusActivity != top && !focusActivity.isFinishing()) {
                    top = focusActivity;
                }
            }
            if (!surfaceInfo.isPopWindow && (surfaceInfo.width > 5 || surfaceInfo.height > 5)) {
                MslgLogger.LOGD(TAG, "start Activity--- surfaceId = " + surfaceInfo, true);
                if (top != null && !top.isFinishing() && top.windowId == -1) {
                    LinuxVirtualActivity activity = (LinuxVirtualActivity) top;
                    LibFreeRDP.mActivateWindowId = surfaceInfo.id;
                    activity.showWindowInfo(surfaceInfo.id);
                    return;
                }
                Intent intent = new Intent(top, (Class<?>) LinuxVirtualActivity.class); // HACK ::: allow other apps run !
                if (top != null) {
                    switch (this.appType) {
                        case 1:
                            intent = new Intent(top, (Class<?>) LinuxVirtualActivity.class);
                            break;
                        case 3:
                            intent = new Intent(top, (Class<?>) CajViewerActivity.class);
                            break;
                    }
                }
                if (intent != null) {
                    intent.putExtra(Constances.BUNDLE_ID_WINDOW_ID, surfaceInfo.id);
                    if (surfaceInfo.width >= Constances.SCREEN_WIDTH) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);//FLAG_ACTIVITY_CLEAR_TASK
                    } else {
                        int i = this.appType;
                        if (i == 1) {
                            intent.setComponent(new ComponentName(top, (Class<?>) WPSDialogActivity.class));
                        } else if (i == 3) {
                            intent.setComponent(new ComponentName(top, (Class<?>) CajDialogActivity.class));
                        }
                    }
                }
                if (top != null && !top.isFinishing()) {
                    if (surfaceInfo.isModal) {
                        this.modalWindowId = surfaceInfo.id;
                        this.modalTaskId = top.getTaskId();
                        MslgLogger.LOGD(MslgLogger.TAG_MODAL, "437 setModalTaskId = " + this.modalTaskId + " modalWindowId = " + this.modalWindowId, false);
                    } else {
                        MslSurfaceInfo topSurfaceInfo = this.mslSurfaceInfos.get(Integer.valueOf(top.windowId));
                        if (topSurfaceInfo != null && topSurfaceInfo.isModal) {
                            this.modalWindowId = surfaceInfo.id;
                            surfaceInfo.isModal = true;
                            this.modalTaskId = top.getTaskId();
                            int i2 = this.appType;
                            if (i2 == 1) {
                                intent.setComponent(new ComponentName(top, (Class<?>) WPSDialogActivity.class));
                            } else if (i2 == 3) {
                                intent.setComponent(new ComponentName(top, (Class<?>) CajDialogActivity.class));
                            }
                            intent.removeFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            MslgLogger.LOGD(MslgLogger.TAG_MODAL, "449 setModalTaskId = " + this.modalTaskId + " modalWindowId = " + surfaceInfo.id + " topact windowid = " + topSurfaceInfo.id, false);
                        }
                    }
                    top.startActivity(intent);
                    return;
                }
                if (this.surfaceHashMap.size() > 0) {
                    for (Map.Entry<Integer, WeakReference<ISurface>> surfaceEntry : this.surfaceHashMap.entrySet()) {
                        WeakReference<ISurface> v = surfaceEntry.getValue();
                        ISurface sf = v.get();
                        if (sf != null && (sf instanceof BaseActivity)) {
                            BaseActivity acItem = (BaseActivity) sf;
                            if (!acItem.isFinishing()) {
                                if (surfaceInfo.isModal) {
                                    this.modalTaskId = acItem.getTaskId();
                                    MslgLogger.LOGD(MslgLogger.TAG_MODAL, "450 setModalTaskId = " + this.modalTaskId + " windowid = " + surfaceInfo.id, false);
                                }
                                acItem.startActivity(intent);
                                return;
                            }
                        }
                    }
                    return;
                }
                return;
            }
            BaseActivity top2 = (BaseActivity) this.activityLifecycleCallbacks.getTopActivity();
            if (top2 == null || top2.isFinishing()) {
                MslgLogger.LOGD(TAG, "topActivity finishing retry show PopWindow " + surfaceInfo.id, true);
                this.mslSurfaceInfos.remove(Integer.valueOf(surfaceInfo.id));
                addSurfaceRecord(surfaceInfo.id, surfaceInfo);
                return;
            } else {
                if (this.activity2PopupWindows.get(Integer.valueOf(top2.windowId)) == null) {
                    Set<Integer> popWindows = new ArraySet<>();
                    popWindows.add(Integer.valueOf(surfaceInfo.id));
                    this.activity2PopupWindows.put(Integer.valueOf(top2.windowId), popWindows);
                }
                MslgLogger.LOGD(TAG, "show PopWindow---parent id = " + top2.windowId, true);
                LinuxWindowPopWindow.show(surfaceInfo.id, top2, surfaceInfo);
                return;
            }
        }
        MslgLogger.LOGD(TAG, "process updateWindow---", true);
        if (surfaceWR != null && surfaceWR.get() != null) {
            surfaceWR.get().updateWindow(surfaceInfo.id, surfaceInfo.x, surfaceInfo.y, surfaceInfo.width, surfaceInfo.height);
        }
    }

    public void restoreUbuntuProcess() {
        if (this.iSession != null) {
            try {
                if (getSessionManager().getCurrentSession() != null) {
                    this.iSession.sendKillAppProcess(getSessionManager().getCurrentSession().getInstance(), this.appType);
                    MslgLogger.LOGD(TAG, "remote sendKillAppProcess-- ", false);
                    this.surfaceHashMap.clear();
                    this.mslSurfaceInfos.clear();
                    this.pendingCloseWindow.clear();
                    getBitmapPool().destroy();
                    this.mainHandler.postDelayed(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowManager$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            MultiWindowManager.this.m316x964d52dc();
                        }
                    }, 1000L);
                }
            } catch (RemoteException e) {
                MslgLogger.LOGD(TAG, "remote sendKillAppProcess--error-- ", false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$restoreUbuntuProcess$0$com-xiaomi-mslgrdp-multwindow-MultiWindowManager, reason: not valid java name */
    public /* synthetic */ void m316x964d52dc() {
        sendPath(this.mFileUrl, this.mOpenApp);
    }

    public void closeWindow(int windowId) {
        synchronized (lock) {
            MslSurfaceInfo surfaceInfo = this.mslSurfaceInfos.get(Integer.valueOf(windowId));
            if (surfaceInfo != null && surfaceInfo.isModal) {
                this.modalWindowId = -1;
                this.modalTaskId = -1;
                MslgLogger.LOGD(TAG, "closeWindow isModal windowId = " + windowId, true);
            }
            WeakReference<ISurface> surfaceWR = this.surfaceHashMap.get(Integer.valueOf(windowId));
            if (surfaceWR != null && surfaceWR.get() != null) {
                ISurface surface = surfaceWR.get();
                closePopWindowInActivity(windowId, surface);
                surface.closeWindow(windowId);
                this.mslSurfaceInfos.remove(Integer.valueOf(windowId));
                getBitmapPool().removeBitmap(windowId);
                MslgLogger.LOGD(TAG, "closeWindow id = " + windowId, true);
                return;
            }
            if (surfaceInfo != null) {
                this.pendingCloseWindow.add(Integer.valueOf(windowId));
            }
        }
    }

    private void closePopWindowInActivity(int activityWindowId, ISurface surface) {
        ISurface surfacePop;
        if (surface instanceof BaseActivity) {
            Set<Integer> popWindows = this.activity2PopupWindows.get(Integer.valueOf(activityWindowId));
            if (popWindows != null && !popWindows.isEmpty()) {
                for (Integer p_window_id : popWindows) {
                    WeakReference<ISurface> popWindowWR = this.surfaceHashMap.get(p_window_id);
                    if (popWindowWR != null && popWindowWR.get() != null && (surfacePop = popWindowWR.get()) != null && (surfacePop instanceof LinuxWindowPopWindow)) {
                        surfacePop.closeWindow(p_window_id.intValue());
                        SessionState sessionState = getSessionManager().getCurrentSession();
                        if (sessionState != null) {
                            LibFreeRDP.sendWindowEvent(sessionState.getInstance(), p_window_id.intValue(), LibFreeRDP.WINDOW_CLOSE_EVENT);
                        }
                        MslgLogger.LOGD(TAG, "closePopWindowInActivity popWindowId = " + p_window_id, true);
                    }
                }
            }
            this.activity2PopupWindows.remove(Integer.valueOf(activityWindowId));
        }
    }

    public boolean inPendingCloseWindows(int windowId) {
        return this.pendingCloseWindow.contains(Integer.valueOf(windowId));
    }

    public View getControllerDecorView() {
        Activity activity = this.activityLifecycleCallbacks.getTopActivity();
        if (activity != null) {
            return activity.getWindow().getDecorView();
        }
        return null;
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void init() {
        ClipboardManagerProxy clipboardManager = ClipboardManagerProxy.getClipboardManager(GlobalApp.getApplication());
        this.mClipboardManager = clipboardManager;
        clipboardManager.addClipboardChangedListener(this);
        KeyboardMapperManager.getManager().init();
        for (Map.Entry<String, Module> module : modules.entrySet()) {
            module.getValue().init();
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void destroy() {
        for (Map.Entry<String, Module> entry : modules.entrySet()) {
            entry.getValue().destroy();
        }
        this.mslSurfaceInfos.clear();
        this.surfaceHashMap.clear();
        this.pendingCloseWindow.clear();
    }

    public void remoteClipboardChanged(String data) {
        boolean show_toast = this.mClipboardManager.setClipboardData(data);
        if (show_toast) {
            this.mainHandler.sendMessage(Message.obtain(null, Constances.MSG_DISPLAY_TOAST, GlobalApp.getApplication().getString(R.string.text_exceeds_max_limit)));
        }
    }

    @Override // com.xiaomi.mslgrdp.utils.ClipboardManagerProxy.OnClipboardChangedListener
    public void onClipboardChanged(String data) {
        MslgLogger.LOGD(TAG, "onClipboardChanged: " + data, false);
        if (getSessionManager().getCurrentSession() != null) {
            activityOnResume(true);
        }
    }

    public boolean atHome() {
        return this.activityLifecycleCallbacks.activityCount == 1;
    }

    /* loaded from: classes6.dex */
    class FreerdpUiEventListener extends FreerdpUIEventListenerAdapter {
        FreerdpUiEventListener() {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public void OnRemoteClipboardChanged(String data) {
            MslgLogger.LOGD(MultiWindowManager.TAG, "OnRemoteClipboardChanged: " + data, false);
            MultiWindowManager.this.remoteClipboardChanged(data);
        }
    }

    public IServer getServiceToken() {
        return this.iSession;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$new$1() {
        MslgLogger.LOGD(TAG, "linkToDeath binderDied Service destroyed---", true);
        System.exit(0);
    }

    public void bindConnectServer() {
        synchronized (lock) {
            getSessionManager().bindingService();
            Intent intentService = new Intent(GlobalApp.getApplication(), (Class<?>) MultiWindowService.class);
            GlobalApp.getApplication().bindService(intentService, new ServiceConnection() { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowManager.2
                @Override // android.content.ServiceConnection
                public void onServiceConnected(ComponentName name, IBinder service) {
                    MslgLogger.LOGD(MultiWindowManager.TAG, "onServiceConnected---", true);
                    MultiWindowManager.this.iSession = IServer.Stub.asInterface(service);
                    try {
                        MultiWindowManager.this.iSession.addClient(MultiWindowManager.this.appClient, MultiWindowManager.this.appType);
                    } catch (RemoteException e) {
                        MslgLogger.LOGE(MultiWindowManager.TAG, "addClient error---", true);
                    }
                    try {
                        service.linkToDeath(MultiWindowManager.this.deathRecipient, 0);
                    } catch (RemoteException e2) {
                        e2.printStackTrace();
                    }
                }

                @Override // android.content.ServiceConnection
                public void onServiceDisconnected(ComponentName name) {
                    MslgLogger.LOGD(MultiWindowManager.TAG, "onServiceDisconnected---", true);
                    try {
                        MultiWindowManager.this.iSession.removeClient(MultiWindowManager.this.appClient, MultiWindowManager.this.appType);
                    } catch (RemoteException e) {
                        MslgLogger.LOGE(MultiWindowManager.TAG, "removeClient error---", true);
                    }
                }
            }, 65);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void appFinishedExit() {
        MslgLogger.LOGD(TAG, "appFinishandExit", true);
        for (Map.Entry<Integer, WeakReference<ISurface>> entry : this.surfaceHashMap.entrySet()) {
            WeakReference<ISurface> value = entry.getValue();
            int windowid = entry.getKey().intValue();
            if (value != null && value.get() != null && (value.get() instanceof BaseActivity)) {
                BaseActivity activity = (BaseActivity) value.get();
                if (!activity.isFinishing()) {
                    closePopWindowInActivity(windowid, value.get());
                    activity.finishAndRemoveTask();
                }
            }
        }
        if (getActivityLifecycleCallbacks() != null && getActivityLifecycleCallbacks().mTopActivity != null) {
            WeakReference<Activity> activityWR = getActivityLifecycleCallbacks().mTopActivity;
            if (activityWR.get() != null && !activityWR.get().isFinishing()) {
                activityWR.get().finishAndRemoveTask();
            }
        }
        this.mIsAPPStarted = false;
        System.exit(0);
    }

    /* loaded from: classes6.dex */
    public class MultiWindowActivityLifecycleCallbacks extends ActivityLifecycleCallbacksAdapter {
        private WeakReference<Activity> mTopActivity = new WeakReference<>(null);
        public int activityCount = 0;
        private int activityForegroundCount = 0;

        public MultiWindowActivityLifecycleCallbacks() {
        }

        public Activity getTopActivity() {
            return this.mTopActivity.get();
        }

        public void setTopActivity(Activity activity) {
            if (activity != null && !activity.isFinishing()) {
                this.mTopActivity = new WeakReference<>(activity);
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            this.activityCount++;
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStarted(Activity activity) {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
            this.mTopActivity = new WeakReference<>(activity);
            MultiWindowManager.this.mIsAPPStarted = true;
            if (MultiWindowManager.this.appType == 1) {
                Utils.setProperty("sys.mslg.isalive", "true");
            }
            if (MultiWindowManager.this.appType == 3) {
                Utils.setProperty("sys.mslg.caj.isalive", "true");
            }
            if (MultiWindowManager.this.appType == 1) {
                this.activityForegroundCount++;
                if (MultiWindowManager.this.iSession != null && this.activityForegroundCount <= 1) {
                    try {
                        MultiWindowManager.this.iSession.backgroundOrNot(false, MultiWindowManager.this.appType);
                        MslgLogger.LOGD(MultiWindowManager.TAG, "backgroundOrNot false", true);
                    } catch (RemoteException e) {
                        MslgLogger.LOGE(MultiWindowManager.TAG, "backgroundOrNot false error", true);
                    }
                }
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
            if (MultiWindowManager.this.appType == 1) {
                int i = this.activityForegroundCount - 1;
                this.activityForegroundCount = i;
                if (i <= 0 && MultiWindowManager.this.iSession != null) {
                    try {
                        MultiWindowManager.this.iSession.backgroundOrNot(true, MultiWindowManager.this.appType);
                        MslgLogger.LOGD(MultiWindowManager.TAG, "backgroundOrNot true", true);
                    } catch (RemoteException e) {
                        MslgLogger.LOGE(MultiWindowManager.TAG, "backgroundOrNot Stopped error ", true);
                    }
                }
            }
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
            int i = this.activityCount - 1;
            this.activityCount = i;
            if (i <= 0) {
                this.activityCount = 0;
                MultiWindowManager.getManager().setFromMslg(false);
                if (MultiWindowManager.this.appType == 1) {
                    Utils.setProperty("sys.mslg.isalive", "false");
                }
                if (MultiWindowManager.this.appType == 3) {
                    Utils.setProperty("sys.mslg.caj.isalive", "false");
                }
                MultiWindowManager.this.mIsAPPStarted = false;
            }
        }
    }
}