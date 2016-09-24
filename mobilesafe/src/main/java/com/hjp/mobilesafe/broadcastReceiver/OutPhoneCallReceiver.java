package com.hjp.mobilesafe.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.activity.AdvancedToolActivity;
import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.utils.AppConfig;
import com.hjp.mobilesafe.utils.ArtToast;
import com.hjp.mobilesafe.utils.SQLiteDbManager;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class OutPhoneCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //得到拨出电话
        String outPhoneNumber = getResultData();
        String prefix_phone = outPhoneNumber.substring(0, 7);
        //政府服务电话(110，120等)、商业客服电话(工商客服电话等)、集群电话(移动的集群短号566，599等)、座机电话(020-123456)
        SQLiteDatabase phoneSourceDb = SQLiteDbManager.openDatabase(context, AdvancedToolActivity.PHONESOURCEPATH, AdvancedToolActivity.PHONESOURCENAME);
        Cursor phoneSourceCursor = phoneSourceDb.query("phonesource", new String[]{AdvancedToolActivity.PHONESOURCE_KEY_CITY, AdvancedToolActivity.PHONESOURCE_KEY_CARDTYPE}
                , AdvancedToolActivity.PHONESOURCE_KEY_PREFIX + "=? ", new String[]{prefix_phone}, null, null, null);

        String phoneSource = null;
        if (phoneSourceCursor.moveToNext()) {
            String city = phoneSourceCursor.getString(phoneSourceCursor.getColumnIndex(AdvancedToolActivity.PHONESOURCE_KEY_CITY));
            String cardType = phoneSourceCursor.getString(phoneSourceCursor.getColumnIndex(AdvancedToolActivity.PHONESOURCE_KEY_CARDTYPE));
            phoneSource = city + "  " + cardType;
        }
        if (TextUtils.isEmpty(phoneSource)) {
            phoneSource = "暂无此号码的来源";
        }
        //土司，显示号码所在地和运营商
        //获取：设置保存的号码来源颜色
        new AppConfig(context);
        String phoneSourceBgColorName = (String) AppConfig.obtainFromSqlite(Constant.KEY_STRINGPHONESOURCECOLOR);

        int phoneSourceBgColor;
        //得到号码来源的颜色值
        switch (phoneSourceBgColorName) {
            case "红色":
                phoneSourceBgColor = R.color.pop_phoneSource_color1;
                break;
            case "黄色":
                phoneSourceBgColor = R.color.pop_phoneSource_color2;
                break;
            case "绿色":
                phoneSourceBgColor = R.color.pop_phoneSource_color3;
                break;
            case "蓝色":
                phoneSourceBgColor = R.color.pop_phoneSource_color4;
                break;
            case "黑色":
                phoneSourceBgColor = R.color.pop_phoneSource_color5;
                break;
            default:
                phoneSourceBgColor = R.color.pop_phoneSource_color3;
                break;
        }

        ArtToast.showArtToast(context, R.layout.arttoast_phonesource, R.id.artToast_phoneSource,
                phoneSource, phoneSourceBgColor, R.color.colorDarkCirclePoint);
    }
}
