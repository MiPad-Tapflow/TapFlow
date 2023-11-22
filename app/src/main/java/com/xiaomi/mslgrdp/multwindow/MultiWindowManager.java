package com.xiaomi.mslgrdp.multwindow;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.PointerIcon;
import android.view.View;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.xiaomi.mslgrdp.application.GlobalApp;
import com.xiaomi.mslgrdp.application.SessionState;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.base.ISurface;
import com.xiaomi.mslgrdp.multwindow.base.Module;
import com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter;
import com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter;
import com.xiaomi.mslgrdp.utils.Constances;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.ljlVink.Tapflow.R;


/* loaded from: classes5.dex */
public class MultiWindowManager implements Module {
    public static final String TAG = "MultiWindowManager";
    public static volatile AtomicBoolean isDragging;
    private static HashMap<String, Module> modules;
    private Bitmap bitmap_pointer;
    private PointerIcon pointerIcon;
    private SessionState session;
    private static final MultiWindowManager manager = new MultiWindowManager();
    public static final Object lock = new Object();
    private MultiWindowActivityLifecycleCallbacks activityLifecycleCallbacks = new MultiWindowActivityLifecycleCallbacks();
    private int mainWindowId = -1;
    private Handler handler = new Handler(Looper.getMainLooper()) { // from class: com.xiaomi.mslgrdp.multwindow.MultiWindowManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constances.MSG_ADD_SURFACE_INFO /* 10000 */:
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
                default:
                    return;
                case Constances.MSG_LONG_PRESS /* 10005 */:
                    Log.v("MslDragLayout", "long press true");
                    MultiWindowManager.isDragging.set(true);
                    return;
                case Constances.MSG_UOPDATE_MOUSE_ICON /* 10006 */:
                    Log.v("MslDragLayout", "MSG_UOPDATE_MOUSE_ICON");
                    Activity top = MultiWindowManager.this.activityLifecycleCallbacks.getTopActivity();
                    top.getWindow().getDecorView().setPointerIcon(MultiWindowManager.this.pointerIcon);
                    return;
            }
        }
    };
    private volatile Map<Integer, MslSurfaceInfo> mslSurfaceInfos = Collections.synchronizedMap(new HashMap());
    private volatile Map<Integer, WeakReference<ISurface>> surfaceHashMap = Collections.synchronizedMap(new HashMap());
    private volatile Set<Integer> pendingCloseWindow = Collections.synchronizedSet(new HashSet());
    private int mCurrentWindowId = -1;

    static {
        HashMap<String, Module> hashMap = new HashMap<>();
        modules = hashMap;
        hashMap.put(BitmapsPool.class.getSimpleName(), new BitmapsPool());
        modules.put(SessionManager.class.getSimpleName(), new SessionManager());
        isDragging = new AtomicBoolean(false);
    }

    public static BitmapsPool getBitmapPool() {
        return (BitmapsPool) modules.get(BitmapsPool.class.getSimpleName());
    }

    public static SessionManager getSessionManager() {
        return (SessionManager) modules.get(SessionManager.class.getSimpleName());
    }

    public static MultiWindowManager getManager() {
        return manager;
    }

    public MultiWindowActivityLifecycleCallbacks getActivityLifecycleCallbacks() {
        return this.activityLifecycleCallbacks;
    }

    public void runOnUiThread(Runnable runnable) {
        this.handler.post(runnable);
    }

    public void setConnectListener(int sessionId) {
        SessionState sessionState = getSessionManager().getSession(sessionId);
        if (sessionState != null) {
            sessionState.setUIEventListener(new FreerdpUiEventListener());
        }
    }

    public void setCurrentWindowId(int id) {
        this.mCurrentWindowId = id;
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
        Log.v(TAG, " surfaceInfo = " + surfaceInfo);
        Message msg = this.handler.obtainMessage(Constances.MSG_ADD_SURFACE_INFO);
        msg.obj = surfaceInfo;
        this.handler.sendMessage(msg);
    }

    public void removeSurface(int id) {
        Message msg = this.handler.obtainMessage(Constances.MSG_REMOVE_SURFACE_INFO);
        msg.obj = Integer.valueOf(id);
        this.handler.sendMessage(msg);
    }

    public Handler getMainHandler() {
        return this.handler;
    }

    public MslSurfaceInfo getSurfaceInfo(int windowId) {
        return this.mslSurfaceInfos.get(Integer.valueOf(windowId));
    }

    public synchronized void process(long inst, int windowId, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha) {
        Log.e(TAG, "Thread Name = " + Thread.currentThread().getName());
        MslSurfaceInfo surfaceInfo = new MslSurfaceInfo(inst, windowId, x, y, width, height, isPopWindow, isAlpha);
        if (!isDragging.get()) {
            Bitmap bitmap = getBitmapPool().obtainBitmap(windowId, width, height);
            boolean result = LibFreeRDP.updateGraphics(inst, bitmap, surfaceInfo.id);
            if (!result) {
                Log.v(TAG, "bitmap fill failed");
                return;
            }
        }
        addSurfaceRecord(windowId, surfaceInfo);
    }

    public void OnUpdateMousePointer(long inst, int width, int height, int hotSpotX, int hotSpotY) {
        SessionState s = GlobalApp.getSession(inst);
        this.bitmap_pointer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        LibFreeRDP.updatePointerIcon(s.getInstance(), this.bitmap_pointer);
        this.bitmap_pointer = move_mouse_pixel(this.bitmap_pointer, hotSpotX, hotSpotY);
        Activity top = this.activityLifecycleCallbacks.getTopActivity();
        View decorView = getManager().getControllerDecorView();
        if (decorView == null) {
            return;
        }
        if (width < 64 && height < 64) {
            Resources resources = top.getResources();
            if (width != 9 || height != 15) {
                if (width == 15 && height == 9) {
                    Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrows_h);
                    this.pointerIcon = PointerIcon.create(bitmap, 72.0f, 72.0f);
                } else {
                    Bitmap bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.arrow);
                    this.pointerIcon = PointerIcon.create(move_mouse_pixel(bitmap2, 24, 16), 36.0f, 36.0f);
                }
            } else {
                Bitmap bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.arrows_v);
                this.pointerIcon = PointerIcon.create(bitmap3, 72.0f, 72.0f);
            }
            Handler handler = this.handler;
            if (handler != null) {
                handler.sendEmptyMessage(Constances.MSG_UOPDATE_MOUSE_ICON);
                return;
            }
            return;
        }
        if (hotSpotX == 18 && hotSpotY == 18) {
            this.pointerIcon = PointerIcon.create(this.bitmap_pointer, 40.0f, 40.0f);
        } else {
            this.pointerIcon = PointerIcon.create(this.bitmap_pointer, 32.0f, 32.0f);
        }
        Handler handler2 = this.handler;
        if (handler2 != null) {
            handler2.sendEmptyMessage(Constances.MSG_UOPDATE_MOUSE_ICON);
        }
    }

    public Bitmap move_mouse_pixel(Bitmap bitmap, int hotSpotX, int hotSpotY) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if ((hotSpotX == 18 && hotSpotY == 18) || (hotSpotX == 24 && hotSpotY == 16)) {
            width = 80;
            height = 80;
        }
        if (hotSpotX == 0 && hotSpotY == 0) {
            width = 90;
            height = 90;
        }
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int color = bitmap.getPixel(x, y);
                if (Color.alpha(color) != 0) {
                    int newX = ((width / 2) - hotSpotX) + x;
                    int newY = ((height / 2) - hotSpotY) + y;
                    if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                        newBitmap.setPixel(newX, newY, color);
                    }
                }
            }
        }
        return newBitmap;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showWindow(WeakReference<ISurface> surfaceWR, MslSurfaceInfo surfaceInfo, boolean update) {
        if (!update) {
            Log.v(TAG, "process addWindow---");
            Activity top = this.activityLifecycleCallbacks.getTopActivity();
            if (surfaceInfo.isPopWindow || surfaceInfo.isAlpha) {
                Log.v(TAG, "show PopWindow---");
                if (this.activityLifecycleCallbacks.activityCount == 1) {
                    LinuxWindowPopWindow.show(surfaceInfo.id, top, surfaceInfo);
                    return;
                } else {
                    LinuxWindowPopWindow.show(surfaceInfo.id, top, surfaceInfo);
                    return;
                }
            }
            Log.v(TAG, "start Activity---");
            if (top != null) {
                Intent intent = new Intent(top, LinuxWindowActivity.class);
                intent.putExtra(Constances.BUNDLE_ID_WINDOW_ID, surfaceInfo.id);
                top.startActivity(intent);
                return;
            }
            return;
        }
        Log.v(TAG, "process updateWindow---");
        if (surfaceWR != null && surfaceWR.get() != null) {
            ISurface surface = surfaceWR.get();
            surface.updateWindow(surfaceInfo.id, surfaceInfo.x, surfaceInfo.y, surfaceInfo.width, surfaceInfo.height);
        }
    }

    public void closeWindow(int windowId) {
        if (windowId == this.mainWindowId) {
            return;
        }
        MslSurfaceInfo surfaceInfo = this.mslSurfaceInfos.get(Integer.valueOf(windowId));
        WeakReference<ISurface> surfaceWR = this.surfaceHashMap.get(Integer.valueOf(windowId));
        if (surfaceWR != null && surfaceWR.get() != null) {
            ISurface surface = surfaceWR.get();
            surface.closeWindow(windowId);
            this.mslSurfaceInfos.remove(Integer.valueOf(windowId));
            getBitmapPool().removeBitmap(windowId);
        } else if (surfaceInfo != null) {
            this.pendingCloseWindow.add(Integer.valueOf(windowId));
        }
    }

    public boolean inPendingCloseWindows(int windowId) {
        return this.pendingCloseWindow.contains(Integer.valueOf(windowId));
    }

    public synchronized void setMainWindowId(int windowId, MslSurfaceInfo surfaceInfo) {
        this.mainWindowId = windowId;
        this.mslSurfaceInfos.put(Integer.valueOf(windowId), surfaceInfo);
    }

    public synchronized boolean mainWindowShowing() {
        return this.mainWindowId == 2;
    }

    public View getControllerDecorView() {
        WeakReference<ISurface> surfaceWR = this.surfaceHashMap.get(Integer.valueOf(this.mainWindowId));
        if (surfaceWR != null && surfaceWR.get() != null && (surfaceWR.get() instanceof Activity)) {
            Activity activity = (Activity) surfaceWR.get();
            return activity.getWindow().getDecorView();
        }
        return null;
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void init() {
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

    /* loaded from: classes5.dex */
    class FreerdpUiEventListener extends FreerdpUIEventListenerAdapter {
        FreerdpUiEventListener() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes5.dex */
    public class MultiWindowActivityLifecycleCallbacks extends ActivityLifecycleCallbacksAdapter {
        public int activityCount = 0;
        private WeakReference<Activity> mTopActivity;

        MultiWindowActivityLifecycleCallbacks() {
        }

        public Activity getTopActivity() {
            return this.mTopActivity.get();
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            this.mTopActivity = new WeakReference<>(activity);
            this.activityCount++;
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityResumed(Activity activity) {
            this.mTopActivity = new WeakReference<>(activity);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityPaused(Activity activity) {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityStopped(Activity activity) {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.ActivityLifecycleCallbacksAdapter, android.app.Application.ActivityLifecycleCallbacks
        public void onActivityDestroyed(Activity activity) {
            this.activityCount--;
        }
    }
}