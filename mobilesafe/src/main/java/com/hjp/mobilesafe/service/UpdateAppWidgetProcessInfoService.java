package com.hjp.mobilesafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.broadcastReceiver.ProcessClearAppWidgetProvider;
import com.hjp.mobilesafe.listener.OnCallListener;
import com.hjp.mobilesafe.utils.ComponentState;
import com.hjp.mobilesafe.utils.ProcessInfoProvider;
import com.hjp.mobilesafe.utils.RamSpaceUtil;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HJP on 2016/9/22.
 */

public class UpdateAppWidgetProcessInfoService extends Service {

    private Timer timer;
    private TimerTask timerTask;
    private ProcessUpdateScreenReceiver processScreenReceiver;

    /**
     * 监听屏幕关闭-停止微件更新线程信息(停止服务),监听屏幕开启-开始微件更新线程信息(开启服务)
     */
    private class ProcessUpdateScreenReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Context context1=context;
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                startUpdateProcess();

            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                if (ComponentState.checkServiceState(context, "UpdateAppWidgetProcessInfoService")) {
                    Intent i = new Intent(context, UpdateAppWidgetProcessInfoService.class);
                    stopService(i);
                }
                Log.i("process", "onReceive:ACTION_SCREEN_OFF ");
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //监听屏幕的亮灭
        IntentFilter i = new IntentFilter();
        i.addAction(Intent.ACTION_SCREEN_OFF);
        i.addAction(Intent.ACTION_SCREEN_ON);
        processScreenReceiver = new ProcessUpdateScreenReceiver();
        registerReceiver(processScreenReceiver, i);

     startUpdateProcess();
    }

    private void startUpdateProcess(){
        final AppWidgetManager awm = AppWidgetManager.getInstance(getApplicationContext());
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                final ComponentName provider = new ComponentName(UpdateAppWidgetProcessInfoService.this
                        , ProcessClearAppWidgetProvider.class);
                final RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.appwidget_process);
                ProcessInfoProvider.getProcessCount(getApplicationContext(), new OnCallListener() {
                    @Override
                    public void onCall(View v, String processCount) {
                        remoteViews.setTextViewText(R.id.textV_appWidget_processCount
                                , "进程个数:" + processCount);
                        String ramFreeSpace = RamSpaceUtil.getRamFreeSpace(getApplicationContext());
                        remoteViews.setTextViewText(R.id.textV_appWidget_ramFreeSize, "剩余内存:" + ramFreeSpace);

                        Intent broadCast_clearProcess = new Intent();
                        broadCast_clearProcess.setAction("com.hjp.mobilesafe.clearProcess");

                        //延期意图
                        Log.i("ClearProcess", "onCall: ");
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, broadCast_clearProcess, PendingIntent.FLAG_UPDATE_CURRENT);
                        remoteViews.setOnClickPendingIntent(R.id.btn_appwidget_clearProcess, pendingIntent);
                        awm.updateAppWidget(provider, remoteViews);
                    }
                });

            }
        };
        timer.schedule(timerTask, 0, 2000);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (processScreenReceiver != null) {
            unregisterReceiver(processScreenReceiver);
            processScreenReceiver = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
