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
import android.widget.ImageView;

import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.base.BasePopWindow;

import cn.ljlVink.Tapflow.R;


/* loaded from: classes5.dex */
public class LinuxWindowPopWindow extends BasePopWindow {
    private PointerIcon pointerIcon;

    public LinuxWindowPopWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public LinuxWindowPopWindow(Context context) {
        super(context);
    }

    public PointerIcon getPointerIcon() {
        return this.pointerIcon;
    }

    void init(Activity context) {
        Resources resources = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrow);
        this.pointerIcon = PointerIcon.create(MultiWindowManager.getManager().move_mouse_pixel(bitmap, 24, 16), 40.0f, 40.0f);
    }

    public static LinuxWindowPopWindow show(int windowId, Activity context, final MslSurfaceInfo mslSurfaceInfo) {
        final ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(R.layout.pop_layout, (ViewGroup) null);
        final LinuxWindowPopWindow popWindow = new LinuxWindowPopWindow(context);
        popWindow.surfaceInfo = mslSurfaceInfo;
        popWindow.mBitmap = MultiWindowManager.getBitmapPool().obtainBitmap(windowId, mslSurfaceInfo.width, mslSurfaceInfo.height);
        popWindow.setContentView(imageView);
        popWindow.init(context);
        imageView.setImageBitmap(popWindow.mBitmap);
        popWindow.getContentView().setOnGenericMotionListener(new View.OnGenericMotionListener() { // from class: com.xiaomi.mslgrdp.multwindow.LinuxWindowPopWindow.1
            @Override // android.view.View.OnGenericMotionListener
            public boolean onGenericMotion(View v, MotionEvent event) {
                View decorView = MultiWindowManager.getManager().getControllerDecorView();
                if (decorView != null && event.getX() >= 0.0f && event.getY() >= 0.0f) {
                    event.offsetLocation(mslSurfaceInfo.x, mslSurfaceInfo.y);
                }
                switch (event.getAction()) {
                    case 7:
                        MotionEvent myCopy = MotionEvent.obtain(event);
                        if (popWindow.getPointerIcon() != null) {
                            imageView.setPointerIcon(popWindow.getPointerIcon());
                        }
                        return decorView.dispatchGenericMotionEvent(myCopy);
                    default:
                        return false;
                }
            }
        });
        ViewGroup decor = (ViewGroup) context.getWindow().getDecorView();
        View parent = decor.findViewById(android.R.id.content);
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() { // from class: com.xiaomi.mslgrdp.multwindow.LinuxWindowPopWindow.2
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View v, MotionEvent event) {
                View decorView = MultiWindowManager.getManager().getControllerDecorView();
                if (decorView == null) {
                    return false;
                }
                event.offsetLocation(mslSurfaceInfo.x, mslSurfaceInfo.y);
                return decorView.dispatchTouchEvent(event);
            }
        });
        popWindow.setBackgroundDrawable(null);
        popWindow.setWidth(mslSurfaceInfo.width);
        popWindow.setHeight(mslSurfaceInfo.height);
        popWindow.showAtLocation(parent, 8388659, mslSurfaceInfo.x, mslSurfaceInfo.y);
        MultiWindowManager.getManager().addSurface(windowId, popWindow);
        return popWindow;
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BasePopWindow, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void updateWindow(int windowId, int x, int y, int width, int height) {
        if (this.surfaceInfo != null && (this.surfaceInfo.width != width || this.surfaceInfo.height != height)) {
            this.mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        MslSurfaceInfo surfaceInfo = MultiWindowManager.getManager().getSurfaceInfo(windowId);
        this.surfaceInfo = surfaceInfo;
        this.mBitmap = MultiWindowManager.getBitmapPool().obtainBitmap(windowId, width, height);
        refreshContent();
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BasePopWindow, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void refreshContent() {
        ImageView imageView = (ImageView) getContentView();
        imageView.setImageBitmap(this.mBitmap);
    }
}