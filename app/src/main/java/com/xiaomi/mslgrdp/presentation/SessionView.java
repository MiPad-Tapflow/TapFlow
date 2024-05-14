package com.xiaomi.mslgrdp.presentation;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.multwindow.SessionManager;
import com.xiaomi.mslgrdp.multwindow.SessionState;
import com.xiaomi.mslgrdp.utils.DoubleGestureDetector;
import com.xiaomi.mslgrdp.utils.GestureDetector;
import com.xiaomi.mslgrdp.utils.Mouse;
import java.util.Stack;

/* loaded from: classes6.dex */
public class SessionView extends FrameLayout {
    private static final String TAG = "SessionView";
    private static final float TOUCH_SCROLL_DELTA = 10.0f;
    private View child;
    private DoubleGestureDetector doubleGestureDetector;
    private GestureDetector gestureDetector;
    private int height;
    private Stack<Rect> invalidRegions;
    private boolean isLeftDown;
    private boolean isMoveDown;
    private boolean isPressMove;
    private Context mContext;
    private boolean move_status;
    private SessionManager sessionManager;
    private SessionViewListener sessionViewListener;
    private int width;

    /* loaded from: classes6.dex */
    public interface SessionViewListener {
        void onSessionViewBeginTouch();

        void onSessionViewEndTouch();

        void onSessionViewHScroll(boolean z, int i, int i2, float f);

        void onSessionViewLeftTouch(int i, int i2, boolean z);

        void onSessionViewMove(int i, int i2);

        void onSessionViewRightTouch(int i, int i2, boolean z);

        void onSessionViewScroll(boolean z, int i, int i2, float f);
    }

    public SessionView(Context context) {
        super(context);
        this.sessionViewListener = null;
        this.move_status = false;
        this.isPressMove = false;
        this.isLeftDown = false;
        this.isMoveDown = false;
        this.sessionManager = MultiWindowManager.getSessionManager();
        initSessionView(context);
    }

    public SessionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.sessionViewListener = null;
        this.move_status = false;
        this.isPressMove = false;
        this.isLeftDown = false;
        this.isMoveDown = false;
        this.sessionManager = MultiWindowManager.getSessionManager();
        initSessionView(context);
    }

    public SessionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.sessionViewListener = null;
        this.move_status = false;
        this.isPressMove = false;
        this.isLeftDown = false;
        this.isMoveDown = false;
        this.sessionManager = MultiWindowManager.getSessionManager();
        initSessionView(context);
    }

    public void setContext(Context context) {
        if (context != null) {
            this.mContext = context;
        }
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.child = getChildAt(0);
    }

    private void initSessionView(Context context) {
        this.invalidRegions = new Stack<>();
        this.gestureDetector = new GestureDetector(context, new SessionGestureListener(), null, true);
        this.doubleGestureDetector = new DoubleGestureDetector(context, null, new SessionDoubleGestureListener());
    }

    public void setSessionViewListener(SessionViewListener sessionViewListener) {
        this.sessionViewListener = sessionViewListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MotionEvent mapDoubleTouchEvent(MotionEvent event) {
        MotionEvent mappedEvent = MotionEvent.obtain(event);
        float[] coordinates = {(mappedEvent.getX(0) + mappedEvent.getX(1)) / 2.0f, (mappedEvent.getY(0) + mappedEvent.getY(1)) / 2.0f};
        mappedEvent.setLocation(coordinates[0], coordinates[1]);
        return mappedEvent;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        LinuxVirtualActivity.isExit = false;
        boolean res = this.gestureDetector.onTouchEvent(event);
        return res | this.doubleGestureDetector.onTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes6.dex */
    public class SessionGestureListener extends GestureDetector.SimpleOnGestureListener {
        boolean longPressInProgress;
        boolean mouseCliack;

        private SessionGestureListener() {
            this.longPressInProgress = false;
            this.mouseCliack = false;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            if (this.mouseCliack) {
                this.mouseCliack = false;
            }
            SessionState sessionState = SessionView.this.sessionManager.getCurrentSession();
            if (sessionState != null) {
                if (SessionView.this.isMoveDown) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getTouchUpEvent());
                    SessionView.this.isMoveDown = false;
                }
                if (this.longPressInProgress) {
                    this.longPressInProgress = false;
                }
                SessionView.this.sessionViewListener.onSessionViewBeginTouch();
                return true;
            }
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onUp(MotionEvent e) {
            SessionState sessionState = SessionView.this.sessionManager.getCurrentSession();
            if (sessionState != null) {
                if (SessionView.this.isLeftDown) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, false));
                    SessionView.this.isLeftDown = false;
                }
                if (SessionView.this.isMoveDown) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getTouchUpEvent());
                    SessionView.this.isMoveDown = false;
                }
                if (this.longPressInProgress) {
                    this.longPressInProgress = false;
                }
                if (this.mouseCliack) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, false));
                    this.mouseCliack = false;
                }
                SessionView.this.sessionViewListener.onSessionViewEndTouch();
                return true;
            }
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
            SessionView.this.sessionViewListener.onSessionViewBeginTouch();
            SessionView.this.isPressMove = false;
            this.longPressInProgress = true;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public void onLongPressUp(MotionEvent e) {
            SessionState sessionState = SessionView.this.sessionManager.getCurrentSession();
            if (sessionState != null) {
                if (SessionView.this.isLeftDown) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, false));
                    SessionView.this.isLeftDown = false;
                }
                if (!SessionView.this.isPressMove && e.getPointerCount() > 0 && (e.getToolType(e.getActionIndex()) != MotionEvent.TOOL_TYPE_MOUSE || e.getDevice().getName().contains("Touch") || e.getDevice().getName().contains("Keyboard"))) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getMoveEvent());
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getRightButtonEvent(SessionView.this.mContext, true));
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getRightButtonEvent(SessionView.this.mContext, false));
                }
                this.longPressInProgress = false;
                SessionView.this.isPressMove = false;
                SessionView.this.sessionViewListener.onSessionViewEndTouch();
            }
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            SessionState sessionState = SessionView.this.sessionManager.getCurrentSession();
            if (sessionState == null) {
                return true;
            }
            if (!this.longPressInProgress) {
                if (e1.getPointerCount() <= 0 || e1.getToolType(e1.getActionIndex()) != MotionEvent.TOOL_TYPE_MOUSE || e1.getButtonState() != 1) {
                    if (SessionView.this.isMoveDown) {
                        if (!SessionView.this.move_status) {
                            SessionView.this.move_status = true;
                            return false;
                        }
                        LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e2.getX(), (int) e2.getY(), Mouse.getTouchMoveEvent());
                        SessionView.this.move_status = false;
                        return true;
                    }
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e1.getX(), (int) e1.getY(), Mouse.getTouchDownEvent());
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e2.getX(), (int) e2.getY(), Mouse.getTouchMoveEvent());
                    SessionView.this.isMoveDown = true;
                    SessionView.this.move_status = true;
                    return true;
                }
                if (!this.mouseCliack) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e1.getX(), (int) e1.getY(), Mouse.getMoveEvent());
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e1.getX(), (int) e1.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, true));
                    this.mouseCliack = true;
                }
                if (touchViewMove(e1, e2)) {
                    SessionView.this.sessionViewListener.onSessionViewMove((int) e2.getX(), (int) e2.getY());
                }
                return true;
            }
            if (touchViewMove(e1, e2)) {
                if (!SessionView.this.isLeftDown) {
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e1.getX(), (int) e1.getY(), Mouse.getMoveEvent());
                    LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e1.getX(), (int) e1.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, true));
                    SessionView.this.isLeftDown = true;
                }
                SessionView.this.sessionViewListener.onSessionViewMove((int) e2.getX(), (int) e2.getY());
            }
            return false;
        }

        private boolean touchViewMove(MotionEvent e1, MotionEvent e2) {
            int deltaX = (int) (e2.getX() - e1.getX());
            int deltaY = (int) (e2.getY() - e1.getY());
            int distance = (deltaX * deltaX) + (deltaY * deltaY);
            ViewConfiguration configuration = ViewConfiguration.get(SessionView.this.mContext);
            int touchSlop = configuration.getScaledTouchSlop();
            int touchSlopSquare = touchSlop * touchSlop;
            if (distance > touchSlopSquare) {
                SessionView.this.isPressMove = true;
                return true;
            }
            return false;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTap(MotionEvent e) {
            if (LinuxInputMethod.mInputActivate) {
                InputMethodManager mgr = (InputMethodManager) SessionView.this.mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (mgr.isActive()) {
                    mgr.toggleSoftInput(2, 0);
                }
            }
            SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) e.getX(), (int) e.getY(), true);
            SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) e.getX(), (int) e.getY(), false);
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            SessionState sessionState = SessionView.this.sessionManager.getCurrentSession();
            if (sessionState != null) {
                SessionView.this.sessionViewListener.onSessionViewBeginTouch();
                switch (e.getButtonState()) {
                    case 2:
                        LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getMoveEvent());
                        LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getRightButtonEvent(SessionView.this.mContext, true));
                        LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getRightButtonEvent(SessionView.this.mContext, false));
                        break;
                    default:
                        LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) e.getX(), (int) e.getY(), Mouse.getMoveEvent());
                        SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) e.getX(), (int) e.getY(), true);
                        SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) e.getX(), (int) e.getY(), false);
                        break;
                }
                SessionView.this.sessionViewListener.onSessionViewEndTouch();
                return true;
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes6.dex */
    public class SessionDoubleGestureListener implements DoubleGestureDetector.OnDoubleGestureListener {
        private MotionEvent prevEvent;

        private SessionDoubleGestureListener() {
            this.prevEvent = null;
        }

        @Override // com.xiaomi.mslgrdp.utils.DoubleGestureDetector.OnDoubleGestureListener
        public boolean onDoubleTouchDown(MotionEvent e) {
            SessionView.this.sessionViewListener.onSessionViewBeginTouch();
            this.prevEvent = MotionEvent.obtain(e);
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.DoubleGestureDetector.OnDoubleGestureListener
        public boolean onDoubleTouchUp(MotionEvent e) {
            MotionEvent motionEvent = this.prevEvent;
            if (motionEvent != null) {
                motionEvent.recycle();
                this.prevEvent = null;
            }
            SessionView.this.sessionViewListener.onSessionViewEndTouch();
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.DoubleGestureDetector.OnDoubleGestureListener
        public boolean onDoubleTouchScroll(MotionEvent e1, MotionEvent e2) {
            float deltaY = e2.getY() - this.prevEvent.getY();
            float deltaX = e2.getX() - this.prevEvent.getX();
            if (Math.abs(deltaY) > Math.abs(deltaX)) {
                if (deltaY > SessionView.TOUCH_SCROLL_DELTA) {
                    SessionView.this.sessionViewListener.onSessionViewScroll(true, (int) e2.getX(), (int) e2.getY(), Math.abs(deltaY));
                    this.prevEvent.recycle();
                    this.prevEvent = MotionEvent.obtain(e2);
                } else if (deltaY < -10.0f) {
                    SessionView.this.sessionViewListener.onSessionViewScroll(false, (int) e2.getX(), (int) e2.getY(), Math.abs(deltaY));
                    this.prevEvent.recycle();
                    this.prevEvent = MotionEvent.obtain(e2);
                }
            } else if (deltaX < SessionView.TOUCH_SCROLL_DELTA) {
                SessionView.this.sessionViewListener.onSessionViewHScroll(false, (int) e2.getX(), (int) e2.getY(), Math.abs(deltaX));
                this.prevEvent.recycle();
                this.prevEvent = MotionEvent.obtain(e2);
            } else if (deltaX > -10.0f) {
                SessionView.this.sessionViewListener.onSessionViewHScroll(true, (int) e2.getX(), (int) e2.getY(), Math.abs(deltaX));
                this.prevEvent.recycle();
                this.prevEvent = MotionEvent.obtain(e2);
            }
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.DoubleGestureDetector.OnDoubleGestureListener
        public boolean onDoubleTouchSingleTap(MotionEvent e) {
            SessionState sessionState = SessionView.this.sessionManager.getCurrentSession();
            if (sessionState != null) {
                MotionEvent mappedEvent = SessionView.this.mapDoubleTouchEvent(e);
                SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), true);
                SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), false);
                LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getMoveEvent());
                LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, true));
                LibFreeRDP.sendCursorEvent(sessionState.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, false));
                return true;
            }
            return true;
        }
    }
}