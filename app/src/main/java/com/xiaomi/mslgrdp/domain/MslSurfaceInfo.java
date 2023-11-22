package com.xiaomi.mslgrdp.domain;

/* loaded from: classes5.dex */
public class MslSurfaceInfo {
    public int height;
    public int id;
    public boolean isAlpha;
    public boolean isPopWindow;
    public long sessionId;
    public int width;
    public int x;
    public int y;

    public MslSurfaceInfo() {
    }

    public MslSurfaceInfo(long sessionId, int id, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha) {
        this.sessionId = sessionId;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isPopWindow = isPopWindow;
        this.isAlpha = isAlpha;
    }

    public String toString() {
        return "MslSurfaceInfo{id=" + this.id + ", x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + ", isPopWindow=" + this.isPopWindow + ", isAlpha=" + this.isAlpha + '}';
    }
}