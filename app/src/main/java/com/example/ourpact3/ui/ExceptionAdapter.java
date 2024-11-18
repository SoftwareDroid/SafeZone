package com.example.ourpact3.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.ourpact3.R;

import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.util.PackageUtil;

import java.util.List;

public class ExceptionAdapter extends BaseAdapter
{

    private List<DatabaseManager.ExceptionTuple> exceptions;
    private Context context;

    public void setExceptions( List<DatabaseManager.ExceptionTuple> exceptions)
    {
        this.exceptions = exceptions;
    }

    public ExceptionAdapter(Context context, List<DatabaseManager.ExceptionTuple> exceptions) {
        this.context = context;
        this.exceptions = exceptions;
    }

    @Override
    public int getCount() {
        return exceptions.size();
    }

    @Override
    public Object getItem(int position) {
        return exceptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.exception_list_item, parent, false);

        DatabaseManager.ExceptionTuple exception = exceptions.get(position);
        view.setBackgroundColor(context.getResources().getColor(exception.writable? R.color.white: R.color.purple_200));
        TextView textView = view.findViewById(R.id.text);

        String fullName = PackageUtil.getAppName(context, exception.appName);
        textView.setText(fullName);

        ImageView imageView = view.findViewById(R.id.icon);
        PackageUtil.getAppIcon(context,exception.appName,imageView);

        return view;
    }


}

