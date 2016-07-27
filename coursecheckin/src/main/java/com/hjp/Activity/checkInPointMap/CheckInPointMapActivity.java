package com.hjp.Activity.checkInPointMap;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.hjp.Application.MainApplication;
import com.hjp.coursecheckin.R;
import com.hjp.vo.ComputerCheckIn;
import com.util.BaiduMapUtil;
import com.util.ObtainAttrUtil;
import com.util.SendInModuleUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by HJP on 2016/6/17 0017.
 */
public class CheckInPointMapActivity extends AppCompatActivity {

    private LinearLayout rootLayout_map;
    private Toolbar toolbar;
    private TextureMapView mapV_showCheckInPoint;
    private Context mContext;
    private BaiduMap mBaiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        boolean isNight = MainApplication.mAppConfig.getNightModeSwitch();
        if (isNight) {
            this.setTheme(R.style.nightTheme);
        } else {
            this.setTheme(R.style.DefaultTheme);
        }
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_checkin_pointmap);
        initView();
        initPointMap();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapV_showCheckInPoint.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapV_showCheckInPoint.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapV_showCheckInPoint.onDestroy();
    }

    private void initPointMap() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.i("stu", "initPointMap: " + extras.getString("jobNum"));
            Log.i("stu", "initPointMap: " + extras.getString("courseName"));
            Log.i("stu", "initPointMap: " + extras.getString("className"));
            Log.i("stu", "initPointMap: " + extras.getString("stuNum"));

            BmobQuery<ComputerCheckIn> bmobQuery = new BmobQuery<>();
            bmobQuery.addWhereEqualTo("jobNum", extras.getString("jobNum", ""));
            bmobQuery.addWhereEqualTo("courseName", extras.getString("courseName", ""));
            bmobQuery.addWhereEqualTo("className", extras.getString("className", ""));
            bmobQuery.addWhereEqualTo("stuNum", extras.getString("stuNum", ""));
            bmobQuery.addQueryKeys(ComputerCheckIn.KEY_CHECKINPOINT);
            bmobQuery.findObjects(mContext, new FindListener<ComputerCheckIn>() {
                @Override
                public void onSuccess(List<ComputerCheckIn> list) {
                    if (list != null && list.size() == 1) {
                        String checkInPoint = list.get(0).getCheckInGeoPoint();
                        if (checkInPoint != null) {
                            Message.obtain(handler, 1, checkInPoint).sendToTarget();
                        } else {
                            LocationClient locationClient = new LocationClient(mContext);
                            LocationClientOption locationClientOption = new LocationClientOption();
                            BaiduMapUtil.initMap(locationClient, locationClientOption, new MyLocationListener());
                            locationClient.start();
                            //没有签到
                            rootLayout_map.removeView(mapV_showCheckInPoint);
                            SendInModuleUtil.showToast(mContext, "没有签到");
                        }
                    }

                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }
    }

    private static final String TAG = "CheckInPointMapActivity";
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            mBaiduMap.clear();
            MarkerOptions options = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pointinmap_24dp));
            switch (what) {

                case 1:
                    String point = (String) msg.obj;
                    String points[] = point.split(",");
                    Double latitude = Double.valueOf(points[0]);
                    Double longitude = Double.valueOf(points[1]);
                    Log.i(TAG, "handleMessage: " + +longitude + "," + latitude);

                    LatLng geoPoint = new LatLng(latitude, longitude);
                    options.position(geoPoint);
                    mBaiduMap.addOverlay(options);
                    MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(geoPoint, 19);
                    mBaiduMap.animateMapStatus(mapStatusUpdate);
                    break;
                case 2:
                    Log.i(TAG, "handleMessage: " + 2);
                    LatLng defaultGeoPoint = (LatLng) msg.obj;
                    options.position(defaultGeoPoint);
                    mBaiduMap.addOverlay(options);

                    MapStatusUpdate defaultMapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(defaultGeoPoint, 19);
                    mBaiduMap.setMapStatus(defaultMapStatusUpdate);
                    break;
            }
        }
    };

    private void initView() {

        rootLayout_map = (LinearLayout) findViewById(R.id.rootLayout_map);
        toolbar = (Toolbar) findViewById(R.id.toolBar_checkInPointMap);
        mapV_showCheckInPoint = (TextureMapView) findViewById(R.id.mapV_showCheckInPoint);
        mBaiduMap = mapV_showCheckInPoint.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
        mBaiduMap.setBuildingsEnabled(true);
        mBaiduMap.setIndoorEnable(true);

        setSupportActionBar(toolbar);
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.i("activity", "onReceiveLocation: ");

            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {// GPS定位结果
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng geoPoint = new LatLng(latitude, longitude);
                Message.obtain(handler, 2, geoPoint).sendToTarget();

                Log.i("stu", "TypeGpsLocation: " + latitude + "," + longitude);

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                //请检查网络是否通畅
                Log.i("stu", "TypeNetWorkException: ");
                return;
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                //当前处于飞行模式,请取消
                Log.i("stu", "TypeCriteriaException: ");
                return;
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
