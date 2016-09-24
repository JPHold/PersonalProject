package com.hjp.mobilesafe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
     * 号码来源view的被按下时开始点的横纵坐标
     */
    private static int phoneSourceView_downPointX;
    private static int phoneSourceView_downPointY;
    /**
     * 号码来源view的被拖动时结束点的横纵坐标
     */
    private static int phoneSourceView_endPointX;
    private static int phoneSourceView_endPointY;
    /**
     * 按下开始点和拖动结束点的相差距离
     */
    private static int distance_phoneSource_downNendMotionX;
    private static int distance_phoneSource_downNendMotionY;


    /**
     * @param context
     * @param layout             显示toast的布局
     * @param showToastMsgViewId layout里用来显示吐司信息的view-id
     * @param showMsg            吐司信息
     */
    public static void showArtToast(Context context, int layout, int showToastMsgViewId, String showMsg, int bgColor, int textShowColor) {
        initWindowManagerParams(context);
        if (mArtToastLayout != null) {
            mWm.removeView(mArtToastLayout);
        }

        Resources resources = context.getResources();

        mArtToastLayout = View.inflate(context, layout, null);
        mArtToastLayout.setOnTouchListener(mTouchListener);

        mArtToastLayout.setBackgroundColor(resources.getColor(bgColor));
        TextView textV_showToastMsg = (TextView) mArtToastLayout.findViewById(showToastMsgViewId);//传递进来的显示toast信息的view-id
        //这里可对TextView做些属性设置(颜色等)
        textV_showToastMsg.setTextColor(resources.getColor(textShowColor));
        textV_showToastMsg.setText(showMsg);
        mWm.addView(mArtToastLayout, mToastParams);
    }

    public static void cancelArtToast(Context context) {
        //没有显示过吐司
        if (mArtToastLayout == null) {
            return;
        }

        if (mWm != null) {
            //取消吐司的显示
            mWm.removeView(mArtToastLayout);
            mArtToastLayout = null;
        }
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

    private static String TAG = "ArtToast";
    private static View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    phoneSourceView_downPointX = (int) event.getRawX();
                    phoneSourceView_downPointY = (int) event.getRawY();
                    Log.i(TAG, "onTouch:ACTION_DOWN " + phoneSourceView_downPointX + "," + phoneSourceView_downPointY);
                    break;
                case MotionEvent.ACTION_MOVE:
                    phoneSourceView_endPointX = (int) event.getRawX();
                    phoneSourceView_endPointY = (int) event.getRawY();

                    Log.i(TAG, "onTouch:ACTION_MOVE " + phoneSourceView_endPointX + "," + phoneSourceView_endPointY);

                    distance_phoneSource_downNendMotionX = phoneSourceView_endPointX - phoneSourceView_downPointX;
                    distance_phoneSource_downNendMotionY = phoneSourceView_endPointY - phoneSourceView_downPointY;

                    Log.i(TAG, "onTouch:DISTANCE" + distance_phoneSource_downNendMotionX + "," + distance_phoneSource_downNendMotionY);

                    mToastParams.x = mToastParams.x + distance_phoneSource_downNendMotionX;
                    mToastParams.y = mToastParams.y + distance_phoneSource_downNendMotionY;

                    //防止将吐司拖出屏幕
                    //更新号码来源-view的位置
                    mWm.updateViewLayout(mArtToastLayout, mToastParams);

                    phoneSourceView_downPointX = phoneSourceView_endPointX;
                    phoneSourceView_downPointY = phoneSourceView_endPointY;
                    break;
            }

            return true;
        }
    };
}
