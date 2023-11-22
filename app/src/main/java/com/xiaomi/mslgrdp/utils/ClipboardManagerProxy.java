package com.xiaomi.mslgrdp.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.nio.charset.StandardCharsets;

/* loaded from: classes5.dex */
public abstract class ClipboardManagerProxy {
    private static final int MAX_STRING_SIZE = 819200;
    private static final String TAG = "ClipboardManagerProxy";

    /* loaded from: classes5.dex */
    public interface OnClipboardChangedListener {
        void onClipboardChanged(String str);
    }

    public abstract void addClipboardChangedListener(OnClipboardChangedListener onClipboardChangedListener);

    public abstract void removeClipboardboardChangedListener(OnClipboardChangedListener onClipboardChangedListener);

    public abstract boolean setClipboardData(String str);

    public static ClipboardManagerProxy getClipboardManager(Context ctx) {
        if (Build.VERSION.SDK_INT < 11) {
            return new PreHCClipboardManager(ctx);
        }
        return new HCClipboardManager(ctx);
    }

    /* loaded from: classes5.dex */
    private static class PreHCClipboardManager extends ClipboardManagerProxy {
        public PreHCClipboardManager(Context ctx) {
        }

        @Override // com.xiaomi.mslgrdp.utils.ClipboardManagerProxy
        public boolean setClipboardData(String data) {
            return false;
        }

        @Override // com.xiaomi.mslgrdp.utils.ClipboardManagerProxy
        public void addClipboardChangedListener(OnClipboardChangedListener listener) {
        }

        @Override // com.xiaomi.mslgrdp.utils.ClipboardManagerProxy
        public void removeClipboardboardChangedListener(OnClipboardChangedListener listener) {
        }
    }

    /* loaded from: classes5.dex */
    private static class HCClipboardManager extends ClipboardManagerProxy implements ClipboardManager.OnPrimaryClipChangedListener {
        private ClipboardManager mClipboardManager;
        private OnClipboardChangedListener mListener;

        public HCClipboardManager(Context ctx) {
            this.mClipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        }

        @Override // com.xiaomi.mslgrdp.utils.ClipboardManagerProxy
        public boolean setClipboardData(String data) {
            boolean result = false;
            int size = data == null ? 0 : data.getBytes(StandardCharsets.UTF_8).length;
            if (size > ClipboardManagerProxy.MAX_STRING_SIZE) {
                data = data.substring(0, 204800);
                Log.i(ClipboardManagerProxy.TAG, "TransactionTooLarge, old = " + size + ", new = " + data.getBytes(StandardCharsets.UTF_8).length);
                result = true;
            }
            this.mClipboardManager.setPrimaryClip(ClipData.newPlainText("rdp-clipboard", data == null ? "" : data));
            return result;
        }

        @Override // android.content.ClipboardManager.OnPrimaryClipChangedListener
        public void onPrimaryClipChanged() {
            OnClipboardChangedListener onClipboardChangedListener;
            CharSequence cs;
            ClipData clip = this.mClipboardManager.getPrimaryClip();
            String data = null;
            CharSequence label = null;
            if (clip != null) {
                label = clip.getDescription().getLabel();
                if (clip.getItemCount() > 0 && (cs = clip.getItemAt(0).getText()) != null) {
                    data = cs.toString();
                }
            }
            if ((label == null || !label.equals("rdp-clipboard")) && (onClipboardChangedListener = this.mListener) != null) {
                onClipboardChangedListener.onClipboardChanged(data);
            }
        }

        @Override // com.xiaomi.mslgrdp.utils.ClipboardManagerProxy
        public void addClipboardChangedListener(OnClipboardChangedListener listener) {
            this.mListener = listener;
            this.mClipboardManager.addPrimaryClipChangedListener(this);
        }

        @Override // com.xiaomi.mslgrdp.utils.ClipboardManagerProxy
        public void removeClipboardboardChangedListener(OnClipboardChangedListener listener) {
            this.mListener = null;
            this.mClipboardManager.removePrimaryClipChangedListener(this);
        }
    }
}
