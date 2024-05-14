package com.xiaomi.mslgrdp.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.utils.Utils;

/* loaded from: classes6.dex */
public class MslgOpenFileReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.xiaomi.action.mslgopenextenfile.Broadcast")) {
            String fileUrl = intent.getStringExtra("MiRdpFileUrl");
            String openApp = intent.getStringExtra("StarMslgApp");
            Log.v("MslgOpenFileReceiver ", " onReceive -- " + openApp);
            if (MultiWindowManager.getSessionManager().getCurrentSession() != null) {
                Utils.sendPath(true, fileUrl, context, openApp);
            }
        }
    }
}