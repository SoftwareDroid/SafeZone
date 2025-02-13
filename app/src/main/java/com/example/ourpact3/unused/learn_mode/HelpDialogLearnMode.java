package com.example.ourpact3.unused.learn_mode;

// DialogActivity.java

import android.app.Activity;
import android.os.Bundle;
import android.app.AlertDialog;

import com.example.ourpact3.R;

public class HelpDialogLearnMode extends Activity
{
    private static OnDialogClosedListener mListener;

    public static void setOnDialogClosedListener(OnDialogClosedListener listener) {
        mListener = listener;
    }

    public interface OnDialogClosedListener {
        void onHelpDialogClosed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Create and show the dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.help_learn_mode_title)
                .setMessage(R.string.help_learn_mode_body)
                .setPositiveButton("OK", (dialog, which) -> {
                    setResult(Activity.RESULT_OK);
                    mListener.onHelpDialogClosed();
                    finish();
                })
//                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .show();
    }
}

