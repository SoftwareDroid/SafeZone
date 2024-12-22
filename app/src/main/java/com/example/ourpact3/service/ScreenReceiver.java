package com.example.ourpact3.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenReceiver extends BroadcastReceiver
{
    private IContentFilterService myService;
    public ScreenReceiver(IContentFilterService myService)
    {
        this.myService = myService;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
            // Screen is turned off
            myService.onScreenStateChange(false);
        } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
            myService.onScreenStateChange(true);
        }
    }


}
