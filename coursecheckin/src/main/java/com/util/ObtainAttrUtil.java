package com.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.TypedValue;

/**
 * Created by HJP on 2016/7/8 0008.
 */

public class ObtainAttrUtil {
    public static TypedArray getSpecialTypedArray(Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        int[] attribute = new int[]{attr};
        TypedArray array = context.obtainStyledAttributes(typedValue.resourceId, attribute);
        return array;
    }
}
