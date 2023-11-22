package com.xiaomi.mslgrdp.domain;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.Locale;

/* loaded from: classes5.dex */
public class BookmarkBase implements Parcelable, Cloneable {
    public static final Parcelable.Creator<BookmarkBase> CREATOR = new Parcelable.Creator<BookmarkBase>() { // from class: com.xiaomi.mslgrdp.domain.BookmarkBase.1
        @Override // android.os.Parcelable.Creator
        public BookmarkBase createFromParcel(Parcel in) {
            return new BookmarkBase(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BookmarkBase[] newArray(int size) {
            return new BookmarkBase[size];
        }
    };
    public static final int TYPE_CUSTOM_BASE = 1000;
    public static final int TYPE_INVALID = -1;
    public static final int TYPE_MANUAL = 1;
    public static final int TYPE_PLACEHOLDER = 3;
    public static final int TYPE_QUICKCONNECT = 2;
    private AdvancedSettings advancedSettings;
    private DebugSettings debugSettings;
    private String domain;
    private long id;
    private String label;
    private String password;
    private PerformanceFlags performanceFlags;
    private ScreenSettings screenSettings;
    protected int type;
    private String username;

    public BookmarkBase(Parcel parcel) {
        this.type = parcel.readInt();
        this.id = parcel.readLong();
        this.label = parcel.readString();
        this.username = parcel.readString();
        this.password = parcel.readString();
        this.domain = parcel.readString();
        this.screenSettings = (ScreenSettings) parcel.readParcelable(ScreenSettings.class.getClassLoader());
        this.performanceFlags = (PerformanceFlags) parcel.readParcelable(PerformanceFlags.class.getClassLoader());
        this.advancedSettings = (AdvancedSettings) parcel.readParcelable(AdvancedSettings.class.getClassLoader());
        this.debugSettings = (DebugSettings) parcel.readParcelable(DebugSettings.class.getClassLoader());
    }

    public BookmarkBase() {
        init();
    }

    private void init() {
        this.type = -1;
        this.id = -1L;
        this.label = "";
        this.username = "";
        this.password = "";
        this.domain = "";
        this.screenSettings = new ScreenSettings();
        this.performanceFlags = new PerformanceFlags();
        this.advancedSettings = new AdvancedSettings();
        this.debugSettings = new DebugSettings();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @SuppressWarnings("unchecked") public <T extends BookmarkBase> T get()
    {
        return (T)this;
    }

    public int getType() {
        return this.type;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public ScreenSettings getScreenSettings() {
        return this.screenSettings;
    }

    public void setScreenSettings(ScreenSettings screenSettings) {
        this.screenSettings = screenSettings;
    }

    public PerformanceFlags getPerformanceFlags() {
        return this.performanceFlags;
    }

    public void setPerformanceFlags(PerformanceFlags performanceFlags) {
        this.performanceFlags = performanceFlags;
    }

    public AdvancedSettings getAdvancedSettings() {
        return this.advancedSettings;
    }

    public void setAdvancedSettings(AdvancedSettings advancedSettings) {
        this.advancedSettings = advancedSettings;
    }

    public DebugSettings getDebugSettings() {
        return this.debugSettings;
    }

    public void setDebugSettings(DebugSettings debugSettings) {
        this.debugSettings = debugSettings;
    }

    public ScreenSettings getActiveScreenSettings() {
        if (this.advancedSettings.getEnable3GSettings()) {
            return this.advancedSettings.getScreen3G();
        }
        return this.screenSettings;
    }

    public PerformanceFlags getActivePerformanceFlags() {
        if (this.advancedSettings.getEnable3GSettings()) {
            return this.advancedSettings.getPerformance3G();
        }
        return this.performanceFlags;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.type);
        out.writeLong(this.id);
        out.writeString(this.label);
        out.writeString(this.username);
        out.writeString(this.password);
        out.writeString(this.domain);
        out.writeParcelable(this.screenSettings, flags);
        out.writeParcelable(this.performanceFlags, flags);
        out.writeParcelable(this.advancedSettings, flags);
        out.writeParcelable(this.debugSettings, flags);
    }

    public void writeToSharedPreferences(SharedPreferences sharedPrefs) {
        Locale locale = Locale.ENGLISH;
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.clear();
        editor.putString("bookmark.label", this.label);
        editor.putString("bookmark.username", this.username);
        editor.putString("bookmark.password", this.password);
        editor.putString("bookmark.domain", this.domain);
        editor.putInt("bookmark.colors", this.screenSettings.getColors());
        editor.putString("bookmark.resolution", this.screenSettings.getResolutionString().toLowerCase(locale));
        editor.putInt("bookmark.width", this.screenSettings.getWidth());
        editor.putInt("bookmark.height", this.screenSettings.getHeight());
        editor.putBoolean("bookmark.perf_remotefx", this.performanceFlags.getRemoteFX());
        editor.putBoolean("bookmark.perf_gfx", this.performanceFlags.getGfx());
        editor.putBoolean("bookmark.perf_gfx_h264", this.performanceFlags.getH264());
        editor.putBoolean("bookmark.perf_wallpaper", this.performanceFlags.getWallpaper());
        editor.putBoolean("bookmark.perf_font_smoothing", this.performanceFlags.getFontSmoothing());
        editor.putBoolean("bookmark.perf_desktop_composition", this.performanceFlags.getDesktopComposition());
        editor.putBoolean("bookmark.perf_window_dragging", this.performanceFlags.getFullWindowDrag());
        editor.putBoolean("bookmark.perf_menu_animation", this.performanceFlags.getMenuAnimations());
        editor.putBoolean("bookmark.perf_themes", this.performanceFlags.getTheming());
        editor.putBoolean("bookmark.enable_3g_settings", this.advancedSettings.getEnable3GSettings());
        editor.putInt("bookmark.colors_3g", this.advancedSettings.getScreen3G().getColors());
        editor.putString("bookmark.resolution_3g", this.advancedSettings.getScreen3G().getResolutionString().toLowerCase(locale));
        editor.putInt("bookmark.width_3g", this.advancedSettings.getScreen3G().getWidth());
        editor.putInt("bookmark.height_3g", this.advancedSettings.getScreen3G().getHeight());
        editor.putBoolean("bookmark.perf_remotefx_3g", this.advancedSettings.getPerformance3G().getRemoteFX());
        editor.putBoolean("bookmark.perf_gfx_3g", this.advancedSettings.getPerformance3G().getGfx());
        editor.putBoolean("bookmark.perf_gfx_h264_3g", this.advancedSettings.getPerformance3G().getH264());
        editor.putBoolean("bookmark.perf_wallpaper_3g", this.advancedSettings.getPerformance3G().getWallpaper());
        editor.putBoolean("bookmark.perf_font_smoothing_3g", this.advancedSettings.getPerformance3G().getFontSmoothing());
        editor.putBoolean("bookmark.perf_desktop_composition_3g", this.advancedSettings.getPerformance3G().getDesktopComposition());
        editor.putBoolean("bookmark.perf_window_dragging_3g", this.advancedSettings.getPerformance3G().getFullWindowDrag());
        editor.putBoolean("bookmark.perf_menu_animation_3g", this.advancedSettings.getPerformance3G().getMenuAnimations());
        editor.putBoolean("bookmark.perf_themes_3g", this.advancedSettings.getPerformance3G().getTheming());
        editor.putBoolean("bookmark.redirect_sdcard", this.advancedSettings.getRedirectSDCard());
        editor.putInt("bookmark.redirect_sound", this.advancedSettings.getRedirectSound());
        editor.putBoolean("bookmark.redirect_microphone", this.advancedSettings.getRedirectMicrophone());
        editor.putInt("bookmark.security", this.advancedSettings.getSecurity());
        editor.putString("bookmark.remote_program", this.advancedSettings.getRemoteProgram());
        editor.putString("bookmark.work_dir", this.advancedSettings.getWorkDir());
        editor.putBoolean("bookmark.console_mode", this.advancedSettings.getConsoleMode());
        editor.putBoolean("bookmark.async_channel", this.debugSettings.getAsyncChannel());
        editor.putBoolean("bookmark.async_input", this.debugSettings.getAsyncInput());
        editor.putBoolean("bookmark.async_update", this.debugSettings.getAsyncUpdate());
        editor.putString("bookmark.debug_level", this.debugSettings.getDebugLevel());
        editor.apply();
    }

    public void readFromSharedPreferences(SharedPreferences sharedPrefs) {
        this.label = sharedPrefs.getString("bookmark.label", "");
        this.username = sharedPrefs.getString("bookmark.username", "");
        this.password = sharedPrefs.getString("bookmark.password", "");
        this.domain = sharedPrefs.getString("bookmark.domain", "");
        this.screenSettings.setColors(sharedPrefs.getInt("bookmark.colors", 16));
        this.screenSettings.setResolution(sharedPrefs.getString("bookmark.resolution", "automatic"), sharedPrefs.getInt("bookmark.width", 800), sharedPrefs.getInt("bookmark.height", 600));
        this.performanceFlags.setRemoteFX(sharedPrefs.getBoolean("bookmark.perf_remotefx", false));
        this.performanceFlags.setGfx(sharedPrefs.getBoolean("bookmark.perf_gfx", false));
        this.performanceFlags.setH264(sharedPrefs.getBoolean("bookmark.perf_gfx_h264", false));
        this.performanceFlags.setWallpaper(sharedPrefs.getBoolean("bookmark.perf_wallpaper", false));
        this.performanceFlags.setFontSmoothing(sharedPrefs.getBoolean("bookmark.perf_font_smoothing", false));
        this.performanceFlags.setDesktopComposition(sharedPrefs.getBoolean("bookmark.perf_desktop_composition", false));
        this.performanceFlags.setFullWindowDrag(sharedPrefs.getBoolean("bookmark.perf_window_dragging", false));
        this.performanceFlags.setMenuAnimations(sharedPrefs.getBoolean("bookmark.perf_menu_animation", false));
        this.performanceFlags.setTheming(sharedPrefs.getBoolean("bookmark.perf_themes", false));
        this.advancedSettings.setEnable3GSettings(sharedPrefs.getBoolean("bookmark.enable_3g_settings", false));
        this.advancedSettings.getScreen3G().setColors(sharedPrefs.getInt("bookmark.colors_3g", 16));
        this.advancedSettings.getScreen3G().setResolution(sharedPrefs.getString("bookmark.resolution_3g", "automatic"), sharedPrefs.getInt("bookmark.width_3g", 800), sharedPrefs.getInt("bookmark.height_3g", 600));
        this.advancedSettings.getPerformance3G().setRemoteFX(sharedPrefs.getBoolean("bookmark.perf_remotefx_3g", false));
        this.advancedSettings.getPerformance3G().setGfx(sharedPrefs.getBoolean("bookmark.perf_gfx_3g", false));
        this.advancedSettings.getPerformance3G().setH264(sharedPrefs.getBoolean("bookmark.perf_gfx_h264_3g", false));
        this.advancedSettings.getPerformance3G().setWallpaper(sharedPrefs.getBoolean("bookmark.perf_wallpaper_3g", false));
        this.advancedSettings.getPerformance3G().setFontSmoothing(sharedPrefs.getBoolean("bookmark.perf_font_smoothing_3g", false));
        this.advancedSettings.getPerformance3G().setDesktopComposition(sharedPrefs.getBoolean("bookmark.perf_desktop_composition_3g", false));
        this.advancedSettings.getPerformance3G().setFullWindowDrag(sharedPrefs.getBoolean("bookmark.perf_window_dragging_3g", false));
        this.advancedSettings.getPerformance3G().setMenuAnimations(sharedPrefs.getBoolean("bookmark.perf_menu_animation_3g", false));
        this.advancedSettings.getPerformance3G().setTheming(sharedPrefs.getBoolean("bookmark.perf_themes_3g", false));
        this.advancedSettings.setRedirectSDCard(sharedPrefs.getBoolean("bookmark.redirect_sdcard", false));
        this.advancedSettings.setRedirectSound(sharedPrefs.getInt("bookmark.redirect_sound", 0));
        this.advancedSettings.setRedirectMicrophone(sharedPrefs.getBoolean("bookmark.redirect_microphone", false));
        this.advancedSettings.setSecurity(sharedPrefs.getInt("bookmark.security", 0));
        this.advancedSettings.setRemoteProgram(sharedPrefs.getString("bookmark.remote_program", ""));
        this.advancedSettings.setWorkDir(sharedPrefs.getString("bookmark.work_dir", ""));
        this.advancedSettings.setConsoleMode(sharedPrefs.getBoolean("bookmark.console_mode", false));
        this.debugSettings.setAsyncChannel(sharedPrefs.getBoolean("bookmark.async_channel", true));
        this.debugSettings.setAsyncInput(sharedPrefs.getBoolean("bookmark.async_input", true));
        this.debugSettings.setAsyncUpdate(sharedPrefs.getBoolean("bookmark.async_update", true));
        this.debugSettings.setDebugLevel(sharedPrefs.getString("bookmark.debug_level", "INFO"));
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /* loaded from: classes5.dex */
    public static class PerformanceFlags implements Parcelable {
        public static final Parcelable.Creator<PerformanceFlags> CREATOR = new Parcelable.Creator<PerformanceFlags>() { // from class: com.xiaomi.mslgrdp.domain.BookmarkBase.PerformanceFlags.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PerformanceFlags createFromParcel(Parcel in) {
                return new PerformanceFlags(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public PerformanceFlags[] newArray(int size) {
                return new PerformanceFlags[size];
            }
        };
        private boolean desktopComposition;
        private boolean fontSmoothing;
        private boolean fullWindowDrag;
        private boolean gfx;
        private boolean h264;
        private boolean menuAnimations;
        private boolean remotefx;
        private boolean theming;
        private boolean wallpaper;

        public PerformanceFlags() {
            this.remotefx = false;
            this.gfx = false;
            this.h264 = false;
            this.wallpaper = false;
            this.theming = false;
            this.fullWindowDrag = false;
            this.menuAnimations = false;
            this.fontSmoothing = false;
            this.desktopComposition = false;
        }

        public PerformanceFlags(Parcel parcel) {
            this.remotefx = parcel.readInt() == 1;
            this.gfx = parcel.readInt() == 1;
            this.h264 = parcel.readInt() == 1;
            this.wallpaper = parcel.readInt() == 1;
            this.theming = parcel.readInt() == 1;
            this.fullWindowDrag = parcel.readInt() == 1;
            this.menuAnimations = parcel.readInt() == 1;
            this.fontSmoothing = parcel.readInt() == 1;
            this.desktopComposition = parcel.readInt() == 1;
        }

        public boolean getRemoteFX() {
            return this.remotefx;
        }

        public void setRemoteFX(boolean remotefx) {
            this.remotefx = remotefx;
        }

        public boolean getGfx() {
            return this.gfx;
        }

        public void setGfx(boolean gfx) {
            this.gfx = gfx;
        }

        public boolean getH264() {
            return this.h264;
        }

        public void setH264(boolean h264) {
            this.h264 = h264;
        }

        public boolean getWallpaper() {
            return this.wallpaper;
        }

        public void setWallpaper(boolean wallpaper) {
            this.wallpaper = wallpaper;
        }

        public boolean getTheming() {
            return this.theming;
        }

        public void setTheming(boolean theming) {
            this.theming = theming;
        }

        public boolean getFullWindowDrag() {
            return this.fullWindowDrag;
        }

        public void setFullWindowDrag(boolean fullWindowDrag) {
            this.fullWindowDrag = fullWindowDrag;
        }

        public boolean getMenuAnimations() {
            return this.menuAnimations;
        }

        public void setMenuAnimations(boolean menuAnimations) {
            this.menuAnimations = menuAnimations;
        }

        public boolean getFontSmoothing() {
            return this.fontSmoothing;
        }

        public void setFontSmoothing(boolean fontSmoothing) {
            this.fontSmoothing = fontSmoothing;
        }

        public boolean getDesktopComposition() {
            return this.desktopComposition;
        }

        public void setDesktopComposition(boolean desktopComposition) {
            this.desktopComposition = desktopComposition;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.remotefx ? 1 : 0);
            out.writeInt(this.gfx ? 1 : 0);
            out.writeInt(this.h264 ? 1 : 0);
            out.writeInt(this.wallpaper ? 1 : 0);
            out.writeInt(this.theming ? 1 : 0);
            out.writeInt(this.fullWindowDrag ? 1 : 0);
            out.writeInt(this.menuAnimations ? 1 : 0);
            out.writeInt(this.fontSmoothing ? 1 : 0);
            out.writeInt(this.desktopComposition ? 1 : 0);
        }
    }

    /* loaded from: classes5.dex */
    public static class ScreenSettings implements Parcelable {
        public static final int AUTOMATIC = -1;
        public static final Parcelable.Creator<ScreenSettings> CREATOR = new Parcelable.Creator<ScreenSettings>() { // from class: com.xiaomi.mslgrdp.domain.BookmarkBase.ScreenSettings.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ScreenSettings createFromParcel(Parcel in) {
                return new ScreenSettings(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ScreenSettings[] newArray(int size) {
                return new ScreenSettings[size];
            }
        };
        public static final int CUSTOM = 0;
        public static final int FITSCREEN = -2;
        public static final int PREDEFINED = 1;
        private int colors;
        private int height;
        private int resolution;
        private int width;

        public ScreenSettings() {
            init();
        }

        public ScreenSettings(Parcel parcel) {
            this.resolution = parcel.readInt();
            this.colors = parcel.readInt();
            this.width = parcel.readInt();
            this.height = parcel.readInt();
        }

        private void validate() {
            switch (this.colors) {
                case 8:
                case 15:
                case 16:
                case 24:
                case 32:
                    break;
                default:
                    this.colors = 32;
                    break;
            }
            int i = this.width;
            if (i <= 0 || i > 65536) {
                this.width = 1024;
            }
            int i2 = this.height;
            if (i2 <= 0 || i2 > 65536) {
                this.height = 768;
            }
            switch (this.resolution) {
                case -2:
                case -1:
                case 0:
                case 1:
                    return;
                default:
                    this.resolution = -1;
                    return;
            }
        }

        private void init() {
            this.resolution = -1;
            this.colors = 16;
            this.width = 0;
            this.height = 0;
        }

        public void setResolution(String resolution, int width, int height) {
            if (resolution.contains("x")) {
                String[] dimensions = resolution.split("x");
                this.width = Integer.valueOf(dimensions[0]).intValue();
                this.height = Integer.valueOf(dimensions[1]).intValue();
                this.resolution = 1;
            } else if (resolution.equalsIgnoreCase("custom")) {
                this.width = width;
                this.height = height;
                this.resolution = 0;
            } else if (resolution.equalsIgnoreCase("fitscreen")) {
                this.height = 0;
                this.width = 0;
                this.resolution = -2;
            } else {
                this.height = 0;
                this.width = 0;
                this.resolution = -1;
            }
        }

        public int getResolution() {
            return this.resolution;
        }

        public void setResolution(int resolution) {
            this.resolution = resolution;
            if (resolution == -1 || resolution == -2) {
                this.width = 0;
                this.height = 0;
            }
        }

        public String getResolutionString() {
            if (isPredefined()) {
                return this.width + "x" + this.height;
            }
            return isFitScreen() ? "fitscreen" : isAutomatic() ? "automatic" : "custom";
        }

        public boolean isPredefined() {
            validate();
            return this.resolution == 1;
        }

        public boolean isAutomatic() {
            validate();
            return this.resolution == -1;
        }

        public boolean isFitScreen() {
            validate();
            return this.resolution == -2;
        }

        public boolean isCustom() {
            validate();
            return this.resolution == 0;
        }

        public int getWidth() {
            validate();
            return this.width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            validate();
            return this.height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getColors() {
            validate();
            return this.colors;
        }

        public void setColors(int colors) {
            this.colors = colors;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.resolution);
            out.writeInt(this.colors);
            out.writeInt(this.width);
            out.writeInt(this.height);
        }
    }

    /* loaded from: classes5.dex */
    public static class DebugSettings implements Parcelable {
        public static final Parcelable.Creator<DebugSettings> CREATOR = new Parcelable.Creator<DebugSettings>() { // from class: com.xiaomi.mslgrdp.domain.BookmarkBase.DebugSettings.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public DebugSettings createFromParcel(Parcel in) {
                return new DebugSettings(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public DebugSettings[] newArray(int size) {
                return new DebugSettings[size];
            }
        };
        private boolean asyncChannel;
        private boolean asyncInput;
        private boolean asyncTransport;
        private boolean asyncUpdate;
        private String debug;

        public DebugSettings() {
            init();
        }

        public DebugSettings(Parcel parcel) {
            this.asyncChannel = parcel.readInt() == 1;
            this.asyncTransport = parcel.readInt() == 1;
            this.asyncInput = parcel.readInt() == 1;
            this.asyncUpdate = parcel.readInt() == 1;
            this.debug = parcel.readString();
        }

        private void init() {
            this.debug = "INFO";
            this.asyncChannel = true;
            this.asyncTransport = false;
            this.asyncInput = true;
            this.asyncUpdate = true;
        }

        private void validate() {
            String[] levels = {"OFF", "FATAL", "ERROR", "WARN", "INFO", "DEBUG", "TRACE"};
            for (String level : levels) {
                if (level.equalsIgnoreCase(this.debug)) {
                    return;
                }
            }
            this.debug = "INFO";
        }

        public String getDebugLevel() {
            validate();
            return this.debug;
        }

        public void setDebugLevel(String debug) {
            this.debug = debug;
        }

        public boolean getAsyncUpdate() {
            return this.asyncUpdate;
        }

        public void setAsyncUpdate(boolean enabled) {
            this.asyncUpdate = enabled;
        }

        public boolean getAsyncInput() {
            return this.asyncInput;
        }

        public void setAsyncInput(boolean enabled) {
            this.asyncInput = enabled;
        }

        public boolean getAsyncChannel() {
            return this.asyncChannel;
        }

        public void setAsyncChannel(boolean enabled) {
            this.asyncChannel = enabled;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.asyncChannel ? 1 : 0);
            out.writeInt(this.asyncTransport ? 1 : 0);
            out.writeInt(this.asyncInput ? 1 : 0);
            out.writeInt(this.asyncUpdate ? 1 : 0);
            out.writeString(this.debug);
        }
    }

    /* loaded from: classes5.dex */
    public static class AdvancedSettings implements Parcelable {
        public static final Parcelable.Creator<AdvancedSettings> CREATOR = new Parcelable.Creator<AdvancedSettings>() { // from class: com.xiaomi.mslgrdp.domain.BookmarkBase.AdvancedSettings.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AdvancedSettings createFromParcel(Parcel in) {
                return new AdvancedSettings(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AdvancedSettings[] newArray(int size) {
                return new AdvancedSettings[size];
            }
        };
        private boolean consoleMode;
        private boolean enable3GSettings;
        private PerformanceFlags performance3G;
        private boolean redirectMicrophone;
        private boolean redirectSDCard;
        private int redirectSound;
        private String remoteProgram;
        private ScreenSettings screen3G;
        private int security;
        private String workDir;

        public AdvancedSettings() {
            init();
        }

        public AdvancedSettings(Parcel parcel) {
            this.enable3GSettings = parcel.readInt() == 1;
            this.screen3G = (ScreenSettings) parcel.readParcelable(ScreenSettings.class.getClassLoader());
            this.performance3G = (PerformanceFlags) parcel.readParcelable(PerformanceFlags.class.getClassLoader());
            this.redirectSDCard = parcel.readInt() == 1;
            this.redirectSound = parcel.readInt();
            this.redirectMicrophone = parcel.readInt() == 1;
            this.security = parcel.readInt();
            this.consoleMode = parcel.readInt() == 1;
            this.remoteProgram = parcel.readString();
            this.workDir = parcel.readString();
        }

        private void init() {
            this.enable3GSettings = false;
            this.screen3G = new ScreenSettings();
            this.performance3G = new PerformanceFlags();
            this.redirectSDCard = false;
            this.redirectSound = 0;
            this.redirectMicrophone = false;
            this.security = 0;
            this.consoleMode = false;
            this.remoteProgram = "";
            this.workDir = "";
        }

        private void validate() {
            switch (this.redirectSound) {
                case 0:
                case 1:
                case 2:
                    break;
                default:
                    this.redirectSound = 0;
                    break;
            }
            switch (this.security) {
                case 0:
                case 1:
                case 2:
                case 3:
                    return;
                default:
                    this.security = 0;
                    return;
            }
        }

        public boolean getEnable3GSettings() {
            return this.enable3GSettings;
        }

        public void setEnable3GSettings(boolean enable3GSettings) {
            this.enable3GSettings = enable3GSettings;
        }

        public ScreenSettings getScreen3G() {
            return this.screen3G;
        }

        public void setScreen3G(ScreenSettings screen3G) {
            this.screen3G = screen3G;
        }

        public PerformanceFlags getPerformance3G() {
            return this.performance3G;
        }

        public void setPerformance3G(PerformanceFlags performance3G) {
            this.performance3G = performance3G;
        }

        public boolean getRedirectSDCard() {
            return this.redirectSDCard;
        }

        public void setRedirectSDCard(boolean redirectSDCard) {
            this.redirectSDCard = redirectSDCard;
        }

        public int getRedirectSound() {
            validate();
            return this.redirectSound;
        }

        public void setRedirectSound(int redirect) {
            this.redirectSound = redirect;
        }

        public boolean getRedirectMicrophone() {
            return this.redirectMicrophone;
        }

        public void setRedirectMicrophone(boolean redirect) {
            this.redirectMicrophone = redirect;
        }

        public int getSecurity() {
            validate();
            return this.security;
        }

        public void setSecurity(int security) {
            this.security = security;
        }

        public boolean getConsoleMode() {
            return this.consoleMode;
        }

        public void setConsoleMode(boolean consoleMode) {
            this.consoleMode = consoleMode;
        }

        public String getRemoteProgram() {
            return this.remoteProgram;
        }

        public void setRemoteProgram(String remoteProgram) {
            this.remoteProgram = remoteProgram;
        }

        public String getWorkDir() {
            return this.workDir;
        }

        public void setWorkDir(String workDir) {
            this.workDir = workDir;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(this.enable3GSettings ? 1 : 0);
            out.writeParcelable(this.screen3G, flags);
            out.writeParcelable(this.performance3G, flags);
            out.writeInt(this.redirectSDCard ? 1 : 0);
            out.writeInt(this.redirectSound);
            out.writeInt(this.redirectMicrophone ? 1 : 0);
            out.writeInt(this.security);
            out.writeInt(this.consoleMode ? 1 : 0);
            out.writeString(this.remoteProgram);
            out.writeString(this.workDir);
        }
    }
}