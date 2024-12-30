package com.example.ourpact3.ui.usage_restriction;

import com.example.ourpact3.R;
import com.example.ourpact3.smart_filter.ProductivityTimeRule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TimeRuleListAdapter extends RecyclerView.Adapter<TimeRuleListAdapter.ViewHolder> {
    private List<ProductivityTimeRule> items; // List of TimeRule objects
    private Context context;

    public TimeRuleListAdapter(Context context) {
        this.items = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_entry_time_rule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductivityTimeRule timeRuleListEntry = items.get(position);
        holder.itemMainText.setText(timeRuleListEntry.getTimeText());
        holder.weekdaysInRule.setText(timeRuleListEntry.getWeekdayText(context)); // Assuming you have this TextView in your layout

        holder.deleteButton.setOnClickListener(v -> {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        });
    }
    public List<ProductivityTimeRule> getAllItems(){return items;}

    public void addTimeRule(ProductivityTimeRule entry)
    {
        items.add(entry);
        notifyItemInserted(items.size() - 1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemMainText;
        TextView weekdaysInRule; // Add this if you have a second TextView for additional info
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemMainText = itemView.findViewById(R.id.item_main_text);
            weekdaysInRule = itemView.findViewById(R.id.weekdays_in_rule); // Initialize this if you have it in your layout
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
