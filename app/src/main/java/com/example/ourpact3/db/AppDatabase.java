package com.example.ourpact3.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {AppInfo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase
{
    public abstract AppDao packageDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "database_name")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

