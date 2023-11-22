package com.xiaomi.mslgrdp.presentation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.freerdp.freerdpcore.services.RunningStateService;
import com.xiaomi.mslgrdp.application.GlobalApp;
import com.xiaomi.mslgrdp.application.SessionState;
import com.xiaomi.mslgrdp.domain.BookmarkBase;
import com.xiaomi.mslgrdp.utils.Mouse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;

import cn.ljlVink.Tapflow.R;


/* loaded from: classes5.dex */
public class MslSessionActivity extends AppCompatActivity implements LibFreeRDP.UIEventListener {
    private static final String FILE_PATH = "/sdcard/Download/Cache/WpsOffice";
    private static final int FIRST_CHARACTER_IN_ASCII = 32;
    private static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = Integer.MIN_VALUE;
    private static final int FLAG_RECEIVER_INCLUDE_BACKGROUND = 16777216;
    private static final int LAST_CHARACTER_IN_ASCII = 126;
    private static final int MAX_DISCARDED_MOVE_EVENTS = 3;
    public static final String PARAM_CONNECTION_REFERENCE = "conRef";
    public static final String PARAM_INSTANCE = "instance";
    private static final int SCROLLING_DISTANCE = 20;
    private static final int SCROLLING_TIMEOUT = 50;
    private static final int SEND_MOVE_EVENT_TIMEOUT = 150;
    public static final int SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 8192;
    private static final String TAG = "MslSessionActivity";
    private static final float ZOOMING_STEP = 0.5f;
    private Bitmap bitmap;
    private boolean callbackDialogResult;
    private AlertDialog dlgManagerRequset;
    private AlertDialog dlgPerssionRequset;
    private AlertDialog dlgPerssionRequsetRefuse;
    private LibFreeRDPBroadcastReceiver libFreeRDPBroadcastReceiver;
    View mDecor;
    private int screen_height;
    private int screen_width;
    private SessionState session;
    private View tempView;
    private UIHandler uiHandler;
    public static Boolean isExit = false;
    public static Boolean isAlive = false;
    public static boolean D = false;
    private static final String[] TYPE_ET = {"application/vnd.ms-excel", "application/msexcel", "application/x-msexcel", "application/vnd.ms-excel.sheet.macroenabled.12", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel.template.macroenabled.12", "application/vnd.openxmlformats-officedocument.spreadsheetml.template"};
    private static final String[] TYPE_WPP = {"application/vnd.ms-powerpoint", "application/powerpoint", "application/mspowerpoint", "application/x-mspowerpoint", "application/vnd.ms-powerpoint.presentation.macroenabled.12", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.openxmlformats-officedocument.presentationml.slideshow", "application/vnd.openxmlformats-officedocument.presentationml.template"};
    private static final String[] TYPE_WPS = {"application/msword", "application/vnd.ms-word", "application/x-msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-word.document.macroenabled.12", "application/msword-template", "application/vnd.ms-word.template.macroenabled.12", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.wordprocessingml.template", "application/x-ole-storage", "application/x-ole-storage"};
    public static Boolean isSet = false;
    private int inputCount = 0;
    private boolean isRefreshUi = false;
    private boolean sessionRunning = false;
    private boolean toggleMouseButtons = false;
    private boolean isCheck = false;
    private boolean isOpenRequest = false;
    private boolean isSuccess = false;
    private boolean sysKeyboardVisible = false;
    private boolean extKeyboardVisible = false;
    private int discardedMoveEvents = 0;
    private final String ET = "/usr/bin/et";
    private final String WPP = "/usr/bin/wpp";
    private final String WPS = "/usr/bin/wps";
    public String MSLGKEY = "sys.mslg.mounted";

    private boolean hasHardwareMenuButton() {
        if (Build.VERSION.SDK_INT <= 10) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= 14) {
            ViewConfiguration cfg = ViewConfiguration.get(this);
            return cfg.hasPermanentMenuKey();
        }
        return false;
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("WPS Office");
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(134217728);
        getWindow().requestFeature(12);
        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());
        if (!getProperty(this.MSLGKEY).equals("1")) {
            isSet = true;
            Log.d(TAG, "set SystemProperty");
            setProperty(this.MSLGKEY, "1");
        }
        setContentView(R.layout.activity_welcome);
        setFinishOnTouchOutside(false);
        Log.v(TAG, "Session.onCreate");
        final View activityRootView = findViewById(R.id.session_root_view);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.xiaomi.mslgrdp.presentation.MslSessionActivity.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                MslSessionActivity.this.screen_width = activityRootView.getWidth();
                MslSessionActivity.this.screen_height = activityRootView.getHeight();
                if (!MslSessionActivity.this.sessionRunning && MslSessionActivity.this.getIntent() != null) {
                    MslSessionActivity mslSessionActivity = MslSessionActivity.this;
                    mslSessionActivity.processIntent(mslSessionActivity.getIntent());
                    MslSessionActivity.this.sessionRunning = true;
                }
            }
        });
        View findViewById = findViewById(R.id.welcome_view);
        this.tempView = findViewById;
        findViewById.setVisibility(View.VISIBLE);
        this.uiHandler = new UIHandler();
        this.libFreeRDPBroadcastReceiver = new LibFreeRDPBroadcastReceiver();
        this.toggleMouseButtons = false;
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalApp.ACTION_EVENT_FREERDP);
        filter.addAction(LinuxInputMethod.ACTION_EVENT_KEYBOARD);
        registerReceiver(this.libFreeRDPBroadcastReceiver, filter, RECEIVER_EXPORTED);
        View decorView = getWindow().getDecorView();
        this.mDecor = decorView;
        decorView.setSystemUiVisibility(4098);
        //requestMyPermissions();
        HandlerThread thread = new HandlerThread("MslgRdpHandler");
        thread.start();
        thread.getLooper();
        Intent intent = new Intent(this, RunningStateService.class);
        startService(intent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestMyPermissions() {
        if (!Environment.isExternalStorageManager()) {
            createManagerDialogs();
            AlertDialog alertDialog = this.dlgManagerRequset;
            if (alertDialog != null) {
                this.uiHandler.sendMessage(Message.obtain(null, 5, alertDialog));
            }
        } else if (checkSelfPermission("android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.READ_MEDIA_VIDEO") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requestMyPermissions: 没有VIDEO权限");
            requestPermissions(new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO"}, 100);
        } else {
            this.isCheck = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (!Environment.isExternalStorageManager()) {
                if (this.dlgManagerRequset == null) {
                    createManagerDialogs();
                }
                this.uiHandler.sendMessage(Message.obtain(null, 5, this.dlgManagerRequset));
                return;
            }
            requestMyPermissions();
        }
    }

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Boolean sendDialog = false;
        Boolean sendDialogRefuse = false;
        if (requestCode == 100) {
            createDialogs();
            if (checkSelfPermission("android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.READ_MEDIA_VIDEO") != PackageManager.PERMISSION_GRANTED) {
                boolean rejectVideo = ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_VIDEO");
                Log.d(TAG, "onRequestPermissionsResult: 没有读VIDEO权限 ");
                if (rejectVideo) {
                    this.uiHandler.sendMessage(Message.obtain(null, 5, this.dlgPerssionRequset));
                    sendDialog = true;
                } else {
                    this.uiHandler.sendMessage(Message.obtain(null, 5, this.dlgPerssionRequsetRefuse));
                    sendDialogRefuse = true;
                }
            }
            if (checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                boolean rejectAudio = ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_AUDIO");
                Log.d(TAG, "onRequestPermissionsResult: 没有读AUDIO权限 ");
                if (rejectAudio) {
                    if (!sendDialog.booleanValue()) {
                        this.uiHandler.sendMessage(Message.obtain(null, 5, this.dlgPerssionRequset));
                        sendDialog = true;
                    }
                } else if (!sendDialogRefuse.booleanValue()) {
                    this.uiHandler.sendMessage(Message.obtain(null, 5, this.dlgPerssionRequsetRefuse));
                    sendDialogRefuse = true;
                }
            }
            boolean rejectAudio2 = sendDialog.booleanValue();
            if (!rejectAudio2 && !sendDialogRefuse.booleanValue()) {
                this.isCheck = true;
                UIHandler uIHandler = this.uiHandler;
                if (uIHandler != null) {
                    uIHandler.sendEmptyMessageDelayed(9, 100L);
                }
            }
        }
    }

    private void sendPath(boolean alive) {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String fileurl = intent.getStringExtra("MiRdpFileUrl");
        if (D) {
            Log.v(TAG, "sendPath fileurl =" + fileurl);
        }
        if (fileurl != null && !fileurl.isEmpty()) {
            Uri uri = Uri.parse(fileurl);
            String path = uri.getPath();
            if (path != null) {
                if (uri.getAuthority().equals("media")) {
                    path = getFilePathFromContentUri(this, uri);
                    if (path.contains("/storage/emulated/0/")) {
                        path = path.replace("/storage/emulated/0/", "/sdcard/");
                    }
                }
                String[] s = path.split("/");
                if (!s[1].equals("tablet")) {
                    path = path.replace(s[1], "tablet");
                }
            }
            Log.v(TAG, "sendPath path =" + path);
            String appType = path != null ? getAppType(path) : "/usr/bin/wps";
            Log.v(TAG, "appType = " + appType);
            SessionState sessionState = this.session;
            if (sessionState != null && sessionState.getRailChannelStatus()) {
                LibFreeRDP.openRemoteApp(this.session.getInstance(), appType, path);
                setIntent(null);
                return;
            }
            return;
        }
        SessionState sessionState2 = this.session;
        if (sessionState2 != null && sessionState2.getRailChannelStatus() && !alive) {
            Log.v(TAG, "Open defalut app");
            LibFreeRDP.openRemoteApp(this.session.getInstance(), "/usr/bin/wps", null);
            setIntent(null);
        }
    }

    public static ParcelFileDescriptor open(Context context, Uri uri, int mode) throws FileNotFoundException {
        String modeStr;
        ParcelFileDescriptor fileDescriptor = null;
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!TextUtils.equals("content", uri.getScheme()) && !TextUtils.equals("file", uri.getScheme())) {
            File srcFile = new File(uri.toString());
            fileDescriptor = ParcelFileDescriptor.open(srcFile, mode);
            return fileDescriptor;
        }
        switch (mode) {
            case 536870912:
                modeStr = "w";
                break;
            case 805306368:
                modeStr = "rw";
                break;
            default:
                modeStr = "r";
                break;
        }
        fileDescriptor = context.getContentResolver().openFileDescriptor(uri, modeStr);
        return fileDescriptor;
    }

    private Uri copyToAppCache(Uri uri) {
        String name;
        if (uri == null) {
            return null;
        }
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment == null) {
            name = "temp";
        } else {
            int nameStartPos = lastPathSegment.lastIndexOf("/") + 1;
            name = nameStartPos < 0 ? "temp" : lastPathSegment.substring(nameStartPos);
        }
        getExternalCacheDir();
        DocumentFile srcDocumentFile = DocumentFile.fromSingleUri(this, uri);
        File externalSaveDir = new File(FILE_PATH);
        if (!externalSaveDir.exists()) {
            externalSaveDir.mkdirs();
        }
        File externalSavePath = new File(externalSaveDir, name);
        if (!externalSavePath.exists()) {
            try {
                externalSavePath.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            DocumentFile dstDocumentFile = DocumentFile.fromFile(externalSavePath);
            copyDocument(getApplicationContext(), srcDocumentFile, dstDocumentFile);
            if (dstDocumentFile.length() > 0) {
                return dstDocumentFile.getUri();
            }
            dstDocumentFile.delete();
            return uri;
        }
        return Uri.fromFile(externalSavePath);
    }

    public static void copyDocument(Context context, DocumentFile srcFile, DocumentFile dstFile) {
        if (srcFile == null || dstFile == null) {
            return;
        }
        InputStream input = null;
        OutputStream output = null;
        try {
            try {
                try {
                    Uri srcUri = srcFile.getUri();
                    Uri dstUri = dstFile.getUri();
                    ParcelFileDescriptor srcFileDescriptor = open(context, srcUri, 268435456);
                    ParcelFileDescriptor dstFileDescriptor = open(context, dstUri, 805306368);
                    if (srcFileDescriptor != null && dstFileDescriptor != null) {
                        input = new BufferedInputStream(new FileInputStream(srcFileDescriptor.getFileDescriptor()));
                        output = new BufferedOutputStream(new FileOutputStream(dstFileDescriptor.getFileDescriptor()));
                        byte[] buffer = new byte[4096];
                        while (true) {
                            int length = input.read(buffer);
                            if (length <= 0) {
                                break;
                            }
                            output.write(buffer, 0, length);
                        }
                        output.flush();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (input != null && output != null) {
                        input.close();
                        output.close();
                    } else {
                        return;
                    }
                }
                if (input != null && output != null) {
                    input.close();
                    output.close();
                }
            } catch (Throwable th) {
                if (input != null && output != null) {
                    try {
                        input.close();
                        output.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
                throw th;
            }
        } catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    private static String getFilePathFromContentUri(Context context, Uri uri) {
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
                        data = getPathFromInputStreamUri(context, uri, fileName);
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:9:0x0020 -> B:25:0x0035). Please submit an issue!!! */
    public static String getPathFromInputStreamUri(Context context, Uri uri, String fileName) {
        InputStream inputStream = null;
        String filePath = null;

        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File file = createTemporalFileFrom(context, inputStream, fileName);
                filePath = file.getPath();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return filePath;
    }

    private static File createTemporalFileFrom(Context context, InputStream inputStream, String fileName) throws IOException {
        File targetFile = null;
        if (inputStream != null) {
            try {
                byte[] buffer = new byte[8192];
                targetFile = new File(FILE_PATH, fileName);
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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return targetFile;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        Log.v(TAG, "Session.onStart");
    }

    @Override // android.app.Activity
    protected void onRestart() {
        super.onRestart();
        Log.v(TAG, "Session.onRestart");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onNewIntent(Intent intent) {
        Log.v(TAG, "Session.onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        D = Log.isLoggable("XIAOMI_MSLGRDP", Log.VERBOSE);
        if (isAlive.booleanValue()) {
            sendPath(true);
        }
        Log.v(TAG, "Session.onResume");
        isAlive = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onPause() {
        super.onPause();
        Log.v(TAG, "Session.onPause");
        isExit = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        Log.v(TAG, "Session.onStop");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Session.onDestroy");
        Collection<SessionState> sessions = GlobalApp.getSessions();
        for (SessionState session : sessions) {
            LibFreeRDP.disconnect(session.getInstance());
        }
        unregisterReceiver(this.libFreeRDPBroadcastReceiver);
        GlobalApp.freeSession(this.session.getInstance());
        isExit = false;
        this.session = null;
        this.inputCount = 0;
        isAlive = false;
        this.isSuccess = false;
        LinuxInputMethod.mInputActivate = false;
        this.isOpenRequest = false;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        this.mDecor.setSystemUiVisibility(4098);
    }

    private String getFilePath(String url) {
        if (url.isEmpty()) {
            return "";
        }
        StringBuilder openPath = new StringBuilder();
        URI uri = URI.create(url);
        String path = uri.getPath();
        openPath.append(getAppType(path)).append(" ").append(path);
        Log.v(TAG, "openFile openPath  ");
        return openPath.toString();
    }

    private String getAppType(String path) {
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

    private String getOpenAppExten(String type) {
        if (type.contains(".doc") || type.contains(".doc") || type.contains(".docm") || type.contains(".rtf") || type.contains(".dot") || type.contains(".dotm") || type.contains(".dotx") || type.contains(".wps") || type.contains(".wpt") || type.contains(".wpso") || type.contains(".wpss")) {
            return "/usr/bin/wps";
        }
        if (type.contains(".ppt") || type.contains(".pptm") || type.contains(".pps") || type.contains(".pot") || type.contains(".potm") || type.contains(".potx") || type.contains(".pptx") || type.contains(".ppsx") || type.contains(".dps") || type.contains(".dpss") || type.contains(".dpso")) {
            return "/usr/bin/wpp";
        }
        return (type.contains(".xls") || type.contains(".xlsm") || type.contains(".xlt") || type.contains(".xltm") || type.contains(".xltx") || type.contains(".xlsx") || type.contains(".et") || type.contains(".ett") || type.contains(".ets") || type.contains(".eto")) ? "/usr/bin/et" : "/usr/bin/wps";
    }

    private String getOpenApp(String type) {
        String[] strArr;
        String[] strArr2;
        String[] strArr3;
        if (type.isEmpty()) {
            return "";
        }
        for (String name : TYPE_ET) {
            if (type.equals(name)) {
                return "/usr/bin/et";
            }
        }
        for (String name2 : TYPE_WPP) {
            if (type.equals(name2)) {
                return "/usr/bin/wpp";
            }
        }
        for (String name3 : TYPE_WPS) {
            if (type.equals(name3)) {
                return "/usr/bin/wps";
            }
        }
        return "/usr/bin/wps";
    }

    private void setProperty(String key, String value) {
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method setMethod = systemPropertiesClass.getDeclaredMethod("set", String.class, String.class);
            setMethod.setAccessible(true);
            setMethod.invoke(null, key, value);
        } catch (Exception e) {
            Log.e(TAG, "System property " + e);
            e.printStackTrace();
        }
    }

    private String getProperty(String key) {
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

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint("LogTagMismatch")
    public void processIntent(Intent intent) {
        Boolean isRootFs = Boolean.valueOf(intent.getBooleanExtra("StartFromMSLG", false));
        Log.v(TAG, "processIntent isRootFs =" + isRootFs);
        if (isRootFs.booleanValue()) {
            String link = System.getProperty("sys.mslg.localsocket");
            int id = 2;
            if (Log.isLoggable("sys.mslg.localsocket", Log.VERBOSE)) {
                Log.v(TAG, "sys.mslg.localsocket");
                id = 1;
            }
            if (link != null && link.equals("1")) {
                Log.v(TAG, "sys.mslg.localsocket =" + link);
                id = 1;
            }
            if (isSet.booleanValue()) {
                this.uiHandler.sendMessageDelayed(Message.obtain(null, 10, Integer.valueOf(id)), 1000L);
            } else {
                connect(id);
            }
            this.uiHandler.sendMessageDelayed(Message.obtain(null, 10, Integer.valueOf(id)), 10000L);
            return;
        }
        Toast.makeText(this, "启动失败", Toast.LENGTH_SHORT).show();
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connect(int id) {
        BookmarkBase bookmark = GlobalApp.getManualBookmarkGateway().findById(id);
        BookmarkBase.AdvancedSettings advancedSettings = bookmark.getAdvancedSettings();
        bookmark.setAdvancedSettings(advancedSettings);
        if (bookmark != null) {
            connect(bookmark);
        } else {
            closeSessionActivity(0);
        }
    }

    private void connect(BookmarkBase bookmark) {
        Log.d(TAG, "connect bookmark " + bookmark.getDomain());
        SessionState createSession = GlobalApp.createSession(bookmark, getApplicationContext());
        this.session = createSession;
        BookmarkBase.ScreenSettings screenSettings = createSession.getBookmark().getActiveScreenSettings();
        if (D) {
            Log.v(TAG, "Screen Resolution: " + screenSettings.getResolutionString());
        }
        if (screenSettings.isAutomatic()) {
            if ((getResources().getConfiguration().screenLayout & 15) >= 3) {
                screenSettings.setHeight(this.screen_height);
                screenSettings.setWidth(this.screen_width);
            } else {
                int screenMax = this.screen_width;
                int i = this.screen_height;
                if (screenMax <= i) {
                    screenMax = i;
                }
                screenSettings.setHeight(screenMax);
                screenSettings.setWidth((int) (screenMax * 1.6f));
            }
        }
        if (screenSettings.isFitScreen()) {
            screenSettings.setHeight(this.screen_height);
            screenSettings.setWidth(this.screen_width);
        }
        connectWithTitle(bookmark.getLabel());
    }

    private void connect(Uri openUri) {
        Log.d(TAG, " connect uri " + openUri.toString());
        this.session = GlobalApp.createSession(openUri, getApplicationContext());
        connectWithTitle(openUri.getAuthority());
    }

    private void connectWithTitle(String title) {
        Log.d(TAG, " connectWithTitle" + title);
        this.session.setUIEventListener(this);
        Thread thread = new Thread(new Runnable() { // from class: com.xiaomi.mslgrdp.presentation.MslSessionActivity.2
            @Override // java.lang.Runnable
            public void run() {
                MslSessionActivity.this.session.connect(MslSessionActivity.this.getApplicationContext());
            }
        });
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindSession() {
        Log.v(TAG, "bindSession called");
        this.session.setUIEventListener(this);
        this.mDecor.setSystemUiVisibility(4098);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeSessionActivity(int resultCode) {
        setResult(resultCode, getIntent());
        finish();
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnSettingsChanged(int width, int height, int bpp) {
        if (bpp > 16) {
            this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        }
        this.session.setSurface(new BitmapDrawable(this.bitmap));
        this.session.getBookmark();
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnGraphicsUpdate(int x, int y, int width, int height) {
        Log.v(TAG, "OnGraphicsUpdate: -----");
        this.uiHandler.sendEmptyMessage(1);
        if (this.uiHandler.hasMessages(10)) {
            this.uiHandler.removeMessages(10);
        }
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnGraphicsResize(int width, int height, int bpp) {
        if (bpp > 16) {
            this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        }
        this.session.setSurface(new BitmapDrawable(this.bitmap));
        this.uiHandler.sendEmptyMessage(6);
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public boolean OnAuthenticate(StringBuilder username, StringBuilder domain, StringBuilder password) {
        this.callbackDialogResult = false;
        username.setLength(0);
        domain.setLength(0);
        password.setLength(0);
        return this.callbackDialogResult;
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public boolean OnGatewayAuthenticate(StringBuilder username, StringBuilder domain, StringBuilder password) {
        this.callbackDialogResult = false;
        username.setLength(0);
        domain.setLength(0);
        password.setLength(0);
        return this.callbackDialogResult;
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public int OnVerifiyCertificate(String commonName, String subject, String issuer, String fingerprint, boolean mismatch) {
        this.callbackDialogResult = false;
        return 0;
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public int OnVerifyChangedCertificate(String commonName, String subject, String issuer, String fingerprint, String oldSubject, String oldIssuer, String oldFingerprint) {
        this.callbackDialogResult = false;
        return 0;
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnRemoteClipboardChanged(String data) {
        if (D) {
            Log.v(TAG, "OnRemoteClipboardChanged: " + data);
        }
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnRailChannelReady(boolean ready) {
        if (ready) {
            sendPath(false);
        }
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnMinimizeRequested(boolean minimized) {
        moveTaskToBack(true);
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnOpenwpsRequested(boolean openwps) {
        this.isOpenRequest = openwps;
        UIHandler uIHandler = this.uiHandler;
        if (uIHandler != null && openwps) {
            uIHandler.sendEmptyMessageDelayed(9, 100L);
        }
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha) {
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnUpdatePointerIcon(int width, int height, int hotSpotX, int hotSpotY) {
    }

    @Override // android.app.Activity
    public boolean onGenericMotionEvent(MotionEvent e) {
        switch (e.getAction()) {
            case 8:
                float vScroll = e.getAxisValue(9);
                if (vScroll < 0.0f) {
                    LibFreeRDP.sendCursorEvent(this.session.getInstance(), 0, 0, Mouse.getScrollEventMouse(this, false));
                }
                if (vScroll > 0.0f) {
                    LibFreeRDP.sendCursorEvent(this.session.getInstance(), 0, 0, Mouse.getScrollEventMouse(this, true));
                    break;
                }
                break;
        }
        super.onGenericMotionEvent(e);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public class UIHandler extends Handler {
        public static final int CLEAR_TEXT = 8;
        public static final int CONNECT_RDP = 10;
        public static final int DISPLAY_TOAST = 2;
        public static final int GRAPHICS_CHANGED = 6;
        public static final int REFRESH_SESSIONVIEW = 1;
        public static final int REFRESH_UI = 9;
        public static final int SEND_MOVE_EVENT = 4;
        public static final int SHOW_DIALOG = 5;

        UIHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 5:
                    ((AlertDialog) msg.obj).show();
                    AlertDialog dialog = (AlertDialog) msg.obj;
                    Button button = dialog.getButton(-1);
                    button.setTextColor(ContextCompat.getColor(MslSessionActivity.this, R.color.bule));
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = 900;
                    params.height = 450;
                    dialog.getWindow().setAttributes(params);
                    return;
                case 9:
                    if (!MslSessionActivity.this.isRefreshUi && MslSessionActivity.this.isCheck) {
                        if (MslSessionActivity.this.isOpenRequest || MslSessionActivity.this.isSuccess) {
                            if (MslSessionActivity.this.uiHandler.hasMessages(9)) {
                                MslSessionActivity.this.uiHandler.removeMessages(9);
                            }
                            MslSessionActivity.this.tempView.setVisibility(View.GONE);
                            MslSessionActivity.this.isRefreshUi = true;
                            return;
                        }
                        return;
                    }
                    return;
                case 10:
                    Log.v(MslSessionActivity.TAG, " CONNECT_RDP" + ((Integer) msg.obj).intValue());
                    MslSessionActivity.this.connect(((Integer) msg.obj).intValue());
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: classes5.dex */
    private class LibFreeRDPBroadcastReceiver extends BroadcastReceiver {
        private LibFreeRDPBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!action.equals(GlobalApp.ACTION_EVENT_FREERDP) || MslSessionActivity.this.session == null || MslSessionActivity.this.session.getInstance() != intent.getExtras().getLong(GlobalApp.EVENT_PARAM, -1L)) {
                return;
            }
            switch (intent.getExtras().getInt(GlobalApp.EVENT_TYPE, -1)) {
                case 1:
                    OnConnectionSuccess(context);
                    return;
                case 2:
                    OnConnectionFailure(context);
                    return;
                case 3:
                    OnDisconnected(context);
                    return;
                default:
                    return;
            }
        }

        private void OnConnectionSuccess(Context context) {
            Log.v(MslSessionActivity.TAG, "OnConnectionSuccess");
            MslSessionActivity.this.bindSession();
            if (MslSessionActivity.this.uiHandler != null) {
                MslSessionActivity.this.isSuccess = true;
                MslSessionActivity.this.uiHandler.sendEmptyMessageDelayed(9, 5000L);
            }
            MslSessionActivity.this.session.getBookmark();
        }

        private void OnConnectionFailure(Context context) {
            Log.v(MslSessionActivity.TAG, "OnConnectionFailure");
            MslSessionActivity.this.uiHandler.removeMessages(4);
            MslSessionActivity.this.closeSessionActivity(0);
        }

        private void OnDisconnected(Context context) {
            Log.v(MslSessionActivity.TAG, "OnDisconnected");
            MslSessionActivity.this.uiHandler.removeMessages(4);
            MslSessionActivity.this.session.setUIEventListener(null);
            MslSessionActivity.this.closeSessionActivity(-1);
        }
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == 0 && isOutOfBounds(this, event)) {
            backToHome();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean isOutOfBounds(Activity context, MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        View decorView = context.getWindow().getDecorView();
        return x < (-slop) || y < (-slop) || x > decorView.getWidth() + slop || y > decorView.getHeight() + slop;
    }

    private void backToHome() {
        Log.v(TAG, "backToHome");
        Intent home = new Intent("android.intent.action.MAIN");
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        home.addCategory("android.intent.category.HOME");
        startActivity(home);
    }

    private void setDisplay() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.height = (int) (dm.heightPixels * 0.8d);
        p.width = (int) (dm.widthPixels * 0.8d);
        getWindow().setAttributes(p);
    }

    private void createManagerDialogs() {
        this.dlgManagerRequset = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_manager_message).setPositiveButton(R.string.allow_perssion, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.MslSessionActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
                    intent.setData(Uri.parse("package:" + MslSessionActivity.this.getPackageName()));
                    MslSessionActivity.this.startActivityForResult(intent, 100);
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.MslSessionActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    MslSessionActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
    }

    private void createDialogs() {
        this.dlgPerssionRequsetRefuse = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_message).setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.MslSessionActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    Uri uri = Uri.fromParts("package", MslSessionActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    MslSessionActivity.this.startActivity(intent);
                    MslSessionActivity.this.finish();
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.MslSessionActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    MslSessionActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
        this.dlgPerssionRequset = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_message).setPositiveButton(R.string.retry_perssion, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.MslSessionActivity.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    MslSessionActivity.this.requestMyPermissions();
                    dialog.notify();
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.MslSessionActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    MslSessionActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
    }
}
