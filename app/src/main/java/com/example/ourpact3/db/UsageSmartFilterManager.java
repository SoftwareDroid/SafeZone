package com.example.ourpact3.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.smart_filter.ProductivityFilter;
import com.example.ourpact3.smart_filter.ProductivityTimeRule;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumSet;

public class UsageSmartFilterManager
{

    /**
     * overwrites all time restrictions for an app which has exactly use usage_filter_id
     */
    public static void setAllTimeRestrictionRules(long usageFilterId, ArrayList<ProductivityTimeRule> timeRules)
    {

        // Start a transaction for safety
        DatabaseManager.db.beginTransaction();
        try
        {
            // Step 1: Delete existing rows with the specified usage_filter_id
            String whereClause = "usage_filter_id = ?";
            String[] whereArgs = new String[]{String.valueOf(usageFilterId)};
            DatabaseManager.db.delete("time_restriction_rules", whereClause, whereArgs);
            // Step 2: Insert new rows from the array
            for (ProductivityTimeRule rule : timeRules)
            {
                ContentValues values = new ContentValues();
                values.put("usage_filter_id", usageFilterId);
                values.put("monday", rule.hasWeekday(DayOfWeek.MONDAY) ? 1 : 0);
                values.put("tuesday", rule.hasWeekday(DayOfWeek.TUESDAY) ? 1 : 0);
                values.put("wednesday", rule.hasWeekday(DayOfWeek.WEDNESDAY) ? 1 : 0);
                values.put("thursday", rule.hasWeekday(DayOfWeek.THURSDAY) ? 1 : 0);
                values.put("friday", rule.hasWeekday(DayOfWeek.FRIDAY) ? 1 : 0);
                values.put("saturday", rule.hasWeekday(DayOfWeek.SATURDAY) ? 1 : 0);
                values.put("sunday", rule.hasWeekday(DayOfWeek.SUNDAY) ? 1 : 0);
                values.put("start_hour", rule.getStartTime().getHour());
                values.put("start_min", rule.getStartTime().getMinute());
                values.put("end_hour", rule.getEndTime().getHour());
                values.put("end_min", rule.getEndTime().getHour());
                values.put("black_list", rule.isBlackListMode() ? 1 : 0);
                // Insert the new row
                DatabaseManager.db.insert("time_restriction_rules", null, values);
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

    /**
     * Retrieves all time restriction rules for an app with the specified usage_filter_id.
     */
    @SuppressLint("Range")  //supress warning of colomn return -1 if they cannot be found by name
    public static ArrayList<ProductivityTimeRule> getAllTimeRestrictionRules(int usageFilterId)
    {
        ArrayList<ProductivityTimeRule> timeRules = new ArrayList<>();
        Cursor cursor = null;

        try
        {
            // Define the columns to retrieve
            String[] columns = {
                    "start_hour",
                    "start_min",
                    "end_hour",
                    "end_min",
                    "monday",
                    "tuesday",
                    "wednesday",
                    "thursday",
                    "friday",
                    "saturday",
                    "sunday",
                    "black_list"
            };

            // Define the selection criteria
            String selection = "usage_filter_id = ?";
            String[] selectionArgs = new String[]{String.valueOf(usageFilterId)};

            // Execute the query
            cursor = DatabaseManager.db.query("time_restriction_rules", columns, selection, selectionArgs, null, null, null);

            // Iterate through the results
            if (cursor != null && cursor.moveToFirst())
            {
                do
                {
                    // Extract data from the cursor
                    LocalTime startTime = LocalTime.of(
                            cursor.getInt(cursor.getColumnIndex("start_hour")),
                            cursor.getInt(cursor.getColumnIndex("start_min"))
                    );

                    LocalTime endTime = LocalTime.of(
                            cursor.getInt(cursor.getColumnIndex("end_hour")),
                            cursor.getInt(cursor.getColumnIndex("end_min"))
                    );

                    // Create an EnumSet for weekdays
                    EnumSet<DayOfWeek> weekdays = EnumSet.noneOf(DayOfWeek.class);
                    if (cursor.getInt(cursor.getColumnIndex("monday")) == 1)
                        weekdays.add(DayOfWeek.MONDAY);
                    if (cursor.getInt(cursor.getColumnIndex("tuesday")) == 1)
                        weekdays.add(DayOfWeek.TUESDAY);
                    if (cursor.getInt(cursor.getColumnIndex("wednesday")) == 1)
                        weekdays.add(DayOfWeek.WEDNESDAY);
                    if (cursor.getInt(cursor.getColumnIndex("thursday")) == 1)
                        weekdays.add(DayOfWeek.THURSDAY);
                    if (cursor.getInt(cursor.getColumnIndex("friday")) == 1)
                        weekdays.add(DayOfWeek.FRIDAY);
                    if (cursor.getInt(cursor.getColumnIndex("saturday")) == 1)
                        weekdays.add(DayOfWeek.SATURDAY);
                    if (cursor.getInt(cursor.getColumnIndex("sunday")) == 1)
                        weekdays.add(DayOfWeek.SUNDAY);

                    boolean isBlackListMode = cursor.getInt(cursor.getColumnIndex("black_list")) == 1;

                    // Create a new ProductivityTimeRule object
                    ProductivityTimeRule rule = new ProductivityTimeRule(startTime, endTime, weekdays, isBlackListMode);

                    // Add the rule to the list
                    timeRules.add(rule);
                } while (cursor.moveToNext());
            }
        } catch (Exception e)
        {
            // Handle any exceptions (e.g., log the error)
        } finally
        {
            // Close the cursor if it was opened
            if (cursor != null)
            {
                cursor.close();
            }
        }

        return timeRules;
    }

    /*
    if filterId is null a entry will be created
     */
    public static long addOrUpdateUsageFilter(ProductivityFilter usageFilter)
    {
        long filterId = -1;
        // Start a transaction for safety
        DatabaseManager.db.beginTransaction();
        try
        {
            CounterAction counterAction = usageFilter.getCounterAction();
            ContentValues values = new ContentValues();
            values.put("window_action", counterAction.getWindowAction().getValue());
            values.put("button_action", counterAction.getButtonAction().getValue());
            values.put("kill", counterAction.isKillAction() ? 1 : 0); // Assuming kill is a boolean
            values.put("enabled", usageFilter.isEnabled() ? 1 : 0); // Assuming enabled is a boolean
            values.put("reset_period", usageFilter.getResetPeriodInSeconds());
            values.put("time_limit", usageFilter.getLimitInSeconds());
            values.put("max_starts", usageFilter.maxNumberOfUsages);

            // Check if the usage filter already exists
            if (usageFilter.database_id != null)
            {
                // Update existing row
                String whereClause = "id = ?";
                String[] whereArgs = new String[]{String.valueOf(usageFilter.database_id)};
                DatabaseManager.db.update("usage_filters", values, whereClause, whereArgs);
                filterId = usageFilter.database_id;
            } else
            {
                // Insert new row
                filterId = DatabaseManager.db.insert("usage_filters", null, values);
            }
            //update time rules
            setAllTimeRestrictionRules(filterId,usageFilter.getAllTimeRules());

            // Mark the transaction as successful
            DatabaseManager.db.setTransactionSuccessful();
        } catch (Exception e)
        {
            // Handle any exceptions (e.g., log the error)
        } finally
        {
            // End the transaction
            DatabaseManager.db.endTransaction();
        }
        return filterId;
    }

    @SuppressLint("Range")
    public static ProductivityFilter getUsageFilterById(long filterId)
    {
        ProductivityFilter usageFilter = null; // Initialize to null
        Cursor cursor = null;

        try
        {
            // Query to retrieve the usage filter by ID
            String query = "SELECT * FROM usage_filters WHERE id = ?";
            String[] selectionArgs = new String[]{String.valueOf(filterId)};

            cursor = DatabaseManager.db.rawQuery(query, selectionArgs);

            // Check if a result was returned
            if (cursor != null && cursor.moveToFirst())
            {
                CounterAction counterAction = new CounterAction();
                counterAction.setWindowAction(PipelineWindowAction.fromValue(cursor.getInt(cursor.getColumnIndex("window_action"))));
                counterAction.setButtonAction(PipelineButtonAction.fromValue(cursor.getInt(cursor.getColumnIndex("button_action"))));
                counterAction.setKillState(cursor.getInt(cursor.getColumnIndex("kill")) == 1 ? CounterAction.KillState.KILL_BEFORE_WINDOW : CounterAction.KillState.DO_NOT_KILL);
                boolean enabled = cursor.getInt(cursor.getColumnIndex("enabled")) == 1;
                int resetPeriod = cursor.getInt(cursor.getColumnIndex("reset_period"));
                int timeLimit = cursor.getInt(cursor.getColumnIndex("time_limit"));
                Integer maxStarts = cursor.getInt(cursor.getColumnIndex("max_starts"));
                usageFilter = new ProductivityFilter(counterAction, "Usage Limit", resetPeriod, timeLimit, maxStarts, new ArrayList<>());
                usageFilter.database_id = cursor.getLong(cursor.getColumnIndex("id"));
            }
            // If no entry is found, usageFilter remains null
        } catch (Exception e)
        {
            // Handle any exceptions (e.g., log the error)
        } finally
        {
            // Close the cursor to avoid memory leaks
            if (cursor != null)
            {
                cursor.close();
            }
        }
        return usageFilter; // Returns null if no entry was found
    }

}
