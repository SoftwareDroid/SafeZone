package com.example.ourpact3;

import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.service.IFilterResultCallback;
import com.example.ourpact3.pipeline.PipelineResultBase;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.service.ScreenInfoExtractor;
import com.example.ourpact3.topics.TopicManager;
import com.example.ourpact3.smart_filter.WordProcessorSmartFilterBase;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class AppFilter
{
    AppFilter(ContentFilterService service, TopicManager topicManager, ArrayList<WordProcessorSmartFilterBase> filters, String packageName)

    {
        this.service = service;
        this.topicManager = topicManager;
        this.keywordFilters = filters;
        this.packageName = packageName;
        this.isMagnificationEnabled = service.isMagnificationEnabled();
        this.specialSmartFilters = new TreeMap<>();
    }


    public AccessibilityService service;
    private final boolean isMagnificationEnabled; //needed beacuse node isVisible behaves differnt
    private String packageName = "";
    private boolean pipelineRunning = false;
    private TopicManager topicManager;
    private int delayCount = 0;
    private final ArrayList<WordProcessorSmartFilterBase> keywordFilters; //TODO: create and sort
    private final TreeMap<SpecialSmartFilterBase.Name, SpecialSmartFilterBase> specialSmartFilters;
    private IFilterResultCallback callback;

    public ArrayList<WordProcessorSmartFilterBase> getAllFilters()
    {
        return keywordFilters;
    }

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
    public SpecialSmartFilterBase getSpecialSmartFilter(SpecialSmartFilterBase.Name name)
    {
        return this.specialSmartFilters.get(name);
    }

    public void setSpecialSmartFilter(SpecialSmartFilterBase.Name name, SpecialSmartFilterBase filter)
    {
        this.specialSmartFilters.put(name, filter);
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
            ScreenInfoExtractor.Screen screen = ScreenInfoExtractor.extractTextElements(rootNode, isMagnificationEnabled);
            String LOG_TAG = "ContentFiler";
            // First Check generic filters

            for (WordProcessorSmartFilterBase processor : keywordFilters)
            {
                processor.reset();
                processScreen(screen, processor);
                if (!pipelineRunning)
                {
                    return;
                }
            }
            // Send end of pipeline
            endOfPipelineReached(screen);

        }
    };

    @SuppressLint("NewApi")
    public void processEvent(AccessibilityEvent event)
    {
        // Empty string is default app this is every other app
        if (event.getPackageName() == null || (!event.getPackageName().toString().equals(packageName) && !packageName.isEmpty()))
        {
            return;
        }


        int MAX_DELAYED_CALLS = 3;
        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                // First process generic filters but with viewer events
                // Traversing the TreeMap in ascending order of keys
                for (Map.Entry<SpecialSmartFilterBase.Name, SpecialSmartFilterBase> entry : specialSmartFilters.entrySet())
                {
                    System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                    PipelineResultBase result = entry.getValue().onAccessibilityEvent(event);
                    if (result != null)
                    {
                        PipelineResultBase resultCopy = result.clone();
                        resultCopy.setCurrentAppFilter(this);
                        resultCopy.setTriggerPackage(event.getPackageName().toString());
                        this.callback.onPipelineResultBackground(resultCopy);
                        return;
                    }
                }
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if (handler.hasCallbacks(searchRunnable))
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

    private void endOfPipelineReached(ScreenInfoExtractor.Screen screen)
    {
        PipelineResultBase endToken = new PipelineResultKeywordFilter(this.packageName);
        endToken.setWindowAction(PipelineWindowAction.END_OF_PIPE_LINE);
        endToken.setScreen(screen);
        this.callback.onPipelineResultBackground(endToken);
    }

    /**
     * @param
     * @param currentFilter
     * @return
     */
    private void processScreen(ScreenInfoExtractor.Screen screen, WordProcessorSmartFilterBase currentFilter)
    {
        for (ScreenInfoExtractor.Screen.TextNode textNode : screen.getTextNodes())
        {
            if (textNode.visible || !currentFilter.isCheckOnlyVisibleNodes())
            {
                // feed word into filter
                PipelineResultBase result = currentFilter.feedWord(textNode);
                if (result != null)
                {
                    result.setTriggerPackage(screen.appName);
                    // Feed result to generic event filters
                    for (Map.Entry<SpecialSmartFilterBase.Name, SpecialSmartFilterBase> entry : specialSmartFilters.entrySet())
                    {
                        PipelineResultBase genericResult = entry.getValue().onPipelineResult(result);
                        if (genericResult != null)
                        {
                            pipelineRunning = genericResult.getWindowAction() == PipelineWindowAction.CONTINUE_PIPELINE;
                            return;
                        }
                    }
                    PipelineResultBase resultCopy = result.clone();
                    resultCopy.setScreen(screen);
                    resultCopy.setCurrentAppFilter(this);
                    // Forward result to callback
                    this.callback.onPipelineResultBackground(resultCopy);

                    pipelineRunning = resultCopy.getWindowAction() == PipelineWindowAction.CONTINUE_PIPELINE;
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
