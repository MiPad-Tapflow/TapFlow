package com.xiaomi.mslgrdp.services;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import com.xiaomi.mslgrdp.domain.BookmarkBase;
import java.util.ArrayList;

/* loaded from: classes5.dex */
public abstract class BookmarkBaseGateway {
    private static final String JOIN_PREFIX = "join_";
    private static final String KEY_BOOKMARK_ID = "bookmarkId";
    private static final String KEY_PERFORMANCE_COMPOSITION = "performanceDesktopComposition";
    private static final String KEY_PERFORMANCE_COMPOSITION_3G = "performanceDesktopComposition3G";
    private static final String KEY_PERFORMANCE_DRAG = "performanceFullWindowDrag";
    private static final String KEY_PERFORMANCE_DRAG_3G = "performanceFullWindowDrag3G";
    private static final String KEY_PERFORMANCE_FONTS = "performanceFontSmoothing";
    private static final String KEY_PERFORMANCE_FONTS_3G = "performanceFontSmoothing3G";
    private static final String KEY_PERFORMANCE_GFX = "performanceGfx";
    private static final String KEY_PERFORMANCE_GFX_3G = "performanceGfx3G";
    private static final String KEY_PERFORMANCE_H264 = "performanceGfxH264";
    private static final String KEY_PERFORMANCE_H264_3G = "performanceGfxH2643G";
    private static final String KEY_PERFORMANCE_MENU_ANIMATIONS = "performanceMenuAnimations";
    private static final String KEY_PERFORMANCE_MENU_ANIMATIONS_3G = "performanceMenuAnimations3G";
    private static final String KEY_PERFORMANCE_RFX = "performanceRemoteFX";
    private static final String KEY_PERFORMANCE_RFX_3G = "performanceRemoteFX3G";
    private static final String KEY_PERFORMANCE_THEME = "performanceTheming";
    private static final String KEY_PERFORMANCE_THEME_3G = "performanceTheming3G";
    private static final String KEY_PERFORMANCE_WALLPAPER = "performanceWallpaper";
    private static final String KEY_PERFORMANCE_WALLPAPER_3G = "performanceWallpaper3G";
    private static final String KEY_SCREEN_COLORS = "screenColors";
    private static final String KEY_SCREEN_COLORS_3G = "screenColors3G";
    private static final String KEY_SCREEN_HEIGHT = "screenHeight";
    private static final String KEY_SCREEN_HEIGHT_3G = "screenHeight3G";
    private static final String KEY_SCREEN_RESOLUTION = "screenResolution";
    private static final String KEY_SCREEN_RESOLUTION_3G = "screenResolution3G";
    private static final String KEY_SCREEN_WIDTH = "screenWidth";
    private static final String KEY_SCREEN_WIDTH_3G = "screenWidth3G";
    private static final String TAG = "BookmarkBaseGateway";
    private SQLiteOpenHelper bookmarkDB;

    protected abstract void addBookmarkSpecificColumns(BookmarkBase bookmarkBase, ContentValues contentValues);

    protected abstract void addBookmarkSpecificColumns(ArrayList<String> arrayList);

    protected abstract BookmarkBase createBookmark();

    protected abstract String getBookmarkTableName();

    protected abstract void readBookmarkSpecificColumns(BookmarkBase bookmarkBase, Cursor cursor);

    public BookmarkBaseGateway(SQLiteOpenHelper bookmarkDB) {
        this.bookmarkDB = bookmarkDB;
    }

    public void insert(BookmarkBase bookmark) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("label", bookmark.getLabel());
        values.put("username", bookmark.getUsername());
        values.put("password", bookmark.getPassword());
        values.put("domain", bookmark.getDomain());
        long rowid = insertScreenSettings(db, bookmark.getScreenSettings());
        values.put("screen_settings", Long.valueOf(rowid));
        long rowid2 = insertPerformanceFlags(db, bookmark.getPerformanceFlags());
        values.put("performance_flags", Long.valueOf(rowid2));
        values.put("enable_3g_settings", Boolean.valueOf(bookmark.getAdvancedSettings().getEnable3GSettings()));
        long rowid3 = insertScreenSettings(db, bookmark.getAdvancedSettings().getScreen3G());
        values.put("screen_3g", Long.valueOf(rowid3));
        long rowid4 = insertPerformanceFlags(db, bookmark.getAdvancedSettings().getPerformance3G());
        values.put("performance_3g", Long.valueOf(rowid4));
        values.put("redirect_sdcard", Boolean.valueOf(bookmark.getAdvancedSettings().getRedirectSDCard()));
        values.put("redirect_sound", Integer.valueOf(bookmark.getAdvancedSettings().getRedirectSound()));
        values.put("redirect_microphone", Boolean.valueOf(bookmark.getAdvancedSettings().getRedirectMicrophone()));
        values.put("security", Integer.valueOf(bookmark.getAdvancedSettings().getSecurity()));
        values.put("console_mode", Boolean.valueOf(bookmark.getAdvancedSettings().getConsoleMode()));
        values.put("remote_program", bookmark.getAdvancedSettings().getRemoteProgram());
        values.put("work_dir", bookmark.getAdvancedSettings().getWorkDir());
        values.put("async_channel", Boolean.valueOf(bookmark.getDebugSettings().getAsyncChannel()));
        values.put("async_input", Boolean.valueOf(bookmark.getDebugSettings().getAsyncInput()));
        values.put("async_update", Boolean.valueOf(bookmark.getDebugSettings().getAsyncUpdate()));
        values.put("debug_level", bookmark.getDebugSettings().getDebugLevel());
        addBookmarkSpecificColumns(bookmark, values);
        db.insertOrThrow(getBookmarkTableName(), null, values);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public boolean update(BookmarkBase bookmark) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put("label", bookmark.getLabel());
        values.put("username", bookmark.getUsername());
        values.put("password", bookmark.getPassword());
        values.put("domain", bookmark.getDomain());
        updateScreenSettings(db, bookmark);
        updatePerformanceFlags(db, bookmark);
        values.put("enable_3g_settings", Boolean.valueOf(bookmark.getAdvancedSettings().getEnable3GSettings()));
        updateScreenSettings3G(db, bookmark);
        updatePerformanceFlags3G(db, bookmark);
        values.put("redirect_sdcard", Boolean.valueOf(bookmark.getAdvancedSettings().getRedirectSDCard()));
        values.put("redirect_sound", Integer.valueOf(bookmark.getAdvancedSettings().getRedirectSound()));
        values.put("redirect_microphone", Boolean.valueOf(bookmark.getAdvancedSettings().getRedirectMicrophone()));
        values.put("security", Integer.valueOf(bookmark.getAdvancedSettings().getSecurity()));
        values.put("console_mode", Boolean.valueOf(bookmark.getAdvancedSettings().getConsoleMode()));
        values.put("remote_program", bookmark.getAdvancedSettings().getRemoteProgram());
        values.put("work_dir", bookmark.getAdvancedSettings().getWorkDir());
        values.put("async_channel", Boolean.valueOf(bookmark.getDebugSettings().getAsyncChannel()));
        values.put("async_input", Boolean.valueOf(bookmark.getDebugSettings().getAsyncInput()));
        values.put("async_update", Boolean.valueOf(bookmark.getDebugSettings().getAsyncUpdate()));
        values.put("debug_level", bookmark.getDebugSettings().getDebugLevel());
        addBookmarkSpecificColumns(bookmark, values);
        boolean res = db.update(getBookmarkTableName(), values, new StringBuilder().append("_id = ").append(bookmark.getId()).toString(), null) == 1;
        db.setTransactionSuccessful();
        db.endTransaction();
        return res;
    }

    public void delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(getBookmarkTableName(), "_id = " + id, null);
    }

    public BookmarkBase findById(long id) {
        Cursor cursor = queryBookmarks(getBookmarkTableName() + "." + BookmarkDB.ID + " = " + id, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }
        cursor.moveToFirst();
        BookmarkBase bookmark = getBookmarkFromCursor(cursor);
        cursor.close();
        return bookmark;
    }

    public BookmarkBase findByLabel(String label) {
        Cursor cursor = queryBookmarks("label = '" + label + "'", "label");
        if (cursor.getCount() > 1) {
            Log.e(TAG, "More than one bookmark with the same label found!");
        }
        BookmarkBase bookmark = null;
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            bookmark = getBookmarkFromCursor(cursor);
        }
        cursor.close();
        return bookmark;
    }

    public ArrayList<BookmarkBase> findByLabelLike(String pattern) {
        Cursor cursor = queryBookmarks("label LIKE '%" + pattern + "%'", "label");
        ArrayList<BookmarkBase> bookmarks = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                bookmarks.add(getBookmarkFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
            return bookmarks;
        }
        cursor.close();
        return bookmarks;
    }

    public ArrayList<BookmarkBase> findAll() {
        Cursor cursor = queryBookmarks(null, "label");
        int count = cursor.getCount();
        ArrayList<BookmarkBase> bookmarks = new ArrayList<>(count);
        if (cursor.moveToFirst() && count > 0) {
            do {
                bookmarks.add(getBookmarkFromCursor(cursor));
            } while (cursor.moveToNext());
            cursor.close();
            return bookmarks;
        }
        cursor.close();
        return bookmarks;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public Cursor queryBookmarks(String whereClause, String orderBy) {
        ArrayList<String> columns = new ArrayList<>();
        addBookmarkColumns(columns);
        addScreenSettingsColumns(columns);
        addPerformanceFlagsColumns(columns);
        addScreenSettings3GColumns(columns);
        addPerformanceFlags3GColumns(columns);
        String[] cols = new String[columns.size()];
        columns.toArray(cols);
        SQLiteDatabase db = getReadableDatabase();
        String query = SQLiteQueryBuilder.buildQueryString(false, "tbl_manual_bookmarks INNER JOIN tbl_screen_settings AS join_screen_settings ON join_screen_settings._id = tbl_manual_bookmarks.screen_settings INNER JOIN tbl_performance_flags AS join_performance_flags ON join_performance_flags._id = tbl_manual_bookmarks.performance_flags INNER JOIN tbl_screen_settings AS join_screen_3g ON join_screen_3g._id = tbl_manual_bookmarks.screen_3g INNER JOIN tbl_performance_flags AS join_performance_3g ON join_performance_3g._id = tbl_manual_bookmarks.performance_3g", cols, whereClause, null, null, orderBy, null);
        return db.rawQuery(query, null);
    }

    private void addBookmarkColumns(ArrayList<String> columns) {
        columns.add(getBookmarkTableName() + "." + BookmarkDB.ID + " " + KEY_BOOKMARK_ID);
        columns.add("label");
        columns.add("username");
        columns.add("password");
        columns.add("domain");
        columns.add("enable_3g_settings");
        columns.add("redirect_sdcard");
        columns.add("redirect_sound");
        columns.add("redirect_microphone");
        columns.add("security");
        columns.add("console_mode");
        columns.add("remote_program");
        columns.add("work_dir");
        columns.add("debug_level");
        columns.add("async_channel");
        columns.add("async_update");
        columns.add("async_input");
        addBookmarkSpecificColumns(columns);
    }

    private void addScreenSettingsColumns(ArrayList<String> columns) {
        columns.add("join_screen_settings.colors as screenColors");
        columns.add("join_screen_settings.resolution as screenResolution");
        columns.add("join_screen_settings.width as screenWidth");
        columns.add("join_screen_settings.height as screenHeight");
    }

    private void addPerformanceFlagsColumns(ArrayList<String> columns) {
        columns.add("join_performance_flags.perf_remotefx as performanceRemoteFX");
        columns.add("join_performance_flags.perf_gfx as performanceGfx");
        columns.add("join_performance_flags.perf_gfx_h264 as performanceGfxH264");
        columns.add("join_performance_flags.perf_wallpaper as performanceWallpaper");
        columns.add("join_performance_flags.perf_theming as performanceTheming");
        columns.add("join_performance_flags.perf_full_window_drag as performanceFullWindowDrag");
        columns.add("join_performance_flags.perf_menu_animations as performanceMenuAnimations");
        columns.add("join_performance_flags.perf_font_smoothing as performanceFontSmoothing");
        columns.add("join_performance_flags.perf_desktop_composition performanceDesktopComposition");
    }

    private void addScreenSettings3GColumns(ArrayList<String> columns) {
        columns.add("join_screen_3g.colors as screenColors3G");
        columns.add("join_screen_3g.resolution as screenResolution3G");
        columns.add("join_screen_3g.width as screenWidth3G");
        columns.add("join_screen_3g.height as screenHeight3G");
    }

    private void addPerformanceFlags3GColumns(ArrayList<String> columns) {
        columns.add("join_performance_3g.perf_remotefx as performanceRemoteFX3G");
        columns.add("join_performance_3g.perf_gfx as performanceGfx3G");
        columns.add("join_performance_3g.perf_gfx_h264 as performanceGfxH2643G");
        columns.add("join_performance_3g.perf_wallpaper as performanceWallpaper3G");
        columns.add("join_performance_3g.perf_theming as performanceTheming3G");
        columns.add("join_performance_3g.perf_full_window_drag as performanceFullWindowDrag3G");
        columns.add("join_performance_3g.perf_menu_animations as performanceMenuAnimations3G");
        columns.add("join_performance_3g.perf_font_smoothing as performanceFontSmoothing3G");
        columns.add("join_performance_3g.perf_desktop_composition performanceDesktopComposition3G");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @SuppressLint("Range")
    public BookmarkBase getBookmarkFromCursor(Cursor cursor) {
        BookmarkBase bookmark = createBookmark();
        bookmark.setId(cursor.getLong(cursor.getColumnIndex(KEY_BOOKMARK_ID)));
        bookmark.setLabel(cursor.getString(cursor.getColumnIndex("label")));
        bookmark.setUsername(cursor.getString(cursor.getColumnIndex("username")));
        bookmark.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        bookmark.setDomain(cursor.getString(cursor.getColumnIndex("domain")));
        readScreenSettings(bookmark, cursor);
        readPerformanceFlags(bookmark, cursor);
        bookmark.getAdvancedSettings().setEnable3GSettings(cursor.getInt(cursor.getColumnIndex("enable_3g_settings")) != 0);
        readScreenSettings3G(bookmark, cursor);
        readPerformanceFlags3G(bookmark, cursor);
        bookmark.getAdvancedSettings().setRedirectSDCard(cursor.getInt(cursor.getColumnIndex("redirect_sdcard")) != 0);
        bookmark.getAdvancedSettings().setRedirectSound(cursor.getInt(cursor.getColumnIndex("redirect_sound")));
        bookmark.getAdvancedSettings().setRedirectMicrophone(cursor.getInt(cursor.getColumnIndex("redirect_microphone")) != 0);
        bookmark.getAdvancedSettings().setSecurity(cursor.getInt(cursor.getColumnIndex("security")));
        bookmark.getAdvancedSettings().setConsoleMode(cursor.getInt(cursor.getColumnIndex("console_mode")) != 0);
        bookmark.getAdvancedSettings().setRemoteProgram(cursor.getString(cursor.getColumnIndex("remote_program")));
        bookmark.getAdvancedSettings().setWorkDir(cursor.getString(cursor.getColumnIndex("work_dir")));
        bookmark.getDebugSettings().setAsyncChannel(cursor.getInt(cursor.getColumnIndex("async_channel")) == 1);
        bookmark.getDebugSettings().setAsyncInput(cursor.getInt(cursor.getColumnIndex("async_input")) == 1);
        bookmark.getDebugSettings().setAsyncUpdate(cursor.getInt(cursor.getColumnIndex("async_update")) == 1);
        bookmark.getDebugSettings().setDebugLevel(cursor.getString(cursor.getColumnIndex("debug_level")));
        readBookmarkSpecificColumns(bookmark, cursor);
        return bookmark;
    }

    @SuppressLint("Range")
    private void readScreenSettings(BookmarkBase bookmark, Cursor cursor) {
        BookmarkBase.ScreenSettings screenSettings = bookmark.getScreenSettings();
        screenSettings.setColors(cursor.getInt(cursor.getColumnIndex(KEY_SCREEN_COLORS)));
        screenSettings.setResolution(cursor.getInt(cursor.getColumnIndex(KEY_SCREEN_RESOLUTION)));
        screenSettings.setWidth(cursor.getInt(cursor.getColumnIndex(KEY_SCREEN_WIDTH)));
        screenSettings.setHeight(cursor.getInt(cursor.getColumnIndex(KEY_SCREEN_HEIGHT)));
    }

    @SuppressLint("Range")
    private void readPerformanceFlags(BookmarkBase bookmark, Cursor cursor) {
        BookmarkBase.PerformanceFlags perfFlags = bookmark.getPerformanceFlags();
        perfFlags.setRemoteFX(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_RFX)) != 0);
        perfFlags.setGfx(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_GFX)) != 0);
        perfFlags.setH264(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_H264)) != 0);
        perfFlags.setWallpaper(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_WALLPAPER)) != 0);
        perfFlags.setTheming(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_THEME)) != 0);
        perfFlags.setFullWindowDrag(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_DRAG)) != 0);
        perfFlags.setMenuAnimations(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_MENU_ANIMATIONS)) != 0);
        perfFlags.setFontSmoothing(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_FONTS)) != 0);
        perfFlags.setDesktopComposition(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_COMPOSITION)) != 0);
    }

    @SuppressLint("Range")
    private void readScreenSettings3G(BookmarkBase bookmark, Cursor cursor) {
        BookmarkBase.ScreenSettings screenSettings = bookmark.getAdvancedSettings().getScreen3G();
        screenSettings.setColors(cursor.getInt(cursor.getColumnIndex(KEY_SCREEN_COLORS_3G)));
        screenSettings.setResolution(cursor.getInt(cursor.getColumnIndex(KEY_SCREEN_RESOLUTION_3G)));
        screenSettings.setWidth(cursor.getInt(cursor.getColumnIndex(KEY_SCREEN_WIDTH_3G)));
        screenSettings.setHeight(cursor.getInt(cursor.getColumnIndex(KEY_SCREEN_HEIGHT_3G)));
    }

    @SuppressLint("Range")
    private void readPerformanceFlags3G(BookmarkBase bookmark, Cursor cursor) {
        BookmarkBase.PerformanceFlags perfFlags = bookmark.getAdvancedSettings().getPerformance3G();
        perfFlags.setRemoteFX(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_RFX_3G)) != 0);
        perfFlags.setGfx(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_GFX_3G)) != 0);
        perfFlags.setH264(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_H264_3G)) != 0);
        perfFlags.setWallpaper(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_WALLPAPER_3G)) != 0);
        perfFlags.setTheming(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_THEME_3G)) != 0);
        perfFlags.setFullWindowDrag(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_DRAG_3G)) != 0);
        perfFlags.setMenuAnimations(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_MENU_ANIMATIONS_3G)) != 0);
        perfFlags.setFontSmoothing(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_FONTS_3G)) != 0);
        perfFlags.setDesktopComposition(cursor.getInt(cursor.getColumnIndex(KEY_PERFORMANCE_COMPOSITION_3G)) != 0);
    }

    private void fillScreenSettingsContentValues(BookmarkBase.ScreenSettings settings, ContentValues values) {
        values.put("colors", Integer.valueOf(settings.getColors()));
        values.put("resolution", Integer.valueOf(settings.getResolution()));
        values.put("width", Integer.valueOf(settings.getWidth()));
        values.put("height", Integer.valueOf(settings.getHeight()));
    }

    private void fillPerformanceFlagsContentValues(BookmarkBase.PerformanceFlags perfFlags, ContentValues values) {
        values.put("perf_remotefx", Boolean.valueOf(perfFlags.getRemoteFX()));
        values.put("perf_gfx", Boolean.valueOf(perfFlags.getGfx()));
        values.put("perf_gfx_h264", Boolean.valueOf(perfFlags.getH264()));
        values.put("perf_wallpaper", Boolean.valueOf(perfFlags.getWallpaper()));
        values.put("perf_theming", Boolean.valueOf(perfFlags.getTheming()));
        values.put("perf_full_window_drag", Boolean.valueOf(perfFlags.getFullWindowDrag()));
        values.put("perf_menu_animations", Boolean.valueOf(perfFlags.getMenuAnimations()));
        values.put("perf_font_smoothing", Boolean.valueOf(perfFlags.getFontSmoothing()));
        values.put("perf_desktop_composition", Boolean.valueOf(perfFlags.getDesktopComposition()));
    }

    private long insertScreenSettings(SQLiteDatabase db, BookmarkBase.ScreenSettings settings) {
        ContentValues values = new ContentValues();
        fillScreenSettingsContentValues(settings, values);
        return db.insertOrThrow("tbl_screen_settings", null, values);
    }

    private boolean updateScreenSettings(SQLiteDatabase db, BookmarkBase bookmark) {
        ContentValues values = new ContentValues();
        fillScreenSettingsContentValues(bookmark.getScreenSettings(), values);
        String whereClause = "_id IN (SELECT screen_settings FROM " + getBookmarkTableName() + " WHERE " + BookmarkDB.ID + " =  " + bookmark.getId() + ");";
        return db.update("tbl_screen_settings", values, whereClause, null) == 1;
    }

    private boolean updateScreenSettings3G(SQLiteDatabase db, BookmarkBase bookmark) {
        ContentValues values = new ContentValues();
        fillScreenSettingsContentValues(bookmark.getAdvancedSettings().getScreen3G(), values);
        String whereClause = "_id IN (SELECT screen_3g FROM " + getBookmarkTableName() + " WHERE " + BookmarkDB.ID + " =  " + bookmark.getId() + ");";
        return db.update("tbl_screen_settings", values, whereClause, null) == 1;
    }

    private long insertPerformanceFlags(SQLiteDatabase db, BookmarkBase.PerformanceFlags perfFlags) {
        ContentValues values = new ContentValues();
        fillPerformanceFlagsContentValues(perfFlags, values);
        return db.insertOrThrow("tbl_performance_flags", null, values);
    }

    private boolean updatePerformanceFlags(SQLiteDatabase db, BookmarkBase bookmark) {
        ContentValues values = new ContentValues();
        fillPerformanceFlagsContentValues(bookmark.getPerformanceFlags(), values);
        String whereClause = "_id IN (SELECT performance_flags FROM " + getBookmarkTableName() + " WHERE " + BookmarkDB.ID + " =  " + bookmark.getId() + ");";
        return db.update("tbl_performance_flags", values, whereClause, null) == 1;
    }

    private boolean updatePerformanceFlags3G(SQLiteDatabase db, BookmarkBase bookmark) {
        ContentValues values = new ContentValues();
        fillPerformanceFlagsContentValues(bookmark.getAdvancedSettings().getPerformance3G(), values);
        String whereClause = "_id IN (SELECT performance_3g FROM " + getBookmarkTableName() + " WHERE " + BookmarkDB.ID + " =  " + bookmark.getId() + ");";
        return db.update("tbl_performance_flags", values, whereClause, null) == 1;
    }

    private SQLiteDatabase getWritableDatabase() {
        return this.bookmarkDB.getWritableDatabase();
    }

    private SQLiteDatabase getReadableDatabase() {
        try {
            SQLiteDatabase db = this.bookmarkDB.getReadableDatabase();
            return db;
        } catch (SQLiteException e) {
            SQLiteDatabase db2 = this.bookmarkDB.getWritableDatabase();
            return db2;
        }
    }
}
