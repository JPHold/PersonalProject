package com.hjp.others.util;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hjp.R;

/**
 * Created by HJP on 2016/7/5 0005.
 */

public class PopupUtil {
    /**
     * @param context
     * @param hangView 依靠view
     */
    public static PopupWindow showPopup(Context context, View hangView, ViewGroup contentView,
                                        int width, int height, int xoff, int yoff) {
        PopupWindow ppWindow = new PopupWindow(contentView, width, height, true);
        ppWindow.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.night_extraFunctionBackColor)));
        ppWindow.setAnimationStyle(R.anim.scale_ppwindow);
        ppWindow.showAsDropDown(hangView, xoff, yoff);
        return ppWindow;
    }
}
