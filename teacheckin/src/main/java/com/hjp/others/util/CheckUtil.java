package com.hjp.others.util;

import android.content.Context;

import com.hjp.others.app.db.AppConfig;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by HJP on 2016/6/28 0028.
 */
public class CheckUtil {

    /**
     * 字符串判断。没有分隔符则regularExpression为null
     */
    public static boolean checkNull(String s, String regularExpression) {
        if (null == s && "".equals(s)) {
            return true;
        }
        if (regularExpression != null) {
            if (!s.contains(regularExpression))
                return true;
        }
        return false;
    }

    /**
     * 检查类
     */
    public static Object checkNull(Context context, Object o) {
        if (o == null) {
            if (o instanceof AppConfig) {
                o = new AppConfig(context);
            }
        }
        return o;
    }

    public static boolean checkNull(Serializable s) {
        if (s == null) {
            return true;
        }
        return false;
    }

    public static boolean checkNullOrZero(Serializable s) {
        boolean isVain;
        isVain = checkNull(s);

        if (!isVain) {
            int size = 0;
            if (s instanceof List) {
                size = ((List) s).size();
            } else if (s instanceof Map) {
                size = ((Map) s).size();
            }
            if (size == 0) {
                isVain = true;
            }
        }
        return isVain;
    }

    public static int checkNullOrZero(int type, Serializable s) {
        boolean b = checkNullOrZero(s);
        return b == true ? 0 : type;
    }

}
