package com.hjp.main.all.msg.view;

/**
 * Created by HJP on 2016/12/5.
 */

public interface ClassView {
    /**
     * 提示开始查询课程名
     */
    void startObtainClassCheckInInfo();

    /**
     *
     */
    void errorObtainClassCheckInInfo(int status,String msg);
}
