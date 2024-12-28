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
    private ReusableSettingsItemView item;
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

    public ReusableSettingsDurationInputView(Context context, ReusableSettingsItemView item)
    {
        this.item = item;
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
        this.item.setSummary(String.format(this.summaryFormat, this.number3, this.number2, this.number1));

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

    private void updateSeperatedInputFields()
    {
        // Split the current input into groups of two
        ArrayList<String> groups = splitIntoGroupsOfTwo(currentInputAsString);

        // Assign values based on the number of groups
        String lastTwoDigits = groups.size() > 0 ? groups.get(groups.size() - 1) : "";
        String middleTwoDigits = groups.size() > 1 ? groups.get(groups.size() - 2) : "";
        String firstTwoDigits = groups.size() > 2 ? groups.get(0) : "";
        this.input_1.setText(lastTwoDigits);
        if (lastTwoDigits.isEmpty())
        {
            number1 = 0;
        } else
        {
            number1 = Integer.valueOf(lastTwoDigits);
        }
        this.input_2.setText(middleTwoDigits);
        if (middleTwoDigits.isEmpty())
        {
            number2 = 0;
        } else
        {
            number2 = Integer.valueOf(middleTwoDigits);

        }
        this.input_3.setText(firstTwoDigits);
        if (firstTwoDigits.isEmpty())
        {
            number1 = 0;
        } else
        {
            number3 = Integer.valueOf(firstTwoDigits);
        }
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
            updateSeperatedInputFields();
        }
    }

    private void showDialog(View v)
    {
        // Create a new dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.stop_watch);
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
            currentInputAsString = currentInputAsString.substring(currentInputAsString.length() - 1);
            updateSeperatedInputFields();
        });
        numberDel.setOnLongClickListener(view -> {
            currentInputAsString = "";
            updateSeperatedInputFields();
            return true;
        });

        if (currentInputAsString == null)
        {
            reset();
        }
        // Set up the Cancel button click listener
        cancelButton.setOnClickListener(v2 -> {
            reset();
            dialog.dismiss();
        }); // Close the dialog
        okButton.setOnClickListener(v2 -> {
            this.item.setSummary(String.format(this.summaryFormat, this.number3, this.number2, this.number1));
            dialog.dismiss();
        }); // Close the dialog
        // Show the dialog
        dialog.show();
    }

    public void reset()
    {
        //setup start value, we always need a valid field
        this.currentInputAsString = startValue;
        updateSeperatedInputFields();

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

