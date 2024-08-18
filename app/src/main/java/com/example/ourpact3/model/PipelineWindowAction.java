package com.example.ourpact3.model;

public enum PipelineWindowAction
{
    KILL_WINDOW,
    WARNING,
    NOTHING, // Only this action doesn't abort the pipeline
    PERFORM_BACK_ACTION,
    STOP_FURTHER_PROCESSING,
}
