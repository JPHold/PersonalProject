package com.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

/**
 * Created by HJP on 2016/7/1 0001.
 */

public class ProgressBarUtil {

    private static ProgressBar progressBar;
    private static ViewGroup layout;

    private static void addProgressBarInRelativeL(RelativeLayout relativeL, ProgressBar progressBar) {

    }

    private static void addProgressBarInLinearL(LinearLayout linearL, ProgressBar progressBar) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        progressBar.setLayoutParams(layoutParams);
        linearL.addView(progressBar);
    }


    public static void showProgressBar(Context context, ViewGroup viewGroup) {
        layout = viewGroup;
        progressBar = new ProgressBar(context, null, android.R.style.Widget_Material_Light_ProgressBar_Small);
        //不知是什么
        progressBar.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        progressBar.setVisibility(View.VISIBLE);

        if (viewGroup instanceof LinearLayout) {
            addProgressBarInLinearL(((LinearLayout) viewGroup), progressBar);
            return;
        }
        addProgressBarInRelativeL(((RelativeLayout) viewGroup), progressBar);
    }

    public static void removeProgressBar() {
        if (progressBar != null) {
            layout.removeView(progressBar);
        }
    }
}
