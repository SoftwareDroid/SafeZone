package com.example.ourpact3.learn_mode;


import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.ourpact3.R;
import com.example.ourpact3.model.PipelineResultBase;
import com.example.ourpact3.model.PipelineResultKeywordFilter;
import com.example.ourpact3.model.PipelineWindowAction;
import com.example.ourpact3.service.IContentFilterService;
import com.example.ourpact3.service.ScreenInfoExtractor;
import com.example.ourpact3.smart_filter.SpecialSmartFilterBase;
import com.example.ourpact3.smart_filter.UI_ID_Filter;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
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

    public LearnModeComponent(@NotNull Context context, @NotNull IContentFilterService iContentFilterService)
    {
        if (!Settings.canDrawOverlays(context))
        {
            throw new RuntimeException("Need draw overlay Permission");
        }
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.context = context;
        this.iContentFilterService = iContentFilterService;
    }

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

//        params.gravity = Gravity.END | Gravity.TOP; // Position the overlay
        params.gravity = (drawAtLeftEdge ? Gravity.START : Gravity.END) | Gravity.CLIP_VERTICAL; // Position the overlay
        params.x = 0;
        params.y = 0; // No need to adjust y, as it's centered vertically

        // Add the view to the window
        windowManager.addView(overlayButtons, params);

        // Set up button click listeners
        Button buttonThumpUp = overlayButtons.findViewById(R.id.thumb_up);
        Button buttonThumpDown = overlayButtons.findViewById(R.id.thumb_down);
        Button buttonSettings = overlayButtons.findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(this::showSettingsContextMenu);

        currentStatus = overlayButtons.findViewById(R.id.current_status);

        buttonThumpUp.setOnClickListener(v -> {
            this.labelCurrentScreen(AppLearnProgress.ScreenLabel.GOOD, false);
        });
        buttonThumpUp.setOnLongClickListener(v -> {
            this.labelCurrentScreen(AppLearnProgress.ScreenLabel.GOOD, true);
            return true;
        });

        buttonThumpDown.setOnClickListener(v -> {
            this.labelCurrentScreen(AppLearnProgress.ScreenLabel.BAD, false);
        });
        buttonThumpDown.setOnLongClickListener(v -> {
            this.labelCurrentScreen(AppLearnProgress.ScreenLabel.BAD, true);
            return true;
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

    public void onAppChange(String oldApp, String newApp)
    {
        Button buttonThumpUp = overlayButtons.findViewById(R.id.thumb_up);
        Button buttonThumpDown = overlayButtons.findViewById(R.id.thumb_down);
        assert buttonThumpDown != null;
        assert buttonThumpUp != null;
        if (this.iContentFilterService.isPackagedIgnoredForLearning(newApp))
        {
            overlayButtons.setVisibility(View.GONE);
        } else
        {
            overlayButtons.setVisibility(View.VISIBLE);
        }
    }

    public void onPipelineResult(@NotNull PipelineResultBase result)
    {
        // update GUI
        lastResult = result;
        ScreenInfoExtractor.Screen screen = lastResult.getScreen();
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
        assert currentStatus != null;
        currentStatus.setBackgroundColor(overwritten ? context.getColor(R.color.learner_screen_learned) : context.getColor(R.color.learner_screen_not_learned));
        Button buttonThumpUp = overlayButtons.findViewById(R.id.thumb_up);
        Button buttonThumpDown = overlayButtons.findViewById(R.id.thumb_down);
        assert buttonThumpDown != null;
        assert buttonThumpUp != null;
        if (this.iContentFilterService.isPackagedIgnoredForLearning(packageID))
        {
            buttonThumpUp.setVisibility(View.INVISIBLE);
            buttonThumpDown.setVisibility(View.INVISIBLE);
            return;
        }
        switch (label)
        {
            case GOOD:
                buttonThumpUp.setVisibility(View.INVISIBLE);
                buttonThumpDown.setVisibility(View.VISIBLE);
                break;
            case BAD:
                buttonThumpUp.setVisibility(View.VISIBLE);
                buttonThumpDown.setVisibility(View.INVISIBLE);
                break;
            case NOT_LABELED:
                buttonThumpUp.setVisibility(View.VISIBLE);
                buttonThumpDown.setVisibility(View.VISIBLE);
                break;
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
                    saveLearned();
                }

                return false;
            }
        });

        // Show the menu
        popupMenu.show();
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
                    if(oldGoodFilter == null)
                    {
                        PipelineResultKeywordFilter defaultGoodResult = new PipelineResultKeywordFilter(app);
                        defaultGoodResult.setWindowAction(PipelineWindowAction.STOP_FURTHER_PROCESSING);
                        defaultGoodResult.setHasExplainableButton(false);
                        UI_ID_Filter newUI_ID_Filter = new UI_ID_Filter(defaultGoodResult, this.context.getString(R.string.name_good_filter), goodIds);
                        this.iContentFilterService.setSpecialSmartFilter(app, SpecialSmartFilterBase.Name.LEARNED_BAD, newUI_ID_Filter);
                    }
                    else
                    {
                        oldGoodFilter.setFilterIds(goodIds);
                    }
                }
                {
                    Set<String> badIds = learnProgress.getExpressionGoodIds();

                    UI_ID_Filter oldBadFilter = (UI_ID_Filter) this.iContentFilterService.getSpecialSmartFilter(app, SpecialSmartFilterBase.Name.LEARNED_BAD);
                    if (oldBadFilter == null)
                    {
                        PipelineResultKeywordFilter defaultBadResult = new PipelineResultKeywordFilter(app);
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
                //                this.iContentFilterService.setSpecialSmartFilter(app, SpecialSmartFilterBase.Name.LEARNED_GOOD,goodIds);
            }
        }
    }


    private void labelCurrentScreen(AppLearnProgress.ScreenLabel label, boolean force)
    {
        if (this.lastResult != null)
        {
            String app = this.lastResult.getTriggerPackage();
            if (app != null)
            {
                if (!this.appIdToLearnProgress.containsKey(app))
                {
                    appIdToLearnProgress.put(app, new AppLearnProgress());
                }
                AppLearnProgress learnProgress = this.appIdToLearnProgress.get(app);
                // if a screen already there we remove it. so that it is not in any class and we can relabel it.
                ScreenInfoExtractor.Screen screen = lastResult.getScreen();
                assert learnProgress != null;
                // Check if we already the same screen
                AppLearnProgress.LabeledScreen labeledScreen = learnProgress.getLabeledScreen(screen);
                AppLearnProgress.ScreenLabel newCurrentScreenlabel = label;
                if (!force)
                {
                    if (labeledScreen != null)
                    {
                        learnProgress.removeScreen(lastResult.getScreen());
                        newCurrentScreenlabel = AppLearnProgress.ScreenLabel.NOT_LABELED; // go e.g from Up -> NOT_LABELED and with next click to
                    } else
                    {
                        learnProgress.addScreen(lastResult.getScreen(), label);
                    }
                } else
                {
                    learnProgress.removeScreen(lastResult.getScreen());
                    learnProgress.addScreen(lastResult.getScreen(), label);
                }
                learnProgress.recalculateExpressions();
                // Wait for the next pipeline Update
                this.updateUIBasedOnCurrentLabel(newCurrentScreenlabel, app, newCurrentScreenlabel != AppLearnProgress.ScreenLabel.NOT_LABELED);
            }
        }
    }

    public String convertLabelTOResultToInfoTest(AppLearnProgress.ScreenLabel label)
    {
        return label == AppLearnProgress.ScreenLabel.GOOD ? "GOOD" : "BAD";
    }

    public String convertPiplineResultToInfoText(PipelineResultBase result)
    {
        // TODO: UTF chars nehmen ggf. Totenkopf falls KILL_ACTION
        String status = "";
        switch (result.getWindowAction())
        {
            case PERFORM_HOME_BUTTON_AND_WARNING:
                status = "HOME";
                break;
            case WARNING:
                status = "WARN";
                break;
            case CONTINUE_PIPELINE:
                break;
            case PERFORM_BACK_ACTION:
                status = "BACK";
                break;
            case STOP_FURTHER_PROCESSING:
                status = "STOP";
                break;
            case PERFORM_BACK_ACTION_AND_WARNING:
                status = "WARN2";
                break;
            case END_OF_PIPE_LINE:
                status = "OMIT";
                break;
        }
        return String.format("[%s]", status);
    }


    public void stopOverlay()
    {
        if (overlayButtons != null)
        {
            windowManager.removeView(overlayButtons);
            overlayButtons = null;
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
