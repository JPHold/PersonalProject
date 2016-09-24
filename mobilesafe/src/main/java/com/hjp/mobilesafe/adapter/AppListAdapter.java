package com.hjp.mobilesafe.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.utils.AppInfoProvider;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HJP on 2016/9/13 0013.
 */

public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private int myPosition;
    private final List<AppInfoProvider.AppInfo> mAppInfos;
    private static final int ITEMTYPE_TITLE = 1;
    private int size;
    private onLongPressListener longPressListener;


    public AppListAdapter(Context context, int position, List<AppInfoProvider.AppInfo> appInfos) {
        super();
        mContext = context;
        myPosition = position;
        mAppInfos = appInfos;

        //这段代码不能在getItemCount()执行,会造成onBindViewHolder()获取appInfo时为null
        size = mAppInfos.size();
        if (myPosition == 0) {
            //在数据集的最末尾添加null,为了避免indexOutBound错误
            mAppInfos.add(myPosition, null);
            size += 1;
        } else {
            //在数据集的开头和最末尾添加null,为了避免indexOutBound错误
            mAppInfos.add(0, null);
            mAppInfos.add(myPosition + 1, null);
            size += 2;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEMTYPE_TITLE) {
            return new TitleViewHolder(View.inflate(mContext, R.layout.cell_applist_title, null));
        }
        View itemView = View.inflate(mContext, R.layout.cell_appmanager_applist, null);
        return new AppListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TitleViewHolder) {
            if ((position == 0 && myPosition == 0) || position == (myPosition + 1)) {
                ((TitleViewHolder) holder).textV_appList_title.setText("系统应用");
                return;
            }
            if (position == 0 && myPosition != 0) {
                ((TitleViewHolder) holder).textV_appList_title.setText("用户应用");
                return;
            }
        }
        if (holder instanceof AppListViewHolder) {
            AppListViewHolder avHolder = (AppListViewHolder) holder;
            avHolder.setLongPressListener(new onLongPressListener() {
                @Override
                public void onLongPress(int p, float x, float y) {
                    longPressListener.onLongPress(position, x, y);
                }
            });
            AppInfoProvider.AppInfo appInfo = mAppInfos.get(position);

                Drawable icon = appInfo.getIcon();
                avHolder.imgV_icon.setTag(position);
                avHolder.imgV_icon.setImageDrawable(icon);

                String appName = appInfo.getAppName();
                avHolder.texV_appName.setText(appName);

                String instaLoc = appInfo.getInstaLoc();
                avHolder.texV_instaLoc.setText(instaLoc);

                String size = appInfo.getSize();
                avHolder.texV_appSize.setText(size);
        }
    }

    @Override
    public int getItemViewType(int position) {

        //没有用户app并且是第一个item或达到系统应用位置，设置标题为系统应用
        if ((position == 0 && myPosition == 0) || position == (myPosition + 1)) {
            return ITEMTYPE_TITLE;
        }

        //有用户app并且是第一个item,设置标题为用户应用
        if (position == 0 && (myPosition != 0)) {
            return ITEMTYPE_TITLE;
        }

        return super.getItemViewType(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class AppListViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgV_icon;
        private TextView texV_appName;
        private TextView texV_instaLoc;
        private TextView texV_appSize;
        private Timer mTimer;
        private onLongPressListener longPressListener;

        public AppListViewHolder(View itemView) {
            super(itemView);
            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, final MotionEvent event) {
                    int action = event.getAction();

                    switch (action) {
                        case MotionEvent.ACTION_DOWN:

                            //定时器,两秒后弹出pop
                            mTimer = new Timer();
                            TimerTask tt = new TimerTask() {
                                @Override
                                public void run() {
                                    mTimer.cancel();
                                    longPressListener.onLongPress(-1, event.getRawX(), event.getRawY());
                                }
                            };
                            mTimer.schedule(tt, 2000);
                            break;
                        case MotionEvent.ACTION_UP:
                            //在2s内抬起了,则取消弹出pop任务
                            if (mTimer != null) {
                                mTimer.cancel();
                                mTimer = null;
                            }
                            return true;
                    }
                    return true;
                }
            });
            imgV_icon = (ImageView) itemView.findViewById(R.id.imgV_icon_cell_appList);
            texV_appName = (TextView) itemView.findViewById(R.id.textV_appName_cell_appList);
            texV_instaLoc = (TextView) itemView.findViewById(R.id.textV_instaLoc_cell_appList);
            texV_appSize = (TextView) itemView.findViewById(R.id.textV_appSize_cell_appList);
        }

        public void setLongPressListener(onLongPressListener olp) {
            longPressListener = olp;
        }
    }

    public interface onLongPressListener {
        public void onLongPress(int position, float x, float y);
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {

        private TextView textV_appList_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            textV_appList_title = (TextView) itemView.findViewById(R.id.textV_appList_title);
        }
    }

    public void setLongPressListener(onLongPressListener olp) {
        longPressListener = olp;
    }
}
