package com.hjp.others.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by HJP on 2015/10/30 0030.
 */
public class ScreenUtils {

    public static int SCREENW;
    public static int SCREENH;
    public static float SCREENDENSITY;

    public static void initScreen(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SCREENW = metrics.widthPixels;
        SCREENH = metrics.heightPixels;
        SCREENDENSITY = metrics.density;
    }

}
