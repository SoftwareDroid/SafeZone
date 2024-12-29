package com.example.ourpact3.ui.usage_restriction;

import java.time.LocalTime;
import java.util.Calendar;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.TimePicker;
import android.content.Intent;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ourpact3.R;
import com.example.ourpact3.ui.settings.ReusableSettingsDurationInputView;
import com.example.ourpact3.ui.settings.ReusableSettingsItemView;
import com.example.ourpact3.ui.settings.ReusableSettingsNumberInputView;
import com.example.ourpact3.ui.settings.ReusableSettingsTimePickerInputView;

import java.util.concurrent.TimeUnit;

public class UsageRestrictionActivity extends AppCompatActivity
{

    private ReusableSettingsNumberInputView numberOfStartsInput;
    private ReusableSettingsDurationInputView timeLimitInput;
    private ReusableSettingsDurationInputView resetPeriodInput;
    private ReusableSettingsTimePickerInputView selectedStartInput;
    private ReusableSettingsTimePickerInputView selectedEndInput;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_restriction);
        Intent intent = getIntent();
        String packageId = intent.getStringExtra("app_id");
        String appName = intent.getStringExtra("app_name");
        // set app name in toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add back arrow
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(this.getString(R.string.usage_restriction_for) + " " + appName); // Set the title on the custom TextView
        // inputs
        numberOfStartsInput = new ReusableSettingsNumberInputView(this, findViewById(R.id.setting_input_number_of_start));
        numberOfStartsInput.setLimits(0, 1000);
        numberOfStartsInput.setParameters(this.getString(R.string.number_of_possible_starts), "%s", 10);
        //
        timeLimitInput = new ReusableSettingsDurationInputView(this, findViewById(R.id.setting_input_time_limit));
        timeLimitInput.setParameters(this.getString(R.string.set_time_limit), "%d:%d:%d (hh:mm:ss)", TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, "999999");
        //
        resetPeriodInput = new ReusableSettingsDurationInputView(this, findViewById(R.id.setting_input_reset_period));
        // 1 day reset period
        resetPeriodInput.setParameters(this.getString(R.string.reset_period), "%d:%d:%d (dd:hh:mm)", TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS, "010000");

        // setup time pickers
        selectedStartInput = new ReusableSettingsTimePickerInputView(this, findViewById(R.id.setting_input_start_time));
        selectedStartInput.setParameters(this.getString(R.string.pick_start_time), "%02d:%02d", LocalTime.now());
        selectedEndInput = new ReusableSettingsTimePickerInputView(this, findViewById(R.id.setting_input_end_time));
        selectedEndInput.setParameters(this.getString(R.string.pick_end_time), "%02d:%02d", LocalTime.now());

    }


    // Back button in title
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish(); // Close the activity when back arrow is clicked
        }
        return super.onOptionsItemSelected(item);
    }
}

