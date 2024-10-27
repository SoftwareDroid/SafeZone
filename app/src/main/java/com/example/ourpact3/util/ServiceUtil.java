package com.example.ourpact3.util;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.example.ourpact3.MyDeviceAdminReceiver;

public class ServiceUtil {

    // Method to check if an accessibility service is enabled
    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null) {
            return false;
        }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName)) {
                return true;
            }
        }

        return false;
    }

    // Method to check if the device admin is active
    public static boolean hasDeviceAdmin(Activity activity) {
        // Get the DevicePolicyManager
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);

        // Check if the DevicePolicyManager is not null
        if (devicePolicyManager != null) {
            ComponentName adminComponent = new ComponentName(activity, MyDeviceAdminReceiver.class);
            return devicePolicyManager.isAdminActive(adminComponent);
        }
        return false;
    }
}

