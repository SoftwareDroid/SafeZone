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
    public static WordEntity createRegexWord(String word, long languageID, long wordListID, int readPunishment, int writePunishment)
    {
        WordEntity w1 = new WordEntity();
        w1.setLanguageId(languageID);
        w1.setText("\\bpo\\b");
        w1.setTopicType(WordEntity.TOPIC_SCORED);
        w1.setRegex(true);
        w1.setReadScore(readPunishment);
        w1.setWriteScore(writePunishment);
        w1.setWordListID(wordListID);
        return w1;
    }

    public static ExceptionListEntity createException(String name,boolean readable, boolean writeable)
    {
        ExceptionListEntity exceptionList = new ExceptionListEntity();
        exceptionList.setAppName(name);
        exceptionList.setReadable(readable);
        exceptionList.setWritable(writeable);
        return exceptionList;
    }

    public static WordEntity createWordEntry(String word, long languageID, long wordListID, int readPunishment, int writePunishment)
    {
        WordEntity w1 = new WordEntity();
        w1.setLanguageId(languageID);
        w1.setText("\\bpo\\b");
        w1.setTopicType(WordEntity.TOPIC_SCORED);
        w1.setRegex(false);
        w1.setReadScore(readPunishment);
        w1.setWriteScore(writePunishment);
        w1.setWordListID(wordListID);
        return w1;
    }

    public void createExceptions(AppsDatabase db, Context context)
    {
        ArrayList<ExceptionListEntity> allExceptions = new ArrayList<ExceptionListEntity>();
        allExceptions.add(createException(context.getPackageName(), true, false));
        db.exceptionListDao().insert(allExceptions);
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
    }

    public void createNSFWWordList(Context context)
    {
        AppsDatabase db = Room.databaseBuilder(context, AppsDatabase.class, "apps-database")
                .allowMainThreadQueries()
                .build();


        createExceptions(db, context);

        LanguageEntity englishLang = new LanguageEntity();
        englishLang.setLongLanguageCode("en");
        englishLang.setLongLanguageCode("English");

        LanguageEntity germanLang = new LanguageEntity();
        germanLang.setLongLanguageCode("de");
        germanLang.setLongLanguageCode("German");

        long englishLangID = db.languageDao().insertLanguage(englishLang);
        long germanLangID = db.languageDao().insertLanguage(germanLang);
    }


}
