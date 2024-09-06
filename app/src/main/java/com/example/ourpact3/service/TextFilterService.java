package com.example.ourpact3.service;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;

import com.example.ourpact3.AppFilter;
import com.example.ourpact3.model.PipelineResultBase;

import java.util.TreeMap;

public class TextFilterService implements IServiceEventHandler, IFilterResultCallback
{
    private IContentFilterService iContentFilterService;
    private OverlayWindowManager overlayWindowManager;
    public TreeMap<String, AppFilter> keywordFilters = new TreeMap<>();

    private AccessibilityService service;
    public TextFilterService(AccessibilityService service, IContentFilterService iContentFilterService)
    {
        this.iContentFilterService = iContentFilterService;
        this.service = service;
        this.overlayWindowManager = new OverlayWindowManager(service);
    }

    @Override
    public void onPipelineResult(PipelineResultBase result)
    {
        switch (result.windowAction)
        {
            case WARNING:
            case PERFORM_HOME_BUTTON_AND_WARNING:
                overlayWindowManager.showOverlayWindow(result, new int[] {AccessibilityService.GLOBAL_ACTION_BACK, AccessibilityService.GLOBAL_ACTION_HOME});
                break;
            case PERFORM_BACK_ACTION_AND_WARNING:
                overlayWindowManager.showOverlayWindow(result, new int[] {AccessibilityService.GLOBAL_ACTION_BACK});
                break;
            case PERFORM_BACK_ACTION:
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case CONTINUE_PIPELINE:
                break;
            case STOP_FURTHER_PROCESSING:
                break;
        }
        if(result.interruptSound)
        {
//            playSoundAndOverwriteMedia(this,"sounds/silence.mp3");
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

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        AppFilter filter = this.keywordFilters.get(event.getPackageName());
        if (filter != null)
        {
            filter.processEvent(event);
        }
    }
}
