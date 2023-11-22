package com.xiaomi.mslgrdp.multwindow.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;

/* loaded from: classes5.dex */
public class BasePopWindow extends PopupWindow implements ISurface {
    public Bitmap mBitmap;
    public MslSurfaceInfo surfaceInfo;

    public BasePopWindow() {
    }

    public BasePopWindow(Context context) {
        super(context);
    }

    public BasePopWindow(int width, int height) {
        super(width, height);
    }

    public BasePopWindow(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public BasePopWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void closeWindow(int windowId) {
        Log.v(MultiWindowManager.TAG, "popupWindow closeWindow windowId = " + windowId);
        dismiss();
        MultiWindowManager.getManager().removeSurface(windowId);
    }

    public void updateWindow(int windowId, int x, int y, int width, int height) {
    }

    public void refreshContent() {
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.ISurface
    public Rect getRegion() {
        return null;
    }
}