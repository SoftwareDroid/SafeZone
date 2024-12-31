package com.example.ourpact3.ui.app_rule_detail;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;

import com.example.ourpact3.ui.settings.ReusableSettingsCheckboxView;
import com.example.ourpact3.ui.settings.ReusableSettingsItemView;

import com.example.ourpact3.R;
import com.example.ourpact3.ui.usage_restriction.UsageRestrictionActivity;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.List;

/*
 Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                    intent.putExtra("item", item);
                    itemView.getContext().startActivity(intent);
 */
public class AppRuleDetailActivitiy extends AppCompatActivity {
    private ReusableSettingsItemView usageRestriction;
    private ReusableSettingsCheckboxView<String> checkboxView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_rule_detail);
        Intent intent = getIntent();
        String packageId = intent.getStringExtra("app_id");
        String appName = intent.getStringExtra("app_name");
        boolean writeable = intent.getBooleanExtra("writeable",true);
        int usageFilterId = intent.getIntExtra("usage_filter_id",-1);
        // set app name in toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add back arrow
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(this.getString(R.string.rules_for)+ " " + appName); // Set the title on the custom TextView
        // Get reference to the ReusableSettingsItemView
        usageRestriction = findViewById(R.id.usage_restriction);


        // Set up the click listener
        // The usage restriction dialog is quite complex so we start a new activity here
        usageRestriction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the new Activity
                Intent intent = new Intent(v.getContext(), UsageRestrictionActivity.class);
                intent.putExtra("app_id", packageId);
                intent.putExtra("app_name", appName);
                intent.putExtra("usage_filter_id", usageFilterId);
                intent.putExtra("writeable", writeable);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when back arrow is clicked
        }
        return super.onOptionsItemSelected(item);
    }
}