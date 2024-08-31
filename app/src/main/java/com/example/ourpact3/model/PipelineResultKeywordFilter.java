package com.example.ourpact3.model;

import android.content.Context;

public class PipelineResultKeywordFilter extends PipelineResultBase
{
    public String inputTriggerWord;

    @Override
    public String getDialogTitle(Context ctx)
    {
        return "Keyword Block: " + getAppName(ctx);
    }

    @Override
    public String getDialogText(Context ctx)
    {
        return "This app was blocked due to a keyword filter.";
    }
}
