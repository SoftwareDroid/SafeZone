package com.example.ourpact3.config_download;

import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.LanguageEntity;
import com.example.ourpact3.db.WordEntity;
import com.example.ourpact3.db.WordListEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordAppExceptionParser
{


    public List<WordEntity> parseWordEntities(InputStreamReader reader, AppsDatabase db) throws Exception {


        List<WordEntity> wordEntities = new ArrayList<>();
        List<WordListEntity> wordLists = new ArrayList<>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(reader);

        int eventType = parser.getEventType();
        String currentElement = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                currentElement = parser.getName();

                // Parse WordListEntities
                if (currentElement.equals("WordListEntity")) {
                    String name = parser.getAttributeValue(null, "name");
                    String version = parser.getAttributeValue(null, "version");
                    WordListEntity wordListEntity = new WordListEntity();
                    wordListEntity.setName(name);
                    wordListEntity.setVersion(Integer.valueOf(version));
                    wordLists.add(wordListEntity);
                    // create word list first
                    WordListEntity wordList = db.wordListDao().getWordListByName(name);
                    if(wordList == null)
                    {
                        db.wordListDao().insert(wordListEntity);
                    }
                }


                // Parse WordEntities
                if ("WordEntity".equals(currentElement)) {
                    WordEntity wordEntity = new WordEntity();
                    wordEntity.setText(parser.getAttributeValue(null, "text"));
                    String shortLangCode = parser.getAttributeValue(null, "language");
                    LanguageEntity languageEntity = db.languageDao().getLanguageByShortCode(shortLangCode);
                    wordEntity.setLanguageId(languageEntity.getId());
                    wordEntity.setReadScore(Integer.parseInt(parser.getAttributeValue(null, "read_score")));
                    wordEntity.setWriteScore(Integer.parseInt(parser.getAttributeValue(null, "write_score")));
                    wordEntity.setExpressionGroupNumber(Integer.parseInt(parser.getAttributeValue(null, "group_number")));
                    wordEntity.setRegex(Boolean.parseBoolean(parser.getAttributeValue(null, "is_regex")));
                    wordEntity.setTopicType(Integer.parseInt(parser.getAttributeValue(null, "topic_type")));

                    // Get the word list ID from the database
                    String wordListName = parser.getAttributeValue(null, "word_list");
                    WordListEntity wordList = db.wordListDao().getWordListByName(wordListName);
                    long wordListID = wordList.getId();
                    wordEntity.setWordListID(wordListID);

                    wordEntities.add(wordEntity);
                }
            }
            eventType = parser.next();
        }

        // Optionally, you can save the word lists to the database here if needed
        // db.wordListDao().insertAll(wordLists);

        return wordEntities;
    }


}
