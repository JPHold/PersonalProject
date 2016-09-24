package com.hjp.mobilesafe.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SQLiteDbManager {
/*    //数据库存储路径
    String filePath = "data/data/com.zwinsoft.voice/Voice_2.db";
    // 数据库存放的文件夹 data/data/com.zwinsoft.voice 下面
    String pathStr = "data/data/com.zwinsoft.voice";*/

    SQLiteDatabase database;

    public static SQLiteDatabase openDatabase(Context context, String path, String dbName) {
        File dbPath = new File(path + "/" + dbName);
        // 查看数据库文件是否存在
        if (dbPath.exists()) {
            // 存在则直接返回打开的数据库
            return SQLiteDatabase.openOrCreateDatabase(dbPath, null);
        } else {
            // 不存在先创建文件夹
            File file_path = new File(path);
            file_path.mkdir();

            InputStream is = null;
            FileOutputStream fos = null;
            try {
                // 得到资源
                AssetManager am = context.getAssets();
                // 得到数据库的输入流
                is = am.open(dbName);
                // 用输出流写到SDcard上面
                fos = new FileOutputStream(dbPath);

                // 创建byte数组 用于1KB写一次
                byte[] buffer = new byte[1024];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                if (fos != null) {
                    fos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            // 如果没有这个数据库 我们已经把他写到SD卡上了，然后在执行一次这个方法 就可以返回数据库了
            return openDatabase(context, path, dbName);
        }
    }
}