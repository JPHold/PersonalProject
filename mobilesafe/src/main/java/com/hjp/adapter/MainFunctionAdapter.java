package com.hjp.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjp.activity.R;
import com.hjp.constant.Constant;

/**
 * Created by HJP on 2016/8/19 0019.
 */

public class MainFunctionAdapter extends BaseAdapter {
    private final Context mContext;
    private final String[] mFunction_title;
    private final Integer[] mFunction_img;
    private final LayoutInflater mLayoutInflater;

   public MainFunctionAdapter(Context context){
        mContext = context;
       Resources resources = mContext.getResources();
       mLayoutInflater = LayoutInflater.from(mContext);
       mFunction_title = resources.getStringArray(R.array.main_function_name);
       mFunction_img= Constant.main_function_img;
   }
    @Override
    public int getCount() {
        return mFunction_title.length;
    }

    @Override
    public String getItem(int position) {
        return mFunction_title[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String titleStr=mFunction_title[position];
        Integer imgStr=mFunction_img[position];

        View cell_layout = mLayoutInflater.inflate(R.layout.cell_main_function,parent);
        TextView title = (TextView) cell_layout.findViewById(R.id.textV_cell_main_function);
        ImageView img = (ImageView) cell_layout.findViewById(R.id.imgV_cell_main_function);

        title.setText(titleStr);
        img.setImageResource(imgStr);
        return cell_layout;
    }
}
