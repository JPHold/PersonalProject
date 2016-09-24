package com.hjp.mobilesafe.broadcastReceiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hjp.mobilesafe.activity.SplashActivity;
import com.hjp.mobilesafe.service.UpdateAppWidgetProcessInfoService;
import com.hjp.mobilesafe.utils.ComponentState;

/**
 * Created by HJP on 2016/9/22.
 */

public class ProcessClearAppWidgetProvider extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        startProceessUpdate(context);
    }

    public ProcessClearAppWidgetProvider() {
        super();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        startProceessUpdate(context);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        startProceessUpdate(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        startProceessUpdate(context);
        if (ComponentState.checkServiceState(context, "UpdateAppWidgetProcessInfoService")) {
            Intent updateAppWidgetProcessInfos = new Intent(context, UpdateAppWidgetProcessInfoService.class);
            context.stopService(updateAppWidgetProcessInfos);
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        startProceessUpdate(context);
    }

    private void startProceessUpdate(Context context) {
        //定时更新微件-进程信息
        //服务关闭了,重新开启
        if (!ComponentState.checkServiceState(context, "UpdateAppWidgetProcessInfoService")) {
            Intent updateAppWidgetProcessInfos = new Intent(context, UpdateAppWidgetProcessInfoService.class);
            context.startService(updateAppWidgetProcessInfos);
        }
    }
}
