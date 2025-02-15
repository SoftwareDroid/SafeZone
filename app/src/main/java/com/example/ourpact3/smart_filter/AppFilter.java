package com.example.ourpact3.smart_filter;

import com.example.ourpact3.ContentFilterService;
import com.example.ourpact3.db.AppEntity;
import com.example.ourpact3.db.AppsDatabase;
import com.example.ourpact3.db.ContentFilterEntity;
import com.example.ourpact3.db.ContentFilterToAppEntity;
import com.example.ourpact3.db.UsageFiltersEntity;
import com.example.ourpact3.pipeline.PipelineEndToken;
import com.example.ourpact3.service.IFilterResultCallback;
import com.example.ourpact3.pipeline.PipelineResultBase;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.service.ScreenInfoExtractor;
import com.example.ourpact3.topics.TopicManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AppFilter
{
    private final ArrayList<AccessibilityEvent> cachedEvents = new ArrayList<>();
    private final AppEntity appEntity;
    private boolean checkAllEvents;

    public AppFilter(ContentFilterService service, TopicManager topicManager, AppEntity appEntity, AppsDatabase db)

    {
        this.service = service;
        this.topicManager = topicManager;
        this.isMagnificationEnabled = service.isMagnificationEnabled();
        this.specialSmartFilters = new TreeMap<>();
        this.appEntity = appEntity;
        this.db = db;
        loadContentFilters();
    }

    private void loadContentFilters()
    {
        // load filters
        keywordFilters.clear();
        // Sorting through db query
        List<ContentFilterToAppEntity> filters = db.contentFilterToAppDao().getByPackageName(appEntity.getPackageName());
        for (ContentFilterToAppEntity filter : filters)
        {
            long contentFilterID = filter.getContentFilterID();
            ContentFilterEntity contentFilter = db.contentFiltersDao().getContentFilterById(contentFilterID);
            // wrap the db entry into a object with state
            keywordFilters.add(new ContentSmartFilter(contentFilter, topicManager));
        }
        loadUsageFilter();
    }

    private void loadUsageFilter()
    {
        long usageFilterID = this.appEntity.getUsageFilterId();
        UsageFiltersEntity usageFilter = db.usageFiltersDao().getUsageFilterById(usageFilterID);
        if (usageFilter != null)
        {
            UsageRestrictionsFilter usageRestrictionsFilter = new UsageRestrictionsFilter(usageFilter, db);
            this.setSpecialSmartFilter(SpecialSmartFilterBase.Name.USAGE_RESTRICTION, usageRestrictionsFilter);
        }
    }

    public AccessibilityService service;
    private final AppsDatabase db;
    private final boolean isMagnificationEnabled; //needed beacuse node isVisible behaves differnt
    private boolean pipelineRunning = false;
    private TopicManager topicManager;
    private int delayCount = 0;
    private final ArrayList<ContentSmartFilter> keywordFilters = new ArrayList<>();
    private final TreeMap<SpecialSmartFilterBase.Name, SpecialSmartFilterBase> specialSmartFilters;
    private IFilterResultCallback callback;

    public boolean isAllOtherApps() {return this.appEntity.getPackageName().isEmpty();}

    public void onScreenStateChanged(boolean isScreenOn)
    {
        for (SpecialSmartFilterBase.Name key : specialSmartFilters.keySet())
        {
            SpecialSmartFilterBase filter = specialSmartFilters.get(key);
            assert filter != null;
            if (!filter.isEnabled())
            {
                continue;
            }
            filter.onScreenStateChange(isScreenOn);
        }
    }

    public void onAppStateChange(boolean active)
    {
        for (Map.Entry<SpecialSmartFilterBase.Name, SpecialSmartFilterBase> entry : specialSmartFilters.entrySet())
        {
            if (!entry.getValue().isEnabled())
            {
                continue;
            }
            SpecialSmartFilterBase value = entry.getValue();
            value.onAppStateChange(active);
        }
    }

    public ArrayList<ContentSmartFilter> getAllFilters()
    {
        return keywordFilters;
    }

    public String getPackageName()
    {
        return appEntity.getPackageName();
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
            // First Check generic filters

            for (ContentSmartFilter processor : keywordFilters)
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
//    public boolean isAllOtherApps()
//    {
//        return packageName.isEmpty();
//    }

    public void processOldCachedEvents()
    {
        if (checkAllEvents)
        {
            if (!cachedEvents.isEmpty())
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                {
                    if (!handler.hasCallbacks(searchRunnable))
                    {
                        AccessibilityEvent oldEvent = cachedEvents.remove(cachedEvents.size() - 1);
                        this.processEvent(oldEvent);
                    }
                }
            }
        }

    }

    @SuppressLint("NewApi")
    public void processEvent(AccessibilityEvent event)
    {
        // Empty string is default app this is every other app
        if (event.getPackageName() == null || (!event.getPackageName().toString().equals(appEntity.getPackageName()) && !isAllOtherApps()))
        {
            return;
        }
        processOldCachedEvents();
        int MAX_DELAYED_CALLS = 1;
        switch (event.getEventType())
        {
//            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                // First process generic filters but with viewer events
                // Traversing the TreeMap in ascending order of keys
                for (Map.Entry<SpecialSmartFilterBase.Name, SpecialSmartFilterBase> entry : specialSmartFilters.entrySet())
                {
                    if (!entry.getValue().isEnabled())
                    {
                        continue;
                    }

//                    System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
                    PipelineResultBase result = entry.getValue().onAccessibilityEvent(event);
                    if (result != null)
                    {
                        PipelineResultBase resultCopy = result.clone();
                        resultCopy.setCurrentAppFilter(this);
                        // set package name in empty screen
                        resultCopy.setScreen(new ScreenInfoExtractor.Screen(null, null, event.getPackageName().toString()));
                        this.callback.onPipelineResultBackground(resultCopy);
                        if (resultCopy.getCounterAction().isBlockingAction())
                        {

                            return;
                        }
                    }
                }
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                if (handler.hasCallbacks(searchRunnable))
                {
                    if (checkAllEvents)
                    {
                        cachedEvents.add(event);
                    }
                    Log.d("DEBUG Q", "Has callbacks");
                    return;
                }
                // We delay an wait for more evens and postpone it at nax maxNumDelays times
                if (delayCount < MAX_DELAYED_CALLS)
                {
                    handler.removeCallbacks(searchRunnable);
                    int SEARCH_DELAY_MS = this.checkAllEvents ? 0 : 500;
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


    public void cancelAllCallbacks()
    {
        handler.removeCallbacks(searchRunnable);
    }

    private void endOfPipelineReached(ScreenInfoExtractor.Screen screen)
    {
        PipelineResultBase endToken = new PipelineEndToken();
        endToken.getCounterAction().setWindowAction(PipelineWindowAction.END_OF_PIPE_LINE);
        endToken.setScreen(screen);
        this.callback.onPipelineResultBackground(endToken);
    }

    private void processScreen(ScreenInfoExtractor.Screen screen, ContentSmartFilter currentFilter)
    {
        for (ScreenInfoExtractor.Screen.TextNode textNode : screen.getTextNodes())
        {
            if (textNode.visible || !currentFilter.getContentFilterEntity().getChecksOnlyVisible())
            {
                // feed word into filter
                PipelineResultBase result = currentFilter.feedWord(textNode);
                if (result != null)
                {
//                    result.setTriggerPackage(screen.appName);
                    // Feed result to generic event filters
                    for (Map.Entry<SpecialSmartFilterBase.Name, SpecialSmartFilterBase> entry : specialSmartFilters.entrySet())
                    {
                        if (!entry.getValue().isEnabled())
                        {
                            continue;
                        }

                        PipelineResultBase genericResult = entry.getValue().onPipelineResult(result);
                        if (genericResult != null)
                        {
                            pipelineRunning = genericResult.getCounterAction().getWindowAction() == PipelineWindowAction.CONTINUE_PIPELINE;
                            return;
                        }
                    }
                    PipelineResultBase resultCopy = result.clone();
                    resultCopy.setScreen(screen);
                    resultCopy.setCurrentAppFilter(this);
                    // Forward result to callback
                    this.callback.onPipelineResultBackground(resultCopy);

                    pipelineRunning = resultCopy.getCounterAction().getWindowAction() == PipelineWindowAction.CONTINUE_PIPELINE;
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
