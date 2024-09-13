package com.example.ourpact3.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.AppFilter;
import com.example.ourpact3.model.PipelineResultBase;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class NormalModeComponent implements IServiceEventHandler, IFilterResultCallback
{
    private IContentFilterService iContentFilterService;
    private final OverlayWindowManager overlayWindowManager;
    public TreeMap<String, AppFilter> keywordFilters = new TreeMap<>();
    private ConcurrentLinkedDeque<PipelineResultBase> pipelineResults = new ConcurrentLinkedDeque<PipelineResultBase>();

    private final AccessibilityService service;
    public void destroyGUI()
    {
        this.overlayWindowManager.hideOverlayWindow();
    }
    public NormalModeComponent(AccessibilityService service, IContentFilterService iContentFilterService)
    {
        this.service = service;
        this.overlayWindowManager = new OverlayWindowManager(service);
        this.iContentFilterService = iContentFilterService;
    }


    public void onPipelineResultForeground(PipelineResultBase result)
    {
        if(this.iContentFilterService.getMode() == IContentFilterService.Mode.LEARN_OVERLAY_MODE)
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
        switch (result.getWindowAction())
        {
            case WARNING:
            case PERFORM_HOME_BUTTON_AND_WARNING:
                overlayWindowManager.showOverlayWindow(result, new int[]{AccessibilityService.GLOBAL_ACTION_BACK, AccessibilityService.GLOBAL_ACTION_HOME});
                break;
            case PERFORM_BACK_ACTION_AND_WARNING:
                overlayWindowManager.showOverlayWindow(result, new int[]{AccessibilityService.GLOBAL_ACTION_BACK});
                break;
            case PERFORM_BACK_ACTION:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case CONTINUE_PIPELINE:
                break;
            case STOP_FURTHER_PROCESSING:
                break;
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
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        AppFilter filter = this.keywordFilters.get(event.getPackageName());
        if (filter != null)
        {
            filter.processEvent(event);
        }
        if(event.getEventType() == AccessibilityEvent.TYPE_WINDOWS_CHANGED)
        {
            String app = event.getPackageName().toString();
            if(app != null && app != lastUsedApp)
            {
                lastUsedApp = app;
                this.iContentFilterService.onAppChange(lastUsedApp,app);
            }
        }
    }

    public void processPipelineResults()
    {
        while(!this.pipelineResults.isEmpty())
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
