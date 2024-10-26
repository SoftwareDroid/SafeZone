package com.example.ourpact3;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin Enabled", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Toast.makeText(context, "Device Admin Disabled", Toast.LENGTH_SHORT).show();
    }
}

/*
import static android.content.Context.ACCESSIBILITY_SERVICE;
+import static androidx.core.content.ContextCompat.getSystemService;
+
+import android.accessibilityservice.AccessibilityServiceInfo;
+import android.app.ActivityManager;
 import android.app.admin.DeviceAdminReceiver;
 import android.content.Context;
 import android.content.Intent;
+import android.net.Uri;
+import android.os.Handler;
+import android.provider.Settings;
+import android.view.accessibility.AccessibilityManager;
 import android.widget.Toast;

+import java.util.List;
+
 public class MyDeviceAdminReceiver extends DeviceAdminReceiver {
+    private static final int CHECK_INTERVAL = 1000 * 5; // 1 minute
+    private Handler handler;
+    private Runnable checkOverlayPermissionRunnable;
+
     @Override
     public void onEnabled(Context context, Intent intent) {
         Toast.makeText(context, "Device Admin Enabled", Toast.LENGTH_SHORT).show();
+        startCheckingOverlayPermission(context);
     }

     @Override
     public void onDisabled(Context context, Intent intent) {
         Toast.makeText(context, "Device Admin Disabled", Toast.LENGTH_SHORT).show();
+        stopCheckingOverlayPermission();
+    }
+
+    private void startCheckingOverlayPermission(Context context) {
+        handler = new Handler();
+        checkOverlayPermissionRunnable = new Runnable() {
+            @Override
+            public void run() {
+                if (isMyServiceRunning(context,ContentFilterService.class)) {
+                    // Overlay permission is already enabled
+                } else {
+                    // Overlay permission is not enabled, check if settings page is already open
+//                    if (!isSettingsPageOpen(context)) {
+                        // Settings page is not open, open it
+                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
+                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required for starting activity from service
+//                        intent.setData(Uri.parse("package:" + context.getPackageName()));
+                    context.startActivity(intent);
+//                    }
+                }
+                handler.postDelayed(this, CHECK_INTERVAL);
+            }
+        };
+        handler.postDelayed(checkOverlayPermissionRunnable, CHECK_INTERVAL);
     }
-}

+    private boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
+        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
+        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
+            if (serviceClass.getName().equals(service.service.getClassName())) {
+                return true;
+            }
+        }
+        return false;
+    }
+
+    private void stopCheckingOverlayPermission() {
+        if (handler != null) {
+            handler.removeCallbacks(checkOverlayPermissionRunnable);
+        }
+    }
+
+    private boolean isSettingsPageOpen(Context context) {
+        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
+        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
+        for (ActivityManager.RunningTaskInfo task : tasks) {
+            if (task.topActivity.getClassName().equals("com.android.settings.Settings$OverlaySettingsActivity")) {
+                return true;
+            }
+        }
+        return false;
+    }
+}

 */
