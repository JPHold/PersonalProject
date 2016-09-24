package com.hjp.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterViewAnimator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.listener.DataListener;
import com.hjp.mobilesafe.lock.LockMainActivity;
import com.hjp.mobilesafe.utils.SQLiteDbManager;
import com.hjp.mobilesafe.utils.SendInModuleUtil;
import com.hjp.mobilesafe.utils.SmsBackupUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.R.attr.path;


public class AdvancedToolActivity extends Activity {

    private String TAG = "AdvancedToolActivity";
    /**
     * 电话来电去电显示的号码数据库
     */
    public static final String PHONESOURCEPATH = "data/data/com.hjp.mobilesafe/database";
    public static final String PHONESOURCENAME = "phonesource.db";
    public static final String PHONESOURCE_TABLE = "phonesource";
    public static final String PHONESOURCE_KEY_CITY = "city";
    public static final String PHONESOURCE_KEY_CARDTYPE = "cardtype";
    public static final String PHONESOURCE_KEY_PREFIX = "prefix";

    private Context mContext;
    private Button mbtn_query_phonenumber_source;
    private EditText mEditT_input_phoneNumber;
    private TextView mTextV_phoneSource;

    private TranslateAnimation mTranslateAnim_editT_inputPhoneNumber;
    private Vibrator mVibrator;
    private Button btn_lockApp;
    /**
     * 短信备份模块
     */
    private Button btn_backUpSms;
    private ProgressDialog pd;
    private static final int SIGN_SHOWBACKUPRATE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advancedtool);
        initView();
    }

    private void initView() {
        mContext = getApplicationContext();
        mEditT_input_phoneNumber = (EditText) findViewById(R.id.editT_advanceTool_input_phoneNumber);
        mbtn_query_phonenumber_source = (Button) findViewById(R.id.btn_advanceTool_query_phoneNumber_source);
        mTextV_phoneSource = (TextView) findViewById(R.id.textV_phoneSource);
        mbtn_query_phonenumber_source.setOnClickListener(clickListener);

        btn_backUpSms = (Button) findViewById(R.id.btn_backUpSms);
        btn_backUpSms.setOnClickListener(clickListener);

        btn_lockApp = (Button) findViewById(R.id.btn_lockApp);
        btn_lockApp.setOnClickListener(clickListener);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int clickId = v.getId();
            if (clickId == R.id.btn_advanceTool_query_phoneNumber_source) {
                String phoneNumber = mEditT_input_phoneNumber.getText().toString();
                //输入内容是否为空
                if (TextUtils.isEmpty(phoneNumber)) {
                    loadAnimation(R.anim.editt_shake);
                    Log.i(TAG, "onClick: mEditT_input_phoneNumber" + (mEditT_input_phoneNumber == null));
                    Log.i(TAG, "onClick: mTranslateAnim_editT_inputPhoneNumber" + (mTranslateAnim_editT_inputPhoneNumber == null));
                    mEditT_input_phoneNumber.startAnimation(mTranslateAnim_editT_inputPhoneNumber);//左右摇摆EditText

                    Toast.makeText(mContext, "号码不能为空", Toast.LENGTH_LONG).show();

                    loadVibrator();//震动提醒
                    return;
                }
                //输入的内容是否为号码
                //手机号码11位,13、14、15、16、17、18开头
                //正则表达式:^[345678]\d{9}$
                if (!phoneNumber.matches("^1[345678]\\d{9}$")) {
                    loadAnimation(R.anim.editt_shake);
                    mEditT_input_phoneNumber.startAnimation(mTranslateAnim_editT_inputPhoneNumber);//左右摇摆EditText

                    Toast.makeText(mContext, "请输入正确号码", Toast.LENGTH_LONG).show();

                    loadVibrator();//震动提醒
                    return;
                }
                //从手机号码归属来源数据库查询
                //这里还要对号码的类型做处理
                String prefix_phone = phoneNumber.substring(0, 7);
                //政府服务电话(110，120等)、商业客服电话(工商客服电话等)、集群电话(移动的集群短号566，599等)、座机电话(020-123456)
                SQLiteDatabase phoneSourceDb = SQLiteDbManager.openDatabase(getApplicationContext(), PHONESOURCEPATH, PHONESOURCENAME);
                Cursor phoneSourceCursor = phoneSourceDb.query("phonesource", new String[]{PHONESOURCE_KEY_CITY, PHONESOURCE_KEY_CARDTYPE}
                        , PHONESOURCE_KEY_PREFIX + "=? ", new String[]{prefix_phone}, null, null, null);

                String phoneSource = null;
                if (phoneSourceCursor.moveToNext()) {
                    String city = phoneSourceCursor.getString(phoneSourceCursor.getColumnIndex(PHONESOURCE_KEY_CITY));
                    String cardType = phoneSourceCursor.getString(phoneSourceCursor.getColumnIndex(PHONESOURCE_KEY_CARDTYPE));
                    phoneSource = city + "  " + cardType;
                }
                if (TextUtils.isEmpty(phoneSource)) {
                    phoneSource = "暂无此号码的来源";
                }
                mTextV_phoneSource.setText(phoneSource);
            } else if (clickId == R.id.btn_backUpSms) {
                //跳到文件管理系统，选择保存路径

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
                    String time = simpleDateFormat.format(new Date());
                    String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + getPackageName();//文件名过长是创建不了的
                    Log.i(TAG, "onClick: " + dir);
                    String fileName = "sms.xml";


                    pd = new ProgressDialog(AdvancedToolActivity.this);
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.setMessage("正在备份中");
                    pd.setCanceledOnTouchOutside(true);
                    pd.show();
                    //开始备份

                    try {
                        SmsBackupUtil.smsBackup(mContext, dir, fileName, new SmsBackupUtil.DataListener() {
                            @Override
                            public void onDataCount(final int count) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.setMax(count);
                                    }
                                });
                            }

                            @Override
                            public void onCurrPosition(int position) {
                                SendInModuleUtil.sendMsgByHandler(SIGN_SHOWBACKUPRATE, position, handler);
                            }

                            @Override
                            public void onEnd() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        pd.dismiss();
                                        SendInModuleUtil.showToast(mContext, "备份短信成功");
                                    }
                                });
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (pd != null) {
                            pd.dismiss();
                            pd = null;
                        }
                    }
                }
            }
            if (clickId == R.id.btn_lockApp) {
                Intent intent = new Intent(AdvancedToolActivity.this, LockMainActivity.class);
                startActivity(intent);
            }
        }
    };

    private Animation loadAnimation(int animId) {
        switch (animId) {
            case R.anim.editt_shake:
                if (mTranslateAnim_editT_inputPhoneNumber == null) {
                    mTranslateAnim_editT_inputPhoneNumber = (TranslateAnimation) AnimationUtils.loadAnimation(mContext, animId);
                }
                return mTranslateAnim_editT_inputPhoneNumber;

            default:
                return null;
        }

    }

    private void loadVibrator() {
        if (mVibrator == null) {
            mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }
        mVibrator.vibrate(1000);
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            Object data = msg.obj;
            switch (what) {
                case 1:
                    pd.setProgress((Integer) data);
                    break;
                case 2:

                    break;
            }
        }
    };

}
