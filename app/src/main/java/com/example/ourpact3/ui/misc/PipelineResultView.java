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
        appNameTextView = view.findViewById(R.id.app_name);
    }

    public void setPipelineResult(PipelineResultBase pipelineResult, Context context,boolean readOnly) {
        if(pipelineResult == null)
        {
            return;
        }
        String appName = pipelineResult.getTriggerPackage();
        if(appName != null)
        {
            this.appNameTextView.setText(appName);
        }
    }
}
