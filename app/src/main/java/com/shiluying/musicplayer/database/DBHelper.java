package com.shiluying.musicplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    public final static String MUSICNAME_TABLE_NAME = "MusicName";
    public final static String MUSICLIST_TABLE_NAME = "MusicList";
    public final static String MUSICLISTNAME_TABLE_NAME = "MusicListName";
    public final static String RECORD_MUSICLIST_ID = "musiclistid";
    public final static String RECORD_MUSICLIST_NAME = "musiclistname";
    public final static String RECORD_MUSIC_ID = "musicid";
    public final static String RECORD_MUSIC_NAME = "musicname";
    public static final String[] MUSICLIST_TABLE_COLUMNS = {
            RECORD_MUSICLIST_ID,
            RECORD_MUSIC_ID
    };
    public static final String[] MUSICLISTNAME_TABLE_COLUMNS = {
            RECORD_MUSICLIST_ID,
            RECORD_MUSICLIST_NAME
    };
    public static final String[] MUSICNAME_TABLE_COLUMNS = {
            RECORD_MUSIC_ID,
            RECORD_MUSIC_NAME
    };

    public DBHelper(Context context) {
        super(context, "Database.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + MUSICLIST_TABLE_NAME + " (" +
                    RECORD_MUSICLIST_ID + " INTEGER NOT NULL," +
                    RECORD_MUSIC_ID + " INTEGER NOT NULL)";
            db.execSQL(sql);
            Log.i("TEST","初始化数据库成功");
            sql = "CREATE TABLE IF NOT EXISTS " + MUSICLISTNAME_TABLE_NAME + " (" +
                    RECORD_MUSICLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    RECORD_MUSICLIST_NAME + " TEXT NOT NULL)";
            db.execSQL(sql);
            Log.i("TEST","初始化数据库成功!");

            sql = "CREATE TABLE IF NOT EXISTS " + MUSICNAME_TABLE_NAME + " (" +
                    RECORD_MUSIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    RECORD_MUSIC_NAME + " TEXT NOT NULL)";
            db.execSQL(sql);
            Log.i("TEST","初始化数据库成功!!");
        }catch (Exception e) {
            Log.i("TEST","初始化数据库失败!!"+e);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public boolean deleteDatabase(Context context) {
        return context.deleteDatabase("Database.db");
    }

}
