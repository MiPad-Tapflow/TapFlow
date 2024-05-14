package com.xiaomi.mslgrdp.domain;

public class MslSurfaceInfo {
    public int appType;
    public int height;
    public int id;
    public boolean isAlpha;
    public boolean isMaximized;
    public boolean isModal;
    public boolean isPopWindow;
    public long sessionId;
    public int width;
    public int x;
    public int y;

    public MslSurfaceInfo() {
    }

    public MslSurfaceInfo(long sessionId, int id, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha, int appType, boolean isModal, boolean isMaximized) {
        this.sessionId = sessionId;
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isPopWindow = isPopWindow;
        this.isAlpha = isAlpha;
        this.appType = appType;
        this.isModal = isModal;
        this.isMaximized = isMaximized;
    }

    public boolean isDiff(MslSurfaceInfo other) {
        return (other != null && this.x == other.x && this.y == other.y && this.width == other.width && this.height == other.height) ? false : true;
    }

    public String toString() {
        return "MslSurfaceInfo{sessionId=" + this.sessionId + ", id=" + this.id + ", x=" + this.x + ", y=" + this.y + ", width=" + this.width + ", height=" + this.height + ", isPopWindow=" + this.isPopWindow + ", isAlpha=" + this.isAlpha + ", appType=" + this.appType + ", isModal=" + this.isModal + '}';
    }
}