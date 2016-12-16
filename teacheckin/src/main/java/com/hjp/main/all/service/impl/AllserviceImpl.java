package com.hjp.main.all.service.impl;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.hjp.main.all.service.AllService;
import com.hjp.main.all.service.bean.ClassRoom;
import com.hjp.main.all.service.bean.Course;
import com.hjp.main.all.service.bean.CoursePart;
import com.hjp.main.all.service.bean.WeekNum;
import com.hjp.main.all.service.listener.OnMakeCheckInInfosListener;
import com.hjp.main.all.service.listener.OnMakeCheckInListener;
import com.hjp.others.constants.url.AllUrl;
import com.hjp.others.constants.url.MainUrl;
import com.hjp.others.util.ListUtils;
import com.hjp.others.util.SecureUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by HJP on 2016/12/4.
 */

public class AllServiceImpl implements AllService {
    @Override
    public void dissectionKeyPwd(Context context, String type, String certificateName, String passWord, String data, String dataCoding, SecureUtil.OnResultListener resultListener) {

    }


    @Override
    public void getMakeCheckInInfos(String teaNum, String date, final OnMakeCheckInInfosListener makeCheckInInfosListener) {
        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                makeCheckInInfosListener.onError(id, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                if (TextUtils.isEmpty(response)) {
                    makeCheckInInfosListener.onError(id, "结果为空");
                }

                /*{"1":{"courseparts":["大课","小课"],"weeknums":{"start":1,"end":19},"coursename":"杀杀杀","classroomname":["T101"]}}
*/
                /*
                {"1":{"courseparts":[{"name":"大课","id":1},{"name":"小课","id":2}]
                ,"weeknums":{"start":1,"end":19}
                ,"coursename":"杀杀杀"
                ,"classroomname":[{"name":"T101","id":1}]}}
                */
                //解析json
                try {
                    JSONObject result = JSONObject.fromObject(response);
                    Iterator makeCheckInInfosKeys = result.entrySet().iterator();
                    Map<String, Map<String, Object>> makeCheckInInfos = new HashMap<>();

                    ArrayList<Course> courseNames = new ArrayList<>();
               /*     ArrayList<WeekNum> weekNums = new ArrayList<>();
                    ArrayList<List<String>> classRooms = new ArrayList<>();
                    ArrayList<List<String>> courseParts = new ArrayList<>();*/

                    while (makeCheckInInfosKeys.hasNext()) {
                        Map.Entry makeCheckInInfosEntry = (Map.Entry) makeCheckInInfosKeys.next();
                        //课程编号
                        String courseId = (String) makeCheckInInfosEntry.getKey();
                        //存储课室\周数\节数
                        Map<String, Object> makeCheckInInfoMap = new HashMap<>();

                        JSONObject makeCheckInInfo = result.getJSONObject(courseId);
                        Iterator makeCheckInInfoKeys = makeCheckInInfo.entrySet().iterator();

                        String courseName = null;
                        while (makeCheckInInfoKeys.hasNext()) {
                            Map.Entry makeCheckInInfoEntry = (Map.Entry) makeCheckInInfoKeys.next();
                            String key = (String) makeCheckInInfoEntry.getKey();
                            switch (key) {
                                case "coursename":
                                    courseName = (String) makeCheckInInfoEntry.getValue();
                                    Course course = new Course();
                                    course.setId(courseId);
                                    course.setName(courseName);
                                    courseNames.add(course);
                                    break;
                                case "classroomname":
                                    List<ClassRoom> classRoomNames = JSON.parseArray(makeCheckInInfoEntry.getValue().toString(), ClassRoom.class);
                                    makeCheckInInfoMap.put("classroomname", classRoomNames);
                                    break;
                                case "courseparts":
                                    List<CoursePart> courseParts = JSON.parseArray(makeCheckInInfoEntry.getValue().toString().trim(), CoursePart.class);
                                    makeCheckInInfoMap.put("courseparts", courseParts);
                                    break;
                                case "weeknums":
                                    WeekNum weekNums = JSON.parseObject(makeCheckInInfoEntry.getValue().toString(), WeekNum.class);
                                    makeCheckInInfoMap.put("weeknums", weekNums);
                                    break;
                            }

                        }

                        makeCheckInInfos.put(courseNames.get(courseNames.size() - 1).getId(), makeCheckInInfoMap);

                    }

                    if (!ListUtils.isEmpty(courseNames)) {
                        makeCheckInInfosListener.onSuccess(courseNames, makeCheckInInfos);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    makeCheckInInfosListener.onError(id, "json解析错误");
                }
            }
        };

        OkHttpUtils
                .post()
                .url(MainUrl.URL_FINGETMAKECHECKININFOS)
                .addParams(MainUrl.PARAM_TEACHERNUM, teaNum)
                .addParams(MainUrl.PARAM_DATE, date)
                .build()
                .execute(stringCallback);
    }

    @Override
    public void makeCheckIn(String courseId, Integer classRoomId, String weekNum, Integer coursePartId, final OnMakeCheckInListener onMakeCheckInListener) {

        StringCallback stringCallback = new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                onMakeCheckInListener.onError(id, e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                if (AllUrl.SUCCESS.equals(response)) {
                    onMakeCheckInListener.onSuccess();
                    return;
                } else {
                    onMakeCheckInListener.onError(id, "发布失败,请重试!");
                    return;
                }
            }
        };

        OkHttpUtils
                .post()
                .url(AllUrl.URL_MAKECHECKIN)
                .addParams(AllUrl.PARAM_COURSEID, courseId)
                .addParams(AllUrl.PARAM_CLASSROOMID, String.valueOf(classRoomId))
                .addParams(AllUrl.PARAM_WEEKNUM, weekNum)
                .addParams(AllUrl.PARAM_COURSEPARTID, String.valueOf(coursePartId))
                .build()
                .execute(stringCallback);
    }
}
