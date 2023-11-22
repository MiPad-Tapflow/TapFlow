package com.xiaomi.mslgrdp.presentation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.freerdp.freerdpcore.services.RunningStateService;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.xiaomi.mslgrdp.application.GlobalApp;
import com.xiaomi.mslgrdp.application.SessionState;
import com.xiaomi.mslgrdp.domain.BookmarkBase;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.KeyboardMapperManager;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.multwindow.base.BaseActivity;
import com.xiaomi.mslgrdp.presentation.ScrollView2D;
import com.xiaomi.mslgrdp.presentation.SessionView;
import com.xiaomi.mslgrdp.utils.ClipboardManagerProxy;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.KeyboardMapper;
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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.ljlVink.Tapflow.R;


/* loaded from: classes5.dex */
public class SessionActivity extends BaseActivity implements LibFreeRDP.UIEventListener, ScrollView2D.ScrollView2DListener, SessionView.SessionViewListener, ClipboardManagerProxy.OnClipboardChangedListener {
    private static final String ET = "/usr/bin/et";
    private static final String FILE_PATH = "/sdcard/Download/Cache/WpsOffice";
    private static final int FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS = Integer.MIN_VALUE;
    private static final int FLAG_RECEIVER_INCLUDE_BACKGROUND = 16777216;
    private static final int MAX_DISCARDED_MOVE_EVENTS = 3;
    public static final String PARAM_CONNECTION_REFERENCE = "conRef";
    public static final String PARAM_INSTANCE = "instance";
    private static final int SCROLLING_DISTANCE = 20;
    private static final int SCROLLING_TIMEOUT = 50;
    private static final int SEND_MOVE_EVENT_TIMEOUT = 150;
    public static final int SYSTEM_UI_FLAG_LIGHT_STATUS_BAR = 8192;
    private static final String TAG = "Mi.SessionActivity";
    private static final String WPP = "/usr/bin/wpp";
    private static final String WPS = "/usr/bin/wps";
    private static final float ZOOMING_STEP = 0.5f;
    private View activityRootView;
    private Bitmap bitmap;
    private Bitmap bitmap_pointer;
    private boolean callbackDialogResult;
    private AlertDialog dlgManagerRequset;
    private AlertDialog dlgPerssionRequset;
    private AlertDialog dlgPerssionRequsetRefuse;
    private EditText editText;
    private String fileurl;
    private ImageView imageView;
    private KeyboardMapper keyboardMapper;
    private LibFreeRDPBroadcastReceiver libFreeRDPBroadcastReceiver;
    private ClipboardManagerProxy mClipboardManager;
    View mDecor;
    private PointerIcon pointerIcon;
    private int screen_height;
    private int screen_width;
    private ScrollView2D scrollView;
    private SessionState session;
    private SessionView sessionView;
    private View tempView;
    private UIHandler uiHandler;
    public static Boolean isExit = false;
    public static Boolean isAlive = false;
    public static boolean D = false;
    private static final String[] TYPE_ET = {"application/vnd.ms-excel", "application/msexcel", "application/x-msexcel", "application/vnd.ms-excel.sheet.macroenabled.12", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel.template.macroenabled.12", "application/vnd.openxmlformats-officedocument.spreadsheetml.template"};
    private static final String[] TYPE_WPP = {"application/vnd.ms-powerpoint", "application/powerpoint", "application/mspowerpoint", "application/x-mspowerpoint", "application/vnd.ms-powerpoint.presentation.macroenabled.12", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "application/vnd.openxmlformats-officedocument.presentationml.slideshow", "application/vnd.openxmlformats-officedocument.presentationml.template"};
    private static final String[] TYPE_WPS = {"application/msword", "application/vnd.ms-word", "application/x-msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.ms-word.document.macroenabled.12", "application/msword-template", "application/vnd.ms-word.template.macroenabled.12", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/vnd.openxmlformats-officedocument.wordprocessingml.template", "application/x-ole-storage", "application/x-ole-storage"};
    public static Boolean isSet = false;
    private boolean isRefreshUi = false;
    private boolean sessionRunning = false;
    private boolean toggleMouseButtons = false;
    private boolean isCheck = false;
    private boolean isOpenRequest = false;
    private boolean isSuccess = false;
    private boolean sysKeyboardVisible = false;
    private boolean extKeyboardVisible = false;
    private int discardedMoveEvents = 0;
    private MslgRdpHandler mMslgRdpHandler = null;
    public String MSLGKEY = "sys.mslg.mounted";
    private ExecutorService executors = Executors.newFixedThreadPool(5);
    private int scrollCount = 0;

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

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!getProperty(this.MSLGKEY).equals("1")) {
            isSet = true;
            Log.d(TAG, "set SystemProperty");
            setProperty(this.MSLGKEY, "1");
        }
        setProperty("sys.mslg.isalive", "true");
        setContentView(R.layout.session);
        setFinishOnTouchOutside(false);
        Log.v(TAG, "Session.onCreate");
        View findViewById = findViewById(R.id.session_root_view);
        this.activityRootView = findViewById;
        findViewById.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: com.xiaomi.mslgrdp.presentation.SessionActivity.1
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                SessionActivity sessionActivity = SessionActivity.this;
                sessionActivity.screen_width = sessionActivity.activityRootView.getWidth();
                SessionActivity sessionActivity2 = SessionActivity.this;
                sessionActivity2.screen_height = sessionActivity2.activityRootView.getHeight();
                Constances.SCREEN_WIDTH = SessionActivity.this.screen_width;
                Constances.SCREEN_HEIGHT = SessionActivity.this.screen_height;
                if (!SessionActivity.this.sessionRunning && SessionActivity.this.getIntent() != null) {
                    SessionActivity sessionActivity3 = SessionActivity.this;
                    sessionActivity3.processIntent(sessionActivity3.getIntent());
                    SessionActivity.this.sessionRunning = true;
                }
            }
        });
        this.sessionView = (SessionView) findViewById(R.id.sessionView);
        View findViewById2 = findViewById(R.id.session_temp_view);
        this.tempView = findViewById2;
        findViewById2.setVisibility(View.VISIBLE);
        ImageView imageView = (ImageView) findViewById(R.id.image);
        this.imageView = imageView;
        imageView.setVisibility(View.VISIBLE);
        this.editText = (EditText) findViewById(R.id.editTextInput);
        this.sessionView.setContext(this);
        this.sessionView.setScaleGestureDetector(new ScaleGestureDetector(this, new PinchZoomListener()));
        this.sessionView.setSessionViewListener(this);
        this.sessionView.requestFocus();
        this.keyboardMapper = KeyboardMapperManager.getManager().init(this);
        this.editText.addTextChangedListener(KeyboardMapperManager.getManager().getEditWatcher());
        KeyboardMapperManager.getManager().setEditText(this.editText);
        ScrollView2D scrollView2D = (ScrollView2D) findViewById(R.id.sessionScrollView);
        this.scrollView = scrollView2D;
        scrollView2D.setScrollViewListener(this);
        this.uiHandler = new UIHandler();
        this.libFreeRDPBroadcastReceiver = new LibFreeRDPBroadcastReceiver();
        this.toggleMouseButtons = false;
        IntentFilter filter = new IntentFilter();
        filter.addAction(GlobalApp.ACTION_EVENT_FREERDP);
        filter.addAction(LinuxInputMethod.ACTION_EVENT_KEYBOARD);
        registerReceiver(this.libFreeRDPBroadcastReceiver, filter, Context.RECEIVER_EXPORTED);
        ClipboardManagerProxy clipboardManager = ClipboardManagerProxy.getClipboardManager(this);
        this.mClipboardManager = clipboardManager;
        clipboardManager.addClipboardChangedListener(this);
        this.mDecor = getWindow().getDecorView();
        requestMyPermissions();
        HandlerThread thread = new HandlerThread("MslgRdpHandler");
        thread.start();
        Looper looper = thread.getLooper();
        mMslgRdpHandler = new MslgRdpHandler(looper);
        Intent intent = new Intent(getApplicationContext(), RunningStateService.class);
        startService(intent);

    }

    /* loaded from: classes5.dex */
    public final class MslgRdpHandler extends Handler {
        public static final int MSG_HOVER_MOVE = 100;

        public MslgRdpHandler(Looper looper) {
            super(looper);
            Log.i(SessionActivity.TAG, "MslgRdpHandler ");
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:
                    MotionEvent e = (MotionEvent) msg.obj;
                    try {
                        if (e.getPointerCount() > 0 && e.getToolType(e.getActionIndex()) != MotionEvent.TOOL_TYPE_STYLUS && !e.getDevice().getName().contains("Pen")) {
                            Point p = SessionActivity.this.mapScreenCoordToSessionCoord((int) e.getX(), (int) e.getY());
                            LibFreeRDP.sendCursorEvent(SessionActivity.this.session.getInstance(), p.x, p.y, Mouse.getMoveEvent());
                            return;
                        }
                        return;
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        return;
                    }
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestMyPermissions() {
        this.isCheck = true;
        if (!Environment.isExternalStorageManager()) {
            /*createManagerDialogs();
            AlertDialog alertDialog = this.dlgManagerRequset;
            if (alertDialog != null) {
                this.uiHandler.sendMessage(Message.obtain(null, 5, alertDialog));
            }*/
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
                /*if (this.dlgManagerRequset == null) {
                    createManagerDialogs();
                }
                this.uiHandler.sendMessage(Message.obtain(null, 5, this.dlgManagerRequset));
                return;*/
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

    public static void sendPath(boolean alive, String url, Context context, SessionState session) {
        if (D) {
            Log.v(TAG, "sendPath fileurl =" + url);
        }
        String appType = WPS;
        if (url != null && !url.isEmpty()) {
            Uri uri = Uri.parse(url);
            String path = uri.getPath();
            if (path != null) {
                if (uri.getAuthority().equals("media")) {
                    path = getFilePathFromContentUri(context, uri);
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
            if (path != null) {
                appType = getAppType(path);
            }
            Log.v(TAG, "appType = " + appType);
            if (session != null && session.getRailChannelStatus()) {
                LibFreeRDP.openRemoteApp(session.getInstance(), appType, path);
            }
        } else if (session != null && session.getRailChannelStatus() && !alive) {
            Log.v(TAG, "Open defalut app");
                LibFreeRDP.openRemoteApp(session.getInstance(), WPS, null);
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
    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        D = Log.isLoggable("XIAOMI_MSLGRDP", Log.VERBOSE);
        if (this.editText.getVisibility() == View.VISIBLE && LinuxInputMethod.mInputActivate) {
            this.editText.setFocusable(true);
            this.editText.requestFocus();
        }
        Intent intent = getIntent();
        if (intent != null) {
            this.fileurl = intent.getStringExtra("MiRdpFileUrl");
            if (isAlive.booleanValue()) {
                sendPath(true, this.fileurl, this, this.session);
                setIntent(null);
            }
        }
        Log.v(TAG, "Session.onResume");
        isAlive = true;
        Bitmap welcom = BitmapFactory.decodeResource(getResources(), R.drawable.welcom);
        Bitmap.createBitmap(welcom.getWidth(), welcom.getHeight(), welcom.getConfig());
        Resources resources = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrow);
        PointerIcon pointerIcon = PointerIcon.create(bitmap, 1.0f, 1.0f);
        this.activityRootView.setPointerIcon(pointerIcon);
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
    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Session.onDestroy");
        unregisterReceiver(this.libFreeRDPBroadcastReceiver);
        this.uiHandler.removeCallbacksAndMessages(null);
        this.mClipboardManager.removeClipboardboardChangedListener(this);
        setProperty("sys.mslg.isalive", "false");
        MultiWindowManager.getManager().destroy();
        isExit = false;
        this.session = null;
        isAlive = false;
        this.isSuccess = false;
        LinuxInputMethod.mInputActivate = false;
        this.isOpenRequest = false;
        MslgRdpHandler mslgRdpHandler = this.mMslgRdpHandler;
        if (mslgRdpHandler != null) {
            mslgRdpHandler.removeCallbacksAndMessages(null);
            Looper looper = this.mMslgRdpHandler.getLooper();
            if (looper != null) {
                Log.i(TAG, "Quit looper");
                looper.quit();
            }
            this.mMslgRdpHandler = null;
        }
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
        if (type.contains(".doc") || type.contains(".doc") || type.contains(".docm") || type.contains(".rtf") || type.contains(".dot") || type.contains(".dotm") || type.contains(".dotx") || type.contains(".wps") || type.contains(".wpt") || type.contains(".wpso") || type.contains(".wpss")) {
            return WPS;
        }
        if (type.contains(".ppt") || type.contains(".pptm") || type.contains(".pps") || type.contains(".pot") || type.contains(".potm") || type.contains(".potx") || type.contains(".pptx") || type.contains(".ppsx") || type.contains(".dps") || type.contains(".dpss") || type.contains(".dpso")) {
            return WPP;
        }
        return (type.contains(".xls") || type.contains(".xlsm") || type.contains(".xlt") || type.contains(".xltm") || type.contains(".xltx") || type.contains(".xlsx") || type.contains(".et") || type.contains(".ett") || type.contains(".ets") || type.contains(".eto")) ? ET : WPS;
    }

    private static String getOpenApp(String type) {
        String[] strArr;
        String[] strArr2;
        String[] strArr3;
        if (type.isEmpty()) {
            return "";
        }
        for (String name : TYPE_ET) {
            if (type.equals(name)) {
                return ET;
            }
        }
        for (String name2 : TYPE_WPP) {
            if (type.equals(name2)) {
                return WPP;
            }
        }
        for (String name3 : TYPE_WPS) {
            if (type.equals(name3)) {
                return WPS;
            }
        }
        return WPS;
    }

    private void setProperty(String key, String value) {
        /*try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method setMethod = systemPropertiesClass.getDeclaredMethod("set", String.class, String.class);
            setMethod.setAccessible(true);
            setMethod.invoke(null, key, value);
        } catch (Exception e) {
            Log.e(TAG, "System property " + e);
            e.printStackTrace();
        }*/
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
        Boolean isRootFs = true;
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
        Thread thread = new Thread(new Runnable() { // from class: com.xiaomi.mslgrdp.presentation.SessionActivity.2
            @Override // java.lang.Runnable
            public void run() {
                SessionActivity.this.session.connect(SessionActivity.this.getApplicationContext());
            }
        });
        thread.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void bindSession() {
        Log.v(TAG, "bindSession called");
        this.session.setUIEventListener(this);
        this.sessionView.onSurfaceChange(this.session);
        this.scrollView.requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftInput() {
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mgr.isActive()) {
            mgr.toggleSoftInput(2, 0);
        } else {
            mgr.toggleSoftInput(0, 2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeSessionActivity(int resultCode) {
        setResult(resultCode, getIntent());
        finish();
    }

    private void sendDelayedMoveEvent(int x, int y) {
        if (this.uiHandler.hasMessages(4)) {
            this.uiHandler.removeMessages(4);
            this.discardedMoveEvents++;
        } else {
            this.discardedMoveEvents = 0;
        }
        if (this.discardedMoveEvents > 3) {
            LibFreeRDP.sendCursorEvent(this.session.getInstance(), x, y, Mouse.getMoveEvent());
        } else {
            this.uiHandler.sendMessageDelayed(Message.obtain(null, 4, x, y), 150L);
        }
    }

    private void cancelDelayedMoveEvent() {
        this.uiHandler.removeMessages(4);
    }

    public void onBackKeyPressed() {
        Log.d(TAG, "onBackPressed ");
        if (isExit.booleanValue()) {
            isExit = false;
            moveTaskToBack(true);
            return;
        }
        isExit = true;
        Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).show();
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (event.isMetaPressed()) {
            return true;
        }
        if (keycode == 4) {
            onBackKeyPressed();
        }
        return this.keyboardMapper.processAndroidKeyEvent(event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keycode, KeyEvent event) {
        return this.keyboardMapper.processAndroidKeyEvent(event);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return this.keyboardMapper.processAndroidKeyEvent(event);
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnSettingsChanged(int width, int height, int bpp) {
        Log.v(TAG, "OnSettingsChanged --- ");
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
    }

    private void saveBitmap(Bitmap bitmap) throws IOException {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wpstemp/" + System.currentTimeMillis() + ".PNG");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnGraphicsUpdateMultiWindow(long inst, int windowId, int x, int y, int width, int height, boolean isPopWindow, boolean isAlpha) {
        if (windowId == 1) {
            return;
        }
        MslSurfaceInfo surfaceInfo = new MslSurfaceInfo(inst, windowId, x, y, width, height, isPopWindow, isAlpha);
        boolean result = LibFreeRDP.updateGraphics(inst, this.bitmap, windowId);
        if (!result) {
            Log.v(TAG, "bitmap fill failed");
            return;
        }
        Log.v(TAG, "OnGraphicsUpdateMultiWindow --- windowid = " + windowId);
        if (windowId == 2) {
            MultiWindowManager.getManager().setMainWindowId(windowId, surfaceInfo);
            if (!MultiWindowManager.getManager().hasSurface(windowId)) {
                MultiWindowManager.getManager().addSurface(windowId, this);
            }
        }
        this.sessionView.addInvalidRegion(new Rect(x, y, x + width, y + height));
        this.uiHandler.sendEmptyMessage(1);
        if (this.uiHandler.hasMessages(10)) {
            this.uiHandler.removeMessages(10);
        }
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnGraphicsResize(int width, int height, int bpp) {
        Log.v(TAG, "OnGraphicsResize --- ");
        if (bpp > 16) {
            this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        } else {
            this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        }
        this.session.setSurface(new BitmapDrawable(this.bitmap));
        this.uiHandler.sendEmptyMessage(6);
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnUpdatePointerIcon(int width, int height, int hotSpotX, int hotSpotY) {
        PointerIcon pointerIcon;
        PointerIcon pointerIcon2;
        this.bitmap_pointer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        LibFreeRDP.updatePointerIcon(this.session.getInstance(), this.bitmap_pointer);
        Bitmap move_mouse_pixel = MultiWindowManager.getManager().move_mouse_pixel(this.bitmap_pointer, hotSpotX, hotSpotY);
        this.bitmap_pointer = move_mouse_pixel;
        if (width < 64 && height < 64) {
            Resources resources = getResources();
            if (width != 9 || height != 15) {
                if (width == 15 && height == 9) {
                    Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrows_h);
                    pointerIcon2 = PointerIcon.create(bitmap, 72.0f, 72.0f);
                } else {
                    Bitmap bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.arrow);
                    pointerIcon2 = PointerIcon.create(MultiWindowManager.getManager().move_mouse_pixel(bitmap2, 24, 16), 40.0f, 40.0f);
                }
            } else {
                Bitmap bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.arrows_v);
                pointerIcon2 = PointerIcon.create(bitmap3, 72.0f, 72.0f);
            }
            this.uiHandler.sendMessage(Message.obtain(null, 11, pointerIcon2));
            return;
        }
        if (hotSpotX == 18 && hotSpotY == 18) {
            pointerIcon = PointerIcon.create(move_mouse_pixel, 40.0f, 40.0f);
        } else {
            pointerIcon = PointerIcon.create(move_mouse_pixel, 32.0f, 32.0f);
        }
        this.uiHandler.sendMessage(Message.obtain(null, 11, pointerIcon));
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
        UIHandler uIHandler;
        if (D) {
            Log.v(TAG, "OnRemoteClipboardChanged: " + data);
        }
        boolean show_toast = this.mClipboardManager.setClipboardData(data);
        if (show_toast && (uIHandler = this.uiHandler) != null) {
            uIHandler.sendMessage(Message.obtain(null, 2, getString(R.string.text_exceeds_max_limit)));
        }
    }

    @Override // com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
    public void OnRailChannelReady(boolean ready) {
        if (ready) {
            sendPath(false, this.fileurl, this, this.session);
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

    @Override // com.xiaomi.mslgrdp.presentation.ScrollView2D.ScrollView2DListener
    public void onScrollChanged(ScrollView2D scrollView, int x, int y, int oldx, int oldy) {
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewBeginTouch() {
        this.scrollView.setScrollEnabled(false);
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewEndTouch() {
        this.scrollView.setScrollEnabled(true);
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewLeftTouch(int x, int y, boolean down) {
        if (!down) {
            cancelDelayedMoveEvent();
        }
        LibFreeRDP.sendCursorEvent(this.session.getInstance(), x, y, this.toggleMouseButtons ? Mouse.getRightButtonEvent(this, down) : Mouse.getLeftButtonEvent(this, down));
        if (!down) {
            this.toggleMouseButtons = false;
        }
        if (this.editText.getVisibility() == View.VISIBLE && LinuxInputMethod.mInputActivate) {
            this.editText.setFocusable(true);
            this.editText.requestFocus();
        }
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewRightTouch(int x, int y, boolean down) {
        if (!down) {
            this.toggleMouseButtons = !this.toggleMouseButtons;
        }
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewMove(int x, int y) {
        sendDelayedMoveEvent(x, y);
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewScroll(boolean down, int touch_x, int touch_y, float y) {
        LibFreeRDP.sendCursorEvent(this.session.getInstance(), touch_x, touch_y, Mouse.getScrollEvent(this, down, y));
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewHScroll(boolean down, int touch_x, int touch_y, float x) {
        LibFreeRDP.sendCursorEvent(this.session.getInstance(), touch_x, touch_y, Mouse.getScrollEventH(this, down, x));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Point mapScreenCoordToSessionCoord(int x, int y) {
        int mappedX = (int) ((this.scrollView.getScrollX() + x) / this.sessionView.getZoom());
        int mappedY = (int) ((this.scrollView.getScrollY() + y) / this.sessionView.getZoom());
        Bitmap bitmap = this.bitmap;
        if (bitmap != null) {
            if (mappedX > bitmap.getWidth()) {
                mappedX = this.bitmap.getWidth();
            }
            if (mappedY > this.bitmap.getHeight()) {
                mappedY = this.bitmap.getHeight();
            }
        }
        return new Point(mappedX, mappedY);
    }

    @Override // android.app.Activity
    public boolean onGenericMotionEvent(MotionEvent e) {
        switch (e.getAction()) {
            case 7:
                MotionEvent myCopy = MotionEvent.obtain(e);
                this.uiHandler.sendMessage(Message.obtain(null, 11, this.pointerIcon));
                MslgRdpHandler mslgRdpHandler = this.mMslgRdpHandler;
                if (mslgRdpHandler != null) {
                    mslgRdpHandler.obtainMessage(100, myCopy).sendToTarget();
                    break;
                }
                break;
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

    @Override // com.xiaomi.mslgrdp.utils.ClipboardManagerProxy.OnClipboardChangedListener
    public void onClipboardChanged(String data) {
        if (D) {
            Log.v(TAG, "onClipboardChanged: " + data);
        }
        LibFreeRDP.sendClipboardData(this.session.getInstance(), data);
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
        public static final int UPDATE_MOUSE = 11;

        UIHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SessionActivity.D = Log.isLoggable("XIAOMI_MSLGRDP", Log.VERBOSE);
                    SessionActivity.this.sessionView.invalidateRegion();
                    return;
                case 2:
                    Toast errorToast = Toast.makeText(SessionActivity.this.getApplicationContext(), msg.obj.toString(), Toast.LENGTH_LONG);
                    errorToast.show();
                    return;
                case 3:
                case 7:
                default:
                    return;
                case 4:
                    LibFreeRDP.sendCursorEvent(SessionActivity.this.session.getInstance(), msg.arg1, msg.arg2, Mouse.getMoveEvent());
                    return;
                case 5:
                   /* ((AlertDialog) msg.obj).show();
                    AlertDialog dialog = (AlertDialog) msg.obj;
                    Button button = dialog.getButton(-1);
                    button.setTextColor(ContextCompat.getColor(SessionActivity.this, R.color.bule));
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    params.width = 900;
                    params.height = 450;
                    dialog.getWindow().setAttributes(params);*/
                    return;
                case 6:
                    SessionActivity.this.sessionView.onSurfaceChange(SessionActivity.this.session);
                    SessionActivity.this.scrollView.requestLayout();
                    return;
                case 8:
                    SessionActivity.this.editText.setText("");
                    SessionActivity.this.editText.addTextChangedListener(KeyboardMapperManager.getManager().getEditWatcher());
                    return;
                case 9:
                    Log.v(SessionActivity.TAG, " REFRESH_UI isRefreshUi = " + SessionActivity.this.isRefreshUi + " isCheck = " + SessionActivity.this.isCheck + " isOpenRequest = " + SessionActivity.this.isOpenRequest + " isSuccess = " + SessionActivity.this.isSuccess);
                    if (!SessionActivity.this.isRefreshUi && SessionActivity.this.isCheck) {
                        if (SessionActivity.this.isOpenRequest || SessionActivity.this.isSuccess) {
                            if (SessionActivity.this.uiHandler.hasMessages(9)) {
                                SessionActivity.this.uiHandler.removeMessages(9);
                            }
                            SessionActivity.this.tempView.setVisibility(View.GONE);
                            SessionActivity.this.sessionView.setVisibility(View.VISIBLE);
                            Log.v(SessionActivity.TAG, " REFRESH_UI sessionView.setVisibility(View.VISIBLE)");
                            SessionActivity.this.isRefreshUi = true;
                            return;
                        }
                        return;
                    }
                    return;
                case 10:
                    Log.v(SessionActivity.TAG, " CONNECT_RDP" + ((Integer) msg.obj).intValue());
                    SessionActivity.this.connect(((Integer) msg.obj).intValue());
                    return;
                case 11:
                    Log.v(SessionActivity.TAG, " UPDATE_MOUSE");
                    SessionActivity.this.pointerIcon = (PointerIcon) msg.obj;
                    SessionActivity.this.activityRootView.setPointerIcon(SessionActivity.this.pointerIcon);
                    return;
            }
        }
    }

    /* loaded from: classes5.dex */
    private class PinchZoomListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float scaleFactor;

        private PinchZoomListener() {
            this.scaleFactor = 1.0f;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            SessionActivity.this.scrollView.setScrollEnabled(false);
            return true;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = this.scaleFactor * detector.getScaleFactor();
            this.scaleFactor = scaleFactor;
            this.scaleFactor = Math.max(1.0f, Math.min(scaleFactor, 3.0f));
            if (!SessionActivity.this.sessionView.isAtMinZoom() && !SessionActivity.this.sessionView.isAtMaxZoom()) {
                float transOriginX = SessionActivity.this.scrollView.getScrollX() * detector.getScaleFactor();
                float transOriginY = SessionActivity.this.scrollView.getScrollY() * detector.getScaleFactor();
                float transCenterX = (SessionActivity.this.scrollView.getScrollX() + detector.getFocusX()) * detector.getScaleFactor();
                float transCenterY = (SessionActivity.this.scrollView.getScrollY() + detector.getFocusY()) * detector.getScaleFactor();
                SessionActivity.this.scrollView.scrollBy((int) ((transCenterX - transOriginX) - detector.getFocusX()), (int) ((transCenterY - transOriginY) - detector.getFocusY()));
                return true;
            }
            return true;
        }

        @Override // android.view.ScaleGestureDetector.SimpleOnScaleGestureListener, android.view.ScaleGestureDetector.OnScaleGestureListener
        public void onScaleEnd(ScaleGestureDetector de) {
            SessionActivity.this.scrollView.setScrollEnabled(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isInputMethodShowing() {
        int screenHeight = getWindow().getDecorView().getHeight();
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return (screenHeight - rect.bottom) - getSoftButtonsBarHeight() > 0;
    }

    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        }
        return 0;
    }

    /* loaded from: classes5.dex */
    private class LibFreeRDPBroadcastReceiver extends BroadcastReceiver {
        private LibFreeRDPBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(GlobalApp.ACTION_EVENT_FREERDP)) {
                if (SessionActivity.this.session == null || SessionActivity.this.session.getInstance() != intent.getExtras().getLong(GlobalApp.EVENT_PARAM, -1L)) {
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
            } else if (action.equals(LinuxInputMethod.ACTION_EVENT_KEYBOARD)) {
                SessionActivity.this.getResources().getConfiguration();
                if (LinuxInputMethod.mInputActivate) {
                    SessionActivity.this.editText.setVisibility(View.VISIBLE);
                    SessionActivity.this.editText.setFocusable(true);
                    SessionActivity.this.editText.requestFocus();
                    return;
                }
                SessionActivity.this.sessionView.setFocusable(true);
                SessionActivity.this.sessionView.requestFocus();
                SessionActivity.this.editText.setVisibility(View.GONE);
                if (SessionActivity.this.isInputMethodShowing()) {
                    SessionActivity.this.hideSoftInput();
                }
            }
        }

        private void OnConnectionSuccess(Context context) {
            Log.v(SessionActivity.TAG, "OnConnectionSuccess");
            SessionActivity.this.bindSession();
            if (SessionActivity.this.uiHandler != null) {
                SessionActivity.this.isSuccess = true;
                SessionActivity.this.uiHandler.sendEmptyMessageDelayed(9, 5000L);
            }
            SessionActivity.this.session.getBookmark();
        }

        private void OnConnectionFailure(Context context) {
            Log.v(SessionActivity.TAG, "OnConnectionFailure");
            SessionActivity.this.uiHandler.removeMessages(4);
            SessionActivity.this.closeSessionActivity(0);
        }

        private void OnDisconnected(Context context) {
            Log.v(SessionActivity.TAG, "OnDisconnected");
            SessionActivity.this.uiHandler.removeMessages(4);
            SessionActivity.this.session.setUIEventListener(null);
            SessionActivity.this.closeSessionActivity(-1);
        }
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
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
        this.dlgManagerRequset = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_manager_message).setPositiveButton(R.string.allow_perssion, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.SessionActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
                    intent.setData(Uri.parse("package:" + SessionActivity.this.getPackageName()));
                    SessionActivity.this.startActivityForResult(intent, 100);
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.SessionActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    SessionActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
    }

    private void createDialogs() {
        this.dlgPerssionRequsetRefuse = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_message).setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.SessionActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    Uri uri = Uri.fromParts("package", SessionActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    SessionActivity.this.startActivity(intent);
                    SessionActivity.this.finish();
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.SessionActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    SessionActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
        this.dlgPerssionRequset = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_message).setPositiveButton(R.string.retry_perssion, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.SessionActivity.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    SessionActivity.this.requestMyPermissions();
                    dialog.notify();
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.presentation.SessionActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    SessionActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
    }
}