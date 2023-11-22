package com.xiaomi.mslgrdp.multwindow.base;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.xiaomi.mslgrdp.multwindow.LinuxWindowActivity;
import com.xiaomi.mslgrdp.multwindow.MultiWindowManager;
import com.xiaomi.mslgrdp.views.MslDragLayout;

/* loaded from: classes5.dex */
public abstract class BaseActivity extends AppCompatActivity implements ISurface {
    protected MslDragLayout container_layout;
    protected ImageView iv_content;
    public Bitmap mBitmap;
    protected int windowId = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("WPS Office");
        getWindow().requestFeature(12);
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(134217728);
    }


    @Override
    public void onResume() {
        super.onResume();
        View mDecor = getWindow().getDecorView();
        mDecor.setSystemUiVisibility(4098);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        View mDecor = getWindow().getDecorView();
        mDecor.setSystemUiVisibility(4098);
    }

    @Override
    public void closeWindow(int windowId) {
        if (this instanceof LinuxWindowActivity) {
            MultiWindowManager.getManager().removeSurface(windowId);
            finish();
        }
    }

    public void updateWindow(int windowId, int x, int y, int width, int height) {
    }

    public void refreshContent() {
    }

    public Rect getRegion() {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}