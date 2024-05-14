package com.xiaomi.mslgrdp.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.xiaomi.mslgrdp.multwindow.SurfaceHandlerThread;
import com.xiaomi.mslgrdp.utils.MslgLogger;

public class MslSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "MslSurfaceView";
    private SurfaceHandlerThread handlerThread;
    private SurfaceHolder holder;
    private boolean isSurfaceCreated;
    private Bitmap mBitmap;
    private int windowId;

    public MslSurfaceView(Context context) {
        this(context, null);
    }

    public MslSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MslSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.windowId = -1;
        this.isSurfaceCreated = false;
        SurfaceHolder holder = getHolder();
        this.holder = holder;
        holder.addCallback(this);
        getHolder().setFormat(-2);
    }

    public void setWindowId(int windowId) {
        this.windowId = windowId;
        SurfaceHandlerThread surfaceHandlerThread = new SurfaceHandlerThread("Thread-sf-" + windowId);
        this.handlerThread = surfaceHandlerThread;
        surfaceHandlerThread.start();
    }

    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        if (this.isSurfaceCreated) {
            drawBuffer(this.holder);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        MslgLogger.LOGD(TAG, "surfaceCreated---", false);
        this.holder = holder;
        this.isSurfaceCreated = true;
        drawBuffer(holder);
    }

    private void drawBuffer(final SurfaceHolder holder) {
        SurfaceHandlerThread surfaceHandlerThread = this.handlerThread;
        if (surfaceHandlerThread != null) {
            surfaceHandlerThread.post(new Runnable() {
                @Override
                public void run() {
                    MslSurfaceView.this.drawImage(holder);
                }
            });
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        SurfaceHandlerThread surfaceHandlerThread = this.handlerThread;
        if (surfaceHandlerThread != null && surfaceHandlerThread.isAlive()) {
            this.handlerThread.quitSafely();
            try {
                this.handlerThread.join();
            } catch (InterruptedException e) {
                MslgLogger.LOGD(TAG, "onDetachedFromWindow---join error", false);
            }
        }
        super.onDetachedFromWindow();
    }

    public void drawImage(SurfaceHolder holder) {
        Canvas canvas;
        if (this.mBitmap != null && holder != null && (canvas = holder.lockHardwareCanvas()) != null) {
            canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, (Paint) null);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        MslgLogger.LOGD(TAG, "surfaceChanged---", false);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        MslgLogger.LOGD(TAG, "surfaceDestroyed---", false);
        this.isSurfaceCreated = false;
    }
}