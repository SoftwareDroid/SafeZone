package com.example.ourpact3.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.ourpact3.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * How to use:
 * AppListDialog.showAppListDialog(context, packageManager, new AppListDialog.AppListDialogListener() {
 *     @Override
 *     public void onAppSelected(String packageName) {
 *         // Handle app selection
 *     }
 * });
 */
public class AppListDialog {

    public interface AppListDialogListener {
        void onAppSelected(String packageName);
        TreeSet<String> getAppsToIgnore();
    }

    public static Dialog showAppListDialog(Context context, PackageManager packageManager, final AppListDialogListener listener) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_app_list);

        final ListView listView = dialog.findViewById(R.id.list_view);
        final SearchView searchView = dialog.findViewById(R.id.search_view);

        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

        final List<String> appNames = new ArrayList<>();
        final List<String> packageNames = new ArrayList<>();
        TreeSet<String> appsToIgnore = listener.getAppsToIgnore();
        for (PackageInfo packageInfo : packageInfos) {
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            String packageName = packageInfo.packageName;

            if (!appsToIgnore.contains(packageName)) {
                appNames.add(appName);
                packageNames.add(packageName);
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, appNames);
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
                String packageName = packageNames.get(position);
                listener.onAppSelected(packageName);
                dialog.dismiss();
            }
        });

        dialog.show();
        return dialog;
    }
}
