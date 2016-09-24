package com.hjp.mobilesafe.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.hjp.mobilesafe.R;

/**
 * Created by HJP on 2016/9/22.
 */

public class ShowAppShortCut {
    public static void show(Context context) {
        //创建app快捷方式(发送广播,通知launcher创建)
        Intent createShortCutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士");
        createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        //点击快捷方式,要跳转到哪个组件
        Intent actionIntent = new Intent();//不能为显示意图
actionIntent.setAction("com.shortcut.main");
        createShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);

        //通知创建快捷方式
        context.sendBroadcast(createShortCutIntent);
    }
}
