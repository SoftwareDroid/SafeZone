package com.example.ourpact3.ui.settings;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import com.example.ourpact3.learn_mode.AppLearnProgress;
import com.example.ourpact3.model.CheatKeyManager;
import com.example.ourpact3.util.CurrentTimestamp;
import com.example.ourpact3.util.ServiceUtil;

public class SettingsFragment extends Fragment
{

    private FragmentSettingsBinding binding;
    private boolean dirtySettings;

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
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                // Proceed with enabling the lock
                                setLockState(true);
                                updateUI();
                            }
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

        binding.buttonDisableLock.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Create an EditText to get user input for the license key
                final EditText input = new EditText(v.getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT); // Regular text input for license key

                // Create an AlertDialog to prompt for the license key
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.enter_master_key)
                        .setMessage(R.string.enter_master_key_to_proced)
                        .setView(input)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String enteredKey = input.getText().toString();
                                // Check if the entered key is correct
                                if (isMasterKeyCorrect(enteredKey))
                                {
                                    setLockState(false);
                                    updateUI();
                                } else
                                {
                                    // Show a message if the key is incorrect
                                    Toast.makeText(v.getContext(), R.string.message_incorrect_master_key, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
        // init checkbox

        binding.useWarnWindowsCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.dirtySettings = true;
        });
        binding.logBlockingCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.dirtySettings = true;
        });

//        final TextView textView = binding.textNotifications;
//        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void updateUI()
    {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
        boolean preventDisableing = sharedPreferences.getBoolean(PreferencesKeys.PREVENT_DISABLING, PreferencesKeys.PREVENT_DISABLING_DEFAULT_VALUE);
        boolean useWarnWindows = sharedPreferences.getBoolean(PreferencesKeys.OPTION_USE_WARN_WINDOWS, PreferencesKeys.OPTION_USE_WARN_WINDOWS_DEFAULT);
        boolean useLogging = sharedPreferences.getBoolean(PreferencesKeys.OPTION_LOG_BLOCKING, PreferencesKeys.OPTION_LOG_BLOCKING_DEFAULT);
        binding.logBlockingCheckbox.setChecked(useLogging);
        binding.useWarnWindowsCheckbox.setChecked(useWarnWindows);

        binding.buttonDisableLock.setVisibility(View.VISIBLE);
        binding.buttonEnableLock.setVisibility(View.VISIBLE);
        if (!preventDisableing)
        {
            binding.buttonDisableLock.setVisibility(View.GONE);
        } else
        {
            boolean hasAccessService = ServiceUtil.isAccessibilityServiceEnabled(getContext(), ContentFilterService.class);
            ;
            binding.buttonDisableLock.setEnabled(hasAccessService);
            binding.buttonEnableLock.setVisibility(View.GONE);
        }

    }

    public boolean isMasterKeyCorrect(String key)
    {
        // Get the SharedPreferences object
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);

        String masterKeyHash = sharedPreferences.getString(PreferencesKeys.MASTER_KEY_HASH, PreferencesKeys.MASTER_KEY_DEFAULT_VALUE);
        return CheatKeyManager.calculateHashFromString(key).equals(masterKeyHash);
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
        if(this.dirtySettings)
        {
            boolean useWarnWindows = binding.useWarnWindowsCheckbox.isChecked();
            boolean logBlocking = binding.logBlockingCheckbox.isChecked();
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PreferencesKeys.OPTION_USE_WARN_WINDOWS, useWarnWindows);
            editor.putBoolean(PreferencesKeys.OPTION_LOG_BLOCKING, logBlocking);
            editor.apply();
        }
        sendCommandToService(ContentFilterService.COMMAND_RELOAD_SETTINGS);
    }


    private void sendCommandToService(String command) {
        Intent intent = new Intent("SEND_COMMAND");
        intent.putExtra("command", command);
        requireActivity().sendBroadcast(intent);
    }
    @Override
    public void onDestroyView()
    {
        saveSettings();
        super.onDestroyView();
        binding = null;
    }
}