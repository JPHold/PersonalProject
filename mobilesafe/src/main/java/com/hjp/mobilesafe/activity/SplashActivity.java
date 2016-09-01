package com.hjp.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.hjp.mobilesafe.R;

/**
 * Created by HJP on 2016/8/19 0019.
 */

/**
 * 主要完成以下内容
 * 显示当前版本
 * 检查是否有更新版本
 */

public class SplashActivity extends Activity {
    private TextView textV_splash_version;
    private Object appVersion;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //初始资源
        initView();
        //检查版本


    }

    @Override
    protected void onStart() {
        super.onStart();
        //到主页面
        Intent intent=new Intent(SplashActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        textV_splash_version = (TextView) findViewById(R.id.textV_splash_version);
        String appVersion = getAppVersion();
        textV_splash_version.setText("版本号："+appVersion);
    }

    private String getAppVersion() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
