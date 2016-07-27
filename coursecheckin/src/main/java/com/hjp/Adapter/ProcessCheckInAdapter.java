package com.hjp.Adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hjp.Activity.student.ProcessCheckInViewHolder;
import  com.hjp.coursecheckin.R;
import com.hjp.vo.ComputerCheckIn;
import com.util.BaiduMapUtil;
import com.util.GeoPointDistance;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by HJP on 2016/5/30 0030.
 */
public class ProcessCheckInAdapter extends RecyclerView.Adapter<com.hjp.Activity.student.ProcessCheckInViewHolder> {

    private Context mContext;

    private List<Map<String, Object>> listArray_allPostCheckIn;
    private LocationClient locationClient;
    private LocationClientOption locationOption;
    private ChangeUIListener mUiListener;

    public ProcessCheckInAdapter(Context context, List<Map<String, Object>> list) {
        mContext = context;
        listArray_allPostCheckIn = list;
        initBaiduLBS();
    }

    @Override
    public com.hjp.Activity.student.ProcessCheckInViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cell_recyv_processcheckin
                , parent, false);
        return new com.hjp.Activity.student.ProcessCheckInViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(com.hjp.Activity.student.ProcessCheckInViewHolder holder, int position) {
        TextView textV_courseName = holder.textV_processCheckIn_courseName;
        TextView textV_className = holder.textV_processCheckIn_className;
        Button btn_processCheckIn = holder.btn_checkIn;
        Map<String, Object> mapArray_onePostCheckIn = listArray_allPostCheckIn.get(position);

        if (mapArray_onePostCheckIn.containsKey("objectId")) {
            btn_processCheckIn.setTag(R.id.checkIn_adapter_viewHolder, holder);
            btn_processCheckIn.setTag(R.id.checkIn_updateId, (String) mapArray_onePostCheckIn.get("objectId"));
        } else {
            //
            return;
        }

        String courseName = "";
        if (mapArray_onePostCheckIn.containsKey("courseName")) {
            courseName = (String) mapArray_onePostCheckIn.get("courseName");
        }
        String className = "";
        if (mapArray_onePostCheckIn.containsKey("className")) {
            className = (String) mapArray_onePostCheckIn.get("className");
        }

        BmobGeoPoint postGeoPoint;
        if (mapArray_onePostCheckIn.containsKey("postGeoPoint")) {
            postGeoPoint = (BmobGeoPoint) mapArray_onePostCheckIn.get("postGeoPoint");
            if (postGeoPoint != null && postGeoPoint.getLongitude() != 0 && postGeoPoint.getLatitude() != 0) {
                btn_processCheckIn.setTag(R.id.checkIn_postGeoPoint, postGeoPoint);
            }
        } else {
            //
        }
        textV_courseName.setText(courseName);
        textV_className.setText(className);

        btn_processCheckIn.setOnClickListener(clickListener);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listArray_allPostCheckIn.size();
    }

    private int mItemRemovePosition;
    private String mUpdateId;
    private BmobGeoPoint mPostGeoPoint;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof Button) {
                Button clickBtn = (Button) v;
                int clickId = clickBtn.getId();
                switch (clickId) {
                    case R.id.btn_processCheckIn:
                        Log.i("activity", "onClick: ");
                        com.hjp.Activity.student.ProcessCheckInViewHolder checkInViewHolder =
                                (ProcessCheckInViewHolder) clickBtn.getTag(R.id.checkIn_adapter_viewHolder);
                        int removeItemPosition = checkInViewHolder.getAdapterPosition();
                        String updateId = (String) clickBtn.getTag(R.id.checkIn_updateId);
                        BmobGeoPoint postGeoPoint = (BmobGeoPoint) clickBtn.getTag(R.id.checkIn_postGeoPoint);

                        if (postGeoPoint != null && updateId != null && removeItemPosition >= 0) {
                            Log.i("activity", "postGeoPoint: ");
                            mItemRemovePosition = removeItemPosition;
                            mUpdateId = updateId;
                            mPostGeoPoint = postGeoPoint;

                            Toast.makeText(mContext, "签到成功", Toast.LENGTH_LONG).show();
                            Message message = Message.obtain(removeHandler);
                            message.sendToTarget();
                        } else {
                            //
                        }
                        break;
                }
            }
        }
    };

    private void initBaiduLBS() {
        locationClient = new LocationClient(mContext);
        locationOption = new LocationClientOption();
        BaiduMapUtil.initMap(locationClient,locationOption,new MyLocationListener());
    }

    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.i("activity", "onReceiveLocation: ");
            double post_latitude = mPostGeoPoint.getLatitude();
            double post_longitude = mPostGeoPoint.getLongitude();
            double location_latitude = location.getLatitude();
            double location_longitude = location.getLongitude();
            BmobGeoPoint geoPoint = null;
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {// GPS定位结果
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                geoPoint = new BmobGeoPoint(longitude, latitude);
                Log.i("activity", "TypeGpsLocation: ");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                //请检查网络是否通畅
                Log.i("activity", "TypeNetWorkException: ");
                return;
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                //当前处于飞行模式,请取消
                Log.i("activity", "TypeCriteriaException: ");
                return;
            }

            if (geoPoint == null) {
                Log.i("activity", "搜索不到当前的位置: ");
                mUiListener.pushMsg("搜索不到当前的位置");
                return;
            }
            double distance_LocationAndPost = GeoPointDistance.getPortDistance(location_longitude, location_latitude, post_longitude, post_latitude);
            boolean isExceed;
            if (distance_LocationAndPost > 10D) {
                isExceed = true;
            } else {
                isExceed = false;
            }
         /*   insertCheckInInfo(mUpdateId, addStress, isExceed, new InsertCheckInInfoListener() {
                @Override
                public void msg(String msg) {
                    if ("签到成功".equals(msg)) {
                        Toast.makeText(mContext, "签到成功", Toast.LENGTH_LONG).show();
                        //移除当前签到的条目
                        Message message = Message.obtain(removeHandler);
                        message.sendToTarget();

                    } else {
                        //提示签到失败
                        Toast.makeText(mContext, "签到失败", Toast.LENGTH_LONG).show();
                    }
                }
            });*/

        }
    }


    private Handler removeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            listArray_allPostCheckIn.remove(mItemRemovePosition);
            notifyItemRemoved(mItemRemovePosition);
            locationClient.stop();
        }
    };

    public void setChangeUiListener(ChangeUIListener uiListener) {
        mUiListener = uiListener;
    }

    public interface ChangeUIListener {
        public void changUI(int position, String msg);

        public void pushMsg(String msg);
    }

    public interface InsertCheckInInfoListener {
        public void msg(String msg);
    }

    public void insertCheckInInfo(String updateId, String geoPoint, boolean isExceed
            , final InsertCheckInInfoListener insertPostCheckInListener) {
        ComputerCheckIn checkIn = new ComputerCheckIn();

        checkIn.setCheckInGeoPoint(geoPoint);
        checkIn.setExceed(isExceed);
        checkIn.setNeedCheckIn(false);
        checkIn.update(mContext, updateId, new UpdateListener() {
            @Override
            public void onSuccess() {
                insertPostCheckInListener.msg("签到成功");
            }

            @Override
            public void onFailure(int i, String s) {
                insertPostCheckInListener.msg(i + "/" + s);
            }
        });
    }
}
