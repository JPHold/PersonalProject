package com.hjp.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hjp.utils.ArtToast;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class OutPhoneCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //得到拨出电话
        String outPhone = getResultData();
        //得知号码所在地和运营商
        String phone_source;
        //土司显示
        ArtToast.showArtToast(context,0,0,"还未实现");

    }
}
