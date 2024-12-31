package com.example.ourpact3.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ourpact3.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReusableSettingsDurationInputView
{
    private ReusableSettingsItemView settingsItem;
    private Context context;
    private String title;
    private String summaryFormat = "%s";
    private TimeUnit unitInput1;
    private TimeUnit unitInput2;
    private TimeUnit unitInput3;
    private String currentInputAsString = null;
    private EditText input_3;
    private EditText input_2;
    private EditText input_1;
    private int number1;
    private int number2;
    private int number3;
    private String startValue;

    public static String formatSecondsToHMS(long totalSeconds)
    {
        // Calculate hours, minutes, and seconds
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        // Format the output
        return String.format("%02d%02d%02d", hours, minutes, seconds);
    }

    public static String formatSecondsToDHM(long totalSeconds)
    {
        // Calculate days, hours, and minutes
        long days = totalSeconds / 86400; // 86400 seconds in a day
        long hours = (totalSeconds % 86400) / 3600; // 3600 seconds in an hour
        long minutes = (totalSeconds % 3600) / 60; // 60 seconds in a minute

        // Format the output
        return String.format("%02d%02d%02d", days, hours, minutes);
    }

    public ReusableSettingsDurationInputView(Context context, ReusableSettingsItemView item)
    {
        this.settingsItem = item;
        this.context = context;
        item.setOnClickListener(this::showDialog);
    }

    /*

     */
    public void setParameters(String title, String summaryFormat, TimeUnit unitInput1, TimeUnit unitInput2, TimeUnit unitInput3, String startValue)
    {
        this.startValue = startValue;
        this.unitInput1 = unitInput1;
        this.unitInput2 = unitInput2;
        this.unitInput3 = unitInput3;
        this.title = title;
        if (summaryFormat != null)
        {
            this.summaryFormat = summaryFormat;
        } else
        {
            this.summaryFormat = "%d:%d:%d";
        }
        this.currentInputAsString = startValue;
        updateSeperatedInputFields(false);
    }

    private void setTimeUnitForInput(TextView unitInUI, TimeUnit unit)
    {
        switch (unit)
        {

            case SECONDS:
                unitInUI.setText(R.string.unit_seconds);
                break;
            case MINUTES:
                unitInUI.setText(R.string.unit_minutes);
                break;
            case HOURS:
                unitInUI.setText(R.string.unit_hours);
                break;
            case DAYS:
                unitInUI.setText(R.string.unit_days);
                break;

        }
    }

    private ArrayList<String> splitIntoGroupsOfTwo(String input)
    {
        ArrayList<String> groups = new ArrayList<>();
        for (int i = 0; i < input.length(); i += 2)
        {
            // Ensure we don't go out of bounds
            if (i + 1 < input.length())
            {
                groups.add(input.substring(i, i + 2));
            } else
            {
                groups.add(input.substring(i)); // Add the last single character if it exists
            }
        }
        return groups;
    }

    public long getAccumulatedTimeInSeconds()
    {
        return this.unitInput1.toSeconds(number1) + this.unitInput2.toSeconds(number2) + this.unitInput3.toSeconds(number3);
    }

    private void updateSeperatedInputFields(boolean updateTextOfInputFields)
    {
        // Split the current input into groups of two
        ArrayList<String> groups = splitIntoGroupsOfTwo(currentInputAsString);

        // Assign values based on the number of groups
        String lastTwoDigits = groups.size() > 0 ? groups.get(groups.size() - 1) : "";
        String middleTwoDigits = groups.size() > 1 ? groups.get(groups.size() - 2) : "";
        String firstTwoDigits = groups.size() > 2 ? groups.get(0) : "";
        if (lastTwoDigits.isEmpty())
        {
            this.number1 = 0;
        } else
        {
            this.number1 = Integer.valueOf(lastTwoDigits);
        }
        if (middleTwoDigits.isEmpty())
        {
            this.number2 = 0;
        } else
        {
            this.number2 = Integer.valueOf(middleTwoDigits);

        }
        if (updateTextOfInputFields)
        {
            this.input_1.setText(lastTwoDigits);
            this.input_2.setText(middleTwoDigits);
            this.input_3.setText(firstTwoDigits);
        }
        if (firstTwoDigits.isEmpty())
        {
            this.number3 = 0;
        } else
        {
            this.number3 = Integer.valueOf(firstTwoDigits);
        }
        this.settingsItem.setSummary(String.format(this.summaryFormat, this.number3, this.number2, this.number1));
    }

    public int getNumber1()
    {
        return number1;
    }

    public int getNumber2()
    {
        return number2;
    }

    public int getNumber3()
    {
        return number3;
    }

    private void pressNumber(Character c)
    {
        if (currentInputAsString.length() < 6)
        {
            currentInputAsString = currentInputAsString + c;
            updateSeperatedInputFields(true);
        }
    }

    private void showDialog(View v)
    {
        // Create a new dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.reusable_duration_picker_layout);
        this.input_3 = dialog.findViewById(R.id.edit_input_3);
        this.input_2 = dialog.findViewById(R.id.edit_input_2);
        this.input_1 = dialog.findViewById(R.id.edit_input_1);
        setTimeUnitForInput(dialog.findViewById(R.id.unit_1), this.unitInput1);
        setTimeUnitForInput(dialog.findViewById(R.id.unit_2), this.unitInput2);
        setTimeUnitForInput(dialog.findViewById(R.id.unit_3), this.unitInput3);
        // Get references to the views in the dialog
        Button okButton = dialog.findViewById(R.id.button_ok);
        Button cancelButton = dialog.findViewById(R.id.button_cancel);
        // connect number fields
        Button number0 = dialog.findViewById(R.id.input_number_0);
        number0.setOnClickListener(view -> {
            pressNumber('0');
        });

        Button number1 = dialog.findViewById(R.id.input_number_1);
        number1.setOnClickListener(view -> {
            pressNumber('1');
        });

        Button number2 = dialog.findViewById(R.id.input_number_2);
        number2.setOnClickListener(view -> {
            pressNumber('2');
        });

        Button number3 = dialog.findViewById(R.id.input_number_3);
        number3.setOnClickListener(view -> {
            pressNumber('3');
        });

        Button number4 = dialog.findViewById(R.id.input_number_4);
        number4.setOnClickListener(view -> {
            pressNumber('4');
        });

        Button number5 = dialog.findViewById(R.id.input_number_5);
        number5.setOnClickListener(view -> {
            pressNumber('5');
        });

        Button number6 = dialog.findViewById(R.id.input_number_6);
        number6.setOnClickListener(view -> {
            pressNumber('6');
        });

        Button number7 = dialog.findViewById(R.id.input_number_7);
        number7.setOnClickListener(view -> {
            pressNumber('7');
        });

        Button number8 = dialog.findViewById(R.id.input_number_8);
        number8.setOnClickListener(view -> {
            pressNumber('8');
        });

        Button number9 = dialog.findViewById(R.id.input_number_9);
        number9.setOnClickListener(view -> {
            pressNumber('9');
        });

        Button number00 = dialog.findViewById(R.id.input_number_00);
        number00.setOnClickListener(view -> {
            pressNumber('0');
            pressNumber('0');
        });

        Button numberDel = dialog.findViewById(R.id.input_number_del);
        numberDel.setOnClickListener(view -> {
            if (currentInputAsString.length() <= 1)
            {
                currentInputAsString = "";
            } else
            {
                currentInputAsString = currentInputAsString.substring(currentInputAsString.length() - 1);
            }
            updateSeperatedInputFields(true);
        });
        numberDel.setOnLongClickListener(view -> {
            currentInputAsString = "";
            updateSeperatedInputFields(true);
            return true;
        });

        if (currentInputAsString == null)
        {
            reset();
        } else
        {
            updateSeperatedInputFields(true);
        }
        // Set up the Cancel button click listener
        cancelButton.setOnClickListener(v2 -> {
            reset();
            dialog.dismiss();
        }); // Close the dialog
        okButton.setOnClickListener(v2 -> {
            updateSeperatedInputFields(true);
            this.settingsItem.setSummary(String.format(this.summaryFormat, this.number3, this.number2, this.number1));
            dialog.dismiss();
        }); // Close the dialog
        // Show the dialog
        dialog.show();
    }

    public void reset()
    {
        //setup start value, we always need a valid field
        this.currentInputAsString = startValue;
        updateSeperatedInputFields(true);

    }


    public void setTitle(String title)
    {
        settingsItem.setTitle(title);
    }

    public void setSummary(String summary)
    {
        settingsItem.setSummary(summary);
    }
}

