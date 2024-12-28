package com.example.ourpact3.ui.settings;

import android.content.Context;

import com.example.ourpact3.R;

import android.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ReusableSettingsNumberInputView
{

    private ReusableSettingsItemView item;
    private Context context;
    private Integer min;
    private Integer max;
    private String title;
    private int currentNumber;
    private String summaryFormat = "%s";

    public ReusableSettingsNumberInputView(Context context, ReusableSettingsItemView item)
    {
        this.item = item;
        this.context = context;
        item.setOnClickListener(this::showDialog);
    }

    /**
     * @param title
     * @param summaryFormat use "%s" for input as output
     */
    public void setParameters(String title, String summaryFormat, int initialValue)
    {
        this.title = title;
        if(summaryFormat != null)
        {
            this.summaryFormat = summaryFormat;
        }
        setCurrentNumber(initialValue);
    }

    public void setLimits(Integer min, Integer max)
    {
        this.min = min;
        this.max = max;
    }

    private void showDialog(View v)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);

        // Set up the input
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(context.getString(R.string.ok), (dialog, which) -> {
            String userInput = input.getText().toString();
            if (!userInput.isEmpty())
            {
                int number = Integer.parseInt(userInput);
                if ((min != null && max != null) && (number < min || number > max))
                {
                    Toast.makeText(context, R.string.input_number_out_of_range, Toast.LENGTH_SHORT).show();
                } else
                {
                    // Handle the valid number input
                    setCurrentNumber(number);
                }
            } else
            {
                Toast.makeText(context, context.getString(R.string.input_cannot_be_empty), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void setCurrentNumber(int number)
    {
        this.setSummary(String.format(this.summaryFormat, number));
        this.currentNumber = number;
    }

    public int getCurrentNumber()
    {
        return this.currentNumber;
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

