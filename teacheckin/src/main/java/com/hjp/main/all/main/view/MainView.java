package com.hjp.main.all.main.view;

import com.hjp.main.base.view.CourseBaseView;

import java.util.List;

/**
 * Created by HJP on 2016/12/5.
 */

public interface MainView  extends CourseBaseView {

    /**
     * 查询课程名成功后：显示数据
     * @param courses
     */
    void setCourseNames(List<String> courseNums,List<String> courseNames);

}
