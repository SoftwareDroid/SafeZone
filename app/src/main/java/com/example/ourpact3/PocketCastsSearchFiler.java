package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class PocketCastsSearchFiler {
    PocketCastsSearchFiler(AccessibilityService service) {
        this.service = service;
    }

    public AccessibilityService service;
    public String packageName = "au.com.shiftyjelly.pocketcasts";
    public String evilWord = "cat";
    private String LOG_TAG = "ContentFiler";
    private int SEARCH_DELAY_MS = 500;
    private final int MAX_DELAYED_CALLS = 3;
    private int delayCount = 0;


    private Handler handler = new Handler();
    private Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            delayCount = 0;
            AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
            if (rootNode != null) {
                Log.d(LOG_TAG, "Start Search");

                boolean result = searchNode(rootNode, "cat");
                if (result) {
                    Log.d(LOG_TAG, "Found a evil keyword ");
                }
            }
        }
    };

    @SuppressLint("NewApi")
    public void processEvent(AccessibilityEvent event) {
        if(!event.getPackageName().toString().equals(packageName))
        {
            return;
        }

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                // We delay an wait for more evens and postpone it at nax maxNumDelays times
                if (delayCount < MAX_DELAYED_CALLS) {
                    handler.removeCallbacks(searchRunnable);
                    handler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
                    delayCount++;

                } else {
                    searchRunnable.run();
                }
                break;
            default:
        }
    }

    private boolean searchNode(AccessibilityNodeInfo node, String text) {
//        Log.d(LOG_TAG," search in: " + node);
        if (node.getText() != null && node.getText().length() > 1) {
            boolean foo = node.isEditable();
            Log.d(LOG_TAG, "NODE_TEXT: " + node.getText() + " \n Editable: " + foo);
            if (node.getText().toString().contains("hasjdaid")) {
                return true;
            }
        }

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo childNode = node.getChild(i);
            if (childNode != null && searchNode(childNode, text)) {
                return true;
            }
        }

        return false;
    }
}
