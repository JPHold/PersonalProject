package com.hjp.main.base.service;

import android.content.Context;

import com.hjp.others.util.SecureUtil;

/**
 * Created by HJP on 2016/12/4.
 */

public interface GestureBaseService {

    void dissectionKeyPwd(final Context context, final String type, final String certificateName,
                          final String passWord, final String data, final String dataCoding,
                          final SecureUtil.OnResultListener resultListener);
}
