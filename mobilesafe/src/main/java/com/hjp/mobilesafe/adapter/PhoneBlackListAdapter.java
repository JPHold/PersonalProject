package com.hjp.mobilesafe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.database.BlackListPhoneNumberInfo;
import com.hjp.mobilesafe.database.dao.BlackListDeleteDao;

import java.util.List;

/**
 * Created by HJP on 2016/9/7 0007.
 */

public class PhoneBlackListAdapter extends BaseAdapter {
    private Context mContext;
    private List<BlackListPhoneNumberInfo> mPhoneData;

    public PhoneBlackListAdapter(Context context, List<BlackListPhoneNumberInfo> data) {
        mContext = context;
        mPhoneData = data;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return super.areAllItemsEnabled();
    }

    @Override
    public int getCount() {
        return mPhoneData.size();
    }

    @Override
    public BlackListPhoneNumberInfo getItem(int position) {
        return mPhoneData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView  = View.inflate(mContext, R.layout.cell_communication_blacklist, null);
            viewHolder.textV_phone = (TextView) convertView.findViewById(R.id.textV_blackList_phoneNumber);
            viewHolder.textV_mode = (TextView) convertView.findViewById(R.id.textV_blackList_holdMode);
            viewHolder.imgV_delete = (ImageView) convertView.findViewById(R.id.imgV_blackList_delete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BlackListPhoneNumberInfo item = getItem(position);
        final String phoneNumber = item.getPhoneNumber();
        String holdMode = item.getHoldMode();

        viewHolder.textV_phone.setText(phoneNumber);
        viewHolder.textV_mode.setText(holdMode);
        viewHolder.imgV_delete.setId(position);
        viewHolder.imgV_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickId = v.getId();
                if (clickId == position) {
                    BlackListPhoneNumberInfo deleteItem = getItem(position);
                    String phoneNumber_delete = deleteItem.getPhoneNumber();
                    BlackListDeleteDao blackListDeleteDao = new BlackListDeleteDao(mContext);
                    int delete = blackListDeleteDao.deleteBlackListPart(phoneNumber_delete);
                    Toast.makeText(mContext, delete == 0 ? "删除失败" : "删除成功", Toast.LENGTH_LONG).show();
                    mPhoneData.remove(position);
                    notifyDataSetChanged();
                }
            }
        });


        return convertView;
    }

    private class ViewHolder {
        public TextView textV_phone;
        public TextView textV_mode;
        private ImageView imgV_delete;
    }
}
