package com.hjp.Fragment;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hjp.Adapter.CourseClassesCheckInAdapter;
import com.hjp.NetWork.MainBmobQuery2;
import  com.hjp.coursecheckin.R;
import com.hjp.service.TaskIntentService;
import com.util.SendInModuleUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by HJP on 2016/5/16 0016.
 */
public class EachCourseCheckInInfoFragment extends BaseFragment {

    private final String TAG = "test";
    private String mCurrCourseName;
    private ProgressBar progressBar;
    private RecyclerView recyclerV_classCheckInInfo;
    private List<String> listArray_classes;
    private Map<String, List<Double>> mapArray_classCheckInInfo;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("fragment", "onActivityCreated: " + isVisible + "当前班级" + mCurrCourseName);
        isPrepared = true;
//        progressBar = (ProgressBar) fragmentLayout.findViewById(R.id.progressBar_classesCheckInInfo);

    }
    private TaskIntentService.DataReturnListener dataListener;
    private ServiceConnection actConnSer;

    private void initClassCheckInCardView(final MainBmobQuery2 mainBmobQuery) {
       /* final Intent jumpServiceIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("jobNum", mCurrUser);
        bundle.putString("courseName", mCurrCourseName);
        jumpServiceIntent.putExtras(bundle);
        jumpServiceIntent.setAction("com.hjp.queryExtraCheckInInfo");
        actConnSer = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("test", "onServiceConnected: ");
                dataListener = new TaskIntentService.DataReturnListener() {
                    @Override
                    public void data(ArrayList<LinkedHashMap<String, String>> exactCheckInData) {
                        Log.i("test", "data: ");
                        Message.obtain(handler, 1, exactCheckInData).sendToTarget();
                    }
                };
                TaskIntentService.MyBinder binder = (TaskIntentService.MyBinder) service;
                queryService = binder.getService();
                binder.setDataListener(dataListener);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i("test", "onServiceDisconnected: ");
                unbindService(actConnSer);
                stopService(jumpServiceIntent);
                actConnSer = null;
            }
        };
        bindService(jumpServiceIntent, actConnSer, Context.BIND_ADJUST_WITH_ACTIVITY);
        startService(jumpServiceIntent);*/

        Log.i(TAG, "initClassCheckInListView: ");
        //查询当前课程的所有班级
        mainBmobQuery.querySpecialCourseClasses(mCurrUser, mCurrCourseName, new MainBmobQuery2.SpecialCourseClassesQueryListener() {
            public void Success(List<String> list_classes) {
                listArray_classes = list_classes;
                Log.i(TAG, "Success: 查询班级" + list_classes.size());
                mainBmobQuery.querySpecialCourseClassesCheckInInfo(mCurrUser, mCurrCourseName, list_classes,
                        new MainBmobQuery2.SpecialCourseClassesCheckInInfoQueryListener() {
                            @Override
                            public void Success(Map<String, List<Double>> mapArray_classesCheckIn) {
                                Log.i(TAG, "查询所有班级的签到: " + mapArray_classesCheckIn.size());

                                mapArray_classCheckInInfo = mapArray_classesCheckIn;
                                SendInModuleUtil.sendMsgByHandler(1,null,handler);
                            }

                            @Override
                            public void Error(String msg) {
                                Log.i(TAG, "Error: " + msg);
                            }
                        });
            }
            public void Error(String msg) {
                Log.i(TAG, "Error: " + msg);
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.i(TAG, "班级大小" + listArray_classes.size());
                recyclerV_classCheckInInfo = new RecyclerView(mContext);
                recyclerV_classCheckInInfo.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT));
                recyclerV_classCheckInInfo.setLayoutManager(new LinearLayoutManager(mContext));
                recyclerV_classCheckInInfo.setAdapter(new CourseClassesCheckInAdapter(mContext,
                        "1101", mCurrCourseName, listArray_classes, mapArray_classCheckInInfo));
                progressBar.setVisibility(View.GONE);
                progressBar.clearAnimation();
                progressBar.clearFocus();
                fragmentLayout.removeView(progressBar);
                fragmentLayout.addView(recyclerV_classCheckInInfo);
            }
        }
    };

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        if (args.containsKey("courseName")) {
            mCurrCourseName = (String) args.get("courseName");
        } else {
            mCurrCourseName = "";
        }
    }

    @Override
    public void doThing() {
       /* if (!isVisible || !isPrepared || isLoadOnce) {
            return;
        }*/
        //先从数据库读取数据
        Log.i("fragment", "doThing: ");
        //数据库无值,网络加载数据
        final MainBmobQuery2 mainBmobQuery = new MainBmobQuery2(mContext);
        initClassCheckInCardView(mainBmobQuery);
        isLoadOnce = true;
    }
}
