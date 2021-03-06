package com.hjp.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.hjp.mobilesafe.R;

import com.hjp.mobilesafe.adapter.MainFunctionAdapter;
import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.utils.AppConfig;
import com.hjp.mobilesafe.utils.ScreenUtils;
import com.hjp.mobilesafe.utils.ShowAppShortCut;

/**
 * Created by HJP on 2016/8/19 0019.
 */

public class MainActivity extends Activity implements GridView.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    private View rootLayout;
    private GridView gridV_main_function;
    private Context mContext;
    private MainFunctionAdapter mMainFunctionAdapter;
    private Dialog mDialog_input_guard_pwd;
    private Dialog mDialog_set_guard_pwd;
    private PopupWindow ppW_command_guardPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

        ShowAppShortCut.show(mContext);
    }

    private void initView() {
        rootLayout = findViewById(R.id.mainActivity_rootLayout);
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
                Log.i(TAG, "onItemClick: showLostFindDialog");
                showLostFindDialog();
                break;

            case 1://号码黑名单
                jump2Activity(CommunicationActivity.class);
                break;
            case 2:
                jump2Activity(AppManagerActivity.class);
                break;
            case 3:
                jump2Activity(ProcessManagerActivity.class);
                break;
            case 4:
                jump2Activity(TrafficManagerActivity.class);
                break;
            case 7://高级工具
                Log.i(TAG, "onItemClick:jump2Activity ");
                jump2Activity(AdvancedToolActivity.class);
                break;
            case 8://设置中心
                jump2Activity(SettingActivity.class);
                break;
        }
    }

    private void showLostFindDialog() {
        new AppConfig(mContext);

        String lostFindPwd = (String) AppConfig.obtainFromSqlite(Constant.KEY_GUARDPWD);
        Log.i(TAG, "showLostFindDialog: " + 3);
        if (!TextUtils.isEmpty(lostFindPwd)) {
            //Dialog：输入密码
            Log.i(TAG, "showLostFindDialog: " + 4);
            showInputPwdDialog();
        } else {
            //Dialog：设置密码
            Log.i(TAG, "showLostFindDialog: " + 5);
            showSetPwdDialog();
        }
    }

    private void showInputPwdDialog() {
        View layout_input_guard_pwd = getLayoutInflater().inflate(R.layout.dialog_guard_theft_input_pwd, null, false);
        final EditText editT_input_pwd = (EditText) layout_input_guard_pwd.findViewById(R.id.editT_dialog_input_pwd);
        //密码正确
        mDialog_input_guard_pwd = new AlertDialog.Builder(this)
                .setTitle("输入防盗密码")
                .setView(layout_input_guard_pwd)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String guardPwd = editT_input_pwd.getText().toString();
                        if (TextUtils.isEmpty(guardPwd)) {
                            Toast.makeText(mContext, "密码为空", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        new AppConfig(mContext);
                        String local_guard_Pwd = (String) AppConfig.obtainFromSqlite(Constant.KEY_GUARDPWD);

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
                        new AppConfig(mContext);
                        //显示安全号码
                        String safeNum = (String) AppConfig.obtainFromSqlite(Constant.KEY_INTSAFENUM);
                        textV_guardPwd_safeNum.setText(safeNum);
                        //显示是否开了手机防盗
                        boolean isOpenGuard = (boolean) AppConfig.obtainFromSqlite(Constant.KEY_ISOPENGUARD);
                        imgV_guardPwd_safeNum.setImageResource(isOpenGuard == true ? R.mipmap.guard_open : R.mipmap.guard_close);
                        //显示指令
                        webV_command_guardPwd.loadUrl("file:///android_asset/command_guardpwd.html");

                        ppW_command_guardPwd = new PopupWindow(popLayout_guardPwd_usage);

//                        ppW_command_guardPwd.setAnimationStyle(R.style.anim_ppW);
                        ppW_command_guardPwd.setFocusable(true);
                        ppW_command_guardPwd.setOutsideTouchable(true);
                        ppW_command_guardPwd.setBackgroundDrawable(new BitmapDrawable());

                        ScreenUtils.initScreen(MainActivity.this);
                        ppW_command_guardPwd.setWidth(ScreenUtils.getScreenW());
                        ppW_command_guardPwd.setHeight(ScreenUtils.getScreenH() / 2);

                        //当前Activity的decoderView
                        View rootView = findViewById(android.R.id.content);
                        ppW_command_guardPwd.showAsDropDown(rootView, 0, -500);
                    }
                })
                .create();
        mDialog_input_guard_pwd.show();

    }

    private void showSetPwdDialog() {
        Log.i(TAG, "showSetPwdDialog: " + 1);
        View layout_set_guard_pwd = View.inflate(this, R.layout.dialog_guard_theft_set_pwd, null);

        final EditText editT_set_pwd = (EditText) layout_set_guard_pwd.findViewById(R.id.editT_dialog_set_pwd);
        final EditText editT_set_pwd_against = (EditText) layout_set_guard_pwd.findViewById(R.id.editT_dialog_set_pwd_against);

        Log.i(TAG, "showSetPwdDialog: " + 2);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
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
                        new AppConfig(mContext);
                        AppConfig.insert2sqlite(Constant.KEY_GUARDPWD, guardPwd_2);

                        //手机防盗向导开始
                        StartGuardGuide();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i(TAG, "onClick: " + 5);
                    }
                });
      /*  AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("hhhhhh")
                .setPositiveButton("one", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("two", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });*/
        Log.i(TAG, "showSetPwdDialog: " + 3);
        mDialog_set_guard_pwd = builder.show();
        Log.i(TAG, "showSetPwdDialog: " + 4);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_restart_guard_guide:
                //重置防盗向导标志
                new AppConfig(mContext);
                AppConfig.insert2sqlite(Constant.KEY_ISGUIDED, false);
                //跳转到防盗向导页面com/hjp/mobilesafe/activity/GuardTheftActivity
                StartGuardGuide();
                break;
        }
    }

    /**
     * 手机防盗向导开始
     */
    private void StartGuardGuide() {

        //第一次设置防盗向导
        if (mDialog_set_guard_pwd != null) {
            mDialog_set_guard_pwd.dismiss();
        } else {
            //重新设置防盗向导
            if (ppW_command_guardPwd != null) {
                ppW_command_guardPwd.dismiss();
            }
        }
        Log.i(TAG, "StartGuardGuide: 5");
        Intent intent = new Intent(MainActivity.this, GuardTheftActivity.class);
        Log.i(TAG, "StartGuardGuide: 6");
        MainActivity.this.startActivity(intent);
        Log.i(TAG, "StartGuardGuide: 7");
    }

    /**
     * 跳转到指定Activity
     */
    private void jump2Activity(Class<?> jumpClass) {
        Intent intent_activity = new Intent(this, jumpClass);
        startActivity(intent_activity);
    }
}
