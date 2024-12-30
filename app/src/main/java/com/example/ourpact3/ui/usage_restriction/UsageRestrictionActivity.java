package com.example.ourpact3.ui.usage_restriction;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

import android.os.Bundle;
import android.widget.TextView;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpact3.R;
import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.db.UsageSmartFilterManager;
import com.example.ourpact3.smart_filter.ProductivityFilter;
import com.example.ourpact3.smart_filter.ProductivityTimeRule;
import com.example.ourpact3.ui.settings.ReusableSettingsCheckboxView;
import com.example.ourpact3.ui.settings.ReusableSettingsCounterActionView;
import com.example.ourpact3.ui.settings.ReusableSettingsDurationInputView;
import com.example.ourpact3.ui.settings.ReusableSettingsNumberInputView;
import com.example.ourpact3.ui.settings.ReusableSettingsTimePickerInputView;
import com.example.ourpact3.ui.settings.ReuseableSettingsBooleanView;
import com.example.ourpact3.util.WeekDayToString;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UsageRestrictionActivity extends AppCompatActivity
{
    private TimeRuleListAdapter adapterTimeRules;
    private ReusableSettingsNumberInputView numberOfStartsInput;
    private ReusableSettingsDurationInputView timeLimitInput;
    private ReusableSettingsDurationInputView resetPeriodInput;
    private ReusableSettingsTimePickerInputView selectedStartInput;
    private ReusableSettingsTimePickerInputView selectedEndInput;
    private ReusableSettingsCheckboxView<DayOfWeek> weekdaySelector;
    private ReuseableSettingsBooleanView enabledInput;
    private ReusableSettingsCounterActionView counterActionInput;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_restriction);
        Intent intent = getIntent();
        String packageId = intent.getStringExtra("app_id");
        String appName = intent.getStringExtra("app_name");
        int usageFilterId = intent.getIntExtra("usage_filter_id", -1);
        assert usageFilterId != -1;
        // Get default parameters
        DatabaseManager.open();
        ProductivityFilter productivityFilter = UsageSmartFilterManager.getUsageFilterById(usageFilterId);
        enabledInput = findViewById(R.id.setting_input_enabled);

        // set app name in toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add back arrow
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(this.getString(R.string.usage_restriction_for) + " " + appName); // Set the title on the custom TextView

        counterActionInput = findViewById(R.id.setting_counter_action);
        // inputs
        numberOfStartsInput = new ReusableSettingsNumberInputView(this, findViewById(R.id.setting_input_number_of_start));
        numberOfStartsInput.setLimits(0, 1000);
        numberOfStartsInput.setParameters(this.getString(R.string.number_of_possible_starts), "%s", productivityFilter.getMaxNumberOfUsages());
        //
        timeLimitInput = new ReusableSettingsDurationInputView(this, findViewById(R.id.setting_input_time_limit));
        timeLimitInput.setParameters(this.getString(R.string.set_time_limit), "%d:%d:%d (hh:mm:ss)", TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, ReusableSettingsDurationInputView.formatSecondsToHMS(productivityFilter.getLimitInSeconds()));
        //
        resetPeriodInput = new ReusableSettingsDurationInputView(this, findViewById(R.id.setting_input_reset_period));
        // 1 day reset period
        resetPeriodInput.setParameters(this.getString(R.string.reset_period), "%d:%d:%d (dd:hh:mm)", TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS, ReusableSettingsDurationInputView.formatSecondsToDHM(productivityFilter.getResetPeriodInSeconds()));

        // setup time pickers
        selectedStartInput = new ReusableSettingsTimePickerInputView(this, findViewById(R.id.setting_input_start_time));
        selectedStartInput.setParameters(this.getString(R.string.pick_start_time), "%02d:%02d", LocalTime.now());
        selectedEndInput = new ReusableSettingsTimePickerInputView(this, findViewById(R.id.setting_input_end_time));
        selectedEndInput.setParameters(this.getString(R.string.pick_end_time), "%02d:%02d", LocalTime.now().plusHours(1));
        // weekday picker
        weekdaySelector = new ReusableSettingsCheckboxView<DayOfWeek>(this, findViewById(R.id.setting_input_weekdays));
        LinkedHashMap<DayOfWeek, String> values = new LinkedHashMap<DayOfWeek, String>();
        values.put(DayOfWeek.MONDAY, WeekDayToString.getShortForm(DayOfWeek.MONDAY, this));
        values.put(DayOfWeek.TUESDAY, WeekDayToString.getShortForm(DayOfWeek.TUESDAY, this));
        values.put(DayOfWeek.WEDNESDAY, WeekDayToString.getShortForm(DayOfWeek.WEDNESDAY, this));
        values.put(DayOfWeek.THURSDAY, WeekDayToString.getShortForm(DayOfWeek.THURSDAY, this));
        values.put(DayOfWeek.FRIDAY, WeekDayToString.getShortForm(DayOfWeek.FRIDAY, this));
        values.put(DayOfWeek.SATURDAY, WeekDayToString.getShortForm(DayOfWeek.SATURDAY, this));
        values.put(DayOfWeek.SUNDAY, WeekDayToString.getShortForm(DayOfWeek.SUNDAY, this));
        // setup UI for creating new time rules
        List<DayOfWeek> initialSelection = new ArrayList<DayOfWeek>();
        initialSelection.add(DayOfWeek.MONDAY);
        initialSelection.add(DayOfWeek.TUESDAY);
        initialSelection.add(DayOfWeek.WEDNESDAY);
        initialSelection.add(DayOfWeek.THURSDAY);
        initialSelection.add(DayOfWeek.FRIDAY);
        weekdaySelector.setParameters(this.getString(R.string.choose_a_option), "%s", values, initialSelection);
        // setup adding time rules
        adapterTimeRules = new TimeRuleListAdapter(this);
        RecyclerView recyclerViewTimeRules = findViewById(R.id.added_time_rules); // Make sure this ID matches your layout
        recyclerViewTimeRules.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTimeRules.setAdapter(adapterTimeRules);
        loadInitialTimeRules(usageFilterId);
        DatabaseManager.close();
        findViewById(R.id.add_time_rule).setOnClickListener(v -> {
            // Get the current start and end times
            LocalTime startTime = selectedStartInput.getCurrentTime(); // Assuming this returns a LocalTime or similar
            LocalTime endTime = selectedEndInput.getCurrentTime();
            EnumSet<DayOfWeek> selectedWeekdays = EnumSet.copyOf(weekdaySelector.getSelectedItems());
            //prevents adding the same rule twice
            for (ProductivityTimeRule entry : adapterTimeRules.getAllItems())
            {
                if (entry.getWeekdays().equals(selectedWeekdays))
                {
                    long diffStartTime = Math.abs(Duration.between(startTime, entry.getStartTime()).toMinutes());
                    long diffEndTime = Math.abs(Duration.between(endTime, entry.getEndTime()).toMinutes());
                    final long MIN_DIFF_IN_MINUTES = 1;
                    if (diffStartTime <= MIN_DIFF_IN_MINUTES || diffEndTime <= MIN_DIFF_IN_MINUTES)
                    {
                        Toast.makeText(this, getString(R.string.invalid_time_rule_with_similiar_start_times_exists), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            // Check if the start time is greater than the end time
            if (startTime.plusMinutes(2).isAfter(endTime))
            {
                // Show a toast message for invalid time
                Toast.makeText(this, getString(R.string.invalid_start_time_bigger_as_end), Toast.LENGTH_SHORT).show();
            } else
            {
                if (selectedWeekdays.isEmpty())
                {
                    Toast.makeText(this, getString(R.string.select_at_least_on_weekday), Toast.LENGTH_SHORT).show();
                } else
                {
                    // Create a new TimeRule with example text
                    ProductivityTimeRule newTimeRule = new ProductivityTimeRule(startTime, endTime, selectedWeekdays, true);
                    adapterTimeRules.addTimeRule(newTimeRule);
                    recyclerViewTimeRules.scrollToPosition(adapterTimeRules.getItemCount() - 1); // Scroll to the new item
                }
            }
        });
    }

    private void loadInitialTimeRules(int usageFilterID)
    {
        ArrayList<ProductivityTimeRule> rules = UsageSmartFilterManager.getAllTimeRestrictionRules(usageFilterID);
        for (ProductivityTimeRule rule : rules)
        {
            adapterTimeRules.addTimeRule(rule);
        }
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

    @Override
    protected void onStop()
    {
        super.onStop();
        List<ProductivityTimeRule> rules = this.adapterTimeRules.getAllItems();
        boolean enabled = enabledInput.getSwitchElement().isChecked();
        this.counterActionInput.
        // Code to release resources or save data
    }
}

