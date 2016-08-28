package com.hjp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjp.constant.Constant;
import com.hjp.customview.SingleLineSelectLayout;
import com.hjp.utils.AppConfig;

import java.math.BigDecimal;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class SettingActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private SingleLineSelectLayout mSingleLSLayout_phoneCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        mSingleLSLayout_phoneCall = (SingleLineSelectLayout) findViewById(R.id.singleLSLayout_phoneCall);
        mSingleLSLayout_phoneCall.setSettingState(true);//添加箭头设置指向
        mSingleLSLayout_phoneCall.setOnClickListener(this);//点击，改变checkBox的状态以及副标题
    }

    @Override
    public void onClick(View v) {
        int clickId = v.getId();
        switch (clickId) {
            case R.id.singleLSLayout_phoneCall:

                setContentView(R.layout.setting_phonecall);
                //电话监听的第二级设置的子view获取
                SingleLineSelectLayout secondSetting_phoneCall = (SingleLineSelectLayout) findViewById(
                        R.id.singleLSLayout_phoneCall_secondSetting);
                SingleLineSelectLayout secondSetting_phoneSource_showStyle_choose = (SingleLineSelectLayout) findViewById(
                        R.id.singleLSLayout_phoneCall_secondSetting_phoneSource_showStyle);

                //电话拨出和来电监听是否开启
                new AppConfig(mContext);
                boolean isPhoneCallOpen = (boolean) AppConfig.obtainFromSqlite(Constant.KEY_ISPHONECALLOPEN);
                secondSetting_phoneCall.setOpenState(isPhoneCallOpen);
                //电话拨出和来电监听如果关闭了，则蒙蔽号码来源的吐司颜色的选取
//                secondSetting_phoneSource_showStyle_choose.setco
                break;
        }

    }
}
