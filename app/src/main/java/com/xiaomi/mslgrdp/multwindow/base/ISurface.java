package com.xiaomi.mslgrdp.multwindow.base;

import android.graphics.Rect;

/* loaded from: classes5.dex */
public interface ISurface {
    public static final String TAG = "ISurface";
    public static final Rect region = new Rect();

    void closeWindow(int i);

    Rect getRegion();

    void refreshContent();

    void updateWindow(int i, int i2, int i3, int i4, int i5);
}