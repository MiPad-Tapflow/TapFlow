package com.xiaomi.mslgrdp.presentation;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class WPSSplashActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        intent.setComponent(new ComponentName(this, (Class<?>) LinuxVirtualActivity.class));
        startActivity(intent);
        finish();
    }
}