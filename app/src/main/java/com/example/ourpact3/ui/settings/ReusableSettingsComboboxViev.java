package com.example.ourpact3.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

import com.example.ourpact3.R;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReusableSettingsComboboxViev<T>
{

    private ReusableSettingsItemView item;
    private Context context;
    private String dialogTitle;
    private Map<T, String> mapOfValuesToDisplayStrings;
    private T lastSelection;
    private String summaryFormat;

    public ReusableSettingsComboboxViev(Context context, ReusableSettingsItemView item)
    {
        this.item = item;
        this.context = context;
        item.setOnClickListener(this::showDialog);
    }

    /**
     * @param title
     * @param summaryFormat use "%s" for input as output
     */
    public void setParameters(String title, String summaryFormat, LinkedHashMap<T, String> mapOfValuesToDisplayStrings, T intialValue)
    {
        this.summaryFormat = summaryFormat;
        this.dialogTitle = title;
        this.mapOfValuesToDisplayStrings = mapOfValuesToDisplayStrings;
        this.lastSelection = intialValue;
        update();
    }

    private void update()
    {
        setSummary(String.format(summaryFormat, mapOfValuesToDisplayStrings.get(lastSelection)));
    }

    private void showDialog(View view)
    {
        String[] items = this.mapOfValuesToDisplayStrings.values().toArray(new String[0]);
        T[] keys = (T[]) this.mapOfValuesToDisplayStrings.keySet().toArray();
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle(dialogTitle)
                .setItems(items, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        lastSelection = keys[which]; // Use the index to get the key
                        update();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.dismiss();
                    }
                });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public T getLastSelection()
    {
        return lastSelection;
    }

    public void setLastSelection(T v)
    {
        lastSelection = v;
        update();
    }

    public void setTitle(String title)
    {
        item.setTitle(title);
    }

    public void setSummary(String summary)
    {
        item.setSummary(summary);
    }
}

