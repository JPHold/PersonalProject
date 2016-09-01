package com.hjp.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.hjp.mobilesafe.broadcastReceiver.OutPhoneCallReceiver;
import com.hjp.mobilesafe.listener.PhoneCallListener;

/**
 * Created by HJP on 2016/8/25 0025.
 */

/**
 * 在设置中心开启/关闭这个服务，要通过ComponentState判断服务是否被停止
 */
public class ListenPhoneCallService extends Service {
    private TelephonyManager tm;
    private PhoneCallListener mCallListener;
    private OutPhoneCallReceiver mOutPhoneCallReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mCallListener = new PhoneCallListener(getApplicationContext());
        //监听来电
        tm.listen(mCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        //接收去电广播
        IntentFilter intentFilter_sendOutPhoneCall = new IntentFilter();
        intentFilter_sendOutPhoneCall.addAction("android.intent.action.NEW_OUTGOING_CALL");
        mOutPhoneCallReceiver = new OutPhoneCallReceiver();
        registerReceiver(mOutPhoneCallReceiver, intentFilter_sendOutPhoneCall);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(mCallListener, PhoneStateListener.LISTEN_NONE);
        mCallListener = null;

        unregisterReceiver(mOutPhoneCallReceiver);
        mOutPhoneCallReceiver = null;

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
