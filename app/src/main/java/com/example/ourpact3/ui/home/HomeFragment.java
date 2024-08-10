package com.example.ourpact3.ui.home;

import android.provider.Settings;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ourpact3.databinding.FragmentHomeBinding;

import android.content.ComponentName;
import android.text.TextUtils;

import com.example.ourpact3.ContentFilerService;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Button buttonRequestOverlayPermission;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Add a button to the layout
        buttonRequestOverlayPermission = binding.button;
        buttonRequestOverlayPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
            }
        });

        // Add the RequestAccessibilityServiceButton
        RequestAccessibilityServiceButton(getContext());

        return root;
    }

    /*public void RequestOverlayPermission(Context context) {
        if (!Settings.canDrawOverlays(context)) {
            // Request the permission
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            startActivityForResult(intent, REQUEST_CODE_OVERLAY_PERMISSION);
        } else {
            buttonRequestOverlayPermission.setVisibility(View.GONE);
        }
    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void RequestAccessibilityServiceButton(Context context) {
        if (!hasAccessibilityServicePermission(context)) {
            Button button = new Button(context);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAccessibilitySettings();
                }
            });
            button.setText("Request Accessibility Service");
            binding.accessibilityButtonContainer.addView(button);
        } else {
            binding.accessibilityButtonContainer.setVisibility(View.GONE);
        }
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, REQUEST_CODE_ACCESSIBILITY_SETTINGS);
    }

    private static final int REQUEST_CODE_ACCESSIBILITY_SETTINGS = 10;
    private static final int REQUEST_CODE_OVERLAY_PERMISSION = 11;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ACCESSIBILITY_SETTINGS) {
            // Check if the user has granted the accessibility service permission
            if (hasAccessibilityServicePermission(requireContext())) {
                binding.accessibilityButtonContainer.setVisibility(View.GONE);
            } else {
                // The user has not granted the permission, keep the button visible
                binding.accessibilityButtonContainer.setVisibility(View.VISIBLE);
            }
        }
       /* if (requestCode == REQUEST_CODE_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(getContext())) {
                buttonRequestOverlayPermission.setVisibility(View.GONE);
            }
        }*/
    }

    //TODO: https://stackoverflow.com/questions/5081145/android-how-do-you-check-if-a-particular-accessibilityservice-is-enabled

    /**
     * Based on {@link com.android.settingslib.accessibility.AccessibilityUtils#getEnabledServicesFromSettings(Context, int)}
     *
     * @see <a href="https://github.com/android/platform_frameworks_base/blob/d48e0d44f6676de6fd54fd8a017332edd6a9f096/packages/SettingsLib/src/com/android/settingslib/accessibility/AccessibilityUtils.java#L55">AccessibilityUtils</a>
     */
    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    private boolean hasAccessibilityServicePermission(Context context) {
        boolean var = isAccessibilityServiceEnabled(context, ContentFilerService.class);
        return var;
        /*
        String permission = android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = context.checkCallingOrSelfPermission(permission);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return true;*/
    }
}


