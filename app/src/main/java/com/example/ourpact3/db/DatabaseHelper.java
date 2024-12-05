package com.example.ourpact3.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE exception_list (appName TEXT PRIMARY KEY, readable INTEGER, writable INTEGER)");
        //
        db.execSQL("CREATE TABLE apps (" +
                "package_name TEXT PRIMARY KEY, " +
                "writable INTEGER, " +
                "readable INTEGER, " +
                "comment TEXT, " +
                "enabled INTEGER)");
        // Create table for which filter
        db.execSQL("CREATE TABLE smart_filters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "window_action TEXT, " +
                "warning INTEGER, " +
                "kill INTEGER, " +
                "enabled INTEGER, " +
                "comment TEXT, " +
                "filter_type TEXT)");   // special smartfilter can be detected with the special smart filter name and then added differtly
        // combination table map n filters to one app
        db.execSQL("CREATE TABLE app_filters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "app_package_name TEXT, " +
                "filter_id INTEGER, " +
                "FOREIGN KEY (app_package_name) REFERENCES apps (package_name), " +
                "FOREIGN KEY (filter_id) REFERENCES smart_filters (id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS exception_list");
        onCreate(db);
    }
}

