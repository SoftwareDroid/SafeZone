package com.example.ourpact3;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import java.io.InputStream;
import android.widget.TextView;
import android.content.res.XmlResourceParser;

// https://developer.android.com/guide/topics/ui/accessibility/service
public class ContentFilerService extends AccessibilityService {
    private static ContentFilter contentFilter;
    private WindowManager windowManager;
    private View overlayView;

    @Override
    public void onServiceConnected() {
        Log.i("FOO", "Starting service");
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        contentFilter = new ContentFilter(getResources().getXml(R.xml.adult_filter));
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        //   String uuid = Helper.getUuid();
        // Date now = DateTimeHelper.getCurrentDay();
        String accessibilityEvent = null;
        String msg = null;
//        Log.i("FOO","hallo" + event.getEventType());
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                accessibilityEvent = "TYPE_VIEW_TEXT_CHANGED";
                msg = String.valueOf(event.getText());
                ContentFilter.FilterResult result = contentFilter.runFiler(event.getSource());
                if (result.filterOut) {
                    Log.i("FOO", "Evil keyword found");
                    showOverlayWindow(result.keywords);
                }

                break;
            }
            case AccessibilityEvent.TYPE_VIEW_FOCUSED: {
                accessibilityEvent = "TYPE_VIEW_FOCUSED";
                msg = String.valueOf(event.getText());
                break;
            }
            case AccessibilityEvent.TYPE_VIEW_CLICKED: {
                accessibilityEvent = "TYPE_VIEW_CLICKED";
                msg = String.valueOf(event.getText());
                break;
            }
            default:
        }

        if (accessibilityEvent == null) {
            return;
        }


    }


    private void showOverlayWindow(String forbiddenKeyword) {
        // we need the permission to show the overlay which blocks input
        /*if(!Settings.canDrawOverlays(getApplicationContext()))
        {
            return;
        }*/

        if (overlayView == null && windowManager != null) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            overlayView = inflater.inflate(R.layout.overlay_window, null);

            TextView overlayTextView = overlayView.findViewById(R.id.overlay_text);
            overlayTextView.setText("Forbidden keyword:" + forbiddenKeyword);

            overlayView.findViewById(R.id.close_button).setOnClickListener(v -> {
                hideOverlayWindow();
            });

            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
//                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE                     | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.OPAQUE);
            windowManager.addView(overlayView, params);
        }
    }

    private void hideOverlayWindow() {
        if (overlayView != null) {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }

    @Override
    public void onInterrupt() {

    }


}
