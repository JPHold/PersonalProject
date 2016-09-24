package com.hjp.mobilesafe.utils;

import android.content.Context;
import android.text.format.Formatter;

import java.io.File;

/**
 * Created by HJP on 2016/9/15 0015.
 */

public class ObtainFileSize {

    /**
     * 获得文件大小,格式:XXX MB
     *
     * @param path
     */
    public static String getFileSize(Context context, String filePath) {
        File file = new File(filePath);
        String size = null;
        size = Formatter.formatFileSize(context, file.length());
        return size;
    }

    /**
     * 获得文件夹大小
     *
     * @param dirPath
     */
    public static void getDirSize(String dirPath) {

    }
}
