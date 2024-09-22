package com.example.ourpact3.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

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
}