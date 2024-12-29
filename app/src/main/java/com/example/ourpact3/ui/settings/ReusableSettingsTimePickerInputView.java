package com.example.ourpact3.ui.settings;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ourpact3.R;

import java.time.LocalTime;
import java.util.Calendar;

public class ReusableSettingsTimePickerInputView
{

    private ReusableSettingsItemView item;
    private Context context;
    private String title;
    private LocalTime currentTime;
    private String summaryFormat = "%s";

    public ReusableSettingsTimePickerInputView(Context context, ReusableSettingsItemView item)
    {
        this.item = item;
        this.context = context;
        item.setOnClickListener(this::showTimePickerDialog);
    }

    /**
     * @param title
     * @param summaryFormat use "%02d:%02d"
     */
    public void setParameters(String title, String summaryFormat, LocalTime initialValue)
    {
        this.title = title;
        if (summaryFormat != null)
        {
            this.summaryFormat = summaryFormat;
        }
        setCurrentTime(initialValue);
    }

    private void showTimePickerDialog(View view)
    {
        // Get the current time
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Create a TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute)
            {
                setCurrentTime(LocalTime.of(hourOfDay, minute));

            }
        }, hour, minute, true); // true for 24-hour format

        // Show the TimePickerDialog
        timePickerDialog.show();
    }

    public void setCurrentTime(LocalTime newTime)
    {
        this.setSummary(String.format(this.summaryFormat, newTime.getHour(), newTime.getMinute()));
        this.currentTime = newTime;
    }

    public LocalTime getCurrentTime()
    {
        return this.currentTime;
    }

    public void setTitle(String title)
    {
        item.setTitle(title);
    }

    public void setSummary(String summary)
    {
        item.setSummary(summary);
    }
}

