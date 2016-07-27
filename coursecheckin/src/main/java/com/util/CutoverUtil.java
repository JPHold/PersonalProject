package com.util;

import java.io.Serializable;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by HJP on 2016/7/4 0004.
 */

public class CutoverUtil {
    public static String listToString(List<Integer> list) {
        if (list == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (Integer i : list) {
            result.append(i);
            result.append(",");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    public static int[] stringToInts(String string, String regularExpression) {

        int ints[] = null;

        boolean isNull = CheckUtil.checkNull(string);
        if (!isNull) {
            String[] split = string.split(regularExpression);
            int length = split.length;
            ints = new int[length];

            for (int j = 0; j < length; j++) {
                int i = Integer.parseInt(split[j]);
                ints[j] = i;
            }
        }
        return ints;
    }

    //Map<Serializable,Serializable>是用不了for(key:map.keyet())
    public static List<Serializable> setToList(Map<Serializable, Serializable> data) {

        Set<Serializable> keys = data.keySet();
        Iterator<Serializable> iterator = keys.iterator();

        List<Serializable> s = new ArrayList<>();

        while (iterator.hasNext()) {
            Serializable next = iterator.next();
            s.add(next);
        }
        return s;
    }
}

