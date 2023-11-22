package com.xiaomi.mslgrdp.presentation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;

/* loaded from: classes5.dex */
public class MslgOpenFileReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        Log.v("MslgOpenFileReceiver ", "onReceive -- " + intent.getAction());
        if (intent.getAction().equals("com.xiaomi.action.mslgopenextenfile.Broadcast")) {
            String fileurl = intent.getStringExtra("MiRdpFileUrl");
            if (!MultiWindowManager.getSessionManager().getSessions().isEmpty()) {
                SessionActivity.sendPath(true, fileurl, context, MultiWindowManager.getSessionManager().getSessions().get(0));
            }
        }
    }
}