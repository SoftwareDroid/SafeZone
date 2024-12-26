package com.example.ourpact3.ui.rules_tab;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ourpact3.R;
import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.ui.dialog.AppListDialog;
import com.example.ourpact3.util.PackageUtil;
import com.example.ourpact3.util.PreferencesKeys;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class AppRulesTabFragment extends Fragment
{
    private boolean preventDisabling;
    private RecyclerView recyclerView;
    //    private ListView listView;
    private AppRulesAdapter adapter;
    private TreeSet<String> unaddableApps = new TreeSet<>();
    private Handler handler = new Handler(Looper.getMainLooper()); // load db in background

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_app_rules, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final SearchView searchView = view.findViewById(R.id.search_field);

        // Initialize the adapter and set it to the list view
        adapter = new AppRulesAdapter(getActivity(), new ArrayList<>(), (item, position) -> {
            if (item.getItemId() == R.id.menu_delete)
            {
                deleteAppRule(position);
            }
        });
        recyclerView.setAdapter(adapter);

        // Load data from the database
        loadAppRules();

        // setup search
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        return view;
    }



    public void deleteAppRule(int position)
    {
        //TODO
        /*
        DatabaseManager dbManger = new DatabaseManager(getContext());
        dbManger.open();
        dbManger.deleteException(adapter.getDBidFromPos(position));
        dbManger.close();
        loadExceptions();*/
    }

    private void showAppListDialog()
    {
        AppListDialog.showAppListDialog(getActivity(), getActivity().getPackageManager(), new AppListDialog.AppListDialogListener() {
            @Override
            public void onAppSelected(String packageName) {
                // Add the selected app to appsShown
                unaddableApps.add(packageName);
                // Add the selected app to the adapter
                DatabaseManager dbManger = new DatabaseManager(getContext());
                dbManger.open();
                dbManger.insertException(packageName, true, true);
                dbManger.close();
                loadAppRules();
            }

            @Override
            public TreeSet<String> getAppsToIgnore() {
                return unaddableApps;
            }
        });
    }

    private void loadAppRules()
    {
        // Create a new thread to load data from the database
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
                preventDisabling = sharedPreferences.getBoolean(PreferencesKeys.PREVENT_DISABLING,PreferencesKeys.PREVENT_DISABLING_DEFAULT_VALUE);

                // Get all Rules from the database
                unaddableApps.clear();
                DatabaseManager dbManger = new DatabaseManager(getContext());
                dbManger.open();
                List<DatabaseManager.AppRuleTuple> rules = dbManger.getAllAppRules();
                for (DatabaseManager.AppRuleTuple tuple : rules)
                {
                    // We have to set the name as it is needed in search
                    tuple.appName = PackageUtil.getAppName(getContext(), tuple.packageID);
                    unaddableApps.add(tuple.packageID);
                    if(preventDisabling)
                    {
                        tuple.writeable = false;
                    }
                }
                dbManger.close();

                // Update the UI on the main thread
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adapter.setAppRules(rules);
                        adapter.notifyDataSetChanged();
                        // Show or hide the FAB button based on preventDisabling
                        final FloatingActionButton floatingActionButton = getView().findViewById(R.id.fab);
                        if (preventDisabling) {
                            floatingActionButton.setVisibility(View.INVISIBLE);
                        } else {
                            floatingActionButton.setVisibility(View.VISIBLE);
                            floatingActionButton.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {
                                    showAppListDialog();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }
}