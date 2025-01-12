package com.example.ourpact3.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashSet;
import java.util.Set;

public class DatabaseHelper extends SQLiteOpenHelper
{

    public static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create table for which filter
        db.execSQL("CREATE TABLE exception_list (appName TEXT PRIMARY KEY, readable INTEGER, writable INTEGER)");
        //every app was its rules and one line in this table
        db.execSQL("CREATE TABLE apps (" +
                "package_name TEXT PRIMARY KEY, " +
                "writable INTEGER, " +
                "readable INTEGER, " +
                "comment TEXT, " +
                "enabled INTEGER," +
                "check_all_events INTEGER," +
                "usage_filter_id INTEGER NOT NULL, " + // Foreign key column
                "FOREIGN KEY (usage_filter_id) REFERENCES usage_filters(id) ON DELETE CASCADE" // Foreign key constraint
                + ")");

        // special smartfilter can be detected with the special smart filter name and then added differtly
        // parameters which define the productivity filters
        db.execSQL("CREATE TABLE usage_filters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "explainable INTEGER, " +
                "window_action INTEGER, " +
                "button_action INTEGER, " +
                "kill INTEGER, " +  //boolean
                "enabled INTEGER, " +
                "reset_period INTEGER, " +   //in seconds
                "time_limit INTEGER," +       //in seconds
                "max_starts INTEGER" +       // max starts between rests
                ")");
        // Every Usage filter can have n restrictions
        db.execSQL("CREATE TABLE time_restriction_rules (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usage_filter_id INTEGER, " +  // Define the column for the foreign key
                "monday INTEGER, " +   //active on this day
                "tuesday INTEGER," +       //active on this day
                "wednesday INTEGER," +       // active on this day
                "thursday INTEGER," +       // active on this day
                "friday INTEGER," +       // active on this day
                "saturday INTEGER," +       // active on this day
                "sunday INTEGER," +       // active on this day
                "start_hour INTEGER," +
                "start_min INTEGER," +
                "end_hour INTEGER," +
                "end_min INTEGER," +
                "black_list INTEGER," +
                "FOREIGN KEY (usage_filter_id) REFERENCES usage_filters(id) ON DELETE CASCADE" + // Foreign key constraint
                ")");
        ////////////////////////////////////////////////////////////////////////
        // Content filters
        // the priority is defined by the order in row
        // now its no longer relevant, if a word list is fixed or not this handels the database
        // by adding a app system rules are automaticlyy included like Enforce SafeSearch and Porn block // different Rules shown in every app
        db.execSQL("CREATE TABLE content_filters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                // counter action
                "explainable INTEGER, " +
                "window_action INTEGER, " +
                "button_action INTEGER, " +
                "kill INTEGER, " +  //boolean
                // end of counter action
                "enabled INTEGER, " +
                "user_created INTEGER, " + // maybe relevant
                "app_group INTEGER, " + //Edit of shared filters can affect multible apps also between differn groups like 0=All, 1=Browsers, 2= Non_browsers
                "readable INTEGER, " + // maybe relevant
                "writable INTEGER, " + //maybe relevant. Like Write protection
                "name TEXT, " +
                "short_description TEXT," +
                "checks_only_visible INTEGER," + // boolean
                "what_to_check INTEGER," + // Only Editable, Only none editable, both, TODO
                "ignore_case INTEGER," + //TODO
                "type_of_list INTEGER," + // extact,scoring
                "id_for_list INTEGER" +
                ")");
        // A app can have n content filters and they can be shared among k apps (if shared attribute is on)
        // Create the junction table for the many-to-many relationship
        db.execSQL("CREATE TABLE app_content_filter (" +
                "app_package_name TEXT, " +
                "content_filter_id INTEGER, " +
                "PRIMARY KEY (app_package_name, content_filter_id), " +
                "FOREIGN KEY (app_package_name) REFERENCES apps(package_name) ON DELETE CASCADE, " +
                "FOREIGN KEY (content_filter_id) REFERENCES content_filters(id) ON DELETE CASCADE" +
                ")");
        /////////////////////////////////////////////////////////////////////////////////////// end of content filters


        // scored topics
        db.execSQL("CREATE TABLE topic_scored (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT," +
                "lower_case_topic INTEGER) ");
        // exact list topics
        db.execSQL("CREATE TABLE topic_exact (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT)");


        db.execSQL("CREATE TABLE word_list (" +
                "id INTEGER PRIMARY KEY, " +
                "text TEXT, " +
                "language_id INTEGER, " +
                "is_regex INTEGER, " +
                "topic_type INTEGER, " +    // either scored of exact more info are then word_scores or word groups
                "topic_id INTEGER, " + // this is either topic 1 (scored or topic two"
                "FOREIGN KEY (language_id) REFERENCES languages (id))");   // every word belongs to a topic, scoring  and word groups is save in a different table

        // seperate scoring from word entries
        db.execSQL("CREATE TABLE word_scores (" +
                "id INTEGER PRIMARY KEY, " +
                "word_id INTEGER, " +
                "read INTEGER, " +
                "write INTEGER, " +
                "FOREIGN KEY (word_id) REFERENCES word_list (id))");

        // for exact filter
        db.execSQL("CREATE TABLE word_groups (" +
                "id INTEGER PRIMARY KEY, " +
                "word_id INTEGER, " +
                "group_nr INTEGER, " +  // for or/and expression ame group is and
                "FOREIGN KEY (word_id) REFERENCES word_list (id))");


        // combination table map n filters to one app
        /*db.execSQL("CREATE TABLE app_filters (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "app_package_name TEXT, " +
                "filter_id INTEGER, " +
                "FOREIGN KEY (app_package_name) REFERENCES apps (package_name), " +
                "FOREIGN KEY (filter_id) REFERENCES smart_filters (id))");*/

        /*// more tables for these special filters in particular word filter need several tables


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

    */
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("DROP TABLE IF EXISTS exception_list");
        onCreate(db);
    }

}

