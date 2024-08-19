package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.graphics.PixelFormat;

import android.widget.TextView;

import com.example.ourpact3.model.IFilterResultCallback;
import com.example.ourpact3.model.PipelineResult;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.model.Topic;
import com.example.ourpact3.model.TopicManager;

import java.util.ArrayList;
import java.util.List;

// https://developer.android.com/guide/topics/ui/accessibility/service
public class ContentFilerService extends AccessibilityService implements IFilterResultCallback
{
    private static ContentFilter contentFilter;
    private WindowManager windowManager;
    private View overlayView;
    private String LOG_TAG = "ContentFiler";
    private TopicManager topicManager = new TopicManager();
    public PocketCastsSearchFilter pocketCastFilter = new PocketCastsSearchFilter(this, this.topicManager);

    //    private Handler handler = new Handler();
//    private boolean isRunning = false;
    @Override
    public void onServiceConnected()
    {
        Log.i("FOO", "Starting service");
        pocketCastFilter.setCallback(this);//TODO: add callback
//        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
//        contentFilter = new ContentFilter(getResources().getXml(R.xml.adult_filter));
        // Add sample topic
        Topic adultTopic = new Topic("porn", "topics/en");
        adultTopic.setWords(new ArrayList<String>(List.of("porn", "femdom", "naked")));
        adultTopic.setIncludedTopics(new ArrayList<String>(List.of("female")));

        Topic adultChildTopic = new Topic("female", "topics/en");
        adultChildTopic.setWords(new ArrayList<String>(List.of("girl", "butt")));

        topicManager.addTopic(adultTopic);
        topicManager.addTopic(adultChildTopic);
        //New

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        setServiceInfo(info);
    }


    @SuppressLint("NewApi")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        // never process this for UI control reasons
        if(event.getPackageName().equals(this.getPackageName()))
        {
          return;
        }
        pocketCastFilter.processEvent(event);
        return;
        //   String uuid = Helper.getUuid();
        // Date now = DateTimeHelper.getCurrentDay();
//        Log.i("FOO","hallo" + event.getEventType());
        /*switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                if (pocketCastFilter.packageName.equals(event.getPackageName().toString()) || event.getPackageName().toString().equals("org.mozilla.firefox")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                    String timestamp = dateFormat.format(new Date(System.currentTimeMillis()));
                    String className = event.getSource().getClassName().toString();
                    String text = event.getSource().getText().toString();
                    Log.d(LOG_TAG, "Timestamp: " + System.currentTimeMillis() + "\n" +
                            "  Package Name: " + event.getPackageName().toString() + "\n" +
                            "  Class Name: " + event.getSource().getClassName().toString() + "\n" +
                            "  Text: " + event.getSource().getText().toString() + "\n" +
//                            "  Window: " + event.getSource().getWindow().toString() + "\n" +
//                            "  Number of Child Nodes: " + event.getSource().getWindow().getChildCount() + "\n" +
                            "  Event Time: " + event.getEventTime() + "\n" +
                            "  Source: " + event.getSource() + "\n" +
                            "  ContentDescription: " + event.getSource().getContentDescription());

//                    for (AccessibilityNodeInfo.AccessibilityAction action : event.getSource().getActionList()) {
//                        Log.d("ContentFilter", "action id: " + action + " class " + action.getClass() + " label: '" + action.getLabel() + "'");
//                        {
//                        }
//                        // min api 3ß
//                        if (action.getId() == AccessibilityNodeInfo.AccessibilityAction.ACTION_IME_ENTER.getId()) {
//                            // NOte: catch pocket cast search, browser url search
//                            Log.d("ContentFilter", " action " + action + " Search with: " + event.getSource().getText().toString());   //TODO: works
//                            break;
//
//                        }
//                    }
                }
                //NOTE pocket cast search: catch class Names; android.widget.AutoCompleteTextView
                break;
            }
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: {
                if (event.getContentChangeTypes() == AccessibilityEvent.CONTENT_CHANGE_TYPE_TEXT) {
                    AccessibilityNodeInfo root = getRootInActiveWindow();


//                    Log.d(LOG_TAG, "ev2 :" + event);

                    // Text has changed, do something
                }
                break;
            }


            default:
                if (event.getPackageName() != null && contentFilter != null && pocketCastFilter != null && pocketCastFilter.packageName.equals(event.getPackageName().toString())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

                    String timestamp = dateFormat.format(new Date(System.currentTimeMillis()));

//                    Log.d("ContentFilter", "new event at " + timestamp.toString() + "\n " + event.toString());
                }
        }*/

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
            overlayTextView.setText(text);
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

                this.showOverlayWindow("test " + result, result);
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
            Log.i(LOG_TAG, " pipeline result " + result);
        }
    }
}
