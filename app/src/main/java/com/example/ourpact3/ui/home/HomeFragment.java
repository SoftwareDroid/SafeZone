package com.example.ourpact3.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.admin.DevicePolicyManager;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ourpact3.ContentFilterService;
import com.example.ourpact3.MyDeviceAdminReceiver;
import com.example.ourpact3.PreferencesKeys;
import com.example.ourpact3.R;
import com.example.ourpact3.databinding.FragmentHomeBinding;
import com.example.ourpact3.util.CurrentTimestamp;
import com.example.ourpact3.util.ServiceUtil;

import androidx.activity.result.contract.ActivityResultContracts;
import android.content.ComponentName;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment
{
    private ActivityResultLauncher<Intent> deviceAdminRequestLauncher;
    private FragmentHomeBinding binding;
    //    private Button buttonRequestOverlayPermission;
    private Button buttonRequestDeviceAdmin;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private ComponentName deviceAdminComponent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        if (!Settings.canDrawOverlays(requireContext())) {
            int REQUEST_CODE = 101;
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            myIntent.setData(Uri.parse("package:" + requireActivity().getPackageName()));
            startActivityForResult(myIntent, REQUEST_CODE);
        }

        deviceAdminRequestLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    updateUIBasedOnPermissions();
                });

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // access button
        binding.requestAccessButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openAccessibilitySettings();
            }
        });
        binding.buttonRequestDeviceAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(requireContext(), MyDeviceAdminReceiver.class));
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "You need to activate Device Administrator to perform phonelost tasks!");
            deviceAdminRequestLauncher.launch(intent);
        });
        // Set app version
        binding.appVersion.setText(getAppVersion());

        updateUIBasedOnPermissions();

        return root;
    }
    private String getAppVersion() {
        boolean isDebuggable =  ( 0 != ( requireActivity().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE ) );

        try {
            PackageInfo packageInfo = requireActivity().getPackageManager().getPackageInfo(requireActivity().getPackageName(), 0);
            if(isDebuggable)
                return packageInfo.versionName + " (Debug)" ;
            else
            {
                return packageInfo.versionName + " (Release)";
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "N/A"; // Return a default value if not found
        }
    }


    private void updateUIBasedOnPermissions()
    {
        // Get the SharedPreferences object
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);

// Retrieve the boolean value
        boolean isPreventDisabling = sharedPreferences.getBoolean(PreferencesKeys.PREVENT_DISABLING, PreferencesKeys.PREVENT_DISABLING_DEFAULT_VALUE);
        boolean hasDeviceAdmin = ServiceUtil.hasDeviceAdmin(requireActivity());
        boolean hasAccessService = hasAccessibilityServicePermission(getContext());

        if (hasAccessService && hasDeviceAdmin)
        {
            if(isPreventDisabling)
            {
                String lockedSince = sharedPreferences.getString(PreferencesKeys.LOCKED_SINCE,"");
                if(lockedSince.isEmpty())
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    lockedSince =  CurrentTimestamp.getCurrentTimestamp();
                    editor.putString(PreferencesKeys.LOCKED_SINCE, lockedSince);
                    editor.apply();
                }
                String formattedString = getString(R.string.lock_state_secure, lockedSince);
                binding.lockStatus.setText(formattedString);
                binding.lockStatus.setTextColor(getResources().getColor(R.color.lime_dark));
            }
            else
            {
                binding.lockStatus.setText(R.string.app_working_but_disablable);
                binding.lockStatus.setTextColor(getResources().getColor(R.color.lemon_yellow));
            }
        } else if (hasAccessService && !hasDeviceAdmin)
        {
            binding.lockStatus.setText(getResources().getString(R.string.lock_state_no_admin));
            binding.lockStatus.setTextColor(getResources().getColor(R.color.purple_200));

        } else
        {
            binding.lockStatus.setText(getResources().getString(R.string.lock_state_disabled));
            binding.lockStatus.setTextColor(getResources().getColor(R.color.crimson));

        }
        binding.buttonRequestDeviceAdmin.setVisibility(View.GONE);
        binding.requestAccessButton.setVisibility(View.GONE);
        if (!hasAccessService)
        {
            binding.requestAccessButton.setVisibility(View.VISIBLE);
        }
        if (!hasDeviceAdmin && !hasAccessService)
        {
            binding.buttonRequestDeviceAdmin.setVisibility(View.VISIBLE);
        }
    }

    private void openAccessibilitySettings()
    {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_ACCESSIBILITY_SETTINGS);
    }

    private static final int REQUEST_CODE_ACCESSIBILITY_SETTINGS = 10;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACCESSIBILITY_SETTINGS)
        {
            updateUIBasedOnPermissions();
        }
    }
/*
    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService)
    {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext())
        {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }


    private boolean hasDeviceAdmin()
    {
        // Get the DevicePolicyManager
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) requireActivity().getSystemService(Context.DEVICE_POLICY_SERVICE);

        // Check if the DevicePolicyManager is not null
        if (devicePolicyManager != null)
        {
            ComponentName adminComponent = new ComponentName(getActivity(), MyDeviceAdminReceiver.class);
            return devicePolicyManager.isAdminActive(adminComponent);
        }
        return false;
    }*/

    private boolean hasAccessibilityServicePermission(Context context)
    {
        return ServiceUtil.isAccessibilityServiceEnabled(context, ContentFilterService.class);
    }
}
