package com.example.ourpact3.ui.misc;

import static com.example.ourpact3.pipeline.CounterAction.KillState.KILL_BEFORE_WINDOW;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.example.ourpact3.R;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.pipeline.PipelineResultBase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PipelineResultView extends LinearLayout
{

    private TextView appNameTextView;
    private TextView filerNameTextView;
    private Switch forceKillCheckbox;
    private Switch switchCheckboxWindow;
    private Spinner spinnerMainActionAgainstApp;

    public PipelineResultView(@NonNull Context context)
    {
        super(context);
        initViews(context);
    }

    public PipelineResultView(@NonNull Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
        initViews(context);
//        initAttributes(context, attrs);
    }

    public PipelineResultView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initViews(context);
//           initAttributes(context, attrs);
    }

    private void initViews(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        android.view.View view = inflater.inflate(R.layout.pipeline_result_settings_view, this, true);
        appNameTextView = view.findViewById(R.id.app_name);
        filerNameTextView = view.findViewById(R.id.smart_filter_name);
        forceKillCheckbox = findViewById(R.id.force_kill_checkbox);
        switchCheckboxWindow = findViewById(R.id.warning_win_checkbox);
        spinnerMainActionAgainstApp = findViewById(R.id.spinner_window_action);
    }

    public void setPipelineResult(PipelineResultBase pipelineResult, Context context)
    {
        if (pipelineResult == null)
        {
            return;
        }
        String packageName = pipelineResult.getTriggerPackage();
        if (packageName != null)
        {
            this.appNameTextView.setText(pipelineResult.getAppName(context));
        }
        // Filtername
        String filterName = pipelineResult.getTriggerFilter();
        if (filterName != null)
        {
            filerNameTextView.setText(filterName);
            forceKillCheckbox.setEnabled(true);
            switchCheckboxWindow.setEnabled(true);
            spinnerMainActionAgainstApp.setEnabled(true);
        } else
        {
            filerNameTextView.setText("None");
            forceKillCheckbox.setEnabled(false);
            switchCheckboxWindow.setEnabled(false);
            spinnerMainActionAgainstApp.setEnabled(false);
        }
        // Kill state
        CounterAction a = pipelineResult.getCounterAction();
        CounterAction.KillState killState = pipelineResult.getCounterAction().getKillState();
        if (killState == CounterAction.KillState.KILL_BEFORE_WINDOW)
        {
            forceKillCheckbox.setChecked(true);
        } else
        {
            forceKillCheckbox.setChecked(false);

        }
        if (a.getWindowAction() == PipelineWindowAction.WARNING)
        {
            switchCheckboxWindow.setChecked(true);
        } else
        {
            switchCheckboxWindow.setChecked(false);
        }
        // Main Window Action
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) this.spinnerMainActionAgainstApp.getAdapter();
        int spinnerIndex = 0;
        switch (a.getButtonAction())
        {
            case HOME_BUTTON:
            {
                spinnerIndex = adapter.getPosition(context.getString(R.string.home_button));
                break;
            }
            case BACK_BUTTON:
            {
                spinnerIndex = adapter.getPosition(context.getString(R.string.back_button));
                break;
            }
            default:
            {
                spinnerIndex = adapter.getPosition(context.getString(R.string.nothing));
                break;
            }

        }
        spinnerMainActionAgainstApp.setSelection(spinnerIndex);
    }
}
