package com.xiaomi.mslgrdp.multwindow.imp;

import com.freerdp.freerdpcore.services.LibFreeRDP;

/* loaded from: classes5.dex */
public class FreerdpUIEventListenerAdapter implements LibFreeRDP.UIEventListener {
    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnSettingsChanged(int width, int height, int bpp) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public boolean OnAuthenticate(StringBuilder username, StringBuilder domain, StringBuilder password) {
        return false;
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public boolean OnGatewayAuthenticate(StringBuilder username, StringBuilder domain, StringBuilder password) {
        return false;
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public int OnVerifiyCertificate(String commonName, String subject, String issuer, String fingerprint, boolean mismatch) {
        return 0;
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public int OnVerifyChangedCertificate(String commonName, String subject, String issuer, String fingerprint, String oldSubject, String oldIssuer, String oldFingerprint) {
        return 0;
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnGraphicsUpdate(int x, int y, int width, int height) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnGraphicsResize(int width, int height, int bpp) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnRemoteClipboardChanged(String data) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnRailChannelReady(boolean ready) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnMinimizeRequested(boolean minimized) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnOpenwpsRequested(boolean openwps) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnUpdatePointerIcon(int width, int height, int hotSpotX, int hotSpotY) {
    }
}