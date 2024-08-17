package com.example.ourpact3;

import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;
import android.content.res.XmlResourceParser;

import java.util.List;

public class ContentFilter {
    private static final int FILTER_THRESHOLD = 100;
    private ParsedWordFilter adultFilter;

    ContentFilter(XmlResourceParser inputStream) {
        try {
            adultFilter = new ParsedWordFilter(inputStream);
        } catch (Exception e) {
            // handle both XmlPullParserException and IOException
            Log.e("Exception", "Error parsing or reading XML", e);
        }
    }

    public static class FilterResult {
        public int score;
        public boolean filterOut = false;
        public String keywords = "";
    }

    public FilterResult runFiler(AccessibilityNodeInfo node) {
        FilterResult result = new ContentFilter.FilterResult();
        /*int sumScore = 0;
        if (node != null && adultFilter != null) {
            String appName = node.getPackageName().toString();
            if (adultFilter.getIgnoredApps().contains(appName)) {
                Log.i("FOO", "ignore app");
                return result;
            }
            boolean isInputNode = node.getClassName().equals("android.widget.EditText") ||
                    node.getClassName().equals("android.widget.TextView") && node.isEditable() ||
                    node.getClassName().equals("android.widget.SearchView");
            List<ParsedWordFilter.WordGroup> wordGroups = adultFilter.getWordGroups();
            String nodeText = node.getText().toString().toLowerCase();

            for (ParsedWordFilter.WordGroup group : wordGroups) {
                for (String keyword : group.getWords()) {
                    if (nodeText.contains(keyword)) {
                        sumScore += isInputNode ? group.getWriteScore() : group.getReadScore();
                        result.keywords += " " + keyword;

                        if (sumScore >= FILTER_THRESHOLD) {
                            result.filterOut = true;
                            result.score = sumScore;
                            return result;
                        }
                    }
                }
            }
        }*/
        return result;
    }
}
