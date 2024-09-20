package com.example.ourpact3.ui.home;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.admin.DevicePolicyManager;
import android.os.Build;
import android.provider.Settings;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ourpact3.ContentFilterService;
import com.example.ourpact3.R;
import com.example.ourpact3.databinding.FragmentHomeBinding;

import android.content.ComponentName;
import android.text.TextUtils;
import android.widget.Toast;

public class HomeFragment extends Fragment
{
    private ActivityResultLauncher<Intent> overlayPermissionLauncher;
    private FragmentHomeBinding binding;
    private Button buttonRequestOverlayPermission;
    private static final int REQUEST_CODE_ENABLE_ADMIN = 1;
    private ComponentName deviceAdminComponent;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState)
    {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        // Initialize the ActivityResultLauncher
        overlayPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (Settings.canDrawOverlays(requireActivity()))
                    {
                        startOverlayService(); // Start the service if permission is granted
                        buttonRequestOverlayPermission.setVisibility(View.GONE); // Hide the button
                    }
                });

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        buttonRequestOverlayPermission = binding.button; // Assuming this is your button
        buttonRequestOverlayPermission.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(requireActivity()))
            {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                overlayPermissionLauncher.launch(intent); // Use the launcher to request permission
            } else
            {
                startOverlayService();
            }
        });

        // Check if overlay permission is already granted and hide the button if it is
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(requireActivity()))
        {
            startOverlayService(); // Start the service if permission is granted
            buttonRequestOverlayPermission.setVisibility(View.GONE); // Hide the button if permission is granted
        }

        // Add the RequestAccessibilityServiceButton
        RequestAccessibilityServiceButton(getContext());

        return root;
    }

    private void startOverlayService()
    {
        // Service is part of other servie
        //        Intent serviceIntent = new Intent(requireActivity(), OverlayService.class);
//        requireActivity().startService(serviceIntent); // Call startService on the Activity
    }

    private void RequestAccessibilityServiceButton(Context context)
    {
        if (!hasAccessibilityServicePermission(context))
        {
            Button button = new Button(context);
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    openAccessibilitySettings();
                }
            });
            button.setText("Request Accessibility Service");
            binding.accessibilityButtonContainer.addView(button);
        } else
        {
            binding.accessibilityButtonContainer.setVisibility(View.GONE);
        }
    }

    private void openAccessibilitySettings()
    {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_ACCESSIBILITY_SETTINGS);
    }

    private static final int REQUEST_CODE_ACCESSIBILITY_SETTINGS = 10;
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 11;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACCESSIBILITY_SETTINGS)
        {
            // Check if the user has granted the accessibility service permission
            if (hasAccessibilityServicePermission(requireContext()))
            {
                binding.accessibilityButtonContainer.setVisibility(View.GONE);
            } else
            {
                // The user has not granted the permission, keep the button visible
                binding.accessibilityButtonContainer.setVisibility(View.VISIBLE);
            }
        }
    }

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

    private boolean hasAccessibilityServicePermission(Context context)
    {
        return isAccessibilityServiceEnabled(context, ContentFilterService.class);
    }
}
