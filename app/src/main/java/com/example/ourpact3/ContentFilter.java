package com.example.ourpact3;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;

public class ContentFilter {
    private static int FILTER_THRESHOLD = 100;
    private static final String[] TARGET_KEYWORDS = {"girl", "ass"};

    public static class FilterResult {
        public int score;
        public boolean filterOut = false;
        public String keywords;
    }
/*
node.getClassName().equals("android.widget.EditText") ||
            node.getClassName().equals("android.widget.TextView") && node.isEditable() ||
            node.getClassName().equals("android.widget.SearchView"

 */
    public FilterResult runFiler(AccessibilityNodeInfo node) {
     FilterResult result = new ContentFilter.FilterResult();
        if (node != null) {
            String appName = node.getPackageName().toString();
            String nodeText = node.getText().toString().toLowerCase();
            Log.i("FOO", " search in " + nodeText);
            for (String keyword : ContentFilter.TARGET_KEYWORDS) {
                if (nodeText.contains(keyword))
                {
                    result.filterOut = true;
                    result.keywords = keyword;
                    result.score = 100;
                    return result;
                }
            }
        }
        return result;
    }
}
