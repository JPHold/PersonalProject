package com.hjp.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.customview.SingleLineSelectLayout;
import com.hjp.mobilesafe.listener.OnCallListener;
import com.hjp.mobilesafe.listener.PhoneCallListener;
import com.hjp.mobilesafe.service.ListenPhoneCallService;
import com.hjp.mobilesafe.utils.AppConfig;
import com.hjp.mobilesafe.utils.ComponentState;
import com.hjp.mobilesafe.utils.ScreenUtils;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class SettingActivity extends Activity implements OnCallListener, View.OnClickListener {

    private static String TAG = "SettingActivity";
    private Context mContext;
    private Resources mResources;

    private ScrollView mSettingActivity_rootLayout;
    private SingleLineSelectLayout mSingleLSLayout_phoneCall;
    private View mLinearL_phoneCall_secondSetting_rootLayout;

    /**
     * 当前的来电号码的提示颜色名
     */
    private String currColor_phoneSource = "未设置";
    /**
     * 记录选择的color-view的id，用于：如果这次选的颜色跟上次一样,那么不用重新获取颜色资源和insert-sqlite
     */
    private int mCurrId_phoneSource_colorView;
    private PopupWindow mPpw_phoneSource_color_choose;
    private SingleLineSelectLayout mSecondSetting_phoneCall_phoneSource_showStyle;
    private SingleLineSelectLayout mSecondSetting_phoneCall;
    private Intent mIntent_startPhoneCallListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        mContext = getApplicationContext();
        mSettingActivity_rootLayout = (ScrollView) findViewById(R.id.settingActivity_rootLayout);
        //来电显示的设置
        mSingleLSLayout_phoneCall = (SingleLineSelectLayout) findViewById(R.id.setting_singleLSLayout_phoneCall);
        mSingleLSLayout_phoneCall.setSecondSettingState(true);//添加箭头设置指向
        mSingleLSLayout_phoneCall.setConnect(this);

    }

    private void obtainResSources() {
        if (mResources == null) {
            mResources = getResources();
        }
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();

        //选的颜色跟上次一样,不用重新获取颜色资源
        if (mCurrId_phoneSource_colorView == clickId) {
            return;
        }

        obtainResSources();
        switch (clickId) {
            case R.id.pop_phoneSource_color1:
                currColor_phoneSource = "红色";
                mCurrId_phoneSource_colorView = R.id.pop_phoneSource_color1;
                break;
            case R.id.pop_phoneSource_color2:
                currColor_phoneSource = "黄色";
                mCurrId_phoneSource_colorView = R.id.pop_phoneSource_color2;
                break;
            case R.id.pop_phoneSource_color3:
                currColor_phoneSource = "绿色";
                mCurrId_phoneSource_colorView = R.id.pop_phoneSource_color3;
                break;
            case R.id.pop_phoneSource_color4:
                currColor_phoneSource = "蓝色";
                mCurrId_phoneSource_colorView = R.id.pop_phoneSource_color4;
                break;
            case R.id.pop_phoneSource_color5:
                currColor_phoneSource = "黑色";
                mCurrId_phoneSource_colorView = R.id.pop_phoneSource_color5;
                break;
        }
        insert2Sqlite(Constant.KEY_STRINGPHONESOURCECOLOR, currColor_phoneSource);
        mSecondSetting_phoneCall_phoneSource_showStyle.setSummaryTitle(currColor_phoneSource);

        mPpw_phoneSource_color_choose.dismiss();
    }

    @Override
    public void onCall(View settingView, String order) {
        int settingId = settingView.getId();
        switch (order) {

            case SingleLineSelectLayout.SECONDSETORDER:

                switch (settingId) {
                    case R.id.setting_singleLSLayout_phoneCall://跳转到电话监听的第二级设置页面
                        mSettingActivity_rootLayout.removeAllViews();

                        View.inflate(mContext, R.layout.setting_phonecall, mSettingActivity_rootLayout);
                        //电话监听的第二级设置的子view获取
                        mLinearL_phoneCall_secondSetting_rootLayout = findViewById(R.id.linearL_phoneCall_secondSetting_rootLayout);
                        mSecondSetting_phoneCall = (SingleLineSelectLayout) findViewById(
                                R.id.secondSetting_singleLSLayout_phoneCall);
                        mSecondSetting_phoneCall.setSecondSettingState(false);

                        mSecondSetting_phoneCall_phoneSource_showStyle = (SingleLineSelectLayout) findViewById(
                                R.id.secondSetting_singleLSLayout_phoneCall_phoneSource_showStyle);
                        mSecondSetting_phoneCall_phoneSource_showStyle.setSecondSettingState(true);//SingleLineSelectLayout应该还要加多一个state(不需要状态view)

                        //电话拨出和来电监听是否开启
                        boolean isPhoneCallOpen = (boolean) obtainFromSqlite(Constant.KEY_ISPHONECALLOPEN);
                        mSecondSetting_phoneCall.setOpenState(isPhoneCallOpen);

                        //获取：保存的号码来源的提示信息(Toast)的颜色
                        String phoneSourceColor = (String) obtainFromSqlite(Constant.KEY_STRINGPHONESOURCECOLOR);
                        mSecondSetting_phoneCall_phoneSource_showStyle.setSummaryTitle(phoneSourceColor);

                        //设置view的点击监听
                        mSecondSetting_phoneCall.setConnect(this);
                        mSecondSetting_phoneCall_phoneSource_showStyle.setConnect(this);
                        //电话拨出和来电监听如果关闭了，则蒙蔽号码来源的吐司颜色的选取

                        break;

                    case R.id.secondSetting_singleLSLayout_phoneCall_phoneSource_showStyle:
                        Log.i(TAG, "onCall: secondSetting_singleLSLayout_phoneCall_phoneSource_showStyle");
                        LinearLayout linearL_phoneSource_colorChoose = (LinearLayout) View.inflate(mContext, R.layout.pop_callphone_setting_phonesource_color_choose, null);
                        //获取：设置号码来源的提示信息(Toast)颜色的color-view。两种方法获取子view和设置点击监听
                        int phoneSourceColorCount = linearL_phoneSource_colorChoose.getChildCount();
                        View currColorView = null;
                        for (int i = 0; i < phoneSourceColorCount; i++) {
                            currColorView = linearL_phoneSource_colorChoose.getChildAt(i);
                            currColorView.setOnClickListener(this);
                        }

                        mPpw_phoneSource_color_choose = new PopupWindow();
                        mPpw_phoneSource_color_choose.setContentView(linearL_phoneSource_colorChoose);

                        mPpw_phoneSource_color_choose.setFocusable(true);
                        mPpw_phoneSource_color_choose.setOutsideTouchable(true);
                        mPpw_phoneSource_color_choose.setBackgroundDrawable(new BitmapDrawable());

                        ScreenUtils.initScreen(SettingActivity.this);
                        float screenDensity = ScreenUtils.getScreenDensity();
                        mPpw_phoneSource_color_choose.setWidth((int) (100 * screenDensity));
                        mPpw_phoneSource_color_choose.setHeight((int) (1000 * screenDensity));

//                        mPpw_phoneSource_color_choose.setAnimationStyle(R.style.ppw_phoneSource_color_choose);

                        //从屏幕右侧弹出。可以做的是：颜色就像DNF的祭坛副本的右侧菜单，行云流水般滑出。还没实现，先实现一致滑出
                        mPpw_phoneSource_color_choose.showAsDropDown(mLinearL_phoneCall_secondSetting_rootLayout, ScreenUtils.getScreenW()
                                , -ScreenUtils.getScreenH());
                        break;
                }

                break;
            case SingleLineSelectLayout.OPENSTARTSTRING:
                switch (settingId) {
                    case R.id.secondSetting_singleLSLayout_phoneCall:
                        insert2Sqlite(Constant.KEY_ISPHONECALLOPEN, true);
                        if (mSecondSetting_phoneCall != null) {
                            boolean openState = mSecondSetting_phoneCall.getOpenState();
                            boolean isOpenListenPhoneCall = ComponentState.checkServiceState(mContext, "ListenPhoneCallService");

                            if (openState) {//电话监听设置开启
                                //电话监听服务没开启则开启
                                if (!isOpenListenPhoneCall) {
                                    mIntent_startPhoneCallListener = new Intent(SettingActivity.this, ListenPhoneCallService.class);
                                    startService(mIntent_startPhoneCallListener);
                                }
                            }
                        }

                        break;
                }
                break;
            case SingleLineSelectLayout.CLOSESTARTSTRING:
                switch (settingId) {
                    case R.id.secondSetting_singleLSLayout_phoneCall:
                        insert2Sqlite(Constant.KEY_ISPHONECALLOPEN, false);
/*//上面的无法进行，先提取上面的代码在这里测试
                        if (mSecondSetting_phoneCall != null) {
                            boolean isOpenListenPhoneCall = ComponentState.checkServiceState(mContext, "ListenPhoneCallService");

                            if (isOpenListenPhoneCall) {//停止电话监听服务
                                if (mIntent_startPhoneCallListener == null) {
                                    mIntent_startPhoneCallListener = new Intent(SettingActivity.this, ListenPhoneCallService.class);
                                }
                                stopService(mIntent_startPhoneCallListener);
                            }
                        }*/
                        if (mSecondSetting_phoneCall != null) {
                            boolean openState = mSecondSetting_phoneCall.getOpenState();
                            boolean isOpenListenPhoneCall = ComponentState.checkServiceState(mContext, "ListenPhoneCallService");

                            if (openState) {//电话监听设置开启
                                //电话监听服务没开启则开启
                                if (!isOpenListenPhoneCall) {
                                    mIntent_startPhoneCallListener = new Intent(SettingActivity.this, ListenPhoneCallService.class);
                                    startService(mIntent_startPhoneCallListener);
                                }
                            }
                        }
                        break;
                }
                break;
        }
    }

    private void insert2Sqlite(String key, Object data) {
        new AppConfig(mContext);
        AppConfig.insert2sqlite(key, data);
    }

    private Object obtainFromSqlite(String key) {
        new AppConfig(mContext);
        return AppConfig.obtainFromSqlite(key);
    }
}
