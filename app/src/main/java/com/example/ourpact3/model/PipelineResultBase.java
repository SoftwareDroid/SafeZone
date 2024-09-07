package com.example.ourpact3.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.example.ourpact3.AppFilter;

public abstract class PipelineResultBase
{
    public enum KillState
    {
        DO_NOT_KILL,
        KILL_BEFORE_WINDOW,
        KILLED // internal usage only
    }

    public PipelineWindowAction windowAction;
    public String triggerPackage;
    public String triggerFilter;
    public ScreenTextExtractor.Screen screen; // the screen at time of the filter hit
    public AppFilter currentAppFilter;
    public KillState killState = KillState.DO_NOT_KILL;
    //    public boolean interruptSound = false;
//    public String topicTriggerWord; // which word in the topic caused the match
    public boolean hasExplainableButton;

    public abstract String getDialogTitle(Context ctx);

    public abstract String getDialogText(Context ctx);

    //    public int delay;
    public String getAppName(Context ctx)
    {
        try
        {
            PackageManager packageManager = ctx.getPackageManager();
            packageManager.getApplicationInfo(triggerPackage, 0);
            return (String) packageManager.getApplicationLabel(packageManager.getApplicationInfo(triggerPackage, PackageManager.GET_META_DATA));
        } catch (Exception e)
        {
            return triggerPackage;
        }
    }

}
