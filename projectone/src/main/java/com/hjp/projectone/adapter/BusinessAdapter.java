package com.hjp.projectone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjp.projectone.R;
import com.hjp.projectone.util.PhotoAsyncTask;

import java.util.List;

/**
 * Created by HJP on 2016/7/12 0012.
 */

public class BusinessAdapter extends RecyclerView.Adapter<BusinessViewHolder> {
    private static final String PHOTOURL = "http://img.dd369.com/space/100481/1151012261649.png";
    private final Context mContext;
    private final String mBankName;
    private final String[] mTitle;
    private final String[] mMsg;

    public BusinessAdapter(Context context, String bankName, String[] title
            , String[] msg) {
        mContext = context;
        mBankName = bankName;
        mTitle = title;
        mMsg = msg;
    }

    @Override
    public BusinessViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.businessitem, parent, false);
        return new BusinessViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BusinessViewHolder holder, int position) {
        holder.mBusiness_bankSign.setId(position);
        PhotoAsyncTask asyncTask = new PhotoAsyncTask(holder.mBusiness_bankSign, position,null);
        asyncTask.execute(PHOTOURL);

        holder.mBusiness_title.setText(mTitle[position]);
        holder.mBusiness_msg.setText(mMsg[position]);
    }

    @Override
    public int getItemCount() {
        return mTitle.length;
    }
}
