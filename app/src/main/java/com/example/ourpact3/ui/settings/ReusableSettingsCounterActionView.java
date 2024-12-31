package com.example.ourpact3.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.ourpact3.R;
import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.pipeline.CounterAction;

import java.util.LinkedHashMap;

public class ReusableSettingsCounterActionView extends LinearLayout
{
    private ReuseableSettingsBooleanView explainableView;
    private ReuseableSettingsBooleanView killAppView;
    private ReusableSettingsComboboxViev<PipelineButtonAction> buttonActionView;
    private ReusableSettingsComboboxViev<PipelineWindowAction> windowActionView;
    private Context context;

    public ReusableSettingsCounterActionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.reusable_settings_counter_action, this, true);
        killAppView = findViewById(R.id.setting_kill_app);
        explainableView = findViewById(R.id.setting_is_explainable);
        ReusableSettingsItemView buttonActionItem = findViewById(R.id.setting_button_action);
        buttonActionView = new ReusableSettingsComboboxViev<PipelineButtonAction>(context, buttonActionItem);
        LinkedHashMap<PipelineButtonAction, String> values = new LinkedHashMap<PipelineButtonAction, String>();
        values.put(PipelineButtonAction.HOME_BUTTON, context.getString(R.string.home_button));
        values.put(PipelineButtonAction.BACK_BUTTON, context.getString(R.string.back_button));
        values.put(PipelineButtonAction.NONE, context.getString(R.string.none));
        buttonActionView.setParameters(context.getString(R.string.choose_a_option), "%s", values, PipelineButtonAction.BACK_BUTTON);
        // Window action
        windowActionView = new ReusableSettingsComboboxViev<PipelineWindowAction>(context, findViewById(R.id.setting_window_action));
        // Initial setting is off
        setExpertMode(false);
        // Obtain custom attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ReusableSettingsPiplineActionView,
                0, 0);
    }

    public void setExpertMode(boolean on)
    {
        LinkedHashMap<PipelineWindowAction, String> values = new LinkedHashMap<PipelineWindowAction, String>();
        values.put(PipelineWindowAction.WARNING, context.getString(R.string.show_dialog));
        values.put(PipelineWindowAction.NO_WARNING_AND_STOP, context.getString(R.string.stop_further_processing));
        if (on)
        {
            values.put(PipelineWindowAction.CONTINUE_PIPELINE, context.getString(R.string.continue_processing));
        }
        windowActionView.setParameters(context.getString(R.string.choose_a_option), "%s", values, PipelineWindowAction.WARNING);
    }

    public CounterAction getCounterAction()
    {
        CounterAction action = new CounterAction(windowActionView.getLastSelection(), buttonActionView.getLastSelection(), this.killAppView.getSwitchElement().isChecked());
        action.setHasExplainableButton(this.explainableView.getSwitchElement().isChecked());
        return action;
    }

    public boolean isExplainable()
    {
        return explainableView.getSwitchElement().isChecked();
    }

    public void setExplainable(boolean value)
    {
        explainableView.getSwitchElement().setChecked(value);
    }

    public boolean hasKillAction()
    {
        return killAppView.getSwitchElement().isChecked();
    }

    public void setKillAction(boolean value)
    {
        killAppView.getSwitchElement().setChecked(value);
    }


    public PipelineButtonAction getPipelineButtonAction()
    {
        return buttonActionView.getLastSelection();
    }

    public void setPipelineButtonAction(PipelineButtonAction value)
    {
        this.buttonActionView.setLastSelection(value);
    }
}

