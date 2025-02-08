package com.example.ourpact3.service;

import android.content.Context;

import androidx.room.Room;

import com.example.ourpact3.db.AppEntity;
import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.ExceptionListEntity;
import com.example.ourpact3.db.LanguageEntity;
import com.example.ourpact3.db.UsageFiltersEntity;
import com.example.ourpact3.db.WordDao;
import com.example.ourpact3.db.WordEntity;
import com.example.ourpact3.db.WordListEntity;

import java.util.ArrayList;

public class FirstDatabaseFilling
{

    public void createSystemFilter()
    {

    }

    public void createAppEntry(AppsDatabase db,String name,boolean readable,boolean writable, boolean checkAll)
    {
        AppEntity defaultApp = new AppEntity();
        defaultApp.setEnabled(true);
        defaultApp.setReadable(readable);
        defaultApp.setWritable(writable);
        defaultApp.setCheckAllEvents(checkAll);
        defaultApp.setPackageName(name);
        //
        UsageFiltersEntity usageFiltersEntity = new UsageFiltersEntity();
        usageFiltersEntity.setEnabled(false);
        long usageFilterID = db.usageFiltersDao().insert(usageFiltersEntity);

        defaultApp.setUsageFilterId(usageFilterID);
        db.appsDao().insertApp(defaultApp);
    }
    //TODO: this is not not needed as user can export the datebase
    /*
    public void createAppEntries(AppsDatabase db, Context context)
    {
        // default filter
        createAppEntry(db,"",true,true,true);
        // other filter
        createAppEntry(db,"au.com.shiftyjelly.pocketcasts",true,true,true);
        createAppEntry(db,"org.telegram.messenger",true,true,false);
        createAppEntry(db,"org.mozilla.firefox",true,true,true);
        createAppEntry(db,"org.schabi.newpipe",true,true,true);
        createAppEntry(db,"com.google.android.packageinstaller",true,true,true);
        createAppEntry(db,"de.ard.audiothek",true,true,true);
        createAppEntry(db,"org.nuclearfog.apollo",true,true,false);
        createAppEntry(db,"com.android.settings",true,true,true);
        createAppEntry(db,"org.telegram.messenger",true,true,true);
    }*/

}
