package com.xiaomi.mslgrdp.multwindow.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import cn.ljlVink.Tapflow.R;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.KeyboardMapperManager;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.multwindow.SessionState;
import com.xiaomi.mslgrdp.presentation.SessionView;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.KeyboardMapper;
import com.xiaomi.mslgrdp.utils.Mouse;
import com.xiaomi.mslgrdp.utils.MslgLogger;
import com.xiaomi.mslgrdp.utils.Utils;
import com.xiaomi.mslgrdp.views.MslDragLayout;
import com.xiaomi.mslgrdp.views.MslSurfaceView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes6.dex */
public abstract class BaseActivity extends AppCompatActivity implements ISurface {
    protected MslDragLayout container_layout;
    private AlertDialog dlgManagerRequset;
    private AlertDialog dlgPerssionRequset;
    private AlertDialog dlgPerssionRequsetRefuse;
    protected EditText editText;
    protected MslSurfaceView iv_content;
    private KeyboardMapper keyboardMapper;
    public Bitmap mBitmap;
    protected SessionView sessionView;
    protected MslSurfaceInfo surfaceInfo;
    public int windowId = -1;
    private InnerHandler innerHandler = new InnerHandler();
    protected boolean permissionGranted = false;
    protected boolean isConnectActivity = false;
    private boolean closeWindowByNative = false;
    protected boolean isImActivate = false;
    private LinuxInputMethod.IMActivateListener imActivateListener = new LinuxInputMethod.IMActivateListener() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.1
        @Override // com.freerdp.freerdpcore.services.LinuxInputMethod.IMActivateListener
        public void imActivate(int appType) {
            BaseActivity.this.runOnUiThread(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.1.1
                @Override // java.lang.Runnable
                public void run() {
                    if (BaseActivity.this.container_layout != null && BaseActivity.this.editText != null) {
                        BaseActivity.this.container_layout.removeView(BaseActivity.this.editText);
                        BaseActivity.this.container_layout.addView(BaseActivity.this.editText);
                        BaseActivity.this.editText.setVisibility(View.VISIBLE);
                        BaseActivity.this.editText.setFocusable(true);
                        BaseActivity.this.editText.requestFocus();
                        BaseActivity.this.editText.setEnabled(true);
                        BaseActivity.this.isImActivate = true;
                        BaseActivity.this.container_layout.requestFocus();
                    }
                    if (BaseActivity.this.innerHandler.hasMessages(10013)) {
                        BaseActivity.this.innerHandler.removeMessages(10013);
                    }
                }
            });
        }

        @Override // com.freerdp.freerdpcore.services.LinuxInputMethod.IMActivateListener
        public void imDeactivate(int appType) {
            BaseActivity.this.innerHandler.sendMessageDelayed(Message.obtain((Handler) null, 10013), 300L);
        }

        @Override // com.freerdp.freerdpcore.services.LinuxInputMethod.IMActivateListener
        public void imCursorRect(int appType, final int left, final int top, int width, final int height) {
            MslgLogger.LOGD(ISurface.TAG, "IMActivateListener imCursorRect:   left : " + left + " top : " + top, false);
            BaseActivity.this.runOnUiThread(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.1.2
                @Override // java.lang.Runnable
                public void run() {
                    if (BaseActivity.this.editText != null) {
                        BaseActivity.this.editText.setX(left);
                        BaseActivity.this.editText.setY(top + height);
                        BaseActivity.this.editText.invalidate();
                    }
                }
            });
        }
    };
    private ExecutorService executors = Executors.newFixedThreadPool(5);

    protected abstract void onPermissionGranted();

    public boolean isRootActivity() {
        return this.isConnectActivity;
    }

    public void OnRailChannelReady(boolean ready) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes6.dex */
    public class InnerHandler extends Handler {
        private InnerHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constances.MSG_SHOW_PERMISSION_DIALOG /* 10008 */:
                    ((AlertDialog) msg.obj).show();
                    AlertDialog dialog = (AlertDialog) msg.obj;
                    Button button = dialog.getButton(-1);
                    button.setTextColor(ContextCompat.getColor(BaseActivity.this, R.color.bule));
                    WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                    WindowManager wm = BaseActivity.this.getWindowManager();
                    WindowMetrics windowMetrics = wm.getMaximumWindowMetrics();
                    int width = windowMetrics.getBounds().width();
                    int height = windowMetrics.getBounds().height();
                    params.width = width / 3;
                    params.height = height / 4;
                    dialog.getWindow().setAttributes(params);
                    return;
                case 10013:
                    BaseActivity.this.runOnUiThread(new Runnable() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.InnerHandler.1
                        @Override // java.lang.Runnable
                        public void run() {
                            Log.v(ISurface.TAG, "MSG_HIDE_INPUT ");
                            if (BaseActivity.this.sessionView != null && BaseActivity.this.editText != null) {
                                BaseActivity.this.isImActivate = false;
                                BaseActivity.this.sessionView.setFocusable(true);
                                BaseActivity.this.sessionView.requestFocus();
                                BaseActivity.this.editText.setEnabled(false);
                                BaseActivity.this.editText.setVisibility(View.GONE);
                                if (Utils.isInputMethodShowing(BaseActivity.this.getWindow(), BaseActivity.this.getWindowManager())) {
                                    Utils.hideSoftInput(BaseActivity.this.getApplicationContext(), BaseActivity.this.editText);
                                }
                            }
                        }
                    });
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Utils.getProperty(Constances.PROP_MSLG_KEY).equals("1")) {
            Constances.isWestonStarted = true;
            MslgLogger.LOGD(ISurface.TAG, "set SystemProperty", true);
            Utils.setProperty(Constances.PROP_MSLG_KEY, "1");
        }
        getWindow().requestFeature(12);
        this.keyboardMapper = KeyboardMapperManager.getManager().getKeyboardMapper();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        this.editText.addTextChangedListener(KeyboardMapperManager.getManager().getEditWatcher());
        KeyboardMapperManager.getManager().setEditText(this.editText);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        MslSurfaceInfo mslSurfaceInfo = this.surfaceInfo;
        if (mslSurfaceInfo != null && mslSurfaceInfo.id > 0 && this.surfaceInfo.isModal) {
            MslgLogger.LOGD(MslgLogger.TAG_MODAL, "onResume winid = " + this.windowId, false);
            MultiWindowManager.getManager().setModalWindowId(this.surfaceInfo.id);
        }
        MultiWindowManager.getManager().addIMActivateListener(this.imActivateListener);
        if (this.editText.getVisibility() != View.VISIBLE && LinuxInputMethod.mInputActivate) {
            this.editText.setVisibility(View.VISIBLE);
            this.editText.setFocusable(true);
            this.editText.requestFocus();
            this.editText.setEnabled(true);
        }
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(134217728);
        View mDecor = getWindow().getDecorView();
        mDecor.setSystemUiVisibility(5894);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        SessionState sessionState;
        super.onDestroy();
        this.isImActivate = false;
        KeyboardMapperManager.getManager().setEditText(null);
        if (this.windowId > 0 && !this.closeWindowByNative && (sessionState = MultiWindowManager.getSessionManager().getCurrentSession()) != null) {
            LibFreeRDP.sendWindowEvent(sessionState.getInstance(), this.windowId, LibFreeRDP.WINDOW_CLOSE_EVENT);
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.activity.ComponentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(134217728);
        View mDecor = getWindow().getDecorView();
        mDecor.setSystemUiVisibility(5894);
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void closeWindow(int windowId) {
        this.closeWindowByNative = true;
        MultiWindowManager.getManager().removeSurface(windowId);
        LibFreeRDP.sendCursorEvent(MultiWindowManager.getSessionManager().getCurrentSession().getInstance(), 0, 0, Mouse.getMoveEvent());
        finishAndRemoveTask();
        MslgLogger.LOGD(MslgLogger.TAG_ACTIVITY, "LinuxVirtualActivity finishAndRemoveTask windowid = " + windowId, false);
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void updateWindow(int windowId, int x, int y, int width, int height) {
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void refreshContent() {
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.ISurface
    public Rect getRegion() {
        return null;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (this.isImActivate) {
            int unicodeChar = event.getUnicodeChar();
            if (Character.isLetter(unicodeChar)) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (mgr.isActive()) {
                    mgr.showSoftInput(this.editText, 1);
                }
                this.editText.setVisibility(View.VISIBLE);
                this.editText.setFocusable(true);
                this.editText.requestFocus();
                this.editText.setEnabled(true);
            }
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

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (!Environment.isExternalStorageManager()) {
                if (this.dlgManagerRequset == null) {
                    createManagerDialogs();
                }
                this.innerHandler.sendMessage(Message.obtain(null, Constances.MSG_SHOW_PERMISSION_DIALOG, this.dlgManagerRequset));
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
                MslgLogger.LOGD(ISurface.TAG, "onRequestPermissionsResult: 没有读VIDEO权限 ", true);
                if (rejectVideo) {
                    this.innerHandler.sendMessage(Message.obtain(null, Constances.MSG_SHOW_PERMISSION_DIALOG, this.dlgPerssionRequset));
                    sendDialog = true;
                } else {
                    this.innerHandler.sendMessage(Message.obtain(null, Constances.MSG_SHOW_PERMISSION_DIALOG, this.dlgPerssionRequsetRefuse));
                    sendDialogRefuse = true;
                }
            }
            if (checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED) {
                boolean rejectAudio = ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.READ_MEDIA_AUDIO");
                MslgLogger.LOGD(ISurface.TAG, "onRequestPermissionsResult: 没有读AUDIO权限 ", true);
                if (rejectAudio) {
                    if (!sendDialog.booleanValue()) {
                        this.innerHandler.sendMessage(Message.obtain(null, Constances.MSG_SHOW_PERMISSION_DIALOG, this.dlgPerssionRequset));
                        sendDialog = true;
                    }
                } else if (!sendDialogRefuse.booleanValue()) {
                    this.innerHandler.sendMessage(Message.obtain(null, Constances.MSG_SHOW_PERMISSION_DIALOG, this.dlgPerssionRequsetRefuse));
                    sendDialogRefuse = true;
                }
            }
            if (checkSelfPermission("android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
                boolean rejectNotic = ActivityCompat.shouldShowRequestPermissionRationale(this, "android.permission.POST_NOTIFICATIONS");
                MslgLogger.LOGD(ISurface.TAG, "onRequestPermissionsResult: 没有读NOTIFICATIONS权限 ", true);
                if (rejectNotic) {
                    if (!sendDialog.booleanValue()) {
                        this.innerHandler.sendMessage(Message.obtain(null, Constances.MSG_SHOW_PERMISSION_DIALOG, this.dlgPerssionRequset));
                        sendDialog = true;
                    }
                } else if (!sendDialogRefuse.booleanValue()) {
                    this.innerHandler.sendMessage(Message.obtain(null, Constances.MSG_SHOW_PERMISSION_DIALOG, this.dlgPerssionRequsetRefuse));
                    sendDialogRefuse = true;
                }
            }
            boolean rejectNotic2 = sendDialog.booleanValue();
            if (!rejectNotic2 && !sendDialogRefuse.booleanValue()) {
                this.permissionGranted = true;
                onPermissionGranted();
            }
        }
    }

    public void requestMyPermissions() {
        if (!Environment.isExternalStorageManager()) {
            createManagerDialogs();
            AlertDialog alertDialog = this.dlgManagerRequset;
            if (alertDialog != null) {
                this.innerHandler.sendMessage(Message.obtain(null, Constances.MSG_SHOW_PERMISSION_DIALOG, alertDialog));
                return;
            }
            return;
        }
        if (checkSelfPermission("android.permission.READ_MEDIA_IMAGES") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.READ_MEDIA_VIDEO") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != PackageManager.PERMISSION_GRANTED || checkSelfPermission("android.permission.POST_NOTIFICATIONS") != PackageManager.PERMISSION_GRANTED) {
            MslgLogger.LOGD(ISurface.TAG, "requestMyPermissions: 没有VIDEO权限", true);
            requestPermissions(new String[]{"android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO", "android.permission.POST_NOTIFICATIONS"}, 100);
        } else {
            this.permissionGranted = true;
            onPermissionGranted();
        }
    }

    private void createManagerDialogs() {
        this.dlgManagerRequset = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_manager_message).setPositiveButton(R.string.allow_perssion, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    Intent intent = new Intent("android.settings.MANAGE_APP_ALL_FILES_ACCESS_PERMISSION");
                    intent.setData(Uri.parse("package:" + BaseActivity.this.getPackageName()));
                    BaseActivity.this.startActivityForResult(intent, 100);
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    BaseActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
    }

    private void createDialogs() {
        this.dlgPerssionRequsetRefuse = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_message).setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    Uri uri = Uri.fromParts("package", BaseActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    BaseActivity.this.startActivity(intent);
                    BaseActivity.this.finish();
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    BaseActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
        this.dlgPerssionRequset = new AlertDialog.Builder(this).setTitle(R.string.refuse_perssion).setMessage(R.string.dig_message).setPositiveButton(R.string.retry_perssion, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.7
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    BaseActivity.this.requestMyPermissions();
                    dialog.notify();
                }
            }
        }).setNegativeButton(R.string.cancek, new DialogInterface.OnClickListener() { // from class: com.xiaomi.mslgrdp.multwindow.base.BaseActivity.6
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                synchronized (dialog) {
                    dialog.notify();
                    BaseActivity.this.finish();
                }
            }
        }).setCancelable(false).create();
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
}