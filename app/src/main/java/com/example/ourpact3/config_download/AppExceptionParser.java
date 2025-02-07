package com.example.ourpact3.config_download;

import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.ExceptionListEntity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AppExceptionParser
{


    public List<ExceptionListEntity> parseAppExceptions(InputStreamReader reader, AppsDatabase db) throws Exception
    {
        List<ExceptionListEntity> appExceptions = new ArrayList<>();

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
                if (currentElement.equals("exception"))
                {
                    String app = parser.getAttributeValue(null, "app");
                    boolean visible = Boolean.parseBoolean(parser.getAttributeValue(null, "visible"));
                    boolean editable = Boolean.parseBoolean(parser.getAttributeValue(null, "editable"));
                    ExceptionListEntity newException = new ExceptionListEntity();
                    newException.setAppName(app);
                    newException.setReadable(visible);
                    newException.setWritable(editable);
                    appExceptions.add(newException);
                }

            }
            eventType = parser.next();
        }
        return appExceptions;
    }


}
