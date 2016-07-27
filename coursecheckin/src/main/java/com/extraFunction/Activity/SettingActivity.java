package com.extraFunction.Activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.BaseContent.CallListener;
import com.extraFunction.Fragment.SettingsFragment;
import com.hjp.Application.MainApplication;
import com.hjp.coursecheckin.R;
import com.util.DialogUtil;
import com.util.SendInModuleUtil;

/**
 * Created by HJP on 2016/7/3 0003.
 */

public class SettingActivity extends Activity implements CallListener {
    public static final String NOTIFY = "notify";
    public static final String RING = "ring";
    public static final String DEVICECLOCK = "deviceClock";
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        mContext = getApplicationContext();

        SettingsFragment settingsFragment = new SettingsFragment();
        settingsFragment.setCallListener(this);

        getFragmentManager().beginTransaction()
                .add(android.R.id.content, settingsFragment)
                .commit();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 1:
                    MainApplication app = (MainApplication) getApplication();
                    app.addActivityTask(SettingActivity.this);
                    DialogUtil.showDialog(SettingActivity.this, 0, R.string.exit, R.string.sureExit, R.string.noExit);
                    break;
            }
        }
    };


    @Override
    public void call(Object data) {
        SendInModuleUtil.sendMsgByHandler(1, null, handler);
    }
}
