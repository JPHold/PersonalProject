package com.hjp.main.base.view;

/**
 * Created by HJP on 2016/12/10.
 */

public interface CourseBaseView {

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
     * 获取当前年份
     * @return
     */
    String getDate();

    /**
     * 查询课程名失败
     * @param status 失败编号
     * @param msg 失败信息
     */
    void errorObtainCourse(int status,String msg);

}
