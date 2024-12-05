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
        // more tables for these special filters in particular word filter need several tables
        db.execSQL("CREATE TABLE word_lists (" +
                "id TEXT PRIMARY KEY, " +
                "language TEXT, " +
                "description TEXT)");

        db.execSQL("CREATE TABLE word_list_words (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "word_list_id TEXT, " +
                "word TEXT, " +
                "FOREIGN KEY (word_list_id) REFERENCES word_lists (id))");

        db.execSQL("CREATE INDEX idx_word_list_words_word_list_id ON word_list_words (word_list_id)");
        db.execSQL("CREATE INDEX idx_word_list_words_word ON word_list_words (word)");

        db.execSQL("CREATE TABLE word_list_regex (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "word_list_id TEXT, " +
                "regex TEXT, " +
                "FOREIGN KEY (word_list_id) REFERENCES word_lists (id))");

        db.execSQL("CREATE INDEX idx_word_list_regex_word_list_id ON word_list_regex (word_list_id)");

        db.execSQL("CREATE TABLE word_list_sublists (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "word_list_word_id INTEGER, " +
                "sublist_word TEXT, " +
                "FOREIGN KEY (word_list_word_id) REFERENCES word_list_words (id))");

        db.execSQL("CREATE INDEX idx_word_list_sublists_word_list_word_id ON word_list_sublists (word_list_word_id)");
        db.execSQL("CREATE INDEX idx_word_list_sublists_sublist_word ON word_list_sublists (sublist_word)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS exception_list");
        onCreate(db);
    }
}

