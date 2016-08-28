package com.hjp.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class ComponentState {

    private static ActivityManager mAm;

    /**
     *
     * @param serviceName 服务类的类名，如"ListenPhoneCallService"
     * @return true：服务开启了；false：服务停止了
     */
    //因为在应用管理中，可以关闭服务。当用户手动关闭服务时，app不做处理的话，依然还是显示服务开启中
    public static boolean checkServiceState(Context context, String serviceName) {
        List<ActivityManager.RunningServiceInfo> runningServices = mAm.getRunningServices(100);//取最新的100个服务
        for (ActivityManager.RunningServiceInfo rs : runningServices) {
            if (rs.service.getShortClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    private static void getActivityManager(Context context, String managerName) {
        if (mAm == null) {
            mAm = (ActivityManager) context.getSystemService(managerName);
        }
    }
}
