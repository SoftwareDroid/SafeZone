package com.example.ourpact3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.LanguageEntity;
import com.example.ourpact3.db.WordEntity;
import com.example.ourpact3.service.WordEntityParser;
import com.example.ourpact3.service.XMLDownloader;

import org.junit.Test;

import java.io.InputStreamReader;
import java.util.List;

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
        LanguageEntity enLang = new LanguageEntity();
        enLang.setShortLanguageCode("en");
        enLang.setLongLanguageCode("english");
        db.languageDao().insertLanguage(enLang);
        LanguageEntity deLang = new LanguageEntity();

        deLang.setShortLanguageCode("de");
        deLang.setLongLanguageCode("german");
        db.languageDao().insertLanguage(deLang);

        XMLDownloader downloader = new XMLDownloader();
        InputStreamReader wordStream = downloader.downloadXml("https://raw.githubusercontent.com/SoftwareDroid/SafeZoneData/refs/heads/main/word_list.xml");

        WordEntityParser wordEntityParser = new WordEntityParser();

        List<WordEntity> words = wordEntityParser.parseWordEntities(wordStream, db);
        for(WordEntity w: words)
        {
            Log.d("word", w.getText());
        }
    }
}
