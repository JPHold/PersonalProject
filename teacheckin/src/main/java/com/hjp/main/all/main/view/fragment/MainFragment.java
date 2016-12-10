package com.hjp.main.all.main.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hjp.R;
import com.hjp.main.all.main.presenter.MainPresenter;
import com.hjp.main.all.main.view.MainView;
import com.hjp.main.all.main.view.adapter.CoursesAdapter;
import com.hjp.main.all.view.AllView;
import com.hjp.main.all.view.RelativeView;
import com.hjp.others.app.MainApplication;

import java.util.List;

/**
 * Created by HJP on 2016/12/4.
 */

public class MainFragment extends android.support.v4.app.Fragment implements MainView, com.hjp.main.all.main.view.RelativeView {

    private AllView mAllView;
    private RelativeView mRelativeView;
    private MainPresenter mMainPresenter;
    private TabLayout mTabLayoutCourses;
    private ViewPager mViewPagerClasses;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRelativeView = (RelativeView) context;
        mAllView = (AllView) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainLayout = inflater.inflate(R.layout.main, container, false);
        mTabLayoutCourses = (TabLayout) mainLayout.findViewById(R.id.main_tabLayout_courses);
        mViewPagerClasses = (ViewPager) mainLayout.findViewById(R.id.main_viewPager_classes);
        return mainLayout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //加载课程
        mMainPresenter.getCourses();
    }

    @Override
    public void startObtainCourses() {
        mRelativeView.runProgressBar();
    }

    @Override
    public String getTeaNum() {
        return mAllView.getTeaNum();
    }

    @Override
    public void errorObtainCourse(int status, String msg) {
        mRelativeView.stopProgressBar();
        Toast.makeText(MainApplication.getContext(), status + "," + msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setCourseNames(List<String> courseNums, List<String> courseNames) {
        CoursesAdapter coursesAdapter = new CoursesAdapter(mRelativeView.obtainFragmentManager()
                , courseNums, courseNames, mRelativeView.getContext());

        mViewPagerClasses.setAdapter(coursesAdapter);
        mTabLayoutCourses.setupWithViewPager(mViewPagerClasses);
    }

    private void initView() {
        mMainPresenter = new MainPresenter(this, mRelativeView.getContext());
    }

    @Override
    public void runProgressBar() {
        mRelativeView.runProgressBar();
    }

    @Override
    public void stopProgressBar() {
        mRelativeView.stopProgressBar();
    }
}
