package com.example.ourpact3.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * List here all Entities and no DAOs
 */
@Database(entities = {AppsEntity.class, UsageFiltersEntity.class, ContentFiltersEntity.class, AppContentFilterEntity.class, WordListEntity.class,LanguageEntity.class, ExceptionListEntity.class}, version = 1)
public abstract class AppsDatabase extends RoomDatabase
{
    /*
    List here only DAOs
     */
    public abstract AppsDao appsDao();
    public abstract UsageFiltersDao usageFiltersDao();
    public abstract ContentFiltersDao contentFiltersDao();
    public abstract AppContentFilterDao appContentFilterDao();
    public abstract LanguageDao languageDao();
    public abstract ExceptionListDao exceptionListDao();
    public abstract WordListDao wordListDao();
}
