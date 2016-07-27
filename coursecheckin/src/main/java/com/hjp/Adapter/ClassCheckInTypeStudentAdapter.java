package com.hjp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.hjp.coursecheckin.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by HJP on 2016/6/16 0016.
 */
public class ClassCheckInTypeStudentAdapter extends BaseAdapter {

    private final String mJobNum;
    private final String mCourseName;
    private final String mClassName;
    private Context mContext;
    private final LinkedHashMap<String, String> specialCheckInTypeStudents;
    private final ArrayList<String> studentNum;
    private ImageLoader imageLoader;
    private JumpCheckInPointMapActivityListener jumpPointMapActivity;

    public ClassCheckInTypeStudentAdapter(Context context,
                                          String jobNum, String courseName, String className,
                                          LinkedHashMap<String, String> linkedHashMap) {
        this.mContext = context;
        mJobNum = jobNum;
        mCourseName = courseName;
        mClassName = className;
        this.specialCheckInTypeStudents = linkedHashMap;

        Set<String> keys = specialCheckInTypeStudents.keySet();
        studentNum = new ArrayList<>();
        for (String stuNum : keys) {
            studentNum.add(stuNum);
        }
        imageLoader = ImageLoader.getInstance();
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount: " + studentNum.size());
        return studentNum.size();
    }

    @Override
    public String getItem(int position) {
        return specialCheckInTypeStudents.get(studentNum.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private final String TAG = "stu";

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        String currItem = getItem(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.cell_wrapcheckintype_student, parent, false);
            holder.radioBtn = (RadioButton) convertView.findViewById(R.id.checkInType_student);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.radioBtn.setId(position);
        int index = currItem.indexOf(",");
        String stuName = currItem.substring(0, index);
        Log.i(TAG, "getView: " + stuName);
        holder.radioBtn.setText(stuName);
        //获取头像uri
        String portrait_uri = currItem.substring(index, currItem.length());
        Log.i(TAG, "getView: " + portrait_uri);
        final ViewHolder finalHolder = holder;
      /*  imageLoader.loadImage(portrait_uri, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String uri, View displayView, Bitmap bitmap) {
                BitmapDrawable drawable = new BitmapDrawable(bitmap);
                finalHolder.radioBtn.setCompoundDrawablesRelative(null, drawable, null, null);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });*/
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.user_avatar);
        BitmapDrawable drawable = new BitmapDrawable(bitmap);
        holder.radioBtn.setBackground(drawable);

        //监听头像点击(查看最后签到的位置)
        holder.radioBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == position) {
                    Bundle bundle = new Bundle();
                    bundle.putString("jobNum", mJobNum);
                    bundle.putString("courseName", mCourseName);
                    bundle.putString("className", mClassName);
                    bundle.putString("stuNum", studentNum.get(position));
                    jumpPointMapActivity.jump(bundle);
                }
            }
        });
        return convertView;
    }

    private class ViewHolder {
        public RadioButton radioBtn;
    }

    public void setJumpPointMapActivity(ClassCheckInTypeStudentAdapter.JumpCheckInPointMapActivityListener jumpPointMapActivity) {
        this.jumpPointMapActivity = jumpPointMapActivity;
    }

    public interface JumpCheckInPointMapActivityListener {
        public void jump(Bundle bundle);
    }
}
