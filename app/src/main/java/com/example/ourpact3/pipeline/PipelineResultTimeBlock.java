package com.example.ourpact3.pipeline;

import android.content.Context;

import com.example.ourpact3.model.PipelineWindowAction;

public class PipelineResultTimeBlock
        extends PipelineResultBase
{
    public PipelineResultTimeBlock(PipelineWindowAction action)
    {
        this.setWindowAction(action);
    }

    public long usageTime;
    public long usageLimit;
    public int resetPeriod;

    @Override
    public String getDialogTitle(Context ctx)
    {
        return "Usage Blocked " + getAppName(ctx);
    }

    @Override
    public String getDialogText(Context ctx)
    {
        return "This app was used too much. Usage limit reached of " + usageLimit + "sc in " + resetPeriod + "hours.";
    }
}
