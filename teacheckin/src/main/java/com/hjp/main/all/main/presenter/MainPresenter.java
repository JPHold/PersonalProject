package com.hjp.main.all.main.presenter;

import android.content.Context;

import com.hjp.main.all.main.service.MainService;
import com.hjp.main.all.main.service.impl.MainServiceImpl;
import com.hjp.main.all.main.service.listener.OnCoursesObtainListener;
import com.hjp.main.all.main.view.MainView;

import java.util.List;

/**
 * Created by HJP on 2016/12/5.
 */

public class MainPresenter{

    private MainView mMainView;
    private MainService mMainService;
    private Context mContext;

    public MainPresenter(MainView mainView,Context context) {
        this.mMainView = mainView;
        mMainService=new MainServiceImpl();
        this.mContext = context;
    }

    public void getCourses(){
        mMainService.getCourses(mMainView.getTeaNum(), mMainView.getDate(),new OnCoursesObtainListener() {
            @Override
            public void onSuccess(List<String> courseNums,List<String> courseNames) {
                mMainView.setCourseNames(courseNums,courseNames);
            }

            @Override
            public void onError(int status, String msg) {
                mMainView.errorObtainCourse(status,msg);
            }
        });
    }



}
