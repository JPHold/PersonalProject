package com.hjp.mobilesafe.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by HJP on 2016/7/5 0005.
 */

public class PopupUtil {
    /**
     * @param context
     * @param hangView 依靠view
     */
    public static PopupWindow showPopup(Context context, View hangView, View contentView,
                                        int width, int height, int xoff, int yoff) {
        PopupWindow ppWindow = new PopupWindow(contentView, width, height, true);
        ppWindow.setBackgroundDrawable(new ColorDrawable(Color.GRAY));
//        ppWindow.showAsDropDown(hangView, xoff, yoff);

        ppWindow.showAtLocation(hangView, Gravity.LEFT + Gravity.TOP, xoff, yoff);

        return ppWindow;
    }
}
