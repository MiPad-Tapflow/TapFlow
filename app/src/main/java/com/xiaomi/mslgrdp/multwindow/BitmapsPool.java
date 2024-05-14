package com.xiaomi.mslgrdp.multwindow;

import android.graphics.Bitmap;
import com.freerdp.freerdpcore.services.LibGraphicsUpdate;
import com.xiaomi.mslgrdp.multwindow.base.Module;
import com.xiaomi.mslgrdp.utils.MslgLogger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BitmapsPool implements Module {
    private Object lock = new Object();
    private volatile Map<Integer, Bitmap> bitmaps = Collections.synchronizedMap(new HashMap());
    private volatile Map<Integer, LibGraphicsUpdate> memoryMap = Collections.synchronizedMap(new HashMap());

    public Bitmap getBitmapWithWinId(int windowId, int width, int height, String file_name, int size) {
        synchronized (this.lock) {
            Bitmap bitmap = this.bitmaps.get(Integer.valueOf(windowId));
            if (bitmap != null && !bitmap.isRecycled()) {
                return bitmap;
            }
            Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            this.bitmaps.put(Integer.valueOf(windowId), bitmap2);
            LibGraphicsUpdate memory = new LibGraphicsUpdate(windowId, file_name, size);
            this.memoryMap.put(Integer.valueOf(windowId), memory);
            return bitmap2;
        }
    }

    public Bitmap getBitmapWithWinId(int windowId) {
        Bitmap bitmap;
        synchronized (this.lock) {
            bitmap = this.bitmaps.get(Integer.valueOf(windowId));
        }
        return bitmap;
    }

    public LibGraphicsUpdate getMemoryWithWinId(int windowId) {
        LibGraphicsUpdate memory;
        synchronized (this.lock) {
            memory = this.memoryMap.get(Integer.valueOf(windowId));
        }
        return memory;
    }

    public void putBitmap(int windowId, Bitmap inbitmap) {
        synchronized (this.lock) {
            if (inbitmap != null) {
                this.bitmaps.put(Integer.valueOf(windowId), inbitmap);
            }
        }
    }

    public void removeBitmap(int windowId) {
        synchronized (this.lock) {
            MslgLogger.LOGD(MslgLogger.TAG_BITMAP, "removeBitmap windowId = " + windowId, false);
            this.bitmaps.remove(Integer.valueOf(windowId));
            LibGraphicsUpdate memory = this.memoryMap.remove(Integer.valueOf(windowId));
            if (memory != null) {
                memory.destroy();
            }
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
        this.bitmaps.clear();
        this.memoryMap.clear();
    }
}