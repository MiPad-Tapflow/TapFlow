package com.xiaomi.mslgrdp.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.inputmethod.InputMethodManager;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.freerdp.freerdpcore.services.LinuxInputMethod;
import com.xiaomi.mslgrdp.application.SessionState;
import com.xiaomi.mslgrdp.utils.DoubleGestureDetector;
import com.xiaomi.mslgrdp.utils.GestureDetector;
import com.xiaomi.mslgrdp.utils.Mouse;
import java.util.Stack;

/* loaded from: classes5.dex */
public class SessionView extends View {
    public static final float MAX_SCALE_FACTOR = 3.0f;
    public static final float MIN_SCALE_FACTOR = 1.0f;
    private static final float SCALE_FACTOR_DELTA = 1.0E-4f;
    private static final String TAG = "SessionView";
    private static final float TOUCH_SCROLL_DELTA = 10.0f;
    private SessionState currentSession;
    private DoubleGestureDetector doubleGestureDetector;
    private GestureDetector gestureDetector;
    private int height;
    private Matrix invScaleMatrix;
    private RectF invalidRegionF;
    private Stack<Rect> invalidRegions;
    private boolean isLeftDown;
    private boolean isMoveDown;
    private boolean isPressMove;
    private Context mContext;
    private boolean move_status;
    private float scaleFactor;
    private Matrix scaleMatrix;
    private SessionViewListener sessionViewListener;
    private BitmapDrawable surface;
    private int touchPointerPaddingHeight;
    private int touchPointerPaddingWidth;
    private int width;

    /* loaded from: classes5.dex */
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
        this.touchPointerPaddingWidth = 0;
        this.touchPointerPaddingHeight = 0;
        this.sessionViewListener = null;
        this.scaleFactor = 1.0f;
        this.move_status = false;
        this.isPressMove = false;
        this.isLeftDown = false;
        this.isMoveDown = false;
        initSessionView(context);
    }

    public SessionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.touchPointerPaddingWidth = 0;
        this.touchPointerPaddingHeight = 0;
        this.sessionViewListener = null;
        this.scaleFactor = 1.0f;
        this.move_status = false;
        this.isPressMove = false;
        this.isLeftDown = false;
        this.isMoveDown = false;
        initSessionView(context);
    }

    public SessionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.touchPointerPaddingWidth = 0;
        this.touchPointerPaddingHeight = 0;
        this.sessionViewListener = null;
        this.scaleFactor = 1.0f;
        this.move_status = false;
        this.isPressMove = false;
        this.isLeftDown = false;
        this.isMoveDown = false;
        initSessionView(context);
    }

    public void setContext(Context context) {
        if (context != null) {
            this.mContext = context;
        }
    }

    private void initSessionView(Context context) {
        this.invalidRegions = new Stack<>();
        this.gestureDetector = new GestureDetector(context, new SessionGestureListener(), null, true);
        this.doubleGestureDetector = new DoubleGestureDetector(context, null, new SessionDoubleGestureListener());
        this.scaleFactor = 1.0f;
        this.scaleMatrix = new Matrix();
        this.invScaleMatrix = new Matrix();
        this.invalidRegionF = new RectF();
        setSystemUiVisibility(4098);
    }

    public void setScaleGestureDetector(ScaleGestureDetector scaleGestureDetector) {
        this.doubleGestureDetector.setScaleGestureDetector(scaleGestureDetector);
    }

    public void setSessionViewListener(SessionViewListener sessionViewListener) {
        this.sessionViewListener = sessionViewListener;
    }

    public void addInvalidRegion(Rect invalidRegion) {
        this.invalidRegions.add(invalidRegion);
    }

    public void invalidateRegion() {
        invalidate();
    }

    public void onSurfaceChange(SessionState session) {
        Log.v(TAG, "onSurfaceChange --- inst = " + session.getInstance());
        BitmapDrawable surface = session.getSurface();
        this.surface = surface;
        Bitmap bitmap = surface.getBitmap();
        this.width = bitmap.getWidth();
        int height = bitmap.getHeight();
        this.height = height;
        this.surface.setBounds(0, 0, this.width, height);
        setMinimumWidth(this.width);
        setMinimumHeight(this.height);
        requestLayout();
        this.currentSession = session;
    }

    public float getZoom() {
        return this.scaleFactor;
    }

    public void setZoom(float factor) {
        this.scaleFactor = factor;
        this.scaleMatrix.setScale(factor, factor);
        Matrix matrix = this.invScaleMatrix;
        float f = this.scaleFactor;
        matrix.setScale(1.0f / f, 1.0f / f);
        requestLayout();
    }

    public boolean isAtMaxZoom() {
        return this.scaleFactor > 2.9999f;
    }

    public boolean isAtMinZoom() {
        return this.scaleFactor < 1.0001f;
    }

    public boolean zoomIn(float factor) {
        boolean res = true;
        float f = this.scaleFactor + factor;
        this.scaleFactor = f;
        if (f > 2.9999f) {
            this.scaleFactor = 3.0f;
            res = false;
        }
        setZoom(this.scaleFactor);
        return res;
    }

    public boolean zoomOut(float factor) {
        boolean res = true;
        float f = this.scaleFactor - factor;
        this.scaleFactor = f;
        if (f < 1.0001f) {
            this.scaleFactor = 1.0f;
            res = false;
        }
        setZoom(this.scaleFactor);
        return res;
    }

    public void setTouchPointerPadding(int widht, int height) {
        this.touchPointerPaddingWidth = widht;
        this.touchPointerPaddingHeight = height;
        requestLayout();
    }

    public int getTouchPointerPaddingWidth() {
        return this.touchPointerPaddingWidth;
    }

    public int getTouchPointerPaddingHeight() {
        return this.touchPointerPaddingHeight;
    }

    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float f = this.scaleFactor;
        setMeasuredDimension(((int) (this.width * f)) + this.touchPointerPaddingWidth, ((int) (this.height * f)) + this.touchPointerPaddingHeight);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.concat(this.scaleMatrix);
        canvas.drawColor(0);
        this.surface.draw(canvas);
        canvas.restore();
    }

    @Override // android.view.View
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return super.dispatchKeyEventPreIme(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MotionEvent mapTouchEvent(MotionEvent event) {
        MotionEvent mappedEvent = MotionEvent.obtain(event);
        float[] coordinates = {mappedEvent.getX(), mappedEvent.getY()};
        this.invScaleMatrix.mapPoints(coordinates);
        mappedEvent.setLocation(coordinates[0], coordinates[1]);
        return mappedEvent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public MotionEvent mapDoubleTouchEvent(MotionEvent event) {
        MotionEvent mappedEvent = MotionEvent.obtain(event);
        float[] coordinates = {(mappedEvent.getX(0) + mappedEvent.getX(1)) / 2.0f, (mappedEvent.getY(0) + mappedEvent.getY(1)) / 2.0f};
        this.invScaleMatrix.mapPoints(coordinates);
        mappedEvent.setLocation(coordinates[0], coordinates[1]);
        return mappedEvent;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        SessionActivity.isExit = false;
        boolean res = this.gestureDetector.onTouchEvent(event);
        return res | this.doubleGestureDetector.onTouchEvent(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
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
            MotionEvent mappedEvent = SessionView.this.mapTouchEvent(e);
            LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getMoveEvent());
            if (this.longPressInProgress) {
                this.longPressInProgress = false;
            }
            SessionView.this.sessionViewListener.onSessionViewBeginTouch();
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onUp(MotionEvent e) {
            MotionEvent mappedEvent = SessionView.this.mapTouchEvent(e);
            if (SessionView.this.isLeftDown) {
                LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, false));
                SessionView.this.isLeftDown = false;
            }
            if (SessionView.this.isMoveDown) {
                LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getTouchUpEvent());
                SessionView.this.isMoveDown = false;
            }
            if (this.longPressInProgress) {
                this.longPressInProgress = false;
            }
            if (this.mouseCliack) {
                LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, false));
                this.mouseCliack = false;
            }
            SessionView.this.sessionViewListener.onSessionViewEndTouch();
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
            SessionView.this.mapTouchEvent(e);
            SessionView.this.sessionViewListener.onSessionViewBeginTouch();
            SessionView.this.isPressMove = false;
            this.longPressInProgress = true;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public void onLongPressUp(MotionEvent e) {
            MotionEvent mappedEvent = SessionView.this.mapTouchEvent(e);
            if (SessionView.this.isLeftDown) {
                LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, false));
                SessionView.this.isLeftDown = false;
            }
            if (!SessionView.this.isPressMove && e.getPointerCount() > 0 && (e.getToolType(e.getActionIndex()) != MotionEvent.TOOL_TYPE_MOUSE || e.getDevice().getName().contains("Touch"))) {
                LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getRightButtonEvent(SessionView.this.mContext, true));
                LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getRightButtonEvent(SessionView.this.mContext, false));
            }
            this.longPressInProgress = false;
            SessionView.this.isPressMove = false;
            SessionView.this.sessionViewListener.onSessionViewEndTouch();
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!this.longPressInProgress) {
                if (e1.getPointerCount() <= 0 || e1.getToolType(e1.getActionIndex()) != MotionEvent.TOOL_TYPE_MOUSE || e1.getButtonState() != 1) {
                    if (!SessionView.this.isMoveDown) {
                        LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) e1.getX(), (int) e1.getY(), Mouse.getTouchDownEvent());
                        LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) e2.getX(), (int) e2.getY(), Mouse.getTouchMoveEvent());
                        SessionView.this.isMoveDown = true;
                        SessionView.this.move_status = true;
                        return true;
                    } else if (SessionView.this.move_status) {
                        LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) e2.getX(), (int) e2.getY(), Mouse.getTouchMoveEvent());
                        SessionView.this.move_status = false;
                        return true;
                    } else {
                        SessionView.this.move_status = true;
                        return false;
                    }
                }
                if (!this.mouseCliack) {
                    MotionEvent mappedEvent = SessionView.this.mapTouchEvent(e1);
                    LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, true));
                    this.mouseCliack = true;
                }
                if (touchViewMove(e1, e2)) {
                    SessionView.this.sessionViewListener.onSessionViewMove((int) e2.getX(), (int) e2.getY());
                }
                return true;
            }
            if (touchViewMove(e1, e2)) {
                MotionEvent mappedEvent2 = SessionView.this.mapTouchEvent(e1);
                if (!SessionView.this.isLeftDown) {
                    LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent2.getX(), (int) mappedEvent2.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, true));
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
            } else {
                MotionEvent mappedEvent = SessionView.this.mapTouchEvent(e);
                SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), true);
                SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), false);
            }
            return true;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.SimpleOnGestureListener, com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            MotionEvent mappedEvent = SessionView.this.mapTouchEvent(e);
            SessionView.this.sessionViewListener.onSessionViewBeginTouch();
            switch (e.getButtonState()) {
                case 2:
                    LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getRightButtonEvent(SessionView.this.mContext, true));
                    LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getRightButtonEvent(SessionView.this.mContext, false));
                    break;
                default:
                    SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), true);
                    SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), false);
                    break;
            }
            SessionView.this.sessionViewListener.onSessionViewEndTouch();
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
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
            MotionEvent mappedEvent = SessionView.this.mapDoubleTouchEvent(e);
            SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), true);
            SessionView.this.sessionViewListener.onSessionViewLeftTouch((int) mappedEvent.getX(), (int) mappedEvent.getY(), false);
            LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, true));
            LibFreeRDP.sendCursorEvent(SessionView.this.currentSession.getInstance(), (int) mappedEvent.getX(), (int) mappedEvent.getY(), Mouse.getLeftButtonEvent(SessionView.this.mContext, false));
            return true;
        }
    }
}
