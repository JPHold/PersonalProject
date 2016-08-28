package com.hjp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.hjp.constant.Constant;

/**
 * Created by HJP on 2016/8/21 0021.
 */

public class AppConfig {

    public static  Context mContext;
    public static SharedPreferences mSharedPreferences;

    public AppConfig(Context context){
        mContext =context;
    }

    public static void getDefaultSqlite() {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences(Constant.NAME_APPCONFIG, Activity.MODE_PRIVATE);
        }
    }

    /**
     * 插入数据到默认sqlite
     */
    public static  void insert2sqlite(String key, Object data) {
        getDefaultSqlite();
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        if (data instanceof Boolean) {
            edit.putBoolean(key, (Boolean) data);
        }
        edit.commit();
    }

    public static Object obtainFromSqlite(String key) {
        getDefaultSqlite();
        if (key.contains("is")) {
            return mSharedPreferences.getBoolean(key, false);
        }
        return null;
    }
}
