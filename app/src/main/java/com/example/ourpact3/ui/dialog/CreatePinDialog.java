package com.example.ourpact3.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;

/*
PinDialog.showPinDialog(context, new PinDialog.PinDialogListener() {
    @Override
    public void onPinConfirmed(String pin) {
        // Handle PIN confirmation
    }
    @Override
    public void onWrongSecondPIN(String pin)
    {
    }
});
 */

public class CreatePinDialog
{

    public interface PinDialogListener {
        void onPinConfirmed(String pin);
        void onWrongSecondPin(String pin);
    }

    public static Dialog showPinDialog(Context context, final PinDialogListener listener) {
        final EditText pinEditText = new EditText(context);
        pinEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinEditText.setHint("Enter PIN");

        final EditText confirmPinEditText = new EditText(context);
        confirmPinEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        confirmPinEditText.setHint("Confirm PIN");

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(pinEditText);
        layout.addView(confirmPinEditText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter PIN");
        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pin = pinEditText.getText().toString();
                String confirmPin = confirmPinEditText.getText().toString();

                if (pin.equals(confirmPin)) {
                    listener.onPinConfirmed(pin);
                } else {
                    listener.onWrongSecondPin(pin);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }
}