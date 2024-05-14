package com.xiaomi.mslgrdp.utils;

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;

public class DoubleGestureDetector {
    private static final long DOUBLE_TOUCH_TIMEOUT = 100;
    private static final int MODE_PINCH_ZOOM = 1;
    private static final int MODE_SCROLL = 2;
    private static final int MODE_UNKNOWN = 0;
    private static final int SCROLL_SCORE_TO_REACH = 20;
    private static final long SINGLE_DOUBLE_TOUCH_TIMEOUT = 1000;
    private static final int TAP = 1;
    private boolean mCancelDetection;
    private MotionEvent mCurrentDoubleDownEvent;
    private MotionEvent mCurrentDownEvent;
    private int mCurrentMode;
    private boolean mDoubleInProgress;
    private GestureHandler mHandler;
    private final OnDoubleGestureListener mListener;
    private int mPointerDistanceSquare;
    private MotionEvent mPreviousPointerUpEvent;
    private MotionEvent mPreviousUpEvent;
    private int mScrollDetectionScore;


    public interface OnDoubleGestureListener {
        boolean onDoubleTouchDown(MotionEvent motionEvent);

        boolean onDoubleTouchScroll(MotionEvent motionEvent, MotionEvent motionEvent2);

        boolean onDoubleTouchSingleTap(MotionEvent motionEvent);

        boolean onDoubleTouchUp(MotionEvent motionEvent);
    }

    public DoubleGestureDetector(Context context, Handler handler, OnDoubleGestureListener listener) {
        this.mListener = listener;
        init(context, handler);
    }

    private void init(Context context, Handler handler) {
        if (this.mListener == null) {
            throw new NullPointerException("OnGestureListener must not be null");
        }
        if (handler != null) {
            this.mHandler = new GestureHandler(handler);
        } else {
            this.mHandler = new GestureHandler();
        }
        float distPixelsX = context.getResources().getDisplayMetrics().xdpi * 0.19685039f;
        float distPixelsY = context.getResources().getDisplayMetrics().ydpi * 0.19685039f;
        this.mPointerDistanceSquare = (int) ((distPixelsX * distPixelsX) + (distPixelsY * distPixelsY));
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        int action = ev.getAction();
        switch (action & 255) {
            case 0:
                MotionEvent motionEvent = this.mCurrentDownEvent;
                if (motionEvent != null) {
                    motionEvent.recycle();
                }
                this.mCurrentMode = 0;
                this.mCurrentDownEvent = MotionEvent.obtain(ev);
                this.mCancelDetection = false;
                this.mDoubleInProgress = false;
                this.mScrollDetectionScore = 0;
                handled = true;
                break;
            case 1:
                if (this.mPreviousPointerUpEvent != null && ev.getEventTime() - this.mPreviousPointerUpEvent.getEventTime() > DOUBLE_TOUCH_TIMEOUT) {
                    this.mPreviousPointerUpEvent.recycle();
                    this.mPreviousPointerUpEvent = null;
                    cancel();
                    break;
                } else if (!this.mCancelDetection && this.mDoubleInProgress) {
                    boolean hasTapEvent = this.mHandler.hasMessages(1);
                    MotionEvent currentUpEvent = MotionEvent.obtain(ev);
                    if (this.mCurrentMode == 0 && hasTapEvent) {
                        handled = this.mListener.onDoubleTouchSingleTap(this.mCurrentDoubleDownEvent);
                    }
                    MotionEvent motionEvent2 = this.mPreviousUpEvent;
                    if (motionEvent2 != null) {
                        motionEvent2.recycle();
                    }
                    this.mPreviousUpEvent = currentUpEvent;
                    handled |= this.mListener.onDoubleTouchUp(ev);
                    break;
                }
                break;
            case 2:
                if (!this.mCancelDetection && this.mDoubleInProgress && ev.getPointerCount() == 2) {
                    if (this.mCurrentMode == 0) {
                        if (pointerDistanceChanged(this.mCurrentDoubleDownEvent, ev)) {
                            MotionEvent e = MotionEvent.obtain(ev);
                            e.setAction(this.mCurrentDoubleDownEvent.getAction());
                            e.recycle();
                            this.mCurrentMode = 1;
                            break;
                        } else {
                            int i = this.mScrollDetectionScore + 1;
                            this.mScrollDetectionScore = i;
                            if (i >= 20) {
                                this.mCurrentMode = 2;
                            }
                        }
                    }
                    switch (this.mCurrentMode) {
                        case 2:
                            handled = this.mListener.onDoubleTouchScroll(this.mCurrentDownEvent, ev);
                            break;
                        default:
                            handled = true;
                            break;
                    }
                }
                break;
            case 3:
                cancel();
                break;
            case 5:
                if (ev.getPointerCount() > 2 || ev.getEventTime() - this.mCurrentDownEvent.getEventTime() > DOUBLE_TOUCH_TIMEOUT) {
                    cancel();
                    break;
                } else if (!this.mCancelDetection) {
                    this.mDoubleInProgress = true;
                    MotionEvent motionEvent3 = this.mCurrentDoubleDownEvent;
                    if (motionEvent3 != null) {
                        motionEvent3.recycle();
                    }
                    this.mCurrentDoubleDownEvent = MotionEvent.obtain(ev);
                    this.mCurrentMode = 0;
                    this.mHandler.sendEmptyMessageDelayed(1, SINGLE_DOUBLE_TOUCH_TIMEOUT);
                    handled = false | this.mListener.onDoubleTouchDown(ev);
                    break;
                }
                break;
            case 6:
                MotionEvent motionEvent4 = this.mPreviousPointerUpEvent;
                if (motionEvent4 != null) {
                    motionEvent4.recycle();
                }
                this.mPreviousPointerUpEvent = MotionEvent.obtain(ev);
                break;
        }
        if (action == 2 && !handled) {
            return true;
        }
        return handled;
    }

    private void cancel() {
        this.mHandler.removeMessages(1);
        this.mCurrentMode = 0;
        this.mCancelDetection = true;
        this.mDoubleInProgress = false;
    }

    private boolean pointerDistanceChanged(MotionEvent oldEvent, MotionEvent newEvent) {
        int deltaX1 = Math.abs(((int) oldEvent.getX(0)) - ((int) oldEvent.getX(1)));
        int deltaX2 = Math.abs(((int) newEvent.getX(0)) - ((int) newEvent.getX(1)));
        int distXSquare = (deltaX2 - deltaX1) * (deltaX2 - deltaX1);
        int deltaY1 = Math.abs(((int) oldEvent.getY(0)) - ((int) oldEvent.getY(1)));
        int deltaY2 = Math.abs(((int) newEvent.getY(0)) - ((int) newEvent.getY(1)));
        int distYSquare = (deltaY2 - deltaY1) * (deltaY2 - deltaY1);
        return distXSquare + distYSquare > this.mPointerDistanceSquare;
    }

    public class GestureHandler extends Handler {
        GestureHandler() {
        }

        GestureHandler(Handler handler) {
            super(handler.getLooper());
        }
    }
}
