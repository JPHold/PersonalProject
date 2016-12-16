package com.hjp.main.all.presenter;

import android.content.Context;

import com.hjp.main.all.service.AllService;
import com.hjp.main.all.service.bean.ClassRoom;
import com.hjp.main.all.service.bean.Course;
import com.hjp.main.all.service.bean.CoursePart;
import com.hjp.main.all.service.bean.WeekNum;
import com.hjp.main.all.service.impl.AllServiceImpl;
import com.hjp.main.all.service.listener.OnMakeCheckInInfosListener;
import com.hjp.main.all.service.listener.OnMakeCheckInListener;
import com.hjp.main.all.view.AllView;
import com.hjp.others.util.SecureUtil;

import java.util.ArrayList;
import java.util.Map;

import static com.hjp.main.gesture.view.activity.GestureLockActivity.KEYSTORE_FILE;
import static com.hjp.main.gesture.view.activity.GestureLockActivity.KEYSTORE_PASSWORD;

/**
 * Created by HJP on 2016/12/4.
 */

public class AllPresenter {

    public AllView mAllView;
    private Context mContext;
    private AllService mAllService;

    public AllPresenter(AllView allView, Context context) {
        this.mAllView = allView;
        this.mContext = context;
        mAllService = new AllServiceImpl();
    }

    public void dissectionKeyPwd() {
        if (mAllView.getGestureState()) {
            //解密
            SecureUtil.dissection(mContext, "PKCS12", KEYSTORE_FILE, KEYSTORE_PASSWORD, mAllView.getKeyGesturePassword(), "ISO-8859-1",
                    new SecureUtil.OnResultListener() {
                        @Override
                        public void onResult(String password) {
                            mAllView.setKeyPwd2GestureView(password);
                        }

                        @Override
                        public void onError(String data) {

                        }
                    });
        }
    }

    public void makeCheckInInfos(String courseId, Map<String, Map<String, Object>> makeCheckInInfoMap) {

        Map<String, Object> makeCheckInInfo = makeCheckInInfoMap.get(courseId);

        //提取课室
        ArrayList<ClassRoom> classRoomsData = (ArrayList<ClassRoom>) makeCheckInInfo.get("classroomname");
        ArrayList<ClassRoom> classRooms = new ArrayList<>();
        for (ClassRoom classRoom : classRoomsData) {
            classRooms.add(new ClassRoom(classRoom.getId(), classRoom.getName()));
        }

        //提取周数
        WeekNum weekNumData = (WeekNum) makeCheckInInfo.get("weeknums");


        int start = weekNumData.getStart();
        int end = weekNumData.getEnd();
        int classRoomSize = classRooms.size();

        ArrayList<ArrayList<String>> weekNums = new ArrayList<>();

        for (int i = 0; i < classRoomSize; i++) {
            ArrayList<String> weekNum = new ArrayList<>();
            for (int j = start; j <= end; j++) {
                weekNum.add("第" + j + "周");
            }
            weekNums.add(weekNum);
        }


        //提取节数
        ArrayList<CoursePart> coursePartsData = (ArrayList<CoursePart>) makeCheckInInfo.get("courseparts");

        ArrayList<ArrayList<ArrayList<CoursePart>>> courseParts = new ArrayList<>();
        for (int i = 0; i < classRoomSize; i++) {
            ArrayList<ArrayList<CoursePart>> parts = new ArrayList<>();
            int weekNumsSize = weekNums.get(i).size();
            for (int j = 0; j < weekNumsSize; j++) {
                parts.add(coursePartsData);
            }

            courseParts.add(parts);
        }

        mAllView.setMakeCheckInInfos(courseId, classRooms, weekNums, courseParts);
    }

    public void getMakeCheckInInfos() {
        mAllService.getMakeCheckInInfos(mAllView.getTeaNum(), mAllView.getDate(), new OnMakeCheckInInfosListener() {
            @Override
            public void onSuccess(final ArrayList<Course> courseNames, final Map<String, Map<String, Object>> makeCheckInInfoMap) {
                mAllView.setMakeCheckInCourses(courseNames, makeCheckInInfoMap);
            }

            @Override
            public void onError(int status, String msg) {
            }
        });
    }

    public void makeCheckIn(String courseId, Integer classRoomId, String weekNum, Integer partId) {

        mAllService.makeCheckIn(courseId, classRoomId, weekNum, partId, new OnMakeCheckInListener() {
            @Override
            public void onSuccess() {
                mAllView.successMakeCheckIn();
            }

            @Override
            public void onError(int status, String msg) {
                mAllView.errorMakeCheckIn(status, msg);
            }
        });
    }

}
