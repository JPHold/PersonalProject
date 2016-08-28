package com.hjp.listener;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.hjp.utils.ArtToast;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class PhoneCallListener extends PhoneStateListener {

    private final Context mContext;

    public PhoneCallListener(Context context) {
        super();
        mContext = context;
    }

    /**
     *
     * @param state 来电的状态：参考TelephonyManager的三个call状态
     * @param incomingNumber 来电号码
     */
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            //来电时
            case TelephonyManager.CALL_STATE_RINGING:
                String phoneSource = null;
                //土司，显示号码所在地和运营商
                ArtToast.showArtToast(mContext, 0, 0, "还未实现");
                break;

            //挂掉电话
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //取消吐司显示
                break;

        }

    }
}
