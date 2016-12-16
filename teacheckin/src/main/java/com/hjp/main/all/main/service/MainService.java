package com.hjp.main.all.main.service;

import com.hjp.main.all.main.service.listener.OnCoursesObtainListener;

/**
 * Created by HJP on 2016/12/5.
 */

public interface MainService {

    void getCourses(String teaNum, String date,OnCoursesObtainListener onCoursesObtainListener);
}
