package com.example.ourpact3;

import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ParsedWordFilter {

    private String version;
    private List<WordGroup> wordGroups;
    private Set<String> ignoredApps = new TreeSet<>();

    public ParsedWordFilter(XmlResourceParser inputStream) throws XmlPullParserException, IOException {

        wordGroups = new ArrayList<>();

        int eventType = inputStream.getEventType(); //TODO: app ingnore list
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                String tagName = inputStream.getName();

                if (tagName.equals("filter")) {
                    version = inputStream.getAttributeValue(null, "version");
                } else if (tagName.equals("group")) {
                    WordGroup wordGroup = new WordGroup();
                    wordGroup.setName(inputStream.getAttributeValue(null, "name"));
                    String lang = inputStream.getAttributeValue("topics/en", "lang");

                    wordGroup.setRead(inputStream.getAttributeIntValue(null, "read", 0));
                    wordGroup.setWrite(inputStream.getAttributeIntValue(null, "write", 0));

                    wordGroups.add(wordGroup);
                } else if (tagName.equals("word")) {
                    String word = inputStream.nextText();
                    WordGroup lastGroup = wordGroups.get(wordGroups.size() - 1);
                    lastGroup.addWord(word);
                } else if (tagName.equals("app")) {
                    String appName = inputStream.nextText();
                    ignoredApps.add(appName);
                }
            }
            eventType = inputStream.next();
        }
    }


    public Set<String> getIgnoredApps() {
        return ignoredApps;
    }

    public String getVersion() {
        return version;
    }

    public List<WordGroup> getWordGroups() {
        return wordGroups;
    }

    public static class WordGroup {
        private String name;
        private String language;
        private int read;
        private int write;
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

        public int getReadScore() {
            return read;
        }

        public void setRead(int read) {
            this.read = read;
        }

        public int getWriteScore() {
            return write;
        }

        public void setWrite(int write) {
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