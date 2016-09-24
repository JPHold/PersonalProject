package com.hjp.mobilesafe.adapter;

import android.content.Context;
import android.net.TrafficStats;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.utils.AppInfoProvider;

import java.util.List;

/**
 * Created by HJP on 2016/9/23.
 */

public class TrafficAdapter extends BaseAdapter {

    private final String TAG = "TrafficAdapter";
    private Context context;
    private List<AppInfoProvider.AppInfo> trafficData;

    public TrafficAdapter(Context context, List<AppInfoProvider.AppInfo> trafficData) {
        this.context = context;
        this.trafficData = trafficData;
        Log.i(TAG, "trafficData: " + trafficData.size());
    }

    public void chanageTraffics() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return trafficData.size();
    }

    @Override
    public AppInfoProvider.AppInfo getItem(int position) {
        return trafficData.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.i(TAG, "getItemId: 位置" + position);
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Log.i(TAG, "getView: 位置" + position);

        if (convertView == null) {
            convertView = View.inflate(context, R.layout.cell_traffiic, null);
            viewHolder = new ViewHolder();
            viewHolder.appIcon = (ImageView) convertView.findViewById(R.id.imgV_traffic_appIcon);
            viewHolder.appName = (TextView) convertView.findViewById(R.id.textV_traffic_appName);
            viewHolder.down = (TextView) convertView.findViewById(R.id.textV_traffic_down);
            viewHolder.upload = (TextView) convertView.findViewById(R.id.textV_traffic_upload);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AppInfoProvider.AppInfo item = getItem(position);

        viewHolder.appIcon.setImageDrawable(item.getIcon());//应用图标
        viewHolder.appName.setText(item.getAppName());//应用名

        int uid = item.getUid();
        //测试代码
        long uidRxBytes = TrafficStats.getUidRxBytes(uid);//某个app的下载的流量,如果没有流量则为-1

        String downSize = "0.0";
        if (uidRxBytes != -1) {
            downSize = Formatter.formatFileSize(context, uidRxBytes);
        }

        //设置当前应用的下载流量
        viewHolder.down.setText("下载:" + downSize);

        long uidTxBytes = TrafficStats.getUidTxBytes(uid);//某个app的上传的流量,如果没有流量则为-1
        String uploadSize = "0.0";
        if (uidTxBytes != -1) {
            uploadSize = Formatter.formatFileSize(context, uidTxBytes);
        }

        //设置当前应用的上传流量
        viewHolder.upload.setText("上传:" + uploadSize);

      /*  if (downTraffics != null) {

            //测试代码
            long uidRxBytes = TrafficStats.getUidRxBytes(uid);//某个app的下载的流量,如果没有流量则为-1
            long uidTxBytes = TrafficStats.getUidTxBytes(uid);//某个app的上传的流量,如果没有流量则为-1

            String downSize = "0.0";
            if (uidRxBytes != -1) {
                downSize = Formatter.formatFileSize(context, uidRxBytes);
            }

            String uploadSize = "0.0";
            if (uidTxBytes != -1) {
                uploadSize = Formatter.formatFileSize(context, uidTxBytes);
            }
            String down = downTraffics.get(position);
            if (downTrafficsCount != 0 && position < downTrafficsCount) {
                //设置当前应用的下载流量
                viewHolder.down.setText("下载:" + down);
            } else {
                viewHolder.down.setText("下载:" + 0.0);
            }
        }

        if (uploadTraffics != null) {
            String upload = uploadTraffics.get(position);
            if (uploadTrafficsCount != 0 && position < uploadTrafficsCount) {
                //设置当前应用的上传流量
                viewHolder.upload.setText("上传:" + upload);
            } else {
                viewHolder.upload.setText("上传" + 0.0);
            }
        }*/
        return convertView;
    }

    public class ViewHolder {

        private ImageView appIcon;
        private TextView appName;
        private TextView down;
        private TextView upload;
    }
}
