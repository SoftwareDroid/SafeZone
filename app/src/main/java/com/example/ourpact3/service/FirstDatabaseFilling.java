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
        WordListEntity safeSearchList = new WordListEntity();
        safeSearchList.setName("SafeSearch");


        WordListEntity nsfwWordList = new WordListEntity();
        nsfwWordList.setName("NSFW");

        WordListEntity newsWordList = new WordListEntity();
        newsWordList.setName("News");
        long newsListId = db.wordListDao().insert(newsWordList);
        long safeSearchListId = db.wordListDao().insert(safeSearchList);
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
        words.add(createRegexWord("\\b(möse|venusberg|labia|scheide|schamlippen|genitalien|muschi|fotze|vulva|kitzler|klitoris)\\b", germanLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("schwanz", germanLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("penis", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("sperma", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("hoden", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
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
        words.add(createWordEntry("kondom", germanLangID, nsfwListId, SMALL_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("vibrationsring", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("keuschheitskäfig", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("peniskäfig", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("sexspielzeug", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("liebeskugel", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("nippelklemme", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("vibrator", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("dildo", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("strap-?on", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("analplug", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("dilator", englishLangID, nsfwListId, SMALL_PUNISHMENT, HIGH_PUNISHMENT));
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
        words.add(createWordEntry("sklavin", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("dienerin", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("erniedrigung", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("demütigung", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("femdom", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("findom", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        // safe search
        words.add(createRegexWord("^safe\\.duckduckgo.*&ia=chat$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^google\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^bing\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^ecosia.\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^lycos.\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^yahoo\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^excite\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^info\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^webcrawler\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^dogpile\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^baidu\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^search\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^boardreader\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^gigablast\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^mojeek\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^searx\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^brave\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^ecosia\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^metacrawler\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^mywebsearch\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^onesearch\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^lite.qwant\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^startpage\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^searchencrypt\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^torch\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^lite.startpage\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^qwant\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT,  BANNED_PUNSIHMENT));
        words.add(createRegexWord("^search\\.aol\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("^yandex\\.[a-z]{2,3}.*$", englishLangID, safeSearchListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));

        words.add(createRegexWord("\\bcum\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bsperm\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\brimming\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\banal\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\benema\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bbbw\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bcock\\b", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createRegexWord("\\bass\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createRegexWord("\\bnudes?\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\bbsdm\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bcbt\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bbutt\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bhottest\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bsexy\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bpee\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bsex\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\btwerking\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));

        words.add(createWordEntry("porn", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("cumshot", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("escort", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("penetrat", englishLangID, nsfwListId, SMALL_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("genitial", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("creampie", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("hentai", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("masturbate", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("chastity", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("hot body", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("hot yoga", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("asshole", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("butthole", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("erotic", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("mistress", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("xxx", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("sexual", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("cuckold", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("humiliation", englishLangID, nsfwListId, SMALL_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("softcore", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("figging", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("fetish", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("missionary", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("doggy style", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("cowgirl", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("fingering", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("submission", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("rim job", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("blowjob", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bhand(\\s?)job\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("quickie", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("spanking", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("ejaculation", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("foreplay", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("afterplay", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("squirt", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("analingus", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("pegging", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("fellatio", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("cunnilingus", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("dry humping", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("orgasm", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("lolita", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("scissoring", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("kama sutra", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("jerk off", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("climax", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("adult only", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("explicit material", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("dirty talk", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("self-pleasure", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("horny", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("bondage", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("toilet slav", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("human toilet", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("feminization", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("elastrator", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("female lead relationship", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("domina", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("latex", englishLangID, nsfwListId, SMALL_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("edge play", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("corset", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("shit", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("fart", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("furz", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("kacke", germanLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("piss", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("poop", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("asmr", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\b(boot|foot)\\s(worship)\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("sensual massage", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\bface(\\s?)sitting\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createRegexWord("\\b(male|slave|enslaved|submissive|pet|sissy|bottom|restrained|caged|locked|bound|gagged|tamed|trained|subjugated)\\s+(boy|male|sissy|man|training|torture)\\b", englishLangID, nsfwListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\b(girl|nurse|female|women|lady|princess|queen|goddess)\\s+(domination|supremacy|worship|control|authority|moaning)", englishLangID, nsfwListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\b(eat|inhale|taste|smell|sniff|lick)\\s(her|hers|my)\\b", englishLangID, nsfwListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\b(oral)\\s(sex|stimulation)\\b", englishLangID, nsfwListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\bscat\\b", englishLangID, nsfwListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\b(hot|sexy|seductive|tempting|spicy|naughty|cheeky|(plus\\ssize)|chubby|plump|curvy|luscious|voluptuous|fat|dominant|controlling|strong|powerful|commanding|decisive|alpha|master|top|dom)\\s+(gf|wife|girl|women|female|lady|teacher|nurse)\\b", englishLangID, nsfwListId, BANNED_PUNSIHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("breasts", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("cleavage", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("nipple", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("buttocks", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("booty", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("bottom", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("vagina", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("clitoris", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createWordEntry("ovaries", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("uterus", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("cervix", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("pubic hair", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("love tunnel", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("lady parts", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("butt plug", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("private parts", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("nether region", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("pudenda", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("labia", englishLangID, nsfwListId, HIGH_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("ejaculate", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("pecker", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        words.add(createWordEntry("scrotum", englishLangID, nsfwListId, HIGH_PUNISHMENT, HIGH_PUNISHMENT));
        // sex toys
        words.add(createWordEntry("testicle", englishLangID, nsfwListId, HIGH_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createWordEntry("urethra", englishLangID, nsfwListId, HIGH_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createWordEntry("prostate", englishLangID, nsfwListId, HIGH_PUNISHMENT, MEDIUM_PUNISHMENT));


        words.add(createRegexWord("\\btwat\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\bprick\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\btits\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createRegexWord("\\bbust\\b", englishLangID, nsfwListId, 0, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\bclit\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\bbra\\b", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createRegexWord("\\bcunt\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\bpussy\\b", englishLangID, nsfwListId, HIGH_PUNISHMENT, BANNED_PUNSIHMENT));
        words.add(createRegexWord("\\bbum\\b", englishLangID, nsfwListId, SMALL_PUNISHMENT, SMALL_PUNISHMENT));
        words.add(createRegexWord("\\btwerk(ing|s)\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));
        words.add(createRegexWord("\\bball(\\sstretcher|busting)\\b", englishLangID, nsfwListId, MEDIUM_PUNISHMENT, MEDIUM_PUNISHMENT));

        /*

//    "rod",
//    "shaft",
    "manhood",
    "family jewels",
    "balls",
    "testes",
    "scrotum",
    "nuts",
    "bollocks",
    "gonads",
    "semen",
    "erection",
    "boner",
    "prostata",
    "hard-on"
         */

        /*

  ],
  "regExpWords": [
    "\\bscat\\b"
  ],
  "includedTopics": [
    "porn_femdom",
    "porn_explicit",
    "female_body_parts",
    "adult_nudity",
    "adult_sex_toys"
//    "female_names",
//    "female_clothing"
  ]
}

         */
    }


}
