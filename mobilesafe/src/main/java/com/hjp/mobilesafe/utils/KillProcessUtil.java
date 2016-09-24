package com.hjp.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.RemoteException;

import java.util.List;

/**
 * Created by HJP on 2016/9/17 0017.
 */

public class KillProcessUtil {

    public interface onCallListener {
        public void onCall();
    }

    public static void killSpecialProcess(Context context, List<String> pkgNameList, onCallListener callListener) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (String pkgName : pkgNameList) {
            am.killBackgroundProcesses(pkgName);
        }
        callListener.onCall();
    }


    public static void killProcess(Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo rapi : processList) {
            if (rapi != null) {
                String pkgName = rapi.processName;//进程名就是包名
                am.killBackgroundProcesses(pkgName);
            }
        }
    }
}
