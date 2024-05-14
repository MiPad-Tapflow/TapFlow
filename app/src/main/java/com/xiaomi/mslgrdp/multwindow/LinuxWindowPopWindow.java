package com.xiaomi.mslgrdp.multwindow;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import cn.ljlVink.Tapflow.R;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.base.BasePopWindow;
import com.xiaomi.mslgrdp.presentation.LinuxVirtualActivity;
import com.xiaomi.mslgrdp.utils.MslgLogger;
import java.lang.ref.WeakReference;

public class LinuxWindowPopWindow extends BasePopWindow {
    private WeakReference<Activity> activityWR;
    private boolean isChange;
    private PointerIcon pointerIcon;

    public LinuxWindowPopWindow(View contentView, int width, int height) {
        super(contentView, width, height);
        this.isChange = false;
        this.activityWR = null;
    }

    public LinuxWindowPopWindow(Context context) {
        super(context);
        this.isChange = false;
        this.activityWR = null;
    }

    public PointerIcon getPointerIcon() {
        return this.pointerIcon;
    }

    void init(Activity context) {
        this.activityWR = new WeakReference<>(context);
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrow);
        this.pointerIcon = PointerIcon.create(MultiWindowManager.getMouseManager().move_mouse_pixel(bitmap, 24, 16), 40.0f, 40.0f);
    }

    public static LinuxWindowPopWindow show(final int windowId, Activity context, final MslSurfaceInfo mslSurfaceInfo) {
        FrameLayout container = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.pop_layout, (ViewGroup) null);
        final LinuxWindowPopWindow popWindow = new LinuxWindowPopWindow(context);
        popWindow.imageView = (ImageView) container.findViewById(R.id.iv_img);
        popWindow.surfaceInfo = mslSurfaceInfo;
        popWindow.mBitmap = MultiWindowManager.getBitmapPool().getBitmapWithWinId(windowId);
        popWindow.setContentView(container);
        popWindow.setInputMethodMode(1);
        popWindow.init(context);
        popWindow.imageView.setImageBitmap(popWindow.mBitmap);
        popWindow.getContentView().setOnGenericMotionListener(new View.OnGenericMotionListener() { // from class: com.xiaomi.mslgrdp.multwindow.LinuxWindowPopWindow.1
            @Override // android.view.View.OnGenericMotionListener
            public boolean onGenericMotion(View v, MotionEvent event) {
                View decorView = MultiWindowManager.getManager().getControllerDecorView();
                if (decorView != null && event.getX() >= 0.0f && event.getY() >= 0.0f && popWindow.surfaceInfo != null) {
                    event.offsetLocation(popWindow.surfaceInfo.x, popWindow.surfaceInfo.y);
                }
                switch (event.getAction()) {
                    case 7:
                        MotionEvent myCopy = MotionEvent.obtain(event);
                        if (popWindow.getPointerIcon() != null) {
                            popWindow.imageView.setPointerIcon(popWindow.getPointerIcon());
                        }
                        return decorView.dispatchGenericMotionEvent(myCopy);
                    case 8:
                        MotionEvent mCopy = MotionEvent.obtain(event);
                        return decorView.dispatchGenericMotionEvent(mCopy);
                    default:
                        return false;
                }
            }
        });
        ViewGroup decor = (ViewGroup) context.getWindow().getDecorView();
        final View parent = decor.findViewById(android.R.id.content);
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() { // from class: com.xiaomi.mslgrdp.multwindow.LinuxWindowPopWindow.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                View decorView = MultiWindowManager.getManager().getControllerDecorView();
                if (decorView == null) {
                    return false;
                }
                if (popWindow.surfaceInfo != null) {
                    event.offsetLocation(popWindow.surfaceInfo.x, popWindow.surfaceInfo.y);
                }
                return decorView.dispatchTouchEvent(event);
            }
        });
        popWindow.setBackgroundDrawable(null);
        popWindow.setWidth(mslSurfaceInfo.width);
        popWindow.setHeight(mslSurfaceInfo.height);
        popWindow.getContentView().getViewTreeObserver().addOnWindowFocusChangeListener(new ViewTreeObserver.OnWindowFocusChangeListener() { // from class: com.xiaomi.mslgrdp.multwindow.LinuxWindowPopWindow.3
            @Override // android.view.ViewTreeObserver.OnWindowFocusChangeListener
            public void onWindowFocusChanged(boolean hasFocus) {
                MslgLogger.LOGD(LinuxVirtualActivity.TAG, "Android side's View:" + windowId + " now become to " + hasFocus, false);
                if (windowId > 0 && hasFocus) {
                    LibFreeRDP.sendWindowFocusEvent(MultiWindowManager.getSessionManager().getCurrentSession().getInstance(), windowId, hasFocus);
                }
            }
        });
        if (parent.getWindowToken() != null) {
            MslgLogger.LOGD(LinuxVirtualActivity.TAG, "popwindow show windowId = " + windowId, false);
            popWindow.showAtLocation(parent, 8388659, mslSurfaceInfo.x, mslSurfaceInfo.y);
            MultiWindowManager.getManager().addSurface(windowId, popWindow);
        } else {
            MslgLogger.LOGD(LinuxVirtualActivity.TAG, "arent.getWindowToken() == null delay 1000" + windowId, false);
            MultiWindowManager.getManager().getMainHandler().postDelayed(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.LinuxWindowPopWindow.4
                @Override // java.lang.Runnable
                public void run() {
                    View view = parent;
                    if (view != null && view.getWindowToken() != null) {
                        popWindow.showAtLocation(parent, 8388659, mslSurfaceInfo.x, mslSurfaceInfo.y);
                        MultiWindowManager.getManager().addSurface(windowId, popWindow);
                    }
                }
            }, 500L);
        }
        return popWindow;
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BasePopWindow, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void updateWindow(int windowId, int x, int y, int width, int height) {
        if (this.surfaceInfo != null && (this.surfaceInfo.width != width || this.surfaceInfo.height != height)) {
            this.mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        MslSurfaceInfo surfaceInfo = MultiWindowManager.getManager().getSurfaceInfo(windowId);
        this.isChange = this.surfaceInfo.isDiff(surfaceInfo);
        this.surfaceInfo = surfaceInfo;
        this.mBitmap = MultiWindowManager.getBitmapPool().getBitmapWithWinId(windowId);
        refreshContent();
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BasePopWindow, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void refreshContent() {
        Activity activity;
        WeakReference<Activity> weakReference = this.activityWR;
        if (weakReference != null && weakReference.get() != null && (activity = this.activityWR.get()) != null && !activity.isFinishing()) {
            this.imageView.setImageBitmap(this.mBitmap);
            if (this.isChange && isShowing()) {
                update(this.surfaceInfo.x, this.surfaceInfo.y, this.surfaceInfo.width, this.surfaceInfo.height);
            }
        }
    }
}