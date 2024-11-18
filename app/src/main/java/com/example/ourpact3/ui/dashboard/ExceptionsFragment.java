package com.example.ourpact3.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
//import com.example.ourpact3.databinding.FragmentDashboardBinding;
import com.example.ourpact3.db.DatabaseManager;
import com.example.ourpact3.ui.ExceptionAdapter;
import com.example.ourpact3.R;

import java.util.ArrayList;
import java.util.List;

public class ExceptionsFragment extends Fragment {

    private ListView listView;
    private ExceptionAdapter adapter;
//private FragmentDashboardBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exceptions, container, false);

        listView = view.findViewById(R.id.exception_list);

        // Initialize the adapter and set it to the list view
        adapter = new ExceptionAdapter(getActivity(), new ArrayList<>());
        listView.setAdapter(adapter);

        // Load data from the database
        loadExceptions();

        return view;
    }

    private void loadExceptions() {
        // Get all exceptions from the database
        DatabaseManager dbManger = new  DatabaseManager(getContext());
        dbManger.open();
        List<DatabaseManager.ExceptionTuple> exceptions = dbManger.getAllExceptions();
        adapter.setExceptions(exceptions);
        dbManger.close();
        // Update the adapter with the new data
//        adapter.updateExceptions(exceptions);
    }
}