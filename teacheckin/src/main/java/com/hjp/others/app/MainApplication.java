package com.hjp.others.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.hjp.others.app.db.AppConfig;
import com.hjp.others.util.CheckUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HJP on 2016/5/16 0016.
 */
public class MainApplication extends Application {

    private static MainApplication mContext;
    private static String currUser;
    public static AppConfig mAppConfig;
    public static List<Activity> mActivityTaskList;
    public static List<String> mSyncCheckInList;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext =this;
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        ImageLoader.getInstance().init(config);
        mAppConfig = new AppConfig(mContext);
        mActivityTaskList = new ArrayList<>();
        mSyncCheckInList=new ArrayList<>();
    }

    public static String getCurrUser() {
        return currUser;
    }

    public void setCurrUser(String currUser) {
        this.currUser = currUser;
    }

    public AppConfig getAppConfig() {
        Object o = CheckUtil.checkNull(mContext, mAppConfig);
        return (AppConfig) o;
    }

    public void addActivityTask(Activity activity) {
        mActivityTaskList.add(activity);
    }

    public void finishActivity() {
        int size = mActivityTaskList.size();
        for (int i = 0; i < size; i++) {
            Activity activity = mActivityTaskList.get(i);
            if (null != activity) {
                activity.finish();
            }
        }
    }

    public void addCheckIn(String data) {
        mSyncCheckInList.add(data);
    }
    public int  getCheckInCount(){
        if (mSyncCheckInList!=null) {
            return mSyncCheckInList.size();
        }
        return -1;
    }

    public void setTheme(int resid) {
        super.setTheme(resid);
    }

    public  static Context  getContext(){
        return mContext;
    }


}
