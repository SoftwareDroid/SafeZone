package com.example.ourpact3.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AppsEntity.class, UsageFiltersEntity.class, ContentFiltersEntity.class, AppContentFilterEntity.class}, version = 1)
public abstract class AppsDatabase extends RoomDatabase
{
    public abstract AppsDao appsDao();
    public abstract UsageFiltersDao usageFiltersDao();
    public abstract ContentFiltersDao contentFiltersDao();
    public abstract AppContentFilterDao appContentFilterDao();
}
