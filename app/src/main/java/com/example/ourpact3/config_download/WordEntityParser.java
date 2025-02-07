package com.example.ourpact3.config_download;

import android.util.Log;

import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.ContentFilterEntity;
import com.example.ourpact3.db.LanguageEntity;
import com.example.ourpact3.db.WordEntity;
import com.example.ourpact3.db.WordListEntity;
import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.smart_filter.NodeCheckStrategyType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordEntityParser {

    public class Result {
        public List<WordEntity> wordEntities = new ArrayList<>();
        public List<WordListEntity> wordLists = new ArrayList<>();
        public List<ContentFilterEntity> contentFilters = new ArrayList<>();
    }


    public Result parseWordEntities(InputStreamReader reader, AppsDatabase db) throws Exception {

        Result result = new Result();
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
                    result.wordLists.add(wordListEntity);
                    // create word list first
                    WordListEntity wordList = db.wordListDao().getWordListByName(name);
                    if(wordList == null)
                    {
                        db.wordListDao().insert(wordListEntity);
                        if(name.endsWith("/Exceptions"))
                        {
                            ContentFilterEntity filter = new ContentFilterEntity();
                            filter.setName(name);
                            filter.setExplainable(false);
                            filter.setKill(false);
                            filter.setEnabled(true);
                            filter.setUserCreated(false);
                            filter.setReadable(true);
                            filter.setWritable(true);
                            filter.setIgnoreCase(true);
                            filter.setAppGroup(1);
                            filter.setWhatToCheck(NodeCheckStrategyType.BOTH);
                            filter.setShortDescription("auto generated from " + name);
                            //this stops further processing
                            filter.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
                            filter.setButtonAction(PipelineButtonAction.NONE);
                            filter.setWordListID(wordListEntity.getId());
                            result.contentFilters.add(filter);
                        }

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
                    if(wordList == null)
                    {
                        Log.d("WordList not found",wordListName);
                        return null;
                    }
                    long wordListID = wordList.getId();
                    wordEntity.setWordListID(wordListID);

                    result.wordEntities.add(wordEntity);
                }
            }
            eventType = parser.next();
        }

        // Optionally, you can save the word lists to the database here if needed
        // db.wordListDao().insertAll(wordLists);

        return result;
    }


}
