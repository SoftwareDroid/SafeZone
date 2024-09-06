package com.example.ourpact3.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class AppKiller implements IServiceEventHandler{
    private AccessibilityService service;
    private IContentFilterService iContentFilterService;
    public AppKiller(AccessibilityService service,IContentFilterService iContentFilterService)
    {
        this.service = service;
        this.iContentFilterService = iContentFilterService;
    }
    public final String SETTINGS_PACKAGE = "com.android.settings";

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {

    }

    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        if(event.getPackageName() == SETTINGS_PACKAGE)
        {
            //TODO: press button FORCE_STOP and then OK
            performForceStop();
        }
    }
    public static void openAppSettingsForPackage(Context context, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);

        // Add the FLAG_ACTIVITY_NEW_TASK flag
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }
    private void performForceStop() {
        // Get the root node of the current window
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null) {
            // Find the "FORCE_STOP" button
            AccessibilityNodeInfo forceStopButton = findNodeByText(rootNode, "FORCE_STOP");
            if (forceStopButton != null) {
                forceStopButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                // Wait for the second popup to appear
                waitForOkButton();
            }
        }
    }

    private void waitForOkButton() {
        // Use a new thread to wait for the "OK" button to appear
        new Thread(() -> {
            try {
                // Wait for a short period to allow the second popup to appear
                Thread.sleep(1000); // Adjust the delay as needed

                // Check for the "OK" button in a loop
                for (int i = 0; i < 5; i++) { // Check for 5 attempts
                    AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
                    if (rootNode != null) {
                        AccessibilityNodeInfo okButton = findNodeByText(rootNode, "OK");
                        if (okButton != null) {
                            okButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break; // Exit the loop if the button is clicked
                        }
                    }
                    Thread.sleep(500); // Wait before checking again
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo node, String text) {
        // Recursively search for a node with the specified text
        if (node.getText() != null && node.getText().toString().equals(text)) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            AccessibilityNodeInfo result = findNodeByText(child, text);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
}
