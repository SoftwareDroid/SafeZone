package com.example.ourpact3.ui.content_restriction_settings_for_app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpact3.R;
import com.example.ourpact3.smart_filter.ContentSmartFilterBase;

import java.util.ArrayList;
import java.util.List;

public class ContentFilterRuleAdapter extends RecyclerView.Adapter<ContentFilterRuleAdapter.ViewHolder>
{
    private List<ContentSmartFilterBase> items;
    private Context context;
    private int selectedItemPosition = RecyclerView.NO_POSITION; // No selection initially

    public ContentFilterRuleAdapter(Context context)
    {
        this.items = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.list_entry_content_filter_rule, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.items = items;   // give the view access
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position)
    {
        ContentSmartFilterBase rule = items.get(position);
        holder.isEnabledSwitch.setChecked(rule.isEnabled());
        holder.title.setText(rule.getName());
        holder.shortDescription.setText(rule.getShortDescription());
        // Highlight selected item
        holder.itemView.setBackgroundColor(position == selectedItemPosition ? context.getColor(R.color.white)  : context.getColor(R.color.gray));
        // Set the OnCheckedChangeListener for the Switch
        holder.isEnabledSwitch.setOnCheckedChangeListener(null); // Clear previous listener to avoid unwanted triggers
        holder.isEnabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update the isEnabled attribute of the rule
            rule.setEnabled(isChecked);
        });
        ActionModeCallback myActionMode = new ActionModeCallback();
        // Set the OnLongClickListener to start Contextual Action Mode
        holder.itemView.setOnLongClickListener(v -> {
            selectedItemPosition = position; // Select the item
            ((AppCompatActivity) context).startSupportActionMode(myActionMode);
            notifyDataSetChanged();
            return true;
        });

    }

    public List<ContentSmartFilterBase> getAllItems()
    {
        return items;
    }

    public void addEntry(ContentSmartFilterBase entry)
    {
        items.add(entry);
        notifyItemInserted(items.size() - 1);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView title;
        TextView shortDescription;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch isEnabledSwitch;
        List<ContentSmartFilterBase> items;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            title = itemView.findViewById(R.id.item_main_text);
            shortDescription = itemView.findViewById(R.id.content_filter_short_description);
            isEnabledSwitch = itemView.findViewById(R.id.is_enabled);
        }
    }


    private class ActionModeCallback implements ActionMode.Callback
    {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            mode.getMenuInflater().inflate(R.menu.context_menu_context_filter, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
//            mode.setTitle("1 item selected");
            return false;
        }

        private void moveSelectedItemUp()
        {
            if (selectedItemPosition > 0)
            {
                // Get the item to move
                ContentSmartFilterBase item = items.get(selectedItemPosition);

                // Remove the item from its current position
                items.remove(selectedItemPosition);

                // Insert it one position up
                items.add(selectedItemPosition - 1, item);
                notifyItemMoved(selectedItemPosition, selectedItemPosition - 1);
                // Update the selected position
                selectedItemPosition--;

            }
        }

        private void moveSelectedItemDown()
        {
            if (selectedItemPosition < items.size() - 1)
            {
                // Get the item to move
                ContentSmartFilterBase item = items.get(selectedItemPosition);

                // Remove the item from its current position
                items.remove(selectedItemPosition);

                // Insert it one position down
                items.add(selectedItemPosition + 1, item);

                // Update the selected position
                notifyItemMoved(selectedItemPosition, selectedItemPosition + 1);

                selectedItemPosition++;
            }
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            if (item.getItemId() == R.id.action_delete)
            {
                //deleteSelectedItem();
                mode.finish(); // Close the action mode
                return true;
            } else if (item.getItemId() == R.id.action_open)
            {
//                openSelectedItem();
                mode.finish(); // Close the action mode
                return true;
            } else if (item.getItemId() == R.id.action_move_up)
            {
                moveSelectedItemUp();
                mode.finish(); // Close the action mode
                return true;
            } else if (item.getItemId() == R.id.action_move_down)
            {
                moveSelectedItemDown();
                mode.finish(); // Close the action mode
                return true;
            } else if (item.getItemId() == R.id.action_words)
            {
//                showWordsForSelectedItem();
                mode.finish(); // Close the action mode
                return true;
            }
            return false; // Return false if no action was handled
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            selectedItemPosition = RecyclerView.NO_POSITION;
            notifyDataSetChanged();
        }
    }


}
