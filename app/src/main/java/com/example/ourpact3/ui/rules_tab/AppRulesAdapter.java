package com.example.ourpact3.ui.rules_tab;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.example.ourpact3.util.PackageUtil;

import java.util.ArrayList;
import java.util.List;

public class AppRulesAdapter extends RecyclerView.Adapter<AppRulesAdapter.ViewHolder>
{
    public interface OnMenuItemClickListener
    {
        void onMenuItemClick(MenuItem item, int position);
    }

    private List<DatabaseManager.ExceptionTuple> exceptions;
    private List<DatabaseManager.ExceptionTuple> filteredExceptions;
    private Context context;
    private OnMenuItemClickListener listener;

    public String getDBidFromPos(int pos)
    {
        DatabaseManager.ExceptionTuple t = filteredExceptions.get(pos);
        if (t != null)
        {
            return t.packageID;
        }
        return "";
    }


    public AppRulesAdapter(Context context, List<DatabaseManager.ExceptionTuple> exceptions, OnMenuItemClickListener listener)
    {
        this.context = context;
        this.exceptions = exceptions;
        this.filteredExceptions = exceptions;
        this.listener = listener;
    }

    public void setExceptions(List<DatabaseManager.ExceptionTuple> exceptions)
    {
        this.exceptions = exceptions;
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
        DatabaseManager.ExceptionTuple exception = filteredExceptions.get(position);

        String fullName = PackageUtil.getAppName(context, exception.packageID);
        if(exception.packageID.equals(fullName))
        {
            fullName +=  " " + context.getString(R.string.app_not_found);
        }
        holder.lockImageView.setVisibility(exception.writable ? View.INVISIBLE : View.VISIBLE);
        holder.textView.setText(fullName);
        PackageUtil.getAppIcon(context, exception.packageID, holder.imageView);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showPopupMenu(v, position, !exception.writable);
            }
        });
    }

    private void showPopupMenu(View view, int position, boolean readOnly)
    {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.exception_menu);
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
            List<DatabaseManager.ExceptionTuple> filtered = new ArrayList<>();

            if (query.isEmpty())
            {
                filtered = exceptions;
            } else
            {
                for (DatabaseManager.ExceptionTuple item : exceptions)
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
                filteredExceptions = (List<DatabaseManager.ExceptionTuple>) filterResults.values;
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

