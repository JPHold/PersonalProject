package com.hjp.mobilesafe.listener;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.SyncStateContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.activity.AdvancedToolActivity;
import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.database.dao.BlackListQueryDao;
import com.hjp.mobilesafe.utils.AppConfig;
import com.hjp.mobilesafe.utils.ArtToast;
import com.hjp.mobilesafe.utils.SQLiteDbManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class PhoneCallListener extends PhoneStateListener {

    private static final String TAG = "PhoneCallListener";
    private final Context mContext;

    public PhoneCallListener(Context context) {
        super();
        mContext = context;
    }

    /**
     * @param state          来电的状态：参考TelephonyManager的三个call状态
     * @param incomingNumber 来电号码
     */
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            //来电,但还没接听
            case TelephonyManager.CALL_STATE_RINGING:

                Log.i(TAG, "onCallStateChanged: CALL_STATE_RINGING");

                //是否在号码黑名单中
                BlackListQueryDao blackListQueryDao = new BlackListQueryDao(mContext);
                String exist = blackListQueryDao.queryBlackListPartExist(incomingNumber);

                //在号码黑名单中，则不用进行号码来源
                if ("电话".equals(exist) || "电话+短信".equals(exist)) {
                    //挂断电话
                    //反射得到ServiceManager
                    try {
                        Class<?> aClass = mContext.getClassLoader().loadClass("android.os.ServiceManager");
                        Method getService = aClass.getMethod("getService", String.class);
                        IBinder b = (IBinder) getService.invoke(null, Activity.TELEPHONY_SERVICE);
                        ITelephony iTelephony = ITelephony.Stub.asInterface(b);
                        iTelephony.endCall();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } finally {
                        //删除来电记录
                        ContentResolver contentResolver = mContext.getContentResolver();
                        contentResolver.delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{incomingNumber});
                    }
                    return;
                }


                String prefix_phone = incomingNumber.substring(0, 7);
                //政府服务电话(110，120等)、商业客服电话(工商客服电话等)、集群电话(移动的集群短号566，599等)、座机电话(020-123456)
                SQLiteDatabase phoneSourceDb = SQLiteDbManager.openDatabase(mContext, AdvancedToolActivity.PHONESOURCEPATH, AdvancedToolActivity.PHONESOURCENAME);
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
                new AppConfig(mContext);
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

                ArtToast.showArtToast(mContext, R.layout.arttoast_phonesource, R.id.artToast_phoneSource,
                        phoneSource, phoneSourceBgColor, R.color.colorDarkCirclePoint);
                break;

            case TelephonyManager.CALL_STATE_IDLE:
                //挂掉电话
                //取消吐司显示号码来源
                Log.i(TAG, "onCallStateChanged: CALL_STATE_IDLE");
                ArtToast.cancelArtToast(mContext);
                break;
            //接听电话,还没用来做啥
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i(TAG, "onCallStateChanged: CALL_STATE_OFFHOOK");
                break;
        }

    }
}
