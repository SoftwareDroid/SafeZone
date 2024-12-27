package com.example.ourpact3.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Switch;

import com.example.ourpact3.R;

public class ReuseableBooleanSettingView extends LinearLayout
{

    private TextView labelTextView;
    private TextView labelDescription;
    private Switch switchElement;

    public ReuseableBooleanSettingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        LayoutInflater.from(context).inflate(R.layout.reuseable_settings_boolean_layout, this, true);
        labelTextView = findViewById(R.id.title);
        labelDescription = findViewById(R.id.description);
        switchElement = findViewById(R.id.is_enabled);

        // Obtain custom attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ReusableSettingsBooleanView,
                0, 0);

        try
        {
            // Get the title attribute
            String label = a.getString(R.styleable.ReusableSettingsBooleanView_label);
            String description = a.getString(R.styleable.ReusableSettingsBooleanView_description);
            if (label != null )
            {
                labelTextView.setText(label);
            }
            if(description != null)
            {
                labelDescription.setText(description);

            }
            else
            {
                labelDescription.setText("");
            }

            // Get the isEnabled attribute
            boolean isEnabled = a.getBoolean(R.styleable.ReusableSettingsBooleanView_isEnabled, false);
            setStatus(isEnabled);
        } finally
        {
            a.recycle();
        }
    }

    public void setTitle(String title)
    {
        labelTextView.setText(title);
    }

    public Switch getSwitchElement()
    {
        return switchElement;
    }

    public void setStatus(boolean checked)
    {
        switchElement.setChecked(checked);
    }
}

