package com.xiaomi.mslgrdp.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.utils.Constances;

/* loaded from: classes5.dex */
public class MslDragLayout extends FrameLayout {
    private View child;
    float downX;
    float downY;
    private float hotAreaHeight;
    private boolean isFullScreen;
    boolean isLongPress;
    private Handler mainHandler;
    float originLeft;
    float originTop;
    private UpdateListener updateListener;

    /* loaded from: classes5.dex */
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
        this.isLongPress = false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.child = getChildAt(0);
        this.hotAreaHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45.0f, getResources().getDisplayMetrics());
    }

    public void setFullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        UpdateListener updateListener;
        if (this.isFullScreen) {
            return super.onTouchEvent(event);
        }
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case 0:
                this.downX = event.getX();
                this.downY = event.getY();
                Log.v("MslDragLayout", "downx =" + this.downX + " downy = " + this.downY);
                MultiWindowManager.isDragging.set(false);
                if (this.mainHandler.hasMessages(Constances.MSG_LONG_PRESS)) {
                    this.mainHandler.removeMessages(Constances.MSG_LONG_PRESS);
                }
                FrameLayout.LayoutParams params1 = (FrameLayout.LayoutParams) this.child.getLayoutParams();
                this.originLeft = params1.leftMargin;
                this.originTop = params1.topMargin;
                if (this.downX > params1.leftMargin && this.downX < params1.leftMargin + params1.width && this.downY > params1.topMargin && this.downY < params1.topMargin + this.hotAreaHeight) {
                    this.mainHandler.sendEmptyMessageDelayed(Constances.MSG_LONG_PRESS, 200L);
                    Log.v("MslDragLayout", "down in hot area");
                }
                return true;
            case 1:
                Log.v("MslDragLayout", "ACTION_UP " + MultiWindowManager.isDragging);
                if (this.mainHandler.hasMessages(Constances.MSG_LONG_PRESS)) {
                    this.mainHandler.removeMessages(Constances.MSG_LONG_PRESS);
                }
                if (MultiWindowManager.isDragging.get() && (updateListener = this.updateListener) != null) {
                    updateListener.onUpdate();
                }
                MultiWindowManager.isDragging.set(false);
                return true;
            case 2:
                float xDistance = event.getX() - this.downX;
                float yDistance = event.getY() - this.downY;
                if (MultiWindowManager.isDragging.get() && ((xDistance != 0.0f || yDistance != 0.0f) && event.getX() >= 0.0f && event.getY() >= 0.0f)) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.child.getLayoutParams();
                    params.leftMargin = (int) (this.originLeft + xDistance);
                    params.topMargin = (int) (this.originTop + yDistance);
                    this.child.setLayoutParams(params);
                    Log.v("MslDragLayout", "move event.getX() =" + event.getX() + " event.getY() = " + event.getY());
                    return true;
                }
                break;
        }
        return false;
    }

    public void setUpdateLocationListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }
}
