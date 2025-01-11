package com.example.ourpact3.db;

import android.content.ContentValues;

import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.smart_filter.ContentSmartFilterBase;
import com.example.ourpact3.smart_filter.WordListFilterExact;
import com.example.ourpact3.smart_filter.WordListFilterScored;

import java.time.DayOfWeek;
import java.util.ArrayList;


public class ContentSmartFilterManager
{

    /**
     * overwrites all time restrictions for an app which has exactly use usage_filter_id
     */
    public static void setAllContentFilterRules(String packageId, ArrayList<ContentSmartFilterBase> contentFilters)
    {
        // Start a transaction for safety
        DatabaseManager.db.beginTransaction();
        try
        {
            // Step 1: Delete existing rows
            String whereClause = "app_package_name = ?";
            String[] whereArgs = new String[]{String.valueOf(packageId)};
            DatabaseManager.db.delete("app_content_filter", whereClause, whereArgs);
            // Step 2: Insert new rows from the array
            for (ContentSmartFilterBase rule : contentFilters)
            {
                ContentValues values = new ContentValues();
                // Counter action
                CounterAction counterAction = rule.getCounterAction();
                values.put("explainable", counterAction.isHasExplainableButton() ? 1 : 0);
                values.put("window_action", counterAction.getWindowAction().getValue());
                values.put("button_action", counterAction.getButtonAction().getValue());
                values.put("kill", counterAction.isKillAction() ? 1 : 0); // Assuming kill is a boolean
                values.put("enabled", rule.isEnabled() ? 1 : 0); // Assuming enabled is a boolean
                //
                values.put("user_created", rule.isUserCreated() ? 1 : 0); // Assuming enabled is a boolean
                values.put("app_group", rule.getAppGroup().getValue());
                values.put("readable", rule.isReadable() ? 1 : 0);
                values.put("writable", rule.isWritable() ? 1 : 0);
                values.put("name", rule.getName());
                values.put("short_description", rule.getShortDescription());
                values.put("checks_only_visible", rule.isCheckOnlyVisibleNodes() ? 1 : 0);
                values.put("what_to_check", rule.getNodeCheckStrategyType().getValue());
                values.put("ignore_case", rule.isIgnoringCase() ? 1 : 0);
                int type = 0;
                if (rule instanceof WordListFilterScored)
                {
                    type = ContentSmartFilterBase.TYPE_SCORED;
                    // cast to class
                    WordListFilterScored scoredFilter = (WordListFilterScored) rule;
                } else if (rule instanceof WordListFilterExact)
                {
                    type = ContentSmartFilterBase.TYPE_EXACT;
                }
                values.put("type_of_list", type);

                values.put("id_for_list", 1);
                // Insert the new row
                DatabaseManager.db.insert("content_filters", null, values);
            }

            // Mark the transaction as successful
            DatabaseManager.db.setTransactionSuccessful();
        } catch (Exception e)
        {
            // Handle any exceptions
        } finally
        {
            // End the transaction
            DatabaseManager.db.endTransaction();
        }
    }



}
