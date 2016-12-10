package com.hjp.main.all.main.view;

import java.util.List;

/**
 * Created by HJP on 2016/12/5.
 */

public interface MainView {

    /**
     * 提示开始查询课程名
     */
    void startObtainCourses();

    /**
     *  获取老师工号：用来请求所有课程名
     * @return
     */
    String getTeaNum();

    /**
     * 查询课程名失败
     * @param status 失败编号
     * @param msg 失败信息
     */
    void errorObtainCourse(int status,String msg);

    /**
     * 查询课程名成功后：显示数据
     * @param courses
     */
    void setCourseNames(List<String> courseNums,List<String> courseNames);

}
