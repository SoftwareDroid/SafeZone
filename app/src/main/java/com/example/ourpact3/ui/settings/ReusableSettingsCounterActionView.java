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

public class ReusableSettingsCounterActionView extends LinearLayout
{
    private ReuseableBooleanSettingView explainableView;
    private ReuseableBooleanSettingView killAppView;
    private ReusableSettingsItemView windowActionView;
    private PipelineButtonAction pipelineButtonAction = PipelineButtonAction.BACK_BUTTON;

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
        windowActionView = findViewById(R.id.setting_window_action);
        windowActionView.setOnClickListener(this::showButtonPickDialog);
        refreshButtonAction();
        // Obtain custom attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ReusableSettingsPiplineActionView,
                0, 0);
    }

    private void refreshButtonAction()
    {
        switch (this.pipelineButtonAction)
        {
            case BACK_BUTTON:
            {
                this.windowActionView.setSummary(getContext().getString(R.string.back_button));
                break;
            }
            case HOME_BUTTON:
            {
                this.windowActionView.setSummary(getContext().getString(R.string.home_button));
                break;
            }
            case NONE:
            {
                this.windowActionView.setSummary(getContext().getString(R.string.none));
                break;
            }
        }
    }

    private void showButtonPickDialog(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle(this.getContext().getString(R.string.choose_a_option))
                .setItems(new String[]{this.getContext().getString(R.string.home_button), this.getContext().getString(R.string.back_button), this.getContext().getString(R.string.none)}, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        switch (which)
                        {
                            case 0:
                                setPipelineButtonAction(PipelineButtonAction.HOME_BUTTON);
                                break;
                            case 1:
                                setPipelineButtonAction(PipelineButtonAction.BACK_BUTTON);
                                break;
                            case 2:
                                setPipelineButtonAction(PipelineButtonAction.NONE);
                                break;
                        }
                    }
                })
                .setNegativeButton(this.getContext().getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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
        return pipelineButtonAction;
    }

    public void setPipelineButtonAction(PipelineButtonAction value)
    {
        pipelineButtonAction = value;
        refreshButtonAction();
    }
}

