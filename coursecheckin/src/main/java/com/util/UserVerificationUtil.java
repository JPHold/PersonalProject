package com.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.hjp.Activity.LoginActivity;
import com.hjp.Application.MainApplication;
import com.hjp.Database.AppDataBase;

/**
 * Created by HJP on 2016/6/28 0028.
 */
public class UserVerificationUtil {

    private static void getCurrUserFromDataBase(Context mContext, String teacherNum, Activity currActivity) {
        AppDataBase appDataBase = new AppDataBase(mContext, "user.db", null, 1);
        teacherNum = appDataBase.select("userstate", new String[]{"userName"}, "where loginState=?", new String[]{"yes"}
                , null, null, null, null);

        if (AppDataBase.QUERY_ERROR.equals(teacherNum)) {
            //没有登录,跳转到登录界面
            Intent intent_jump2LoginActivity = new Intent(currActivity, LoginActivity.class);
            currActivity.startActivity(intent_jump2LoginActivity);
            currActivity.finish();
        }
    }

    public static String verifyCurrUserFromIntent(Context mContext, Intent jumpIntent, Activity currActivity) {

        String teacherNum = jumpIntent.getStringExtra("user");
        String loginState = jumpIntent.getStringExtra("loginState");
        if (teacherNum == null || teacherNum.length() <= 0 || loginState == null || loginState.length() <= 0) {
            getCurrUserFromDataBase(mContext, teacherNum, currActivity);
        }
        return teacherNum;
    }

    public static String verifyCurrUserFromApplication(Context mContext, Activity currActivity) {
        Application application = currActivity.getApplication();
        String teacherNum = null;
        if (application instanceof MainApplication) {
            teacherNum = ((MainApplication) application).getCurrUser();
        }
        if (teacherNum == null || teacherNum.length() <= 0) {
            getCurrUserFromDataBase(mContext, teacherNum, currActivity);
        }
        return teacherNum;
    }
}
