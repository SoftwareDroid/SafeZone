package com.example.ourpact3.ui.rules_tab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpact3.R;
import com.example.ourpact3.db.AppEntity;
import com.example.ourpact3.ui.app_rule_detail.AppRuleDetailActivitiy;
import com.example.ourpact3.util.PackageUtil;

import java.util.ArrayList;
import java.util.List;

public class AppRulesAdapter extends RecyclerView.Adapter<AppRulesAdapter.ViewHolder>
{
    public interface OnMenuItemClickListener
    {
        void onMenuItemClick(MenuItem item, int position);
    }

    private List<AppEntity> appRules;
    private List<AppEntity> filteredExceptions;
    private final Context context;
    private final OnMenuItemClickListener listener;


    public AppRulesAdapter(Context context, List<AppEntity> exceptions, OnMenuItemClickListener listener)
    {
        this.context = context;
        this.appRules = exceptions;
        this.filteredExceptions = exceptions;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setAppRules(List<AppEntity> exceptions)
    {
        this.appRules = exceptions;
        this.filteredExceptions = exceptions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.exception_list_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        AppEntity rule = filteredExceptions.get(position);
        String fullName = PackageUtil.getAppName(context, rule.getPackageName());
        if (rule.getPackageName().equals(fullName))
        {
            fullName += " " + context.getString(R.string.app_not_found);
        }
        holder.lockImageView.setVisibility(rule.getWritable() ? View.INVISIBLE : View.VISIBLE);
        holder.textView.setText(fullName);
        PackageUtil.getAppIcon(context, rule.getPackageName(), holder.imageView);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(v -> {
            if(rule.getReadable())
            {
                Intent intent = new Intent(v.getContext(), AppRuleDetailActivitiy.class);
                intent.putExtra("app_id", rule.getPackageName());
                intent.putExtra("app_name", rule.getAppName());
                intent.putExtra("usage_filter_id", rule.getUsageFilterId());
                intent.putExtra("writeable", rule.getWritable());
                v.getContext().startActivity(intent);
            }
//                showPopupMenu(v, position, !rule.writeable);
        });
    }

    private void showPopupMenu(View view, int position, boolean readOnly)
    {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.app_rules_menu);
        // make some entries Read Only
        if (readOnly)
        {
            Menu menu = popupMenu.getMenu();
            MenuItem menuItem = menu.findItem(R.id.menu_delete);
            menuItem.setEnabled(false);
            menuItem.setCheckable(false);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            listener.onMenuItemClick(item, position);
            return true;
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount()
    {
        return filteredExceptions.size();
    }

    public Filter getFilter()
    {
        return new ItemFilter();
    }

    private class ItemFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            String query = constraint.toString().toLowerCase();
            List<AppEntity> filtered = new ArrayList<>();

            if (query.isEmpty())
            {
                filtered = appRules;
            } else
            {
                for (AppEntity item : appRules)
                {
                    if (item.getPackageName().toLowerCase().contains(query) || item.getPackageName().toLowerCase().contains(query))
                    {
                        filtered.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filtered;
            results.count = filtered.size();
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            if (filterResults.count > 0)
            {
                filteredExceptions = (List<AppEntity>) filterResults.values;
                notifyDataSetChanged();
            } else
            {
                filteredExceptions = new ArrayList<>();
                notifyDataSetChanged();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public View view;
        public TextView textView;
        public ImageView imageView;
        public ImageView lockImageView;

        public ViewHolder(View itemView)
        {
            super(itemView);
            view = itemView;
            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.app_icon);
            lockImageView = itemView.findViewById(R.id.lock_icon);
        }
    }
}

