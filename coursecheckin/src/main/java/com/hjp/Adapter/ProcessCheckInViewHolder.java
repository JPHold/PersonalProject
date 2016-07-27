package com.hjp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import  com.hjp.coursecheckin.R;

/**
 * Created by HJP on 2016/5/30 0030.
 */
public class ProcessCheckInViewHolder extends RecyclerView.ViewHolder {
    public TextView textV_processCheckIn_courseName;
    public TextView textV_processCheckIn_classeName;
    public Button btn_checkIn;

    public ProcessCheckInViewHolder(View itemView) {
        super(itemView);
        textV_processCheckIn_courseName = (TextView) itemView.findViewById(R.id.textV_processCheckIn_courseName);
        textV_processCheckIn_classeName = (TextView) itemView.findViewById(R.id.textV_processCheckIn_className);
        btn_checkIn = (Button) itemView.findViewById(R.id.btn_processCheckIn);
    }
}
