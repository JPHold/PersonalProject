package com.hjp.projectone.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjp.projectone.R;

/**
 * Created by HJP on 2016/7/12 0012.
 */

public class BusinessViewHolder extends RecyclerView.ViewHolder {
    public ImageView mBusiness_bankSign;
    public TextView mBusiness_title;
    public TextView mBusiness_msg;

    public BusinessViewHolder(View itemView) {
        super(itemView);
        mBusiness_bankSign = (ImageView) itemView.findViewById(R.id.business_bankSign);
        mBusiness_title = (TextView) itemView.findViewById(R.id.business_title);
        mBusiness_msg = (TextView) itemView.findViewById(R.id.business_msg);
    }
}
