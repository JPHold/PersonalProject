package com.hjp.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hjp.broadcastReceiver.GuardDeviceAdminReceiver;
import com.hjp.constant.Constant;
import com.hjp.customview.LightPointView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by HJP on 2016/8/19 0019.
 */

public class GuardTheftActivity extends Activity implements View.OnClickListener {

    private LinearLayout linearL_root_guardTheft;
    private LightPointView lightPointView;
    private SharedPreferences mSharedPreferences;

    /**
     * 向导第几步
     */
    private int mCurrGuidePosition;
    private EditText mEditT_input_safeNum;
    private boolean mIsOpenGuard = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guardtheft);
        initView();
    }

    private void initView() {
        linearL_root_guardTheft = (LinearLayout) findViewById(R.id.linearL_root_guide_guardPwd);
        //是否做过设置向导
        SharedPreferences sharedPreferences = getSharedPreferences(Constant.NAME_APPCONFIG, MODE_PRIVATE);
        boolean isGuardPwdGuided = sharedPreferences.getBoolean(Constant.KEY_ISGUIDED, false);
        if (isGuardPwdGuided) {
            //做过向导，显示设置的信息
        } else {
            //没做过向导，设置向导开始
            showGuardPwdGuide1();
        }
    }

    /**
     * 状态-绑定sim与否
     */
    private boolean isBandSim = false;
    /**
     * 号码-安全号码(SIM变更后发送到此)
     */
    private int safeNum = 0;

    /**
     * 前一向导页还是后一向导页
     */
    public void showWhichGuidePage() {
        switch (mCurrGuidePosition) {
            case 1:
                showGuardPwdGuide1();
                break;
            case 2:
                showGuardPwdGuide2();
                break;
            case 3:

                //如果没有绑定sim卡，不给第三步
                if (!((boolean) obtainFromSqlite(Constant.KEY_ISBANDSIM))) {
                    return;
                }
                showGuardPwdGuide3();
                break;
            case 4:
                //如果没有设置安全号码，不给第四步
                if (safeNum == 0) {
                    return;
                }
                //插入安全号码到sqlite
                insert2sqlite(Constant.KEY_INTSAFENUM, safeNum);
                showGuardPwdGuide4();
                break;
        }
    }

    /**
     * 跳转到联系人列表的请求code
     */
    public static final int CODE_CHOOSECONTACTS = 1;

    @Override
    public void onClick(View v) {
        int btnId = v.getId();
        switch (btnId) {
            //手机防盗向导第二步
            case R.id.btn_guide1_nextGuide2:
                showGuardPwdGuide2();
                break;
            //点击绑定sim卡
            case R.id.reL_band_sim:
                isBandSim = !isBandSim;
                CheckBox checkB_state_bandSim = (CheckBox) v.findViewById(R.id.checkB_state_bandSim);
                checkB_state_bandSim.setChecked(isBandSim);
                insert2sqlite(Constant.KEY_ISBANDSIM, isBandSim);
                //保存sim的唯一标识
                TelephonyManager tlpManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                String simUniqueSign = null;
                simUniqueSign = tlpManager.getLine1Number();
                //为null，则不保存
                if (simUniqueSign == null || TextUtils.isEmpty(simUniqueSign)) {
                    simUniqueSign = tlpManager.getSimSerialNumber();
                    if (simUniqueSign == null || TextUtils.isEmpty(simUniqueSign)) {
                        return;
                    } else {
                        insert2sqlite(Constant.KEY_STRING_SIMUNIQUESIGN, simUniqueSign);
                    }
                } else {
                    insert2sqlite(Constant.KEY_STRING_SIMUNIQUESIGN, simUniqueSign);
                }

                break;
            case R.id.btn_backGuide:
                mCurrGuidePosition--;
                showWhichGuidePage();
                break;
            case R.id.btn_nextGuide:
                mCurrGuidePosition++;
                showWhichGuidePage();
                break;
            //打开联系人列表
            case R.id.btn_chooseontacts:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivityForResult(intent, CODE_CHOOSECONTACTS);
                break;
            //设置是否开启手机防盗
            case R.id.reL_openGuard:

                CheckBox checkB_state_isOpenGuard = (CheckBox) v.findViewById(R.id.checkB_state_isOpenGuard);
                TextView textV_isOpenGuard = (TextView) v.findViewById(R.id.textv_isOpenGuard);
                //保存开启防盗的状态
                mIsOpenGuard = !mIsOpenGuard;
                checkB_state_isOpenGuard.setChecked(mIsOpenGuard);
                insert2sqlite(Constant.KEY_ISOPENGUARD, mIsOpenGuard);
                textV_isOpenGuard.setText(mIsOpenGuard == true ? "手机防盗已开启" : "手机防盗已关闭");
                break;
            //结束向导
            case R.id.btn_end_guardGuide:
                //必须激活本app作为设备管理器，才能实现四个指令来远程操作并且不会报安全错误
                if (mIsOpenGuard) {
                    boolean isActivation = isDevicePolicyActivation();
                    //没激活，则跳转到激活页面
                    if (!isActivation) {
                        jump2DeviceActivation();
                    } else {
                        //提示：JPH手机防盗app已充当设备管理器,对手机的控制功能已生效
                        Toast.makeText(getApplicationContext(), "JPH手机防盗app已充当设备管理器,对手机的控制功能已生效"
                                , Toast.LENGTH_LONG).show();
                        finish();
                    }
                }

                break;
        }
        lightPointView.setCurrLightPosition(mCurrGuidePosition);
    }

    /**
     * 手机防盗向导的所有步骤
     */
    //第一步
    private void showGuardPwdGuide1() {
        linearL_root_guardTheft.removeAllViews();
        View guide1 = getLayoutInflater().inflate(R.layout.activity_replace_guardpwd_guide1, linearL_root_guardTheft);
        Button btn_nextGuide2 = (Button) guide1.findViewById(R.id.btn_guide1_nextGuide2);
        btn_nextGuide2.setOnClickListener(this);
    }

    //第二步
    private void showGuardPwdGuide2() {
        linearL_root_guardTheft.removeAllViews();

        //添加第二步界面到主页面
        View page_guide2 = getLayoutInflater().inflate(R.layout.activity_replace_guardpwd_guide2, linearL_root_guardTheft);

        //获取sqlite中是否绑定了sim
        boolean isBandSim = (boolean) obtainFromSqlite(Constant.KEY_ISBANDSIM);
        View reL_band_sim = page_guide2.findViewById(R.id.reL_band_sim);
        CheckBox checkB_isBandSim = (CheckBox) reL_band_sim.findViewById(R.id.checkB_state_bandSim);
        checkB_isBandSim.setChecked(isBandSim);

        //设置当前第几步
        mCurrGuidePosition = 2;
        lightPointView.setCurrLightPosition(mCurrGuidePosition);

        InputStream is_guide2 = null;
        try {
            //加载html并转string
            is_guide2 = getResources().getAssets().open("guardpwd_guide2_title.html");
            String content_guide2 = inputStream2string(is_guide2);
            CharSequence title_guide2 = Html.fromHtml(content_guide2);
            //设置html
            TextView textV_title_guide2 = (TextView) page_guide2.findViewById(R.id.textV_guardPwd_guide2_title);
            textV_title_guide2.setMovementMethod(ScrollingMovementMethod.getInstance());//支持滚动html
            textV_title_guide2.setText(title_guide2);

        } catch (IOException e) {
            e.printStackTrace();
            linearL_root_guardTheft.removeAllViews();
        } finally {
            if (is_guide2 != null) {
                try {
                    is_guide2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //设置第二个向导页面的点击
        Button btn_preGuide1 = (Button) page_guide2.findViewById(R.id.btn_backGuide);
        Button btn_nextGuide3 = (Button) page_guide2.findViewById(R.id.btn_nextGuide);

        reL_band_sim.setOnClickListener(this);
        btn_preGuide1.setOnClickListener(this);
        btn_nextGuide3.setOnClickListener(this);
    }

    //第三步
    private void showGuardPwdGuide3() {
        linearL_root_guardTheft.removeAllViews();
        //添加第三步界面到主页面
        View page_guide3 = getLayoutInflater().inflate(R.layout.activity_replace_guardpwd_guide3, linearL_root_guardTheft);
        //获取子view
        mEditT_input_safeNum = (EditText) page_guide3.findViewById(R.id.editT_safeNum);
        Button btn_choose_contacts = (Button) page_guide3.findViewById(R.id.btn_chooseontacts);
        Button btn_preGuide1 = (Button) page_guide3.findViewById(R.id.btn_backGuide);
        Button btn_nextGuide3 = (Button) page_guide3.findViewById(R.id.btn_nextGuide);

        //获取设置的安全号码
        safeNum = (int) obtainFromSqlite(Constant.KEY_INTSAFENUM);
        mEditT_input_safeNum.setText(safeNum);
        //点击：前一页or后一页
        btn_preGuide1.setOnClickListener(this);
        btn_nextGuide3.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_CHOOSECONTACTS) {
            if (data == null) {
                return;
            }
            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            String number_contact = null;
            if (cursor.moveToFirst()) {
                number_contact = cursor.getString(cursor.getColumnIndexOrThrow(Contacts.People.Phones.NUMBER));
                mEditT_input_safeNum.setText(number_contact);
            } else {
                Toast.makeText(getApplicationContext(), "无号码", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_ACTIVATION_DEVICE) {
            //如果用户取消激活，不给结束向导
            if (resultCode == Activity.RESULT_CANCELED) {
                //提示：必须激活设备管理器
                Toast.makeText(getApplicationContext(), "必须激活设备管理器", Toast.LENGTH_LONG).show();
            } else if (requestCode == Activity.RESULT_OK) {
                //提示：JPH手机防盗app已充当设备管理器,对手机的控制功能已生效
                Toast.makeText(getApplicationContext(), "JPH手机防盗app已充当设备管理器,对手机的控制功能已生效"
                        , Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    /**
     * 检测是否激活了设备管理器组件(也就是给本app权利)
     */
    public boolean isDevicePolicyActivation() {
        DevicePolicyManager mDevicePolicyManager = (DevicePolicyManager) getSystemService(Activity.DEVICE_POLICY_SERVICE);

        ComponentName componentName = new ComponentName(getApplicationContext(), GuardDeviceAdminReceiver.class);
        boolean mIsActivation = mDevicePolicyManager.isAdminActive(componentName);//设备管理器是否激活
        return mIsActivation;
    }

    //跳转到激活设备管理器页面
    private static final int REQUEST_CODE_ACTIVATION_DEVICE = 1;

    /**
     * 没有激活设备管理器组件,则跳转到激活页面
     */
    public void jump2DeviceActivation() {
        ComponentName mGuardDeviceAdmin = new ComponentName(getApplicationContext(), GuardDeviceAdminReceiver.class);
        // Launch the activity to have the user enable our admin.
        Intent intent_activation = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent_activation.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mGuardDeviceAdmin);
        intent_activation.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                getString(R.string.add_admin_extra_app_text));
        startActivityForResult(intent_activation, REQUEST_CODE_ACTIVATION_DEVICE);
    }

    //第四步
    private void showGuardPwdGuide4() {
        linearL_root_guardTheft.removeAllViews();
        //添加第四步界面到主页面
        View page_guide4 = getLayoutInflater().inflate(R.layout.activity_replace_guardpwd_guide4, linearL_root_guardTheft);
        //获取子view
        RelativeLayout reL_openGuard = (RelativeLayout) page_guide4.findViewById(R.id.reL_openGuard);
        Button btn_end_guardGuide = (Button) page_guide4.findViewById(R.id.btn_end_guardGuide);
        CheckBox checkB_state_isOpenGuard = (CheckBox) page_guide4.findViewById(R.id.checkB_state_isOpenGuard);
        TextView textV_isOpenGuard = (TextView) page_guide4.findViewById(R.id.textv_isOpenGuard);
        //是否开了手机防盗
        mIsOpenGuard = (boolean) obtainFromSqlite(Constant.KEY_ISOPENGUARD);
        checkB_state_isOpenGuard.setChecked(mIsOpenGuard);
        textV_isOpenGuard.setText(mIsOpenGuard == true ? "手机防盗已开启" : "手机防盗已关闭");

        //点击
        reL_openGuard.setOnClickListener(this);
        btn_end_guardGuide.setOnClickListener(this);
    }

    /**
     * inputStream 转为 string
     */
    private String inputStream2string(InputStream is) throws IOException {
        BufferedReader bis = new BufferedReader(new InputStreamReader(is));
        String s;
        StringBuilder sb = new StringBuilder();
        while ((s = bis.readLine()) != null) {
            sb.append(s);
        }

        bis.close();

        if (sb.length() > 0) {
            return sb.toString();
        }
        return "";
    }

    private void getDefaultSqlite() {
        if (mSharedPreferences == null) {
            mSharedPreferences = getSharedPreferences(Constant.NAME_APPCONFIG, MODE_PRIVATE);
        }
    }

    /**
     * 插入数据到默认sqlite
     */
    private void insert2sqlite(String key, Object data) {
        getDefaultSqlite();
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        if (data instanceof Boolean) {
            edit.putBoolean(key, (Boolean) data);
        }
        edit.commit();
    }

    private Object obtainFromSqlite(String key) {
        getDefaultSqlite();
        if (key.contains("is")) {
            return mSharedPreferences.getBoolean(key, false);
        }
        return null;
    }
}
