package com.hjp.mobilesafe.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.listener.OnCallListener;
import com.hjp.mobilesafe.utils.ProcessInfoProvider;
import com.hjp.mobilesafe.utils.RemoveNullInList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HJP on 2016/9/13 0013.
 */

public class RunProcessListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int myPosition;
    private int mMyAppPosition;
    private List<ProcessInfoProvider.RunProcessInfo> mProcessInfos;
    private List<ProcessInfoProvider.RunProcessInfo> mUserProcessInfos;
    private List<ProcessInfoProvider.RunProcessInfo> mSystemProcessInfos;
    private static final int ITEMTYPE_TITLE = 1;
    private int size;
    private List<Integer> notClearPositions;
    /**
     * 需要清理的进程列表
     */
    private LinkedHashMap<Integer, Boolean> needProcessList;
    private String TAG = "process";
    private ArrayList<ProcessInfoProvider.RunProcessInfo> userProcess;
    private ArrayList<ProcessInfoProvider.RunProcessInfo> systemProcess;
    private int allStayProcessSize;

    public RunProcessListAdapter(Context context, int myAppPosition, int position, List<ProcessInfoProvider.RunProcessInfo> userProcessInfos,
                                 List<ProcessInfoProvider.RunProcessInfo> systemProcessInfos) {
        super();
        mContext = context;
        mMyAppPosition = myAppPosition;
        myPosition = position;
        mSystemProcessInfos = systemProcessInfos;
        mUserProcessInfos = userProcessInfos;
        notClearPositions = new ArrayList<>();
        needProcessList = new LinkedHashMap<>();
        changeTitlePosition();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEMTYPE_TITLE) {
            return new TitleViewHolder(View.inflate(mContext, R.layout.cell_applist_title, null));
        }
        View itemView = View.inflate(mContext, R.layout.cell_processmanager_processlist, null);
        return new ProcessListViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TitleViewHolder) {
            if ((position == 0 && myPosition == 0) || position == myPosition) {
                ((TitleViewHolder) holder).textV_appList_title.setText("系统进程");
                return;
            }
            if (position == 0 && myPosition != 0) {
                ((TitleViewHolder) holder).textV_appList_title.setText("用户进程");
                return;
            }
        }
        if (holder instanceof ProcessListViewHolder) {
            ProcessListViewHolder avHolder = (ProcessListViewHolder) holder;

            ProcessInfoProvider.RunProcessInfo runProcessInfo = null;

            if (position < userCount) {
                runProcessInfo = mUserProcessInfos.get(position);
                Log.i(TAG, "onBindViewHolder: " + position + "user " + mUserProcessInfos);
            } else {
                runProcessInfo = mSystemProcessInfos.get(position - userCount);
                Log.i(TAG, "onBindViewHolder: " + position + "system " + mUserProcessInfos);
            }

            if (runProcessInfo != null) {
                //上一个if判断，已经将runProcessInfo为null的情况去掉了
                Drawable icon = runProcessInfo.getIcon();
                avHolder.imgV_icon.setTag(position);
                avHolder.imgV_icon.setImageDrawable(icon);

                String appName = runProcessInfo.getAppName();
                avHolder.texV_appName.setText(appName);

                String size = runProcessInfo.getSize();
                avHolder.texV_processSize.setText("占用内存：" + size);//RecyclerView回收的是item实例,item实例设置的东西依然保存着


                //因为手机卫士的checkbox隐藏了,当他的holder被重复利用时还是隐藏，所以需要先设置为显示
                if (avHolder.checkB_sureProcess.getVisibility() == View.INVISIBLE) {
                    avHolder.checkB_sureProcess.setVisibility(View.VISIBLE);
                }
                if (position != mMyAppPosition) {
                    avHolder.checkB_sureProcess.setId(position);

                    avHolder.checkB_sureProcess.setChecked(needProcessList.containsKey(position) ? needProcessList.get(position) : false);
                    avHolder.checkB_sureProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView.getId() != position) {
                                return;
                            }
                            if (needProcessList == null) {
                                needProcessList = new LinkedHashMap<>();
                            }
                            if (isChecked) {
                                needProcessList.put(position, true);
                            } else {
                                needProcessList.remove(position);
                            }

                        }
                    });
                } else {//当前为手机卫士的app,不用显示checkBox
                    avHolder.checkB_sureProcess.setVisibility(View.INVISIBLE);
                }
            }
        }
    }/*   @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof TitleViewHolder) {
            if ((position == 0 && myPosition == 0) || position == (myPosition + 1)) {
                ((TitleViewHolder) holder).textV_appList_title.setText("系统进程");
                return;
            }
            if (position == 0 && myPosition != 0) {
                ((TitleViewHolder) holder).textV_appList_title.setText("用户进程");
                return;
            }
        }
        if (holder instanceof ProcessListViewHolder) {
            ProcessListViewHolder avHolder = (ProcessListViewHolder) holder;
            ProcessInfoProvider.RunProcessInfo runProcessInfo = mProcessInfos.get(position);

            Drawable icon = runProcessInfo.getIcon();
            avHolder.imgV_icon.setTag(position);
            avHolder.imgV_icon.setImageDrawable(icon);

            String appName = runProcessInfo.getAppName();
            avHolder.texV_appName.setText(appName);

            String size = runProcessInfo.getSize();
            avHolder.texV_processSize.append(size);


            if (position != mMyAppPosition) {
                avHolder.checkB_sureProcess.setId(position);
                avHolder.checkB_sureProcess.setChecked(needProcessList.containsKey(position) ? needProcessList.get(position) : false);
                avHolder.checkB_sureProcess.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (buttonView.getId() != position) {
                            return;
                        }
                        if (isChecked) {
                            needProcessList.put(position, true);
                        } else {
                            needProcessList.remove(position);
                        }

                    }
                });
            }
        }
    }*/

    @Override
    public int getItemViewType(int position) {

        //没有用户app并且是第一个item或达到系统应用位置，设置标题为系统应用
        if ((position == 0 && myPosition == 0) || position == myPosition) {
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

    public class ProcessListViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgV_icon;
        private TextView texV_appName;
        private TextView texV_processSize;
        private CheckBox checkB_sureProcess;


        public ProcessListViewHolder(View itemView) {
            super(itemView);

            imgV_icon = (ImageView) itemView.findViewById(R.id.imgV_icon_cell_appList);
            texV_appName = (TextView) itemView.findViewById(R.id.textV_appName_cell_appList);
            texV_processSize = (TextView) itemView.findViewById(R.id.textV_processSize_cell_appList);
            checkB_sureProcess = (CheckBox) itemView.findViewById(R.id.checkB_sureProcess_cell_processList);
        }
    }


    public class TitleViewHolder extends RecyclerView.ViewHolder {

        private TextView textV_appList_title;

        public TitleViewHolder(View itemView) {
            super(itemView);
            textV_appList_title = (TextView) itemView.findViewById(R.id.textV_appList_title);
        }
    }

    /**
     * 进程清理列表
     */
    public List<String> getClearList() {
        if (needProcessList.size() == 0) {
            return null;
        }
        List<String> clearList = new ArrayList<>();

        Set<Integer> integers = needProcessList.keySet();

        for (Integer i : integers) {
            ProcessInfoProvider.RunProcessInfo runProcessInfo = null;
            if (i < userCount) {
                runProcessInfo = mUserProcessInfos.get(i);
            } else {
                runProcessInfo = mSystemProcessInfos.get(i - userCount);
            }
            if (runProcessInfo != null) {
                String pkgName = runProcessInfo.getPkgName();
                clearList.add(pkgName);
            }
        }
        return clearList;
    }


    private boolean isRemoveProcess = false;

    /**
     * 通知：不显示被杀死的进程在进程集里
     */
    public void removeClearProcessShow(OnCallListener callListener) {
        isRemoveProcess = true;
        Set<Integer> integers = needProcessList.keySet();///太蛋疼了,还是采用用户进程和系统进程分开装

        int userRemoveCount = 0;//用户进程被请出的个数
        int systemRemoveCount = 0;//用户进程被清除的个数

        for (Integer i : integers) {
            if (i < userCount) {//全清：mUserProcessInfos只有两个
                mUserProcessInfos.remove(i - userRemoveCount);
                Log.i(TAG, "removeClearProcessShow: " + i + "size " + mUserProcessInfos.size());
                userRemoveCount++;
            } else {
                try {//全清：理应mSystemProcessInfos只有一个
                    mSystemProcessInfos.remove(i - userCount - systemRemoveCount);//删除时,mSystemProcessInfos的大小已经改变，但是要删除的位置没有改变，所以需要减去删掉的个数
                    systemRemoveCount++;
                    Log.i(TAG, "removeClearProcessShow: " + i + "size " + mSystemProcessInfos.size());
                } catch (IndexOutOfBoundsException e) {
                }
            }
        }

        //清空上一次的清理列表
        needProcessList.clear();
        Log.i(TAG, "removeClearProcessShow: 前user" + mUserProcessInfos);
        Log.i(TAG, "removeClearProcessShow: 前system" + mSystemProcessInfos);

        if (isRemoveProcess) {
            changeTitlePosition();
            isRemoveProcess = false;
        }
        notifyDataSetChanged();

        ArrayList<ProcessInfoProvider.RunProcessInfo> userProcessInfos = new ArrayList<>(mUserProcessInfos);
        ArrayList<ProcessInfoProvider.RunProcessInfo> systemProcessInfos = new ArrayList<>(mSystemProcessInfos);
        userProcess = (ArrayList<ProcessInfoProvider.RunProcessInfo>) RemoveNullInList.removeNullInList(userProcessInfos);
        systemProcess = (ArrayList<ProcessInfoProvider.RunProcessInfo>) RemoveNullInList.removeNullInList(systemProcessInfos);

        allStayProcessSize = 0;
        //更新当前进程数和剩余内存
        int userSize = userProcess.size();

        for (int i = 0; i < userSize; i++) {
            ProcessInfoProvider.RunProcessInfo rpi = userProcess.get(i);
            String size = rpi.getSize();
            size = size.trim();
            Pattern p = Pattern.compile("[^a-zA-Z,]");
            Matcher m = p.matcher(size);
            String result = "";

            if (m.find()) {
                result += m.group();
                if (!TextUtils.isEmpty(result)) {
                    allStayProcessSize += Integer.valueOf(result);
                    measureSystemInfos(i, userSize, callListener);
                }
            } else {//如果匹配不到数值,则判断是否是最后一个,是则查询用户进程,否则不用记录进allStayProcessSize
                measureSystemInfos(i, userSize, callListener);
            }
        }

    }

    /**
     * 计算出清理后的用户进程的所有内存占用大小
     *
     * @param position
     * @param userSize
     */
    private void measureSystemInfos(int position, int userSize, OnCallListener callListener) {
        if (position == (userSize - 1)) {//系统进程计算大小完毕
            int systemSize = systemProcess.size();

            if (systemSize == 0) {//系统进程全清除了
                callActivityOperator(callListener);
            }
            for (int j = 0; j < userSize; j++) {//开始计算用户进程
                ProcessInfoProvider.RunProcessInfo user = userProcess.get(j);

                String oneUserSize = user.getSize();
                oneUserSize = oneUserSize.trim();//去除空格,防止下面的numberFormatException

                Pattern p2 = Pattern.compile("[^a-zA-Z,]");
                Matcher m2 = p2.matcher(oneUserSize);

                String oneUserResult = "";
                if (m2.find()) {
                    oneUserResult += m2.group();

                    if (!TextUtils.isEmpty(oneUserResult)) {
                        allStayProcessSize += Integer.valueOf(oneUserResult);//计算进用户进程大小总和

                        if (j == (userSize - 1)) {//如果用户进程计算完毕
                            callActivityOperator(callListener);
                        }
                    } else {//匹配的数值为空
                        if (j == (userSize - 1)) {//如果用户进程计算完毕
                            callActivityOperator(callListener);
                        }
                    }
                } else {//匹配不到数值
                    if (j == (userSize - 1)) {//如果用户进程计算完毕
                        callActivityOperator(callListener);
                    }
                }
            }
        }
    }


    /**
     * 通知Activity更新进程数和剩余内存
     */

    private void callActivityOperator(OnCallListener callListener) {
        //得到当前剩余的进程数
        int userCount = userProcess.size();
        int systemCount = systemProcess.size();

        int currProcessSize = userCount + systemCount;

        callListener.onCall(null, allStayProcessSize + "," + currProcessSize);
    }


    private boolean mIiInvert;

    /**
     * 全选/反选的功能
     */
    public void changeClearCheck(boolean isInvert) {
        mIiInvert = isInvert;
        if (mIiInvert) {//反选,直接清空保存check记录集
            needProcessList = null;
            needProcessList = new LinkedHashMap<>();
            notifyDataSetChanged();
            return;
        }
        //全选
        for (int i = 0; i < size; i++) {
            if (notClearPositions.contains(i)) {//当前位置是titleItem的位置,则不用记录
                continue;
            }
            needProcessList.put(i, true);
        }
        notifyDataSetChanged();
        changeTitlePosition();//重新计算进程数
    }

    private int userCount;//用户进程额总数
    private int systemPosition;//系统进程的开始位置

    //公共代码块

    /**
     * 在以下情形:需要更新系统应用title的位置、用户应用title的位置、手机卫士进程的位置
     */
    private void changeTitlePosition() {

        notClearPositions = new ArrayList<>();

        //这段代码不能在getItemCount()执行,会造成onBindViewHolder()获取appInfo时为null
        if (myPosition == 0) {
            //在数据集的最末尾添加null,为了避免indexOutBound错误
            mSystemProcessInfos.add(myPosition, null);
            notClearPositions.add(myPosition);

        } else {
            //在数据集的开头和最末尾添加null,为了避免indexOutBound错误
            //清理进程时,已经添加标题了
            if (!isRemoveProcess) {
                mUserProcessInfos.add(0, null);
                mSystemProcessInfos.add(0, null);
            }

            notClearPositions.add(0);//第一个用户进程标题没有checkBox
            notClearPositions.add(myPosition + 1);//中间的系统进程没有checkBox

            userCount = mUserProcessInfos.size();

        }

        myPosition = mUserProcessInfos.size();//更新系统进程title的位置

        if (!isRemoveProcess) {
            mMyAppPosition = 1;
            notClearPositions.add(mMyAppPosition);
        }

        size = mUserProcessInfos.size() + mSystemProcessInfos.size();
        Log.i(TAG, "changeTitlePosition: 后user" + mUserProcessInfos);
        Log.i(TAG, "changeTitlePosition: 后system" + mSystemProcessInfos);
        Log.i(TAG, "changeTitlePosition: remove后的进程大小" + size);

    }
/*
 */
/**
 * 在以下情形:需要更新系统应用title的位置、用户应用title的位置、手机卫士进程的位置
 *//*

    private void changeTitlePosition() {
        //这段代码不能在getItemCount()执行,会造成onBindViewHolder()获取appInfo时为null
        size = mProcessInfos.size();
        if (myPosition == 0) {
            //在数据集的最末尾添加null,为了避免indexOutBound错误
            mProcessInfos.add(myPosition, null);
            notClearPositions.add(myPosition);
            size += 1;
        } else {
            //在数据集的开头和最末尾添加null,为了避免indexOutBound错误
            mProcessInfos.add(0, null);
            mProcessInfos.add(myPosition + 1, null);
            notClearPositions.add(0);
            notClearPositions.add(myPosition + 1);
            size += 2;

        }
        mMyAppPosition++;
        notClearPositions.add(mMyAppPosition);
    }
*/

}
