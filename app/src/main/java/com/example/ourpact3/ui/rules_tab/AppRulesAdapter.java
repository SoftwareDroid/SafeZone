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
import com.example.ourpact3.db.DatabaseManager;
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

    private List<DatabaseManager.AppRuleTuple> appRules;
    private List<DatabaseManager.AppRuleTuple> filteredExceptions;
    private Context context;
    private OnMenuItemClickListener listener;

    public String getDBidFromPos(int pos)
    {
        DatabaseManager.AppRuleTuple t = filteredExceptions.get(pos);
        if (t != null)
        {
            return t.packageID;
        }
        return "";
    }


    public AppRulesAdapter(Context context, List<DatabaseManager.AppRuleTuple> exceptions, OnMenuItemClickListener listener)
    {
        this.context = context;
        this.appRules = exceptions;
        this.filteredExceptions = exceptions;
        this.listener = listener;
    }

    public void setAppRules(List<DatabaseManager.AppRuleTuple> exceptions)
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
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        DatabaseManager.AppRuleTuple rule = filteredExceptions.get(position);
        String fullName = PackageUtil.getAppName(context, rule.packageID);
        if (rule.packageID.equals(fullName))
        {
            fullName += " " + context.getString(R.string.app_not_found);
        }
        holder.lockImageView.setVisibility(rule.writeable ? View.INVISIBLE : View.VISIBLE);
        holder.textView.setText(fullName);
        PackageUtil.getAppIcon(context, rule.packageID, holder.imageView);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(rule.readable)
                {
                    Intent intent = new Intent(v.getContext(), AppRuleDetailActivitiy.class);
                    intent.putExtra("app_id", rule.packageID);
                    intent.putExtra("app_name", rule.appName);
                    intent.putExtra("usage_filter_id", rule.usageFilterID);
                    v.getContext().startActivity(intent);
                }
//                showPopupMenu(v, position, !rule.writeable);
            }
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
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                listener.onMenuItemClick(item, position);
                return true;
            }
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
            List<DatabaseManager.AppRuleTuple> filtered = new ArrayList<>();

            if (query.isEmpty())
            {
                filtered = appRules;
            } else
            {
                for (DatabaseManager.AppRuleTuple item : appRules)
                {
                    if (item.packageID.toLowerCase().contains(query) || item.appName.toLowerCase().contains(query))
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

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults)
        {
            if (filterResults.count > 0)
            {
                filteredExceptions = (List<DatabaseManager.AppRuleTuple>) filterResults.values;
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

