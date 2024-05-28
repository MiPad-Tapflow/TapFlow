package com.xiaomi.mslgrdp.presentation;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import cn.ljlVink.Tapflow.R;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.multwindow.MultiWindowService;
import com.xiaomi.mslgrdp.multwindow.SessionState;
import com.xiaomi.mslgrdp.multwindow.base.BaseActivity;
import com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.Mouse;
import com.xiaomi.mslgrdp.utils.MslgLogger;
import com.xiaomi.mslgrdp.views.MslDragLayout;
import com.xiaomi.mslgrdp.views.MslSurfaceView;
import java.util.List;

/* loaded from: classes6.dex */
public class LinuxVirtualActivity extends BaseActivity implements SessionView.SessionViewListener {
    private static final int MAX_DISCARDED_MOVE_EVENTS = 3;
    private static final int SEND_MOVE_EVENT_TIMEOUT = 150;
    public static final String TAG = "Mi.LinuxVirtualActivity";
    private ImageView imageView;
    private int left;
    private String mFileurl;
    private PointerIcon pointerIcon;
    private SessionState session;
    private View tempView;
    private int top;
    private UIHandler uiHandler;
    public static Boolean isExit = false;
    private String mOpenApp = "wps";
    private boolean toggleMouseButtons = false;
    private int discardedMoveEvents = 0;
    private boolean mStartTiming = false;
    private long timestampSeconds = 0;
    private FreerdpUiEventListener uiEventListener = new FreerdpUiEventListener();
    private boolean hasDrag = false;
    public Intent originIntent = null;

    public PointerIcon getPointerIcon() {
        return this.pointerIcon;
    }

    public void setPointerIcon(PointerIcon pointerIcon) {
        this.pointerIcon = pointerIcon;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes6.dex */
    public class FreerdpUiEventListener extends FreerdpUIEventListenerAdapter {
        FreerdpUiEventListener() {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.FreerdpUIEventListenerAdapter, com.freerdp.freerdpcore.services.LibFreeRDP.UIEventListener
        public boolean OnMinimizeRequested(int appType, boolean minimized) {
            if (LinuxVirtualActivity.this.windowId != LibFreeRDP.mActivateWindowId) {
                return false;
            }
            if (MultiWindowManager.getSessionManager().getCurrentSession() != null) {
                LibFreeRDP.sendCursorEvent(MultiWindowManager.getSessionManager().getCurrentSession().getInstance(), 0, 0, Mouse.getMoveEvent());
            }
            LinuxVirtualActivity.this.moveTaskToBack(true);
            return true;
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate ");
        setContentView(R.layout.session);
        this.uiHandler = new UIHandler();
        this.originIntent = new Intent(getIntent());
        if (getIntent() != null && getIntent().getAction() != null) {
            if (!MultiWindowManager.getManager().isFromMslg()) {
                MultiWindowManager.getManager().setFromMslg(true);
            }
        }
        initView();
        this.toggleMouseButtons = false;
        this.container_layout = (MslDragLayout) findViewById(R.id.drag_view);
        this.container_layout.setBackgroundColor(0);
        requestMyPermissions();
        if (!this.permissionGranted) {
            return;
        }
        if (MultiWindowManager.getSessionManager().canConnect()) {
            MslgLogger.LOGD(MslgLogger.TAG_ACTIVITY, "LinuxVirtualActivity onCreate startConnectServer--- isConnectActivity = true", false);
            startConnectServer();
            return;
        }
        Boolean isRootFs = Boolean.valueOf(getIntent().getBooleanExtra("StartFromMSLG", false));
        if (!isRootFs.booleanValue()) {
            int wid = getIntent().getIntExtra(Constances.BUNDLE_ID_WINDOW_ID, -1);
            showWindowInfo(wid);
        }
    }

    private void startConnectServer() {
        UIHandler uIHandler;
        WindowManager wm = getWindowManager();
        WindowMetrics windowMetrics = wm.getMaximumWindowMetrics();
        int width = windowMetrics.getBounds().width();
        int height = windowMetrics.getBounds().height();
        MultiWindowManager.getManager().setmIsAPPStarted(false);
        Log.v(TAG, "startConnectServer width = " + width + " height = " + height);
        MultiWindowManager.getSessionManager().connectReady(width, height);
        this.isConnectActivity = true;
        if (Constances.isWestonStarted.booleanValue() && (uIHandler = this.uiHandler) != null) {
            uIHandler.sendMessageDelayed(uIHandler.obtainMessage(10), 1000L);
        } else {
            Log.v(TAG, "startConnectServer ");
            Intent intentService = new Intent(this, (Class<?>) MultiWindowService.class);
            startForegroundService(intentService);
            MultiWindowManager.getManager().bindConnectServer();
        }
        this.uiHandler.removeMessages(UIHandler.MSG_STUCK_ON_LOADING);
    }

    private void initView() {
        String appName = getIntent().getStringExtra("StarMslgApp");
        if (appName != null) {
            View findViewById = findViewById(R.id.session_temp_view);
            this.tempView = findViewById;
            findViewById.setBackgroundColor(-1);
            this.tempView.setVisibility(View.VISIBLE);
            this.uiHandler.sendEmptyMessageDelayed(UIHandler.MSG_STUCK_ON_LOADING, 3000L);
            ImageView imageView = (ImageView) findViewById(R.id.image);
            this.imageView = imageView;
            imageView.setVisibility(View.VISIBLE);
            if (appName.equals("wps")) {
                MultiWindowManager.getManager().setAppType(1);
                this.imageView.setImageDrawable(getDrawable(R.drawable.welcom));
            } else if (appName.equals("cajviewer")) {
                MultiWindowManager.getManager().setAppType(3);
                this.imageView.setImageDrawable(getDrawable(R.drawable.welcom));
            } else {
                MultiWindowManager.getManager().setAppType(1);
                this.imageView.setImageDrawable(getDrawable(R.drawable.welcom));
            }
        }
        this.sessionView = (SessionView) findViewById(R.id.sessionView);
        this.editText = (EditText) findViewById(R.id.editTextInput);
        this.iv_content = (MslSurfaceView) findViewById(R.id.iv_content);
        this.sessionView.setContext(this);
        this.sessionView.setSessionViewListener(this);
        this.sessionView.requestFocus();
    }

    public void showWindowInfo(int windowId) {
        if (MultiWindowManager.getSessionManager().isConnected()) {
            View view = this.tempView;
            if (view != null && view.getVisibility() == View.VISIBLE) {
                AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
                fadeOut.setDuration(300L);
                this.tempView.startAnimation(fadeOut);
                this.tempView.setVisibility(View.GONE);
                this.imageView.setImageDrawable(null);
            }
            this.uiHandler.removeMessages(UIHandler.MSG_STUCK_ON_LOADING);
            this.iv_content.setVisibility(View.VISIBLE);
            SessionState currentSession = MultiWindowManager.getSessionManager().getCurrentSession();
            this.session = currentSession;
            currentSession.addUIEventListener(this.uiEventListener);
            this.windowId = windowId;
            MslgLogger.LOGD(MslgLogger.TAG_ACTIVITY, "LinuxVirtualActivity onCreate windowid = " + windowId, true);
            if (windowId == -1) {
                finishAndRemoveTask();
            }
            if (windowId != -1) {
                if (MultiWindowManager.getManager().inPendingCloseWindows(windowId)) {
                    MultiWindowManager.getManager().removeSurface(windowId);
                    finishAndRemoveTask();
                    return;
                }
                this.surfaceInfo = MultiWindowManager.getManager().getSurfaceInfo(windowId);
                Log.v(MultiWindowManager.TAG, "LinuxVirtualActivity onCreate surfaceInfo = " + this.surfaceInfo);
                if (this.surfaceInfo == null) {
                    finishAndRemoveTask();
                    return;
                }
                MultiWindowManager.getManager().addSurface(windowId, this);
                this.left = this.surfaceInfo.x;
                this.top = this.surfaceInfo.y;
                this.iv_content.setWindowId(windowId);
                refreshContent();
                boolean isFullScreen = Math.abs(this.surfaceInfo.width - Constances.SCREEN_WIDTH) < 10;
                this.container_layout.setDragView(this.iv_content, this.surfaceInfo);
                this.container_layout.setFullScreen(isFullScreen);
                this.container_layout.setUpdateLocationListener(new MslDragLayout.UpdateListener() { // from class: com.xiaomi.mslgrdp.presentation.LinuxVirtualActivity.1
                    @Override // com.xiaomi.mslgrdp.views.MslDragLayout.smali.UpdateListener
                    public void onUpdate() {
                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) LinuxVirtualActivity.this.iv_content.getLayoutParams();
                        LinuxVirtualActivity.this.left = params.leftMargin;
                        LinuxVirtualActivity.this.top = params.topMargin;
                        LinuxVirtualActivity.this.hasDrag = true;
                    }
                });
            }
        }
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    protected void onNewIntent(Intent intent) {
        Log.v(TAG, "Session.onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        Intent intent = getIntent();
        if (intent != null) {
            Boolean isRootFs = Boolean.valueOf(intent.getBooleanExtra("StartFromMSLG", false));
            if (isRootFs.booleanValue() && !MultiWindowManager.getManager().ismIsAPPStarted()) {
                Log.v(TAG, "Session.isRootFs");
                this.mOpenApp = intent.getStringExtra("StarMslgApp");
                this.mFileurl = intent.getStringExtra("MiRdpFileUrl");
                MultiWindowManager.getManager().sendPath(this.mFileurl, this.mOpenApp);
            } else {
                MultiWindowManager.getManager().activityOnResume(true);
            }
            setIntent(null);
        } else {
            MultiWindowManager.getManager().activityOnResume(true);
        }
        if (!this.mStartTiming) {
            this.timestampSeconds = System.currentTimeMillis() / 1000;
            this.mStartTiming = true;
        }
        Log.v(TAG, "Session.onResume");
        if (this.pointerIcon == null) {
            Resources resources = getResources();
            Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrow);
            MultiWindowManager.getManager();
            this.pointerIcon = PointerIcon.create(MultiWindowManager.getMouseManager().move_mouse_pixel(bitmap, 24, 16), 1.0f, 1.0f);
            this.container_layout.setPointerIcon(this.pointerIcon);
        }
        super.onResume();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause");
        isExit = false;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        setIntent(null);
        this.mFileurl = null;
        if (this.mStartTiming && this.container_layout.isFullScreen()) {
            //diable onetrack
            this.mStartTiming = false;
            this.timestampSeconds = 0L;
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        MslgLogger.LOGD(TAG, "Android Window:" + this.windowId + " now become to " + hasFocus, false);
        MultiWindowManager multiWindowManager = MultiWindowManager.getManager();
        if (hasFocus && multiWindowManager.getActivityLifecycleCallbacks() != null) {
            multiWindowManager.getActivityLifecycleCallbacks().setTopActivity(this);
        }
        if (!hasFocus && multiWindowManager.getModalWindowId() != -1 && this.windowId == multiWindowManager.getModalWindowId() && getTaskCount() > 1) {
            MslgLogger.LOGD(MslgLogger.TAG_MODAL, "onWindowFocusChanged moveTaskToBack winid = " + this.windowId + " getModalWindowId = " + multiWindowManager.getModalWindowId(), false);
            moveTaskToBack(true);
        }
        if (this.windowId > 0 && hasFocus) {
            if (multiWindowManager.getModalWindowId() != -1 && this.windowId != multiWindowManager.getModalWindowId()) {
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningTaskInfo> rt = am.getRunningTasks(Integer.MAX_VALUE);
                for (int i = 0; i < rt.size(); i++) {
                    ActivityManager.RunningTaskInfo taskInfo = rt.get(i);
                    if (taskInfo.taskId == multiWindowManager.getModalTaskId()) {
                        MslgLogger.LOGD(MslgLogger.TAG_ACTIVITY, "onWindowFocusChanged getModalTaskId = " + taskInfo.taskId, false);
                        am.moveTaskToFront(multiWindowManager.getModalTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                        return;
                    }
                }
                return;
            }
            LibFreeRDP.sendWindowFocusEvent(MultiWindowManager.getSessionManager().getCurrentSession().getInstance(), this.windowId, hasFocus);
        }
    }

    private int getTaskCount() {
        int count = 0;
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rt = am.getRunningTasks(Integer.MAX_VALUE);
        for (int i = 0; i < rt.size(); i++) {
            ActivityManager.RunningTaskInfo taskInfo = rt.get(i);
            if (taskInfo.topActivity != null && getPackageName().equals(taskInfo.topActivity.getPackageName())) {
                count++;
            }
        }
        return count;
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        MslgLogger.LOGD(MslgLogger.TAG_ACTIVITY, "onDestroy isConnectActivity = " + this.isConnectActivity + " windowid =" + this.windowId, false);
        isExit = false;
        if (MultiWindowManager.getSessionManager().getCurrentSession() != null) {
            MultiWindowManager.getSessionManager().getCurrentSession().removeUIEventListener(this.uiEventListener);
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity
    public void OnRailChannelReady(boolean ready) {
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity
    protected void onPermissionGranted() {
        if (MultiWindowManager.getSessionManager().canConnect()) {
            MslgLogger.LOGD(MslgLogger.TAG_ACTIVITY, "onPermissionGranted startConnectServer", false);
            startConnectServer();
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void updateWindow(int windowId, int x, int y, int width, int height) {
        if (this.surfaceInfo != null && this.surfaceInfo.width == width && this.surfaceInfo.height == height) {
            Log.v(MultiWindowManager.TAG, "windowid = " + windowId + "复用bitmap");
        }
        if (!this.container_layout.isFullScreen() && !this.hasDrag) {
            boolean isFullScreen = Math.abs(this.surfaceInfo.width - Constances.SCREEN_WIDTH) < 10;
            this.container_layout.setFullScreen(isFullScreen);
            this.top = this.surfaceInfo.y;
            this.left = this.surfaceInfo.x;
            MslgLogger.LOGD(MultiWindowManager.TAG, "wps window animation", false);
        }
        Log.v(MultiWindowManager.TAG, "windowid = " + windowId + " x = " + x + " y = " + y + " width = " + width + " height = " + height);
        MslSurfaceInfo surfaceInfo = MultiWindowManager.getManager().getSurfaceInfo(windowId);
        if (surfaceInfo != null && this.surfaceInfo != null && surfaceInfo.isMaximized != this.surfaceInfo.isMaximized) {
            this.top = surfaceInfo.y;
            this.left = surfaceInfo.x;
        }
        this.surfaceInfo = surfaceInfo;
        if (!MultiWindowManager.isDragging.get()) {
            refreshContent();
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, com.xiaomi.mslgrdp.multwindow.base.ISurface
    public void refreshContent() {
        Log.v(MultiWindowManager.TAG, "refreshContent windowid = " + this.windowId + " surfaceInfo " + this.surfaceInfo);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.iv_content.getLayoutParams();
        int x = this.surfaceInfo.x;
        int y = this.surfaceInfo.y;
        int width = this.surfaceInfo.width;
        int height = this.surfaceInfo.height;
        params.leftMargin = this.left;
        params.topMargin = this.top;
        params.width = width;
        params.height = height;
        this.iv_content.setLayoutParams(params);
        this.mBitmap = MultiWindowManager.getBitmapPool().getBitmapWithWinId(this.windowId);
        this.iv_content.setBitmap(this.mBitmap);
        region.set(x, y, x + width, y + height);
    }

    private void sendDelayedMoveEvent(int x, int y) {
        UIHandler uIHandler = this.uiHandler;
        if (uIHandler != null && uIHandler.hasMessages(4)) {
            this.uiHandler.removeMessages(4);
            this.discardedMoveEvents++;
        } else {
            this.discardedMoveEvents = 0;
        }
        if (this.discardedMoveEvents > 3) {
            SessionState sessionState = this.session;
            if (sessionState != null) {
                LibFreeRDP.sendCursorEvent(sessionState.getInstance(), x, y, Mouse.getMoveEvent());
                return;
            }
            return;
        }
        UIHandler uIHandler2 = this.uiHandler;
        if (uIHandler2 != null) {
            uIHandler2.sendMessageDelayed(Message.obtain(null, 4, x, y), 150L);
        }
    }

    private void cancelDelayedMoveEvent() {
        UIHandler uIHandler = this.uiHandler;
        if (uIHandler != null) {
            uIHandler.removeMessages(4);
        }
    }

    public void onBackKeyPressed() {
        MslgLogger.LOGD(TAG, "onBackPressed ", false);
        if (isExit.booleanValue()) {
            isExit = false;
            if (MultiWindowManager.getManager().atHome()) {
                moveTaskToBack(true);
                return;
            } else {
                finishAndRemoveTask();
                return;
            }
        }
        isExit = true;
        UIHandler uIHandler = this.uiHandler;
        if (uIHandler != null) {
            if (uIHandler.hasMessages(1)) {
                this.uiHandler.removeMessages(1);
            }
            this.uiHandler.sendEmptyMessageDelayed(1, 5000L);
        }
        Toast.makeText(this, R.string.exit_app, Toast.LENGTH_SHORT).show();
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.BaseActivity, androidx.appcompat.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (event.isMetaPressed()) {
            return true;
        }
        if (keycode == 4) {
            if (MultiWindowManager.getManager().atHome() || this.container_layout.isFullScreen()) {
                onBackKeyPressed();
                return super.onKeyDown(keycode, event);
            }
            finishAndRemoveTask();
        } else {
            UIHandler uIHandler = this.uiHandler;
            if (uIHandler != null && uIHandler.hasMessages(1)) {
                this.uiHandler.removeMessages(1);
            }
            isExit = false;
        }
        return super.onKeyDown(keycode, event);
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewBeginTouch() {
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewEndTouch() {
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewLeftTouch(int x, int y, boolean down) {
        if (!down) {
            cancelDelayedMoveEvent();
        }
        SessionState sessionState = this.session;
        if (sessionState != null) {
            LibFreeRDP.sendCursorEvent(sessionState.getInstance(), x, y, this.toggleMouseButtons ? Mouse.getRightButtonEvent(this, down) : Mouse.getLeftButtonEvent(this, down));
        }
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
        SessionState sessionState = this.session;
        if (sessionState != null) {
            LibFreeRDP.sendCursorEvent(sessionState.getInstance(), touch_x, touch_y, Mouse.getScrollEvent(this, down, y));
        }
    }

    @Override // com.xiaomi.mslgrdp.presentation.SessionView.SessionViewListener
    public void onSessionViewHScroll(boolean down, int touch_x, int touch_y, float x) {
        SessionState sessionState = this.session;
        if (sessionState != null) {
            LibFreeRDP.sendCursorEvent(sessionState.getInstance(), touch_x, touch_y, Mouse.getScrollEventH(this, down, x));
        }
    }

    @Override // android.app.Activity
    public boolean onGenericMotionEvent(MotionEvent e) {
        SessionState sessionState;
        SessionState sessionState2;
        switch (e.getAction()) {
            case 7:
                MotionEvent myCopy = MotionEvent.obtain(e);
                this.container_layout.setPointerIcon(this.pointerIcon);
                MultiWindowManager.getManager().sendMouseEvent2Native(myCopy);
                break;
            case 8:
                float vScroll = e.getAxisValue(9);
                if (vScroll < 0.0f && (sessionState2 = this.session) != null) {
                    LibFreeRDP.sendCursorEvent(sessionState2.getInstance(), 0, 0, Mouse.getScrollEventMouse(this, false));
                }
                if (vScroll > 0.0f && (sessionState = this.session) != null) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), 0, 0, Mouse.getScrollEventMouse(this, true));
                    break;
                }
                break;
        }
        super.onGenericMotionEvent(e);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes6.dex */
    public class UIHandler extends Handler {
        public static final int CONNECT_SERVICE = 10;
        public static final int MSG_STUCK_ON_LOADING = 12312;
        public static final int RESET_FLAG = 1;
        public static final int SEND_MOVE_EVENT = 4;
        public static final int UPDATE_MOUSE = 11;

        private UIHandler() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    LinuxVirtualActivity.isExit = false;
                    return;
                case 4:
                    if (LinuxVirtualActivity.this.session != null) {
                        LibFreeRDP.sendCursorEvent(LinuxVirtualActivity.this.session.getInstance(), msg.arg1, msg.arg2, Mouse.getMoveEvent());
                        return;
                    }
                    return;
                case 10:
                    Log.v(LinuxVirtualActivity.TAG, " CONNECT_SERVICE");
                    Intent intentService = new Intent(LinuxVirtualActivity.this, (Class<?>) MultiWindowService.class);
                    LinuxVirtualActivity.this.startForegroundService(intentService);
                    MultiWindowManager.getManager().bindConnectServer();
                    return;
                case 11:
                    Log.v(LinuxVirtualActivity.TAG, " UPDATE_MOUSE");
                    PointerIcon pointerIcon2 = (PointerIcon) msg.obj;
                    LinuxVirtualActivity.this.container_layout.setPointerIcon(pointerIcon2);
                    return;
                case MSG_STUCK_ON_LOADING /* 12312 */:
                    MslgLogger.LOGD(MultiWindowManager.TAG, "MSG_STUCK_ON_LOADING restoreUbuntuProcess invoke....", true);
                    MultiWindowManager.getManager().restoreUbuntuProcess();
                    return;
                default:
                    return;
            }
        }
    }
}