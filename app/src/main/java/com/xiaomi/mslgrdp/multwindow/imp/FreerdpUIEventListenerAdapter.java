package com.xiaomi.mslgrdp.multwindow.imp;

import com.freerdp.freerdpcore.services.LibFreeRDP;

/* loaded from: classes5.dex */
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
    public void OnMinimizeRequested(boolean minimized) {
    }

    @Override
    public void OnOpenwpsRequested(boolean openwps) {
    }

    @Override
    public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha) {
    }

    @Override
    public void OnUpdatePointerIcon(int width, int height, int hotSpotX, int hotSpotY) {
    }
}