package com.hjp.mobilesafe.utils;

import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;

/**
 * Created by HJP on 2016/9/13 0013.
 */

public class RomAvailSpaceUtil {
    public static String getAvailSpace(Context context,String path){
        StatFs statFs=new StatFs(path);
        //获得多少块可用空间
        long availSpaceCount = statFs.getAvailableBlocks();
        //获得每块空间的大小(字节)
        int blockSize = statFs.getBlockSize();
        String s = Formatter.formatFileSize(context, blockSize * availSpaceCount);
        return s;
    }
}
