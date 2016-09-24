package com.hjp.mobilesafe.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by HJP on 2016/9/15 0015.
 */

public class RamSpaceUtil {

    private static ActivityManager am;
    private static ActivityManager.MemoryInfo mi;


    /**
     * 得到运行内存的总大小
     */
    public static String getTotalSpace(Context context) {
        String totalMem = null;
        if (Build.VERSION.SDK_INT > 16) {
            getMemInfo(context);
            long totalMemByte = mi.totalMem;//只能在16以上
            totalMem = Formatter.formatFileSize(context, totalMemByte);
        } else {
            //在16以下获取运行内存的总大小
            //在机身内存的/proc/meminfo
            String totalMemInfo;
            BufferedReader br = null;
            try {
                File file = new File("/proc/meminfo");
                FileInputStream is = new FileInputStream(file);
                br = new BufferedReader(new InputStreamReader(is));

                totalMemInfo = br.readLine();

                StringBuilder sb = new StringBuilder();
                //totalMemInfo是MemTotal：       XXXXXX KB，所以需要提取出XXXXX(数字)
                for (char c : totalMemInfo.toCharArray()) {
                    if (c >= '0' && c <= '9') {
                        sb.append(c);
                    }
                }
                long totalMemByte = (Integer.valueOf(sb.toString())) * 1024;//Integer.valueOf(sb.toString())得到的是XXXXKB,转化为字节,再通过Formater转化
                totalMem = Formatter.formatFileSize(context, totalMemByte);

            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return totalMem;
    }

    /**
     * 获得运行内存的剩余大小
     */

    public static String getRamFreeSpace(Context context) {
        String availMem = null;

        getMemInfo(context);
        long availMemByte = mi.availMem;
        availMem = Formatter.formatFileSize(context, availMemByte);
        return availMem;
    }

    private static void getActivityManager(Context context) {
        if (am == null) {
            am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
    }

    private static void getMemInfo(Context context) {
        getActivityManager(context);
        mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
    }
}
