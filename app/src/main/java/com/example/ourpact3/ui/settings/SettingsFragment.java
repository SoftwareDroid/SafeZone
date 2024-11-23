package com.example.ourpact3.ui.settings;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ourpact3.ContentFilterService;
import com.example.ourpact3.PreferencesKeys;
import com.example.ourpact3.R;
import com.example.ourpact3.databinding.FragmentSettingsBinding;
import com.example.ourpact3.ui.AskForPinDialog;
import com.example.ourpact3.ui.CreatePinDialog;
import com.example.ourpact3.util.CurrentTimestamp;
import com.example.ourpact3.util.ServiceUtil;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

public class SettingsFragment extends Fragment
{

    private FragmentSettingsBinding binding;
    private boolean dirtySettings;
    private boolean isPINUsed;
    private Handler handler = new Handler(Looper.getMainLooper());
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        updateUI();

        binding.buttonEnableLock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Create an AlertDialog to warn the user
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.warning)
                        .setMessage(R.string.warning_enable_app_lock)
                        .setPositiveButton(R.string.yes, (dialog, which) -> {
                            // Proceed with enabling the lock
                            setLockState(true);
                            updateUI();
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel(); // Dismiss the dialog
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        });
        // Increase Timelock
        binding.incrementTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startIncrementTimeDialog();
            }
        });

        // end of increase timelock
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);

        //
        binding.buttonManagePIN.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!isPINUsed)
                {
                    setFirstPIN();

                } else
                {

                    setSecondPIN();
                    //
                }
            }
        });
        ////////////////
        binding.buttonDisableLock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);

                String usedPIN = sharedPreferences.getString(PreferencesKeys.USED_PIN, PreferencesKeys.USED_PIN_DEFAULT_VALUE);
                if (!usedPIN.isEmpty())
                {
                    askForPINAndDisableLockIfCorrect();
                } else
                {
                    // There was no PIN set
                    setLockState(false);
                    updateUI();
                }
            }
        });
        // init checkbox

        binding.useWarnWindowsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.dirtySettings = true;
        });
        binding.logBlockingCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.dirtySettings = true;
        });

        return root;
    }

    public void setSecondPIN()
    {
        Dialog dialog = AskForPinDialog.showPinInputDialog(getContext(), new AskForPinDialog.PinInputDialogListener()
        {
            @Override
            public void onPinEntered(String pin)
            {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
                String currentPIN = sharedPreferences.getString(PreferencesKeys.USED_PIN, PreferencesKeys.USED_PIN_DEFAULT_VALUE);
                if (currentPIN.isEmpty() || Objects.equals(pin, currentPIN))
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(PreferencesKeys.USED_PIN, PreferencesKeys.USED_PIN_DEFAULT_VALUE);
                    editor.apply();
                    updateUI();
                } else
                {
                    Toast.makeText(getContext(), R.string.incorrect_pin_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
    }

    private void startIncrementTimeDialog()
    {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);

        String timestampString = sharedPreferences.getString(PreferencesKeys.STRICT_MODE_GLOBAL_SETTINGS_TIME_LOCK_TIL, PreferencesKeys.STRICT_MODE_GLOBAL_SETTINGS_TIME_LOCK_TIL_DEFAULT);
        Instant loadedTimestamp = timestampString.isEmpty() ? Instant.now() : Instant.parse(timestampString);

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.enter_duration_h)
                .setMessage(R.string.message_enter_duration_h)
                .setView(input)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        int newDurationInH = Integer.parseInt(input.getText().toString());
                        if (newDurationInH > PreferencesKeys.MAX_NUMBER_TIME_LOCK_INC_IN_H)
                        {
                            Toast.makeText(getContext(), R.string.message_time_to_big, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // increase time
                        Instant newLockedTill = loadedTimestamp.plus(newDurationInH, ChronoUnit.HOURS);
                        // save time back
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PreferencesKeys.STRICT_MODE_GLOBAL_SETTINGS_TIME_LOCK_TIL, newLockedTill.toString());
                        editor.apply();
                        updateUI();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                })
                .show();
    }

    public void setFirstPIN()
    {
        Dialog dialog = CreatePinDialog.showPinDialog(getContext(), new CreatePinDialog.PinDialogListener()
        {
            @Override
            public void onPinConfirmed(String pin)
            {
                // Handle PIN confirmation
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PreferencesKeys.USED_PIN, pin);
                editor.apply();
                updateUI();
            }

            @Override
            public void onWrongSecondPin(String pin)
            {
                Toast.makeText(getContext(), R.string.pin_mismatch, Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }


    @SuppressLint("SetTextI18n")
    private void updateUI()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // Do heavy operations here
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
                boolean preventDisableing = sharedPreferences.getBoolean(PreferencesKeys.PREVENT_DISABLING, PreferencesKeys.PREVENT_DISABLING_DEFAULT_VALUE);
                boolean useWarnWindows = sharedPreferences.getBoolean(PreferencesKeys.OPTION_USE_WARN_WINDOWS, PreferencesKeys.OPTION_USE_WARN_WINDOWS_DEFAULT);
                boolean useLogging = sharedPreferences.getBoolean(PreferencesKeys.OPTION_LOG_BLOCKING, PreferencesKeys.OPTION_LOG_BLOCKING_DEFAULT);
                boolean hasAccessService = ServiceUtil.isAccessibilityServiceEnabled(getContext(), ContentFilterService.class);
                String usedPIN = sharedPreferences.getString(PreferencesKeys.USED_PIN, PreferencesKeys.USED_PIN_DEFAULT_VALUE);
                String timeLockTil = sharedPreferences.getString(PreferencesKeys.STRICT_MODE_GLOBAL_SETTINGS_TIME_LOCK_TIL, PreferencesKeys.STRICT_MODE_GLOBAL_SETTINGS_TIME_LOCK_TIL_DEFAULT);
                Instant currentTime = Instant.now();
                Instant lockedTill = timeLockTil.isEmpty() ? currentTime : Instant.parse(timeLockTil); // Default is now Lock

                // Update the UI on the main thread
                requireActivity().runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        binding.logBlockingCheckbox.setChecked(useLogging);
                        binding.useWarnWindowsCheckbox.setChecked(useWarnWindows);

                        binding.buttonDisableLock.setVisibility(View.VISIBLE);
                        binding.buttonEnableLock.setVisibility(View.VISIBLE);
                        if (!preventDisableing)
                        {
                            binding.buttonDisableLock.setVisibility(View.GONE);
                        } else
                        {
                            binding.buttonDisableLock.setEnabled(hasAccessService);
                            binding.buttonEnableLock.setVisibility(View.GONE);
                        }
                        // Update has PINSet
                        isPINUsed = !usedPIN.isEmpty();
                        binding.buttonManagePIN.setText(isPINUsed ? R.string.disable_pin : R.string.enable_pin);
                        // Update time lock
                        if (lockedTill.isAfter(currentTime))
                        {
                            binding.buttonDisableLock.setEnabled(false); // we have a time lock to disable unlock button
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
                                    .withZone(ZoneId.systemDefault());
                            String formattedTimestamp = formatter.format(lockedTill);
                            binding.timeLockStatus.setText(formattedTimestamp);
                        } else
                        {
                            binding.timeLockStatus.setText(R.string.no_time_lock);
                        }
                    }
                });
            }
        }).start();
    }


    private void askForPINAndDisableLockIfCorrect()
    {
        AskForPinDialog.showPinInputDialog(getContext(), new AskForPinDialog.PinInputDialogListener()
        {
            @Override
            public void onPinEntered(String pin)
            {
                // Handle PIN entry
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);

                String usedPIN = sharedPreferences.getString(PreferencesKeys.USED_PIN, PreferencesKeys.USED_PIN_DEFAULT_VALUE);
                if (usedPIN.isEmpty() || Objects.equals(pin, usedPIN))
                {
                    setLockState(false);
                    updateUI();
                } else
                {
                    Toast.makeText(getContext(), R.string.incorrect_pin_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isPinCorrect(String key)
    {
        // Get the SharedPreferences object
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);

        String usedPIN = sharedPreferences.getString(PreferencesKeys.USED_PIN, PreferencesKeys.USED_PIN_DEFAULT_VALUE);
        return usedPIN.equals(key);
    }

    private void setLockState(boolean preventDisabling)
    {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PreferencesKeys.PREVENT_DISABLING, preventDisabling);
        if (preventDisabling)
        {
            String lockedSince = CurrentTimestamp.getCurrentTimestamp();
            editor.putString(PreferencesKeys.LOCKED_SINCE, lockedSince);
        }
        editor.apply();
    }

    private void saveSettings()
    {
        if (this.dirtySettings)
        {
            boolean useWarnWindows = binding.useWarnWindowsCheckbox.isChecked();
            boolean logBlocking = binding.logBlockingCheckbox.isChecked();
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PreferencesKeys.OPTION_USE_WARN_WINDOWS, useWarnWindows);
            editor.putBoolean(PreferencesKeys.OPTION_LOG_BLOCKING, logBlocking);
            editor.apply();
            this.dirtySettings = false;
            sendCommandToService(ContentFilterService.COMMAND_RELOAD_SETTINGS);
        }
    }


    private void sendCommandToService(String command)
    {
        Intent intent = new Intent("SEND_COMMAND");
        intent.putExtra("command", command);
        requireActivity().sendBroadcast(intent);
    }

    // When e.g Home button, tab switched is pressed
    @Override
    public void onPause()
    {
        super.onPause();
        // Save settings or perform any necessary actions here
        saveSettings();
    }


    @Override
    public void onDestroyView()
    {
        saveSettings();
        super.onDestroyView();
        binding = null;
    }
}