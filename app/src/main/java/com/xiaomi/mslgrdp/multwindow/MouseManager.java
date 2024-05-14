package com.xiaomi.mslgrdp.multwindow;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PointerIcon;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import cn.ljlVink.Tapflow.R;
import com.xiaomi.mslgrdp.multwindow.base.Module;
import com.xiaomi.mslgrdp.utils.Constances;
import com.xiaomi.mslgrdp.utils.Mouse;

/* loaded from: classes6.dex */
public class MouseManager implements Module {
    public static final String TAG = "MouseManager";
    private Bitmap bitmap_pointer;
    private MouseThreadHandler mouseThreadHandler;

    public MouseThreadHandler getMouseThreadHandler() {
        return this.mouseThreadHandler;
    }

    /* loaded from: classes6.dex */
    public final class MouseThreadHandler extends Handler {
        public MouseThreadHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constances.MSG_HOVER_MOVE /* 10009 */:
                    Log.v(MouseManager.TAG, "MSG_HOVER_MOVE");
                    MotionEvent e = (MotionEvent) msg.obj;
                    try {
                        if (e.getPointerCount() > 0 && e.getToolType(e.getActionIndex()) != MotionEvent.TOOL_TYPE_STYLUS && !e.getDevice().getName().contains("Pen")) {
                            Point p = new Point((int) e.getX(), (int) e.getY());
                            LibFreeRDP.sendCursorEvent(MultiWindowManager.getSessionManager().getCurrentSession().getInstance(), p.x, p.y, Mouse.getMoveEvent());
                            e.recycle();
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

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void init() {
        HandlerThread thread = new HandlerThread("MslgRdpHandler");
        thread.start();
        Looper looper = thread.getLooper();
        this.mouseThreadHandler = new MouseThreadHandler(looper);
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void destroy() {
    }

    public PointerIcon createPointerIcon(Context context, int width, int height, int hotSpotX, int hotSpotY, Bitmap bitmap_pointer) {
        if (width < 64 && height < 64) {
            Resources resources = context.getResources();
            if (width == 9 && height == 15) {
                Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.arrows_v);
                PointerIcon pointerIcon = PointerIcon.create(Bitmap.createScaledBitmap(bitmap, 80, 80, true), 36.0f, 34.0f);
                return pointerIcon;
            }
            if (width == 15 && height == 9) {
                Bitmap bitmap2 = BitmapFactory.decodeResource(resources, R.drawable.arrows_h);
                PointerIcon pointerIcon2 = PointerIcon.create(Bitmap.createScaledBitmap(bitmap2, 80, 80, true), 40.0f, 36.0f);
                return pointerIcon2;
            }
            Bitmap bitmap3 = BitmapFactory.decodeResource(resources, R.drawable.arrow);
            MultiWindowManager.getManager();
            PointerIcon pointerIcon3 = PointerIcon.create(MultiWindowManager.getMouseManager().move_mouse_pixel(bitmap3, 24, 16), 40.0f, 40.0f);
            return pointerIcon3;
        }
        if (hotSpotX == 18 && hotSpotY == 18) {
            PointerIcon pointerIcon4 = PointerIcon.create(bitmap_pointer, 40.0f, 40.0f);
            return pointerIcon4;
        }
        PointerIcon pointerIcon5 = PointerIcon.create(bitmap_pointer, 32.0f, 32.0f);
        return pointerIcon5;
    }

    public Bitmap move_mouse_pixel(Bitmap bitmap, int hotSpotX, int hotSpotY) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if ((hotSpotX == 18 && hotSpotY == 18) || (hotSpotX == 24 && hotSpotY == 16)) {
            width = 80;
            height = 80;
        }
        if (hotSpotX == 0 && hotSpotY == 0) {
            width = 90;
            height = 90;
        }
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int color = bitmap.getPixel(x, y);
                if (Color.alpha(color) != 0) {
                    int newX = ((width / 2) - hotSpotX) + x;
                    int newY = ((height / 2) - hotSpotY) + y;
                    if (newX >= 0 && newX < width && newY >= 0 && newY < height) {
                        newBitmap.setPixel(newX, newY, color);
                    }
                }
            }
        }
        return newBitmap;
    }
}