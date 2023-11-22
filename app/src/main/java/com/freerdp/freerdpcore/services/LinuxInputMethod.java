package com.freerdp.freerdpcore.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/* loaded from: classes5.dex */
public final class LinuxInputMethod {
    public static final String ACTION_EVENT_KEYBOARD = "com.mirdp.inpuetevent.keyboard";
    private static final int NEED_WAIT_TIME = 85;
    private static final String TAG = "LinuxInputMethod";
    private static volatile LinuxInputMethod linuxInputMethod;
    private static Context mContext;
    public static boolean mInputActivate = false;
    private long mLastSentTime;
    private long mPtr = nativeInit();

    private static native void nativeDestroy(long j);

    private static native long nativeInit();

    private static native boolean send_string_to_linux(long j, String str);

    static {
        try {
            System.loadLibrary("linux-ime");
            Log.i(TAG, "Successfully loaded native library: linux-ime");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load library: " + e.toString());
            throw e;
        }
    }

    public boolean sendStringToLinux(String data) {
        Log.v(TAG,"send string:"+data);
        boolean send_string_to_linux;
        if (this.mPtr != 0) {
            long now = System.currentTimeMillis();
            synchronized (this) {
                long gap = now - this.mLastSentTime;
                if (gap < 85) {
                    long wait_time = 85 - gap;
                    try {
                        wait(wait_time);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "sendStringToLinux exception: " + e.toString());
                    }
                }
                long wait_time2 = System.currentTimeMillis();
                this.mLastSentTime = wait_time2;
                send_string_to_linux = send_string_to_linux(this.mPtr, data);
            }
            return send_string_to_linux;
        }
        Log.e(TAG, "Not allowed to send string before init a native socket.");
        return false;
    }

    public static void inputMethodDeactivate() {
        Log.i(TAG, "inputMethodDeactivate");
        mInputActivate = false;
        Intent intent = new Intent(ACTION_EVENT_KEYBOARD);
        intent.putExtra("InputActivate", false);
        mContext.sendBroadcast(intent, "com.ljlvink.mslgrdp.brocast_mirdp");
    }

    public static void inputMethodActivate() {
        Log.i(TAG, "inputMethodActivate");
        mInputActivate = true;
        Intent intent = new Intent(ACTION_EVENT_KEYBOARD);
        intent.putExtra("InputActivate", true);
        mContext.sendBroadcast(intent, "com.ljlvink.mslgrdp.brocast_mirdp");
    }

    public static LinuxInputMethod getInstance(Context context) {
        if (linuxInputMethod == null) {
            synchronized (LinuxInputMethod.class) {
                if (linuxInputMethod == null) {
                    linuxInputMethod = new LinuxInputMethod(context);
                }
            }
        }
        return linuxInputMethod;
    }

    private LinuxInputMethod(Context context) {
        this.mLastSentTime = 0L;
        mContext = context;
        this.mLastSentTime = 0L;
    }

    public void dispose() {
        long j = this.mPtr;
        if (j != 0) {
            nativeDestroy(j);
            this.mPtr = 0L;
        }
    }
}