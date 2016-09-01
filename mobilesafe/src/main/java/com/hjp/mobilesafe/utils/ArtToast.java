package com.hjp.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.hjp.mobilesafe.R;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class ArtToast {

    private static WindowManager mWm;
    private static WindowManager.LayoutParams mToastParams;
    private static View mArtToastLayout;

    /**
     * @param context
     * @param layout             显示toast的布局
     * @param showToastMsgViewId layout里用来显示吐司信息的view-id
     * @param showMsg            吐司信息
     */
    public static void showArtToast(Context context, int layout, int showToastMsgViewId, String showMsg) {
        initWindowManagerParams(context);
        if (mArtToastLayout != null) {
            mWm.removeView(mArtToastLayout);
        }
        mArtToastLayout = View.inflate(context, layout, null);
        TextView textV_showToastMsg = (TextView) mArtToastLayout.findViewById(showToastMsgViewId);//传递进来的显示toast信息的view-id
        //这里可对TextView做些属性设置(颜色等)
        textV_showToastMsg.setText(showMsg);
        mWm.addView(mArtToastLayout, mToastParams);
    }

    public static void cancelArtToast(Context context) {
        initWindowManagerParams(context);
        //没有显示过吐司
        if (mArtToastLayout == null) {
            return;
        }

        //取消吐司的显示
        mWm.removeView(mArtToastLayout);
    }

    private static void initWindowManagerParams(Context context) {

        if (mWm == null) {
            mWm = (WindowManager) context.getSystemService(Activity.WINDOW_SERVICE);
        }


        mToastParams = new WindowManager.LayoutParams();

        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        mToastParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mToastParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mToastParams.format = PixelFormat.TRANSLUCENT;
        mToastParams.windowAnimations = R.style.ArtToast_Animation;
        mToastParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mToastParams.setTitle("Toast");
        mToastParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
              /*  | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/;//因为需要移动这个toast，所以不设置这个flag

    }
}
