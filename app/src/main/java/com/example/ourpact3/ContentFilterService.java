package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourpact3.model.CheatKeyManager;
import com.example.ourpact3.model.CrashHandler;
import com.example.ourpact3.model.IFilterResultCallback;
import com.example.ourpact3.model.PipelineResultBase;
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
    private WindowManager windowManager;
    private View overlayView;
    private final TopicManager topicManager = new TopicManager();
    private final TreeMap<String, AppFilter> keywordFilters = new TreeMap<>();
    private CrashHandler crashHandler;
    private CheatKeyManager cheatKeyManager;
    //    private boolean isRunning = false;
    @Override
    public void onServiceConnected()
    {
        Log.i("FOO", "Stating service");
        crashHandler = new CrashHandler(this);
        cheatKeyManager = new CheatKeyManager(this,45);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
// get WindowManager needed for creating overlay window
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
// Load all system topics
        TopicLoader topicLoader = new TopicLoader();
        String[] usedLanguages = {"de", "en"};
        ArrayList<TopicLoader.TopicDescriptor> allAvailableTopics = null;
        try
        {
            allAvailableTopics = topicLoader.getAllLoadableTopics(getApplicationContext(), Set.of(usedLanguages));
            // Check if all topics are not null
            for (TopicLoader.TopicDescriptor descriptor : allAvailableTopics)
            {
                Topic topic = topicLoader.loadTopicFile(getApplicationContext(), descriptor);
                if (topic != null)
                {
                    topicManager.addTopic(topic);
                }
            }
            // check all topics
            topicManager.checkAllTopics();
            // load all example filters
            ExampleAppKeywordFilters exampleFilters = new ExampleAppKeywordFilters(this, this.topicManager);
            exampleFilters.addExampleTopics();
            for (AppFilter filter : exampleFilters.getAllExampleFilters())
            {
                filter.setCallback(this);
                keywordFilters.put(filter.getPackageName(), filter);
            }
            AccessibilityServiceInfo info = new AccessibilityServiceInfo();
            info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
            info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
            setServiceInfo(info);
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isKeyboardOpen() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.isAcceptingText();
    }

    public boolean isMagnificationEnabled()
    {
        ContentResolver cr = getContentResolver();
        return Settings.Secure.getInt(cr, "accessibility_display_magnification_enabled", 0) == 1;
    }

    private AppFilter currentAppFilter;

    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        // the a cheat key is used then don't filter
        if(cheatKeyManager.isServiceIsDisabled())
        {
            return;
        }
        // never process this for UI control reasons
        if (event == null || event.getPackageName() == null || event.getPackageName().equals(this.getPackageName()))
        {
            return;
        }
        AppFilter filter = this.keywordFilters.get(event.getPackageName());
        if (filter != null)
        {
            currentAppFilter = filter;
            filter.processEvent(event);
        }
    }


    private void showOverlayWindow(PipelineResultBase result2, int globalAction)
    {
        if (overlayView == null && windowManager != null)
        {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            overlayView = inflater.inflate(R.layout.overlay_window, null);

            TextView overlayTextView = overlayView.findViewById(R.id.overlay_text);
            overlayTextView.setText(result2.getDialogText(this));
            TextView overlayTitle = overlayView.findViewById(R.id.overlay_title);
            overlayTitle.setText(result2.getDialogTitle(this));
            Button explainButton = (Button) overlayView.findViewById(R.id.explain_button);
            explainButton.setVisibility(result2.hasExplainableButton ? View.VISIBLE : View.GONE);

            overlayView.findViewById(R.id.close_button).setOnClickListener(v ->
            {
                hideOverlayWindow();
                if(globalAction != -1)
                {
                    performGlobalAction(globalAction);
                }
            });

            overlayView.findViewById(R.id.explain_button).setOnClickListener(v ->
            {
                overlayTitle.setText("Explaination:");
                AccessibilityNodeInfo rootNode = this.getRootInActiveWindow();
                KeywordScoreWindowCalculator scoreExplainer = new KeywordScoreWindowCalculator();
                String explaination = scoreExplainer.getDebugFilterState(rootNode, currentAppFilter, isMagnificationEnabled());
                overlayTextView.setText(explaination);
                overlayView.findViewById(R.id.explain_button).setEnabled(false);
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

    @Override
    public void onPipelineResult(PipelineResultBase result)
    {
        switch (result.windowAction)
        {
            case WARNING:
            case PERFORM_HOME_BUTTON_AND_WARNING:
                this.showOverlayWindow( result,GLOBAL_ACTION_HOME);
                break;
            case PERFORM_BACK_ACTION_AND_WARNING:
                this.showOverlayWindow( result,GLOBAL_ACTION_BACK);
                break;
            case PERFORM_BACK_ACTION:
                performGlobalAction(GLOBAL_ACTION_BACK);
                break;
            case CONTINUE_PIPELINE:
                break;
            case STOP_FURTHER_PROCESSING:
                break;
        }

    }

}
