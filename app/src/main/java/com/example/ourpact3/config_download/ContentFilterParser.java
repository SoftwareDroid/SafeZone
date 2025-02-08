package com.example.ourpact3.config_download;

import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.ContentFilterEntity;
import com.example.ourpact3.db.PipelineButtonActionConverter;
import com.example.ourpact3.db.WindowActionConverter;
import com.example.ourpact3.db.WordListEntity;
import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.smart_filter.NodeCheckStrategyType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ContentFilterParser
{


    public List<ContentFilterEntity> parseContentFilters(InputStreamReader reader, AppsDatabase db) throws Exception
    {
        List<ContentFilterEntity> contentFilters = new ArrayList<>();

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(reader);

        int eventType = parser.getEventType();
        String currentElement = null;

        while (eventType != XmlPullParser.END_DOCUMENT)
        {
            if (eventType == XmlPullParser.START_TAG)
            {
                currentElement = parser.getName();
                // Parse WordListEntities
                if (currentElement.equals("content_filter"))
                {
                    ContentFilterEntity filter = new ContentFilterEntity();
                    boolean explainable = Boolean.parseBoolean(parser.getAttributeValue(null, "explainable"));
                    boolean kill = Boolean.parseBoolean(parser.getAttributeValue(null, "kill"));
                    boolean enabled = Boolean.parseBoolean(parser.getAttributeValue(null, "enabled"));
                    boolean userCreated = Boolean.parseBoolean(parser.getAttributeValue(null, "user_created"));
                    boolean readable = Boolean.parseBoolean(parser.getAttributeValue(null, "readable"));
                    boolean writable = Boolean.parseBoolean(parser.getAttributeValue(null, "writable"));
                    boolean ignore_case = Boolean.parseBoolean(parser.getAttributeValue(null, "ignore_case"));
                    int appGroup = Integer.parseInt(parser.getAttributeValue(null, "app_group"));
                    int whatToCheck = Integer.parseInt(parser.getAttributeValue(null, "what_to_check"));
                    String shortDescription = parser.getAttributeValue(null, "short_description");
                    String name = parser.getAttributeValue(null, "name");

                    PipelineWindowAction windowAction = WindowActionConverter.fromInteger(Integer.parseInt(parser.getAttributeValue(null, "window_action")));
                    PipelineButtonAction buttonAction = PipelineButtonActionConverter.fromInteger(Integer.parseInt(parser.getAttributeValue(null, "button_action")));
                    String wordListName = parser.getAttributeValue(null, "word_list");
                    WordListEntity wordListEntity = db.wordListDao().getWordListByName(wordListName);
                    if (wordListEntity != null)
                    {
                        filter.setName(name);
                        filter.setExplainable(explainable);
                        filter.setKill(kill);
                        filter.setEnabled(enabled);
                        filter.setUserCreated(userCreated);
                        filter.setReadable(readable);
                        filter.setWritable(writable);
                        filter.setIgnoreCase(ignore_case);
                        filter.setAppGroup(appGroup);
                        filter.setWhatToCheck(NodeCheckStrategyType.BOTH);
                        filter.setShortDescription(shortDescription);
                        filter.setWindowAction(windowAction);
                        filter.setButtonAction(buttonAction);
                        filter.setWordListID(wordListEntity.getId());
                        contentFilters.add(filter);
                    }


                }

            }
            eventType = parser.next();
        }
        return contentFilters;
    }
}
