package com.hjp.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.hjp.Fragment.EachCourseCheckInInfoFragment;

import java.util.List;

/**
 * Created by HJP on 2016/5/16 0016.
 */
public class CoursesAdapter extends FragmentPagerAdapter {

    /**
     * 存放着当前老师的所有课程名,根据这个课程名查找所此课程所有班级的签到情况
     */
    private final List<String> courseNames;
    private final Context mContext;

    public CoursesAdapter(FragmentManager fm, List<String> courseNames, Context context) {
        super(fm);
        this.courseNames = courseNames;
        this.mContext = context;
    }
    @Override
    public int getCount() {
        return courseNames.size();
    }

    @Override
    public Fragment getItem(int position) {
        EachCourseCheckInInfoFragment eachCourseCheckInInfoFragment = new EachCourseCheckInInfoFragment();
        Bundle bundle_saveCourseName = new Bundle();
        Log.i("test", "getItem: " + getPageTitle(position).toString()+"实例"+eachCourseCheckInInfoFragment);
        bundle_saveCourseName.putString("courseName", getPageTitle(position).toString());
        eachCourseCheckInInfoFragment.setArguments(bundle_saveCourseName);
        return eachCourseCheckInInfoFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (!(position < 0) && !(position > courseNames.size()))
            return courseNames.get(position);
        return "";
    }
}
