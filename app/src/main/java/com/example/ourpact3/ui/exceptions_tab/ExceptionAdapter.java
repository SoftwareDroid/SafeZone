package com.example.ourpact3.ui.exceptions_tab;

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

import com.example.ourpact3.db.ExceptionListEntity;
import com.example.ourpact3.util.PackageUtil;

import java.util.ArrayList;
import java.util.List;

public class ExceptionAdapter extends RecyclerView.Adapter<ExceptionAdapter.ViewHolder>
{
    public interface OnMenuItemClickListener
    {
        void onMenuItemClick(MenuItem item, int position);
    }

    private List<ExceptionListEntity> exceptions;
    private List<ExceptionListEntity> filteredExceptions;
    private final Context context;
    private final OnMenuItemClickListener listener;

    public String getDBidFromPos(int pos)
    {
        ExceptionListEntity entity = filteredExceptions.get(pos);
        if (entity != null)
        {
            return entity.getPackageName();
        }
        return "";
    }


    public ExceptionAdapter(Context context, List<ExceptionListEntity> exceptions, OnMenuItemClickListener listener)
    {
        this.context = context;
        this.exceptions = exceptions;
        this.filteredExceptions = exceptions;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setExceptions(List<ExceptionListEntity> exceptions)
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
        ExceptionListEntity exception = filteredExceptions.get(position);

        String fullName = PackageUtil.getAppName(context, exception.getPackageName());
        if(exception.getPackageName().equals(fullName))
        {
            fullName +=  " " + context.getString(R.string.app_not_found);
        }
        holder.lockImageView.setVisibility(exception.isWritable() ? View.INVISIBLE : View.VISIBLE);
        holder.textView.setText(fullName);
        PackageUtil.getAppIcon(context, exception.getPackageName(), holder.imageView);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showPopupMenu(v, position, !exception.isWritable());
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
            List<ExceptionListEntity> filtered = new ArrayList<>();

            if (query.isEmpty())
            {
                filtered = exceptions;
            } else
            {
                for (ExceptionListEntity item : exceptions)
                {
                    if (item.getPackageName().toLowerCase().contains(query) || item.getAppName().toLowerCase().contains(query))
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
                filteredExceptions = (List<ExceptionListEntity>) filterResults.values;
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

