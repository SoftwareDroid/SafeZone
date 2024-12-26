package com.example.ourpact3.ui.app_rule_detail;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;

import com.example.ourpact3.R;
/*
 Intent intent = new Intent(itemView.getContext(), DetailActivity.class);
                    intent.putExtra("item", item);
                    itemView.getContext().startActivity(intent);
 */
public class AppRuleDetailActivitiy extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_rule_detail);
        Intent intent = getIntent();
        String packageId = intent.getStringExtra("app_id");
        String appName = intent.getStringExtra("app_name");
        // set app name in toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Add back arrow
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Hide the default title
        TextView titleTextView = findViewById(R.id.title);
        titleTextView.setText(this.getString(R.string.rules_for)+ " " + appName); // Set the title on the custom TextView
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity when back arrow is clicked
        }
        return super.onOptionsItemSelected(item);
    }
}