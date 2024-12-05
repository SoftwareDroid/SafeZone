package com.example.ourpact3.service;
import com.example.ourpact3.R; // Adjust the package name as necessary

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.pipeline.PipelineResultBase;

import org.jetbrains.annotations.NotNull;

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
    private Context ctx;
    private Mode mode;
    private PipelineResultBase pipelineResult;
    private final String SETTINGS_PACKAGE = "com.android.settings";
    private boolean isSettingsOpen = false; // Track if settings dialog is open
    // Number of attempts to reopen settings if closed
    private final int MAX_ATTEMPTS = 3;
    private int attemptCount = 0;

    public AppKiller(AccessibilityService service, IContentFilterService iContentFilterService)
    {
        this.ctx = service.getApplicationContext();
        this.isSettingsOpen = false;
        this.service = service;
        this.iContentFilterService = iContentFilterService;
        this.mode = Mode.FINISHED; // wait til setApp
    }

    public void setApp(PipelineResultBase result)
    {
        if (result.getTriggerPackage() != null && !Objects.equals(result.getTriggerPackage(), this.service.getPackageName()))
        {
            this.pipelineResult = result;
            this.attemptCount = 0;
            this.mode = Mode.WAIT_FOR_KILLING;
            openAppSettingsForPackage(service.getApplicationContext(), this.pipelineResult.getTriggerPackage());
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

    public void onAccessibilityEvent(AccessibilityEvent event) throws InterruptedException
    {
        if (mode == Mode.FINISHED)
        {
            Log.d("KILLER", "I AM FINISHED");
            return;
        }
        if (pipelineResult.getTriggerPackage() != null && event.getPackageName().toString().equals(this.SETTINGS_PACKAGE))
        {
            Log.d("KILLER", "branch 1");

            performForceStop();
            isSettingsOpen = true;
        } else if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED)
        {
            Log.d("KILLER", "branch 2");

            // If the current window is not the settings package, it means the user closed it
            if (!event.getPackageName().toString().equals(this.SETTINGS_PACKAGE))
            {
                Log.d("KILLER", "branch 2a");
                isSettingsOpen = false; // Reset the state if settings are closed
                attemptReopenSettings(); // Reopen settings dialog
            }
        }
    }

    public static void openAppSettingsForPackage(Context context, String packageName)
    {
        Log.d("KILLER", "OPEN SETIINGS FOR APP");

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void performForceStop() throws InterruptedException
    {
        int MAX_NUMBER_OK_TRIES = 3;
        for (int i = 0; i < MAX_NUMBER_OK_TRIES; i++)
        {
            Log.d("KILLER", "PERFORM STOP " + String.valueOf(i));

            AccessibilityNodeInfo rootNode = service.getRootInActiveWindow();
            if (rootNode != null)
            {
                AccessibilityNodeInfo forceStopButton = findNodeByText(rootNode, ctx.getString(R.string.force_kill_button));
                AccessibilityNodeInfo okButton = findNodeByText(rootNode,  ctx.getString(R.string.force_kill_ok_button));
                if (okButton != null)
                {
                    Log.d("KILLER", "CLICK B");

                    okButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    finishKilling();
                    return; // Exit the loop if the button is clicked
                } else if (forceStopButton != null)
                {
                    if (forceStopButton.isEnabled())
                    {
                        Log.d("KILLER", "CLICK A");

                        forceStopButton.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        this.mode = AppKiller.Mode.WAIT_FOR_KILLING;
                    } else
                    {
                        finishKilling();
                    }
                }
                Thread.sleep(200);
            }
        }
    }

    private void finishKilling()
    {
        Log.d("KILLER", "FINISH KILLING 0");

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
        Log.d("Killer", "set to killed");
        this.pipelineResult.getCounterAction().setKillState(CounterAction.KillState.KILLED);
        this.iContentFilterService.finishAppKilling(this.pipelineResult);

    }

    private void attemptReopenSettings()
    {
        Log.d("KILLER", "reopen settings");
        if (attemptCount < MAX_ATTEMPTS && !isSettingsOpen)
        {
            attemptCount++;
            openAppSettingsForPackage(service.getApplicationContext(), this.pipelineResult.getTriggerPackage());
        } else
        {
            Log.d("KILLER", "FINISH KILLING 1");
            // Reset attempt count if max attempts reached
            finishKilling();
        }
    }

    private AccessibilityNodeInfo findNodeByText(AccessibilityNodeInfo node, @NotNull String text)
    {
        if (node == null)
        {
            Log.d("KILLER","findNodeByText null");
            return null;
        }
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
