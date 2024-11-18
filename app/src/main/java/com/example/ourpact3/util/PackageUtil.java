package com.example.ourpact3.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import com.example.ourpact3.R;
public class PackageUtil
{
    public static String getAppName(Context ctx,String packageName)
    {
        try
        {
            PackageManager packageManager = ctx.getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            return (String) packageManager.getApplicationLabel(appInfo);
        } catch (Exception e)
        {
            return packageName;
        }
    }
    public static void getAppIcon(Context ctx,String packageName, ImageView view)
    {
        // Get the package manager
        PackageManager packageManager = ctx.getPackageManager();

        // Get the icon for the app
        try {
            Drawable icon = packageManager.getApplicationIcon(packageName);
            view.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            // Handle the case where the app is not found
            view.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }
}