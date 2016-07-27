package com.hjp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hjp.coursecheckin.R;

import java.util.List;

/**
 * Created by HJP on 2016/5/25 0025.
 */
public class CourseClassNotCheckInPeopleAdapter extends RecyclerView.Adapter<CourseClassNotCheckInPeopleViewHolder> {

    private Context mContext;
    private List<String> mList_notCheckInPeople;


    public CourseClassNotCheckInPeopleAdapter(Context context, List<String> list) {
        mContext = context;
        mList_notCheckInPeople = list;

    }

    @Override
    public CourseClassNotCheckInPeopleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.cell_classnotcheckinpeople, parent, false);
        return new CourseClassNotCheckInPeopleViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CourseClassNotCheckInPeopleViewHolder holder, int position) {
        //不存在如点击之类的引起错乱的情况,所以不用判断之前设置的tag和当前数据是否对等
        holder.textV_peopleNum_and_Name.setText(mList_notCheckInPeople.get(position));
    }


    @Override
    public int getItemCount() {
        return mList_notCheckInPeople.size();
    }
}
