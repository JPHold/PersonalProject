package com.hjp.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AdvancedToolActivity extends Activity {

    private Context mContext;
    private Button mbtn_query_phonenumber_source;
    private EditText mEditT_input_phoneNumber;

    private TranslateAnimation mTranslateAnim_editT_inputPhoneNumber;
    private Vibrator mVibrator;

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
                    mEditT_input_phoneNumber.startAnimation(mTranslateAnim_editT_inputPhoneNumber);//左右摇摆EditText

                    Toast.makeText(mContext, "号码不能为空", Toast.LENGTH_LONG).show();

                    loadVibrator();//震动提醒
                    return;
                }
                //输入的内容是否为号码
                //手机号码11位,13、14、15、16、17、18开头
                //正则表达式:^[345678]\d{9}$
                if (!phoneNumber.matches("^[345678]\\d{9}$")) {
                    loadAnimation(R.anim.editt_cycle_7);
                    mEditT_input_phoneNumber.startAnimation(mTranslateAnim_editT_inputPhoneNumber);//左右摇摆EditText

                    Toast.makeText(mContext, "请输入正确号码", Toast.LENGTH_LONG).show();

                    loadVibrator();//震动提醒
                    return;
                }
                //从手机号码归属来源数据库查询，数据库还没做出来，先放着
                //这里还要对号码的类型做处理
                //政府服务电话(110，120等)、商业客服电话(工商客服电话等)、集群电话(移动的集群短号566，599等)、座机电话(020-123456)
            }
        }
    };

    private Animation loadAnimation(int animId) {
        switch (animId) {
            case R.anim.editt_cycle_7:
                if (mTranslateAnim_editT_inputPhoneNumber == null) {
                    mTranslateAnim_editT_inputPhoneNumber = (TranslateAnimation) AnimationUtils.loadAnimation(mContext, animId);
                }
                return mTranslateAnim_editT_inputPhoneNumber;

            default:
                return null;
        }

    }

    private void loadVibrator(){
        if(mVibrator==null){
            mVibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        }
        mVibrator.vibrate(2000);
    }

}
