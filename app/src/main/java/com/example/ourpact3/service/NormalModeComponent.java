package com.example.ourpact3.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.AppFilter;
import com.example.ourpact3.BlockLoggingService;
import com.example.ourpact3.model.PipelineButtonAction;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.pipeline.PipelineResultBase;

import org.json.JSONException;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NormalModeComponent implements IServiceEventHandler, IFilterResultCallback
{
    private IContentFilterService iContentFilterService;
    private final OverlayWindowManager overlayWindowManager;
    public TreeMap<String, AppFilter> appFilters = new TreeMap<>();
    private ConcurrentLinkedDeque<PipelineResultBase> pipelineResults = new ConcurrentLinkedDeque<PipelineResultBase>();
    public boolean useWarnWindows;
    public boolean useLogging;
    private final AccessibilityService service;
    private BlockLoggingService logger;

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
    }

    public void onAppChange(String oldApp, String newApp)
    {
        AppFilter oldApp2 = appFilters.get(oldApp);
        if(oldApp2 != null)
        {
            oldApp2.onStateChange(false);
        }
        AppFilter newApp2 = appFilters.get(newApp);
        if(newApp2 != null)
        {
            newApp2.onStateChange(true);
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

        if (result.getKillState() == PipelineResultBase.KillState.KILL_BEFORE_WINDOW)
        {
            // Kill app first we get the result a second time via callback wit state == KILLED
            this.iContentFilterService.activateAppKillMode(result);
            return;
        }
        {
            if (result.getButtonAction() == PipelineButtonAction.BACK_BUTTON)
            {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

            } else if (result.getButtonAction() == PipelineButtonAction.HOME_BUTTON)
            {
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);

            }
        }

        if (result.getWindowAction() == PipelineWindowAction.WARNING)
        {
            if (this.useWarnWindows)
            {
                int[] actions = result.getButtonAction() == PipelineButtonAction.NONE ? new int[0] : new int[]{(result.getButtonAction() == PipelineButtonAction.HOME_BUTTON ? AccessibilityService.GLOBAL_ACTION_HOME : AccessibilityService.GLOBAL_ACTION_BACK)};
                overlayWindowManager.showOverlayWindow(result, actions);
            }
        }

        if (useLogging)
        {
            if (result.getKillState() == PipelineResultBase.KillState.KILLED || result.getButtonAction() == PipelineButtonAction.BACK_BUTTON || result.getButtonAction() == PipelineButtonAction.HOME_BUTTON)
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
        AppFilter filter = this.appFilters.get(appName);
        if (filter == null && this.appFilters.containsKey(""))
        {
            filter = this.appFilters.get("");
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
}
