package com.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.hjp.Application.MainApplication;
import com.hjp.coursecheckin.R;

/**
 * Created by HJP on 2016/7/7 0007.
 */

public class DialogUtil {
    private static String TAG = "dialog";
    private static Activity mActivity;

    public static AlertDialog showDialog(Activity activity, int themeId, int title,
                                         int neutralBtnTextId, int negativeBtnTextId) {
        mActivity = activity;
        AlertDialog dialog =
                new AlertDialog.Builder(activity, themeId)
                        .setTitle(title)
                        .setNeutralButton(neutralBtnTextId, clickListener)
                        .setNegativeButton(negativeBtnTextId, clickListener)
                        .show();
        return dialog;
    }

    private static DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == -3) {
                //当前activity退出
//                System.exit(0);
                //整体app退出
                MainApplication app = (MainApplication) mActivity.getApplication();
                app.finishActivity();
            }
        }
    };
}
