package com.example.ourpact3.ui.misc;
import androidx.appcompat.widget.SwitchCompat;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ourpact3.AppFilter;
import com.example.ourpact3.R;
import com.example.ourpact3.pipeline.PipelineResultBase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PipelineResultView extends LinearLayout {

    private TextView appNameTextView;
    private TextView dialogTitleTextView;
    private TextView dialogTextTextView;
    private TextView triggerPackageTextView;
    private TextView triggerFilterTextView;
    private SwitchCompat windowActionSwitch;
    private TextView screenTextView;
    private TextView currentAppFilterTextView;
    private SwitchCompat killStateSwitch;
    private SwitchCompat hasExplainableButtonSwitch;

    public PipelineResultView(@NonNull Context context) {
        super(context);
        initViews(context);
    }

    public PipelineResultView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
//        initAttributes(context, attrs);
    }

    public PipelineResultView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
//           initAttributes(context, attrs);
    }

    private void initViews(Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        android.view.View view = inflater.inflate(R.layout.pipeline_result_settings_view, this, true);

        appNameTextView = view.findViewById(R.id.app_name_text_view);
    }
//        dialogTitleTextView = view.findViewById(R.id.dialog_title_text_view);
//        dialogTextTextView = view.findViewById(R.id.dialog_text_text_view);
//        triggerPackageTextView = view.findViewById(R.id.trigger_package_text_view);
//        triggerFilterTextView = view.findViewById(R.id.trigger_filter_text_view);
//        windowActionSwitch = view.findViewById(R.id.window_action_switch);
//        screenTextView = view.findViewById(R.id.screen_text_view);
//        currentAppFilterTextView = view.findViewById(R.id.current_app_filter_text_view);
//        killStateSwitch = view.findViewById(R.id.kill_state_switch);
//        hasExplainableButtonSwitch = view.findViewById(R.id.has_explainable_button_switch);
//    }

//    private void initAttributes(Context context, AttributeSet attrs) {
//        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PipelineResultView);
//        boolean readonly = a.getBoolean(R.styleable.PipelineResultView_readonly, false);
//        a.recycle();
//
//        if (readonly) {
//            windowActionSwitch.setEnabled(false);
//            killStateSwitch.setEnabled(false);
//            hasExplainableButtonSwitch.setEnabled(false);
//        }
//    }

    public void initStaticTextLayout()
    {
        appNameTextView.setText("App Name");
//        dialogTitleTextView.setText("Dialog Title");
//        dialogTextTextView.setText("Dialog Text");
//        triggerPackageTextView.setText("Trigger Package");
//        triggerFilterTextView.setText("Trigger Filter");
//        screenTextView.setText("Screen");
//        currentAppFilterTextView.setText("Current App Filter");
//    }
    }
    public void setPipelineResult(PipelineResultBase pipelineResult, Context context) {
        if(pipelineResult == null)
        {
            return;
        }
        /*setTextAndVisibility(dialogTitleTextView, pipelineResult.getDialogTitle(context));
        setTextAndVisibility(dialogTextTextView, pipelineResult.getDialogText(context));
        setTextAndVisibility(triggerPackageTextView, pipelineResult.getTriggerPackage());
        setTextAndVisibility(triggerFilterTextView, pipelineResult.getTriggerFilter());
//        setTextAndVisibility(screenTextView, pipelineResult.getScreen().toString());
        AppFilter filter = pipelineResult.getCurrentAppFilter();
        if(filter != null)
        {
            String packageName = filter.getPackageName();
            setTextAndVisibility(currentAppFilterTextView, packageName);
        }
        if (pipelineResult.getWindowAction() != null) {
            windowActionSwitch.setChecked(true);
            windowActionSwitch.setVisibility(VISIBLE);
        } else {
            windowActionSwitch.setChecked(false);
            windowActionSwitch.setVisibility(GONE);
        }

        if (pipelineResult.getKillState() != null) {
            killStateSwitch.setChecked(pipelineResult.getKillState() == PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
            killStateSwitch.setVisibility(VISIBLE);
        } else {
            killStateSwitch.setVisibility(GONE);
        }

        if (pipelineResult.isHasExplainableButton()) {
            hasExplainableButtonSwitch.setChecked(true);
            hasExplainableButtonSwitch.setVisibility(VISIBLE);
        } else {
            hasExplainableButtonSwitch.setChecked(false);
        }*/
    }
    private void setTextAndVisibility(TextView textView, String text) {
        if (text != null) {
            textView.setText(text);
            textView.setVisibility(VISIBLE);
        } else {
            textView.setVisibility(GONE);
        }
    }
}
