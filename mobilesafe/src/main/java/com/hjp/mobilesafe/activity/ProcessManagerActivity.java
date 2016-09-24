package com.hjp.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.adapter.RunProcessListAdapter;
import com.hjp.mobilesafe.listener.OnCallListener;
import com.hjp.mobilesafe.utils.KillProcessUtil;
import com.hjp.mobilesafe.utils.ProcessInfoProvider;
import com.hjp.mobilesafe.utils.RamSpaceUtil;
import com.hjp.mobilesafe.utils.SendInModuleUtil;
import com.stericson.RootTools.RootTools;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by HJP on 2016/9/15 0015.
 */

public class ProcessManagerActivity extends Activity {
    private Context mContext;
    private TextView textV_processCount;
    private TextView textV_ramTotalSpace;
    private TextView textV_processList_topTitle;
    private RecyclerView recyV_runProcessList;
    private RunProcessListAdapter mRunProcessListAdapter;
    private ProgressDialog pd;
    private Button btn_processFunction_clear;
    private Button btn_processFunction_setting;
    private Button btn_processFunction_all;
    public int myPosition;
    public static String TAG = "AppManagerActivity";
    private float screenDensity = -1;
    private List<ProcessInfoProvider.RunProcessInfo> userProcessData;
    private List<ProcessInfoProvider.RunProcessInfo> systemProcessData;
    private PackageManager mPackageManager;
    private int myAppPosition;
    private String mTotalSpace;
    private int mProcessCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processmanager);
        initView();
    }

    private void initView() {
        mContext = getApplicationContext();
        textV_processCount = (TextView) findViewById(R.id.textV_processCount);
        textV_ramTotalSpace = (TextView) findViewById(R.id.textV_ramTotalSpace);
        btn_processFunction_clear = (Button) findViewById(R.id.btn_processFunction_clear);
        btn_processFunction_setting = (Button) findViewById(R.id.btn_processFunction_setting);
        btn_processFunction_all = (Button) findViewById(R.id.btn_processFunction_all);

        btn_processFunction_clear.setOnClickListener(clickListener);
        btn_processFunction_setting.setOnClickListener(clickListener);
        btn_processFunction_all.setOnClickListener(clickListener);

        boolean rootAvailable = RootTools.isRootAvailable();
        Log.i(TAG, "initView: " + rootAvailable);

        textV_processList_topTitle = (TextView) findViewById(R.id.textV_processList_topTitle);
        recyV_runProcessList = (RecyclerView) findViewById(R.id.recyV_processList);
        final LinearLayoutManager llManager_appList = new LinearLayoutManager(mContext);
        recyV_runProcessList.setLayoutManager(llManager_appList);
        recyV_runProcessList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItemPosition = llManager_appList.findFirstVisibleItemPosition();
                String currTitle = textV_processList_topTitle.getText().toString();
                if (firstVisibleItemPosition > myPosition) {
                    if (!currTitle.equals("系统应用"))
                        textV_processList_topTitle.setText("系统应用");
                } else if (firstVisibleItemPosition < myPosition) {
                    if (!currTitle.equals("用户应用"))
                        textV_processList_topTitle.setText("用户应用");
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        pd = new ProgressDialog(ProcessManagerActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("扫描所有app");
        pd.show();

        ProcessInfoProvider.getAllProcessInfos(mContext, new ProcessInfoProvider.DataListener() {
            @Override
            public void onData(int myAppPosition, int userPosition, List<ProcessInfoProvider.RunProcessInfo> userData, List<ProcessInfoProvider.RunProcessInfo> systemData) {
                ProcessManagerActivity.this.myPosition = userPosition;
                ProcessManagerActivity.this.myAppPosition = myAppPosition;
                userProcessData = userData;
                systemProcessData = systemData;
                SendInModuleUtil.sendMsgByHandler(1, userProcessData.size() + systemProcessData.size(), handler);
            }

            @Override
            public void onCount(int count) {
                SendInModuleUtil.sendMsgByHandler(2, count, handler);
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 1:
                    mRunProcessListAdapter = new RunProcessListAdapter(mContext, myAppPosition, myPosition, userProcessData, systemProcessData);
                    recyV_runProcessList.setAdapter(mRunProcessListAdapter);
                    if (pd != null) {
                        pd.dismiss();
                    }
                    break;
                case 2:
                    mTotalSpace = RamSpaceUtil.getTotalSpace(mContext);
                    String ramFreeSpace = RamSpaceUtil.getRamFreeSpace(mContext);
                    textV_ramTotalSpace.append(ramFreeSpace);
                    textV_ramTotalSpace.append("/" + mTotalSpace);

                    mProcessCount = (int) msg.obj;

                    textV_processCount.setText("进程个数:" + mProcessCount);
                    break;

                case 3:
                    Log.i(TAG, "onCall: 2");
                    String processInfo = (String) msg.obj;
                    Log.i(TAG, "onCall: 3");
                    String[] processInfos = processInfo.split(",");
                    Log.i(TAG, "onCall: 4" + processInfos);

                    mProcessCount = Integer.valueOf(processInfos[1]);
                    Log.i(TAG, "onCall: 5" + mProcessCount);
                    textV_processCount.setText("进程个数:" + mProcessCount);
                    Log.i(TAG, "onCall: 6" + textV_processCount.getText().toString());

                    String freeSpace = processInfos[0];//清理过后的剩余内存
                    textV_ramTotalSpace.setText("剩余/总内存:" + freeSpace + "/" + freeSpace);

                    break;
            }
        }
    };

    private boolean isSelectAll = false;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_processFunction_clear://清理进程
                    List<String> clearList = mRunProcessListAdapter.getClearList();
                    if (clearList == null) {
                        SendInModuleUtil.showToast(mContext, "请选择");
                        return;
                    }
                    KillProcessUtil.killSpecialProcess(mContext, clearList, new KillProcessUtil.onCallListener() {
                        @Override
                        public void onCall() {
                            //进程列表不显示这些进程,表示已经被杀掉
                            mRunProcessListAdapter.removeClearProcessShow(new OnCallListener() {
                                @Override
                                public void onCall(View v, String processInfo) {
                                    String[] processInfos = processInfo.split(",");
                                    Integer safeAppSize;
                                    if (TextUtils.isEmpty(processInfos[0])) {
                                        safeAppSize = 0;
                                    } else {
                                        safeAppSize = Integer.valueOf(processInfos[0]);
                                    }

                                    mTotalSpace = mTotalSpace.trim();
                                    Pattern p = Pattern.compile("[a-zA-Z]");
                                    Matcher m = p.matcher(mTotalSpace);
                                    String totalSpace = m.replaceAll("");

                                    Log.i(TAG, "onCall: totalSpace" + totalSpace);
                                    if (!TextUtils.isEmpty(totalSpace)) {
                                        //总ram大小减去手机卫士大小=剩余ram
                                        String clearRamFreeSapce = String.valueOf(Integer.valueOf(totalSpace.trim()) - safeAppSize);
                                        String processI = clearRamFreeSapce + "," + processInfos[1];//剩余ram大小+线程的清理数
                                        Log.i(TAG, "onCall: 1");
                                        SendInModuleUtil.sendMsgByHandler(3, processI, handler);

                                    }
                                }
                            });
                        }
                    });
                    break;
                case R.id.btn_processFunction_setting:

                    break;

                case R.id.btn_processFunction_all://全选进程
                    mRunProcessListAdapter.changeClearCheck(isSelectAll);
                    isSelectAll = !isSelectAll;//如果上一代码,没有将isSelectAll保存在mRunProcessListAdapter内，则会false变为true,true变为false
                    break;
            }
        }
    };
}

