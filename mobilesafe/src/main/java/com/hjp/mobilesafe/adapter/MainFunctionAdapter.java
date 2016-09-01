package com.hjp.mobilesafe.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjp.mobilesafe.R;

import com.hjp.mobilesafe.constant.Constant;

/**
 * Created by HJP on 2016/8/19 0019.
 */

public class MainFunctionAdapter extends BaseAdapter {

    private static final String TAG="MainFunctionAdapter";

    private final Context mContext;
    private final String[] mFunction_title;
    private final Integer[] mFunction_img;
    private final LayoutInflater mLayoutInflater;

    public MainFunctionAdapter(Context context){
        mContext = context;
       Resources resources = mContext.getResources();
        Log.i(TAG, "MainFunctionAdapter-constructor:  "+1);
       mLayoutInflater = LayoutInflater.from(mContext);
        Log.i(TAG, "MainFunctionAdapter: "+2);
       mFunction_title = resources.getStringArray(R.array.main_function_name);
        Log.i(TAG, "MainFunctionAdapter: "+3);
       mFunction_img= Constant.main_function_img;
        Log.i(TAG, "MainFunctionAdapter: "+4);
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
        Log.i(TAG, "getView: "+1);
        String titleStr=mFunction_title[position];
        Log.i(TAG, "getView: "+2);
        Integer imgStr=mFunction_img[position];
        Log.i(TAG, "getView: "+3);

        View cell_layout = mLayoutInflater.inflate(R.layout.cell_main_function,parent,false);
        Log.i(TAG, "getView: "+4);
        TextView title = (TextView) cell_layout.findViewById(R.id.textV_cell_main_function);
        Log.i(TAG, "getView: "+5);
        ImageView img = (ImageView) cell_layout.findViewById(R.id.imgV_cell_main_function);
        Log.i(TAG, "getView: "+6);
        title.setText(titleStr);
        Log.i(TAG, "getView: "+7);
        img.setImageResource(imgStr);
        Log.i(TAG, "getView: "+8);
        return cell_layout;
    }
}
