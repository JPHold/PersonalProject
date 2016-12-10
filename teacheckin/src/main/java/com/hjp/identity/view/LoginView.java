package com.hjp.identity.view;

import android.app.Activity;

import com.hjp.identity.bean.Teacher;

/**
 * Created by HJP on 2016/11/27.
 */

public interface LoginView {

    /**
     * 账户和密码为空
     */
    void emptyToast(String msg);

    /**
     * 获取输入的工号和密码
     */
    String getNumber();
    String getPwd();

    /**
     * 开始登录,启动progressbar
     */
    void startLogin();

    /**
     * 登录成功
     * @param jumpActivity 要跳转的activity
     */
    void successLogin(Teacher teacher);

    /**
     * 登录失败
     * @param msg 失败的原因
     */
    void errorLogin(int status,String msg);

    /**
     * 停止登录,取消progressbar
     */
    void stopLogin();
}
