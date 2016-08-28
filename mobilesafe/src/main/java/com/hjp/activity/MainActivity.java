package com.hjp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hjp.adapter.MainFunctionAdapter;
import com.hjp.constant.Constant;
import com.hjp.utils.AppConfig;
import com.hjp.utils.ScreenUtils;

/**
 * Created by HJP on 2016/8/19 0019.
 */

public class MainActivity extends Activity implements GridView.OnItemClickListener, View.OnClickListener {
    private GridView gridV_main_function;
    private Context mContext;
    private MainFunctionAdapter mMainFunctionAdapter;
    private Dialog mDialog_input_guard_pwd;
    private Dialog mDialog_set_guard_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {
        gridV_main_function = (GridView) findViewById(R.id.gridV_main_function);
        mContext = getApplicationContext();
        mMainFunctionAdapter = new MainFunctionAdapter(mContext);
        gridV_main_function.setAdapter(mMainFunctionAdapter);
        gridV_main_function.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0://手机防盗
                showLostFindDialog();
                break;
            case 7://高级工具
                jump2Activity(AdvancedToolActivity.class);
                break;
        }
    }

    private void showLostFindDialog() {
        SharedPreferences config = getSharedPreferences(Constant.NAME_APPCONFIG, MODE_PRIVATE);
        String lostFindPwd = config.getString(Constant.KEY_GUARDPWD, null);
        if (!TextUtils.isEmpty(lostFindPwd)) {
            //Dialog：输入密码
            showInputPwdDialog();
        } else {
            //Dialog：设置密码
            showSetPwdDialog();
        }
    }

    private void showInputPwdDialog() {
        View layout_input_guard_pwd = getLayoutInflater().inflate(R.layout.dialog_guard_theft_input_pwd, null);
        final EditText editT_input_pwd = (EditText) layout_input_guard_pwd.findViewById(R.id.editT_dialog_input_pwd);
        //密码正确
        mDialog_input_guard_pwd = new AlertDialog.Builder(mContext)
                .setTitle("输入防盗密码")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String guardPwd = editT_input_pwd.getText().toString();
                        if (TextUtils.isEmpty(guardPwd)) {
                            Toast.makeText(mContext, "密码为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences(Constant.NAME_APPCONFIG, MODE_PRIVATE);
                        String local_guard_Pwd = sharedPreferences.getString(Constant.KEY_GUARDPWD, null);
                        if (TextUtils.isEmpty(local_guard_Pwd)) {
                            Toast.makeText(mContext, "本地密码未设置", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!local_guard_Pwd.equals(guardPwd)) {
                            Toast.makeText(mContext, "密码错误", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //密码正确，弹出pop：显示手机防盗的操作命令
                        mDialog_input_guard_pwd.dismiss();

                        View popLayout_guardPwd_usage = getLayoutInflater().inflate(R.layout.pop_guardpwd_usage, null);
                        //子view获取
                        Button btn_restart_guard_guide = (Button) popLayout_guardPwd_usage.findViewById(R.id.btn_restart_guard_guide);
                        ImageView imgV_guardPwd_safeNum = (ImageView) popLayout_guardPwd_usage.findViewById(R.id.imgV_guardPwd_isOpenGuard);
                        TextView textV_guardPwd_safeNum = (TextView) popLayout_guardPwd_usage.findViewById(R.id.textV_guardPwd_safeNum);
                        WebView webV_command_guardPwd = (WebView) popLayout_guardPwd_usage.findViewById(R.id.webV_command_guardPwd);
                        //重新设置手机防盗向导
                        btn_restart_guard_guide.setOnClickListener(MainActivity.this);
                        AppConfig appConfig = new AppConfig(mContext);
                        //显示安全号码
                        int safeNum = (int) AppConfig.obtainFromSqlite(Constant.KEY_INTSAFENUM);
                        textV_guardPwd_safeNum.setText(safeNum);
                        //显示是否开了手机防盗
                        boolean isOpenGuard = (boolean) AppConfig.obtainFromSqlite(Constant.KEY_ISOPENGUARD);
                        imgV_guardPwd_safeNum.setImageResource(isOpenGuard == true ? R.mipmap.guard_open : R.mipmap.guard_close);
                        //显示指令
                        webV_command_guardPwd.loadUrl("file:///android_asset/command_guardpwd.html");

                        PopupWindow ppW_command_guardPwd = new PopupWindow(popLayout_guardPwd_usage);

                        //制作从下往上弹出pop的动画
                        TranslateAnimation tlAnim = new TranslateAnimation(getApplicationContext(), null);
                        tlAnim.setDuration(2000);
                        //自动计算初始位置和终点位置
                        tlAnim.initialize((int) (500 * ScreenUtils.getScreenDensity()), (int) (300 * ScreenUtils.getScreenDensity())
                                , ScreenUtils.getScreenW(), ScreenUtils.getScreenH());
                        //当前Activity的decoderView
                        View rootView = findViewById(android.R.id.content);
                        ppW_command_guardPwd.showAsDropDown(rootView);
                    }
                })
                .create();
//        mDialog_input_guard_pwd.setContentView(layout_input_guard_pwd);
        mDialog_input_guard_pwd.show();

    }

    private void showSetPwdDialog() {
        View layout_set_guard_pwd = getLayoutInflater().inflate(R.layout.dialog_guard_theft_set_pwd, null);
        final EditText editT_set_pwd = (EditText) layout_set_guard_pwd.findViewById(R.id.editT_dialog_set_pwd);
        final EditText editT_set_pwd_against = (EditText) layout_set_guard_pwd.findViewById(R.id.editT_dialog_set_pwd_against);
        mDialog_set_guard_pwd = new AlertDialog.Builder(mContext)
                .setView(layout_set_guard_pwd)
                .setTitle("设置防盗密码")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String guardPwd_1 = editT_set_pwd.getText().toString();
                        String guardPwd_2 = editT_set_pwd_against.getText().toString();
                        if (TextUtils.isEmpty(guardPwd_1) || TextUtils.isEmpty(guardPwd_2)) {
                            Toast.makeText(mContext, "密码为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!guardPwd_1.equals(guardPwd_2)) {
                            Toast.makeText(mContext, "两次密码不相同", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        SharedPreferences sharedPreferences = getSharedPreferences(Constant.NAME_APPCONFIG, MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString(Constant.KEY_GUARDPWD, guardPwd_2);
                        edit.commit();

                        //手机防盗向导开始
                        StartGuardGuide();
                    }
                })
                .create();
        mDialog_set_guard_pwd.show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_restart_guard_guide:
                StartGuardGuide();
                break;
        }
    }

    /**
     * 手机防盗向导开始
     */
    private void StartGuardGuide() {
        Intent intent = new Intent(MainActivity.this, GuardTheftActivity.class);
        startActivity(intent);
    }

    /**
     * 跳转到指定Activity
     */
    private void jump2Activity(Class<?> jumpClass) {
        Intent intent_activity = new Intent(this, jumpClass);
        startActivity(intent_activity);
    }
}
