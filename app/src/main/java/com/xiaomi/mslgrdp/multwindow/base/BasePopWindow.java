package com.xiaomi.mslgrdp.multwindow.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.multwindow.SessionState;

public class BasePopWindow extends PopupWindow implements ISurface {
    private boolean closeWindowByNative;
    protected ImageView imageView;
    public Bitmap mBitmap;
    public MslSurfaceInfo surfaceInfo;

    public BasePopWindow() {
        this.closeWindowByNative = false;
    }

    public BasePopWindow(Context context) {
        super(context);
        this.closeWindowByNative = false;
    }

    public BasePopWindow(int width, int height) {
        super(width, height);
        this.closeWindowByNative = false;
    }

    public BasePopWindow(View contentView, int width, int height) {
        super(contentView, width, height);
        this.closeWindowByNative = false;
    }

    public BasePopWindow(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
        this.closeWindowByNative = false;
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void closeWindow(int windowId) {
        this.closeWindowByNative = true;
        Log.v(MultiWindowManager.TAG, "popupWindow closeWindow windowId = " + windowId);
        dismiss();
        MultiWindowManager.getManager().removeSurface(windowId);
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        SessionState sessionState;
        super.dismiss();
        MslSurfaceInfo mslSurfaceInfo = this.surfaceInfo;
        if (mslSurfaceInfo != null && mslSurfaceInfo.id > 0 && !this.closeWindowByNative && (sessionState = MultiWindowManager.getSessionManager().getCurrentSession()) != null) {
            LibFreeRDP.sendWindowEvent(sessionState.getInstance(), this.surfaceInfo.id, LibFreeRDP.WINDOW_CLOSE_EVENT);
        }
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