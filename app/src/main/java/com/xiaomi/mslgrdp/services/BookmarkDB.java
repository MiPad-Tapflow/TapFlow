package com.xiaomi.mslgrdp.services;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookmarkDB extends SQLiteOpenHelper {
    private static final String DB_BACKUP_PREFIX = "temp_";
    static final String DB_KEY_BOOKMARK_3G_ENABLE = "enable_3g_settings";
    static final String DB_KEY_BOOKMARK_ASYNC_CHANNEL = "async_channel";
    static final String DB_KEY_BOOKMARK_ASYNC_INPUT = "async_input";
    static final String DB_KEY_BOOKMARK_ASYNC_UPDATE = "async_update";
    static final String DB_KEY_BOOKMARK_CONSOLE_MODE = "console_mode";
    static final String DB_KEY_BOOKMARK_DEBUG_LEVEL = "debug_level";
    static final String DB_KEY_BOOKMARK_DOMAIN = "domain";
    static final String DB_KEY_BOOKMARK_GW_DOMAIN = "gateway_domain";
    static final String DB_KEY_BOOKMARK_GW_ENABLE = "enable_gateway_settings";
    static final String DB_KEY_BOOKMARK_GW_HOSTNAME = "gateway_hostname";
    static final String DB_KEY_BOOKMARK_GW_PASSWORD = "gateway_password";
    static final String DB_KEY_BOOKMARK_GW_PORT = "gateway_port";
    static final String DB_KEY_BOOKMARK_GW_USERNAME = "gateway_username";
    static final String DB_KEY_BOOKMARK_HOSTNAME = "hostname";
    static final String DB_KEY_BOOKMARK_LABEL = "label";
    static final String DB_KEY_BOOKMARK_PASSWORD = "password";
    static final String DB_KEY_BOOKMARK_PORT = "port";
    static final String DB_KEY_BOOKMARK_REDIRECT_MICROPHONE = "redirect_microphone";
    static final String DB_KEY_BOOKMARK_REDIRECT_SDCARD = "redirect_sdcard";
    static final String DB_KEY_BOOKMARK_REDIRECT_SOUND = "redirect_sound";
    static final String DB_KEY_BOOKMARK_REMOTE_PROGRAM = "remote_program";
    static final String DB_KEY_BOOKMARK_SECURITY = "security";
    static final String DB_KEY_BOOKMARK_USERNAME = "username";
    static final String DB_KEY_BOOKMARK_WORK_DIR = "work_dir";
    static final String DB_KEY_PERFORMANCE_COMPOSITION = "perf_desktop_composition";
    static final String DB_KEY_PERFORMANCE_DRAG = "perf_full_window_drag";
    static final String DB_KEY_PERFORMANCE_FLAGS = "performance_flags";
    static final String DB_KEY_PERFORMANCE_FLAGS_3G = "performance_3g";
    static final String DB_KEY_PERFORMANCE_FONTS = "perf_font_smoothing";
    static final String DB_KEY_PERFORMANCE_GFX = "perf_gfx";
    static final String DB_KEY_PERFORMANCE_H264 = "perf_gfx_h264";
    static final String DB_KEY_PERFORMANCE_MENU_ANIMATIONS = "perf_menu_animations";
    static final String DB_KEY_PERFORMANCE_RFX = "perf_remotefx";
    static final String DB_KEY_PERFORMANCE_THEME = "perf_theming";
    static final String DB_KEY_PERFORMANCE_WALLPAPER = "perf_wallpaper";
    static final String DB_KEY_SCREEN_COLORS = "colors";
    static final String DB_KEY_SCREEN_HEIGHT = "height";
    static final String DB_KEY_SCREEN_RESOLUTION = "resolution";
    static final String DB_KEY_SCREEN_SETTINGS = "screen_settings";
    static final String DB_KEY_SCREEN_SETTINGS_3G = "screen_3g";
    static final String DB_KEY_SCREEN_WIDTH = "width";
    private static final String DB_NAME = "bookmarks.db";
    private static final int DB_VERSION = 9;
    public static final String ID = "_id";
    static final String DB_TABLE_BOOKMARK = "tbl_manual_bookmarks";
    static final String DB_TABLE_SCREEN = "tbl_screen_settings";
    static final String DB_TABLE_PERFORMANCE = "tbl_performance_flags";
    private static final String[] DB_TABLES = {DB_TABLE_BOOKMARK, DB_TABLE_SCREEN, DB_TABLE_PERFORMANCE};

    public BookmarkDB(Context context) {
        super(context, DB_NAME, (SQLiteDatabase.CursorFactory) null, 9);
    }

    private static List<String> GetColumns(SQLiteDatabase db, String tableName)
    {
        List<String> ar = null;
        Cursor c = null;
        try
        {
            c = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
            if (c != null)
            {
                ar = new ArrayList<>(Arrays.asList(c.getColumnNames()));
            }
        }
        catch (Exception e)
        {
            Log.v(tableName, e.getMessage(), e);
            e.printStackTrace();
        }
        finally
        {
            if (c != null)
                c.close();
        }
        return ar;
    }

    private static String joinStrings(List<String> list, String delim) {
        StringBuilder buf = new StringBuilder();
        int num = list.size();
        for (int i = 0; i < num; i++) {
            if (i != 0) {
                buf.append(delim);
            }
            buf.append(list.get(i));
        }
        return buf.toString();
    }

    private void backupTables(SQLiteDatabase db) {
        for (String table : DB_TABLES) {
            String tmpTable = DB_BACKUP_PREFIX + table;
            String query = "ALTER TABLE '" + table + "' RENAME TO '" + tmpTable + "'";
            try {
                db.execSQL(query);
            } catch (Exception e) {
            }
        }
    }

    private void dropOldTables(SQLiteDatabase db) {
        for (String table : DB_TABLES) {
            String tmpTable = DB_BACKUP_PREFIX + table;
            String query = "DROP TABLE IF EXISTS '" + tmpTable + "'";
            db.execSQL(query);
        }
    }

    private void createDB(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_screen_settings (_id INTEGER PRIMARY KEY, colors INTEGER DEFAULT 16, resolution INTEGER DEFAULT 0, width, height);");
        db.execSQL("CREATE TABLE IF NOT EXISTS tbl_performance_flags (_id INTEGER PRIMARY KEY, perf_remotefx INTEGER, perf_gfx INTEGER, perf_gfx_h264 INTEGER, perf_wallpaper INTEGER, perf_theming INTEGER, perf_full_window_drag INTEGER, perf_menu_animations INTEGER, perf_font_smoothing INTEGER, perf_desktop_composition INTEGER);");
        String sqlManualBookmarks = getManualBookmarksCreationString();
        db.execSQL(sqlManualBookmarks);
    }

    private void upgradeTables(SQLiteDatabase db) {
        for (String table : DB_TABLES) {
            String tmpTable = DB_BACKUP_PREFIX + table;
            List<String> newColumns = GetColumns(db, table);
            List<String> columns = GetColumns(db, tmpTable);
            if (columns != null) {
                columns.retainAll(newColumns);
                String cols = joinStrings(columns, ",");
                String query = String.format("INSERT INTO %s (%s) SELECT %s from '%s'", table, cols, cols, tmpTable);
                db.execSQL(query);
            }
        }
    }

    private void downgradeTables(SQLiteDatabase db) {
        for (String table : DB_TABLES) {
            String tmpTable = DB_BACKUP_PREFIX + table;
            List<String> oldColumns = GetColumns(db, table);
            List<String> columns = GetColumns(db, tmpTable);
            if (oldColumns != null) {
                oldColumns.retainAll(columns);
                String cols = joinStrings(oldColumns, ",");
                String query = String.format("INSERT INTO %s (%s) SELECT %s from '%s'", table, cols, cols, tmpTable);
                db.execSQL(query);
            }
        }
    }

    private List<String> getTableNames(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        List<String> list = new ArrayList<>();
        try {
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                    list.add(name);
                    cursor.moveToNext();
                }
            }
            return list;
        } finally {
            cursor.close();
        }
    }

    private void insertDefault(SQLiteDatabase db) {
        ContentValues screenValues = new ContentValues();
        screenValues.put(DB_KEY_SCREEN_COLORS, (Integer) 32);
        screenValues.put(DB_KEY_SCREEN_RESOLUTION, (Integer) (-2));
        screenValues.put(DB_KEY_SCREEN_WIDTH, (Integer) 1024);
        screenValues.put(DB_KEY_SCREEN_HEIGHT, (Integer) 768);
        long idScreen = db.insert(DB_TABLE_SCREEN, null, screenValues);
        long idScreen3g = db.insert(DB_TABLE_SCREEN, null, screenValues);
        ContentValues performanceValues = new ContentValues();
        performanceValues.put(DB_KEY_PERFORMANCE_RFX, (Integer) 1);
        performanceValues.put(DB_KEY_PERFORMANCE_GFX, (Integer) 1);
        performanceValues.put(DB_KEY_PERFORMANCE_H264, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_WALLPAPER, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_THEME, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_DRAG, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_MENU_ANIMATIONS, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_FONTS, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_COMPOSITION, (Integer) 0);
        long idPerformance = db.insert(DB_TABLE_PERFORMANCE, null, performanceValues);
        long idPerformance3g = db.insert(DB_TABLE_PERFORMANCE, null, performanceValues);
        ContentValues bookmarkValues = new ContentValues();
        bookmarkValues.put(DB_KEY_BOOKMARK_LABEL, "Local Pc");
        bookmarkValues.put(DB_KEY_BOOKMARK_HOSTNAME, "127.0.0.1");
        bookmarkValues.put(DB_KEY_BOOKMARK_USERNAME, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_PASSWORD, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_DOMAIN, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_PORT, "3389");
        bookmarkValues.put(DB_KEY_SCREEN_SETTINGS, Long.valueOf(idScreen));
        bookmarkValues.put(DB_KEY_SCREEN_SETTINGS_3G, Long.valueOf(idScreen3g));
        bookmarkValues.put(DB_KEY_PERFORMANCE_FLAGS, Long.valueOf(idPerformance));
        bookmarkValues.put(DB_KEY_PERFORMANCE_FLAGS_3G, Long.valueOf(idPerformance3g));
        bookmarkValues.put(DB_KEY_BOOKMARK_REDIRECT_SDCARD, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_REDIRECT_SOUND, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_REDIRECT_MICROPHONE, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_SECURITY, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_REMOTE_PROGRAM, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_WORK_DIR, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_ASYNC_CHANNEL, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_ASYNC_INPUT, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_ASYNC_UPDATE, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_CONSOLE_MODE, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_DEBUG_LEVEL, "DEBUG");
        db.insert(DB_TABLE_BOOKMARK, null, bookmarkValues);
    }

    private void insertLocalSocket(SQLiteDatabase db) {
        ContentValues screenValues = new ContentValues();
        screenValues.put(DB_KEY_SCREEN_COLORS, (Integer) 32);
        screenValues.put(DB_KEY_SCREEN_RESOLUTION, (Integer) (-2));
        screenValues.put(DB_KEY_SCREEN_WIDTH, (Integer) 1024);
        screenValues.put(DB_KEY_SCREEN_HEIGHT, (Integer) 768);
        long idScreen = db.insert(DB_TABLE_SCREEN, null, screenValues);
        long idScreen3g = db.insert(DB_TABLE_SCREEN, null, screenValues);
        ContentValues performanceValues = new ContentValues();
        performanceValues.put(DB_KEY_PERFORMANCE_RFX, (Integer) 1);
        performanceValues.put(DB_KEY_PERFORMANCE_GFX, (Integer) 1);
        performanceValues.put(DB_KEY_PERFORMANCE_H264, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_WALLPAPER, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_THEME, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_DRAG, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_MENU_ANIMATIONS, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_FONTS, (Integer) 0);
        performanceValues.put(DB_KEY_PERFORMANCE_COMPOSITION, (Integer) 0);
        long idPerformance = db.insert(DB_TABLE_PERFORMANCE, null, performanceValues);
        long idPerformance3g = db.insert(DB_TABLE_PERFORMANCE, null, performanceValues);
        ContentValues bookmarkValues = new ContentValues();
        bookmarkValues.put(DB_KEY_BOOKMARK_LABEL, "Local socket");
        bookmarkValues.put(DB_KEY_BOOKMARK_HOSTNAME, "/dev/msl/rdp/rdp_socket");
        bookmarkValues.put(DB_KEY_BOOKMARK_USERNAME, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_PASSWORD, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_DOMAIN, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_PORT, "3389");
        bookmarkValues.put(DB_KEY_SCREEN_SETTINGS, Long.valueOf(idScreen));
        bookmarkValues.put(DB_KEY_SCREEN_SETTINGS_3G, Long.valueOf(idScreen3g));
        bookmarkValues.put(DB_KEY_PERFORMANCE_FLAGS, Long.valueOf(idPerformance));
        bookmarkValues.put(DB_KEY_PERFORMANCE_FLAGS_3G, Long.valueOf(idPerformance3g));
        bookmarkValues.put(DB_KEY_BOOKMARK_REDIRECT_SDCARD, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_REDIRECT_SOUND, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_REDIRECT_MICROPHONE, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_SECURITY, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_REMOTE_PROGRAM, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_WORK_DIR, "");
        bookmarkValues.put(DB_KEY_BOOKMARK_ASYNC_CHANNEL, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_ASYNC_INPUT, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_ASYNC_UPDATE, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_CONSOLE_MODE, (Integer) 0);
        bookmarkValues.put(DB_KEY_BOOKMARK_DEBUG_LEVEL, "DEBUG");
        db.insert(DB_TABLE_BOOKMARK, null, bookmarkValues);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("BookmarkDB", " onCreate BookmarkDB ");
        createDB(db);
        insertDefault(db);
        insertLocalSocket(db);
    }

    private String getManualBookmarksCreationString() {
        return "CREATE TABLE IF NOT EXISTS tbl_manual_bookmarks (_id INTEGER PRIMARY KEY, label TEXT NOT NULL, hostname TEXT NOT NULL, username TEXT NOT NULL, password TEXT, domain TEXT, port TEXT, screen_settings INTEGER NOT NULL, performance_flags INTEGER NOT NULL, enable_gateway_settings INTEGER DEFAULT 0, gateway_hostname TEXT, gateway_port INTEGER DEFAULT 443, gateway_username TEXT, gateway_password TEXT, gateway_domain TEXT, enable_3g_settings INTEGER DEFAULT 0, screen_3g INTEGER NOT NULL, performance_3g INTEGER NOT NULL, redirect_sdcard INTEGER DEFAULT 0, redirect_sound INTEGER DEFAULT 0, redirect_microphone INTEGER DEFAULT 0, security INTEGER, remote_program TEXT, work_dir TEXT, async_channel INTEGER DEFAULT 0, async_input INTEGER DEFAULT 0, async_update INTEGER DEFAULT 0, console_mode INTEGER, debug_level TEXT DEFAULT 'INFO', FOREIGN KEY(screen_settings) REFERENCES tbl_screen_settings(_id), FOREIGN KEY(performance_flags) REFERENCES tbl_performance_flags(_id), FOREIGN KEY(screen_3g) REFERENCES tbl_screen_settings(_id), FOREIGN KEY(performance_3g) REFERENCES tbl_performance_flags(_id) );";
    }

    private void recreateDB(SQLiteDatabase db) {
        String[] strArr;
        for (String table : DB_TABLES) {
            String query = "DROP TABLE IF EXISTS '" + table + "'";
            db.execSQL(query);
        }
        onCreate(db);
    }

    private void upgradeDB(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            dropOldTables(db);
            backupTables(db);
            createDB(db);
            upgradeTables(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            dropOldTables(db);
        }
    }

    private void downgradeDB(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            dropOldTables(db);
            backupTables(db);
            createDB(db);
            downgradeTables(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            dropOldTables(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                upgradeDB(db);
                return;
            default:
                recreateDB(db);
                return;
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        downgradeDB(db);
    }
}
