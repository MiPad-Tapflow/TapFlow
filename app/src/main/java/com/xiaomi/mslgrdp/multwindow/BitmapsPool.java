package com.xiaomi.mslgrdp.multwindow;

import android.graphics.Bitmap;
import com.xiaomi.mslgrdp.multwindow.base.Module;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes5.dex */
public class BitmapsPool implements Module {
    private Object lock = new Object();
    private volatile Map<Integer, Bitmap> bitmaps = Collections.synchronizedMap(new HashMap());

    public Bitmap obtainBitmap(int windowId, int width, int height) {
        synchronized (this.lock) {
            Bitmap bitmap = this.bitmaps.get(Integer.valueOf(windowId));
            if (bitmap != null && !bitmap.isRecycled()) {
                return bitmap;
            }
            Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.bitmaps.put(Integer.valueOf(windowId), bitmap2);
            return bitmap2;
        }
    }

    public void removeBitmap(int windowId) {
        synchronized (this.lock) {
            this.bitmaps.remove(Integer.valueOf(windowId));
        }
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void init() {
    }

    @Override // com.xiaomi.mslgrdp.multwindow.base.Module
    public void destroy() {
        this.bitmaps.clear();
    }
}