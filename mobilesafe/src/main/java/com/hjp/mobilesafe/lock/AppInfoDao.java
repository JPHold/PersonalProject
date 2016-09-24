package com.hjp.mobilesafe.lock;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

public class AppInfoDao {

    /**
     * 获取手机中所有的app包名集合
     *
     * @param context
     * @return 包名的集合
     */
    public List<String> getAllApps(Context context) {
        List<String> packageNames = new ArrayList<String>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        for (PackageInfo info : infos) {
            String packageName = info.packageName;
            packageNames.add(packageName);
        }
        return packageNames;
    }

}
