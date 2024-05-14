package com.xiaomi.mslgrdp.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.multwindow.SessionState;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes6.dex */
public class Utils {
    private static final String TAG = "MslgRdp_Utils";

    public static void setProperty(String key, String value) {

    }

    public static String getProperty(String key) {
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method setMethod = systemPropertiesClass.getDeclaredMethod("get", String.class);
            setMethod.setAccessible(true);
            return setMethod.invoke(null, key).toString();
        } catch (Exception e) {
            Log.e(TAG, "getProperty " + e);
            e.printStackTrace();
            return " ";
        }
    }

    public static boolean isInputMethodShowing(Window window, WindowManager windowManager) {
        int screenHeight = window.getDecorView().getHeight();
        Rect rect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        return (screenHeight - rect.bottom) - getSoftButtonsBarHeight(windowManager) > 0;
    }

    public static boolean isInputMethodShowing(View decorView, WindowManager windowManager) {
        if (decorView == null || windowManager == null) {
            return false;
        }
        int screenHeight = decorView.getHeight();
        Rect rect = new Rect();
        decorView.getWindowVisibleDisplayFrame(rect);
        return (screenHeight - rect.bottom) - getSoftButtonsBarHeight(windowManager) > 0;
    }

    public static int getSoftButtonsBarHeight(WindowManager windowManager) {
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        windowManager.getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        }
        return 0;
    }

    public static void hideSoftInput(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && v != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static synchronized boolean sendPath(boolean alive, String url, Context context, String app) {
        synchronized (Utils.class) {
            SessionState session = MultiWindowManager.getSessionManager().getCurrentSession();
            Log.v(TAG, "sendPath fileurl =" + url);
            String appType = "/usr/bin/wps";
            if (app != null && app.equals("wps")) {
                appType = "/usr/bin/wps";
            }
            if (app != null && app.equals("cajviewer")) {
                appType = Constances.CAJVIEWER;
            }
            Log.v(TAG, "sendPath appType =" + appType);
            if (url != null && !url.isEmpty()) {
                Uri uri = Uri.parse(url);
                String path = uri.getPath();
                if (path != null) {
                    if (uri.getAuthority().equals("media")) {
                        path = getFilePathFromContentUri(context, uri, appType);
                    }
                    if (path.contains("/storage/emulated/0/")) {
                        path = path.replace("/storage/emulated/0/", "/sdcard/");
                    }
                    String[] s = path.split("/");
                    if (!s[1].equals("tablet")) {
                        path = path.replace(s[1], "tablet");
                    }
                }
                Log.v(TAG, "sendPath path =" + path);
                if (path != null && appType.equals("/usr/bin/wps")) {
                    appType = getAppType(path);
                }
                Log.v(TAG, "appType = " + appType);
                if (session != null && session.getRailChannelStatus()) {
                    LibFreeRDP.openRemoteApp(session.getInstance(), appType, path);
                    return true;
                }
            } else if (session != null && session.getRailChannelStatus()) {
                Log.v(TAG, "Open defalut app" + appType);
                LibFreeRDP.openRemoteApp(session.getInstance(), appType, null);
                return true;
            }
            return false;
        }
    }

    private static String getFilePathFromContentUri(Context context, Uri uri, String app) {
        if (uri == null) {
            return null;
        }
        String data = null;
        try {
            String[] filePathColumn = {"_data", "_display_name"};
            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex("_data");
                    if (index > -1) {
                        data = cursor.getString(index);
                    } else {
                        int nameIndex = cursor.getColumnIndex("_display_name");
                        String fileName = cursor.getString(nameIndex);
                        data = getPathFromInputStreamUri(context, uri, fileName, app);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:24:0x0020 -> B:11:0x0035). Please report as a decompilation issue!!! */
    private static String getPathFromInputStreamUri(Context context, Uri uri, String fileName, String app) {
        InputStream inputStream = null;
        String filePath = null;
        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File file = createTemporalFileFrom(context, inputStream, fileName, app);
                filePath = file.getPath();
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Exception e) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e2) {
                    }
                }
                throw th;
            }
        }
        return filePath;
    }

    private static File createTemporalFileFrom(Context context, InputStream inputStream, String fileName, String app) throws IOException {
        File targetFile = null;
        if (inputStream != null) {
            try {
                byte[] buffer = new byte[8192];
                if (app != null && app.equals("/usr/bin/wps")) {
                    targetFile = new File(Constances.FILE_PATH, fileName);
                }
                if (app != null && app.equals(Constances.CAJVIEWER)) {
                    targetFile = new File(Constances.FILE_PATH_CAJ, fileName);
                }
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                OutputStream outputStream = new FileOutputStream(targetFile);
                while (true) {
                    int read = inputStream.read(buffer);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                try {
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return targetFile;
    }

    private static String getAppType(String path) {
        String appType;
        MimeTypeMap mMap = MimeTypeMap.getSingleton();
        String mimeType = mMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(path));
        if (mimeType != null) {
            Log.v(TAG, "openFile mimeType = " + mimeType);
            appType = getOpenApp(mimeType);
        } else {
            appType = getOpenAppExten(path);
        }
        Log.v(TAG, "getAppType = " + appType);
        return appType;
    }

    private static String getOpenAppExten(String type) {
        if (!type.endsWith(".doc") && !type.endsWith(".doc") && !type.endsWith(".docm") && !type.endsWith(".rtf") && !type.endsWith(".dot") && !type.endsWith(".dotm") && !type.endsWith(".dotx") && !type.endsWith(".wps") && !type.endsWith(".wpt") && !type.endsWith(".wpso") && !type.endsWith(".wpss") && !type.endsWith(".ppt") && !type.endsWith(".pptm") && !type.endsWith(".pps") && !type.endsWith(".pot") && !type.endsWith(".potm") && !type.endsWith(".potx") && !type.endsWith(".pptx") && !type.endsWith(".ppsx") && !type.endsWith(".dps") && !type.endsWith(".dpss") && !type.endsWith(".dpso") && !type.endsWith(".xls") && !type.endsWith(".xlsm") && !type.endsWith(".xlt") && !type.endsWith(".xltm") && !type.endsWith(".xltx") && !type.endsWith(".xlsx") && !type.endsWith(".et") && !type.endsWith(".ett") && !type.endsWith(".ets")) {
            type.endsWith(".eto");
        }
        return "/usr/bin/wps";
    }

    private static String getOpenApp(String type) {
        if (type.isEmpty()) {
            return "";
        }
        for (String name : Constances.TYPE_ET) {
            if (type.equals(name)) {
                return "/usr/bin/wps";
            }
        }
        for (String name2 : Constances.TYPE_WPP) {
            if (type.equals(name2)) {
                return "/usr/bin/wps";
            }
        }
        for (String name3 : Constances.TYPE_WPS) {
            if (type.equals(name3)) {
                return "/usr/bin/wps";
            }
        }
        return "/usr/bin/wps";
    }

    public static List<File> traverseFolder(File folder) {
        List<File> files = new ArrayList<>();
        if (folder.isDirectory()) {
            files.add(folder);
            traverseFolder5(files, folder, 0);
        }
        return files;
    }

    private static void traverseFolder5(List<File> folders, File folder, int depth) {
        if (depth != 5 && folders.size() < 1000 && folder.isDirectory()) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.isDirectory() && !file.isHidden()) {
                    folders.add(file);
                }
            }
            for (File file2 : files) {
                if (file2.isDirectory() && !file2.isHidden()) {
                    if ((file2.getPath().contains("Documents") || file2.getPath().contains("Download") || file2.getPath().contains("HyperEngine")) && depth == 0) {
                        traverseFolder5(folders, file2, depth + 1);
                    } else if (depth != 0) {
                        traverseFolder5(folders, file2, depth + 1);
                    }
                }
            }
        }
    }

    public static boolean isWPSFile(String path) {
        if (path.endsWith(".doc") || path.endsWith(".docx") || path.endsWith(".docm") || path.endsWith(".rtf") || path.endsWith(".dot") || path.endsWith(".dotm") || path.endsWith(".dotx") || path.endsWith(".wps") || path.endsWith(".wpt") || path.endsWith(".wpso") || path.endsWith(".wpss") || path.endsWith(".ppt") || path.endsWith(".pptm") || path.endsWith(".pps") || path.endsWith(".pot") || path.endsWith(".potm") || path.endsWith(".potx") || path.endsWith(".pptx") || path.endsWith(".ppsx") || path.endsWith(".dps") || path.endsWith(".dpss") || path.endsWith(".dpso") || path.endsWith(".xls") || path.endsWith(".xlsm") || path.endsWith(".xlt") || path.endsWith(".xltm") || path.endsWith(".xltx") || path.endsWith(".xlsx") || path.endsWith(".et") || path.endsWith(".ett") || path.endsWith(".ets") || path.endsWith(".eto")) {
            return true;
        }
        return false;
    }
}