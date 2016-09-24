package com.hjp.mobilesafe.constant;

import com.hjp.mobilesafe.R;

/**
 * Created by HJP on 2016/8/19 0019.
 */

public class Constant {
    /**
     * 存放主页面的功能列表的图片
     */
    public static Integer[] main_function_img = new Integer[]{
            R.mipmap.main_function_1, R.mipmap.main_function_1, R.mipmap.main_function_1
            , R.mipmap.main_function_1, R.mipmap.main_function_1, R.mipmap.main_function_1
            , R.mipmap.main_function_1, R.mipmap.main_function_1, R.mipmap.main_function_1
    };

    /**
     * 默认sqlite数据库-config：存放app的参数
     */
    public static final String NAME_APPCONFIG="config";
    //防盗密码的sqlite-key
    public static final String KEY_GUARDPWD="guardPwd";
    //是否做过防盗密码引导设置，sqlite-key
    public static  final String KEY_ISGUIDED="isGuided";
    //是否开了手机防盗
    public static  final String KEY_ISOPENGUARD="isOpenGuard";
    //是否绑定Sim卡,sqlite-key
    public static final String KEY_ISBANDSIM="isBandSim";
    //sim的唯一标识
    public static final String KEY_STRING_SIMUNIQUESIGN="simUniqueSign";
    //sim是否变更
    public static final String KEY_ISSIMCHANGE="isSimChange";
    //安全号码
    public static final String KEY_INTSAFENUM="safenum";

    /**
     * 设置中心
     */
    //是否开启电话监听
    public static  final String KEY_ISPHONECALLOPEN="isPhoneCallOpen";
    //号码来源的吐司颜色
    public static final String KEY_STRINGPHONESOURCECOLOR="phoneSourceColor";


    /**
     * 数据库名、数据表名
     */
    //号码黑名单数据库：数据库名
    public static final String  DATABASE_BALCKLIST_NAME="phoneBlackList";
    //号码黑名单数据库：数据表名
    public static final String  DATATABLE_BALCKLIST_NAME="phoneBlackList";

    /**
     * 数据表的字段
     */
    //号码黑名单数据表
    public static final String KEY_BLACKLIST_ID="_id";
    public static final String KEY_BLACKLIST_PHONE="phonenumber";
    public static final String  KEY_BLACKLIST_HOLDMODE="holdmode";

}
