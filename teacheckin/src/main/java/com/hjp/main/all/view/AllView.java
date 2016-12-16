package com.hjp.main.all.view;

import com.hjp.ProvinceBean;
import com.hjp.main.all.service.bean.ClassRoom;
import com.hjp.main.all.service.bean.Course;
import com.hjp.main.all.service.bean.CoursePart;
import com.hjp.main.base.view.CourseBaseView;
import com.hjp.main.base.view.GestureBaseView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by HJP on 2016/12/4.
 */

public interface AllView extends GestureBaseView, CourseBaseView {

    /**
     * 获得手势锁的开启状态
     */
    boolean getGestureState();

    /**
     * 显示课程选择器
     */
    void setMakeCheckInCourses(ArrayList<Course> courseNames, Map<String, Map<String, Object>> makeCheckInInfoMap);

    /**
     * 显示课室\周数\节数三级选择器
     */
    void setMakeCheckInInfos(String selectCourse, ArrayList<ClassRoom> classRooms, ArrayList<ArrayList<String>> weekNums
            , ArrayList<ArrayList<ArrayList<CoursePart>>> courseParts);

    /**
     * 发布签到请求成功
     */
    void successMakeCheckIn();
    /**
     * 发布签到请求失败
     */
    void errorMakeCheckIn(int status,String msg);
}
