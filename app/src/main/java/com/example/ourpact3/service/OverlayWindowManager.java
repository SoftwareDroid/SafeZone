package com.example.ourpact3.service;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourpact3.KeywordScoreWindowCalculator;
import com.example.ourpact3.R;
import com.example.ourpact3.pipeline.PipelineResultBase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OverlayWindowManager
{
    private WindowManager windowManager;
    private View overlayView;
    private AccessibilityService service;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();



        OverlayWindowManager(AccessibilityService service)
    {
        this.service = service;
        windowManager = (WindowManager)service.getSystemService(Context.WINDOW_SERVICE);

    }

    public void showOverlayWindow(PipelineResultBase result2, int[] globalAction)
    {
        try
        {
            if (overlayView == null && windowManager != null)
            {
                LayoutInflater inflater = (LayoutInflater) service.getSystemService(LAYOUT_INFLATER_SERVICE);
                overlayView = inflater.inflate(R.layout.overlay_window, null);

                TextView overlayTextView = overlayView.findViewById(R.id.overlay_text);
                overlayTextView.setText(result2.getDialogText(service));
                TextView overlayTitle = overlayView.findViewById(R.id.overlay_title);
                overlayTitle.setText(result2.getDialogTitle(service));
                Button explainButton = (Button) overlayView.findViewById(R.id.explain_button);
                explainButton.setVisibility(result2.isHasExplainableButton() ? View.VISIBLE : View.GONE);

                overlayView.findViewById(R.id.close_button).setOnClickListener(v ->
                {
                    hideOverlayWindow();
//                pauseEventProcessingFor(100);
                    for (int a : globalAction)
                    {
                        service.performGlobalAction(a);
                    }
                });

                overlayView.findViewById(R.id.explain_button).setOnClickListener(v ->
                {
                    overlayTitle.setText(R.string.explanation_title);
                    if(overlayView != null)
                    {
                        overlayView.findViewById(R.id.explain_button).setEnabled(false);
                    }
                    overlayTextView.setText(R.string.waiting_bg_task);
                    // Run the blocking operation in a background thread
                    executorService.execute(() -> {
                        KeywordScoreWindowCalculator scoreExplainer = new KeywordScoreWindowCalculator();
                        String explanation = scoreExplainer.getDebugFilterState(result2.getScreen(), result2.getCurrentAppFilter());

                        // Update UI on the main thread
                        new Handler(Looper.getMainLooper()).post(() -> {
                            overlayTextView.setText(explanation);
                            if (overlayView != null) {
                                overlayView.findViewById(R.id.explain_button).setEnabled(false);
                            }
                        });
                    });
//                });
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
        catch (Exception e)
        {
            hideOverlayWindow();
            throw e;
        }
    }

    public void hideOverlayWindow()
    {
        if (overlayView != null)
        {
            windowManager.removeView(overlayView);
            overlayView = null;
        }
    }


}
