package com.freerdp.freerdpcore.services;

import android.graphics.Bitmap;
import android.util.Log;

/* loaded from: classes6.dex */
public class LibGraphicsUpdate {
    private static final String TAG = "LibGraphicsUpdate";
    private final long mNativeObject;
    private final int mWinId;

    private final native void native_destroy(long j);

    private final native long native_init(String str, int i);

    private final native boolean native_update_graphics(long j, Bitmap bitmap, int i, int i2, int i3, int i4, int i5, int i6, int i7);

    static {
        try {
            System.loadLibrary("graphics-update");
            Log.i(TAG, "Successfully loaded native library: graphics-update");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load library: " + e.toString());
            throw e;
        }
    }

    public LibGraphicsUpdate(int winId, String file_name, int size) {
        this.mWinId = winId;
        this.mNativeObject = native_init(file_name, size);
    }

    public boolean updateGraphics(Bitmap bitmap, int b_width, int b_height, int stride, int left, int top, int dirty_w, int dirty_h) {
        return native_update_graphics(this.mNativeObject, bitmap, b_width, b_height, stride, left, top, dirty_w, dirty_h);
    }

    public void destroy() {
        native_destroy(this.mNativeObject);
    }
}