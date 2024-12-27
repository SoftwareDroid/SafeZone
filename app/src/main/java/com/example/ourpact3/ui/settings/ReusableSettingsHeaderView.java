package com.example.ourpact3.ui.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ourpact3.R;

public class ReusableSettingsHeaderView extends LinearLayout {

    private TextView titleTextView;

    public ReusableSettingsHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.reuseable_settings_header_layout, this, true);
        titleTextView = findViewById(R.id.title);

        // Obtain custom attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ReusableSettingsHeaderView,
                0, 0);

        try {
            // Get the title attribute
            String title = a.getString(R.styleable.ReusableSettingsHeaderView_title);
            if (title != null) {
                titleTextView.setText(title);
            }
        } finally {
            a.recycle();
        }
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }
}

