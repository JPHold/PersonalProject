package com.hjp.main.all.msg.service.listener;

/**
 * Created by HJP on 2016/12/5.
 */

public interface OnClassesCheckInInfoObtainListener {
    void onSuccess();
    void onError(int status,String msg);
}
