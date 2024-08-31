package com.example.ourpact3;

import com.example.ourpact3.model.AppGenericEventFilterBase;
import com.example.ourpact3.model.IFilterResultCallback;
import com.example.ourpact3.model.PipelineResultBase;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.model.TopicManager;
import com.example.ourpact3.model.WordProcessorFilterBase;

import java.util.ArrayList;

public class AppFilter
{
    AppFilter(ContentFilterService service, TopicManager topicManager, ArrayList<WordProcessorFilterBase> filters, String packageName)

    {
        this.service = service;
        this.topicManager = topicManager;
        this.keywordFilters = filters;
        this.packageName = packageName;
        this.isMagnificationEnabled = service.isMagnificationEnabled();
        this.genericEventFilters = new ArrayList<>();
    }



    public AccessibilityService service;
    private boolean isMagnificationEnabled; //needed beacuse node isVisible behaves differnt
    private String packageName = "";
    private boolean pipelineRunning = false;
    private String LOG_TAG = "ContentFiler";
    private int SEARCH_DELAY_MS = 500;
    private TopicManager topicManager;
    private final int MAX_DELAYED_CALLS = 3;
    private int delayCount = 0;
    private ArrayList<WordProcessorFilterBase> keywordFilters; //TODO: create and sort
    private ArrayList<AppGenericEventFilterBase> genericEventFilters;
    private IFilterResultCallback callback;

    public ArrayList<WordProcessorFilterBase> getAllFilters() {return keywordFilters;}
    public String getPackageName()
    {
        return packageName;
    }
    public void setCallback(IFilterResultCallback callback)
    {
        if (callback != null)
        {
            this.callback = callback;
        }
    }

    public void addGenericEventFilters(AppGenericEventFilterBase genericEventFilter)
    {
        this.genericEventFilters.add(genericEventFilter);
    }

    private final Handler handler = new Handler();
    private final Runnable searchRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            pipelineRunning = true;
            delayCount = 0;
            AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
            Log.d(LOG_TAG, "Start Search");
            if (rootNode != null)
            {

                for (WordProcessorFilterBase processor : keywordFilters)
                {
                    processor.reset();
                    processNode(rootNode, processor);
                    if (!pipelineRunning)
                    {
                        return;
                    }
                }
            }
        }
    };

    @SuppressLint("NewApi")
    public void processEvent(AccessibilityEvent event)
    {
        if (event.getPackageName() == null || !event.getPackageName().toString().equals(packageName))
        {
            return;
        }


        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                // First process generic filters but with viewer events
                for(AppGenericEventFilterBase genericFilter : this.genericEventFilters)
                {
                    PipelineResultBase result = genericFilter.OnAccessibilityEvent(event);
                    if(result != null)
                    {
                        result.triggerPackage = event.getPackageName().toString();
                        this.callback.onPipelineResult(result);
                        return;
                    }
                }
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if(handler.hasCallbacks(searchRunnable))
                {
                    return;
                }
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

    /**
     * @param node
     * @param currentFilter
     * @return
     */
    private void processNode(AccessibilityNodeInfo node, WordProcessorFilterBase currentFilter)
    {
        // public boolean isVisibleToUser ()
        //Between API 16 and API 29, this method may incorrectly return false when magnification is enabled. On other versions, a node is considered visible even if it is not on the screen because magnification is active.
        if ((isMagnificationEnabled || node.isVisibleToUser()) && node.getText() != null && node.getText().length() > 1)
        {
            String text = node.getText().toString();
            // feed word into filer
            PipelineResultBase result = currentFilter.feedWord(text, node.isEditable());
            if (result != null)
            {
                result.triggerPackage = this.packageName;
                // Forward result to callback
                this.callback.onPipelineResult(result);
                // Feed result to generic event filers
                for(AppGenericEventFilterBase genericFilter : this.genericEventFilters)
                {
                    PipelineResultBase genericResult = genericFilter.OnPipelineResult(result);
                    if(genericResult != null)
                    {
                        pipelineRunning = genericResult.windowAction == PipelineWindowAction.CONTINUE_PIPELINE;
                        break;
                    }
                }

                pipelineRunning = result.windowAction == PipelineWindowAction.CONTINUE_PIPELINE;
                if (!pipelineRunning)
                {
                    return;
                }
            }
            // abort all further processing with other processors

        }
        // process all children for current processor
        int childCount = node.getChildCount();
        for (int n = 0; n < childCount; n++)
        {
            AccessibilityNodeInfo childNode = node.getChild(n);
            if (childNode != null && pipelineRunning)
            {
                processNode(childNode, currentFilter);
            }
        }
    }
}
