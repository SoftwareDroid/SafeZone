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
import com.example.ourpact3.model.ScreenTextExtractor;
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
    private final boolean isMagnificationEnabled; //needed beacuse node isVisible behaves differnt
    private String packageName = "";
    private boolean pipelineRunning = false;
    private TopicManager topicManager;
    private int delayCount = 0;
    private final ArrayList<WordProcessorFilterBase> keywordFilters; //TODO: create and sort
    private final ArrayList<AppGenericEventFilterBase> genericEventFilters;
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
            ScreenTextExtractor.Screen screen = ScreenTextExtractor.extractTextElements(rootNode,isMagnificationEnabled);
            String LOG_TAG = "ContentFiler";
            Log.d(LOG_TAG, "Start Search");
            for (WordProcessorFilterBase processor : keywordFilters)
            {
                processor.reset();
                processScreen(screen, processor);
                if (!pipelineRunning)
                {
                    return;
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


        int MAX_DELAYED_CALLS = 3;
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
                    int SEARCH_DELAY_MS = 500;
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
     * @param
     * @param currentFilter
     * @return
     */
    private void processScreen(ScreenTextExtractor.Screen screen, WordProcessorFilterBase currentFilter)
    {
        for (ScreenTextExtractor.Screen.Node node : screen.nodes)
        {
            if (node.visible || !currentFilter.checkOnlyVisibleNodes)
            {
                String text = node.text;
                // feed word into filter
                PipelineResultBase result = currentFilter.feedWord(node);
                if (result != null)
                {
                    result.triggerPackage = this.packageName;
                    // Feed result to generic event filters
                    for (AppGenericEventFilterBase genericFilter : this.genericEventFilters)
                    {
                        PipelineResultBase genericResult = genericFilter.OnPipelineResult(result);
                        if (genericResult != null)
                        {
                            pipelineRunning = genericResult.windowAction == PipelineWindowAction.CONTINUE_PIPELINE;
                            return;
                        }
                    }
                    result.screen = screen;
                    // Forward result to callback
                    this.callback.onPipelineResult(result);

                    pipelineRunning = result.windowAction == PipelineWindowAction.CONTINUE_PIPELINE;
                    if (!pipelineRunning)
                    {
                        return;
                    }
                }
                // abort all further processing with other processors
            }
        }
    }
}
