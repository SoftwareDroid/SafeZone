package com.example.ourpact3.pipeline;

import android.content.Context;

import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
/*
The action if a rules triggers
 */
public class CounterAction implements Cloneable
{
    public enum KillState
    {
        DO_NOT_KILL,
        KILL_BEFORE_WINDOW, //TODO: this is sometimes to slow e.g accessibily serice preventing turning off
        KILLED // internal usage only
    }

    private PipelineButtonAction buttonAction = PipelineButtonAction.NONE;
    private PipelineWindowAction windowAction;
    private KillState killState = KillState.DO_NOT_KILL;
    private boolean hasExplainableButton;

    public PipelineButtonAction getButtonAction()
    {
        return buttonAction;
    }

    public void setButtonAction(PipelineButtonAction buttonAction)
    {
        this.buttonAction = buttonAction;
    }

    public PipelineWindowAction getWindowAction()
    {
        return windowAction;
    }

    public void setWindowAction(PipelineWindowAction windowAction)
    {
        this.windowAction = windowAction;
    }

    public KillState getKillState()
    {
        return killState;
    }

    public void setKillState(KillState killState)
    {
        this.killState = killState;
    }

    public boolean isHasExplainableButton()
    {
        return hasExplainableButton;
    }

    public void setHasExplainableButton(boolean hasExplainableButton)
    {
        this.hasExplainableButton = hasExplainableButton;
    }

    public boolean isBlockingAction()
    {
        if (buttonAction == PipelineButtonAction.BACK_BUTTON || buttonAction == PipelineButtonAction.HOME_BUTTON || windowAction == PipelineWindowAction.STOP_FURTHER_PROCESSING)
        {
            return true;
        }
        if (killState == KillState.KILL_BEFORE_WINDOW)
        {
            return true;
        }
        return false;
    }

    @Override
    public CounterAction clone() throws CloneNotSupportedException
    {
        CounterAction clone = (CounterAction) super.clone();
        clone.buttonAction = buttonAction;
        clone.windowAction = windowAction;
        clone.killState = killState;
        clone.hasExplainableButton = hasExplainableButton;
        return clone;
    }
}
