package com.xiaomi.mslgrdp.utils;

import android.content.Context;

/* loaded from: classes5.dex */
public class Mouse {
    private static final int PTRFLAGS_DOWN = 32768;
    private static final int PTRFLAGS_HWHEEL = 1024;
    private static final int PTRFLAGS_LBUTTON = 4096;
    private static final int PTRFLAGS_MOVE = 2048;
    private static final int PTRFLAGS_RBUTTON = 8192;
    private static final int PTRFLAGS_TOUCH_DOWN = 1;
    private static final int PTRFLAGS_TOUCH_MOVE = 4;
    private static final int PTRFLAGS_TOUCH_UP = 2;
    private static final int PTRFLAGS_WHEEL = 512;
    private static final int PTRFLAGS_WHEEL_NEGATIVE = 256;

    public static int getLeftButtonEvent(Context context, boolean down) {
        return (down ? 32768 : 0) | 4096;
    }

    public static int getRightButtonEvent(Context context, boolean down) {
        return (down ? 32768 : 0) | 8192;
    }

    public static int getMoveEvent() {
        return 2048;
    }

    public static int getTouchMoveEvent() {
        return 4;
    }

    public static int getTouchUpEvent() {
        return 2;
    }

    public static int getTouchDownEvent() {
        return 1;
    }

    public static int getScrollEvent(Context context, boolean down, float y) {
        int flag_y = ((int) (2.0f * y)) & 255;
        if (down) {
            int flags = 512 | (255 - flag_y) | 256;
            return flags;
        }
        int flags2 = 512 | flag_y;
        return flags2;
    }

    public static int getScrollEventH(Context context, boolean down, float x) {
        int flag_x = ((int) (2.0f * x)) & 255;
        if (down) {
            int flags = 1024 | (255 - flag_x) | 256;
            return flags;
        }
        int flags2 = 1024 | flag_x;
        return flags2;
    }

    public static int getScrollEventMouse(Context context, boolean down) {
        if (down) {
            int flags = 512 | 392;
            return flags;
        }
        int flags2 = 512 | 120;
        return flags2;
    }
}
