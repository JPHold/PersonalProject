package com.hjp.mobilesafe.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.utils.AppConfig;
import com.hjp.mobilesafe.utils.ModifyOffset;

/**
 * Created by HJP on 2016/8/22 0022.
 */

public class GuardGpsObtainService extends Service {

    private LocationManager mLcManager;
    private LocationListener mLocationListener;
    private String TAG = "GuardGpsObtainService";

    @Override
    public void onCreate() {
        super.onCreate();

        //获取安全号码
        final String safeNum = (String) AppConfig.obtainFromSqlite(Constant.KEY_INTSAFENUM);

        mLcManager = (LocationManager) getSystemService(Activity.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            //地理位置回调
            @Override
            public void onLocationChanged(Location location) {
                //获得的地理位置，都是经过国家做加偏操作了(百度:火星坐标系统),需要将转为正确无偏的坐标
                String location_theft = null;
                try {
                    ModifyOffset modifyOffset = ModifyOffset.getInstance(this.getClass().getResourceAsStream("axisoffset.dat"));
                    ModifyOffset.PointDouble pointDouble_true = modifyOffset.s2c(modifyOffset.new PointDouble(location.getLongitude()
                            , location.getAltitude()));
                    location_theft = pointDouble_true.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.i(TAG, "onLocationChanged:地址 " + location_theft);
                SmsManager.getDefault().sendTextMessage("5554", null, location_theft, null, null);//测试而已

                //先检测location权限是否被允许了，这样可以避免安全错误
                boolean isGranted = checkPermission(mLcManager);
                if (isGranted) {
                    mLcManager.removeUpdates(mLocationListener);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //gps没开启或者gps的提供方没响应
                if (status == LocationProvider.OUT_OF_SERVICE || status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                    Log.i(TAG, "onStatusChanged: gps没开启或者gps的提供方没响应");
                    SmsManager.getDefault().sendTextMessage("5554", null, "无法获知地理位置", null, null);//测试而已
                }
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                SmsManager.getDefault().sendTextMessage("5554", null, "无法获知地理位置", null, null);//测试而已
            }
        };

        //先检测location权限是否被允许了，这样可以避免安全错误
        boolean isGranted = checkPermission(mLcManager);

        if (isGranted) {
            //得到最佳的位置提供方
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);//精度最佳
            criteria.setAltitudeRequired(false);//无需海拔
            criteria.setBearingRequired(false);//无需方位
            criteria.setPowerRequirement(Criteria.POWER_LOW);//电量要求
            criteria.setCostAllowed(true);//是否付费
            String bestProvider = mLcManager.getBestProvider(criteria, true);
            Log.i(TAG, "onCreate: 提供方" + bestProvider);
            //注册地理位置监听器
            mLcManager.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);
        }

        //发给安全号码：无法获知地理位置
        SmsManager.getDefault().sendTextMessage("5554", null, "无法获知地理位置", null, null);//测试而已
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //先检测location权限是否被允许了，这样可以避免安全错误
        boolean isGranted = checkPermission(mLcManager);
        if (isGranted) {
            mLcManager.removeUpdates(mLocationListener);
        }
        mLocationListener = null;
        mLcManager = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 检测权限，确保安全错误的避免
     */
    public boolean checkPermission(LocationManager locationManager) {
        PackageManager packageManager = getPackageManager();
        boolean isGranted = PackageManager.PERMISSION_GRANTED == packageManager.checkPermission(
                "android.permission.ACCESS_FINE_LOCATION", getPackageName());
        return isGranted;
    }
}
