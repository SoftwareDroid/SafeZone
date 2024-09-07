package com.example.ourpact3.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.ourpact3.model.PipelineResultBase;

import java.util.Objects;

public class AppKiller implements IServiceEventHandler
{
    private AccessibilityService service;
    private IContentFilterService iContentFilterService;

    enum Mode
    {
        WAIT_FOR_KILLING,
        FINISHED,
    }

    private Mode mode;
    private PipelineResultBase piplineResult;
    private final String SETTINGS_PACKAGE = "com.android.settings";
    private boolean isSettingsOpen = false; // Track if settings dialog is open
    // Number of attempts to reopen settings if closed
    private final int MAX_ATTEMPTS = 5;
    private int attemptCount = 0;

    public AppKiller(AccessibilityService service, IContentFilterService iContentFilterService)
    {
        this.isSettingsOpen = false;
        this.service = service;
        this.iContentFilterService = iContentFilterService;
        this.mode = Mode.FINISHED; // wait til setApp
    }

    public void setApp(PipelineResultBase result)
    {
        if (result.triggerPackage != null && !Objects.equals(result.triggerPackage, this.service.getPackageName()))
        {
            this.piplineResult = result;
            this.attemptCount = 0;
            this.mode = Mode.WAIT_FOR_KILLING;
            openAppSettingsForPackage(service.getApplicationContext(), this.piplineResult.triggerPackage);
        } else
        {
            finishKilling();
        }
    }

    @Override
    public void start()
    {
        // Start logic if needed
    }

    @Override
    public void stop()
    {
        // Stop logic if needed
    }

    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        if (mode == Mode.FINISHED)
        {
            return;
        }
        if (piplineResult.triggerPackage != null && event.getPackageName().toString().equals(this.SETTINGS_PACKAGE))
        {
            performForceStop();
            isSettingsOpen = true;
        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        {
            // If the current window is not the settings package, it means the user closed it
            if (!event.getPackageName().toString().equals(this.SETTINGS_PACKAGE))
            {
                isSettingsOpen = false; // Reset the state if settings are closed
                attemptReopenSettings(); // Reopen settings dialog
            }
        }
    }

    public static void openAppSettingsForPackage(Context context, String packageName)
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void performForceStop()
    {
        AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
        if (rootNode != null)
        {
            AccessibilityNodeInfo forceStopButton = findNodeByText(rootNode, "FORCE STOP");
            if (forceStopButton != null)
            {
                if (forceStopButton.isEnabled())
                {
                    forceStopButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    this.mode = AppKiller.Mode.WAIT_FOR_KILLING;
                    waitForOkButton();
                } else
                {
                    finishKilling();
                }
            }
        }
    }

    private void waitForOkButton()
    {
        try
        {
            // Continuously check for the "OK" button
            int MAX_NUMBER_OK_TRIES = 8;
            for (int i = 0; i < MAX_NUMBER_OK_TRIES; i++)
            { // Check for a maximum of 10 attempts
                // Wait for a short period to allow the second popup to appear
                AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
                if (rootNode != null)
                {
                    AccessibilityNodeInfo okButton = findNodeByText(rootNode, "OK");
                    if (okButton != null)
                    {
                        okButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        finishKilling();
                        return; // Exit the loop if the button is clicked
                    }
                }
                // Short sleep to avoid overwhelming the system
                Thread.sleep(50); // Sleep for 50 milliseconds
            }
            finishKilling();    // go back without killing
        } catch (InterruptedException e)
        {
            finishKilling();
            e.printStackTrace();
        }
    }

    private void finishKilling()
    {
        this.mode = Mode.FINISHED;
        isSettingsOpen = false;
        try
        {
            Thread.sleep(50); // Sleep for 50 milliseconds
        } catch (InterruptedException ignored)
        {

        }
        // Close settings agaih
        this.service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        // Callback process completed and change to killed
        this.piplineResult.killState = PipelineResultBase.KillState.KILLED;
        this.iContentFilterService.finishAppKilling(this.piplineResult);

    }

    private void attemptReopenSettings()
    {
        if (attemptCount < MAX_ATTEMPTS && !isSettingsOpen)
        {
            attemptCount++;
            openAppSettingsForPackage(service.getApplicationContext(), this.piplineResult.triggerPackage);
        } else
        {
            // Reset attempt count if max attempts reached
            finishKilling();
        }
    }

    private AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo node, String text)
    {
        if (node.getText() != null && node.getText().toString().equals(text))
        {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++)
        {
            AccessibilityNodeInfo child = node.getChild(i);
            AccessibilityNodeInfo result = findNodeByText(child, text);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }
}
