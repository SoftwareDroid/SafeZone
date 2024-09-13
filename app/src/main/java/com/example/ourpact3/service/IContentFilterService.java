package com.example.ourpact3.service;

import com.example.ourpact3.model.PipelineResultBase;

import org.jetbrains.annotations.NotNull;

public interface IContentFilterService
{
    public enum Mode
    {
        NORMAL_MODE,
        APP_KILL_MODE_1,
        LEARN_OVERLAY_MODE,
    }
    void forwardPipelineResultToLearner(PipelineResultBase result);
    Mode getMode();
    void activateAppKillMode(@NotNull PipelineResultBase lastResult);
    void activateLearnMode();
    void stopLearnMode();
    void finishAppKilling(PipelineResultBase lastResult);
    boolean isPackagedIgnoredForLearning(String id);
    void destroyGUI();
}
