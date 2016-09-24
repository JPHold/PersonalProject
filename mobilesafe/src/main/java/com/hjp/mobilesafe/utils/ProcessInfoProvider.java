package com.hjp.mobilesafe.utils;

/**
 * Created by HJP on 2016/9/13 0013.
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.support.v7.widget.DrawableUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.listener.OnCallListener;

import java.util.ArrayList;
import java.util.List;

/**
 * /data/app,放着非官方开发的app
 * /system/app,放着官方开发的app
 * /data/system,放着package.xml和package.list。前着记录app的权限,后者记录app主进程的id和1的标志(代表非官方开发)
 */
public class ProcessInfoProvider {

    public static String TAG = "ProcessInfoProvider";


    /**
     * 得到进程数
     */
    public static void getProcessCount(Context context, final OnCallListener callListener) {

        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final PackageManager pm = context.getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {
                RunProcessInfo myAppProcessInfo = null;

                List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
                int size = processList.size();
                callListener.onCall(null, String.valueOf(size));
            }
        }).start();
    }

    public interface DataListener {
        public void onData(int myAppPosition, int userPosition, List<RunProcessInfo> userData, List<RunProcessInfo> systemData);

        public void onCount(int count);
    }

    public static void getAllProcessInfos(final Context context, final DataListener dataListener) {

        final List<RunProcessInfo> processnfos = new ArrayList<>();//系统进程+用户进程
        final List<RunProcessInfo> systemProcessList = new ArrayList<>();//系统进程
        final List<RunProcessInfo> myProcessList = new ArrayList<>();//用户进程


        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final PackageManager pm = context.getPackageManager();
        new Thread(new Runnable() {
            @Override
            public void run() {

                RunProcessInfo myAppProcessInfo = null;
                String currAppPkgName = context.getPackageName();

                List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
                int size = processList.size();
                for (int i = 0; i < size; i++) {
                    ActivityManager.RunningAppProcessInfo p = processList.get(i);

                    RunProcessInfo runProcessInfo = new RunProcessInfo();

                    String pkgName = p.processName;//进程名就是包名
                    runProcessInfo.setPkgName(pkgName);
                    PackageInfo pkgInfo = null;
                    try {
                        pkgInfo = pm.getPackageInfo(pkgName, 0);

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        runProcessInfo.setAppName(pkgName);

                        runProcessInfo.setIcon(new BitmapDrawable(context.getResources(),
                                BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)));
                        //进程占运行内存的大小
                        Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{p.uid})[0];
                        long runProcessUseRamByte = memoryInfo.getTotalPrivateDirty() * 1024;
                        String runProcessUseRam = Formatter.formatFileSize(context, runProcessUseRamByte);
                        runProcessInfo.setSize(runProcessUseRam);

                        runProcessInfo.setUserDevelop("系统进程");
                    }
                    if (pkgInfo != null) {

                        //进程所属应用的图标
                        Drawable icon = pkgInfo.applicationInfo.loadIcon(pm);
                        runProcessInfo.setIcon(icon);

                        //进程所属应用的名字
                        String appName = (String) pkgInfo.applicationInfo.loadLabel(pm);
                        runProcessInfo.setAppName(appName);
                        if (currAppPkgName.equals(appName)) {
                            myAppProcessInfo = runProcessInfo;
                        }

                        //进程占运行内存的大小
                        Debug.MemoryInfo memoryInfo = am.getProcessMemoryInfo(new int[]{p.uid})[0];
                        long runProcessUseRamByte = memoryInfo.getTotalPrivateDirty() * 1024;
                        String runProcessUseRam = Formatter.formatFileSize(context, runProcessUseRamByte);
                        runProcessInfo.setSize(runProcessUseRam);

                        //为用户进程还是系统进程
                        int flags = pkgInfo.applicationInfo.flags;

                        if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                            //app为非谷歌开发app
                            runProcessInfo.setUserDevelop("用户进程");
//                        Log.i(TAG, "run: "+appInfo.toString());
                            myProcessList.add(runProcessInfo);

                        } else {
                            //app为谷歌开发app
                            runProcessInfo.setUserDevelop("系统进程");
                            systemProcessList.add(runProcessInfo);
                        }

                        Log.i(TAG, "run: pkgName " + pkgName);
                        Log.i(TAG, "run: appName " + appName);
                        Log.i(TAG, "run: runProcessUseRam " + runProcessUseRam);
                        Log.i(TAG, "run: 类型 " + runProcessInfo.getUserDevelop());
                    }
                }


                //将手机卫士的进程显示在最前面
                if (myAppProcessInfo != null) {
                    int i = myProcessList.indexOf(myAppProcessInfo);//找到手机卫士进程的位置,
                    if (i != 0) {
                        myProcessList.remove(i);//将手机卫士进程放在第一个显示
                        myProcessList.add(0, myAppProcessInfo);
                    }
                }

                dataListener.onData(0, myProcessList.size(), myProcessList, systemProcessList);
                dataListener.onCount(myProcessList.size() + systemProcessList.size());
            }
        }).start();

    }

    public static class RunProcessInfo {

        /**
         * 进程所属的应用图标
         */
        private Drawable icon;
        /**
         * 进程所属的应用的包名
         */
        private String pkgName;
        /**
         * 进程所属的应用名
         */
        private String appName;
        /**
         * 进程占的大小
         */
        private String size;

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

        public void setUserDevelop(String isUserDevelop) {
            this.isUserDevelop = isUserDevelop;
        }

        public String getUserDevelop() {
            return isUserDevelop;
        }

        public String toString() {
            return appName + "," + isUserDevelop + " , " + size;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getPkgName() {
            return pkgName;
        }

        public void setPkgName(String pkgName) {
            this.pkgName = pkgName;
        }
    }
}
