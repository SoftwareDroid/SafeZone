package com.example.ourpact3.service;

import android.content.Context;

import androidx.room.Room;

import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.LanguageEntity;
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


    public void createNSFWWordList(Context context)
    {
        AppsDatabase db = Room.databaseBuilder(context, AppsDatabase.class, "apps-database")
                .allowMainThreadQueries()
                .build();
        LanguageEntity englishLang = new LanguageEntity();
        englishLang.setLongLanguageCode("en");
        englishLang.setLongLanguageCode("English");

        LanguageEntity germanLang = new LanguageEntity();
        germanLang.setLongLanguageCode("de");
        germanLang.setLongLanguageCode("German");

        long englishLangID = db.languageDao().insertLanguage(englishLang);
        long germanLangID = db.languageDao().insertLanguage(germanLang);
        // create list
        WordListEntity nsfwWordList = new WordListEntity();
        nsfwWordList.setName("NSFW");

        WordListEntity newsWordList = new WordListEntity();
        newsWordList.setName("News");
        long newsListId = db.wordListDao().insert(newsWordList);

        long nsfwListId = db.wordListDao().insert(nsfwWordList);
        // german
        int SMALL_PUNISHMENT = 15;
        int MEDIUM_PUNISHMENT = 32; // allow 2
        int HIGH_PUNISHMENT = 55;   //allow 1
        int BANNED_PUNSIHMENT = 100;
        WordDao wordDao = db.wordDao();
        //
        //TODO: was ist wirklich als regex benötigt aufräumen schwanz
        ArrayList<WordEntity> words = new ArrayList<>();
        words.add(createWordEntry("tagesschau", germanLangID, newsListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        // NSFW LIST
        words.add(createRegexWord("\\bnackt\\b", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createRegexWord("\\bpopo\\b", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createRegexWord("\\bpo\\b", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, 0));
        words.add(createRegexWord("\\bpo-loch\\b", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, 0));
        words.add(createRegexWord("\\beier\\s*(treten|foltern|folter|tritt)\\b", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\b(mädchen|freundin|herrin|schwester|frau)\\s*(lecken|befriedigen)\\b", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\b(möse|venusberg|labia|scheide|schamlippen|genitalien|vagina|muschi|fotze|vulva|kitzler|klitoris)\\b", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("schwanz", germanLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("penis", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("pimmel", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("entblößt", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("barbusig", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("barbusig", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("unbedeckt", germanLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("entkleidet", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("unbekleidet", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("oben ohne", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createRegexWord("\\b(bumsen|vögeln|ficken|blasen|analverkehr|oralverkehr|geschlechtsverkehr|masturbieren)\\b", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        // Sex Toys
        words.add(createWordEntry("massagstab", germanLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("kondom", germanLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("vibrationsring", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("keuschheitskäfig", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("peniskäfig", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("sexspielzeug", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("liebeskugel", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("nippelklemme", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("vibrator", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("dildo", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("analplug", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("dilator", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("masturbator", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        // Body parts
        words.add(createWordEntry("arschloch", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("gesäß", germanLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("hintern", germanLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("hinterteil", germanLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("busen", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("möpse", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("titten", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("brüste", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("pobacke", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("schambehaarung", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\barsch\\b", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        //kleider
        words.add(createWordEntry("thong", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("tanga", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("lingerie", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("unterwäsche", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("reizwäsche", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("mini rock", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("hot pants", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("panty", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        // sex stuff
        words.add(createWordEntry("pups", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("prostitution", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("furz", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("prostitution", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("gesichtssitzen", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("masturbieren", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("scheiße", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("sexuell", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("orgasmus", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createRegexWord("\\bintim\\b", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createRegexWord("\\bherrin\\b", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("domina", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("sklavin", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("dienerin", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("erniedrigung", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("demütigung", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("femdom", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));




    }
}
