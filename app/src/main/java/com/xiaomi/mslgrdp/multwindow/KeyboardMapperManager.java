package com.xiaomi.mslgrdp.multwindow;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import com.freerdp.freerdpcore.services.LibFreeRDP;
import com.xiaomi.mslgrdp.application.GlobalApp;
import com.xiaomi.mslgrdp.multwindow.imp.KeyProcessingListenerAdapter;
import com.xiaomi.mslgrdp.utils.KeyboardMapper;

/* loaded from: classes6.dex */
public class KeyboardMapperManager {
    private static final int FIRST_CHARACTER_IN_ASCII = 32;
    private static final int LAST_CHARACTER_IN_ASCII = 126;
    private static final KeyboardMapperManager manager = new KeyboardMapperManager();
    private EditText mEditText;
    private EditTextWatcher mEditWatcher;
    private int inputCount = 0;
    private KeyboardProcessListener keyboardProcessListener = new KeyboardProcessListener();
    private String TAG = "KeyboardMapperManager";
    private KeyboardMapper keyboardMapper = null;

    static /* synthetic */ int access$208(KeyboardMapperManager x0) {
        int i = x0.inputCount;
        x0.inputCount = i + 1;
        return i;
    }

    private KeyboardMapperManager() {
    }

    public static KeyboardMapperManager getManager() {
        return manager;
    }

    public KeyboardMapper getKeyboardMapper() {
        return this.keyboardMapper;
    }

    public EditTextWatcher getEditWatcher() {
        return this.mEditWatcher;
    }

    public void setEditText(EditText editText) {
        this.mEditText = editText;
    }

    public KeyboardMapper init() {
        KeyboardMapper keyboardMapper = new KeyboardMapper();
        this.keyboardMapper = keyboardMapper;
        keyboardMapper.init(GlobalApp.getApplication());
        this.keyboardMapper.reset(this.keyboardProcessListener);
        this.mEditWatcher = new EditTextWatcher();
        return this.keyboardMapper;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes6.dex */
    public class KeyboardProcessListener extends KeyProcessingListenerAdapter {
        KeyboardProcessListener() {
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.KeyProcessingListenerAdapter, com.xiaomi.mslgrdp.utils.KeyboardMapper.KeyProcessingListener
        public void processVirtualKey(int virtualKeyCode, boolean down) {
            if (MultiWindowManager.getSessionManager().getCurrentSession() == null) {
                return;
            }
            SessionState session = MultiWindowManager.getSessionManager().getCurrentSession();
            LibFreeRDP.sendKeyEvent(session.getInstance(), virtualKeyCode, down);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.KeyProcessingListenerAdapter, com.xiaomi.mslgrdp.utils.KeyboardMapper.KeyProcessingListener
        public void processUnicodeKey(int unicodeKey) {
            if (MultiWindowManager.getSessionManager().getCurrentSession() == null) {
                return;
            }
            SessionState session = MultiWindowManager.getSessionManager().getCurrentSession();
            LibFreeRDP.sendUnicodeKeyEvent(session.getInstance(), unicodeKey, true);
            LibFreeRDP.sendUnicodeKeyEvent(session.getInstance(), unicodeKey, false);
        }

        @Override // com.xiaomi.mslgrdp.multwindow.imp.KeyProcessingListenerAdapter, com.xiaomi.mslgrdp.utils.KeyboardMapper.KeyProcessingListener
        public void sendStringTo(String data) {
            MultiWindowManager.getManager().sendString(data);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes6.dex */
    public class EditTextWatcher implements TextWatcher {
        EditTextWatcher() {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int end = start + count;
            try {
                if (before > count) {
                    KeyboardMapperManager.this.keyboardProcessListener.processVirtualKey(8, true);
                    KeyboardMapperManager.this.keyboardProcessListener.processVirtualKey(8, false);
                    return;
                }
                CharSequence str = s.subSequence(start + before, end);
                if (count != 1 || ' ' > str.charAt(0) || str.charAt(0) > '~') {
                    KeyboardMapperManager.this.keyboardProcessListener.processVirtualKey(KeyboardMapper.KEY_A_TO_U, true);
                    KeyboardMapperManager.this.keyboardProcessListener.sendStringTo(str.toString());
                    KeyboardMapperManager.this.keyboardProcessListener.processVirtualKey(KeyboardMapper.KEY_A_TO_U, false);
                    return;
                }
                int vk_code = KeyboardMapper.asciiToVirtualKey(str.charAt(0));
                boolean needShift = KeyboardMapper.needShiftKey(str.charAt(0));
                if (needShift) {
                    KeyboardMapperManager.this.keyboardProcessListener.processVirtualKey(160, true);
                    KeyboardMapper.send_delay(10);
                }
                KeyboardMapperManager.this.keyboardProcessListener.processVirtualKey(vk_code, true);
                KeyboardMapper.send_delay(10);
                KeyboardMapperManager.this.keyboardProcessListener.processVirtualKey(vk_code, false);
                if (needShift) {
                    KeyboardMapper.send_delay(10);
                    KeyboardMapperManager.this.keyboardProcessListener.processVirtualKey(160, false);
                }
            } catch (Exception e) {
                Log.e(KeyboardMapperManager.this.TAG, "onTextChanged:  s = " + s.length() + ", start= " + (start + before) + ", end= " + (start + count));
            }
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable s) {
            if (KeyboardMapperManager.this.inputCount > 20) {
                if (KeyboardMapperManager.this.mEditText != null) {
                    KeyboardMapperManager.this.mEditText.removeTextChangedListener(KeyboardMapperManager.this.mEditWatcher);
                    KeyboardMapperManager.this.mEditText.setText(" ");
                    KeyboardMapperManager.this.mEditText.addTextChangedListener(KeyboardMapperManager.this.mEditWatcher);
                }
                KeyboardMapperManager.this.inputCount = 0;
                return;
            }
            KeyboardMapperManager.access$208(KeyboardMapperManager.this);
        }
    }
}