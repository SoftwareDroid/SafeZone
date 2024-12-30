package com.example.ourpact3.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper
{

    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create table for which filter
        /*db.execSQL("CREATE TABLE smart_filters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "window_action INTEGER, " +
                "warning INTEGER, " +
                "kill INTEGER, " +  //boolean
                "enabled INTEGER, " +
                "comment TEXT, " +
                "id_in_one_filter_table INTEGER, " + // can refer do different tables
                "filter_type INTEGER,"+
                "package_name TEXT, " + // Add package_name column for foreign key reference
                "FOREIGN KEY (package_name) REFERENCES apps(package_name) ON DELETE CASCADE"
                +")");*/

        db.execSQL("CREATE TABLE exception_list (appName TEXT PRIMARY KEY, readable INTEGER, writable INTEGER)");
        //every app was its rules and one line in this table
        db.execSQL("CREATE TABLE apps (" +
                "package_name TEXT PRIMARY KEY, " +
                "writable INTEGER, " +
                "readable INTEGER, " +
                "comment TEXT, " +
                "enabled INTEGER,"+
                "usage_filter_id INTEGER NOT NULL, " + // Foreign key column
                "FOREIGN KEY (usage_filter_id) REFERENCES usage_filters(id) ON DELETE CASCADE" // Foreign key constraint
                +")");

        // special smartfilter can be detected with the special smart filter name and then added differtly
        // parameters which define the productivity filters
        db.execSQL("CREATE TABLE usage_filters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "window_action INTEGER, " +
                "button_action INTEGER, " +
                "kill INTEGER, " +  //boolean
                "enabled INTEGER, " +
                "reset_period INTEGER, "+   //in seconds
                "time_limit INTEGER,"+       //in seconds
                "max_starts INTEGER"+       // max starts between rests
                ")");
        // Every Usage filter can have n restrictions
        db.execSQL("CREATE TABLE time_restriction_rules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usage_filter_id INTEGER, " +  // Define the column for the foreign key
                "monday INTEGER, "+   //active on this day
                "tuesday INTEGER,"+       //active on this day
                "wednesday INTEGER,"+       // active on this day
                "thursday INTEGER,"+       // active on this day
                "friday INTEGER,"+       // active on this day
                "saturday INTEGER,"+       // active on this day
                "sunday INTEGER,"+       // active on this day
                "start_hour INTEGER,"+
                "start_min INTEGER,"+
                "end_hour INTEGER,"+
                "end_min INTEGER,"+
                "black_list INTEGER,"+
                "FOREIGN KEY (usage_filter_id) REFERENCES usage_filters(id) ON DELETE CASCADE" + // Foreign key constraint
                ")");


        // combination table map n filters to one app
        db.execSQL("CREATE TABLE app_filters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "app_package_name TEXT, " +
                "filter_id INTEGER, " +
                "FOREIGN KEY (app_package_name) REFERENCES apps (package_name), " +
                "FOREIGN KEY (filter_id) REFERENCES smart_filters (id))");
        // more tables for these special filters in particular word filter need several tables

        db.execSQL("CREATE TABLE word_lists (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "language_id INTEGER, " +
                "readable INTEGER, " +
                "writable INTEGER, " +
                "description TEXT, " +
                "FOREIGN KEY (language_id) REFERENCES languages (id))");

        db.execSQL("CREATE TABLE words (" +
                "id INTEGER PRIMARY KEY, " +
                "word TEXT, " +
                "is_regex INTEGER, " +
                "comment TEXT)");

        db.execSQL("CREATE TABLE languages (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "code TEXT)");

        db.execSQL("CREATE TABLE word_list_words (" +
                "word_list_id INTEGER, " +
                "word_id INTEGER, " +
                "PRIMARY KEY (word_list_id, word_id), " +
                "FOREIGN KEY (word_list_id) REFERENCES word_lists (id), " +
                "FOREIGN KEY (word_id) REFERENCES words (id))");

        db.execSQL("CREATE TABLE word_list_sublists (" +
                "parent_list_id INTEGER, " +
                "child_list_id INTEGER, " +
                "PRIMARY KEY (parent_list_id, child_list_id), " +
                "FOREIGN KEY (parent_list_id) REFERENCES word_lists (id), " +
                "FOREIGN KEY (child_list_id) REFERENCES word_lists (id))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("DROP TABLE IF EXISTS exception_list");
        onCreate(db);
    }

    /*public boolean[] isStringInTopicBatch(String[] texts, int topicId, String language, boolean checkAgainstLowerCase)
    {
        boolean[] results = new boolean[texts.length];
        SQLiteDatabase db = getWritableDatabase();
        String[] selectionArgs = new String[texts.length + 2];
        selectionArgs[0] = String.valueOf(topicId);
        selectionArgs[1] = language;
        for (int i = 0; i < texts.length; i++)
        {
            selectionArgs[i + 2] = checkAgainstLowerCase ? texts[i].toLowerCase() : texts[i];
        }
        String query = "SELECT w.word FROM word_lists wl JOIN word_list_words wlw ON wl.id = wlw.word_list_id JOIN words w ON wlw.word_id = w.id JOIN languages l ON wl.language_id = l.id WHERE wl.id = ? AND l.code = ? AND w.word IN (" + getCommaSeparatedValues(texts.length) + ")";
        Cursor cursor = db.rawQuery(query, selectionArgs);
        Set<String> matchingWords = new HashSet<>();
        while (cursor.moveToNext())
        {
            matchingWords.add(cursor.getString(0));
        }
        cursor.close();
        for (int i = 0; i < texts.length; i++)
        {
            results[i] = matchingWords.contains(selectionArgs[i + 2]);
        }
        return results;
    }*/

    /*private String getCommaSeparatedValues(int length)
    {
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            values.append("?");
            if (i < length - 1)
            {
                values.append(",");
            }
        }
        return values.toString();
    }*/
}

