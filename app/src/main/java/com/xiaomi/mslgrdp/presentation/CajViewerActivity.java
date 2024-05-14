package com.xiaomi.mslgrdp.presentation;

import android.os.Bundle;
import android.os.Environment;
import com.xiaomi.mslgrdp.presentation.SessionView;
import java.io.File;

/* loaded from: classes6.dex */
public class CajViewerActivity extends LinuxVirtualActivity implements SessionView.SessionViewListener {
    private final String sdcardAbsPath = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File hyperEngineDir = new File(this.sdcardAbsPath + "/HyperEngine");
        if (!hyperEngineDir.exists()) {
            hyperEngineDir.mkdirs();
        }
    }
}