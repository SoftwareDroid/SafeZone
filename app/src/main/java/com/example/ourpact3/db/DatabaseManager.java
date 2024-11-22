package com.example.ourpact3.db;
import android.database.Cursor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

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
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
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

    public boolean needInitialFilling() {
        try {
            Cursor cursor = db.rawQuery("SELECT 1 FROM exception_list", null);
            boolean hasEntries = cursor.getCount() > 0;
            cursor.close();
            return hasEntries;
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

    public Cursor getException(String appName) {
        return db.rawQuery("SELECT * FROM exception_list WHERE appName = '" + appName + "'", null);
    }
}
