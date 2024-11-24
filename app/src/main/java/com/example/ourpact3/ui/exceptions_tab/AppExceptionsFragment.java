package com.example.ourpact3.ui.exceptions_tab;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import com.example.ourpact3.databinding.FragmentDashboardBinding;
import com.example.ourpact3.ui.dialog.AppListDialog;
import com.example.ourpact3.util.PreferencesKeys;
import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.R;
import com.example.ourpact3.util.PackageUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class AppExceptionsFragment extends Fragment
{
    private boolean preventDisabling;
    private RecyclerView recyclerView;
    //    private ListView listView;
    private ExceptionAdapter adapter;
    private TreeSet<String> unaddableApps = new TreeSet<>();
    private Handler handler = new Handler(Looper.getMainLooper()); // load db in background

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_exceptions, container, false);


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //registerForContextMenu(recyclerView);
        final SearchView searchView = view.findViewById(R.id.search_field);

        // Initialize the adapter and set it to the list view
        adapter = new ExceptionAdapter(getActivity(), new ArrayList<>(), (item, position) -> {
            if (item.getItemId() == R.id.menu_delete)
            {
                deleteException(position);
            }
        });
        recyclerView.setAdapter(adapter);

        /*view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showAppListDialog();
            }

        });*/

        // Load data from the database
        loadExceptions();

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



    public void deleteException(int position)
    {
        DatabaseManager dbManger = new DatabaseManager(getContext());
        dbManger.open();
        dbManger.deleteException(adapter.getDBidFromPos(position));
        dbManger.close();
        loadExceptions();
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
                loadExceptions();
            }

            @Override
            public TreeSet<String> getAppsToIgnore() {
                return unaddableApps;
            }
        });
    }

    private void loadExceptions()
    {
        // Create a new thread to load data from the database
        new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PreferencesKeys.MAIN_PREFERENCES, MODE_PRIVATE);
                preventDisabling = sharedPreferences.getBoolean(PreferencesKeys.PREVENT_DISABLING,PreferencesKeys.PREVENT_DISABLING_DEFAULT_VALUE);

                // Get all exceptions from the database
                unaddableApps.clear();
                DatabaseManager dbManger = new DatabaseManager(getContext());
                dbManger.open();
                List<DatabaseManager.ExceptionTuple> exceptions = dbManger.getAllExceptions();
                for (DatabaseManager.ExceptionTuple tuple : exceptions)
                {
                    // We have to set the name as it is needed in search
                    tuple.appName = PackageUtil.getAppName(getContext(), tuple.packageID);
                    unaddableApps.add(tuple.packageID);
                    if(preventDisabling)
                    {
                        tuple.writable = false;
                    }
                }
                dbManger.close();

                // Update the UI on the main thread
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        adapter.setExceptions(exceptions);
                        adapter.notifyDataSetChanged();
                        // Show or hide the FAB button based on preventDisabling
                        final FloatingActionButton fab = getView().findViewById(R.id.fab);
                        if (preventDisabling) {
                            fab.setVisibility(View.INVISIBLE);
                        } else {
                            fab.setVisibility(View.VISIBLE);
                            fab.setOnClickListener(new View.OnClickListener()
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