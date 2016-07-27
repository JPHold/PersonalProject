package com.hjp.Activity.checkInInfo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.hjp.Activity.checkInPointMap.CheckInPointMapActivity;
import com.hjp.Adapter.ClassCheckInInfoAdapter;
import com.hjp.Adapter.ClassCheckInTypeStudentAdapter;
import com.hjp.Application.MainApplication;
import com.hjp.coursecheckin.R;
import com.hjp.service.TaskIntentService;
import com.hjp.service.TaskIntentService.DataReturnListener;
import com.util.ObtainAttrUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by HJP on 2016/6/14 0014.
 */
public class ClassCheckInInfoActivity extends Activity {

    private String jobNum;
    private String courseName;
    private String className;
    private RecyclerView recyV_checkInfo_specialClass;
    private Context mContext;
    private TaskIntentService queryService;
    private DataReturnListener dataListener;
    private Intent jumpServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isNight = MainApplication.mAppConfig.getNightModeSwitch();
        if (isNight) {
            this.setTheme(R.style.nightTheme);
            int color = getResources().getColor(R.color.night_baseColor);
            findViewById(android.R.id.content).setBackgroundColor(color);
        } else {
            this.setTheme(R.style.DefaultTheme);
        }

        setContentView(R.layout.activity_checkininfo_specialclass);
        initView();
        initResource();
        initAdapter();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (jumpServiceIntent != null)
            stopService(jumpServiceIntent);
    }

    private ServiceConnection actConnSer;

    private void initAdapter() {
        jumpServiceIntent = new Intent(ClassCheckInInfoActivity.this, TaskIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putString("jobNum", jobNum);
        bundle.putString("courseName", courseName);
        bundle.putString("className", className);
        jumpServiceIntent.putExtras(bundle);
        jumpServiceIntent.setAction("com.hjp.queryExtraCheckInInfo");
        actConnSer = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i("test", "onServiceConnected: ");
                dataListener = new DataReturnListener() {
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
        startService(jumpServiceIntent);

    }

    private void initResource() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getBundleExtra("classCheckInInfo");
            if (bundle.containsKey("jobNum")) {
                jobNum = bundle.getString("jobNum");
                if (bundle.containsKey("courseName")) {
                    courseName = bundle.getString("courseName");
                } else {
                    courseName = "";
                }
                if (bundle.containsKey("className")) {
                    className = bundle.getString("className");
                } else {
                    className = "";
                }
            } else {//重新加载TeacherMainActivity}
            }
        }
    }

    private void initView() {
        mContext = getApplicationContext();
        recyV_checkInfo_specialClass = (RecyclerView) findViewById(R.id.recyV_checkInfo_specialClass);
        recyV_checkInfo_specialClass.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case 1:
                    ArrayList<LinkedHashMap<String, String>> exactCheckInData =
                            (ArrayList<LinkedHashMap<String, String>>) msg.obj;
                    ClassCheckInInfoAdapter adapter =
                            new ClassCheckInInfoAdapter(mContext, jobNum, courseName, className, exactCheckInData);
                    adapter.setJumpPointMapActivity(new ClassCheckInTypeStudentAdapter.JumpCheckInPointMapActivityListener() {
                        @Override
                        public void jump(Bundle bundle) {
                            Intent intent = new Intent(ClassCheckInInfoActivity.this, CheckInPointMapActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    recyV_checkInfo_specialClass.setAdapter(adapter);

                    break;
            }
        }
    };

}
