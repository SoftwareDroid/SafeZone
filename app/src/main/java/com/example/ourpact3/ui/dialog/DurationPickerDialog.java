package com.example.ourpact3.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ourpact3.R;

public class DurationPickerDialog
{

    public interface DurationPickerListener
    {
        void onDurationPicked(int durationInMinutes);
    }

    private Dialog dialog;
    private EditText editTextMinutes;

    public DurationPickerDialog(Context context, final DurationPickerDialog.DurationPickerListener listener, int minValue, int maxValue)
    {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_duration_picker);
        dialog.setTitle(context.getString(R.string.duration_picker));

        editTextMinutes = dialog.findViewById(R.id.edit_input_2);
        Button buttonOk = dialog.findViewById(R.id.button_ok);

        buttonOk.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String input = editTextMinutes.getText().toString();
                if (!input.isEmpty())
                {
                    int selectedMinutes = Integer.parseInt(input);
                    if (selectedMinutes < minValue || selectedMinutes > maxValue)
                    {
                        Toast.makeText(context, "Please enter a value between " + minValue + " and " + maxValue, Toast.LENGTH_SHORT).show();
                    } else
                    {
                        listener.onDurationPicked(selectedMinutes);
                    }
                } else
                {
                    Toast.makeText(context, context.getString(R.string.invalid_number), Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss(); // Close the dialog
            }
        });
    }

    public void show()
    {
        dialog.show(); // Show the dialog
    }
}
