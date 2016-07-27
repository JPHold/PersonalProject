package com.extraFunction.Adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.BaseContent.CallListener;
import com.hjp.coursecheckin.R;
import com.util.ObtainAttrUtil;

import java.util.List;

public class TagCloudAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> mList;
    private CallListener mCallListener;

    public TagCloudAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public String getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = new RadioButton(mContext);
            convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            holder = new ViewHolder();
            holder.radioBtn = (RadioButton) convertView;
            //获取当前主题下的颜色
            TypedArray typedArray = ObtainAttrUtil.getSpecialTypedArray(mContext, R.attr.radioBtnTextColor);
            int color = typedArray.getColor(0, mContext.getResources().getColor(R.color.default_radioBtnTextColor));

            holder.radioBtn.setTextColor(color);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final String text = getItem(position);
        holder.radioBtn.setText(text);
        holder.radioBtn.setId(position);
        holder.radioBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = buttonView.getId();
                if (position == id) {
                    if (isChecked) {
                        mCallListener.call(text);
                    }
                }
            }
        });
        return convertView;
    }

    public void setListener(CallListener callListener) {
        mCallListener = callListener;
    }

    static class ViewHolder {
        RadioButton radioBtn;
    }
}