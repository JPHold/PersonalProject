package com.hjp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by HJP on 2016/5/25 0025.
 */
public class CourseClassNotCheckInPeopleViewHolder extends RecyclerView.ViewHolder {

    public TextView    textV_peopleNum_and_Name;
    public CourseClassNotCheckInPeopleViewHolder(View itemView) {
        super(itemView);
        textV_peopleNum_and_Name= (TextView) itemView;
    }


}
