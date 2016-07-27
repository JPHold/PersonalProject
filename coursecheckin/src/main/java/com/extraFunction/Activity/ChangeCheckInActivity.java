package com.extraFunction.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.BaseContent.CallListener;
import com.BaseContent.QueryListener;
import com.extraFunction.NetWork.ChangeCheckInBmobQuery;
import com.hjp.Application.MainApplication;
import com.hjp.coursecheckin.R;
import com.hjp.vo.ComputerCheckIn;
import com.util.CheckUtil;
import com.util.ObtainAttrUtil;
import com.util.ProgressBarUtil;
import com.util.SendInModuleUtil;
import com.util.UserVerificationUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by HJP on 2016/6/28 0028.
 */
public class ChangeCheckInActivity extends Activity {

    private final String TAG = "ChangeCheckInActivity";

    private LinearLayout mRootLayout;
    private EditText ediT_changeCheckIn_stuNum;
    private RadioGroup radioG_courses;
    private RadioButton radioBtn_class;
    private Context mContext;
    private CheckBox checkB_defaultCheckInType;
    private CheckBox checkB_comLateCheckInType;
    private CheckBox checkB_leaveEarlyCheckInType;
    private CheckBox checkB_exceedCheckInType;
    private Button mBtn_sureEdit;
    private Button mBtn_noEdit;

    private ChangeCheckInBmobQuery bmobQuery;
    private String currStuNum;
    private String teacherNum;
    private String selectCourse;
    private String currClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();

        boolean isNight = MainApplication.mAppConfig.getNightModeSwitch();
        if (isNight) {
            this.setTheme(R.style.nightTheme);
        } else {
            this.setTheme(R.style.DefaultTheme);
        }


        setContentView(R.layout.extrafunction_changecheckin);
        initView();
        addListener();
    }

    private void addListener() {

        ediT_changeCheckIn_stuNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable stuNum) {
                if (stuNum.length() == 10) {
                    SendInModuleUtil.sendMsgByHandler(0, null, handler);

                    currStuNum = stuNum.toString();
                    teacherNum = UserVerificationUtil.verifyCurrUserFromApplication(mContext, ChangeCheckInActivity.this);

                    bmobQuery = new ChangeCheckInBmobQuery(mContext);
                    bmobQuery.getCheckInedCourses(currStuNum, teacherNum,
                            new QueryListener() {
                                @Override
                                public void data(List<Serializable> checkInedCourses) {
                                    SendInModuleUtil.sendMsgByHandler(1, checkInedCourses, handler);
                                }

                                @Override
                                public void error(String msg) {
                                    Log.i(TAG, "error: " + msg);
                                }
                            });
                }
            }
        });
        mBtn_sureEdit.setOnClickListener(clickListener);
        mBtn_noEdit.setOnClickListener(clickListener);
    }

    private void refreshUI(int type, ArrayList<String> data) {
        //对data进行null检查,如果type被改变为0,则不进行UI更新
        CheckUtil.checkNullOrZero(type, data);
        if (type == 0) {
            return;
        } else if (type == 1) {
            Log.i(TAG, "refreshUI: " + 1);

            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(WRAP_CONTENT
                    , WRAP_CONTENT);
            TypedArray typedArray = ObtainAttrUtil.getSpecialTypedArray(mContext, R.attr.radioBtnTextColor);
            int color = typedArray.getColor(0, mContext.getResources().getColor(R.color.default_radioBtnTextColor));


            radioG_courses.removeAllViews();
            //显示签到过的课程。在查询签到过的课程中,已经剔除课程为null或为""。所以这里尽情更新UI即可
            int size = data.size();
            String currCourse = null;
            for (int i = 0; i < size; i++) {

                RadioButton radioBtn_oneCourse = new RadioButton(mContext);
                radioBtn_oneCourse.setLayoutParams(layoutParams);
                radioBtn_oneCourse.setTextColor(color);


                currCourse = data.get(i);

                radioBtn_oneCourse.setText(currCourse);
                //监听点击要修改的课程,然后网络加载同学当前课程的班级
                radioBtn_oneCourse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            SendInModuleUtil.sendMsgByHandler(0, null, handler);
                            selectCourse = (String) buttonView.getText();

                            //在ChangeCheckInBmobQuery.getCoursesFromQueryResult方法内
                            // 检查了网络结果的null和zero,有其中一个情况,data都为null
                            if (bmobQuery == null) {
                                bmobQuery = new ChangeCheckInBmobQuery(mContext);
                            }

                            bmobQuery.getCheckInedClass(currStuNum, teacherNum, selectCourse,
                                    new QueryListener() {
                                        @Override
                                        public void data(List<Serializable> checkInedCourses) {
                                            SendInModuleUtil.sendMsgByHandler(2, checkInedCourses, handler);
                                        }

                                        @Override
                                        public void error(String msg) {
                                            Log.i(TAG, "error: " + msg);
                                        }
                                    });
                        }
                    }
                });


                radioG_courses.addView(radioBtn_oneCourse);
            }

        } else if (type == 2) {
            Log.i(TAG, "refreshUI: " + 2);
            //显示班级和签到情况
            currClass = data.get(0);
            radioBtn_class.setVisibility(View.VISIBLE);
            radioBtn_class.setChecked(true);
            radioG_courses.setClickable(false);
            radioBtn_class.setText(currClass);

            int size = data.size();
            //有四种情况,根据网络结果,可能存入"1"、"2"、"3"、”4“,一个学生的签到情况数是不定的。所以取余数便知道签到情况数
            int checkInTypeHasCount = (size - 1) % 4;
            //整除的,签到情况数位4
            if (checkInTypeHasCount == 0) {
                checkInTypeHasCount = 4;
            }

            for (int i = 1; i <= checkInTypeHasCount; i++) {
                String s = data.get(i);
                switch (s) {
                    case "1":
                        checkB_defaultCheckInType.setChecked(true);
                        break;
                    case "2":
                        checkB_exceedCheckInType.setChecked(true);
                        break;
                    case "3":
                        checkB_comLateCheckInType.setChecked(true);
                        break;
                    case "4":
                        checkB_leaveEarlyCheckInType.setChecked(true);
                        break;
                }
            }
            //这时就可以修改签到了
            checkB_defaultCheckInType.setClickable(true);
            checkB_exceedCheckInType.setClickable(true);
            checkB_comLateCheckInType.setClickable(true);
            checkB_leaveEarlyCheckInType.setClickable(true);
            //如果是正常,那么其他情况就不能修改
            checkB_defaultCheckInType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkB_exceedCheckInType.setClickable(false);
                        checkB_comLateCheckInType.setClickable(false);
                        checkB_leaveEarlyCheckInType.setClickable(false);
                        return;
                    }
                    checkB_exceedCheckInType.setClickable(true);
                    checkB_comLateCheckInType.setClickable(true);
                    checkB_leaveEarlyCheckInType.setClickable(true);
                }
            });

        }
        SendInModuleUtil.sendMsgByHandler(3, null, handler);
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;

            switch (what) {
                case 0:
                    Log.i(TAG, "handleMessage: " + 0);
                    ProgressBarUtil.showProgressBar(mContext, mRootLayout);
                    break;
                case 1:
                case 2:
                    ArrayList<String> data = (ArrayList<String>) msg.obj;
                    if (data == null || data.size() <= 0) {
                        ProgressBarUtil.removeProgressBar();
                        return;
                    }
                    Log.i(TAG, "handleMessage: " + what);
                    data = (ArrayList<String>) msg.obj;
                    refreshUI(what, data);
                    break;
                case 3:
                    Log.i(TAG, "handleMessage: " + 3);
                    ProgressBarUtil.removeProgressBar();
                    break;
            }

        }
    };

    public View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_sureEdit:

                    SendInModuleUtil.sendMsgByHandler(0, null, handler);

                    //获取更新id
                    if (bmobQuery == null) {
                        bmobQuery = new ChangeCheckInBmobQuery(mContext);
                    }
                    Map<String, Object> where = new HashMap<>();
                    where.put(ComputerCheckIn.KEY_JOBNUM, teacherNum);
                    where.put(ComputerCheckIn.KEY_STUNUM, currStuNum);
                    where.put(ComputerCheckIn.KEY_COURSENAME, selectCourse);
                    where.put(ComputerCheckIn.KEY_CLASSNAME, currClass);
                    String queryKeys = ComputerCheckIn.KEY_OBJECTID;

                    bmobQuery.getData(where, queryKeys, new QueryListener() {
                        @Override
                        public void data(List<Serializable> data) {
                            //修改的唯一凭证
                            String id = (String) data.get(0);
                            Log.i(TAG, "唯一凭证 " + id);

                            //获取修改的签到类型
                            Boolean defaultType = checkB_defaultCheckInType.isChecked();
                            Boolean comeLate = checkB_comLateCheckInType.isChecked();
                            Boolean leaveEarly = checkB_leaveEarlyCheckInType.isChecked();
                            Boolean exceed = checkB_exceedCheckInType.isChecked();
                            //存放
                            Map<Serializable, Serializable> d = new HashMap<>();
                            d.put(ComputerCheckIn.KEY_OBJECTID, id);

                            //正常签到,那么就不存在其他签到类型
                            if (defaultType == true) {
                                //没有离开教室
                                d.put(ComputerCheckIn.KEY_EXCEED, !defaultType);
                            } else {
                                d.put(ComputerCheckIn.KEY_COMLATE, comeLate);
                                d.put(ComputerCheckIn.KEY_LEAVEEARLY, leaveEarly);
                                d.put(ComputerCheckIn.KEY_EXCEED, exceed);
                            }
                            if (bmobQuery == null) {
                                bmobQuery = new ChangeCheckInBmobQuery(mContext);
                            }
                            bmobQuery.update(d, new CallListener() {
                                @Override
                                public void call(Object data) {
                                    if (((String) data) == "success") {
                                        SendInModuleUtil.showToast(mContext, (String) data);
                                        SendInModuleUtil.sendMsgByHandler(3, null, handler);
                                    }
                                }
                            });
                        }

                        @Override
                        public void error(String msg) {
                            SendInModuleUtil.showToast(mContext, msg);
                        }
                    });


                    break;
                case R.id.btn_noEdit:
                   onBackPressed();
                    break;
            }
        }
    };

    private void initView() {

        mRootLayout = (LinearLayout) findViewById(R.id.rootLayout_changeCheckIn);
        ediT_changeCheckIn_stuNum = (EditText) findViewById(R.id.ediT_changeCheckIn_stuNum);
        radioG_courses = (RadioGroup) findViewById(R.id.radio_courses);
        radioBtn_class = (RadioButton) findViewById(R.id.radioBtn_class);
        checkB_defaultCheckInType = (CheckBox) findViewById(R.id.checkB_defaultCheckInType);
        checkB_comLateCheckInType = (CheckBox) findViewById(R.id.checkB_comLateCheckInType);
        checkB_leaveEarlyCheckInType = (CheckBox) findViewById(R.id.checkB_leaveEarlyCheckInType);
        checkB_exceedCheckInType = (CheckBox) findViewById(R.id.checkB_exceedCheckInType);
        mBtn_sureEdit = (Button) findViewById(R.id.btn_sureEdit);
        mBtn_noEdit = (Button) findViewById(R.id.btn_noEdit);
    }
}
