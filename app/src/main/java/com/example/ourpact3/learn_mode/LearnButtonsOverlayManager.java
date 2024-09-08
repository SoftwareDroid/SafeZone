package com.example.ourpact3.learn_mode;


import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import com.example.ourpact3.R;
import com.example.ourpact3.service.IContentFilterService;
import com.example.ourpact3.service.ScreenTextExtractor;
import com.example.ourpact3.service.TextFilterService;

import org.jetbrains.annotations.NotNull;

public class LearnButtonsOverlayManager
{

    private WindowManager windowManager;
    private View overlayView;
    private Context context;
    private IContentFilterService iContentFilterService;

    public LearnButtonsOverlayManager(@NotNull Context context, @NotNull IContentFilterService iContentFilterService)
    {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.context = context;
        this.iContentFilterService = iContentFilterService;
    }

    public void createOverlay()
    {
        if (overlayView != null)
        {
            return;
        }
        overlayView = LayoutInflater.from(context).inflate(R.layout.learn_mode, null);

        // Set the layout parameters for the overlay
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.END | Gravity.TOP; // Position the overlay
        params.x = 0;
        params.y = 100; // Adjust as needed

        // Add the view to the window
        windowManager.addView(overlayView, params);

        // Set up button click listeners
        Button buttonShield = overlayView.findViewById(R.id.button_shield);
        Button buttonSword = overlayView.findViewById(R.id.button_sword);
        Button buttonSettings = overlayView.findViewById(R.id.button_settings);

        buttonShield.setOnClickListener(v -> {
            // Handle shield button click
        });

        buttonSword.setOnClickListener(v -> {
            // Handle sword button click
        });

        buttonSettings.setOnClickListener(v -> {
            // Handle settings button click
            iContentFilterService.stopLearnMode();
        });

        // Handle touch events to allow interaction with the underlying app
        overlayView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            {
                stopOverlay(); // Stop the overlay if touched outside
            }
            return false; // Allow touch events to pass through
        });
    }

    public void onAccessibilityEvent(AccessibilityEvent event, AccessibilityNodeInfo root)
    {
        if(root == null || event.getPackageName() == null)
        {
            return;
        }

        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
            {
                String app = (String) event.getPackageName();

                ScreenTextExtractor.Screen screen = ScreenTextExtractor.extractTextElements(root,false);
                Log.d("LEARN",screen.toString());
                break;
            }
        }
    }

    public void stopOverlay()
    {
        if (overlayView != null)
        {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }
}
