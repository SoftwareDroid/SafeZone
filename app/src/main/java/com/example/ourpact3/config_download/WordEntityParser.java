package com.example.ourpact3.config_download;

import android.util.Log;

import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.ContentFilterEntity;
import com.example.ourpact3.db.ContentFilterToAppEntity;
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
                if(currentElement.equals("language"))
                {
                    String shortCode = parser.getAttributeValue(null, "short");
                    String longName = parser.getAttributeValue(null, "long");
                    // create language if not exist
                    if(db.languageDao().getLanguageByShortCode(shortCode) == null)
                    {
                        LanguageEntity language = new LanguageEntity();
                        language.setShortLanguageCode(shortCode);
                        language.setLongLanguageCode(longName);
                        db.languageDao().insertLanguage(language);
                    }
                }
                else
                if (currentElement.equals("WordListEntity")) {
                    String name = parser.getAttributeValue(null, "name");
                    String version = parser.getAttributeValue(null, "version");
                    String appName = parser.getAttributeValue(null, "app");
                    WordListEntity wordListEntity = new WordListEntity();
                    wordListEntity.setName(name);
                    wordListEntity.setApp(appName);
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
                            filter.setAppGroup(appName);
                            filter.setWhatToCheck(NodeCheckStrategyType.BOTH);
                            filter.setShortDescription("auto generated from " + name);
                            //this stops further processing
                            filter.setWindowAction(PipelineWindowAction.NO_WARNING_AND_STOP);
                            filter.setButtonAction(PipelineButtonAction.NONE);
                            filter.setWordListID(wordListEntity.getId());
                            long insertedFilterID = db.contentFiltersDao().insertContentFilter(filter);
                            // Create only for valid package ids
                            if(appName.contains("."))
                            {
                                // Only create Exception if not already exists
                                if(db.contentFilterToAppDao().getByPriority(ContentFilterToAppEntity.DEFAULT_CREATED_EXCEPTION_PRIORITY) == null)
                                {

                                    ContentFilterToAppEntity contentFilterToAppEntity = new ContentFilterToAppEntity();
                                    contentFilterToAppEntity.setPackageName(appName);
                                    contentFilterToAppEntity.setContentFilterID(insertedFilterID);
                                    // Set a very small ID
                                    contentFilterToAppEntity.setPriority(ContentFilterToAppEntity.DEFAULT_CREATED_EXCEPTION_PRIORITY);
                                    db.contentFilterToAppDao().insert(contentFilterToAppEntity);
                                }
                            }
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
