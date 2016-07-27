package com.hjp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjp.coursecheckin.R;

/**
 * Created by HJP on 2016/6/14 0014.
 */
public class ClassCheckInInfoViewHolder extends RecyclerView.ViewHolder {
    public LinearLayout    linearLayout;
    public TextView textView;
    public GridView gridView;

    public ClassCheckInInfoViewHolder(View itemView) {
        super(itemView);
        linearLayout= (LinearLayout) itemView.findViewById(R.id.lineL_wrapStudent);
        textView = (TextView) itemView.findViewById(R.id.textV_checkInInfo_type);
        gridView = (GridView) itemView.findViewById(R.id.gridV_wrapStudent);
    }
}
