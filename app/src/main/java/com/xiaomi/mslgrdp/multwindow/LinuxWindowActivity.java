package com.xiaomi.mslgrdp.multwindow;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.base.BaseActivity;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.views.MslDragLayout;

import cn.ljlVink.Tapflow.R;


/* loaded from: classes5.dex */
public class LinuxWindowActivity extends BaseActivity {
    private EditText editText;
    int left;
    MslSurfaceInfo surfaceInfo;
    int top;
    int windowId = -1;
    boolean isFullScreen = false;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linux_window_layout);
        this.windowId = getIntent().getIntExtra(Constances.BUNDLE_ID_WINDOW_ID, -1);
        Log.v(MultiWindowManager.TAG, "LinuxWindowActivity onCreate windowid = " + this.windowId);
        if (this.windowId == -1) {
            finish();
        }
        if (this.windowId != -1) {
            if (MultiWindowManager.getManager().inPendingCloseWindows(this.windowId)) {
                MultiWindowManager.getManager().removeSurface(this.windowId);
                finish();
                return;
            }
            this.surfaceInfo = MultiWindowManager.getManager().getSurfaceInfo(this.windowId);
            Log.v(MultiWindowManager.TAG, "LinuxWindowActivity onCreate surfaceInfo = " + this.surfaceInfo);
            if (this.surfaceInfo == null) {
                finish();
                return;
            }
            MultiWindowManager.getManager().addSurface(this.windowId, this);
            this.iv_content = (ImageView) findViewById(R.id.iv_content);
            this.container_layout = (MslDragLayout) findViewById(R.id.container_layout);
            EditText editText = (EditText) findViewById(R.id.editTextInput);
            this.editText = editText;
            editText.addTextChangedListener(KeyboardMapperManager.getManager().getEditWatcher());
            if (this.editText.getVisibility() == View.VISIBLE && LinuxInputMethod.mInputActivate) {
                this.editText.setFocusable(true);
                this.editText.requestFocus();
            }
            this.mBitmap = MultiWindowManager.getBitmapPool().obtainBitmap(this.windowId, this.surfaceInfo.width, this.surfaceInfo.height);
            this.left = this.surfaceInfo.x;
            this.top = this.surfaceInfo.y;
            refreshContent();
            this.isFullScreen = Math.abs(this.surfaceInfo.width - Constances.SCREEN_WIDTH) < 10;
            this.container_layout.setFullScreen(this.isFullScreen);
            this.container_layout.setUpdateLocationListener(new MslDragLayout.UpdateListener() { // from class: com.xiaomi.mslgrdp.multwindow.LinuxWindowActivity.1
                @Override // com.xiaomi.mslgrdp.views.MslDragLayout.UpdateListener
                public void onUpdate() {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) LinuxWindowActivity.this.iv_content.getLayoutParams();
                    LinuxWindowActivity.this.left = params.leftMargin;
                    LinuxWindowActivity.this.top = params.topMargin;
                }
            });
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void refreshContent() {
        Log.v(MultiWindowManager.TAG, "refreshContent windowid = " + this.windowId + " surfaceInfo " + this.surfaceInfo);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.iv_content.getLayoutParams();
        int x = this.surfaceInfo.x;
        int y = this.surfaceInfo.y;
        int width = this.surfaceInfo.width;
        int height = this.surfaceInfo.height;
        params.leftMargin = this.left;
        params.topMargin = this.top;
        params.width = width;
        params.height = height;
        this.iv_content.setLayoutParams(params);
        this.iv_content.setImageBitmap(this.mBitmap);
        this.iv_content.invalidate();
        region.set(x, y, x + width, y + height);
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void updateWindow(int windowId, int x, int y, int width, int height) {
        MslSurfaceInfo mslSurfaceInfo = this.surfaceInfo;
        if (mslSurfaceInfo != null) {
            if (mslSurfaceInfo.width == width && this.surfaceInfo.height == height) {
                Log.v(MultiWindowManager.TAG, "windowid = " + windowId + "复用bitmap");
            } else {
                Log.v(MultiWindowManager.TAG, "windowid updateWindow 不复用bitmap");
                this.mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }
        }
        Log.v(MultiWindowManager.TAG, "windowid = " + windowId + " x = " + x + " y = " + y + " width = " + width + " height = " + height);
        MslSurfaceInfo surfaceInfo = MultiWindowManager.getManager().getSurfaceInfo(windowId);
        this.surfaceInfo = surfaceInfo;
        if (!MultiWindowManager.isDragging.get()) {
            refreshContent();
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public Rect getRegion() {
        return region;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        if (MultiWindowManager.isDragging.get()) {
            Log.v("MslDragLayout", " is dragging ");
            return true;
        }
        View decorView = MultiWindowManager.getManager().getControllerDecorView();
        if (decorView != null && ev.getX() >= 0.0f && ev.getY() >= 0.0f) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.iv_content.getLayoutParams();
            this.left = params.leftMargin;
            this.top = params.topMargin;
            if (inBitmapArea(ev)) {
                ev.offsetLocation(this.surfaceInfo.x - this.left, this.surfaceInfo.y - this.top);
            } else if (inWpsBitmapArea(ev)) {
                return true;
            }
            Log.v("MslDragLayout", "ev x = " + ev.getX() + " y = " + ev.getY());
            decorView.dispatchTouchEvent(ev);
        }
        return result;
    }

    private boolean inBitmapArea(MotionEvent event) {
        if (this.surfaceInfo != null && event.getX() >= this.left && event.getX() <= this.left + this.surfaceInfo.width && event.getY() >= this.top && event.getY() <= this.top + this.surfaceInfo.height) {
            Log.v("MslDragLayout", "inBitmapArea -- ");
            return true;
        }
        return false;
    }

    private boolean inWpsBitmapArea(MotionEvent event) {
        if (this.surfaceInfo != null && event.getX() >= this.surfaceInfo.x && event.getX() <= this.surfaceInfo.x + this.surfaceInfo.width && event.getY() >= this.surfaceInfo.y && event.getY() <= this.surfaceInfo.y + this.surfaceInfo.height) {
            Log.v("MslDragLayout", "inWpsBitmapArea -- ");
            return true;
        }
        return false;
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.core.app.ComponentActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        View decorView = MultiWindowManager.getManager().getControllerDecorView();
        return decorView == null ? super.dispatchKeyEvent(event) : decorView.dispatchKeyEvent(event);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        View decorView = MultiWindowManager.getManager().getControllerDecorView();
        if (decorView != null && ev.getX() >= 0.0f && ev.getY() >= 0.0f) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.iv_content.getLayoutParams();
            this.left = params.leftMargin;
            this.top = params.topMargin;
            if (inBitmapArea(ev)) {
                ev.offsetLocation(this.surfaceInfo.x - this.left, this.surfaceInfo.y - this.top);
            }
        }
        return decorView == null ? super.dispatchGenericMotionEvent(ev) : decorView.dispatchGenericMotionEvent(ev);
    }
}