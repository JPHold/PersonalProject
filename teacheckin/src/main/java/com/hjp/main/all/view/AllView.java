package com.hjp.main.all.view;

import com.hjp.main.base.view.GestureBaseView;

/**
 * Created by HJP on 2016/12/4.
 */

public interface AllView extends GestureBaseView {

    /**
     * 获得手势锁的开启状态
     */
    boolean  getGestureState();

    /**
     * 提供老师工号给MainFragment
     * @return
     */
    String getTeaNum();
}
