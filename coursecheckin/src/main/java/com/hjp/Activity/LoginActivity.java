package com.hjp.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.extraFunction.Activity.GestureLockActivity;
import com.extraFunction.Cache.AppConfig;
import com.hjp.Activity.student.StudentActivity;
import com.hjp.Activity.teacher.TeacherMainActivity;
import com.hjp.Application.MainApplication;
import com.hjp.Database.AppDataBase;
import com.hjp.NetWork.MainBmobQuery;
import com.hjp.coursecheckin.R;
import com.util.CheckUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HJP on 2016/5/28 0028.
 */
public class LoginActivity extends Activity {

    private final String TAG = this.getClass().getName();

    private Context mContext;
    private MainApplication mApp;

    private EditText editT_user;
    private EditText editT_pwd;
    private Spinner spinner_loginType;
    private String loginType;
    private Button btn_login;
    private ImageView imageV_loginWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_login);
        initView();
        setAdapter();
        addListener();
    }

    private void initView() {
        mContext = getApplicationContext();
        mApp = (MainApplication) getApplication();

        editT_user = (EditText) findViewById(R.id.editT_user);
        editT_pwd = (EditText) findViewById(R.id.editT_pwd);
        spinner_loginType = (Spinner) findViewById(R.id.spinner_loginType);
        btn_login = (Button) findViewById(R.id.btn_login);
        imageV_loginWait = (ImageView) findViewById(R.id.imageV_loginWait);
    }

    private void setAdapter() {
        ArrayAdapter<CharSequence> adapter_spinner_loginType = ArrayAdapter.createFromResource(mContext,
                R.array.loginType, R.layout.cell_login_type);
        adapter_spinner_loginType.setDropDownViewResource(R.layout.cell_logintype_drop);
        spinner_loginType.setAdapter(adapter_spinner_loginType);

    }

    private void addListener() {
        btn_login.setOnClickListener(clickListener);
        //setOitemClickListener和setOnClickListener都会出错
        spinner_loginType.setOnItemSelectedListener(itemSelectedListener);
    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            loginType = (String) parent.getItemAtPosition(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof Button) {
                switch (((Button) v).getId()) {
                    case R.id.btn_login:
                        checkUserRequestLoginInfo();
                        break;
                }
            }
        }
    };

    private void checkUserRequestLoginInfo() {
        MainBmobQuery bmobQuery = new MainBmobQuery(mContext);

        final String requestUser = editT_user.getText().toString();
        String requestPwd = editT_pwd.getText().toString();

        if (requestUser != null && requestUser.length() > 0) {
            if (requestPwd != null && requestPwd.length() > 0) {
                if (loginType != null && loginType.length() > 0) {
                    AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) imageV_loginWait.getBackground();
                    imageV_loginWait.setVisibility(View.VISIBLE);
                    drawable.start();
                    bmobQuery.processRequestLogin(loginType, requestUser, requestPwd, new MainBmobQuery.UserRequestLoginListener() {
                        @Override
                        public void msg(String msg) {
                            Message handler_msg = Message.obtain(loginHandler);
                            switch (msg) {
                                case "密码正确":
                                    handler_msg.what = 1;
                                    handler_msg.obj = requestUser;
                                    break;
                                default:
                                    //SeekBar提示"密码错误"、"无此用户"
                                    handler_msg.what = 0;
                                    handler_msg.obj = msg;
                                    break;
                            }
                            handler_msg.sendToTarget();
                        }
                    });
                } else {
                    //SeekBar提示用户类型未选择
                }
            } else {
                //SeekBar提示密码未输入
            }
        } else {
            //SeekBar提示用户名未输入
        }

    }

    private Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (1 == msg.what) {
                Log.i(TAG, "handleMessage: 1");
                String user = (String) msg.obj;
                ((MainApplication) getApplication()).setCurrUser(user);
                //防止出现Application存的用户因异常情况而丢失的情况,将用户和登录状态保存到数据库
                AppDataBase dataBase = new AppDataBase(mContext, "user.db", null, 1);
                dataBase.setProcessTable("create table if not exists userstate" +
                        "(createdAt Text,userName Text,loginState)");
                Map<String, String> map_data = new HashMap<>();
                map_data.put("userName", user);
                map_data.put("loginState", "yes");
                dataBase.insert("userstate", null, map_data);

                Intent intent_jumpActivity = new Intent();
                intent_jumpActivity.putExtra("loginState", "yes");
                intent_jumpActivity.putExtra("user", user);

                //如果首次使用app,本地是没加密密码的,再从云端获取加密密码,如果都没有则设置手势锁
                AppConfig mAppConfig = mApp.getAppConfig();
                String mPassword = mAppConfig.getKeyGesturePassword();
                if (mPassword.equals("")) {
                    intent_jumpActivity.setClass(mContext, GestureLockActivity.class);
                    intent_jumpActivity.putExtra(GestureLockActivity.KEY_ISFIRST, true);
                    startActivity(intent_jumpActivity);
                    finish();
                    return;
                }

                if ("教师".equals(loginType)) {
                    intent_jumpActivity.setClass(mContext, TeacherMainActivity.class);
                } else if ("学生".equals(loginType)) {
                    //学生Activity
                    intent_jumpActivity.setClass(mContext, StudentActivity.class);
                }

                startActivity(intent_jumpActivity);
                finish();
                ((AnimatedVectorDrawable) imageV_loginWait.getBackground()).stop();
            } else if (0 == msg.what) {
                Log.i(TAG, "handleMessage: 2");
                String errorMsg = (String) msg.obj;
                Log.i("bmob", errorMsg);
                //SeekBar提示"密码错误"、"无此用户"
            }
        }
    };
}
