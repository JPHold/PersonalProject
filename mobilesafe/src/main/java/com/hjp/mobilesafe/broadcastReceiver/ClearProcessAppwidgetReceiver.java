package com.hjp.mobilesafe.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hjp.mobilesafe.utils.KillProcessUtil;

/**
 * Created by HJP on 2016/9/22.
 */

public class ClearProcessAppwidgetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ClearProcess", "onReceive: ");

        if (intent.getAction().equals("com.hjp.mobilesafe.clearProcess")) {
            Log.i("ClearProcess", "onReceive: ");
            KillProcessUtil.killProcess(context);
        }
    }
}
