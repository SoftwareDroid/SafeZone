package com.example.ourpact3.model;

public enum PipelineWindowAction
{
    PERFORM_HOME_BUTTON_AND_WARNING,
    WARNING,
    CONTINUE_PIPELINE, // Only this action doesn't abort the pipeline
    PERFORM_BACK_ACTION,
    STOP_FURTHER_PROCESSING,
    PERFORM_BACK_ACTION_AND_WARNING, // goes back and shoes an warning
    END_OF_PIPE_LINE,
}
