package com.example.ourpact3.service;

import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;

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
    void onScreenStateChange(boolean on);
    void finishAppKilling(PipelineResultBase lastResult);
    boolean isPackagedIgnoredForLearning(String id);
    boolean isPackageIgnoredForNormalMode(String id);
    void destroyGUI();
    void onAppChange(String oldApp,String newApp);
    void setSpecialSmartFilter(String app,SpecialSmartFilterBase.Name name, SpecialSmartFilterBase filter);
    SpecialSmartFilterBase getSpecialSmartFilter(String app,SpecialSmartFilterBase.Name name);
}
