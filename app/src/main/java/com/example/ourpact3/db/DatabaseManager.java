package com.example.ourpact3.db;
import android.content.ContentValues;
import android.database.Cursor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.smart_filter.UsageRestrictionsFilter;

import java.util.ArrayList;
import java.util.List;

/**example usage
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         setContentView(R.layout.activity_main);
 *
 *         dbHelper = new DatabaseHelper(this);
 *         dbManager = new DatabaseManager(this);
 *
 *         dbManager.open();
 *
 *         dbManager.insertException("com.example.app", true, false);
 *
 *         Cursor cursor = dbManager.getException("com.example.app");
 *         if (cursor.moveToFirst()) {
 *             String appName = cursor.getString(0);
 *             boolean readable = cursor.getInt(1) == 1;
 *             boolean writable = cursor.getInt(2) == 1;
 *             Log.d("MainActivity", "appName: " + appName + ", readable: " + readable + ", writable: " + writable);
 *             Toast.makeText(this, "appName: " + appName + ", readable: " + readable + ", writable: " + writable, Toast.LENGTH_SHORT).show();
 *         }
 *
 *         dbManager.close();
 *     }
 * }
 */

public class DatabaseManager
{
    public static DatabaseHelper dbHelper;
    public  static SQLiteDatabase db;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public static void open() {
        db = dbHelper.getWritableDatabase();
    }

    public static void close() {
        dbHelper.close();
    }

    public static class ExceptionTuple
    {
        public String packageID;
        public String appName;
        public boolean readable;
        public boolean writable;

        public ExceptionTuple(String packageID, boolean readable, boolean writable)
        {
            this.packageID = packageID;
            this.appName = packageID;
            this.readable = readable;
            this.writable = writable;
        }
    }

    public static class AppRuleTuple
    {
        public String packageID;
        public String appName;
        public String comment;
        public boolean readable;
        public boolean writeable;
        public boolean enabled;
        public Integer usageFilterID;
        public AppRuleTuple(String packageID, String comment, boolean readable, boolean writeable, boolean enabled) {
            this.packageID = packageID;
            this.comment = comment;
            this.readable = readable;
            this.writeable = writeable;
            this.enabled = enabled;
        }

    }

    public static class Word
    {
        public long id;
        public String word;
        public boolean isRegex;
        public String comment;

        public Word(long id, String word, boolean isRegex, String comment)
        {
            this.id = id;
            this.word = word;
            this.isRegex = isRegex;
            this.comment = comment;
        }
    }

    public static class Language
    {
        public long id;
        public String name;
        public String code;

        public Language(long id, String name, String code)
        {
            this.id = id;
            this.name = name;
            this.code = code;
        }
    }

    public static class WordList
    {
        public long id;
        public String name;
        public long languageId;
        public boolean readable;
        public boolean writable;
        public String description;

        public WordList(long id, String name, long languageId, boolean readable, boolean writable, String description)
        {
            this.id = id;
            this.name = name;
            this.languageId = languageId;
            this.readable = readable;
            this.writable = writable;
            this.description = description;
        }
    }

    public static class WordListWord
    {
        public long wordListId;
        public long wordId;

        public WordListWord(long wordListId, long wordId)
        {
            this.wordListId = wordListId;
            this.wordId = wordId;
        }
    }

    public static class WordLanguage
    {
        public long wordId;
        public long languageId;

        public WordLanguage(long wordId, long languageId)
        {
            this.wordId = wordId;
            this.languageId = languageId;
        }
    }

    public static class WordListSublist
    {
        public long parentListId;
        public long childListId;

        public WordListSublist(long parentListId, long childListId)
        {
            this.parentListId = parentListId;
            this.childListId = childListId;
        }
    }

    public boolean needInitialFilling() {
        try {
            Cursor cursor = db.rawQuery("SELECT 1 FROM exception_list", null);
            boolean hasEntries = cursor.getCount() > 0;
            cursor.close();
            return !hasEntries;
        } catch (Exception e) {
            return false;
        }
    }

    public void insertBatchExceptions(List<ExceptionTuple> exceptions) {
        db.beginTransaction();
        try {
            for (ExceptionTuple exception : exceptions) {
                db.execSQL("INSERT INTO exception_list (appName, readable, writable) VALUES ('" + exception.packageID + "', " + (exception.readable ? 1 : 0) + ", " + (exception.writable ? 1 : 0) + ")");
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertBatchAppRules(List<AppRuleTuple> appRules) {
        db.beginTransaction();
        try {
            SQLiteStatement statement = db.compileStatement("INSERT INTO apps (package_name, writable, readable, comment, enabled,usage_filter_id) VALUES (?, ?, ?, ?, ?,?)");
            for (AppRuleTuple appRule : appRules) {
                statement.bindString(1, appRule.packageID);
                statement.bindLong(2, appRule.writeable ? 1 : 0);
                statement.bindLong(3, appRule.readable ? 1 : 0);
                statement.bindString(4, appRule.comment);
                statement.bindLong(5, appRule.enabled ? 1 : 0);
                CounterAction defaultCounterAction = new CounterAction(PipelineWindowAction.WARNING, PipelineButtonAction.BACK_BUTTON,true);
                long usageFilterId = UsageSmartFilterManager.addOrUpdateUsageFilter(new UsageRestrictionsFilter(defaultCounterAction,"Usage Restriction",60*60,5*60,10,new ArrayList<>()));

                statement.bindLong(6,usageFilterId);  // no default use restriction exists

                statement.executeInsert();
                statement.clearBindings();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertException(String appName, boolean readable, boolean writable) {
        db.execSQL("INSERT INTO exception_list (appName, readable, writable) VALUES ('" + appName + "', " + (readable ? 1 : 0) + ", " + (writable ? 1 : 0) + ")");
    }

    public void updateException(String appName, boolean readable, boolean writable) {
        db.execSQL("UPDATE exception_list SET readable = " + (readable ? 1 : 0) + ", writable = " + (writable ? 1 : 0) + " WHERE appName = '" + appName + "'");
    }

    public void deleteException(String appName) {
        db.execSQL("DELETE FROM exception_list WHERE appName = '" + appName + "'");
    }

    public List<ExceptionTuple> getAllExceptions() {
        List<ExceptionTuple> exceptions = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM exception_list", null);
        while (cursor.moveToNext()) {
            String appName = cursor.getString(0);
            boolean readable = cursor.getInt(1) == 1;
            boolean writable = cursor.getInt(2) == 1;
            exceptions.add(new ExceptionTuple(appName, readable, writable));
        }
        cursor.close();
        return exceptions;
    }

    /**
     * retrieves all all rules
     * @return
     */
    public List<AppRuleTuple> getAllAppRules() {
        List<AppRuleTuple> appRules = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM apps", null);
        while (cursor.moveToNext()) {
            String packageName = cursor.getString(0);
            boolean writable = cursor.getInt(1) == 1;
            boolean readable = cursor.getInt(2) == 1;
            String comment = cursor.getString(3);
            boolean enabled = cursor.getInt(4) == 1;
            AppRuleTuple appRuleTuple = new AppRuleTuple(packageName, comment, readable, writable, enabled);
            appRuleTuple.usageFilterID = cursor.getInt(5);
            appRules.add(appRuleTuple);
        }
        cursor.close();
        return appRules;
    }

    /**
     * return the AppRule for a packageId
     * @param packageId
     * @return
     */
    public static AppRuleTuple getAppRuleByPackageId(String packageId) {
        AppRuleTuple appRuleTuple = null;
        Cursor cursor = db.rawQuery("SELECT * FROM apps WHERE package_name = ?", new String[]{packageId});

        if (cursor.moveToFirst()) { // Check if there is at least one result
            String packageName = cursor.getString(0);
            boolean writable = cursor.getInt(1) == 1;
            boolean readable = cursor.getInt(2) == 1;
            String comment = cursor.getString(3);
            boolean enabled = cursor.getInt(4) == 1;
            appRuleTuple = new AppRuleTuple(packageName, comment, readable, writable, enabled);
            appRuleTuple.usageFilterID = cursor.getInt(5);
        }

        cursor.close();
        return appRuleTuple; // Returns null if no match is found
    }


    public Cursor getException(String appName) {
        return db.rawQuery("SELECT * FROM exception_list WHERE appName = '" + appName + "'", null);
    }

    //
    public void addWordToList(long wordListId, String word, boolean isRegex, String comment) {
        if (wordListId <= 0) {
            throw new IllegalArgumentException("Word list ID must be greater than 0");
        }
        if (word == null || word.isEmpty()) {
            throw new IllegalArgumentException("Word must not be null or empty");
        }
        long wordId = getWordId(word);
        if (wordId <= 0) {
            wordId = addWord(word, isRegex, comment);
        }
        db.execSQL("INSERT INTO word_list_words (word_list_id, word_id) VALUES (" + wordListId + ", " + wordId + ")");
    }

    private long getWordId(String word) {
        Cursor cursor = db.rawQuery("SELECT id FROM words WHERE word = ?", new String[] {word});
        if (cursor.moveToFirst()) {
            long wordId = cursor.getLong(0);
            cursor.close();
            return wordId;
        } else {
            cursor.close();
            return 0;
        }
    }

    private long addWord(String word, boolean isRegex, String comment) {
        ContentValues values = new ContentValues();
        values.put("word", word);
        values.put("is_regex", isRegex ? 1 : 0);
        values.put("comment", comment != null ? comment : "");
        return db.insert("words", null, values);
    }
    /*
    public long addWordList(String name, long languageId, boolean readable, boolean writable, String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Word list name must not be null or empty");
        }
        if (languageId <= 0) {
            throw new IllegalArgumentException("Language ID must be greater than 0");
        }
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("language_id", languageId);
        values.put("readable", readable ? 1 : 0);
        values.put("writable", writable ? 1 : 0);
        values.put("description", description != null ? description : "");
        return db.insert("word_lists", null, values);
    }*/

    public void defineAsSublist(long parentListId, long childListId) {
        if (parentListId <= 0 || childListId <= 0) {
            throw new IllegalArgumentException("Parent list ID and child list ID must be greater than 0");
        }
        db.execSQL("INSERT INTO word_list_sublists (parent_list_id, child_list_id) VALUES (" + parentListId + ", " + childListId + ")");
    }

    public void removeWordFromList(long wordListId, long wordId) {
        if (wordListId <= 0 || wordId <= 0) {
            throw new IllegalArgumentException("Word list ID and word ID must be greater than 0");
        }
        db.execSQL("DELETE FROM word_list_words WHERE word_list_id = " + wordListId + " AND word_id = " + wordId);
    }

    public void removeWordList(long wordListId) {
        if (wordListId <= 0) {
            throw new IllegalArgumentException("Word list ID must be greater than 0");
        }
        db.execSQL("DELETE FROM word_lists WHERE id = " + wordListId);
    }

    public void removeSublist(long parentListId, long childListId) {
        if (parentListId <= 0 || childListId <= 0) {
            throw new IllegalArgumentException("Parent list ID and child list ID must be greater than 0");
        }
        db.execSQL("DELETE FROM word_list_sublists WHERE parent_list_id = " + parentListId + " AND child_list_id = " + childListId);
    }
    public long addLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language must not be null");
        }
        ContentValues values = new ContentValues();
        values.put("name", language.name);
        values.put("code", language.code);
        return db.insert("languages", null, values);
    }

    public long addOrUpdateWordList(WordList wordList) {
        if (wordList == null) {
            throw new IllegalArgumentException("Word list must not be null");
        }
        if (wordList.name == null || wordList.name.isEmpty()) {
            throw new IllegalArgumentException("Word list name must not be null or empty");
        }
        if (wordList.languageId <= 0) {
            throw new IllegalArgumentException("Language ID must be greater than 0");
        }
        ContentValues values = new ContentValues();
        values.put("name", wordList.name);
        values.put("language_id", wordList.languageId);
        values.put("readable", wordList.readable ? 1 : 0);
        values.put("writable", wordList.writable ? 1 : 0);
        values.put("description", wordList.description != null ? wordList.description : "");
        if (wordList.id > 0) {
            db.update("word_lists", values, "id = ?", new String[] {String.valueOf(wordList.id)});
            return wordList.id;
        } else {
            return db.insert("word_lists", null, values);
        }
    }

}
