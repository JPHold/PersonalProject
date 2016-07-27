package com.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by HJP on 2016/7/2 0002.
 */

public class ViewUtil {

    private static Context mContext;
    private static Resources resources;

    public ViewUtil(Context context) {
        mContext = context;
    }

    public static void setBackColor(View view, int color) {
        if (resources == null) {
            resources = mContext.getResources();
        }
        view.setBackgroundColor(resources.getColor(color));
    }

    public static void setBackImage(View view, int image) {
        if (resources == null) {
            resources = mContext.getResources();
        }
        view.setBackgroundResource(image);
    }
}
