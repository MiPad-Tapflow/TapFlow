package com.xiaomi.mslgrdp.presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.xiaomi.mslgrdp.multwindow.KeyboardMapperManager;

@SuppressLint("AppCompatCustomView")
public class MslEditText extends EditText {
    private static final String TAG = "MslEditText";

    public MslEditText(Context context) {
        super(context);
    }

    public MslEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MslEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MslEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.view.View
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if ((event.isCtrlPressed() || event.isAltPressed()) && KeyboardMapperManager.getManager().getKeyboardMapper() != null) {
            return KeyboardMapperManager.getManager().getKeyboardMapper().processAndroidKeyEvent(event);
        }
        return false;
    }

    @Override // android.widget.TextView, android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == 66 || keyCode == 112 || keyCode == 19 || keyCode == 20 || keyCode == 22 || keyCode == 21) && KeyboardMapperManager.getManager().getKeyboardMapper() != null) {
            return KeyboardMapperManager.getManager().getKeyboardMapper().processAndroidKeyEvent(event);
        }
        return false;
    }
}