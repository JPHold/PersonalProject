package com.hjp.vo;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobGeoPoint;

/**
 * Created by HJP on 2016/5/17 0017.
 */
public class ComputerCheckIn extends BmobObject {

    public static final String KEY_OBJECTID = "objectId";
    public static final String KEY_JOBNUM = "jobNum";
    public static final String KEY_STUNUM = "stuNum";
    public static final String KEY_COURSENAME = "courseName";
    public static final String KEY_CLASSNAME = "className";
    public static final String KEY_NEEDCHECKIN = "needCheckIn";
    public static final String KEY_EXCEED = "exceed";
    public static final String KEY_COMLATE = "comLate";
    public static final String KEY_LEAVEEARLY = "leaveEarly";
    public static final String KEY_CHECKINPOINT = "checkInGeoPoint";
    public static final String KEY_POSTGEOPOINT = "postGeoPoint";
    public static final String KEY_POSTTIME = "postTime";
    public static final String KEY_EXCEEDTIME = "exceedTime";

    private String jobNum;
    private String stuNum;
    private String courseName;
    private String className;
    private Boolean needCheckIn;
    private Boolean exceed;
    private Boolean comLate;
    private Boolean leaveEarly;
  private String checkInGeoPoint;
    private String postGeoPoint;
    private String postTime;
    private String exceedTime;

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }

    public String getStuNum() {
        return stuNum;
    }

    public void setStuNum(String stuNum) {
        this.stuNum = stuNum;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Boolean getExceed() {
        return exceed;
    }

    public void setExceed(Boolean exceed) {
        this.exceed = exceed;
    }

    public Boolean getNeedCheckIn() {
        return needCheckIn;
    }

    public void setNeedCheckIn(Boolean needCheckIn) {
        this.needCheckIn = needCheckIn;
    }


    public Boolean getComLate() {
        return comLate;
    }

    public void setComLate(Boolean comLate) {
        this.comLate = comLate;
    }

    public Boolean getLeaveEarly() {
        return leaveEarly;
    }

    public void setLeaveEarly(Boolean leaveEarly) {
        this.leaveEarly = leaveEarly;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getExceedTime() {
        return exceedTime;
    }

    public void setExceedTime(String exceedTime) {
        this.exceedTime = exceedTime;
    }


    //根据key获取不同值
    public Serializable getData(String key) {
        Serializable s = null;
        switch (key) {
            case KEY_OBJECTID:
                s = getObjectId();
                break;
            case KEY_JOBNUM:
                s = getJobNum();
                break;
            case KEY_STUNUM:
                s = getStuNum();
                break;
            case KEY_COURSENAME:
                s = getCourseName();
                break;
            case KEY_CLASSNAME:
                s = getClassName();
                break;
            case KEY_NEEDCHECKIN:
                s = getNeedCheckIn();
                break;
            case KEY_EXCEED:
                s = getExceed();
                break;
            case KEY_COMLATE:
                s = getComLate();
                break;
            case KEY_LEAVEEARLY:
                s = getLeaveEarly();
                break;
            case KEY_CHECKINPOINT:
                s = getCheckInGeoPoint();
                break;
            case "postGeoPoint":
                s = getPostGeoPoint();
                break;
            case KEY_POSTTIME:
                s = getPostTime();
                break;
            case KEY_EXCEEDTIME:
                s = getExceedTime();
                break;
        }
        return s;
    }

    public void setData(String key, Serializable data) {
        Serializable s = null;
        switch (key) {
            case KEY_OBJECTID:
                setObjectId((String) data);
                break;
            case "jobNum":
                setJobNum((String) data);
                break;
            case "stuNum":
                setStuNum((String) data);
                break;
            case "courseName":
                setCourseName((String) data);
                break;
            case "className":
                setClassName((String) data);
                break;
            case "needCheckIn":
                setNeedCheckIn((Boolean) data);
                break;
            case "exceed":
                setExceed((Boolean) data);
                break;
            case "comLate":
                setComLate((Boolean) data);
                break;
            case "leaveEarly":
                setLeaveEarly((Boolean) data);
                break;
            case "checkInPoint":
                setCheckInGeoPoint((String) data);
                break;
            case "postGeoPoint":
                setPostGeoPoint((String) data);
                break;
            case "postTime":
                setPostTime((String) data);
                break;
            case "exceedTime":
                setExceedTime((String) data);
                break;
        }
    }

    public String getCheckInGeoPoint() {
        return checkInGeoPoint;
    }

    public void setCheckInGeoPoint(String checkInGeoPoint) {
        this.checkInGeoPoint = checkInGeoPoint;
    }

    public String getPostGeoPoint() {
        return postGeoPoint;
    }

    public void setPostGeoPoint(String postGeoPoint) {
        this.postGeoPoint = postGeoPoint;
    }
}
