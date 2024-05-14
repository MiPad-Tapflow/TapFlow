package com.freerdp.freerdpcore.services;

import android.app.Application;
import android.util.Log;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.utils.Constances;

/* loaded from: classes6.dex */
public final class LinuxInputMethod {
    public static final String ACTION_EVENT_KEYBOARD = "com.mirdp.inpuetevent.keyboard";
    private static final int NEED_WAIT_TIME = 85;
    private static final String TAG = "LinuxInputMethod";
    private static volatile LinuxInputMethod linuxInputMethod;
    public static boolean mInputActivate = false;
    private long mLastSentTime;
    private long mPtr = nativeInit();

    /* loaded from: classes6.dex */
    public interface IMActivateListener {
        void imActivate(int i);

        void imCursorRect(int i, int i2, int i3, int i4, int i5);

        void imDeactivate(int i);
    }

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

    public static void inputMethodDeactivate(final int appType) {
        Log.i(TAG, "inputMethodDeactivate" + appType);
        mInputActivate = false;
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            if (MultiWindowManager.getManager().getImActivateListener() != null) {
                MultiWindowManager.getManager().getImActivateListener().imDeactivate(appType);
                return;
            }
            return;
        }
        MultiWindowManager.getManager().getMainHandler().post(new Runnable() { // from class: com.freerdp.freerdpcore.services.LinuxInputMethod.1
            @Override // java.lang.Runnable
            public void run() {
                if (MultiWindowManager.getManager().getImActivateListener() != null) {
                    MultiWindowManager.getManager().getImActivateListener().imDeactivate(appType);
                }
            }
        });
    }

    public static void inputMethodActivate(final int appType) {
        Log.i(TAG, "inputMethodActivate" + appType);
        mInputActivate = true;
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            if (MultiWindowManager.getManager().getImActivateListener() != null) {
                MultiWindowManager.getManager().getImActivateListener().imActivate(appType);
                return;
            }
            return;
        }
        MultiWindowManager.getManager().getMainHandler().post(new Runnable() { // from class: com.freerdp.freerdpcore.services.LinuxInputMethod.2
            @Override // java.lang.Runnable
            public void run() {
                if (MultiWindowManager.getManager().getImActivateListener() != null) {
                    MultiWindowManager.getManager().getImActivateListener().imActivate(appType);
                }
            }
        });
    }

    public static void inputMethodCursorRect(final int appType, final int left, final int top, final int width, final int height) {
        Log.i(TAG, "inputMethodCursorRect  appType " + appType + " left : " + left + " top : " + top + " width : " + width + " height : " + height);
        MultiWindowManager.getManager().getMainHandler().post(new Runnable() { // from class: com.freerdp.freerdpcore.services.LinuxInputMethod.3
            @Override // java.lang.Runnable
            public void run() {
                if (MultiWindowManager.getManager().getImActivateListener() != null) {
                    MultiWindowManager.getManager().getImActivateListener().imCursorRect(appType, left, top, width, height);
                }
            }
        });
    }

    public static LinuxInputMethod getInstance() {
        if (linuxInputMethod == null) {
            synchronized (LinuxInputMethod.class) {
                if (linuxInputMethod == null) {
                    linuxInputMethod = new LinuxInputMethod();
                }
            }
        }
        return linuxInputMethod;
    }

    private LinuxInputMethod() {
        this.mLastSentTime = 0L;
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