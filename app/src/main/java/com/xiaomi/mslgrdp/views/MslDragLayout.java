package com.xiaomi.mslgrdp.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.xiaomi.mslgrdp.domain.MslSurfaceInfo;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.MslgLogger;

public class MslDragLayout extends FrameLayout {
    private View child;
    float downX;
    float downY;
    private float hotAreaHeight;
    private boolean isFullScreen;
    private Handler mainHandler;
    float originLeft;
    float originTop;
    private float rightAreaWidth;
    private MslSurfaceInfo surfaceInfo;
    private UpdateListener updateListener;
    public interface UpdateListener {
        void onUpdate();
    }

    public MslDragLayout(Context context) {
        this(context, null);
    }

    public MslDragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MslDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mainHandler = MultiWindowManager.getManager().getMainHandler();
        this.isFullScreen = false;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.hotAreaHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45.0f, getResources().getDisplayMetrics());
        this.rightAreaWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90.0f, getResources().getDisplayMetrics());
    }

    public void setDragView(View child, MslSurfaceInfo surfaceInfo) {
        this.child = child;
        this.surfaceInfo = surfaceInfo;
    }

    public void setFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    public boolean isFullScreen() {
        return this.isFullScreen;
    }
    private boolean isInHotArea(FrameLayout.LayoutParams params, MotionEvent event) {
        return (this.downX > params.leftMargin && this.downX < (params.leftMargin + params.width - this.rightAreaWidth) &&
                this.downY > params.topMargin && this.downY < (params.topMargin + this.hotAreaHeight));
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = this.child;

        if (view == null || view.getLayoutParams() == null || this.isFullScreen) {
            return super.dispatchTouchEvent(event);
        }

        FrameLayout.LayoutParams paramsTemp = (FrameLayout.LayoutParams) this.child.getLayoutParams();

        if (paramsTemp.width >= (Constances.SCREEN_WIDTH * 0.5d)) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    this.downX = event.getX();
                    this.downY = event.getY();
                    MslgLogger.LOGD("MslDragLayout", "downx =" + this.downX + " downy = " + this.downY, false);
                    MultiWindowManager.isDragging.set(false);

                    if (this.mainHandler.hasMessages(Constances.MSG_LONG_PRESS)) {
                        this.mainHandler.removeMessages(Constances.MSG_LONG_PRESS);
                    }

                    FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) this.child.getLayoutParams();
                    this.originLeft = params1.leftMargin;
                    this.originTop = params1.topMargin;

                    if (this.downX > params1.leftMargin &&
                            this.downX < (params1.leftMargin + params1.width - this.rightAreaWidth) &&
                            this.downY > params1.topMargin &&
                            this.downY < (params1.topMargin + this.hotAreaHeight)) {

                        this.mainHandler.sendEmptyMessageDelayed(Constances.MSG_LONG_PRESS, 200);
                        MslgLogger.LOGD("MslDragLayout", "down in hot area", false);
                        super.dispatchTouchEvent(event);
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    float xDistance = event.getX() - this.downX;
                    float yDistance = event.getY() - this.downY;

                    if (MultiWindowManager.isDragging.get() && (xDistance != 0.0f || yDistance != 0.0f) &&
                            event.getX() >= 0.0f && event.getY() >= 0.0f) {

                        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.child.getLayoutParams();
                        params.leftMargin = (int) (this.originLeft + xDistance);
                        params.topMargin = (int) (this.originTop + yDistance);
                        this.child.setLayoutParams(params);

                        MslgLogger.LOGD("MslDragLayout", "move event.getX() =" + event.getX() + " event.getY() = " + event.getY(), false);
                        return true;
                    }
                    break;

                default:
                    if (event.getX() >= 0.0f && event.getY() >= 0.0f) {
                        FrameLayout.LayoutParams params2 = (FrameLayout.LayoutParams) this.child.getLayoutParams();
                        int left = params2.leftMargin;
                        int top = params2.topMargin;

                        if (inBitmapArea(event, left, top)) {
                            event.offsetLocation(this.surfaceInfo.x - left, this.surfaceInfo.y - top);
                        } else if (inWpsBitmapArea(event)) {
                            return true;
                        }
                    }
                    break;
            }
        }

        return super.dispatchTouchEvent(event);
    }

    private boolean inBitmapArea(MotionEvent event, int left, int top) {
        if (this.surfaceInfo == null || event.getX() < left || event.getX() > this.surfaceInfo.width + left || event.getY() < top || event.getY() > this.surfaceInfo.height + top) {
            return false;
        }
        MslgLogger.LOGD("MslDragLayout.smali", "inBitmapArea -- ", false);
        return true;
    }

    private boolean inWpsBitmapArea(MotionEvent event) {
        if (this.surfaceInfo == null || event.getX() < this.surfaceInfo.x || event.getX() > this.surfaceInfo.x + this.surfaceInfo.width || event.getY() < this.surfaceInfo.y || event.getY() > this.surfaceInfo.y + this.surfaceInfo.height) {
        return false;
        }
        MslgLogger.LOGD("MslDragLayout.smali", "inWpsBitmapArea -- ", false);
        return true;
    }

    public void setUpdateLocationListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }
}
