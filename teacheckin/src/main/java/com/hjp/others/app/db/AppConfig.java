package com.hjp.others.app.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.hjp.others.constants.db.Setting;

import static android.content.Context.MODE_PRIVATE;

/**
 * 保存应用信息
 *
 * @author Administrator
 */
public class AppConfig extends Object {
    //自定义
    private SharedPreferences innerConfig;
    //默认
    private SharedPreferences defaultConfig;


    public AppConfig(final Context context) {
        innerConfig = context.getSharedPreferences("apponfig", MODE_PRIVATE);
        defaultConfig = context.getSharedPreferences("com.hjp.coursecheckin_preferences", MODE_PRIVATE);
    }


    //夜间模式
    public void setNightModeSwitch(boolean isNight) {

        Editor editor = edit(innerConfig);
        editor.putBoolean(Setting.KEY_NIGHT_MODE_SWITCH, isNight);
        commit(editor);
    }

    public boolean getNightModeSwitch() {
        return innerConfig.getBoolean(Setting.KEY_NIGHT_MODE_SWITCH, false);
    }

    //手势锁的加密密码
    public void setGesturePassWord(String passWord) {
        Editor editor = edit(innerConfig);
        editor.putString(Setting.KEY_GESTURE_PASSWORD, passWord);
        commit(editor);
    }

    public String getKeyGesturePassword() {
        return innerConfig.getString(Setting.KEY_GESTURE_PASSWORD, "");
    }

    //手势锁的状态(已验证/无验证)
    public void setGestureVerifyState(boolean isVerify) {
        Editor editor = edit(innerConfig);
        editor.putBoolean(Setting.KEY_GESTURE_VERIFY_STATE, isVerify);
        commit(editor);
    }

    public boolean getGestureVerifyState() {
        return innerConfig.getBoolean(Setting.KEY_GESTURE_VERIFY_STATE, false);
    }

    //手势锁开启/不开启
    public void setGestureClockOpen(boolean isOpen) {
        Editor editor = edit(defaultConfig);
        editor.putBoolean(Setting.KEY_GESTUR_CLOCK_OPEN, isOpen);
        commit(editor);
    }

    public boolean getGestureClockOpen() {
        return defaultConfig.getBoolean(Setting.KEY_GESTUR_CLOCK_OPEN, true);
    }


    private Editor edit(SharedPreferences preferences) {
        return preferences.edit();
    }

    private void commit(Editor editor) {
        editor.commit();
    }

    /**
     * 清空
     */
    public void clear() {
        Editor editor = innerConfig.edit();
        editor.clear();
        editor.commit();
    }
}
