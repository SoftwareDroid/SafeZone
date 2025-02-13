package com.example.ourpact3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.ourpact3.config_download.AppExceptionParser;
import com.example.ourpact3.config_download.ContentFilterParser;
import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.ContentFilterEntity;
import com.example.ourpact3.db.ContentFilterToAppEntity;
import com.example.ourpact3.db.ExceptionListEntity;
import com.example.ourpact3.db.LanguageEntity;
import com.example.ourpact3.db.WordEntity;
import com.example.ourpact3.config_download.WordEntityParser;
import com.example.ourpact3.config_download.XMLDownloader;
import com.example.ourpact3.topics.TopicManager;

import org.junit.Test;

import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Pattern;

public class DownloadListFromGithubTest
{
    @Test
    public void testDownloadWordListTestCase() throws Exception
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create a new instance of the AppsDatabase
        AppsDatabase db = Room.databaseBuilder(context, AppsDatabase.class, "apps-database")
                .allowMainThreadQueries()
                .build();
        XMLDownloader downloader = new XMLDownloader();
        InputStreamReader wordStream = downloader.downloadXml("https://raw.githubusercontent.com/SoftwareDroid/SafeZoneData/refs/heads/main/word_list.xml");

        WordEntityParser wordEntityParser = new WordEntityParser();

        WordEntityParser.Result result = wordEntityParser.parseWordEntities(wordStream, db);
        db.wordDao().insertWordLists(result.wordEntities);
        for (WordEntity w : result.wordEntities)
        {
            Log.d("word", w.getText());
        }
        assertFalse(result.wordEntities.isEmpty());
        // Download app exceptions
        InputStreamReader exceptionStream = downloader.downloadXml("https://raw.githubusercontent.com/SoftwareDroid/SafeZoneData/refs/heads/main/app_exceptions.xml");
        AppExceptionParser newExceptionParser = new AppExceptionParser();
        List<ExceptionListEntity> exceptions = newExceptionParser.parseAppExceptions(exceptionStream, db);
        for (ExceptionListEntity e : exceptions)
        {
            Log.d("app", e.getAppName());
        }
        assertFalse(exceptions.isEmpty());
        InputStreamReader contentFiltersStream = downloader.downloadXml("https://raw.githubusercontent.com/SoftwareDroid/SafeZoneData/refs/heads/main/content_filters.xml");
        ContentFilterParser contentFilterParser = new ContentFilterParser();
        List<ContentFilterEntity> contentFilters = contentFilterParser.parseContentFilters(contentFiltersStream, db);
//        contentFilters.addAll(result.contentFilters);
        assertFalse(contentFilters.isEmpty());
        for (ContentFilterEntity e : contentFilters)
        {
            Log.d("ContentFilter", e.getName());
        }

        List<ContentFilterToAppEntity> filterInstances = db.contentFilterToAppDao().getAll();
        for(ContentFilterToAppEntity instance : filterInstances)
        {
            Log.d("FilterInstance ", instance.getPackageName());
        }

    }

    @Test
    public void testSearchAgainstDownloadedWords() throws Exception
    {
        //TODO use word groups too
        Pattern compiledRegex = Pattern.compile("\\bcat\\b");
        boolean testSearch = compiledRegex.matcher("my cat is red").find();
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create a new instance of the AppsDatabase
        AppsDatabase db = Room.databaseBuilder(context, AppsDatabase.class, "apps-database")
                .allowMainThreadQueries()
                .build();
        XMLDownloader downloader = new XMLDownloader();
        InputStreamReader wordStream = downloader.downloadXml("https://raw.githubusercontent.com/SoftwareDroid/SafeZoneData/refs/heads/main/word_list.xml");

        WordEntityParser wordEntityParser = new WordEntityParser();

        WordEntityParser.Result result = wordEntityParser.parseWordEntities(wordStream, db);
        db.wordDao().insertWordLists(result.wordEntities);

        TopicManager topicManager = new TopicManager();
        topicManager.db = db;
        long wordListID =  db.wordListDao().getWordListByName("all/NSFW_Filter").getId();
        ContentFilterEntity contentFilter = new ContentFilterEntity();
        contentFilter.setWordListID(wordListID);
        contentFilter.setIgnoreCase(true);
        contentFilter.setEnabled(true);
        long contentFilterID = db.contentFiltersDao().insertContentFilter(contentFilter);
        contentFilter.setId(contentFilterID);
        TopicManager.SearchResult2 searchResult = topicManager.isStringInWordList("her ass is fat",false,contentFilter);
        for(WordEntity match : searchResult.matches)
        {
            Log.d("Search matches: ", match.getText());

        }
        assertFalse(searchResult.matches.isEmpty());
        Log.d("Search: ", "Score " + searchResult.accumulatedScore);
    }
}
