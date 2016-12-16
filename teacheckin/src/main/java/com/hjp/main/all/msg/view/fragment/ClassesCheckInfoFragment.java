package com.hjp.main.all.msg.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.hjp.R;
import com.hjp.main.all.msg.presenter.ClassPresenter;
import com.hjp.main.all.msg.view.ClassView;
import com.hjp.main.all.main.view.fragment.base.BaseFragment;
import com.hjp.main.all.view.RelativeView;
import com.hjp.others.app.MainApplication;

/**
 * Created by HJP on 2016/12/5.
 */

public class ClassesCheckInfoFragment extends BaseFragment implements ClassView {

    private RelativeView mRelativeView;
    private String mCurrCourseId;
    private ClassPresenter mClasspresenter;
    private ProgressBar mProgressBar;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mCurrCourseId = (String) args.get("courseId");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRelativeView = (RelativeView) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
//        mProgressBar = (ProgressBar) fragmentLayout.findViewById(R.id.pull_to_refresh_load_progress);
    }

    @Override
    public void doThing() {
        mClasspresenter.getClassesCheckInfo(mCurrCourseId);
    }

    @Override
    public void startObtainClassCheckInInfo() {
        mRelativeView.runProgressBar();
    }

    @Override
    public void errorObtainClassCheckInInfo(int status,String msg) {
        Toast.makeText(mContext,status+","+msg,Toast.LENGTH_LONG).show();
        mRelativeView.stopProgressBar();
    }

    private void initView() {
        mClasspresenter = new ClassPresenter(this, MainApplication.getContext());
    }

}
