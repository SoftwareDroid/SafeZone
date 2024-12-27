package com.example.ourpact3.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ourpact3.R;

public class ReusableSettingsItemView extends LinearLayout
{

    private TextView titleTextView;
    private TextView summaryTextView;

    public ReusableSettingsItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        LayoutInflater.from(context).inflate(R.layout.reuseable_settings_item_layout, this, true);
        titleTextView = findViewById(R.id.title);
        summaryTextView = findViewById(R.id.summary);

        // Obtain custom attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ReusableSettingsItemView,
                0, 0);

        try
        {
            // Get the title attribute
            String title = a.getString(R.styleable.ReusableSettingsItemView_title2);
            String summary = a.getString(R.styleable.ReusableSettingsItemView_summary);
            if (title != null && summary != null)
            {
                titleTextView.setText(title);
                summaryTextView.setText(summary);
            }
        } finally
        {
            a.recycle();
        }
    }

    public void setTitle(String title)
    {
        titleTextView.setText(title);
    }

    public void setSummary(String summary)
    {
        summaryTextView.setText(summary);
    }
}

