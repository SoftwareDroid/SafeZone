package com.example.ourpact3.model;
public abstract class PipelineResultBase
{

    public PipelineWindowAction windowAction;
    public String triggerApp;
    public String triggerFilter;
//    public String topicTriggerWord; // which word in the topic caused the match
    public boolean hasExplainableButton;
    public abstract String getDialogTitle();
    public abstract String getDialogText();
//    public int delay;

}
