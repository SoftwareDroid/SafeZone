package com.example.ourpact3.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import androidx.appcompat.app.AlertDialog;

import com.example.ourpact3.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReusableSettingsCheckboxView<T> {

    private ReusableSettingsItemView item;
    private Context context;
    private String dialogTitle;
    private Map<T, String> mapOfValuesToDisplayStrings;
    private List<T> selectedItems; // To hold selected keys
    private String summaryFormat;

    public ReusableSettingsCheckboxView(Context context, ReusableSettingsItemView item) {
        this.item = item;
        this.context = context;
        item.setOnClickListener(this::showDialog);
        this.selectedItems = new ArrayList<>(); // Initialize the selected items list
    }

    /**
     * @param title
     * @param summaryFormat use "%s" for input as output
     */
    public void setParameters(String title, String summaryFormat, LinkedHashMap<T, String> mapOfValuesToDisplayStrings, List<T> initialSelections) {
        this.summaryFormat = summaryFormat;
        this.dialogTitle = title;
        this.mapOfValuesToDisplayStrings = mapOfValuesToDisplayStrings;
        this.selectedItems = new ArrayList<>(initialSelections); // Set initial selections
        update();
    }

    private void update() {
        // Create a summary string based on selected items
        StringBuilder summary = new StringBuilder();
        for (T key : selectedItems) {
            if (summary.length() > 0) {
                summary.append(", ");
            }
            summary.append(mapOfValuesToDisplayStrings.get(key));
        }
        setSummary(String.format(summaryFormat, summary.toString()));
    }

    private void showDialog(View view) {
        // Prepare the options and selected states
        String[] options = new String[mapOfValuesToDisplayStrings.size()];
        boolean[] selectedOptions = new boolean[mapOfValuesToDisplayStrings.size()];
        int index = 0;

        for (Map.Entry<T, String> entry : mapOfValuesToDisplayStrings.entrySet()) {
            options[index] = entry.getValue();
            selectedOptions[index] = selectedItems.contains(entry.getKey());
            index++;
        }

        // Create the AlertDialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(dialogTitle)
                .setMultiChoiceItems(options, selectedOptions, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        T key = (T) mapOfValuesToDisplayStrings.keySet().toArray()[which];
                        if (isChecked) {
                            selectedItems.add(key);
                        } else {
                            selectedItems.remove(key);
                        }
                    }
                })
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        update(); // Update the summary with selected items
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public List<T> getSelectedItems() {
        return selectedItems;
    }

    public void setTitle(String title) {
        item.setTitle(title);
    }

    public void setSummary(String summary) {
        item.setSummary(summary);
    }
}
