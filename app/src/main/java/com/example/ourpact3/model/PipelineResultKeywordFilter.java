package com.example.ourpact3.model;

public class PipelineResultKeywordFilter extends PipelineResultBase
{
    public String inputTriggerWord;

    @Override
    public String getDialogTitle()
    {
        return "Blocked " + triggerApp;
    }

    @Override
    public String getDialogText()
    {
        return "This app was blocked due to a keyword filter.";
    }
}
