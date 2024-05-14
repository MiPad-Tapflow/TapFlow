package com.xiaomi.mslgrdp.multwindow.imp;

import com.freerdp.freerdpcore.services.LibFreeRDP;

public class FreerdpUIEventListenerAdapter implements LibFreeRDP.UIEventListener {
    @Override 
    public void OnSettingsChanged(int width, int height, int bpp) {
    }

    @Override 
    public boolean OnAuthenticate(StringBuilder username, StringBuilder domain, StringBuilder password) {
        return false;
    }

    @Override 
    public boolean OnGatewayAuthenticate(StringBuilder username, StringBuilder domain, StringBuilder password) {
        return false;
    }

    @Override 
    public int OnVerifiyCertificate(String commonName, String subject, String issuer, String fingerprint, boolean mismatch) {
        return 0;
    }

    @Override 
    public int OnVerifyChangedCertificate(String commonName, String subject, String issuer, String fingerprint, String oldSubject, String oldIssuer, String oldFingerprint) {
        return 0;
    }

    @Override 
    public void OnGraphicsUpdate(int x, int y, int width, int height) {
    }

    @Override 
    public void OnGraphicsResize(int width, int height, int bpp) {
    }

    @Override 
    public void OnRemoteClipboardChanged(String data) {
    }

    @Override 
    public void OnRailChannelReady(boolean ready) {
    }

    @Override 
    public void OnDeleteOptimg(int appType) {
    }

    @Override 
    public boolean OnMinimizeRequested(int appType, boolean minimized) {
        return false;
    }

    @Override 
    public void OnOpenwpsRequested(boolean openwps) {
    }

    @Override 
    public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int b_width, int b_height, int left, int top, int dirty_w, int dirty_h, int stride, String file_name, int size, boolean isPopWindow, boolean isAlpha, int appType, boolean isModal, boolean isMaximized) {
    }

    @Override 
    public void OnUpdatePointerIcon(long inst, int width, int height, int hotSpotX, int hotSpotY) {
    }

    @Override 
    public void onWindowClosed(long inst, int windowId, int appType) {
    }

    @Override 
    public void onWindowResize(long inst, int windowId, int appType) {
    }
}