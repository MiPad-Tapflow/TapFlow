package com.xiaomi.mslgrdp.utils;

import android.content.Context;
import android.view.KeyEvent;

import cn.ljlVink.Tapflow.R;
import kotlin.text.Typography;

/* loaded from: classes5.dex */
public class KeyboardMapper {
    private static final int EXTKEY_KBCURSOR = 4354;
    private static final int EXTKEY_KBFUNCTIONKEYS = 4352;
    private static final int EXTKEY_KBNUMPAD = 4353;
    public static final int KEYBOARD_TYPE_CURSOR = 3;
    public static final int KEYBOARD_TYPE_FUNCTIONKEYS = 1;
    public static final int KEYBOARD_TYPE_NUMPAD = 2;
    public static final int KEYSTATE_LOCKED = 2;
    public static final int KEYSTATE_OFF = 3;
    public static final int KEYSTATE_ON = 1;
    private static final int KEY_FLAG_TOGGLE = 1073741824;
    private static final int KEY_FLAG_UNICODE = Integer.MIN_VALUE;
    static final int VK_ABNT_C1 = 193;
    static final int VK_ABNT_C2 = 194;
    static final int VK_ACCEPT = 30;
    static final int VK_ADD = 107;
    static final int VK_APPS = 93;
    static final int VK_ATTN = 246;
    static final int VK_BACK = 8;
    static final int VK_BROWSER_BACK = 166;
    static final int VK_BROWSER_FAVORITES = 171;
    static final int VK_BROWSER_FORWARD = 167;
    static final int VK_BROWSER_HOME = 172;
    static final int VK_BROWSER_REFRESH = 168;
    static final int VK_BROWSER_SEARCH = 170;
    static final int VK_BROWSER_STOP = 169;
    static final int VK_CANCEL = 3;
    static final int VK_CAPITAL = 20;
    static final int VK_CLEAR = 12;
    static final int VK_CONTROL = 17;
    static final int VK_CONVERT = 28;
    static final int VK_CRSEL = 247;
    static final int VK_DECIMAL = 110;
    static final int VK_DELETE = 46;
    static final int VK_DIVIDE = 111;
    static final int VK_DOWN = 40;
    static final int VK_END = 35;
    static final int VK_EREOF = 249;
    static final int VK_ESCAPE = 27;
    static final int VK_EXECUTE = 43;
    static final int VK_EXSEL = 248;
    static final int VK_EXT_KEY = 256;
    static final int VK_F1 = 112;
    static final int VK_F10 = 121;
    static final int VK_F11 = 122;
    static final int VK_F12 = 123;
    static final int VK_F13 = 124;
    static final int VK_F14 = 125;
    static final int VK_F15 = 126;
    static final int VK_F16 = 127;
    static final int VK_F17 = 128;
    static final int VK_F18 = 129;
    static final int VK_F19 = 130;
    static final int VK_F2 = 113;
    static final int VK_F20 = 131;
    static final int VK_F21 = 132;
    static final int VK_F22 = 133;
    static final int VK_F23 = 134;
    static final int VK_F24 = 135;
    static final int VK_F3 = 114;
    static final int VK_F4 = 115;
    static final int VK_F5 = 116;
    static final int VK_F6 = 117;
    static final int VK_F7 = 118;
    static final int VK_F8 = 119;
    static final int VK_F9 = 120;
    static final int VK_FINAL = 24;
    static final int VK_HANGUEL = 21;
    static final int VK_HANGUL = 21;
    static final int VK_HANJA = 25;
    static final int VK_HELP = 47;
    static final int VK_HOME = 36;
    static final int VK_INSERT = 45;
    static final int VK_JUNJA = 23;
    static final int VK_KANA = 21;
    static final int VK_KANJI = 25;
    static final int VK_KEY_0 = 48;
    static final int VK_KEY_1 = 49;
    static final int VK_KEY_2 = 50;
    static final int VK_KEY_3 = 51;
    static final int VK_KEY_4 = 52;
    static final int VK_KEY_5 = 53;
    static final int VK_KEY_6 = 54;
    static final int VK_KEY_7 = 55;
    static final int VK_KEY_8 = 56;
    static final int VK_KEY_9 = 57;
    static final int VK_KEY_A = 65;
    static final int VK_KEY_B = 66;
    static final int VK_KEY_C = 67;
    static final int VK_KEY_D = 68;
    static final int VK_KEY_E = 69;
    static final int VK_KEY_F = 70;
    static final int VK_KEY_G = 71;
    static final int VK_KEY_H = 72;
    static final int VK_KEY_I = 73;
    static final int VK_KEY_J = 74;
    static final int VK_KEY_K = 75;
    static final int VK_KEY_L = 76;
    static final int VK_KEY_M = 77;
    static final int VK_KEY_N = 78;
    static final int VK_KEY_O = 79;
    static final int VK_KEY_P = 80;
    static final int VK_KEY_Q = 81;
    static final int VK_KEY_R = 82;
    static final int VK_KEY_S = 83;
    static final int VK_KEY_T = 84;
    static final int VK_KEY_U = 85;
    static final int VK_KEY_V = 86;
    static final int VK_KEY_W = 87;
    static final int VK_KEY_X = 88;
    static final int VK_KEY_Y = 89;
    static final int VK_KEY_Z = 90;
    static final int VK_LAUNCH_APP1 = 182;
    static final int VK_LAUNCH_APP2 = 183;
    static final int VK_LAUNCH_MAIL = 180;
    static final int VK_LAUNCH_MEDIA_SELECT = 181;
    static final int VK_LBUTTON = 1;
    static final int VK_LCONTROL = 162;
    static final int VK_LEFT = 37;
    static final int VK_LMENU = 164;
    public static final int VK_LSHIFT = 160;
    static final int VK_LWIN = 91;
    static final int VK_MBUTTON = 4;
    static final int VK_MEDIA_NEXT_TRACK = 176;
    static final int VK_MEDIA_PLAY_PAUSE = 179;
    static final int VK_MEDIA_PREV_TRACK = 177;
    static final int VK_MEDIA_STOP = 178;
    static final int VK_MENU = 18;
    static final int VK_MODECHANGE = 31;
    static final int VK_MULTIPLY = 106;
    static final int VK_NEXT = 34;
    static final int VK_NONAME = 252;
    static final int VK_NONCONVERT = 29;
    static final int VK_NUMLOCK = 144;
    static final int VK_NUMPAD0 = 96;
    static final int VK_NUMPAD1 = 97;
    static final int VK_NUMPAD2 = 98;
    static final int VK_NUMPAD3 = 99;
    static final int VK_NUMPAD4 = 100;
    static final int VK_NUMPAD5 = 101;
    static final int VK_NUMPAD6 = 102;
    static final int VK_NUMPAD7 = 103;
    static final int VK_NUMPAD8 = 104;
    static final int VK_NUMPAD9 = 105;
    static final int VK_OEM_1 = 186;
    static final int VK_OEM_102 = 226;
    static final int VK_OEM_2 = 191;
    static final int VK_OEM_3 = 192;
    static final int VK_OEM_4 = 219;
    static final int VK_OEM_5 = 220;
    static final int VK_OEM_6 = 221;
    static final int VK_OEM_7 = 222;
    static final int VK_OEM_8 = 223;
    static final int VK_OEM_CLEAR = 254;
    static final int VK_OEM_COMMA = 188;
    static final int VK_OEM_MINUS = 189;
    static final int VK_OEM_PERIOD = 190;
    static final int VK_OEM_PLUS = 187;
    static final int VK_PA1 = 253;
    static final int VK_PACKET = 231;
    static final int VK_PAUSE = 19;
    static final int VK_PLAY = 250;
    static final int VK_PRINT = 42;
    static final int VK_PRIOR = 33;
    static final int VK_PROCESSKEY = 229;
    static final int VK_RBUTTON = 2;
    static final int VK_RCONTROL = 163;
    static final int VK_RETURN = 13;
    static final int VK_RIGHT = 39;
    static final int VK_RMENU = 165;
    static final int VK_RSHIFT = 161;
    static final int VK_RWIN = 92;
    static final int VK_SCROLL = 145;
    static final int VK_SELECT = 41;
    static final int VK_SEPARATOR = 108;
    static final int VK_SHIFT = 16;
    static final int VK_SLEEP = 95;
    static final int VK_SNAPSHOT = 44;
    static final int VK_SPACE = 32;
    static final int VK_SUBTRACT = 109;
    static final int VK_TAB = 9;
    static final int VK_UNICODE = Integer.MIN_VALUE;
    static final int VK_UP = 38;
    static final int VK_VOLUME_DOWN = 174;
    static final int VK_VOLUME_MUTE = 173;
    static final int VK_VOLUME_UP = 175;
    static final int VK_XBUTTON1 = 5;
    static final int VK_XBUTTON2 = 6;
    static final int VK_ZOOM = 251;
    private static boolean initialized = false;
    private static int[] keymapAndroid;
    private static int[] keymapExt;
    private long lastModifierTime;
    private KeyProcessingListener listener = null;
    private boolean shiftPressed = false;
    private boolean ctrlPressed = false;
    private boolean altPressed = false;
    private boolean winPressed = false;
    private int lastModifierKeyCode = -1;
    private boolean isShiftLocked = false;
    private boolean isCtrlLocked = false;
    private boolean isAltLocked = false;
    private boolean isWinLocked = false;


    public interface KeyProcessingListener {
        void modifiersChanged();

        void processUnicodeKey(int i);

        void processVirtualKey(int i, boolean z);

        void sendStringTo(String str);

        void switchKeyboard(int i);
    }

    public void init(Context context) {
        if (initialized) {
            return;
        }
        int[] iArr = new int[256];
        keymapAndroid = iArr;
        iArr[7] = 48;
        iArr[8] = 49;
        iArr[9] = 50;
        iArr[10] = 51;
        iArr[11] = 52;
        iArr[12] = 53;
        iArr[13] = 54;
        iArr[14] = 55;
        iArr[15] = 56;
        iArr[16] = 57;
        iArr[29] = 65;
        iArr[30] = 66;
        iArr[31] = 67;
        iArr[32] = 68;
        iArr[33] = 69;
        iArr[34] = 70;
        iArr[35] = 71;
        iArr[36] = 72;
        iArr[37] = 73;
        iArr[38] = 74;
        iArr[39] = 75;
        iArr[40] = 76;
        iArr[41] = 77;
        iArr[42] = 78;
        iArr[43] = 79;
        iArr[44] = 80;
        iArr[45] = 81;
        iArr[46] = 82;
        iArr[47] = 83;
        iArr[48] = 84;
        iArr[49] = 85;
        iArr[50] = 86;
        iArr[51] = 87;
        iArr[52] = 88;
        iArr[53] = 89;
        iArr[54] = 90;
        iArr[67] = 8;
        iArr[66] = 13;
        iArr[62] = 32;
        iArr[61] = 9;
        iArr[112] = 302;
        iArr[20] = 296;
        iArr[21] = 293;
        iArr[22] = 295;
        iArr[19] = 294;
        iArr[4] = 27;
        iArr[93] = 296;
        iArr[92] = 294;
        int[] iArr2 = new int[256];
        keymapExt = iArr2;
        iArr2[context.getResources().getInteger(R.integer.keycode_F1)] = 112;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F2)] = 113;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F3)] = 114;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F4)] = 115;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F5)] = 116;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F6)] = 117;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F7)] = 118;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F8)] = 119;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F9)] = 120;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F10)] = 121;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F11)] = 122;
        keymapExt[context.getResources().getInteger(R.integer.keycode_F12)] = 123;
        keymapExt[context.getResources().getInteger(R.integer.keycode_tab)] = 9;
        keymapExt[context.getResources().getInteger(R.integer.keycode_print)] = 42;
        keymapExt[context.getResources().getInteger(R.integer.keycode_insert)] = 301;
        keymapExt[context.getResources().getInteger(R.integer.keycode_delete)] = 302;
        keymapExt[context.getResources().getInteger(R.integer.keycode_home)] = 292;
        keymapExt[context.getResources().getInteger(R.integer.keycode_end)] = 291;
        keymapExt[context.getResources().getInteger(R.integer.keycode_pgup)] = 289;
        keymapExt[context.getResources().getInteger(R.integer.keycode_pgdn)] = 290;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_0)] = 96;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_1)] = 97;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_2)] = 98;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_3)] = 99;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_4)] = 100;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_5)] = 101;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_6)] = 102;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_7)] = 103;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_8)] = 104;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_9)] = 105;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_numlock)] = 144;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_add)] = 107;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_comma)] = 110;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_divide)] = 367;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_enter)] = 269;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_multiply)] = 106;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_subtract)] = 109;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_equals)] = -2147483587;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_left_paren)] = -2147483608;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_right_paren)] = -2147483607;
        keymapExt[context.getResources().getInteger(R.integer.keycode_up)] = 294;
        keymapExt[context.getResources().getInteger(R.integer.keycode_down)] = 296;
        keymapExt[context.getResources().getInteger(R.integer.keycode_left)] = 293;
        keymapExt[context.getResources().getInteger(R.integer.keycode_right)] = 295;
        keymapExt[context.getResources().getInteger(R.integer.keycode_enter)] = 269;
        keymapExt[context.getResources().getInteger(R.integer.keycode_backspace)] = 8;
        keymapExt[context.getResources().getInteger(R.integer.keycode_win)] = 347;
        keymapExt[context.getResources().getInteger(R.integer.keycode_menu)] = 349;
        keymapExt[context.getResources().getInteger(R.integer.keycode_esc)] = 27;
        keymapExt[context.getResources().getInteger(R.integer.keycode_specialkeys_keyboard)] = EXTKEY_KBFUNCTIONKEYS;
        keymapExt[context.getResources().getInteger(R.integer.keycode_numpad_keyboard)] = EXTKEY_KBNUMPAD;
        keymapExt[context.getResources().getInteger(R.integer.keycode_cursor_keyboard)] = EXTKEY_KBCURSOR;
        keymapExt[context.getResources().getInteger(R.integer.keycode_toggle_shift)] = 1073741984;
        keymapExt[context.getResources().getInteger(R.integer.keycode_toggle_ctrl)] = 1073741986;
        keymapExt[context.getResources().getInteger(R.integer.keycode_toggle_alt)] = 1073741988;
        keymapExt[context.getResources().getInteger(R.integer.keycode_toggle_win)] = 1073741915;
        initialized = true;
    }

    public void reset(KeyProcessingListener listener) {
        this.shiftPressed = false;
        this.ctrlPressed = false;
        this.altPressed = false;
        this.winPressed = false;
        setKeyProcessingListener(listener);
    }

    public void setKeyProcessingListener(KeyProcessingListener listener) {
        this.listener = listener;
    }

    public static void send_delay(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean processAndroidKeyEvent(KeyEvent event) {
        switch (event.getAction()) {
            case 0:
                boolean modifierActive = isModifierPressed();
                int vkcode = getVirtualKeyCode(event.getKeyCode());
                if ((Integer.MIN_VALUE & vkcode) != 0) {
                    this.listener.processUnicodeKey(Integer.MAX_VALUE & vkcode);
                } else if (vkcode > 0 && (event.getMetaState() & 4103) == 0) {
                    this.listener.processVirtualKey(vkcode, true);
                    send_delay(10);
                    this.listener.processVirtualKey(vkcode, false);
                } else if (event.isCtrlPressed() && vkcode != 0) {
                    this.listener.processVirtualKey(VK_LCONTROL, true);
                    send_delay(10);
                    this.listener.processVirtualKey(vkcode, true);
                    send_delay(10);
                    this.listener.processVirtualKey(vkcode, false);
                    send_delay(10);
                    this.listener.processVirtualKey(VK_LCONTROL, false);
                } else if (event.isShiftPressed() && vkcode != 0) {
                    this.listener.processVirtualKey(160, true);
                    send_delay(10);
                    this.listener.processVirtualKey(vkcode, true);
                    send_delay(10);
                    this.listener.processVirtualKey(vkcode, false);
                    send_delay(10);
                    this.listener.processVirtualKey(160, false);
                } else if (event.isAltPressed() && vkcode == 13) {
                    this.listener.processVirtualKey(VK_LMENU, true);
                    send_delay(10);
                    this.listener.processVirtualKey(vkcode, true);
                    send_delay(10);
                    this.listener.processVirtualKey(vkcode, false);
                    send_delay(10);
                    this.listener.processVirtualKey(VK_LMENU, false);
                } else if (event.getUnicodeChar() == 0) {
                    return false;
                } else {
                    this.listener.processUnicodeKey(event.getUnicodeChar());
                }
                if (modifierActive) {
                    resetModifierKeysAfterInput(false);
                }
                return true;
            case 1:
                return false;
            case 2:
                if (event.getKeyCode() == 0) {
                    this.listener.processVirtualKey(47, true);
                    String str = event.getCharacters();
                    this.listener.sendStringTo(str);
                    this.listener.processVirtualKey(47, false);
                }
                return true;
            default:
                return false;
        }
    }

    public void processCustomKeyEvent(int keycode) {
        int extCode = getExtendedKeyCode(keycode);
        if (extCode == 0) {
            return;
        }
        if ((1073741824 & extCode) != 0) {
            processToggleButton((-1073741825) & extCode);
        } else if (extCode == EXTKEY_KBFUNCTIONKEYS || extCode == EXTKEY_KBNUMPAD || extCode == EXTKEY_KBCURSOR) {
            switchKeyboard(extCode);
        } else {
            if ((Integer.MIN_VALUE & extCode) != 0) {
                this.listener.processUnicodeKey(Integer.MAX_VALUE & extCode);
            } else {
                this.listener.processVirtualKey(extCode, true);
                this.listener.processVirtualKey(extCode, false);
            }
            resetModifierKeysAfterInput(false);
        }
    }

    public void sendAltF4() {
        this.listener.processVirtualKey(VK_LMENU, true);
        this.listener.processVirtualKey(115, true);
        this.listener.processVirtualKey(115, false);
        this.listener.processVirtualKey(VK_LMENU, false);
    }

    private boolean isModifierPressed() {
        return this.shiftPressed || this.ctrlPressed || this.altPressed || this.winPressed;
    }

    public int getModifierState(int keycode) {
        int modifierCode = getExtendedKeyCode(keycode);
        if ((1073741824 & modifierCode) == 0) {
            return -1;
        }
        switch (modifierCode & (-1073741825)) {
            case 91:
                if (this.winPressed) {
                    return this.isWinLocked ? 2 : 1;
                }
                return 3;
            case 160:
                if (this.shiftPressed) {
                    return this.isShiftLocked ? 2 : 1;
                }
                return 3;
            case VK_LCONTROL /* 162 */:
                if (this.ctrlPressed) {
                    return this.isCtrlLocked ? 2 : 1;
                }
                return 3;
            case VK_LMENU /* 164 */:
                if (this.altPressed) {
                    return this.isAltLocked ? 2 : 1;
                }
                return 3;
            default:
                return -1;
        }
    }

    private int getVirtualKeyCode(int keycode) {
        if (keycode >= 0 && keycode <= 255) {
            return keymapAndroid[keycode];
        }
        return 0;
    }

    private int getExtendedKeyCode(int keycode) {
        if (keycode >= 0 && keycode <= 255) {
            return keymapExt[keycode];
        }
        return 0;
    }

    private void processToggleButton(int keycode) {
        switch (keycode) {
            case 91:
                if (!checkToggleModifierLock(91)) {
                    this.isWinLocked = false;
                    boolean z = !this.winPressed;
                    this.winPressed = z;
                    this.listener.processVirtualKey(347, z);
                    break;
                } else {
                    this.isWinLocked = true;
                    break;
                }
            case 160:
                if (!checkToggleModifierLock(160)) {
                    this.isShiftLocked = false;
                    boolean z2 = !this.shiftPressed;
                    this.shiftPressed = z2;
                    this.listener.processVirtualKey(160, z2);
                    break;
                } else {
                    this.isShiftLocked = true;
                    break;
                }
            case VK_LCONTROL /* 162 */:
                if (!checkToggleModifierLock(VK_LCONTROL)) {
                    this.isCtrlLocked = false;
                    boolean z3 = !this.ctrlPressed;
                    this.ctrlPressed = z3;
                    this.listener.processVirtualKey(VK_LCONTROL, z3);
                    break;
                } else {
                    this.isCtrlLocked = true;
                    break;
                }
            case VK_LMENU /* 164 */:
                if (!checkToggleModifierLock(VK_LMENU)) {
                    this.isAltLocked = false;
                    boolean z4 = !this.altPressed;
                    this.altPressed = z4;
                    this.listener.processVirtualKey(VK_LMENU, z4);
                    break;
                } else {
                    this.isAltLocked = true;
                    break;
                }
        }
        this.listener.modifiersChanged();
    }

    public void clearlAllModifiers() {
        resetModifierKeysAfterInput(true);
    }

    private void resetModifierKeysAfterInput(boolean force) {
        if (this.shiftPressed && (!this.isShiftLocked || force)) {
            this.listener.processVirtualKey(160, false);
            this.shiftPressed = false;
        }
        if (this.ctrlPressed && (!this.isCtrlLocked || force)) {
            this.listener.processVirtualKey(VK_LCONTROL, false);
            this.ctrlPressed = false;
        }
        if (this.altPressed && (!this.isAltLocked || force)) {
            this.listener.processVirtualKey(VK_LMENU, false);
            this.altPressed = false;
        }
        if (this.winPressed && (!this.isWinLocked || force)) {
            this.listener.processVirtualKey(347, false);
            this.winPressed = false;
        }
        KeyProcessingListener keyProcessingListener = this.listener;
        if (keyProcessingListener != null) {
            keyProcessingListener.modifiersChanged();
        }
    }

    private void switchKeyboard(int keycode) {
        switch (keycode) {
            case EXTKEY_KBFUNCTIONKEYS /* 4352 */:
                this.listener.switchKeyboard(1);
                return;
            case EXTKEY_KBNUMPAD /* 4353 */:
                this.listener.switchKeyboard(2);
                return;
            case EXTKEY_KBCURSOR /* 4354 */:
                this.listener.switchKeyboard(3);
                return;
            default:
                return;
        }
    }

    private boolean checkToggleModifierLock(int keycode) {
        long now = System.currentTimeMillis();
        if (this.lastModifierKeyCode != keycode) {
            this.lastModifierKeyCode = keycode;
            this.lastModifierTime = now;
            return false;
        } else if (this.lastModifierTime + 800 > now) {
            this.lastModifierTime = 0L;
            return true;
        } else {
            this.lastModifierTime = now;
            return false;
        }
    }

    public static int asciiToVirtualKey(char c) {
        if (c >= 'A' && c <= 'Z') {
            int keyCode = (c - 'A') + 65;
            return keyCode;
        } else if (c >= 'a' && c <= 'z') {
            int keyCode2 = (c - 'a') + 65;
            return keyCode2;
        } else if (c >= '0' && c <= '9') {
            int keyCode3 = (c - '0') + 48;
            return keyCode3;
        } else {
            switch (c) {
                case ' ':
                    return 32;
                case '!':
                    return 49;
                case '\"':
                case '\'':
                    return VK_OEM_7;
                case '#':
                    return 51;
                case '$':
                    return 52;
                case '%':
                    return 53;
                case '&':
                    return 55;
                case '(':
                    return 57;
                case ')':
                    return 48;
                case '*':
                    return 56;
                case '+':
                case '=':
                    return VK_OEM_PLUS;
                case ',':
                case '<':
                    return VK_OEM_COMMA;
                case '-':
                case '_':
                    return VK_OEM_MINUS;
                case '.':
                case '>':
                    return VK_OEM_PERIOD;
                case '/':
                case '?':
                    return VK_OEM_2;
                case ':':
                case ';':
                    return VK_OEM_1;
                case '@':
                    return 50;
                case '[':
                case '{':
                    return VK_OEM_4;
                case '\\':
                case '|':
                    return VK_OEM_5;
                case ']':
                case '}':
                    return VK_OEM_6;
                case '^':
                    return 54;
                case '`':
                case '~':
                    return VK_OEM_3;
                default:
                    return 0;
            }
        }
    }

    public static boolean needShiftKey(char value) {
        char[] chars = {'~', '!', '@', '#', Typography.dollar, '%', '^', Typography.amp, '*', '(', ')', '_', '+', '{', '}', '|', ':', Typography.quote, '?', Typography.greater, Typography.less};
        for (char c : chars) {
            if (c == value) {
                return true;
            }
        }
        return value >= 'A' && value <= 'Z';
    }
}
