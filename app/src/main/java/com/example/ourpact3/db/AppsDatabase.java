package com.example.ourpact3.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

/**
 * List here all Entities and no DAOs
 */
@Database(entities = {AppEntity.class, TimeRestrictionRuleEntity.class, UsageFiltersEntity.class, ContentFiltersEntity.class, AppContentFilterEntity.class, WordEntity.class,WordListEntity.class, LanguageEntity.class, ExceptionListEntity.class, ContentFilterToAppEntity.class}, version = 1)
public abstract class AppsDatabase extends RoomDatabase
{
    /*
    List here only DAOs
     */
    public abstract AppDao appsDao();
    public abstract UsageFiltersDao usageFiltersDao();
    public abstract TimeRestrictionRulesDao timeRestrictionRulesDao();
    public abstract ContentFiltersDao contentFiltersDao();
    public abstract AppContentFilterDao appContentFilterDao();
    public abstract LanguageDao languageDao();
    public abstract ExceptionListDao exceptionListDao();
    public abstract WordDao wordDao();
    public abstract WordListDao wordListDao();
    public abstract ContentFilterToAppDao contentFilterToAppDao();
}
