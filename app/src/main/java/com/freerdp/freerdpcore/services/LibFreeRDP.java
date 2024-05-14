package com.freerdp.freerdpcore.services;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import androidx.collection.LongSparseArray;
import com.xiaomi.mslgrdp.multwindow.IServer;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.multwindow.SessionState;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.MslgLogger;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes6.dex */
public class LibFreeRDP {
    public static final int RDP_APP_TYPE_CAJVIEWER = 3;
    public static final int RDP_APP_TYPE_WPSOFFICE = 1;
    private static final String TAG = "LibFreeRDP";
    public static final int WINDOW_CLOSE_EVENT = 61536;
    public static final int WINDOW_MAXIMIZE_EVENT = 61488;
    public static final int WINDOW_MINIMIZE_EVENT = 61472;
    public static final int WINDOW_RESTORE_EVENT = 61728;
    private static EventListener listener;
    private static boolean mHasH264;
    private static final LongSparseArray<Boolean> mInstanceState = new LongSparseArray<>();
    private static int mWpsStartID = -1;
    public static volatile int mActivateWindowId = 0;
    private static boolean mWpsStart = false;

    /* loaded from: classes6.dex */
    public interface EventListener {
        void OnConnectionFailure(long j);

        void OnConnectionSuccess(long j);

        void OnDisconnected(long j);

        void OnDisconnecting(long j);

        void OnPreConnect(long j);
    }

    /* loaded from: classes6.dex */
    public interface UIEventListener {
        boolean OnAuthenticate(StringBuilder sb, StringBuilder sb2, StringBuilder sb3);

        void OnDeleteOptimg(int i);

        boolean OnGatewayAuthenticate(StringBuilder sb, StringBuilder sb2, StringBuilder sb3);

        void OnGraphicsResize(int i, int i2, int i3);

        void OnGraphicsUpdate(int i, int i2, int i3, int i4);

        void OnGraphicsUpdateMultiWindow(long j, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, String str, int i11, boolean z, boolean z2, int i12, boolean z3, boolean z4);

        boolean OnMinimizeRequested(int i, boolean z);

        void OnOpenwpsRequested(boolean z);

        void OnRailChannelReady(boolean z);

        void OnRemoteClipboardChanged(String str);

        void OnSettingsChanged(int i, int i2, int i3);

        void OnUpdatePointerIcon(long j, int i, int i2, int i3, int i4);

        int OnVerifiyCertificate(String str, String str2, String str3, String str4, boolean z);

        int OnVerifyChangedCertificate(String str, String str2, String str3, String str4, String str5, String str6, String str7);

        void onWindowClosed(long j, int i, int i2);

        void onWindowResize(long j, int i, int i2);
    }

    private static native boolean freerdp_connect(long j);

    private static native boolean freerdp_disconnect(long j);

    private static native void freerdp_free(long j);

    private static native String freerdp_get_build_config();

    private static native String freerdp_get_build_date();

    private static native String freerdp_get_build_revision();

    private static native String freerdp_get_jni_version();

    private static native String freerdp_get_last_error_string(long j);

    private static native String freerdp_get_version();

    private static native boolean freerdp_has_h264();

    private static native long freerdp_new(Context context);

    private static native boolean freerdp_open_linux_app(long j, String str, String str2);

    private static native boolean freerdp_parse_arguments(long j, String[] strArr);

    private static native boolean freerdp_send_app_destory(long j, int i);

    private static native boolean freerdp_send_clipboard_data(long j, String str);

    private static native boolean freerdp_send_cursor_event(long j, int i, int i2, int i3);

    private static native boolean freerdp_send_key_event(long j, int i, boolean z);

    private static native boolean freerdp_send_unicodekey_event(long j, int i, boolean z);

    private static native boolean freerdp_send_window_event(long j, int i, int i2);

    private static native boolean freerdp_send_window_focus_event(long j, int i, boolean z);

    private static native boolean freerdp_update_pointer_icon(long j, Bitmap bitmap);

    static {
        mHasH264 = false;
        try {
            if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
                System.loadLibrary("freerdp-android");
                String version = freerdp_get_jni_version();
                Pattern pattern = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+).*");
                Matcher matcher = pattern.matcher(version);
                if (!matcher.matches() || matcher.groupCount() < 3) {
                    throw new RuntimeException("APK broken: native library version " + version + " does not meet requirements!");
                }
                int major = Integer.parseInt((String) Objects.requireNonNull(matcher.group(1)));
                int minor = Integer.parseInt((String) Objects.requireNonNull(matcher.group(2)));
                int patch = Integer.parseInt((String) Objects.requireNonNull(matcher.group(3)));
                if (major > 2) {
                    mHasH264 = freerdp_has_h264();
                } else if (minor > 5) {
                    mHasH264 = freerdp_has_h264();
                } else if (minor == 5 && patch >= 1) {
                    mHasH264 = freerdp_has_h264();
                } else {
                    throw new RuntimeException("APK broken: native library version " + version + " does not meet requirements!");
                }
                Log.i(TAG, "Successfully loaded native library. H264 is " + (mHasH264 ? "supported" : "not available"));
            }
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load library: " + e.toString());
            throw e;
        }
    }

    private static boolean tryLoad(String[] libraries) {
        if (!Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            return false;
        }
        boolean success = false;
        String LD_PATH = System.getProperty("java.library.path");
        for (String lib : libraries) {
            try {
                MslgLogger.LOGD(TAG, "Trying to load library " + lib + " from LD_PATH: " + LD_PATH, false);
                System.loadLibrary(lib);
                success = true;
            } catch (UnsatisfiedLinkError e) {
                Log.e(TAG, "Failed to load library " + lib + ": " + e.toString());
                return false;
            }
        }
        return success;
    }

    private static boolean tryLoad(String library) {
        return tryLoad(new String[]{library});
    }

    public static boolean hasH264Support() {
        return mHasH264;
    }

    public static void setEventListener(EventListener l) {
        listener = l;
    }

    public static long newInstance(Context context) {
        return freerdp_new(context);
    }

    public static void freeInstance(long inst){
        synchronized (mInstanceState){
            if (mInstanceState.get(inst, false)) {
                freerdp_disconnect(inst);
            }
            while (mInstanceState.get(inst, false)) {
                try {
                    mInstanceState.wait();
                }
                catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
        }
        freerdp_free(inst);
    }

    public static boolean connect(long inst) {
        LongSparseArray<Boolean> longSparseArray = mInstanceState;
        synchronized (longSparseArray) {
            if (longSparseArray.get(inst, false).booleanValue()) {
                throw new RuntimeException("instance already connected");
            }
        }
        return freerdp_connect(inst);
    }

    public static boolean disconnect(long inst) {
        LongSparseArray<Boolean> longSparseArray = mInstanceState;
        synchronized (longSparseArray) {
            if (!longSparseArray.get(inst, false).booleanValue()) {
                return true;
            }
            return freerdp_disconnect(inst);
        }
    }

    public static boolean cancelConnection(long inst) {
        LongSparseArray<Boolean> longSparseArray = mInstanceState;
        synchronized (longSparseArray) {
            if (!longSparseArray.get(inst, false).booleanValue()) {
                return true;
            }
            return freerdp_disconnect(inst);
        }
    }

    private static String addFlag(String name, boolean enabled) {
        if (enabled) {
            return "+" + name;
        }
        return "-" + name;
    }

    private static String changeToUbuntuPath(String androidPath) {
        String[][] pathMap = {new String[]{"/external_files/", "/tablet/"}};
        if (!TextUtils.isEmpty(androidPath)) {
            for (String[] array : pathMap) {
                if (androidPath.indexOf(array[0]) >= 0) {
                    return androidPath.replaceFirst(array[0], array[1]);
                }
            }
            return androidPath;
        }
        return null;
    }

    public static boolean setConnectionInfo(long inst) {
        ArrayList<String> args = new ArrayList<>();
        args.add(TAG);
        args.add("/gdi:sw");
        args.add("/client-hostname: ");
        args.add("/v:/dev/msl/rdp/rdp_socket");
        args.add("/port:" + String.valueOf(3389));
        args.add(String.format("/size:%sx%s", Integer.valueOf(Constances.SCREEN_WIDTH), Integer.valueOf(Constances.SCREEN_HEIGHT)));
        args.add("/bpp:32");
        args.add("/rfx");
        args.add("/gfx");
        args.add(addFlag("wallpaper", false));
        args.add(addFlag("window-drag", false));
        args.add(addFlag("menu-anims", false));
        args.add(addFlag("themes", false));
        args.add(addFlag("fonts", false));
        args.add(addFlag("aero", false));
        args.add(addFlag("glyph-cache", false));
        args.add(addFlag("async-channels", false));
        args.add(addFlag("async-input", false));
        args.add(addFlag("async-update", false));
        args.add("/clipboard");
        args.add("/audio-mode:0");
        args.add("/sound");
        args.add("/cert-ignore");
        args.add("/log-level:DEBUG");
        String[] arrayArgs = (String[]) args.toArray(new String[args.size()]);
        for (String item : arrayArgs) {
            MslgLogger.LOGD("arrayArgs", item, false);
        }
        return freerdp_parse_arguments(inst, arrayArgs);
    }

    public static boolean setConnectionInfo(Context context, long inst, Uri openUri) {
        ArrayList<String> args = new ArrayList<>();
        args.add(TAG);
        args.add("/gdi:sw");
        if (!" ".isEmpty()) {
            args.add("/client-hostname: ");
        }
        String hostname = openUri.getHost();
        int port = openUri.getPort();
        if (hostname != null) {
            args.add("/v:" + (hostname + (port == -1 ? "" : ":" + String.valueOf(port))));
        }
        String user = openUri.getUserInfo();
        if (user != null) {
            args.add("/u:" + user);
        }
        for (String key : openUri.getQueryParameterNames()) {
            String value = openUri.getQueryParameter(key);
            if (value.isEmpty()) {
                args.add("/" + key);
            } else if (value.equals("-") || value.equals("+")) {
                args.add(value + key);
            } else {
                if (key.equals("drive") && value.equals("sdcard")) {
                    String path = Environment.getExternalStorageDirectory().getPath();
                    value = "sdcard," + path;
                }
                args.add("/" + key + ":" + value);
            }
        }
        String[] arrayArgs = (String[]) args.toArray(new String[args.size()]);
        return freerdp_parse_arguments(inst, arrayArgs);
    }

    public static boolean updateGraphics(long inst, Bitmap bitmap, int winId) {
        return false;
    }

    public static boolean updatePointerIcon(long inst, Bitmap bitmap) {
        return freerdp_update_pointer_icon(inst, bitmap);
    }

    public static boolean sendKillappEvent(long inst, int appType) {
        if (appType == 1) {
            mWpsStartID = -1;
            mWpsStart = false;
        }
        return freerdp_send_app_destory(inst, appType);
    }

    public static boolean sendCursorEvent(long inst, int x, int y, int flags) {
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            MslgLogger.LOGD(MslgLogger.TAG_EVENT, "sendCursorEvent x = " + x + " , y = " + y, false);
            return freerdp_send_cursor_event(inst, x, y, flags);
        }
        IServer iSession = MultiWindowManager.getManager().getServiceToken();
        if (iSession != null) {
            try {
                return iSession.sendCursorEvent(inst, x, y, flags);
            } catch (RemoteException e) {
                MslgLogger.LOGD(MslgLogger.TAG_EVENT, "RemoteException sendCursorEvent ", true);
            }
        }
        return false;
    }

    public static boolean sendKeyEvent(long inst, int keycode, boolean down) {
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            return freerdp_send_key_event(inst, keycode, down);
        }
        IServer iSession = MultiWindowManager.getManager().getServiceToken();
        if (iSession != null) {
            try {
                return iSession.sendKeyEvent(inst, keycode, down);
            } catch (RemoteException e) {
                e.printStackTrace();
                MslgLogger.LOGD(MslgLogger.TAG_EVENT, "RemoteException sendKeyEvent ", true);
                return false;
            }
        }
        return false;
    }

    public static boolean sendUnicodeKeyEvent(long inst, int keycode, boolean down) {
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            return freerdp_send_unicodekey_event(inst, keycode, down);
        }
        IServer iSession = MultiWindowManager.getManager().getServiceToken();
        if (iSession != null) {
            try {
                return iSession.sendUnicodeKeyEvent(inst, keycode, down);
            } catch (RemoteException e) {
                MslgLogger.LOGD(MslgLogger.TAG_EVENT, "RemoteException sendUnicodeKeyEvent ", true);
                return false;
            }
        }
        return false;
    }

    public static boolean sendClipboardData(long inst, String data) {
        return freerdp_send_clipboard_data(inst, data);
    }

    public static void openRemoteApp(long inst, String remoteApp, String cmdLine) {
        freerdp_open_linux_app(inst, remoteApp, changeToUbuntuPath(cmdLine));
    }

    public static void sendWindowEvent(long inst, int windId, int cmdId) {
        MslgLogger.LOGD(MultiWindowManager.TAG, "sendWindowEvent windId = " + windId + " cmdid = " + cmdId, false);
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            freerdp_send_window_event(inst, windId, cmdId);
            return;
        }
        IServer iSession = MultiWindowManager.getManager().getServiceToken();
        if (iSession != null) {
            try {
                iSession.sendWindowEvent(inst, windId, cmdId);
            } catch (RemoteException e) {
                MslgLogger.LOGD(MslgLogger.TAG_EVENT, "RemoteException sendWindowEvent", true);
            }
        }
    }

    public static void sendWindowFocusEvent(long inst, int windId, boolean focus) {
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            synchronized (LibFreeRDP.class) {
                if (windId != mActivateWindowId) {
                    freerdp_send_window_focus_event(inst, windId, focus);
                }
            }
            return;
        }
        IServer iSession = MultiWindowManager.getManager().getServiceToken();
        if (iSession != null) {
            try {
                synchronized (LibFreeRDP.class) {
                    if (windId != mActivateWindowId) {
                        mActivateWindowId = windId;
                    }
                }
                iSession.sendWindowFocusEvent(inst, windId, focus, MultiWindowManager.getManager().getAppType());
            } catch (RemoteException e) {
                MslgLogger.LOGD(MultiWindowManager.TAG, "RemoteException sendWindowEvent", true);
            }
        }
    }

    private static void OnConnectionSuccess(long inst) {
        EventListener eventListener = listener;
        if (eventListener != null) {
            eventListener.OnConnectionSuccess(inst);
        }
        LongSparseArray<Boolean> longSparseArray = mInstanceState;
        synchronized (longSparseArray) {
            longSparseArray.append(inst, true);
            longSparseArray.notifyAll();
        }
    }

    private static void OnConnectionFailure(long inst) {
        EventListener eventListener = listener;
        if (eventListener != null) {
            eventListener.OnConnectionFailure(inst);
        }
        LongSparseArray<Boolean> longSparseArray = mInstanceState;
        synchronized (longSparseArray) {
            longSparseArray.remove(inst);
            longSparseArray.notifyAll();
        }
    }

    private static void OnPreConnect(long inst) {
        EventListener eventListener = listener;
        if (eventListener != null) {
            eventListener.OnPreConnect(inst);
        }
    }

    private static void OnDisconnecting(long inst) {
        EventListener eventListener = listener;
        if (eventListener != null) {
            eventListener.OnDisconnecting(inst);
        }
    }

    private static void OnDisconnected(long inst) {
        EventListener eventListener = listener;
        if (eventListener != null) {
            eventListener.OnDisconnected(inst);
        }
        LongSparseArray<Boolean> longSparseArray = mInstanceState;
        synchronized (longSparseArray) {
            longSparseArray.remove(inst);
            longSparseArray.notifyAll();
        }
    }

    private static void OnSettingsChanged(long inst, int width, int height, int bpp) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnSettingsChanged(width, height, bpp);
            }
        }
    }

    private static boolean OnAuthenticate(long inst, StringBuilder username, StringBuilder domain, StringBuilder password) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return false;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnAuthenticate(username, domain, password);
            }
        }
        return false;
    }

    private static boolean OnGatewayAuthenticate(long inst, StringBuilder username, StringBuilder domain, StringBuilder password) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return false;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnGatewayAuthenticate(username, domain, password);
            }
        }
        return false;
    }

    private static int OnVerifyCertificate(long inst, String commonName, String subject, String issuer, String fingerprint, boolean hostMismatch) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return 0;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnVerifiyCertificate(commonName, subject, issuer, fingerprint, hostMismatch);
            }
        }
        return 0;
    }

    private static int OnVerifyChangedCertificate(long inst, String commonName, String subject, String issuer, String fingerprint, String oldSubject, String oldIssuer, String oldFingerprint) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return 0;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnVerifyChangedCertificate(commonName, subject, issuer, fingerprint, oldSubject, oldIssuer, oldFingerprint);
            }
        }
        return 0;
    }

    private static void OnGraphicsUpdate(long inst, int x, int y, int width, int height) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnGraphicsUpdate(x, y, width, height);
            }
        }
    }

    public static void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int b_width, int b_height, int left, int top, int dirty_w, int dirty_h, int stride, String file_name, int size, int appType, boolean isPopWindow, boolean isAlpha, boolean isModal, boolean isMaximized) {
        MslgLogger.LOGD(MultiWindowManager.TAG, "OnGraphicsUpdateMultiWindow windowId = " + windowId + " isModal = " + isModal + " isMaximized = " + isMaximized, false);
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (appType == 1) {
            if (b_width != 1354 || b_height != 954 || size != 5169152) {
                if (b_width == 1350 && b_height == 950 && size == 5132288) {
                    return;
                }
            } else {
                return;
            }
        }
        if (s != null && Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
            for (UIEventListener uiEventListener : uiEventListeners) {
                if (uiEventListener != null) {
                    uiEventListener.OnGraphicsUpdateMultiWindow(inst, windowId, x, y, b_width, b_height, left, top, dirty_w, dirty_h, stride, file_name, size, isPopWindow, isAlpha, appType, isModal, isMaximized);
                }
            }
        }
    }

    public static void onWindowClosed(long inst, final int windowId, int appType) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return;
        }
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
            for (UIEventListener uiEventListener : uiEventListeners) {
                if (uiEventListener != null) {
                    uiEventListener.onWindowClosed(inst, windowId, appType);
                }
            }
            return;
        }
        MultiWindowManager.getManager().runOnUiThread(new Runnable() { // from class: com.freerdp.freerdpcore.services.LibFreeRDP.1
            @Override // java.lang.Runnable
            public void run() {
                MslgLogger.LOGD(MultiWindowManager.TAG, "onWindowClosed windowId = " + windowId, false);
                MultiWindowManager.getManager().closeWindow(windowId);
            }
        });
    }

    public static void onWindowResize(long inst, int windowId, int appType) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s != null && Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
            for (UIEventListener uiEventListener : uiEventListeners) {
                if (uiEventListener != null) {
                    uiEventListener.onWindowResize(inst, windowId, appType);
                }
            }
        }
    }

    private static void onActivateWindowUpdated(long inst, int windowId) {
        synchronized (LibFreeRDP.class) {
            MslgLogger.LOGD(MslgLogger.TAG_ACTIVITY, "Ubuntu window " + windowId + " becomes to focused.", false);
            mActivateWindowId = windowId;
        }
    }

    private static void OnGraphicsResize(long inst, int width, int height, int bpp) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnGraphicsResize(width, height, bpp);
            }
        }
    }

    private static void OnRemoteClipboardChanged(long inst, String data) {
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnRemoteClipboardChanged(data);
            }
        }
    }

    private static void OnRailChannelReady(long inst, boolean ready) {
        MslgLogger.LOGD(TAG, "RailChannelReady.", false);
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return;
        }
        s.setRailChannelStatus(ready);
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnRailChannelReady(ready);
            }
        }
    }

    public static void OnMinimizeRequested(long inst, int appType, boolean minimized) {
        if (minimized) {
            MslgLogger.LOGD(TAG, "OnMinimizedRequested.appType " + appType, false);
            SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
            if (s == null) {
                return;
            }
            synchronized (LibFreeRDP.class) {
                Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
                for (UIEventListener uiEventListener : uiEventListeners) {
                    if (uiEventListener != null) {
                        boolean handle = uiEventListener.OnMinimizeRequested(appType, true);
                        if (handle) {
                            break;
                        }
                    }
                }
            }
        }
    }

    public static void OnDeleteOptimg(long inst, int appType) {
        Log.v(TAG, "OnDeleteOptimg.appType " + appType);
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return;
        }
        Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
        for (UIEventListener uiEventListener : uiEventListeners) {
            if (uiEventListener != null) {
                uiEventListener.OnDeleteOptimg(appType);
            }
        }
    }

    private static void OnOpenwpsRequested(long inst, boolean openwps) {
        if (openwps) {
            MslgLogger.LOGD(TAG, "OnOpenwpsRequested.", false);
            SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
            if (s == null) {
                return;
            }
            Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
            for (UIEventListener uiEventListener : uiEventListeners) {
                if (uiEventListener != null) {
                    uiEventListener.OnOpenwpsRequested(true);
                }
            }
        }
    }

    private static void OnUpdatePointerIcon(long inst, int width, int height, int hotSpotX, int hotSpotY) {
        MslgLogger.LOGD(TAG, "OnUpdatePointerIcon. width=" + width + ", height=" + height + ", hotSpotX=" + hotSpotX + ", hotSpotY=" + hotSpotY + " inst =" + inst, false);
        SessionState s = MultiWindowManager.getSessionManager().getCurrentSession();
        if (s == null) {
            return;
        }
        if (Application.getProcessName().contains(Constances.RDP_PROCESS_NAME)) {
            Set<UIEventListener> uiEventListeners = s.getUIEventListeners();
            for (UIEventListener uiEventListener : uiEventListeners) {
                if (uiEventListener != null) {
                    uiEventListener.OnUpdatePointerIcon(inst, width, height, hotSpotX, hotSpotY);
                }
            }
            return;
        }
        synchronized (MultiWindowManager.lock) {
            Bitmap bitmap_pointer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            updatePointerIcon(inst, bitmap_pointer);
            MultiWindowManager.getManager().OnUpdateMousePointer(inst, width, height, hotSpotX, hotSpotY, bitmap_pointer);
        }
    }

    public static String getVersion() {
        return freerdp_get_version();
    }
}