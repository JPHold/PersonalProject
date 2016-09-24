package com.hjp.mobilesafe.utils;

/**
 * Created by HJP on 2016/9/13 0013.
 */

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * /data/app,放着非官方开发的app
 * /system/app,放着官方开发的app
 * /data/system,放着package.xml和package.list。前着记录app的权限,后者记录app主进程的id和1的标志(代表非官方开发)
 */
public class AppInfoProvider {

    public static String TAG = "AppInfoProvider";

    public interface DataListener {
        public void onData(int myPosition, List<AppInfo> data);

        public void onTrafficData(List<AppInfo> data);
    }

    public static void getAllAppInfos(final Context context, final DataListener dataListener) {

        final List<AppInfo> appInfos = new ArrayList<>();//内存+外存
        final List<AppInfo> systemAppList = new ArrayList<>();//内存的app
        final List<AppInfo> myAppList = new ArrayList<>();//外存的app

        final PackageManager pm = context.getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PackageInfo> pkgs = pm.getInstalledPackages(0);
                for (PackageInfo pi : pkgs) {
                    AppInfo appInfo = new AppInfo();

                    appInfo.pkgName = pi.packageName;//包名

                    appInfo.icon = pi.applicationInfo.loadIcon(pm);

                    appInfo.appName = (String) pi.applicationInfo.loadLabel(pm);

                    String sourceDir = pi.applicationInfo.sourceDir;
                    String size = ObtainFileSize.getFileSize(context, sourceDir);
                    appInfo.setSize(size);

                    /*String publicSourceDir = pi.applicationInfo.publicSourceDir;
                    String size2 = ObtainFileSize.getFileSize(context, publicSourceDir);*/


                    int flags = pi.applicationInfo.flags;

                    if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                        //app装在内存
                        appInfo.setInstaLoc("内存");

                    } else {
                        //app装在sd
                        appInfo.setInstaLoc("sd卡");
                    }

                    if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        //app为非谷歌开发app
                        appInfo.setUserDevelop("自己应用");
//                        Log.i(TAG, "run: "+appInfo.toString());
                        myAppList.add(appInfo);

                    } else {
                        //app为谷歌开发app
                        appInfo.setUserDevelop("系统应用");
                        systemAppList.add(appInfo);
                    }
                }
                appInfos.addAll(myAppList);
                appInfos.addAll(systemAppList);
                dataListener.onData(myAppList.size(), appInfos);
            }
        }).start();

    }

    public static void getTrafficAllAppInfos(final Context context, final DataListener dataListener) {

        final List<AppInfo> appInfos = new ArrayList<>();//内存+外存
        final List<AppInfo> systemAppList = new ArrayList<>();//内存的app
        final List<AppInfo> myAppList = new ArrayList<>();//外存的app

        final PackageManager pm = context.getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<PackageInfo> pkgs = pm.getInstalledPackages(0);
                for (PackageInfo pi : pkgs) {
                    AppInfo appInfo = new AppInfo();

                    appInfo.icon = pi.applicationInfo.loadIcon(pm);

                    appInfo.appName = (String) pi.applicationInfo.loadLabel(pm);

                    String sourceDir = pi.applicationInfo.sourceDir;
                    String size = ObtainFileSize.getFileSize(context, sourceDir);
                    appInfo.setSize(size);

                    int uid = pi.applicationInfo.uid;

                    int flags = pi.applicationInfo.flags;

                    if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        myAppList.add(appInfo);

                    } else {
                        systemAppList.add(appInfo);
                    }

                    appInfo.setUid(uid);

                }
                appInfos.addAll(myAppList);
                appInfos.addAll(systemAppList);

                dataListener.onTrafficData(appInfos);
            }
        }).start();

    }

    public static class AppInfo {

        private Drawable icon;
        private String appName;
        private String pkgName;
        private String size;
        private int uid;//应用的标识

        /**
         * app装在内存
         * app装在sd卡
         */
        public String instaLoc;

        /**
         * 非官方开发
         * 官方开发
         */
        public String isUserDevelop;

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public Drawable getIcon() {
            return icon;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getAppName() {
            return appName;
        }

        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }

        public String getPkgName() {
            return pkgName;
        }

        public void setInstaLoc(String instaLoc) {
            this.instaLoc = instaLoc;
        }

        public String getInstaLoc() {
            return instaLoc;
        }

        public void setUserDevelop(String isUserDevelop) {
            this.isUserDevelop = isUserDevelop;
        }

        public String getUserDevelop() {
            return isUserDevelop;
        }

        public String toString() {
            return appName + "," + pkgName + "," + instaLoc + "," + isUserDevelop;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }
    }
}
