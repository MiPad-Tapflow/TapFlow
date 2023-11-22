package com.freerdp.freerdpcore.services;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import androidx.collection.LongSparseArray;
import com.xiaomi.mslgrdp.application.GlobalApp;
import com.xiaomi.mslgrdp.application.SessionState;
import com.xiaomi.mslgrdp.domain.BookmarkBase;
import com.xiaomi.mslgrdp.domain.ManualBookmark;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes5.dex */
public class LibFreeRDP {
    private static final String TAG = "LibFreeRDP";
    private static EventListener listener;
    private static boolean mHasH264;
    private static final LongSparseArray<Boolean> mInstanceState = new LongSparseArray<>();

    /* loaded from: classes5.dex */
    public interface EventListener {
        void OnConnectionFailure(long j);

        void OnConnectionSuccess(long j);

        void OnDisconnected(long j);

        void OnDisconnecting(long j);

        void OnPreConnect(long j);
    }

    /* loaded from: classes5.dex */
    public interface UIEventListener {
        boolean OnAuthenticate(StringBuilder sb, StringBuilder sb2, StringBuilder sb3);

        boolean OnGatewayAuthenticate(StringBuilder sb, StringBuilder sb2, StringBuilder sb3);

        void OnGraphicsResize(int i, int i2, int i3);

        void OnGraphicsUpdate(int i, int i2, int i3, int i4);

        void OnGraphicsUpdateMultiWindow(long j, int i, int i2, int i3, int i4, int i5, boolean z, boolean z2);

        void OnMinimizeRequested(boolean z);

        void OnOpenwpsRequested(boolean z);

        void OnRailChannelReady(boolean z);

        void OnRemoteClipboardChanged(String str);

        void OnSettingsChanged(int i, int i2, int i3);

        void OnUpdatePointerIcon(int i, int i2, int i3, int i4);

        int OnVerifiyCertificate(String str, String str2, String str3, String str4, boolean z);

        int OnVerifyChangedCertificate(String str, String str2, String str3, String str4, String str5, String str6, String str7);
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

    private static native boolean freerdp_send_clipboard_data(long j, String str);

    private static native boolean freerdp_send_cursor_event(long j, int i, int i2, int i3);

    private static native boolean freerdp_send_key_event(long j, int i, boolean z);

    private static native boolean freerdp_send_unicodekey_event(long j, int i, boolean z);

    private static native boolean freerdp_update_graphics(long j, Bitmap bitmap, int i);

    private static native boolean freerdp_update_pointer_icon(long j, Bitmap bitmap);

    static {
        mHasH264 = false;
        try {
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
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Failed to load library: " + e.toString());
            throw e;
        }
    }

    private static boolean tryLoad(String[] libraries) {
        boolean success = false;
        String LD_PATH = System.getProperty("java.library.path");
        for (String lib : libraries) {
            try {
                Log.v(TAG, "Trying to load library " + lib + " from LD_PATH: " + LD_PATH);
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

    public static void freeInstance(long inst)
    {
        synchronized (mInstanceState)
        {
            if (mInstanceState.get(inst, false))
            {
                freerdp_disconnect(inst);
            }
            while (mInstanceState.get(inst, false))
            {
                try
                {
                    mInstanceState.wait();
                }
                catch (InterruptedException e)
                {
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
            if (longSparseArray.get(inst, false).booleanValue()) {
                return freerdp_disconnect(inst);
            }
            return true;
        }
    }

    public static boolean cancelConnection(long inst) {
        LongSparseArray<Boolean> longSparseArray = mInstanceState;
        synchronized (longSparseArray) {
            if (longSparseArray.get(inst, false).booleanValue()) {
                return freerdp_disconnect(inst);
            }
            return true;
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

    public static boolean setConnectionInfo(Context context, long inst, BookmarkBase bookmark) {
        BookmarkBase.ScreenSettings screenSettings = bookmark.getActiveScreenSettings();
        BookmarkBase.AdvancedSettings advanced = bookmark.getAdvancedSettings();
        BookmarkBase.DebugSettings debug = bookmark.getDebugSettings();
        ArrayList<String> args = new ArrayList<>();
        args.add(TAG);
        args.add("/gdi:sw");
        if (!" ".isEmpty()) {
            args.add("/client-hostname: ");
        }
        if (bookmark.getType() != 1) {
            return false;
        }
        int port = ((ManualBookmark) bookmark.get()).getPort();
        String hostname = ((ManualBookmark) bookmark.get()).getHostname();
        args.add("/v:" + hostname);
        args.add("/port:" + String.valueOf(port));
        String arg = bookmark.getUsername();
        if (!arg.isEmpty()) {
            args.add("/u:" + arg);
        }
        String arg2 = bookmark.getDomain();
        if (!arg2.isEmpty()) {
            args.add("/d:" + arg2);
        }
        String arg3 = bookmark.getPassword();
        if (!arg3.isEmpty()) {
            args.add("/p:" + arg3);
        }
        args.add(String.format("/size:%dx%d", Integer.valueOf(screenSettings.getWidth()), Integer.valueOf(screenSettings.getHeight())));
        args.add("/bpp:" + String.valueOf(screenSettings.getColors()));
        if (advanced.getConsoleMode()) {
            args.add("/admin");
        }
        switch (advanced.getSecurity()) {
            case 1:
                args.add("/sec-rdp");
                break;
            case 2:
                args.add("/sec-tls");
                break;
            case 3:
                args.add("/sec-nla");
                break;
        }
        if (!"".isEmpty()) {
            args.add("/cert-name:");
        }
        BookmarkBase.PerformanceFlags flags = bookmark.getActivePerformanceFlags();
        if (flags.getRemoteFX()) {
            args.add("/rfx");
        }
        if (flags.getGfx()) {
            args.add("/gfx");
        }
        if (flags.getH264() && mHasH264) {
            args.add("/gfx:AVC444");
        }
        args.add(addFlag("wallpaper", flags.getWallpaper()));
        args.add(addFlag("window-drag", flags.getFullWindowDrag()));
        args.add(addFlag("menu-anims", flags.getMenuAnimations()));
        args.add(addFlag("themes", flags.getTheming()));
        args.add(addFlag("fonts", flags.getFontSmoothing()));
        args.add(addFlag("aero", flags.getDesktopComposition()));
        args.add(addFlag("glyph-cache", false));
        if (!advanced.getRemoteProgram().isEmpty()) {
            int space = TextUtils.indexOf((CharSequence) advanced.getRemoteProgram(), ' ');
            if (space <= 0) {
                args.add("/shell:" + advanced.getRemoteProgram());
            } else {
                String app = TextUtils.substring(advanced.getRemoteProgram(), 0, space);
                String arguments = TextUtils.substring(advanced.getRemoteProgram(), space + 1, advanced.getRemoteProgram().length());
                args.add("/shell:" + app);
                args.add("/app-cmd:" + changeToUbuntuPath(arguments));
            }
        }
        if (!advanced.getWorkDir().isEmpty()) {
            args.add("/shell-dir:" + advanced.getWorkDir());
        }
        args.add(addFlag("async-channels", debug.getAsyncChannel()));
        args.add(addFlag("async-input", debug.getAsyncInput()));
        args.add(addFlag("async-update", debug.getAsyncUpdate()));
        if (advanced.getRedirectSDCard()) {
            String path = Environment.getExternalStorageDirectory().getPath();
            args.add("/drive:sdcard," + path);
        }
        args.add("/clipboard");
        if (bookmark.getType() == 1 && ((ManualBookmark) bookmark.get()).getEnableGatewaySettings()) {
            ManualBookmark.GatewaySettings gateway = ((ManualBookmark) bookmark.get()).getGatewaySettings();
            args.add(String.format("/g:%s:%d", gateway.getHostname(), Integer.valueOf(gateway.getPort())));
            String arg4 = gateway.getUsername();
            if (!arg4.isEmpty()) {
                args.add("/gu:" + arg4);
            }
            String arg5 = gateway.getDomain();
            if (!arg5.isEmpty()) {
                args.add("/gd:" + arg5);
            }
            String arg6 = gateway.getPassword();
            if (!arg6.isEmpty()) {
                args.add("/gp:" + arg6);
            }
        }
        args.add("/audio-mode:" + String.valueOf(advanced.getRedirectSound()));
        if (advanced.getRedirectSound() == 0) {
            args.add("/sound");
        }
        if (advanced.getRedirectMicrophone()) {
            args.add("/microphone");
        }
        args.add("/cert-ignore");
        args.add("/log-level:" + debug.getDebugLevel());
        String[] arrayArgs = (String[]) args.toArray(new String[args.size()]);
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
        return freerdp_update_graphics(inst, bitmap, winId);
    }

    public static boolean updatePointerIcon(long inst, Bitmap bitmap) {
        return freerdp_update_pointer_icon(inst, bitmap);
    }

    public static boolean sendCursorEvent(long inst, int x, int y, int flags) {
        Log.v("MslDragLayout", "sendCursorEvent x = " + x + " , y = " + y);
        return freerdp_send_cursor_event(inst, x, y, flags);
    }

    public static boolean sendKeyEvent(long inst, int keycode, boolean down) {
        Log.v("MslDragLayout", "sendKeyEvent keycode = " + keycode);
        return freerdp_send_key_event(inst, keycode, down);
    }

    public static boolean sendUnicodeKeyEvent(long inst, int keycode, boolean down) {
        return freerdp_send_unicodekey_event(inst, keycode, down);
    }

    public static boolean sendClipboardData(long inst, String data) {
        return freerdp_send_clipboard_data(inst, data);
    }

    public static void openRemoteApp(long inst, String remoteApp, String cmdLine) {
        freerdp_open_linux_app(inst, remoteApp, changeToUbuntuPath(cmdLine));
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
        UIEventListener uiEventListener;
        SessionState s = GlobalApp.getSession(inst);
        if (s != null && (uiEventListener = s.getUIEventListener()) != null) {
            uiEventListener.OnSettingsChanged(width, height, bpp);
        }
    }

    private static boolean OnAuthenticate(long inst, StringBuilder username, StringBuilder domain, StringBuilder password) {
        UIEventListener uiEventListener;
        SessionState s = GlobalApp.getSession(inst);
        if (s == null || (uiEventListener = s.getUIEventListener()) == null) {
            return false;
        }
        return uiEventListener.OnAuthenticate(username, domain, password);
    }

    private static boolean OnGatewayAuthenticate(long inst, StringBuilder username, StringBuilder domain, StringBuilder password) {
        UIEventListener uiEventListener;
        SessionState s = GlobalApp.getSession(inst);
        if (s == null || (uiEventListener = s.getUIEventListener()) == null) {
            return false;
        }
        return uiEventListener.OnGatewayAuthenticate(username, domain, password);
    }

    private static int OnVerifyCertificate(long inst, String commonName, String subject, String issuer, String fingerprint, boolean hostMismatch) {
        UIEventListener uiEventListener;
        SessionState s = GlobalApp.getSession(inst);
        if (s == null || (uiEventListener = s.getUIEventListener()) == null) {
            return 0;
        }
        return uiEventListener.OnVerifiyCertificate(commonName, subject, issuer, fingerprint, hostMismatch);
    }

    private static int OnVerifyChangedCertificate(long inst, String commonName, String subject, String issuer, String fingerprint, String oldSubject, String oldIssuer, String oldFingerprint) {
        UIEventListener uiEventListener;
        SessionState s = GlobalApp.getSession(inst);
        if (s == null || (uiEventListener = s.getUIEventListener()) == null) {
            return 0;
        }
        return uiEventListener.OnVerifyChangedCertificate(commonName, subject, issuer, fingerprint, oldSubject, oldIssuer, oldFingerprint);
    }

    private static void OnGraphicsUpdate(long inst, int x, int y, int width, int height) {
        UIEventListener uiEventListener;
        SessionState s = GlobalApp.getSession(inst);
        if (s != null && (uiEventListener = s.getUIEventListener()) != null) {
            uiEventListener.OnGraphicsUpdate(x, y, width, height);
        }
    }

    private static void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha) {
        SessionState s = GlobalApp.getSession(inst);
        if (s == null) {
            return;
        }
        Log.v(MultiWindowManager.TAG, "OnGraphicsUpdateMultiWindow windowId = " + windowId);
        UIEventListener uiEventListener = s.getUIEventListener();
        synchronized (MultiWindowManager.lock) {
            if (windowId == 1 || windowId == 2) {
                if (uiEventListener != null) {
                    uiEventListener.OnGraphicsUpdateMultiWindow(inst, windowId, x, y, width, height, isPopWindow, isAlpha);
                }
            } else {
                MultiWindowManager.getManager().process(inst, windowId, x, y, width, height, isPopWindow, isAlpha);
            }
        }
    }

    private static void onWindowClosed(long inst, final int windowId) {
        SessionState s = GlobalApp.getSession(inst);
        if (s == null) {
            return;
        }
        MultiWindowManager.getManager().runOnUiThread(new Runnable() { // from class: com.freerdp.freerdpcore.services.LibFreeRDP.1
            @Override // java.lang.Runnable
            public void run() {
                Log.v(MultiWindowManager.TAG, "onWindowClosed windowId = " + windowId);
                MultiWindowManager.getManager().closeWindow(windowId);
            }
        });
    }

    private static void OnGraphicsResize(long inst, int width, int height, int bpp) {
        UIEventListener uiEventListener;
        SessionState s = GlobalApp.getSession(inst);
        if (s != null && (uiEventListener = s.getUIEventListener()) != null) {
            uiEventListener.OnGraphicsResize(width, height, bpp);
        }
    }

    private static void OnRemoteClipboardChanged(long inst, String data) {
        UIEventListener uiEventListener;
        SessionState s = GlobalApp.getSession(inst);
        if (s != null && (uiEventListener = s.getUIEventListener()) != null) {
            uiEventListener.OnRemoteClipboardChanged(data);
        }
    }

    private static void OnRailChannelReady(long inst, boolean ready) {
        Log.v(TAG, "RailChannelReady.");
        SessionState s = GlobalApp.getSession(inst);
        if (s == null) {
            return;
        }
        s.setRailChannelStatus(ready);
        UIEventListener uiEventListener = s.getUIEventListener();
        if (uiEventListener != null) {
            uiEventListener.OnRailChannelReady(ready);
        }
    }

    private static void OnMinimizeRequested(long inst, boolean minimized) {
        UIEventListener uiEventListener;
        if (minimized) {
            Log.v(TAG, "OnMinimizedRequested.");
            SessionState s = GlobalApp.getSession(inst);
            if (s != null && (uiEventListener = s.getUIEventListener()) != null) {
                uiEventListener.OnMinimizeRequested(true);
            }
        }
    }

    private static void OnOpenwpsRequested(long inst, boolean openwps) {
        UIEventListener uiEventListener;
        if (openwps) {
            Log.v(TAG, "OnOpenwpsRequested.");
            SessionState s = GlobalApp.getSession(inst);
            if (s != null && (uiEventListener = s.getUIEventListener()) != null) {
                uiEventListener.OnOpenwpsRequested(true);
            }
        }
    }

    private static void OnUpdatePointerIcon(long inst, int width, int height, int hotSpotX, int hotSpotY) {
        Log.v(TAG, "OnUpdatePointerIcon. width=" + width + ", height=" + height + ", hotSpotX=" + hotSpotX + ", hotSpotY=" + hotSpotY);
        SessionState s = GlobalApp.getSession(inst);
        if (s == null) {
            return;
        }
        UIEventListener uiEventListener = s.getUIEventListener();
        MultiWindowManager.getManager().OnUpdateMousePointer(inst, width, height, hotSpotX, hotSpotY);
        if (uiEventListener != null) {
            uiEventListener.OnUpdatePointerIcon(width, height, hotSpotX, hotSpotY);
        }
    }

    public static String getVersion() {
        return freerdp_get_version();
    }
}