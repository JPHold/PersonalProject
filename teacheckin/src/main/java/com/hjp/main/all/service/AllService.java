package com.hjp.main.all.service;

import com.hjp.main.all.service.listener.OnMakeCheckInInfosListener;
import com.hjp.main.all.service.listener.OnMakeCheckInListener;
import com.hjp.main.base.service.GestureBaseService;

import java.util.Map;

/**
 * Created by HJP on 2016/12/4.
 */

public interface AllService extends GestureBaseService {

    /**
     * 根据老师工号和年份学期id来查询出课程
     */
    void getMakeCheckInInfos(String teaNum, String dateTime, OnMakeCheckInInfosListener makeCheckInInfosListener);

    /**
     * 发布签到要求
     */
    void makeCheckIn(String courseId, Integer classroomId, String weekNum, Integer partId, OnMakeCheckInListener onMakeCheckInListener);
}
