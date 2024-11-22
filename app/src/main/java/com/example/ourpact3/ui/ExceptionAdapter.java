package com.example.ourpact3.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpact3.R;

import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.util.PackageUtil;

import java.util.ArrayList;
import java.util.List;

public class ExceptionAdapter extends RecyclerView.Adapter<ExceptionAdapter.ViewHolder>
{

    private List<DatabaseManager.ExceptionTuple> exceptions;
    private List<DatabaseManager.ExceptionTuple> filteredExceptions;
    private Context context;

    public ExceptionAdapter(Context context, List<DatabaseManager.ExceptionTuple> exceptions)
    {
        this.context = context;
        this.exceptions = exceptions;
        this.filteredExceptions = exceptions;
    }

    public void setExceptions(List<DatabaseManager.ExceptionTuple> exceptions)
    {
        this.exceptions = exceptions;
        this.filteredExceptions = exceptions;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.exception_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        DatabaseManager.ExceptionTuple exception = filteredExceptions.get(position);
        holder.view.setBackgroundColor(context.getResources().getColor(exception.writable ? R.color.white : R.color.purple_200));
        String fullName = PackageUtil.getAppName(context, exception.packageID);
        holder.textView.setText(fullName);
        PackageUtil.getAppIcon(context, exception.packageID, holder.imageView);
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

        public ViewHolder(View itemView)
        {
            super(itemView);
            view = itemView;
            textView = itemView.findViewById(R.id.text);
            imageView = itemView.findViewById(R.id.icon);
        }
    }
}

