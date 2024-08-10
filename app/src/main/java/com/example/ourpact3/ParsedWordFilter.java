package com.example.ourpact3;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ParsedWordFilter {

    private String version;
    private List<WordGroup> wordGroups;
    private Set<String> ignoredApps;

    public ParsedWordFilter(InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, null);

        wordGroups = new ArrayList<>();

        int eventType = parser.getEventType(); //TODO: app ingnore list
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals("adult_filer")) {
                    version = parser.getAttributeValue(null, "version");
                } else if (parser.getName().equals("group")) {
                    WordGroup wordGroup = new WordGroup();
                    wordGroup.setName(parser.getAttributeValue(null, "name"));
                    wordGroup.setRead(parser.getAttributeValue(null, "read"));
                    wordGroup.setWrite(parser.getAttributeValue(null, "write"));
                    wordGroups.add(wordGroup);
                } else if (parser.getName().equals("word")) {
                    String word = parser.nextText();
                    WordGroup lastGroup = wordGroups.get(wordGroups.size() - 1);
                    lastGroup.addWord(word);
                }
            } else if (parser.getName().equals("app")) {
                String appName = parser.nextText();
                ignoredApps.add(appName);
            }
        }
        eventType = parser.next();
    }


public Set<String> getIgnoredApps() {return ignoredApps;}
public String getVersion() {
    return version;
}

public List<WordGroup> getWordGroups() {
    return wordGroups;
}

public static class WordGroup {
    private String name;
    private String read;
    private String write;
    private List<String> words;

    public WordGroup() {
        words = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getWrite() {
        return write;
    }

    public void setWrite(String write) {
        this.write = write;
    }

    public List<String> getWords() {
        return words;
    }

    public void addWord(String word) {
        words.add(word);
    }
}
}