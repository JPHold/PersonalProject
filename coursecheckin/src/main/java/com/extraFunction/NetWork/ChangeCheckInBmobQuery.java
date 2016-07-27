package com.extraFunction.NetWork;

import android.content.Context;
import android.util.Log;

import com.BaseContent.CallListener;
import com.BaseContent.QueryListener;
import com.hjp.vo.ComputerCheckIn;
import com.util.CheckUtil;
import com.util.CutoverUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by HJP on 2016/6/28 0028.
 */
public class ChangeCheckInBmobQuery {

    private static final String TAG = "ChangeCheckInBmobQuery";
    private Context mContext;


    public ChangeCheckInBmobQuery(Context mContext) {
        this.mContext = mContext;
    }

    private void getCoursesFromResult(ArrayList<Serializable> list, List<ComputerCheckIn> data) {

        int size = data.size();
        ComputerCheckIn checkIn;
        String course;

        for (int i = 0; i < size; i++) {
            checkIn = data.get(i);
            course = checkIn.getCourseName();
            if (course == null || course.length() <= 0) {
                return;
            }
            list.add(course);
        }
    }

    private void getClassAndCheckInTypeFromResult(ArrayList<Serializable> list, List<ComputerCheckIn> data) {

        ComputerCheckIn checkIn = data.get(0);
        String className = checkIn.getClassName();
        boolean isExceed = checkIn.getExceed();
        boolean isComLate = checkIn.getComLate();
        boolean isLeaveEarly = checkIn.getLeaveEarly();

        list.add(className);
        /**
         *  分析是哪种签到情况
         */
        //判断是否旷课
        if (!isExceed) {
            list.add("1");
        } else {
            list.add("2");
        }
        //判断是否迟到
        if (isComLate) {
            list.add("3");
        }
        //判断是否早退
        if (isLeaveEarly) {
            list.add("4");
        }
        //释放
        data = null;
        className = null;

    }

    private ArrayList<Serializable> getCoursesFromQueryResult(int getResultType, List<ComputerCheckIn> data) {
        boolean isVain = CheckUtil.checkNullOrZero((Serializable) data);
        if (isVain) {
            return null;
        }

        ArrayList<Serializable> listArray = new ArrayList<>();

        if (getResultType == 1) {
            getCoursesFromResult(listArray, data);
        } else if (getResultType == 2) {
            getClassAndCheckInTypeFromResult(listArray, data);
        }
        //释放
        data = null;

        return listArray;
    }


    public void getCheckInedCourses(String stuNum, String teacherNum, final QueryListener queryListener) {
        BmobQuery<ComputerCheckIn> queryCourses = new BmobQuery<>();
        queryCourses.addWhereEqualTo("stuNum", stuNum);
        queryCourses.addWhereEqualTo("jobNum", teacherNum);
        queryCourses.addWhereEqualTo("needCheckIn", false);
        queryCourses.addQueryKeys("courseName");
        queryCourses.findObjects(mContext, new FindListener<ComputerCheckIn>() {
            @Override
            public void onSuccess(List<ComputerCheckIn> list) {
                if (list == null || list.size() <= 0) {
                    onError(0, "返回成功但结果为0");
                }

                ArrayList<Serializable> result = getCoursesFromQueryResult(1, list);
                queryListener.data(result);
            }

            @Override
            public void onError(int i, String s) {
                queryListener.error(s);
                return;
            }
        });
    }

    public void getCheckInedClass(String stuNum, String teacherNum, String courseName, final QueryListener queryListener) {
        BmobQuery<ComputerCheckIn> queryCourses = new BmobQuery<>();
        queryCourses.addWhereEqualTo("stuNum", stuNum);
        queryCourses.addWhereEqualTo("jobNum", teacherNum);
        queryCourses.addWhereEqualTo("courseName", courseName);
        queryCourses.addQueryKeys("className,exceed,comLate,leaveEarly");

        queryCourses.findObjects(mContext, new FindListener<ComputerCheckIn>() {
            @Override
            public void onSuccess(List<ComputerCheckIn> list) {
                if (list == null || list.size() <= 0) {
                    onError(0, "返回成功但结果为0");
                }
                //从结果提取班级,四种签到的情况正常\迟到\早退\旷课
                ArrayList<Serializable> result = getCoursesFromQueryResult(2, list);
                queryListener.data(result);
            }

            @Override
            public void onError(int i, String s) {
                queryListener.error(s);
                return;
            }
        });
    }

    public void getData(Map<String, Object> where, final String queryKey, final QueryListener queryListener) {
        Log.i(TAG, "getData: " + where + "<>" + queryKey);

        BmobQuery<ComputerCheckIn> bmobQuery = new BmobQuery<>();
        for (String key : where.keySet()) {
            Object data = where.get(key);
            Log.i(TAG, "where-键: " + key + "<数据>" + data);
            bmobQuery.addWhereEqualTo(key, data);
        }
        Log.i(TAG, "查询key: " + queryKey);
        bmobQuery.addQueryKeys(queryKey);
        bmobQuery.findObjects(mContext, new FindListener<ComputerCheckIn>() {
            @Override
            public void onSuccess(List<ComputerCheckIn> list) {
                int size = list.size();

                if (list == null || size <= 0) {
                    onError(0, "返回成功结果为0");
                    return;
                }
                List<Serializable> result = new ArrayList<>();
                ComputerCheckIn checkIn = null;

                //多个查询key
                if (queryKey.contains(",")) {
                    String[] keys = queryKey.split(",");

                    for (int i = 0; i < size; i++) {
                        checkIn = list.get(i);
                        for (String key : keys) {
                            result.add(checkIn.getData(key));
                        }
                    }
                }
                //只有一个查询key
                for (int i = 0; i < size; i++) {
                    checkIn = list.get(i);
                    result.add(checkIn.getData(queryKey));
                }
                queryListener.data(result);
            }

            @Override
            public void onError(int i, String s) {
                queryListener.error(s);
            }
        });
    }

    public void update(Map<Serializable, Serializable> data, final CallListener callListener) {
        ComputerCheckIn computerCheckIn = new ComputerCheckIn();
        int size = data.size();
        List<Serializable> keys = CutoverUtil.setToList(data);

        Serializable key = null;
        Serializable d = null;

        for (int i = 0; i < size; i++) {
            key = keys.get(i);
            d = data.get(key);
            Log.i(TAG, "update-键: " + key + "<数据>" + d);
            computerCheckIn.setData((String) key, d);
        }
        data.clear();
        computerCheckIn.update(mContext, new UpdateListener() {
            @Override
            public void onSuccess() {
                callListener.call("success");
            }

            @Override
            public void onFailure(int i, String s) {
                callListener.call(s);
            }
        });
    }

}
