package com.example.ourpact3.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.PowerManager;
import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.pipeline.CounterAction;
import com.example.ourpact3.smart_filter.AppFilter;
import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.pipeline.PipelineResultBase;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NormalModeComponent implements IServiceEventHandler, IFilterResultCallback
{
    private IContentFilterService iContentFilterService;
    private final OverlayWindowManager overlayWindowManager;
    public TreeMap<String, AppFilter> appToFilters = new TreeMap<>();
    private ConcurrentLinkedDeque<PipelineResultBase> pipelineResults = new ConcurrentLinkedDeque<PipelineResultBase>();
    public boolean useWarnWindows;
    public boolean useLogging;
    private final AccessibilityService service;
    private BlockLoggingService logger;
    private PowerManager powerManager;
    private boolean isScreenOn = true;

    public void destroyGUI()
    {
        this.overlayWindowManager.hideOverlayWindow();
    }

    public NormalModeComponent(Context context, AccessibilityService service, IContentFilterService iContentFilterService)
    {
        this.logger = new BlockLoggingService(context);
        this.service = service;
        this.overlayWindowManager = new OverlayWindowManager(service);
        this.iContentFilterService = iContentFilterService;

        powerManager = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
    }

    public void onAppChange(String oldApp, String newApp)
    {
        String systemUI_id = "com.android.systemui";
        // ignore changes just to from the system ui
        // interferes with productivity filter
        if (oldApp.equals(systemUI_id) || newApp.equals(systemUI_id))
        {
            return;
        }
        AppFilter oldApp2 = appToFilters.get(oldApp);
        if (oldApp2 != null)
        {
            oldApp2.onAppStateChange(false);
        }
        AppFilter newApp2 = appToFilters.get(newApp);
        if (newApp2 != null)
        {
            newApp2.onAppStateChange(true);
        }
    }

    public void onPipelineResultForeground(PipelineResultBase result)
    {
        if (iContentFilterService.isPackageIgnoredForNormalMode(result.getTriggerPackage()))
        {
            return;
        }
        if (this.iContentFilterService.getMode() == IContentFilterService.Mode.LEARN_OVERLAY_MODE)
        {
            iContentFilterService.forwardPipelineResultToLearner(result);
            return;
        }
        CounterAction a = result.getCounterAction();
        if (a.getKillState() == CounterAction.KillState.KILL_BEFORE_WINDOW)
        {
            // Kill app first we get the result a second time via callback wit state == KILLED
            this.iContentFilterService.activateAppKillMode(result);
            return;
        }
        {
            if (a.getButtonAction() == PipelineButtonAction.BACK_BUTTON)
            {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

            } else if (a.getButtonAction() == PipelineButtonAction.HOME_BUTTON)
            {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);

            }
        }

        if (a.getWindowAction() == PipelineWindowAction.WARNING)
        {
            if (this.useWarnWindows)
            {
                int[] actions = a.getButtonAction() == PipelineButtonAction.NONE ? new int[0] : new int[]{(a.getButtonAction() == PipelineButtonAction.HOME_BUTTON ? AccessibilityService.GLOBAL_ACTION_HOME : AccessibilityService.GLOBAL_ACTION_BACK)};
                overlayWindowManager.showOverlayWindow(result, actions);
            }
        }

        if (useLogging)
        {
            if (a.getKillState() == CounterAction.KillState.KILLED || a.getButtonAction() == PipelineButtonAction.BACK_BUTTON || a.getButtonAction() == PipelineButtonAction.HOME_BUTTON)
            {
                logger.logScreenBG(result.getScreen(), result);
            }
        }
    }

    @Override
    public void start()
    {

    }

    @Override
    public void stop()
    {

    }

    private String lastUsedApp = "";

    public void updatePotentialAppChange(String appName)
    {
        if (appName != null && !appName.equals(lastUsedApp))
        {
            this.iContentFilterService.onAppChange(lastUsedApp, appName);
            lastUsedApp = appName;
        }
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        String appName = event.getPackageName().toString();
        AppFilter filter = this.appToFilters.get(appName);
        if (filter == null && this.appToFilters.containsKey(""))
        {
            filter = this.appToFilters.get("");
            assert filter != null;
            filter.cancelAllCallbacks();

        }
        switch (event.getEventType())
        {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                updatePotentialAppChange(appName);
                break;
        }

        if (filter != null && !this.iContentFilterService.isPackageIgnoredForNormalMode(appName))
        {
            filter.processEvent(event);
        }
    }


    public void processPipelineResults()
    {
        while (!this.pipelineResults.isEmpty())
        {
            PipelineResultBase result = pipelineResults.poll();
            this.onPipelineResultForeground(result);
        }
    }


    @Override
    public void onPipelineResultBackground(PipelineResultBase result)
    {
        pipelineResults.push(result);
    }

    public void onScreenStateChange(boolean isScreenOn)
    {
        for (String key : appToFilters.keySet()) {
            AppFilter filter = appToFilters.get(key);
            assert filter != null;
            filter.onScreenStateChanged(isScreenOn);
        }
    }
}
