package com.hjp.NetWork;

import android.content.Context;
import android.util.Log;

import com.hjp.vo.ComputerCheckIn;
import com.hjp.vo.ComputerCourse;
import com.hjp.vo.ComputerCourseClass;
import com.hjp.vo.StudentInfo;
import com.hjp.vo.TeacherInfo;

import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by HJP on 2015/12/14 0014.
 */
public class MainBmobQuery2 {
    private final String TAG = "test";
    private final Context mContext;

    public MainBmobQuery2(Context context) {
        mContext = context;

    }

    private void processTeacherLogin(final String user, final String pwd, final UserRequestLoginListener requestLoginListener) {
        BmobQuery<TeacherInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("jobnum", user);
        query.addQueryKeys("pwd");
        query.findObjects(mContext, new FindListener<TeacherInfo>() {
            @Override
            public void onSuccess(List<TeacherInfo> list) {
                if (list != null && list.size() > 0) {
                    String queryPwd = list.get(0).getPwd();
                    checkLoginPwd(pwd, queryPwd, requestLoginListener);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.i("bmob", "检查登录请求" + s);
                requestLoginListener.msg("无此用户");
            }
        });
    }

    private void checkLoginPwd(String requestPwd, String queryPwd, UserRequestLoginListener requestLoginListener) {
        if (queryPwd != null && queryPwd.length() > 0) {
            if (requestPwd.equals(queryPwd)) {
                requestLoginListener.msg("密码正确");
            } else {
                requestLoginListener.msg("密码错误");
            }
        } else {
            requestLoginListener.msg("无此用户");
        }
    }

    private void processStudentLogin(final String user, final String pwd, final UserRequestLoginListener requestLoginListener) {
        BmobQuery<StudentInfo> query = new BmobQuery<>();
        query.addWhereEqualTo("stuNum", user);
        query.addQueryKeys("pwd");
        query.findObjects(mContext, new FindListener<StudentInfo>() {
            @Override
            public void onSuccess(List<StudentInfo> list) {
                if (list != null && list.size() > 0) {
                    String queryPwd = list.get(0).getPwd();
                    checkLoginPwd(pwd, queryPwd, requestLoginListener);
                }
            }

            @Override
            public void onError(int i, String s) {
                Log.i("bmob", "检查登录请求" + s);
                requestLoginListener.msg("无此用户");
            }
        });
    }

    public void processRequestLogin(final String loginType, final String user, final String pwd, final UserRequestLoginListener requestLoginListener) {
        Log.i(TAG, "processRequestLogin: " + loginType);
        if ("教师".equals(loginType)) {
            Log.i(TAG, "processRequestLogin: 教师");
            processTeacherLogin(user, pwd, requestLoginListener);
        } else if ("学生".equals(loginType)) {
            Log.i(TAG, "processRequestLogin: 学生");
            processStudentLogin(user, pwd, requestLoginListener);
        }
    }

    /**
     * @param jobNum
     * @param listener 查询当前老师教的所有课程
     * @throws ParseException
     */

    public void querySpecialTeacherCourses(final String jobNum, final SpecialTeacherCoursesQueryListener listener) throws ParseException {
        final BmobQuery<ComputerCourse> query = new BmobQuery<>();
        //降序显示数据,不知道为什么后端不行
        query.order("createAt");
        query.addWhereEqualTo("jobNum", jobNum);
        query.addQueryKeys("courseNames");

        query.findObjects(mContext, new FindListener<ComputerCourse>() {
            @Override
            public void onSuccess(List<ComputerCourse> list) {
                if (list != null && list.size() > 0) {
                    List<String> result = list.get(0).getcourseNames();
                    listener.Success(result);
                } else {
                    onError(000, "返回成功但值小于0");
                }
            }

            @Override
            public void onError(int code, String msg) {
                listener.Error(msg);
            }
        });

    }

    /**
     * @param jobNum               当前登录的老师工号
     * @param courseName           当前课程的名字
     * @param classesQueryListener 获取当前老师的当前课程的所有班级
     */
    public void querySpecialCourseClasses(String jobNum, String courseName, final SpecialCourseClassesQueryListener classesQueryListener) {
        Log.i(TAG, "querySpecialCourseClasses: " + jobNum + "|" + courseName);
        BmobQuery<ComputerCourseClass> query = new BmobQuery<>();
        query.addWhereEqualTo("jobNum", jobNum);
        query.addWhereEqualTo("courseName", courseName);
        query.addQueryKeys("classes");
        query.findObjects(mContext, new FindListener<ComputerCourseClass>() {
            @Override
            public void onSuccess(List<ComputerCourseClass> list) {
                if (list != null && list.size() > 0) {
                    classesQueryListener.Success(list.get(0).getClasses());
                    return;
                } else {
                    onError(000, "返回成功但值小于等于0");
                }
            }

            @Override
            public void onError(int type, String msg) {
                classesQueryListener.Error(msg);
            }
        });
    }

    //存放当前课程,每个班级的签到人数和未签到人数
    private Map<String, List<Double>> mapArray_classesCheckInInfo;
    private int classIndex = 0;

    /**
     * 查询完签到情况的班级数
     */
    private int inquiredSum = 0;

    /**
     * @param jobNum                        当前登录的老师工号
     * @param courseName                    当前课程的名字
     * @param currCourseClasses             当前课程的所有班级
     * @param classesCheckInfoQueryListener 查询当前老师的当前课程的所有班级的签到人数
     */
    public void querySpecialCourseClassesCheckInInfo(final String jobNum, final String courseName, final List<String> currCourseClasses,
                                                     final SpecialCourseClassesCheckInInfoQueryListener classesCheckInfoQueryListener) {
        mapArray_classesCheckInInfo = new HashMap<>();
        Log.i(TAG, "querySpecialCourseClassesCheckInInfo: " + jobNum + "|" + courseName + "|" + currCourseClasses.size());

        final int size_class = currCourseClasses.size();
        Log.i(TAG, "班级数: " + size_class);


        for (classIndex = 0; classIndex < size_class; classIndex++) {
            final String className = currCourseClasses.get(classIndex);

            /**
             * 防止结果回调时,出现签到数据存到其他班级和一个班级的签到情况list中存有两个以上的数的出现(只能有签到人数和未签到人数这两个数)
             * 所以先分配好className键值存储空间
             */
            mapArray_classesCheckInInfo.put(className, new ArrayList<Double>());

            Log.i(TAG, "班级名: " + className + "i  " + classIndex);

            /**
             * 注意:BmobQuery一定要新建,不然获取当前班级只能获取到for循环中最后一个index所设置的查询条件className
             */

            //查询签到的人数
            final BmobQuery<ComputerCheckIn> query_checkIn = new BmobQuery<>();
            query_checkIn.addWhereEqualTo("jobNum", jobNum);
            query_checkIn.addWhereEqualTo("courseName", courseName);
            query_checkIn.addWhereExists("checkInAddress");
            query_checkIn.addWhereEqualTo("className", className);

            /**
             * countInquireIndex-----防止结果回调,出现一个班级的签到查询和未签到查询不是连续结果回调,
             *                              而是在这两个结果回调之间插了其他班级的签到结果回调
             */

            query_checkIn.count(mContext, ComputerCheckIn.class, new CountListener() {
                @Override
                public void onSuccess(int count) {

                    String currInquireClassName = null;
                    try {
                        currInquireClassName = (String) query_checkIn.getWhere().get("className");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Double> list_classCheckInInfo = (ArrayList<Double>) mapArray_classesCheckInInfo.get(className);
                    if (count >= 0) {
                        list_classCheckInInfo.add((double) count);
                    } else {
                        list_classCheckInInfo.add(0D);
                    }
                    Log.i(TAG, "onSuccess: 查询签到" + "当前班级的" + className + "签到人数" + count);
                    final BmobQuery<ComputerCheckIn> query_notCheckIn = new BmobQuery<>();
                    //查询没签到的人数
                    query_notCheckIn.addWhereEqualTo("jobNum", jobNum);
                    query_notCheckIn.addWhereEqualTo("courseName", courseName);
                    query_notCheckIn.addWhereDoesNotExists("checkInAddress");
                    query_notCheckIn.addWhereEqualTo("className", currInquireClassName);
                    query_notCheckIn.count(mContext, ComputerCheckIn.class, new CountListener() {
                        @Override
                        public void onSuccess(int count) {
                            String currInquireClassName = null;
                            try {
                                currInquireClassName = (String) query_checkIn.getWhere().get("className");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i(TAG, "查询未签到: " + currInquireClassName);

                            //注意:一个班级的签到查询和未签到查询有可能不是连续结果回调
                            //  所以这里不能用countInquireIndex来获取当前班级签到情况列表
                            ArrayList<Double> list_classCheckInInfo =
                                    (ArrayList<Double>) mapArray_classesCheckInInfo.get(currInquireClassName);

                            //为了避免查询没签到操作比查询签到操作先完成，造成顺序错乱的情况
                            while (list_classCheckInInfo.size() > 0) {
                                if (count >= 0) {
                                    list_classCheckInInfo.add((double) count);

                                } else {
                                    list_classCheckInInfo.add(0D);
                                }
                                //注意:有可能mapArray_classesCheckInInfo的存储顺序不是按照currCourseClasses的顺序
                                mapArray_classesCheckInInfo.put(className, list_classCheckInInfo);
                                Log.i(TAG, "onSuccess: 查询未签到:" + "当前班级: " + className + "未签到人数" + count);
                                inquiredSum++;
                                break;
                            }

                            //等待所有班级的签到情况都获取完毕
                            if (inquiredSum == size_class) {
                                Log.i(TAG, "当前key: " + mapArray_classesCheckInInfo);
                                classesCheckInfoQueryListener.Success(mapArray_classesCheckInInfo);
                            }
                        }

                        @Override
                        public void onFailure(int type, String msg) {
                            Log.i(TAG, "onFailure: 查询未签到");
                        }
                    });
                }

                @Override
                public void onFailure(int type, String msg) {
                    Log.i(TAG, "onFailure: 查询签到");
                }
            });


        }
    }

    /**
     * @param jobNum
     * @param courseName
     * @param className
     * @param classQueryListener 获取当前老师的当前课程的当前班级的未签到同学的名字及学号
     */

    public void querySpecialCourseClass(String jobNum, String courseName, String className,
                                        final SpecialClassQueryListener classQueryListener) {
        BmobQuery<ComputerCheckIn> query = new BmobQuery<>();
        query.addWhereEqualTo("jobNum", jobNum);
        query.addWhereEqualTo("courseName", courseName);
        query.addWhereEqualTo("className", className);
        query.addWhereDoesNotExists("geoPoint");
        query.addQueryKeys("stuNum,stuName");

        query.findObjects(mContext, new FindListener<ComputerCheckIn>() {
            @Override
            public void onSuccess(List<ComputerCheckIn> list) {
                ArrayList<String> listArray_saveNotCheckinPeoples = new ArrayList<String>();
                if (list != null) {
                    for (ComputerCheckIn checkIn : list) {
//                        listArray_saveNotCheckinPeoples.add(checkIn.getStuNum() + "  " + checkIn.getStuName());
                    }
                    classQueryListener.Success(listArray_saveNotCheckinPeoples);
                } else {
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    /**
     * 查询当前课程当前班级的所有人员的各自唯一id
     *
     * @param jobNum
     * @param courseName
     * @param className
     * @param needCheckInIdListener
     */
    private void getNeedCheckInId(String jobNum, String courseName, String className,
                                  final GetNeedCheckInIdListener needCheckInIdListener) {
        BmobQuery<ComputerCheckIn> query = new BmobQuery<>();
        query.addWhereEqualTo("jobNum", jobNum);
        query.addWhereEqualTo("courseName", courseName);
        query.addWhereEqualTo("className", className);
        query.addQueryKeys("objectId");
        query.findObjects(mContext, new FindListener<ComputerCheckIn>() {
            @Override
            public void onSuccess(List<ComputerCheckIn> list) {
                List<String> listArray_needCheckInId = new ArrayList<String>();
                if (list != null) {
                    for (int i = 0; i < list.size(); i++) {
                        listArray_needCheckInId.add(list.get(i).getObjectId());
                    }
                } else {
                    onError(1, "返回成功,但数据不正确");
                }
                needCheckInIdListener.data(listArray_needCheckInId);

            }

            @Override
            public void onError(int i, String s) {
                needCheckInIdListener.msg(s);
            }
        });

    }

    private interface GetNeedCheckInIdListener {
        public void data(List<String> list);

        public void msg(String msg);
    }

    public interface InsertPostCheckInListener {
        public void msg(String msg);
    }


    /**
     * 发出特定班级的签到请求
     *
     * @param jobNum
     * @param courseName
     * @param className
     * @return
     */
    public void insertPostCheckIn(String jobNum, String courseName, String className,
                                  final InsertPostCheckInListener postCheckInListener) {
        getNeedCheckInId(jobNum, courseName, className, new GetNeedCheckInIdListener() {
            @Override
            public void data(List<String> list) {
                List<BmobObject> listObject_updateBatch = new ArrayList<BmobObject>();
                for (String objectId : list) {
                    ComputerCheckIn checkIn = new ComputerCheckIn();
                    checkIn.setObjectId(objectId);
                    checkIn.setNeedCheckIn(true);
                    listObject_updateBatch.add(checkIn);
                }
                new BmobObject().updateBatch(mContext, listObject_updateBatch, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        postCheckInListener.msg("发布签到成功");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        postCheckInListener.msg("发布签到失败" + s);
                    }
                });
            }

            @Override
            public void msg(String msg) {
                postCheckInListener.msg(msg);
            }
        });
    }

    public interface CheckPostCheckInListener {
        public void data(List<Map<String, Object>> list);

        public void msg(String msg);
    }

    /**
     * 学生检查是否有签到发布
     *
     * @param stuNum
     * @param checkPostCheckInListener
     */
    public void checkPostCheckIn(String stuNum, final CheckPostCheckInListener checkPostCheckInListener) {
        BmobQuery<ComputerCheckIn> query = new BmobQuery<>();
        query.addWhereEqualTo("stuNum", stuNum);
        query.addWhereEqualTo("needCheckIn", "true");
        query.addQueryKeys("objectId");
        query.addQueryKeys("courseName");
        query.addQueryKeys("className");
        query.addQueryKeys("geoPoint");
        query.findObjects(mContext, new FindListener<ComputerCheckIn>() {
            @Override
            public void onSuccess(List<ComputerCheckIn> list) {
                List<Map<String, Object>> list_savePostCheckInInfo = new ArrayList<Map<String, Object>>();
                if (list != null) {
                    for (ComputerCheckIn checkIn : list) {
                        HashMap<String, Object> mapArray_onePostCheckIn = new HashMap<>();
                        mapArray_onePostCheckIn.put("objectId", checkIn.getObjectId());
                        mapArray_onePostCheckIn.put("courseName", checkIn.getCourseName());
                        mapArray_onePostCheckIn.put("className", checkIn.getClassName());
                        mapArray_onePostCheckIn.put("postGeoPoint", checkIn.getPostGeoPoint());
                        list_savePostCheckInInfo.add(mapArray_onePostCheckIn);
                    }
                    checkPostCheckInListener.data(list_savePostCheckInInfo);
                } else {
                    onError(1, "返回成功但数据不正确");
                }
            }

            @Override
            public void onError(int i, String s) {
                checkPostCheckInListener.msg(i + "+" + s);
            }
        });
    }

    public interface InsertCheckInInfoListener {
        public void msg(String msg);
    }

    public void insertCheckInInfo(String updateId, String geoPoint, boolean isExceed
            , final InsertPostCheckInListener insertPostCheckInListener) {
        ComputerCheckIn checkIn = new ComputerCheckIn();
        checkIn.setCheckInGeoPoint(geoPoint);
        checkIn.setExceed(isExceed);
        checkIn.update(mContext, updateId, new UpdateListener() {
            @Override
            public void onSuccess() {
                insertPostCheckInListener.msg("签到成功");
            }

            @Override
            public void onFailure(int i, String s) {
                insertPostCheckInListener.msg(i + "/" + s);
            }
        });
    }

    public interface UserRequestLoginListener {
        public void msg(String msg);
    }

    public interface SpecialTeacherCoursesQueryListener {
        public void Success(List<String> list);

        public void Error(String msg);
    }

    public interface SpecialCourseClassesQueryListener {
        public void Success(List<String> list_classes);

        public void Error(String msg);
    }

    public interface SpecialCourseClassesCheckInInfoQueryListener {
        public void Success(Map<String, List<Double>> mapArray_classesCheckIn);

        public void Error(String msg);
    }

    public interface SpecialClassQueryListener {
        public void Success(List<String> list);

        public void Error(String msg);
    }


}
