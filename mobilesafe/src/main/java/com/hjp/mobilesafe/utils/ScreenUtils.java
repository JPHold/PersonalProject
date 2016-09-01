package com.hjp.mobilesafe.utils;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by HJP on 2015/10/30 0030.
 */
public class ScreenUtils {

    private static int SCREENW;
    private static int SCREENH;
    private static float SCREENDENSITY;

    public static void initScreen(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        SCREENW = metrics.widthPixels;
        SCREENH = metrics.heightPixels;
        SCREENDENSITY = metrics.density;
    }

    public static int getScreenW() {
        return SCREENW;
    }

    public static int getScreenH() {
        return SCREENH;
    }

    public static float getScreenDensity() {
        return SCREENDENSITY;
    }
}
