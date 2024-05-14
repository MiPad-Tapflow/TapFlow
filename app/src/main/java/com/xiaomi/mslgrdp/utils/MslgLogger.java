package com.xiaomi.mslgrdp.utils;

import android.util.Log;

/* loaded from: classes6.dex */
public class MslgLogger {
    public static final String TAG_ACTIVITY = "TAG_ACTIVITY";
    public static final String TAG_BITMAP = "TAG_BITMAP";
    public static final String TAG_EVENT = "TAG_EVENT";
    public static final String TAG_MODAL = "TAG_MODAL";
    public static final String TAG_WINDOW = "TAG_WINDOW";
    private static final boolean isDebug = true;

    public static void LOGE(String tag, String content, boolean force) {
        Log.e(tag, content);
    }

    public static void LOGD(String tag, String content, boolean force) {
        Log.d(tag, content);
    }
}