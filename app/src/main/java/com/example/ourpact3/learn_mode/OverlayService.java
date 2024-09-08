package com.example.ourpact3.learn_mode;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.ourpact3.R;

public class OverlayService extends Service {

    private WindowManager windowManager;
    private View overlayView;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        createOverlay();
    }

    private void createOverlay() {
        overlayView = LayoutInflater.from(this).inflate(R.layout.learn_mode, null);

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
        });

        // Handle touch events to allow interaction with the underlying app
        overlayView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                stopSelf(); // Stop the service if touched outside
            }
            return false; // Allow touch events to pass through
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) windowManager.removeView(overlayView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

