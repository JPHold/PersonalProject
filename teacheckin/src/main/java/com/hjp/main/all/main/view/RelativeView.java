package com.hjp.main.all.main.view;

import android.content.Context;
import android.support.v4.app.FragmentManager;

/**
 * Created by HJP on 2016/12/5.
 */

public interface RelativeView {

    /**
     * Fragment获取数据时的提示
     */
    void runProgressBar();
    /**
     * 停止提示
     */
    void  stopProgressBar();

}
