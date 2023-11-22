package com.xiaomi.mslgrdp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

/* loaded from: classes5.dex */
public class GestureDetector {
    private static final int DOUBLE_TAP_SLOP = 100;
    private static final int DOUBLE_TAP_TIMEOUT = 200;
    private static final int LARGE_TOUCH_SLOP = 18;
    private static final int LONG_PRESS = 2;
    private static final int SHOW_PRESS = 1;
    private static final int TAP = 3;
    private static final int TAP_TIMEOUT = 100;
    private boolean mAlwaysInBiggerTapRegion;
    private boolean mAlwaysInTapRegion;
    private MotionEvent mCurrentDownEvent;
    private OnDoubleTapListener mDoubleTapListener;
    private int mDoubleTapSlopSquare;
    private final Handler mHandler;
    private boolean mIgnoreMultitouch;
    private boolean mInLongPress;
    private boolean mIsDoubleTapping;
    private boolean mIsLongpressEnabled;
    private int mLargeTouchSlopSquare;
    private float mLastMotionX;
    private float mLastMotionY;
    private final OnGestureListener mListener;
    private int mLongpressTimeout;
    private MotionEvent mPreviousUpEvent;
    private boolean mStillDown;
    private int mTouchSlopSquare;

    /* loaded from: classes5.dex */
    public interface OnDoubleTapListener {
        boolean onDoubleTap(MotionEvent motionEvent);

        boolean onDoubleTapEvent(MotionEvent motionEvent);

        boolean onSingleTapConfirmed(MotionEvent motionEvent);
    }

    /* loaded from: classes5.dex */
    public interface OnGestureListener {
        boolean onDown(MotionEvent motionEvent);

        void onLongPress(MotionEvent motionEvent);

        void onLongPressUp(MotionEvent motionEvent);

        boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2);

        void onShowPress(MotionEvent motionEvent);

        boolean onSingleTapUp(MotionEvent motionEvent);

        boolean onUp(MotionEvent motionEvent);
    }

    public GestureDetector(Context context, OnGestureListener listener) {
        this(context, listener, null);
    }

    public GestureDetector(Context context, OnGestureListener listener, Handler handler) {
        this(context, listener, handler, context != null && context.getApplicationInfo().targetSdkVersion >= 8);
    }

    public GestureDetector(Context context, OnGestureListener listener, Handler handler, boolean ignoreMultitouch) {
        this.mLongpressTimeout = 200;
        if (handler != null) {
            this.mHandler = new GestureHandler(handler);
        } else {
            this.mHandler = new GestureHandler();
        }
        this.mListener = listener;
        if (listener instanceof OnDoubleTapListener) {
            setOnDoubleTapListener((OnDoubleTapListener) listener);
        }
        init(context, ignoreMultitouch);
    }

    private void init(Context context, boolean ignoreMultitouch) {
        int touchSlop;
        int largeTouchSlop;
        int doubleTapSlop;
        if (this.mListener == null) {
            throw new NullPointerException("OnGestureListener must not be null");
        }
        this.mIsLongpressEnabled = true;
        this.mIgnoreMultitouch = ignoreMultitouch;
        if (context == null) {
            touchSlop = ViewConfiguration.getTouchSlop();
            largeTouchSlop = touchSlop + 2;
            doubleTapSlop = 100;
        } else {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            float density = metrics.density;
            ViewConfiguration configuration = ViewConfiguration.get(context);
            int touchSlop2 = configuration.getScaledTouchSlop();
            int largeTouchSlop2 = (int) ((18.0f * density) + 0.5f);
            touchSlop = touchSlop2;
            largeTouchSlop = largeTouchSlop2;
            doubleTapSlop = configuration.getScaledDoubleTapSlop();
        }
        this.mTouchSlopSquare = touchSlop * touchSlop;
        this.mLargeTouchSlopSquare = largeTouchSlop * largeTouchSlop;
        this.mDoubleTapSlopSquare = doubleTapSlop * doubleTapSlop;
    }

    public void setOnDoubleTapListener(OnDoubleTapListener onDoubleTapListener) {
        this.mDoubleTapListener = onDoubleTapListener;
    }

    public void setIsLongpressEnabled(boolean isLongpressEnabled) {
        this.mIsLongpressEnabled = isLongpressEnabled;
    }

    public boolean isLongpressEnabled() {
        return this.mIsLongpressEnabled;
    }

    public void setLongPressTimeout(int timeout) {
        this.mLongpressTimeout = timeout;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        MotionEvent motionEvent;
        int action = ev.getAction();
        float y = ev.getY();
        float x = ev.getX();
        boolean handled = false;
        switch (action & 255) {
            case 0:
                if (this.mDoubleTapListener != null) {
                    boolean hadTapMessage = this.mHandler.hasMessages(3);
                    if (hadTapMessage) {
                        this.mHandler.removeMessages(3);
                    }
                    MotionEvent motionEvent2 = this.mCurrentDownEvent;
                    if (motionEvent2 != null && (motionEvent = this.mPreviousUpEvent) != null && hadTapMessage && isConsideredDoubleTap(motionEvent2, motionEvent, ev)) {
                        this.mIsDoubleTapping = true;
                        handled = false | this.mDoubleTapListener.onDoubleTap(this.mCurrentDownEvent) | this.mDoubleTapListener.onDoubleTapEvent(ev);
                    } else {
                        this.mHandler.sendEmptyMessageDelayed(3, 200L);
                    }
                }
                this.mLastMotionX = x;
                this.mLastMotionY = y;
                MotionEvent motionEvent3 = this.mCurrentDownEvent;
                if (motionEvent3 != null) {
                    motionEvent3.recycle();
                }
                this.mCurrentDownEvent = MotionEvent.obtain(ev);
                this.mAlwaysInTapRegion = true;
                this.mAlwaysInBiggerTapRegion = true;
                this.mStillDown = true;
                this.mInLongPress = false;
                if (this.mIsLongpressEnabled) {
                    if (ev.getSource() == 8194) {
                        this.mLongpressTimeout = 300;
                    } else {
                        this.mLongpressTimeout = 200;
                    }
                    this.mHandler.removeMessages(2);
                    this.mHandler.sendEmptyMessageAtTime(2, this.mCurrentDownEvent.getDownTime() + 100 + this.mLongpressTimeout);
                }
                this.mHandler.sendEmptyMessageAtTime(1, this.mCurrentDownEvent.getDownTime() + 100);
                return handled | this.mListener.onDown(ev);
            case 1:
                this.mStillDown = false;
                MotionEvent currentUpEvent = MotionEvent.obtain(ev);
                if (this.mIsDoubleTapping) {
                    handled = false | this.mDoubleTapListener.onDoubleTapEvent(ev);
                } else if (this.mInLongPress) {
                    this.mHandler.removeMessages(3);
                    this.mListener.onLongPressUp(ev);
                    this.mInLongPress = false;
                } else if (this.mAlwaysInTapRegion) {
                    handled = this.mListener.onSingleTapUp(this.mCurrentDownEvent);
                }
                MotionEvent motionEvent4 = this.mPreviousUpEvent;
                if (motionEvent4 != null) {
                    motionEvent4.recycle();
                }
                this.mPreviousUpEvent = currentUpEvent;
                this.mIsDoubleTapping = false;
                this.mHandler.removeMessages(1);
                this.mHandler.removeMessages(2);
                return handled | this.mListener.onUp(ev);
            case 2:
                if (this.mIgnoreMultitouch && ev.getPointerCount() > 1) {
                    return false;
                }
                float scrollX = this.mLastMotionX - x;
                float scrollY = this.mLastMotionY - y;
                if (this.mIsDoubleTapping) {
                    return false | this.mDoubleTapListener.onDoubleTapEvent(ev);
                }
                if (this.mAlwaysInTapRegion) {
                    int deltaX = (int) (x - this.mCurrentDownEvent.getX());
                    int deltaY = (int) (y - this.mCurrentDownEvent.getY());
                    int distance = (deltaX * deltaX) + (deltaY * deltaY);
                    if (distance > this.mTouchSlopSquare) {
                        this.mLastMotionX = x;
                        this.mLastMotionY = y;
                        this.mAlwaysInTapRegion = false;
                        this.mHandler.removeMessages(3);
                        this.mHandler.removeMessages(1);
                        this.mHandler.removeMessages(2);
                    }
                    if (distance <= this.mLargeTouchSlopSquare) {
                        return false;
                    }
                    this.mAlwaysInBiggerTapRegion = false;
                    return false;
                } else if (Math.abs(scrollX) < 1.0f && Math.abs(scrollY) < 1.0f) {
                    return false;
                } else {
                    boolean handled2 = this.mListener.onScroll(this.mCurrentDownEvent, ev, scrollX, scrollY);
                    this.mLastMotionX = x;
                    this.mLastMotionY = y;
                    return handled2;
                }
            case 3:
                cancel();
                return false;
            case 4:
            default:
                return false;
            case 5:
                if (!this.mIgnoreMultitouch) {
                    return false;
                }
                cancel();
                return false;
            case 6:
                if (!this.mIgnoreMultitouch || ev.getPointerCount() != 2) {
                    return false;
                }
                int index = ((65280 & action) >> 8) == 0 ? 1 : 0;
                this.mLastMotionX = ev.getX(index);
                this.mLastMotionY = ev.getY(index);
                return false;
        }
    }

    private void cancel() {
        this.mHandler.removeMessages(1);
        this.mHandler.removeMessages(2);
        this.mHandler.removeMessages(3);
        this.mAlwaysInTapRegion = false;
        this.mIsDoubleTapping = false;
        this.mStillDown = false;
        if (this.mInLongPress) {
            this.mInLongPress = false;
        }
    }

    private boolean isConsideredDoubleTap(MotionEvent firstDown, MotionEvent firstUp, MotionEvent secondDown) {
        if (this.mAlwaysInBiggerTapRegion && secondDown.getEventTime() - firstUp.getEventTime() <= 200) {
            int deltaX = ((int) firstDown.getX()) - ((int) secondDown.getX());
            int deltaY = ((int) firstDown.getY()) - ((int) secondDown.getY());
            return (deltaX * deltaX) + (deltaY * deltaY) < this.mDoubleTapSlopSquare;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchLongPress() {
        this.mHandler.removeMessages(3);
        this.mInLongPress = true;
        this.mListener.onLongPress(this.mCurrentDownEvent);
    }

    /* loaded from: classes5.dex */
    public static class SimpleOnGestureListener implements OnGestureListener, OnDoubleTapListener {
        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public void onLongPress(MotionEvent e) {
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public void onLongPressUp(MotionEvent e) {
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public void onShowPress(MotionEvent e) {
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnGestureListener
        public boolean onUp(MotionEvent e) {
            return false;
        }

        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnDoubleTapListener
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.utils.GestureDetector.OnDoubleTapListener
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }
    }

    /* loaded from: classes5.dex */
    private class GestureHandler extends Handler {
        GestureHandler() {
        }

        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    GestureDetector.this.mListener.onShowPress(GestureDetector.this.mCurrentDownEvent);
                    return;
                case 2:
                    GestureDetector.this.dispatchLongPress();
                    return;
                case 3:
                    if (GestureDetector.this.mDoubleTapListener != null && !GestureDetector.this.mStillDown) {
                        GestureDetector.this.mDoubleTapListener.onSingleTapConfirmed(GestureDetector.this.mCurrentDownEvent);
                        return;
                    }
                    return;
                default:
                    throw new RuntimeException("Unknown message " + msg);
            }
        }
    }
}
