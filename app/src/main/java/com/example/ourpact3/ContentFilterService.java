package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;

import com.example.ourpact3.model.IFilterResultCallback;
import com.example.ourpact3.model.PipelineResult;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicLoader;
import com.example.ourpact3.model.TopicManager;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

// https://developer.android.com/guide/topics/ui/accessibility/service

/**
 * NOTE: do not rename class then app_dev.sh doesn't work anymore
 */
public class ContentFilterService extends AccessibilityService implements IFilterResultCallback
{
    private static ContentFilter contentFilter;
    private WindowManager windowManager;
    private View overlayView;
    private final TopicManager topicManager = new TopicManager();
    private final TreeMap<String, AppKeywordFilter> keywordFilters = new TreeMap<>();

    //    private boolean isRunning = false;
    @Override
    public void onServiceConnected()
    {
        Log.i("FOO", "Stating service");
        // get WindowManager needed for creating overlay window
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        // Load all system topics
        TopicLoader topicLoader = new TopicLoader();
        String[] usedLanguages = {"de", "en"};
        ArrayList<TopicLoader.TopicDescriptor> allAvailableTopics = topicLoader.getAllLoadableTopics(getApplicationContext(), Set.of(usedLanguages));
        // Check if all topics are not null
        for (TopicLoader.TopicDescriptor descriptor : allAvailableTopics)
        {
            Topic topic = topicLoader.loadTopicFile(getApplicationContext(), descriptor);
            if (topic != null)
            {
                topicManager.addTopic(topic);
            }
        }
        // load all example filters
        ExampleAppKeywordFilters exampleFilters = new ExampleAppKeywordFilters(this, this.topicManager);
        exampleFilters.addExampleTopics();
        for (AppKeywordFilter filter : exampleFilters.getAllExampleFilters())
        {
            filter.setCallback(this);
            keywordFilters.put(filter.getPackageName(), filter);
        }


        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        setServiceInfo(info);
    }

    public boolean isMagnificationEnabled()
    {
        ContentResolver cr = getContentResolver();
        return Settings.Secure.getInt(cr, "accessibility_display_magnification_enabled", 0) == 1;
    }

    private AppKeywordFilter currentAppFilter;

    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        // never process this for UI control reasons
        if (event == null || event.getPackageName() == null || event.getPackageName().equals(this.getPackageName()))
        {
            return;
        }
        AppKeywordFilter filter = this.keywordFilters.get(event.getPackageName());
        if (filter != null)
        {
            currentAppFilter = filter;
            filter.processEvent(event);
        }
    }


    private void showOverlayWindow(String text, PipelineResult result2)
    {
        // we need the permission to show the overlay which blocks input
        /*if(!Settings.canDrawOverlays(getApplicationContext()))
        {
            return;
        }*/
        if (overlayView == null && windowManager != null)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            overlayView = inflater.inflate(R.layout.overlay_window, null);

            TextView overlayTextView = overlayView.findViewById(R.id.overlay_text);

            if (result2.logging)
            {
                AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
                KeywordScoreWindowCalculator scoreExplainer = new KeywordScoreWindowCalculator();
                String explaination = scoreExplainer.getDebugFilterState(rootNode, currentAppFilter,isMagnificationEnabled());
                overlayTextView.setText(explaination);
            } else
            {
                overlayTextView.setText(text);
            }
            overlayView.findViewById(R.id.close_button).setOnClickListener(v ->
            {
                if (result2.triggerApp != null && result2.windowAction == PipelineWindowAction.KILL_WINDOW)
                {
                    closeOtherApp(result2.triggerApp);
                }
                hideOverlayWindow();
            });

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.OPAQUE);
            windowManager.addView(overlayView, params);
        }
    }

    private void hideOverlayWindow()
    {
        if (overlayView != null)
        {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }

    @Override
    public void onInterrupt()
    {
    }

    private void closeOtherApp(String packageName)
    {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null)
        {
            activityManager.killBackgroundProcesses(packageName);
        }
    }

    @Override
    public void onPipelineResult(PipelineResult result)
    {
        switch (result.windowAction)
        {
            case WARNING:
            case KILL_WINDOW:
                this.showOverlayWindow("test ", result);
                break;
            case PERFORM_BACK_ACTION:
                performGlobalAction(GLOBAL_ACTION_BACK);
                performGlobalAction(GLOBAL_ACTION_BACK);
                break;
            case CONTINUE_PIPELINE:
                break;
            case STOP_FURTHER_PROCESSING:
                break;
        }
        if (result.logging)
        {
            String LOG_TAG = "ContentFiler";
            Log.i(LOG_TAG, " pipeline result " + result.windowAction.toString());
        }
    }
}
