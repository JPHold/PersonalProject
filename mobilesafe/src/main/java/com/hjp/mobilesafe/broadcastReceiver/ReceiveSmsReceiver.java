package com.hjp.mobilesafe.broadcastReceiver;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.hjp.mobilesafe.R;

import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.database.dao.BlackListQueryDao;
import com.hjp.mobilesafe.service.GuardGpsObtainService;
import com.hjp.mobilesafe.utils.AppConfig;

/**
 * Created by HJP on 2016/8/21 0021.
 */

public class ReceiveSmsReceiver extends BroadcastReceiver {

    private static String TAG = "ReceiveSmsReceiver";
    private DevicePolicyManager mDevicePolicyManager;
    private boolean mIsActivation;

    @Override
    public void onReceive(final Context context, Intent intent) {


        AppConfig appConfig = new AppConfig(context);

        //得知是否开了防盗保护
        boolean isOpenGuard = (boolean) AppConfig.obtainFromSqlite(Constant.KEY_ISOPENGUARD);
        //手机没丢时，为了不妨碍正常的信息接收，需要判断SIM卡是否变更
        boolean isSimChange = (boolean) AppConfig.obtainFromSqlite(Constant.KEY_ISSIMCHANGE);
        //SIM卡变更了
        if (isOpenGuard && true) {//测试而已
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object pdu : pdus) {
                SmsMessage fromPdu = SmsMessage.createFromPdu((byte[]) pdu);
                //得知发送者号码
                String sender = fromPdu.getOriginatingAddress();


                //是否在号码黑名单中
                BlackListQueryDao blackListQueryDao = new BlackListQueryDao(context);
                String exist = blackListQueryDao.queryBlackListPartExist(sender);

                //在号码黑名单中，则不用进行号码来源
                if ("短信".equals(exist) || "电话+短信".equals(exist)) {
                    //拦截短信，原生短信并存在自己的数据库里(防止误删)
                    abortBroadcast();
                    return;
                }


                //获取安全号码
                final String safeNum = (String) AppConfig.obtainFromSqlite(Constant.KEY_INTSAFENUM);

                //比较是否相等
                if (sender.equals("5554")) {//测试而已
                    //得知信息
                    String msg = fromPdu.getMessageBody();
                    switch (msg) {
                        case "#location#":
                            //发送地理位置
                            Log.i(TAG, "onReceive:发送地理位置");
                            Intent intent_sendLoc = new Intent(context, GuardGpsObtainService.class);
                            context.startService(intent_sendLoc);
                            break;

                        case "#alarm#":
                            //播放报警音乐
                            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm);
                            mediaPlayer.setVolume(1, 1);//最大声
                            mediaPlayer.setLooping(true);//循环播放
                            mediaPlayer.start();
                            break;
                        //一定要注意下面两个case操作，如果在设置->安全->设备管理器，没有激活app作为设备管理器的话，会报出安全错误
                        case "#destroyData#":
                            //得到设备管理器
                            getDevicePolicyManager(context);
                            //得知是否激活了设备管理器
                            isDevicePolicyActivation(context);
                            if (mIsActivation) {
                                ComponentName component_lockScreen = new ComponentName(context, GuardDeviceAdminReceiver.class);
                                mDevicePolicyManager.wipeData(0);//手机本身存储清空
                                mDevicePolicyManager.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);//sd卡清空
                            }
                            break;
                        case "#lock#":
                            //远程锁屏并且有密码(密码的模式是取决于当前设定，比如图案、普通密码、PIN码、无密码锁屏)
                            //得到设备管理器
                            getDevicePolicyManager(context);
                            //得知是否激活了设备管理器
                            isDevicePolicyActivation(context);
                            if (mIsActivation) {
                                ComponentName component_lockScreen = new ComponentName(context, GuardDeviceAdminReceiver.class);
                                mDevicePolicyManager.setMaximumTimeToLock(component_lockScreen, 0);//多久才锁屏
                                mDevicePolicyManager.setPasswordQuality(component_lockScreen
                                        , DevicePolicyManager.PASSWORD_QUALITY_NUMERIC);//设置密码支持的组合类型(比如数字+字母)
                                mDevicePolicyManager.setMaximumFailedPasswordsForWipe(component_lockScreen, 1);//输错几次锁屏密码后，执行出场设置
                                mDevicePolicyManager.setPasswordMinimumNumeric(component_lockScreen, 11);//设置密码的最小长度，还得做做些混淆？
                                mDevicePolicyManager.lockNow();//锁屏
                            } else {//没有激活，则通知安全号码
                                SmsManager.getDefault().sendTextMessage(safeNum, null, "没有激活设备管理器,无法进行远程操作", null, null);
                            }
                            break;
                    }
                    //变更了则阻止广播继续传播(信息广播是有序广播,原生的短信app是接收不到信息的)(自然偷手机的人也就看不到信息)
                    abortBroadcast();
                }
            }
        }

    }


    /**
     * 获取设备保险管理者
     */
    private void getDevicePolicyManager(Context context) {
        if (mDevicePolicyManager == null) {
            mDevicePolicyManager = (DevicePolicyManager) context.getSystemService(Activity.DEVICE_POLICY_SERVICE);
        }
    }

    /**
     * 检测是否激活了设备管理器组件(也就是给本app权利)
     */
    public boolean isDevicePolicyActivation(Context context) {
        ComponentName componentName = new ComponentName(context, GuardDeviceAdminReceiver.class);
        mIsActivation = mDevicePolicyManager.isAdminActive(componentName);//设备管理器是否激活
        return mIsActivation;
    }

}
