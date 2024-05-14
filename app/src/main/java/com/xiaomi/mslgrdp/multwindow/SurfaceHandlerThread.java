package com.xiaomi.mslgrdp.multwindow;

import android.os.Handler;
import android.os.HandlerThread;

public class SurfaceHandlerThread extends HandlerThread {
    private Handler mHandler;

    public SurfaceHandlerThread(String name) {
        super(name);
    }

    @Override // android.os.HandlerThread
    protected void onLooperPrepared() {
        this.mHandler = new Handler(getLooper());
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void post(Runnable runnable) {
        Handler handler = this.mHandler;
        if (handler != null) {
            handler.post(runnable);
        }
    }
}