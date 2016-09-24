package com.hjp.mobilesafe.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by HJP on 2016/9/15 0015.
 */

public class AppControl {

    private static PackageManager mPackageManager;

    public static void startApp(Context context, String pkgName) {

        if (mPackageManager == null) {
            mPackageManager = context.getPackageManager();
        }

        //这种方式:如果遇到manifest文件中的主入口写在最后面,就启动不了真正的入口,而且可能出错
        /*try {
            PackageInfo packageInfo = mPackageManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = packageInfo.activities;
            if (activities == null || activities.length <= 0) {
                SendInModuleUtil.showToast(context, "此app无启动界面");
            } else {
                ActivityInfo activity = activities[0];
                String mainActivityLoc = activity.name;//得到入口Activity的全类名路径
                Intent intent = new Intent();
                intent.setClassName(pkgName, mainActivityLoc);
                context.startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }*/


        //第二种方式:避免了这种错误
        Intent i = mPackageManager.getLaunchIntentForPackage(pkgName);
        context.startActivity(i);
    }
}

