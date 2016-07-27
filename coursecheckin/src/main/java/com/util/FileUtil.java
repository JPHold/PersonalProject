package com.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by HJP on 2016/6/1 0001.
 */
public class FileUtil  {
    public  static void writeLogInFile(String log,String path){
        File file_log=new File(path);
        try {
            if (!file_log.createNewFile()){
                FileOutputStream outputStream=new FileOutputStream(file_log);

                byte[] bytes_oneLog = log.getBytes();
                outputStream.write(bytes_oneLog,0,bytes_oneLog.length);
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        }
    }
}
