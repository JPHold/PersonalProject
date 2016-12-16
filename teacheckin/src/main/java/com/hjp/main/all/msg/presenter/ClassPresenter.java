package com.hjp.main.all.msg.presenter;

import android.content.Context;

import com.hjp.main.all.main.service.ClassService;
import com.hjp.main.all.main.service.impl.ClassServiceImpl;
import com.hjp.main.all.msg.service.listener.OnClassesCheckInInfoObtainListener;
import com.hjp.main.all.msg.view.ClassView;

/**
 * Created by HJP on 2016/12/5.
 */

public class ClassPresenter {

    private ClassView mClassView;
    private ClassService mClassService;
    private Context mContext;

    public ClassPresenter(ClassView mClassView, Context mContext) {
        this.mClassView = mClassView;
        this.mContext = mContext;
        mClassService=new ClassServiceImpl();
    }

    public void getClassesCheckInfo(String currCourseId){
      mClassService.getClassesCheckInInfo(currCourseId, new OnClassesCheckInInfoObtainListener() {
          @Override
          public void onSuccess() {

          }

          @Override
          public void onError(int status, String msg) {

          }
      });
    }
}
