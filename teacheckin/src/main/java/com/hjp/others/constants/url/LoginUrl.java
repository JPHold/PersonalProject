package com.hjp.others.constants.url;

/**
 * Created by HJP on 2016/11/19.
 */

public class LoginUrl {
    /**
     *  用户登录地址和请求参数
     */
    public static final String URL_CHECKTEACHERUSER = "http://"+BaseUrl.LIIP+"/CourseCheckIn/tea/login";
    public static  final String PARAM_TEA_NUM="num";
    public static  final String PARAM_TEA_PWD="pwd";

    /*
    * {"T101":{"comeLateCheckInType":0,"studentCountInClassRoom":12,"leaveEarlyCheckInType":0,"truancyCheckInType":1,"defaultType":0},"T201":{"comeLateCheckInType":0,"studentCountInClassRoom":0,"leaveEarlyCheckInType":0,"truancyCheckInType":0,"defaultType":0}}
    * */
    //Json中班級簽到情況的返回參數
    public static final  String PARAM_STUDENTCOUNTINCLASSROOM="studentCountInClassRoom";
    public static final  String PARAM_COMELATECHECKINTYPE="comeLateCheckInType";
    public static final  String PARAM_LEAVEEARLYCHECKINTYPE="leaveEarlyCheckInType";
    public static final  String PARAM_TRUANCYCHECKINTYPE="truancyCheckInType";
    public static final  String PARAM_DEFAULTTYPE="defaultType";

}
