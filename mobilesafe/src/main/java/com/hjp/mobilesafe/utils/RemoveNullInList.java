package com.hjp.mobilesafe.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by HJP on 2016/9/21.
 */

public class RemoveNullInList {
    public static Collection<?> removeNullInList(Collection<?> collection) {

        Collection<?> c = collection;

        if (c == null) {
            return null;
        }
        Iterator<?> iterator = c.iterator();
        while (iterator.hasNext()) {
            Object o = iterator.next();
            if (null == o || "".equals(o + "")) {
                iterator.remove();
            }
        }
        return c;
    }
}
