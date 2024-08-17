package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.ourpact3.model.FilerAppAction;

import java.util.ArrayList;
import java.util.List;

public class PocketCastsSearchFiler
{
    PocketCastsSearchFiler(AccessibilityService service)

    {
        this.service = service;
        filters = new ArrayList<WordProcessorFilterBase>();
        // Add test Filter
        WordProcessorFilterBase ignoreSearch = new WordListFilterExact(new ArrayList<>(List.of("Recent searches","CLEAR ALL")),false,new ArrayList<>(List.of(
                FilerAppAction.NOTHING)));
        filters.add(ignoreSearch);
    }

    public AccessibilityService service;
    public String packageName = "au.com.shiftyjelly.pocketcasts";
    private String LOG_TAG = "ContentFiler";
    private int SEARCH_DELAY_MS = 500;
    private final int MAX_DELAYED_CALLS = 3;
    private int delayCount = 0;
    private boolean pipelineRunning = false;
    private ArrayList<WordProcessorFilterBase> filters; //TODO: create and sort

    private Handler handler = new Handler();
    private Runnable searchRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            pipelineRunning = true;
            for(WordProcessorFilterBase processor : filters)
            {
                processor.reset();
            }
            delayCount = 0;
            AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
            if (rootNode != null)
            {
                Log.d(LOG_TAG, "Start Search");
                processNode(rootNode);
            }
        }
    };

    @SuppressLint("NewApi")
    public void processEvent(AccessibilityEvent event)
    {
        if (!event.getPackageName().toString().equals(packageName))
        {
            return;
        }

        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                // We delay an wait for more evens and postpone it at nax maxNumDelays times
                if (delayCount < MAX_DELAYED_CALLS)
                {
                    handler.removeCallbacks(searchRunnable);
                    handler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
                    delayCount++;

                } else
                {
                    searchRunnable.run();
                }
                break;
            default:
        }
    }

    private void processNode(AccessibilityNodeInfo node)
    {
//        Log.d(LOG_TAG," search in: " + node);
        // process all nodes with text
        if (node.getText() != null && node.getText().length() > 1)
        {
            String text = node.getText().toString();
            for(WordProcessorFilterBase processor : filters)
            {
                // feed word into filer
                if(processor.feedWord(text,node.isEditable()))
                {
                    // processor finished
                    for (FilerAppAction action : processor.getActions())
                    {
                        switch (action) {
                            case LOGGING:
                                Log.d(LOG_TAG,  " " + processor + " Filter needs to log this: " + text);
                                break;
                            case PIPELINE_ABORT:
                                Log.d(LOG_TAG,"Pipline aborted by " + text);
                                pipelineRunning = false;
                                return;
                        }
                    }
                }
            }
            Log.d(LOG_TAG, "NODE_TEXT: " + node.getText() + " \n Editable: " + node.isEditable());
        }

        int childCount = node.getChildCount();
        for (int n = 0; n < childCount; n++)
        {
            AccessibilityNodeInfo childNode = node.getChild(n);
            if (childNode != null && pipelineRunning)
            {
                processNode(childNode);
            }
        }
    }
}
