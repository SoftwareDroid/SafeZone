package com.example.ourpact3.service;

import android.view.accessibility.AccessibilityEvent;

public interface IServiceEventHandler
{
    void start();
    void stop();
    void onAccessibilityEvent(AccessibilityEvent event);
}
