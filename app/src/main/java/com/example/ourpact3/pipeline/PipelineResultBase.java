package com.example.ourpact3.pipeline;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.ourpact3.smart_filter.AppFilter;
import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.service.ScreenInfoExtractor;
import com.example.ourpact3.util.PackageUtil;

public abstract class PipelineResultBase implements Cloneable
{
    @NonNull
    @Override
    public PipelineResultBase clone()
    {
        try
        {
            PipelineResultBase clone = (PipelineResultBase) super.clone();
            clone.counterAction = this.counterAction.clone();

            if (this.triggerFilter != null)
            {
                clone.triggerFilter = this.triggerFilter; // Strings are immutable, but this is just for clarity
            }
            if (this.screen != null)
            {
                //TODO: perhaps clone in future
                clone.screen = this.screen; // Assuming ScreenTextExtractor.Screen has a clone method
            }
            if (this.currentAppFilter != null)
            {
                clone.currentAppFilter = this.currentAppFilter; // Shallow ref is OK
            }
            return clone;
        } catch (CloneNotSupportedException e)
        {
            throw new AssertionError(); // Can't happen since we are Cloneable
        }
    }


    public CounterAction getCounterAction()
    {
        return this.counterAction;
    }
    public void setCounterAction(CounterAction a)
    {
        this.counterAction = a;
    }
    //    private String triggerPackage; // Changed to private
    private CounterAction counterAction = new CounterAction();
    private String triggerFilter; // Changed to private
    private ScreenInfoExtractor.Screen screen; // Changed to private
    private AppFilter currentAppFilter; // Changed to private

    public PipelineResultBase()
    {

    }

    public String getTriggerPackage()
    {
        assert getScreen() != null;
        return getScreen().appName;
    }

    public String getTriggerFilter()
    {
        return triggerFilter;
    }

    public void setTriggerFilter(String triggerFilter)
    {
        this.triggerFilter = triggerFilter;
    }

    public ScreenInfoExtractor.Screen getScreen()
    {
        return screen;
    }

    public void setScreen(ScreenInfoExtractor.Screen screen)
    {
        this.screen = screen;
    }

    public AppFilter getCurrentAppFilter()
    {
        return currentAppFilter;
    }

    public void setCurrentAppFilter(AppFilter currentAppFilter)
    {
        this.currentAppFilter = currentAppFilter;
    }


    public abstract String getDialogTitle(Context ctx);

    public abstract String getDialogText(Context ctx);

    public String getAppName(Context ctx)
    {
        return PackageUtil.getAppName(ctx, getTriggerPackage());
    }
    public String convertToLogEntry(Context ctx)
    {
        return "App: " + getAppName(ctx) + " Warn: " + String.valueOf(this.getCounterAction().getWindowAction() == PipelineWindowAction.WARNING) + " Kill: " + this.getCounterAction().getKillState();
    }


}
