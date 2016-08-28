package com.hjp.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.hjp.constant.Constant;
import com.hjp.utils.AppConfig;

/**
 * Created by HJP on 2016/8/21 0021.
 */

public class BootStrapReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        AppConfig appConfig = new AppConfig(context);

        //得知是否开了防盗保护
        boolean isOpenGuard = (boolean) AppConfig.obtainFromSqlite(Constant.KEY_ISOPENGUARD);
        if (isOpenGuard) {
            //得到本地保存的sim的唯一标识
            String simUniqueSign_pre = (String) AppConfig.obtainFromSqlite(Constant.KEY_STRING_SIMUNIQUESIGN);

            //得到当前sim卡的唯一标识
            TelephonyManager tlpManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String simUniqueSign_curr = null;
            simUniqueSign_curr = tlpManager.getLine1Number();
            if (simUniqueSign_curr == null || TextUtils.isEmpty(simUniqueSign_curr)) {
                simUniqueSign_curr = tlpManager.getSimSerialNumber();
            }

            //相等则不操作
            if (simUniqueSign_curr.equals(simUniqueSign_pre)) {
                return;
            }
            //不相等，存入Config
            AppConfig.insert2sqlite(Constant.KEY_ISSIMCHANGE,true);
            //不相等，则发短信给安全密码
            int safeNum = (int) AppConfig.obtainFromSqlite(Constant.KEY_INTSAFENUM);
            SmsManager.getDefault().sendTextMessage(String.valueOf(safeNum), null, "SIM已更换,手机被盗", null, null);
        }
    }
}
