package com.example.ourpact3.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.example.ourpact3.R;
import com.example.ourpact3.model.PipelineButtonAction;

import java.util.LinkedHashMap;

public class ReusableSettingsCounterActionView extends LinearLayout
{
    private ReuseableBooleanSettingView explainableView;
    private ReuseableBooleanSettingView killAppView;
    private ReusableSettingsComboboxViev<PipelineButtonAction> windowActionView;

    public ReusableSettingsCounterActionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        LayoutInflater.from(context).inflate(R.layout.reuseable_settings_pipeline_action, this, true);
        killAppView = findViewById(R.id.setting_kill_app);
        explainableView = findViewById(R.id.setting_is_explainable);
        ReusableSettingsItemView windowActionItem = findViewById(R.id.setting_window_action);
        windowActionView = new ReusableSettingsComboboxViev<PipelineButtonAction>(context, windowActionItem);
        LinkedHashMap<PipelineButtonAction, String> values = new LinkedHashMap<PipelineButtonAction, String>();
        values.put(PipelineButtonAction.HOME_BUTTON, context.getString(R.string.home_button));
        values.put(PipelineButtonAction.BACK_BUTTON, context.getString(R.string.back_button));
        values.put(PipelineButtonAction.NONE, context.getString(R.string.none));
        windowActionView.setParameters(context.getString(R.string.choose_a_option), "%s", values, PipelineButtonAction.BACK_BUTTON);
        // Obtain custom attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ReusableSettingsPiplineActionView,
                0, 0);
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
        return windowActionView.getLastSelection();
    }

    public void setPipelineButtonAction(PipelineButtonAction value)
    {
        this.windowActionView.setLastSelection(value);
    }
}

