package com.example.ourpact3.ui.dashboard;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
//import com.example.ourpact3.databinding.FragmentDashboardBinding;
import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.ui.ExceptionAdapter;
import com.example.ourpact3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class AppExceptionsFragment extends Fragment
{
    private ListView listView;
    private ExceptionAdapter adapter;
    private TreeSet<String> unaddableApps = new TreeSet<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exceptions, container, false);

        listView = view.findViewById(R.id.exception_list);

        // Initialize the adapter and set it to the list view
        adapter = new ExceptionAdapter(getActivity(), new ArrayList<>());
        listView.setAdapter(adapter);

        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showAppListDialog();
            }

        });

        // Load data from the database
        loadExceptions();

        return view;
    }

    private void showAppListDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_app_list);

        final ListView listView = dialog.findViewById(R.id.list_view);
        final SearchView searchView = dialog.findViewById(R.id.search_view);

        PackageManager packageManager = getActivity().getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

        final List<String> appNames = new ArrayList<>();
        final List<String> packageNames = new ArrayList<>();

        for (PackageInfo packageInfo : packageInfos) {
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            String packageName = packageInfo.packageName;
            if (!this.unaddableApps.contains(packageName)) {
                appNames.add(appName);
                packageNames.add(packageName);
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, appNames);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: fix background thread problem
                String packageName = packageNames.get(position);
                // Add the selected app to appsShown
                unaddableApps.add(packageName);
                // Add the selected app to the adapter
                DatabaseManager dbManger = new DatabaseManager(getContext());
                dbManger.open();
                dbManger.insertException(packageName,true,true);
                dbManger.close();
                loadExceptions();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void loadExceptions()
    {
        // Get all exceptions from the database
        unaddableApps.clear();
        DatabaseManager dbManger = new DatabaseManager(getContext());
        dbManger.open();
        List<DatabaseManager.ExceptionTuple> exceptions = dbManger.getAllExceptions();
        for(DatabaseManager.ExceptionTuple tuple : exceptions)
        {
            unaddableApps.add(tuple.appName);
        }
        adapter.setExceptions(exceptions);
        adapter.notifyDataSetChanged();
        dbManger.close();
    }
}