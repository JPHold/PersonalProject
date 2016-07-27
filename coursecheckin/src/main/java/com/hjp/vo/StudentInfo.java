package com.hjp.vo;

import cn.bmob.v3.BmobObject;

/**
 * Created by HJP on 2016/5/28 0028.
 */
public class StudentInfo extends BmobObject {
    private String portrait;
    private String stuNum;
    private String pwd;
    private String stuName;

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
    public String getPwd() {
        return pwd;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getStuNum() {
        return stuNum;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }
}
