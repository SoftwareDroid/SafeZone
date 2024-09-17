package com.example.ourpact3.learn_mode;


import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.MenuItem;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.ourpact3.R;
import com.example.ourpact3.pipeline.PipelineResultBase;
import com.example.ourpact3.pipeline.PipelineResultLearnedMode;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.service.IContentFilterService;
import com.example.ourpact3.service.ScreenInfoExtractor;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.smart_filter.UI_ID_Filter;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class LearnModeComponent implements HelpDialogLearnMode.OnDialogClosedListener
{
    private final Map<String, AppLearnProgress> appIdToLearnProgress = new TreeMap<>();
    private boolean drawAtLeftEdge = true;
    private final WindowManager windowManager;
    private View overlayButtons;
    private final Context context;
    private final IContentFilterService iContentFilterService;
    private TextView currentStatus;
    private final AccessibilityService service;
    private CheckBox checkboxThumpUp;
    private CheckBox checkboxThumpDown;

    public LearnModeComponent(@NotNull Context context, @NotNull IContentFilterService iContentFilterService, AccessibilityService service)
    {
        if (!Settings.canDrawOverlays(context))
        {
            throw new RuntimeException("Need draw overlay Permission");
        }
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.context = context;
        this.iContentFilterService = iContentFilterService;
        this.service = service;
    }

    @SuppressLint("InflateParams")
    public void createOverlay()
    {
        if (overlayButtons != null)
        {
            return;
        }
        overlayButtons = LayoutInflater.from(context).inflate(R.layout.learn_mode, null);

        // Set the layout parameters for the overlay
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = (drawAtLeftEdge ? Gravity.START : Gravity.END) | Gravity.CLIP_VERTICAL; // Position the overlay
        params.x = 0;
        params.y = 0; // No need to adjust y, as it's centered vertically

        // Add the view to the window
        windowManager.addView(overlayButtons, params);

        // Set up button click listeners
        this.checkboxThumpUp = overlayButtons.findViewById(R.id.thumb_up);
        this.checkboxThumpDown = overlayButtons.findViewById(R.id.thumb_down);
        Button buttonSettings = overlayButtons.findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(this::showSettingsContextMenu);

        currentStatus = overlayButtons.findViewById(R.id.current_status);

        this.checkboxThumpUp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (programmaticCheckBoxChange)
            {
                return;
            }

            if (isChecked)
            {
                checkboxThumpDown.setChecked(false);
            }
            this.labelCurrentScreen(isChecked ? AppLearnProgress.ScreenLabel.GOOD : AppLearnProgress.ScreenLabel.NOT_LABELED);

        });

        this.checkboxThumpDown.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (programmaticCheckBoxChange)
            {
                return;
            }
            if (isChecked)
            {
                checkboxThumpUp.setChecked(false);
            }
            this.labelCurrentScreen(isChecked ? AppLearnProgress.ScreenLabel.BAD : AppLearnProgress.ScreenLabel.NOT_LABELED);

        });

        // Handle touch events to allow interaction with the underlying app
        overlayButtons.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE)
            {
                stopOverlay(); // Stop the overlay if touched outside
            }
            return false; // Allow touch events to pass through
        });
    }

    private PipelineResultBase lastResult;

    private void loadLearnProgressFromDisk()
    {
        if (this.lastResult != null)
        {
            if (this.lastResult.getTriggerPackage() != null)
            {
                AppLearnProgress progress = LearnProgressSaver.load(this.context, this.lastResult.getTriggerPackage());
                if (progress != null)
                {
                    this.appIdToLearnProgress.put(lastResult.getTriggerPackage(), progress);
                    saveLearned();
                }
            }
        }
    }

    private void refreshOverlayGUI(ScreenInfoExtractor.Screen screen,AppLearnProgress progress)
    {
        AppLearnProgress.LabeledScreen oldScreen = progress.findAndSetNewCurrentScreen(screen);
        AppLearnProgress.ScreenLabel currentLabel = AppLearnProgress.ScreenLabel.NOT_LABELED;
        if (oldScreen == null)
        {
            progress.addNewScreenAndMakeCurrent(screen);
        } else
        {
            currentLabel = oldScreen.label;
        }
        useLabelForCurrentScreen(currentLabel);
    }

    public void onAccessibilityEvent(AccessibilityEvent event)
    {
        if (event.getPackageName() != null && lastResult != null)
        {
            String app = event.getPackageName().toString();
            if (!app.equals(lastResult.getTriggerPackage()))
            {
                return;
            }
            ScreenInfoExtractor.Screen screen = ScreenInfoExtractor.extractTextElements(service.getRootInActiveWindow(), false);
            AppLearnProgress progress = this.appIdToLearnProgress.computeIfAbsent(app,k -> new AppLearnProgress());

            switch (event.getEventType())
            {
                /*
                    assert progress != null;
                    Log.d("LEEEEN","Expand");
                    //TODO: expand erweitert irgendwie history
//                    progress.expandCurrentScreen(screen);
                    break;*/
//                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    refreshOverlayGUI(screen,progress);
                    break;
                default:
                    break;
            }
        }
    }

    private boolean programmaticCheckBoxChange = false;

    private void useLabelForCurrentScreen(AppLearnProgress.ScreenLabel label)
    {
        programmaticCheckBoxChange = true;
        switch (label)
        {
            case NOT_LABELED:
                this.checkboxThumpDown.setChecked(false);
                this.checkboxThumpUp.setChecked(false);
                break;
            case GOOD:
                this.checkboxThumpDown.setChecked(false);
                this.checkboxThumpUp.setChecked(true);
                break;
            case BAD:
                this.checkboxThumpDown.setChecked(true);
                this.checkboxThumpUp.setChecked(false);
                break;
        }
        programmaticCheckBoxChange = false;

    }

    private void saveLearnedToDisk()
    {
        for (Map.Entry<String, AppLearnProgress> entry : appIdToLearnProgress.entrySet())
        {
            String appId = entry.getKey();
            if (entry.getValue().isNeedsSaving())
            {
                LearnProgressSaver.save(this.context, appId, entry.getValue());
                entry.getValue().saveToDisk();
            }

        }
    }

    public void onAppChange(String oldApp, String newApp)
    {
        if (overlayButtons != null)
        {
            if (this.iContentFilterService.isPackagedIgnoredForLearning(newApp))
            {
                overlayButtons.setVisibility(View.GONE);
            } else
            {
                overlayButtons.setVisibility(View.VISIBLE);
                // reload UI
                ScreenInfoExtractor.Screen screen = ScreenInfoExtractor.extractTextElements(service.getRootInActiveWindow(), false);
                AppLearnProgress progress = this.appIdToLearnProgress.computeIfAbsent(newApp,k -> new AppLearnProgress());
                this.refreshOverlayGUI(screen,progress);
//                loadLearnProgressFromDisk();
            }
        }
    }

    public void onPipelineResult(@NotNull PipelineResultBase result)
    {
        lastResult = result;
        ScreenInfoExtractor.Screen screen = lastResult.getScreen();


        // update GUI
        lastResult = result;
        if (screen != null)
        {
            if (currentStatus != null)
            {
                boolean overwritten = false;
                // first check if result is in expression the overwrite the
                if (appIdToLearnProgress.containsKey(result.getTriggerPackage()))
                {
                    AppLearnProgress learnProgress = this.appIdToLearnProgress.get(result.getTriggerPackage());
                    assert learnProgress != null;
                    AppLearnProgress.ScreenLabel screenLabel = learnProgress.getLabelFromCalculatedExpression(screen);
                    if (screenLabel != AppLearnProgress.ScreenLabel.NOT_LABELED)
                    {
                        currentStatus.setText(convertLabelTOResultToInfoTest(screenLabel));
                        overwritten = true;
                        updateUIBasedOnCurrentLabel(screenLabel, result.getTriggerPackage(), overwritten);
                    }
                }
                if (!overwritten)
                {
                    currentStatus.setText(convertPiplineResultToInfoText(lastResult));
                    updateUIBasedOnCurrentLabel(AppLearnProgress.ScreenLabel.NOT_LABELED, result.getTriggerPackage(), overwritten);
                }
            }
        }
    }

    private void updateUIBasedOnCurrentLabel(AppLearnProgress.ScreenLabel label, String packageID, boolean overwritten)
    {
        if (currentStatus != null && overlayButtons != null)
        {
            currentStatus.setBackgroundColor(overwritten ? context.getColor(R.color.learner_screen_learned) : context.getColor(R.color.learner_screen_not_learned));
            overlayButtons.setVisibility(this.iContentFilterService.isPackagedIgnoredForLearning(packageID) ? View.INVISIBLE : View.VISIBLE);
        }
    }

    private void showSettingsContextMenu(View view)
    {
        // Create a PopupMenu
        PopupMenu popupMenu = new PopupMenu(this.context, view);
        // Inflate the menu resource
        popupMenu.getMenuInflater().inflate(R.menu.learn_mode_context_menu, popupMenu.getMenu());

        // Set a click listener for menu items
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                if (item.getItemId() == R.id.close)
                {
                    stopOverlay();
                    iContentFilterService.stopLearnMode();
                    return true;
                } else if (item.getItemId() == R.id.help)
                {
                    showHelpDialog();
                    return true;
                } else if (item.getItemId() == R.id.save)
                {
                    saveLearnedToDisk();    //TODO Perhaps
                    saveLearned();
                    return true;
                } else if (item.getItemId() == R.id.clear)
                {
                    clearLearnProgress();
                    return true;
                } else if (item.getItemId() == R.id.swap_sides)
                {
                    swapSiteOfGUI();
                    return true;
                }

                return false;
            }
        });

        // Show the menu
        popupMenu.show();
    }

    private void swapSiteOfGUI()
    {
        this.drawAtLeftEdge = !this.drawAtLeftEdge;
        this.stopOverlay();
        this.createOverlay();
    }

    private void saveLearned()
    {
        String app = this.lastResult.getTriggerPackage();
        if (app != null)
        {
            AppLearnProgress learnProgress = this.appIdToLearnProgress.get(app);
            if (learnProgress != null)
            {
                {
                    Set<String> goodIds = learnProgress.getExpressionGoodIds();
                    UI_ID_Filter oldGoodFilter = (UI_ID_Filter) this.iContentFilterService.getSpecialSmartFilter(app, SpecialSmartFilterBase.Name.LEARNED_GOOD);
                    if (oldGoodFilter == null)
                    {
                        PipelineResultLearnedMode defaultGoodResult = new PipelineResultLearnedMode(app);
                        defaultGoodResult.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
                        defaultGoodResult.setHasExplainableButton(false);
                        UI_ID_Filter newUI_ID_Filter = new UI_ID_Filter(defaultGoodResult, this.context.getString(R.string.name_good_filter), goodIds);
                        this.iContentFilterService.setSpecialSmartFilter(app, SpecialSmartFilterBase.Name.LEARNED_GOOD, newUI_ID_Filter);
                    } else
                    {
                        oldGoodFilter.setFilterIds(goodIds);
                    }
                }
                {
                    Set<String> badIds = learnProgress.getExpressionBadIds();

                    UI_ID_Filter oldBadFilter = (UI_ID_Filter) this.iContentFilterService.getSpecialSmartFilter(app, SpecialSmartFilterBase.Name.LEARNED_BAD);
                    if (oldBadFilter == null)
                    {
                        PipelineResultLearnedMode defaultBadResult = new PipelineResultLearnedMode(app);
                        defaultBadResult.setWindowAction(PipelineWindowAction.PERFORM_BACK_ACTION_AND_WARNING);
                        defaultBadResult.setHasExplainableButton(false);
                        defaultBadResult.setKillState(PipelineResultBase.KillState.KILL_BEFORE_WINDOW);
                        UI_ID_Filter newUI_ID_Filter = new UI_ID_Filter(defaultBadResult, this.context.getString(R.string.name_bad_filter), badIds);
                        this.iContentFilterService.setSpecialSmartFilter(app, SpecialSmartFilterBase.Name.LEARNED_BAD, newUI_ID_Filter);
                    } else
                    {
                        oldBadFilter.setFilterIds(badIds);
                    }
                }
            }
        }
    }


    private void labelCurrentScreen(AppLearnProgress.ScreenLabel label)
    {
        if (this.lastResult != null)
        {
            String app = this.lastResult.getTriggerPackage();
            if (app != null)
            {
                this.appIdToLearnProgress.computeIfAbsent(app,k -> new AppLearnProgress());
                AppLearnProgress learnProgress = this.appIdToLearnProgress.get(app);

                ScreenInfoExtractor.Screen currenScreen = ScreenInfoExtractor.extractTextElements(service.getRootInActiveWindow(), false);
                // current screen was not added then we do it
                if (learnProgress.findAndSetNewCurrentScreen(currenScreen) == null)
                {
                    // add new screen
                    learnProgress.addNewScreenAndMakeCurrent(currenScreen);
                }

                learnProgress.setLabelForCurrentScreen(label);
                learnProgress.recalculateExpressions();
            }
        }
    }

    public String convertLabelTOResultToInfoTest(AppLearnProgress.ScreenLabel label)
    {
        return label == AppLearnProgress.ScreenLabel.GOOD ? context.getString(R.string.learn_mode_label_thumb_up) : context.getString(R.string.learn_mode_label_thump_down);
    }

    public String convertPiplineResultToInfoText(PipelineResultBase result)
    {
        // TODO: UTF chars nehmen ggf. Totenkopf falls KILL_ACTION
        String status = "";
        if(result.getKillState() == PipelineResultBase.KillState.KILLED || result.getKillState() == PipelineResultBase.KillState.KILL_BEFORE_WINDOW)
        {
            status = "💀 + ";
        }

        switch (result.getWindowAction())
        {
            case PERFORM_HOME_BUTTON_AND_WARNING:
                status =  "🏡";
                break;
            case WARNING:
                status = "⚠";
                break;
            case CONTINUE_PIPELINE:
                break;
            case PERFORM_BACK_ACTION:
                status = "⬅";
                break;
            case STOP_FURTHER_PROCESSING:
                status = "⛔";
                break;
            case PERFORM_BACK_ACTION_AND_WARNING:
                status = "⚠ + ⬅";
                break;
            case END_OF_PIPE_LINE:
                status = "🎌";
                break;
        }
        return status;
    }


    public void stopOverlay()
    {
        if (overlayButtons != null)
        {
            windowManager.removeView(overlayButtons);
            overlayButtons = null;
        }
    }

    private void clearLearnProgress()
    {
        if (lastResult != null)
        {
            String app = lastResult.getTriggerPackage();
            if (app != null)
            {
                AppLearnProgress learnProgress = this.appIdToLearnProgress.get(app);
                if (learnProgress != null)
                {
                    learnProgress.clear();
                    saveLearned();
                }
            }
        }
    }

    private void showHelpDialog()
    {
        // First Stop overlay
        this.stopOverlay();

        // Start the DialogActivity
        // We need an static callback. Its a little bit difficlut as we are an background service
        HelpDialogLearnMode.setOnDialogClosedListener(new HelpDialogLearnMode.OnDialogClosedListener()
                                                      {
                                                          @Override
                                                          public void onHelpDialogClosed()
                                                          {
                                                              createOverlay();
                                                          }
                                                      }
        );
        Intent dialogIntent = new Intent(this.context, HelpDialogLearnMode.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Required for starting activity from service
        this.context.startActivity(dialogIntent);

    }

    @Override
    public void onHelpDialogClosed()
    {
        createOverlay();
    }
}
