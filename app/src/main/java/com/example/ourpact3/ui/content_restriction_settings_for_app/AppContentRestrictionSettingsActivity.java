package com.example.ourpact3.ui.content_restriction_settings_for_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpact3.R;
import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.pipeline.PipelineResultKeywordFilter;
import com.example.ourpact3.unused.WordListFilterExact;
import com.example.ourpact3.smart_filter.ContentSmartFilter;

import java.util.ArrayList;
import java.util.List;

public class AppContentRestrictionSettingsActivity extends AppCompatActivity
{
    private String appName;
    private RecyclerView recyclerViewContentFilters;
    private ContentFilterRuleAdapter adapterContentFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_restrictions_for_app_settings);
        Intent intent = getIntent();
        String packageId = intent.getStringExtra("app_id");
        appName = intent.getStringExtra("app_name");
        boolean writeable = intent.getBooleanExtra("writeable", true);
        int usageFilterId = intent.getIntExtra("usage_filter_id", -1);
        // set app name in toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add back arrow
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(this.getString(R.string.settings_content_restrictions) + " " + appName); // Set the title on the custom TextView
        // Test UI
        // setup adding time rules
        adapterContentFilters = new ContentFilterRuleAdapter(this);
        recyclerViewContentFilters = findViewById(R.id.added_filter_rules); // Make sure this ID matches your layout
        recyclerViewContentFilters.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewContentFilters.setAdapter(adapterContentFilters);
        try
        {
            addExampleFilters();
        } catch (CloneNotSupportedException e)
        {

        }

    }

    void addExampleFilters() throws CloneNotSupportedException
    {
        PipelineResultKeywordFilter preventReinstallingAndLosePermissons = new PipelineResultKeywordFilter("");
        CounterAction a = new CounterAction(PipelineWindowAction.NO_WARNING_AND_STOP, PipelineButtonAction.BACK_BUTTON, false);
        a.setHasExplainableButton(false);
        preventReinstallingAndLosePermissons.setCounterAction(a);

        ContentSmartFilter nsfwBlockRule = new WordListFilterExact( new ArrayList<>(List.of(
                new ArrayList<>(List.of(appName, "Do you want to install this app?")),
                new ArrayList<>(List.of(appName, "Do you want to update this app?"))
        )),  preventReinstallingAndLosePermissons);
        nsfwBlockRule.setName("Block Something");
        nsfwBlockRule.setFilterShortDescription("This is a short text");
        adapterContentFilters.addEntry(nsfwBlockRule);
    }

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