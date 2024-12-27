package com.example.ourpact3.ui.usage_restriction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ourpact3.R;

public class UsageRestrictionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        titleTextView.setText(this.getString(R.string.usage_restriction_for)+ " " + appName); // Set the title on the custom TextView
    }

    // Back button in title
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when back arrow is clicked
        }
        return super.onOptionsItemSelected(item);
    }
}

