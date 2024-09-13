package com.example.ourpact3.learn_mode;


import android.content.Context;
import android.graphics.PixelFormat;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourpact3.R;
import com.example.ourpact3.model.PipelineResultBase;
import com.example.ourpact3.service.IContentFilterService;
import com.example.ourpact3.service.ScreenInfoExtractor;

import org.jetbrains.annotations.NotNull;

public class LearnModeComponent
{
    private boolean drawAtLeftEdge = true;
    private WindowManager windowManager;
    private View overlayButtons;
    private Context context;
    private IContentFilterService iContentFilterService;
    private TextView currentStatus;
    public LearnModeComponent(@NotNull Context context, @NotNull IContentFilterService iContentFilterService)
    {
        if(!Settings.canDrawOverlays(context))
        {
            throw new RuntimeException("Need draw overlay Permission");
        }
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.context = context;
        this.iContentFilterService = iContentFilterService;
    }

    public void createOverlay()
    {
        if (overlayButtons != null)
        {
            return;
        }
        overlayButtons = LayoutInflater.from(context).inflate(R.layout.learn_mode, null);

        // Set the layout parameters for the overlay
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

//        params.gravity = Gravity.END | Gravity.TOP; // Position the overlay
        params.gravity = (drawAtLeftEdge ? Gravity.START : Gravity.END) | Gravity.CLIP_VERTICAL; // Position the overlay
        params.x = 0;
        params.y = 0; // No need to adjust y, as it's centered vertically

        // Add the view to the window
        windowManager.addView(overlayButtons, params);

        // Set up button click listeners
        Button buttonThumpUp = overlayButtons.findViewById(R.id.thumb_up);
        Button buttonThumpDown = overlayButtons.findViewById(R.id.thumb_down);
        Button buttonSettings = overlayButtons.findViewById(R.id.button_settings);
        currentStatus = overlayButtons.findViewById(R.id.current_status);

        buttonThumpUp.setOnClickListener(v -> {
            // Handle shield button click
        });

        buttonThumpDown.setOnClickListener(v -> {
            // Handle sword button click
        });

        buttonSettings.setOnClickListener(v -> {
            // Handle settings button click
            iContentFilterService.stopLearnMode();
        });

        // Handle touch events to allow interaction with the underlying app
        overlayButtons.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            {
                stopOverlay(); // Stop the overlay if touched outside
            }
            return false; // Allow touch events to pass through
        });
    }
    private PipelineResultBase lastResult;
    public void onPipelineResult(@NotNull PipelineResultBase result)
    {
        lastResult = result;
        ScreenInfoExtractor.Screen screen = lastResult.getScreen();
        if(screen != null)
        {
            if(currentStatus != null)
            {
                currentStatus.setText(convertPiplineResultToInfoText(lastResult));
            }
        }
    }

    public String convertPiplineResultToInfoText(PipelineResultBase result)
    {
        String status = "";
        switch (result.getWindowAction())
        {
            case PERFORM_HOME_BUTTON_AND_WARNING:
                status = "HOME";
                break;
            case WARNING:
                status = "WARN";
                break;
            case CONTINUE_PIPELINE:
                break;
            case PERFORM_BACK_ACTION:
                status = "BACK";
                break;
            case STOP_FURTHER_PROCESSING:
                status = "STOP";
                break;
            case PERFORM_BACK_ACTION_AND_WARNING:
                status = "WARN2";
                break;
            case END_OF_PIPE_LINE:
                status = "OMIT";
                break;
        }
        return String.format("[%s]",status);
    }
    /*
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

                ScreenInfoExtractor.Screen screen = ScreenInfoExtractor.extractTextElements(root,false);
                Log.d("LEARN",screen.getIdNodes().toString());
                break;
            }
        }
    }*/

    public void stopOverlay()
    {
        if (overlayButtons != null)
        {
            windowManager.removeView(overlayButtons);
            overlayButtons = null;
        }
    }
}
