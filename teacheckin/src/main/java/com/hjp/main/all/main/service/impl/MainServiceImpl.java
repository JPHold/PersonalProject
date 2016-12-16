package com.hjp.main.all.main.service.impl;

import android.text.TextUtils;

import com.hjp.main.all.main.service.MainService;
import com.hjp.main.all.main.service.listener.OnCoursesObtainListener;
import com.hjp.others.constants.url.MainUrl;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by HJP on 2016/12/5.
 */

public class MainServiceImpl implements MainService {

    @Override
    public void getCourses(String teaNum, String date,final OnCoursesObtainListener onCoursesObtainListener) {
        StringCallback stringCallback = new StringCallback() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws IOException {
                return super.parseNetworkResponse(response, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                onCoursesObtainListener.onError(id, e.getMessage());
            }

            @Override
            public void onResponse(String result, int id) {
                //获取当前老师教授的课程的结果
                String json_courseListByTeacher = result;

                if (TextUtils.isEmpty(json_courseListByTeacher)) {
                    onCoursesObtainListener.onError(0, "无课程");
                } else {

                    try {
                        JSONArray courses = JSONArray.fromObject(json_courseListByTeacher);

                        if (courses != null) {

                            List<String> courseIds = new ArrayList<>();
                            List<String> courseNames = new ArrayList<>();

                            for (int i = 0; i < courses.size(); i++) {
                                JSONObject course = courses.getJSONObject(i);

                                Iterator checkInTypeKeys = course.entrySet().iterator();
                                while (checkInTypeKeys.hasNext()) {
                                    Map.Entry courseEntry = (Map.Entry) checkInTypeKeys.next();
                                    //课程id
                                    String courseId = (String) courseEntry.getKey();
                                    courseIds.add(courseId);

                                    //课程名
                                    String courseName = (String) courseEntry.getValue();
                                    courseNames.add(courseName);
                                }
                            }

                            onCoursesObtainListener.onSuccess(courseIds,courseNames);

                        }
                    } catch (JSONException e) {
                        //不是json数据，而是提示错误的信息
                        String errorRequestText = result;
                        onCoursesObtainListener.onError(1, errorRequestText);
                    }

                }

            }

        };

        //获取当前老师教授的课程
        OkHttpUtils
                .post()
                .url(MainUrl.URL_FINDCOURSELISTFROMTEACHER)
                .addParams(MainUrl.PARAM_TEACHERNUM, teaNum)
                .addParams(MainUrl.PARAM_DATE, date)
                .build()
                .execute(stringCallback);
    }
}
