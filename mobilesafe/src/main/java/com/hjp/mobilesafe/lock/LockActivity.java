package com.hjp.mobilesafe.lock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.hjp.mobilesafe.R;

public class LockActivity extends Activity {

    private static final String COM_DUCK_HUSBAND_UNCHECKED = "com.duck.husband.UNCHECKED";
    private EditText etPwd;
    private Context context;
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.lock_ui);
        packageName = getIntent().getStringExtra("packageName");
        etPwd = (EditText) findViewById(R.id.et_pwd);
        etPwd.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (hasFocus) {
                    // //显示软键盘//
                    imm.showSoftInputFromInputMethod(v.getWindowToken(), 0);
                } else {
                    // 隐藏软键盘
                    //
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        });
    }

    public void open(View view) {
        String text = etPwd.getText().toString().trim();
        if (text.equalsIgnoreCase("123")) {
            Intent intent = new Intent(COM_DUCK_HUSBAND_UNCHECKED);
            intent.putExtra("packageName", packageName);
            sendBroadcast(intent);
            finish();
        } else {
            vtoast("密码不对");
        }
    }

    @Override
    public void onBackPressed() {
        /*
		 * <activity android:name="com.android.launcher2.Launcher"
		 * android:launchMode="singleTask" android:clearTaskOnLaunch="true"
		 * android:stateNotNeeded="true" android:theme="@style/Theme"
		 * android:screenOrientation="nosensor"
		 * android:windowSoftInputMode="stateUnspecified|adjustPan">
		 * <intent-filter> <action android:name="android.intent.action.MAIN" />
		 * <category android:name="android.intent.category.HOME" /> <category
		 * android:name="android.intent.category.DEFAULT" /> <category
		 * android:name="android.intent.category.MONKEY"/> </intent-filter>
		 * </activity>
		 */
        // 打开桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        startActivity(intent);
        // super.onBackPressed();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    protected void vtoast(String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
