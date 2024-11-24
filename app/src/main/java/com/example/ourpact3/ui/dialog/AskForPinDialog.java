package com.example.ourpact3.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

/**
 * How to use:
 * PinInputDialog.showPinInputDialog(context, new PinInputDialog.PinInputDialogListener() {
 *     @Override
 *     public void onPinEntered(String pin) {
 *         // Handle PIN entry
 *     }
 * });
 */
public class AskForPinDialog {

    public interface PinInputDialogListener {
        void onPinEntered(String pin);
    }

    public static Dialog showPinInputDialog(Context context, final PinInputDialogListener listener) {
        final EditText pinEditText = new EditText(context);
        pinEditText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        pinEditText.setHint("Enter PIN");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enter PIN");
        builder.setView(pinEditText);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pin = pinEditText.getText().toString();
                listener.onPinEntered(pin);
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

