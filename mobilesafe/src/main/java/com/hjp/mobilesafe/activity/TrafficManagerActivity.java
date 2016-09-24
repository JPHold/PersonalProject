package com.hjp.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ListView;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.adapter.TrafficAdapter;
import com.hjp.mobilesafe.utils.AppInfoProvider;
import com.hjp.mobilesafe.utils.SendInModuleUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HJP on 2016/9/23.
 */

public class TrafficManagerActivity extends Activity {

    private Context mContext;
    private ListView listV_showTraffic;
    private TrafficAdapter trafficAdapter;
    private List<AppInfoProvider.AppInfo> trafficData;
    private TimerTask updateTrafficTask;
    private Timer updateTrafficTimer;
    private String TAG = "TrafficManagerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trafficmanager);

        initView();
        //2.2版本以上提供了流量统计的api接口
        TrafficStats.getMobileRxBytes();//手机(移动数据)下载的流量的总和
        TrafficStats.getMobileTxBytes();//手机(移动数据)下载总和
        TrafficStats.getTotalRxBytes();//下载的总和
        TrafficStats.getTotalTxBytes();//上传的总和

    }

    private void initView() {
        listV_showTraffic = (ListView) findViewById(R.id.listV_showTraffic);
        mContext = getApplicationContext();
        //获取应用的流量情况
        AppInfoProvider.getTrafficAllAppInfos(mContext, new AppInfoProvider.DataListener() {
            @Override
            public void onData(int myPosition, List<AppInfoProvider.AppInfo> data) {

            }

            @Override
            public void onTrafficData(List<AppInfoProvider.AppInfo> data) {
                trafficData = data;
                SendInModuleUtil.sendMsgByHandler(1, null, handler);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int what = msg.what;
            switch (what) {
                case 1:
                    trafficAdapter = new TrafficAdapter(mContext, trafficData);
                    //根据uid，获取应用的下载流量和上传流量
                    listV_showTraffic.setAdapter(trafficAdapter);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SendInModuleUtil.sendMsgByHandler(2, null, handler);
                        }
                    }).start();
                    break;
                case 2://开启定时更新流量使用情况
                    startUpdateTraffic();
                    break;
                case 3:
                    trafficAdapter.chanageTraffics();
                    Log.i(TAG, "handleMessage: 3");
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startUpdateTraffic();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUpdateTraffic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopUpdateTraffic();
    }

    private void stopUpdateTraffic() {
        if (updateTrafficTask != null) {
            updateTrafficTask.cancel();
            updateTrafficTask = null;
        }

        if (updateTrafficTimer == null) {
            updateTrafficTimer.cancel();
            updateTrafficTimer = null;
        }
    }

    private void startUpdateTraffic() {
        //开启更新流量使用情况的定时器

        if (updateTrafficTask == null) {
            updateTrafficTask = new TimerTask() {
                @Override
                public void run() {
                    SendInModuleUtil.sendMsgByHandler(3, null, handler);
                }
            };
        }

        if (updateTrafficTimer == null) {
            updateTrafficTimer = new Timer();
        }
        updateTrafficTimer.schedule(updateTrafficTask, 2000, 3000);
    }

}
