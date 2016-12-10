package com.hjp.others.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by HJP on 2016/7/1 0001.
 */

public class SendInModuleUtil {
    public static void sendMsgByHandler(int what, Object data, Handler handler) {
        Message.obtain(handler, what, data).sendToTarget();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
