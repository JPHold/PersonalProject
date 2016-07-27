package com.CustomView;

import cn.bmob.v3.BmobQuery;

/**
 * Created by HJP on 2016/6/15 0015.
 */
public class StudentBmobQuery<ComputerCheckIn> extends BmobQuery<ComputerCheckIn> {
    private int mCount=-1;

    public void setSpecialStudentCount(int count) {
        this.mCount = count;
    }

    public int getSpecialStudentCount() {
        return mCount;
    }
}
