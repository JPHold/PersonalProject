package com.hjp.mobilesafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.Telephony;
import android.util.Xml;

import com.hjp.mobilesafe.listener.DataListener;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by HJP on 2016/9/10 0010.
 */

public class SmsBackupUtil {

    public interface DataListener {
        public void onDataCount(int count);

        public void onCurrPosition(int position);

        public void onEnd();
    }

    public static void smsBackup(final Context context, final String dir, String fileName, final DataListener dataListener) throws IOException {
        File file=new File(dir,fileName);
        if (!file.exists()) {
        file.createNewFile();//这才是真正创建文件
        }

        final FileOutputStream os = new FileOutputStream(file);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = context.getContentResolver();


                XmlSerializer serializer = Xml.newSerializer();
                try {
                    serializer.setOutput(os, "utf-8");
                    serializer.startDocument("utf-8", true);
                    serializer.startTag(null, "smss");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Cursor query = contentResolver.query(Uri.parse("content://sms"), new String[]{"address", "date", "type", "body"}, null, null, null);
                int count = query.getCount();
                dataListener.onDataCount(count);
                while (query.moveToNext()) {
                    try {
                        serializer.startTag(null, "sms");
                        serializer.startTag(null, "address");
                        String address = query.getString(query.getColumnIndex("address"));
                        serializer.text(address);
                        serializer.endTag(null, "address");

                        serializer.startTag(null, "date");
                        String date = query.getString(query.getColumnIndex("date"));
                        serializer.text(date);
                        serializer.endTag(null, "date");


                        serializer.startTag(null, "type");
                        String type = query.getString(query.getColumnIndex("type"));
                        serializer.text(type);
                        serializer.endTag(null, "type");

                        serializer.startTag(null, "body");
                        String body = query.getString(query.getColumnIndex("body"));
                        serializer.text(body);
                        serializer.endTag(null, "body");

                        serializer.endTag(null, "sms");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //报告当前进度
                    dataListener.onCurrPosition(query.getPosition());
                    //是否备份完
                    if (query.isLast()) {
                        dataListener.onEnd();
                    }

                }

                try {
                    serializer.endTag(null, "smss");
                    serializer.endDocument();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                query.close();
            }
        }).start();

    }
}
