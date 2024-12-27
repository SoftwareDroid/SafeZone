package com.example.ourpact3.ui.settings;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ourpact3.R;

public class ReusableSettingsNumberInputView extends LinearLayout
{

    private TextView titleTextView;
    private TextView summaryTextView;

    public ReusableSettingsNumberInputView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        //use same as item layout
        LayoutInflater.from(context).inflate(R.layout.reuseable_settings_item_layout, this, true);
        titleTextView = findViewById(R.id.title);
        summaryTextView = findViewById(R.id.summary);

        this.setOnClickListener();
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

    private void showTimePickerDialog() {
        // Get the current time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    // Handle the selected duration
                    String duration = selectedHour + " hours and " + selectedMinute + " minutes";
                    Toast.makeText(MainActivity.this, "Selected Duration: " + duration, Toast.LENGTH_SHORT).show();
                }, hour, minute, true);

        timePickerDialog.show();
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

