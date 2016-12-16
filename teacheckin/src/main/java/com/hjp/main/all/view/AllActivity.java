package com.hjp.main.all.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.hjp.ProvinceBean;
import com.hjp.R;
import com.hjp.main.all.main.view.fragment.MainFragment;
import com.hjp.main.all.presenter.AllPresenter;
import com.hjp.main.all.service.bean.ClassRoom;
import com.hjp.main.all.service.bean.Course;
import com.hjp.main.all.service.bean.CoursePart;
import com.hjp.main.gesture.view.custom.gesturelock.GestureLockViewGroup;
import com.hjp.others.app.MainApplication;
import com.hjp.others.app.db.AppConfig;
import com.hjp.others.util.CheckUtil;
import com.hjp.others.util.CutoverUtil;
import com.hjp.others.util.PopupUtil;
import com.hjp.others.util.ScreenUtils;
import com.hjp.others.util.SendInModuleUtil;
import com.hjp.others.util.UserVerificationUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class AllActivity extends AppCompatActivity implements AllView, RelativeView {

    private Context mContext;

    /**
     * 手势锁
     */
    private GestureLockViewGroup mLockViewGroup;
    private PopupWindow mPopWindowGesture;

    private AllPresenter mAllPresenter;
    /**
     * 老师工号
     */
    private String num;
    /**
     * 顶部栏
     */
    private Toolbar mToolbar;
    /**
     * 课程分栏
     */
    private TabLayout mTabLayoutCourses;
    /**
     * 班级栏
     */
    private ViewPager mViewPagerClasses;
    /**
     * 底部功能切换栏
     */
    private RadioGroup mRadioGroupTab;

    private ProgressBar mProgressBar;
    private OptionsPickerView mMakeCheckInInfoOptionView;
    private String mCurrSelectCourseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();

        //是否开了手势锁
        mAllPresenter = new AllPresenter(this, mContext);
        mAllPresenter.dissectionKeyPwd();

        boolean isNight = MainApplication.mAppConfig.getNightModeSwitch();
        // TODO: 2016/12/4 夜间模式先放着 
     /*   if (isNight) {
            this.setTheme(R.style.nightTheme);
        } else {
            this.setTheme(R.style.DefaultTheme);
        }*/
        setContentView(R.layout.all);

        initView();
        getCurrLoginTeacherUser();
        addListener();
    }


    private void getCurrLoginTeacherUser() {
        Intent jumpDataIntent = getIntent();
        num = UserVerificationUtil.verifyCurrUserFromIntent(mContext, jumpDataIntent, this);
    }

    /**
     * AllView的接口的头
     */
    @Override
    public boolean getGestureState() {
        AppConfig mAppConfig = MainApplication.mAppConfig;
        CheckUtil.checkNull(mContext, mAppConfig);
        //获取手势锁验证状态
        boolean isVerify = mAppConfig.getGestureVerifyState();
        //手势锁开启or关闭
        return mAppConfig.getGestureClockOpen();
    }

    @Override
    public void setMakeCheckInCourses(final ArrayList<Course> courseNames, final Map<String, Map<String, Object>> makeCheckInInfoMap) {

        OptionsPickerView courseOptionView = new OptionsPickerView(this);
        courseOptionView.setTitle("选择需要发布签到的课程");
//        courseOptionView.setSelectOptions(1);
        courseOptionView.setPicker(courseNames);
        courseOptionView.setCyclic(false);
        courseOptionView.show();
        courseOptionView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int courseIndex, int option2, int options3) {
                String courseId = courseNames.get(courseIndex).getId();
                mAllPresenter.makeCheckInInfos(courseId, makeCheckInInfoMap);
            }
        });

    }

    @Override
    public void setMakeCheckInInfos
            (final String selectCourseId, final ArrayList<ClassRoom> classRooms, final ArrayList<ArrayList<String>> weekNums
                    , final ArrayList<ArrayList<ArrayList<CoursePart>>> courseParts) {

        mMakeCheckInInfoOptionView = new OptionsPickerView(this);
        mMakeCheckInInfoOptionView.setPicker(classRooms, weekNums, courseParts, true);
        mMakeCheckInInfoOptionView.setCyclic(false, true, false);
        mMakeCheckInInfoOptionView.setSelectOptions(0, 0, 0);
        mMakeCheckInInfoOptionView.show();
        mMakeCheckInInfoOptionView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mCurrSelectCourseId = selectCourseId;
                Integer classRoomId = classRooms.get(options1).getId();
                String weekNum = weekNums.get(options1).get(option2);
                Integer coursePartId = courseParts.get(options1).get(option2).get(options3).getId();
                //...开始发布签到要求
                runProgressBar();
                //提交发布的签到
                mAllPresenter.makeCheckIn(mCurrSelectCourseId, classRoomId, weekNum, coursePartId);
            }
        });
    }

    @Override
    public String getKeyGesturePassword() {
        return MainApplication.mAppConfig.getKeyGesturePassword();
    }

    @Override
    public void errorDecode(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void setKeyPwd2GestureView(String trueKeyPwd) {
        int[] answer = CutoverUtil.stringToInts(trueKeyPwd, ",");

        //contentView
        mLockViewGroup = (GestureLockViewGroup) getLayoutInflater().inflate(R.layout.include_lockviewgroup, null);

        //不是第一次设置密码
        mLockViewGroup.setFirstSetting(false);
        //传入准确密码,进行密码匹对
        mLockViewGroup.setAnswer(answer);
        mLockViewGroup.setClickable(true);
        //监听划密码
        mLockViewGroup.setOnGestureLockViewListener(lockListener);

        mainHandler.sendEmptyMessage(0);
    }

    /**
     * 头
     * 模块：获取课程列表
     */
    @Override
    public void startObtainCourses() {

    }

    @Override
    public String getTeaNum() {
        return num;
    }

    @Override
    public String getDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(new Date());
    }

    @Override
    public void errorObtainCourse(int status, String msg) {

    }
    /**
     * 尾
     * 模块：获取课程列表
     */

//---------------------

    /**
     * 头
     * 模块：发布签到请求
     */
    @Override
    public void successMakeCheckIn() {
        mMakeCheckInInfoOptionView.dismiss();
        stopProgressBar();
    }

    @Override
    public void errorMakeCheckIn(int status, String msg) {
        Toast.makeText(mContext, msg + status, Toast.LENGTH_LONG).show();
        stopProgressBar();
    }
    /**
     * 尾
     * 模块：发布签到请求
     */

    //AllView接口的尾

    /**
     * RelativeView接口的头
     */

    @Override
    public Context getContext() {
        return MainApplication.getContext();
    }

    @Override
    public void runProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public FragmentManager obtainFragmentManager() {
        return getSupportFragmentManager();
    }

    @Override
    public void stopProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }
    //RelativeView接口的尾

    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            Object data = msg.obj;
            switch (what) {
                case 0:

                    ScreenUtils.initScreen(AllActivity.this);
                    int screenW = ScreenUtils.SCREENW;
                    int screenH = ScreenUtils.SCREENH;

                    //弹出popup
                    mPopWindowGesture = PopupUtil.showPopup(mContext, getWindow().getDecorView(), mLockViewGroup,
                            screenW, screenH, 0, -screenH);
                    break;
                case 1:
                    //撤掉发布窗口
//                    ppWindow_postCheckIn.dismiss();
                    break;
                case 2:
                 /*   resources = mContext.getResources();
                    boolean isNight = (boolean) msg.obj;
                    delegate(isNight);*/
                    break;
                case 3:
                    //撤掉手势锁
                    mPopWindowGesture.dismiss();
                   /* getCurrTeacherCoursers();*/

                    MainFragment mainFragment = new MainFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.all_frameLayout_fragment, mainFragment).commit();
                    break;
            }
        }
    };

    private GestureLockViewGroup.OnGestureLockViewListener lockListener =
            new GestureLockViewGroup.OnGestureLockViewListener() {

                @Override
                public void onUnmatchedExceedBoundary() {
                    //输出三次,暂时不处理
                }

                @Override
                public void onGestureMsg(String msg) {
                    //信息显示
                    if ("密码正确".equals(msg)) {
                        SendInModuleUtil.sendMsgByHandler(3, null, mainHandler);
                    }
                }

                @Override
                public void onPassWord(int currSetCount, List<Integer> passWord) {

                }

            };

    private void initView() {
        mTabLayoutCourses = (TabLayout) findViewById(R.id.main_tabLayout_courses);
        mViewPagerClasses = (ViewPager) findViewById(R.id.main_viewPager_classes);
        mRadioGroupTab = (RadioGroup) findViewById(R.id.all_radioGroup_tag);
        ((RadioButton) mRadioGroupTab.getChildAt(0)).setChecked(true);
        mProgressBar = (ProgressBar) findViewById(R.id.all_progressBar);
    }

    private void addListener() {
        mRadioGroupTab.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case 1://首页

                        break;
                    case 2://信息

                        break;
                    case 3://签到发布按钮
                        mAllPresenter.getMakeCheckInInfos();
                        break;
                    case 4://发现

                        break;
                    case 5://我

                        break;
                }
            }
        });
    }


   /*

   *//* private void initView() {
        mContext = getApplicationContext();
        defaultPerferences = getPreferences(MODE_PRIVATE);

        mMainRootLayout = (CoordinatorLayout) findViewById(R.id.mainRootLayout);
        mAppbarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
        mProfileImage = (CircleImageView) findViewById(R.id.main_profile_image);
        mainTabLayout = (TabLayout) findViewById(R.id.main_tabLayout_course);
        mFaBtn_sendCheckIn = (FloatingActionButton) findViewById(R.id.main_fab_sendCheckIn);
        mainViewPager = (ViewPager) findViewById(R.id.main_viewPager_courseCheckIn);
        mToolBar = (Toolbar) findViewById(R.id.teacherMain_toolbar);
        drawerL_function = (DrawerLayout) findViewById(R.id.teacherMain_drawerL2);

        extraFunction_message = (TextView) findViewById(R.id.extraFunction_message);
        extraFunction_changeCheckIn = (TextView) findViewById(R.id.extraFunction_changeCheckIn);
        extraFunction_setting = (TextView) findViewById(R.id.extraFunction_setting);
        extraFunction_nightDelegate = (TextView) findViewById(R.id.extraFunction_nightDelegate);
        extraFunction_topImage = (ImageView) findViewById(R.id.extraFunction_topImage);
        extraFunction_rootLayout = (LinearLayout) findViewById(R.id.extraFunction_rootLayout);

        mToolBar.setTitle(teacherNum);
        setSupportActionBar(mToolBar);
        mDrawerToggle = new ActionBarDrawerToggle(
                this
                , drawerL_function
                , mToolBar
                , R.string.open_drawerLayout
                , R.string.close_drawerLayout) {
            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
//                mainViewPager.setCanScroll(true);
//                mainTabLayout.setClickable(true);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
//                mainViewPager.setCanScroll(false);
//                mainTabLayout.setClickable(false);
            }
        };
        mDrawerToggle.syncState();
    }
*//*
    *//*private void initResource() {
        mMaxScrollSize = mAppbarLayout.getTotalScrollRange();
    }*//*

*//*

    public void getCurrTeacherCoursers() {

        final Message msg = Message.obtain(handler);
        StringCallback stringCallback = new StringCallback() {
            @Override
            public String parseNetworkResponse(Response response, int id) throws IOException {
                Log.i(TAG, "parseNetworkResponse: " + "parseNetworkResponse: " + response.message() + "," + id);
                return super.parseNetworkResponse(response, id);
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                Log.i(TAG, "onError: " + "onError: " + call.request().url().host() + "," + id + "," + e.getMessage());
            }

            @Override
            public void onResponse(String result, int id) {
                SendInModuleUtil.showToast(mContext, result + "," + id);
                //获取当前老师教授的课程的结果
                String json_courseListByTeacher = result;
                Log.i(TAG, "json_courseListByTeacher: " + json_courseListByTeacher);

                if (TextUtils.isEmpty(json_courseListByTeacher)) {
                    //SeekBar提示无课程
                    msg.what = 0;
                    msg.obj = "当前无教授的课程";
                } else {

                    try {
                        JSONObject jsonObject = JSONObject.fromObject(json_courseListByTeacher);

                        if (jsonObject.containsKey("courselistbyteacher")) {
                            JSONArray jsonArray = jsonObject
                                    .getJSONArray("courselistbyteacher");
                            if (jsonArray != null) {
                                if (list_currTeacherCourses == null) {
                                    list_currTeacherCourses = new ArrayList<>();
                                }

                                for (int i = 0; i < jsonArray.size(); i++) {
                                    String courseName = jsonArray.getString(i);

                                    list_currTeacherCourses.add(courseName);
                                }

                                msg.what = 1;
                                msg.obj = list_currTeacherCourses;

                            }
                        }
                    } catch (JSONException e) {
                        //不是json数据，而是提示错误的信息
                        String errorRequestText = result;
                        msg.what = 0;
                        msg.obj = errorRequestText;
                    }

                }

                msg.sendToTarget();
            }

        };

        //获取当前老师教授的课程
        OkHttpUtils
                .post()
                .url(Url.URL_FINDCOURSELISTFROMTEACHER)
                .addParams(Url.PARAM_TEACHERNUM, teacherNum)
                .build()
                .execute(stringCallback);


      *//*  mainBmobQuery = new MainBmobQuery(mContext);
        try {
            mainBmobQuery.querySpecialTeacherCourses(teacherNum, new MainBmobQuery.SpecialTeacherCoursesQueryListener() {
                @Override
                public void Success(List<String> list) {
                    if (list != null && list.size() > 0)
                        list_currTeacherCourses = list;
                    SendInModuleUtil.sendMsgByHandler(1, list_currTeacherCourses, handler);
                }

                @Override
                public void Error(String msg) {
                    Log.i("checkin", msg);
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }*//*
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    String error_requestCourseByTeacher = (String) msg.obj;
                    Log.i(TAG, "error_requestCourseByTeacher: " + error_requestCourseByTeacher);
                    break;
                case 1:*//*{ when=-21s408ms what=1 obj=[数据库, SSH, ANDROID, 软件工程] target=com.hjp.Activity.teacher.AllActivity$4 }*//*
                    *//*{ when=-290ms what=1 obj=[数据库管理与应用, Android高级开发, Html5游戏开发] target=com.hjp.Activity.teacher.AllActivity$4 }*//*
                    List<String> listArray_saveAllCourseNameInSpecialTeacher = (List<String>) msg.obj;

                    if (listArray_saveAllCourseNameInSpecialTeacher == null) {
                        listArray_saveAllCourseNameInSpecialTeacher = new ArrayList<>();
                    }

                    CoursesAdapter coursesAdapter = new CoursesAdapter(getSupportFragmentManager()
                            , listArray_saveAllCourseNameInSpecialTeacher, mContext);

                    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
                    IntentFilter intentFilter = new IntentFilter();
                    intentFilter.addAction("com.hjp.jumpActivity");
                    localBroadcastManager.registerReceiver(new JumpActivityBroadCastReceiver(), intentFilter);

                    mainViewPager.setAdapter(coursesAdapter);
                    //在此之前没有为viewpager设置adapter会报错:java.lang.IllegalArgumentException: ViewPager does not have a PagerAdapter set
                    mainTabLayout.setupWithViewPager(mainViewPager);
                    break;
            }
        }
    };

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String currSlectCourse = (String) parent.getItemAtPosition(position);
            MainBmobQuery query = new MainBmobQuery(mContext);
            query.querySpecialCourseClasses(teacherNum, currSlectCourse, new MainBmobQuery.SpecialCourseClassesQueryListener() {
                @Override
                public void Success(List<String> list_classes) {
                    ArrayAdapter<CharSequence> adapter_classesSpinner_sendCheckIn = new ArrayAdapter(mContext,
                            android.R.layout.simple_spinner_item, list_classes);
                    adapter_classesSpinner_sendCheckIn.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    classesSpinner.setAdapter(adapter_classesSpinner_sendCheckIn);
                    Log.i(TAG, "Success: spinner查询班级");
                }

                @Override
                public void Error(String msg) {
                    Log.i(TAG, "Error: spinner查询班级" + msg);
                }
            });
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private PopupWindow ppWindow_postCheckIn;
    private boolean isAutoSelectCourse = true;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int clickId = v.getId();
            switch (clickId) {
                case R.id.main_fab_sendCheckIn:
                   *//* AlertDialog dialog=new AlertDialog.Builder(mContext)
                            .setTitle("ssss")
                            .show();*//*
                    Log.i(TAG, "onClick: 提交签到");
                    View layout_popup_postCheckIn = getLayoutInflater().inflate(R.layout.popupwindow_postcheckin, null);
                    final LinearLayout lnL_mainContentView = (LinearLayout) layout_popup_postCheckIn.findViewById(R.id.pop_mainContentView);
                    final ProgressBar proBar_postCheckIn = (ProgressBar) layout_popup_postCheckIn.findViewById(R.id.proBar_postCheckIn);
                    proBar_postCheckIn.setFocusable(true);
                    coursesSpinner = (Spinner) layout_popup_postCheckIn.findViewById(R.id.popup_spinner_courses);
                    classesSpinner = (Spinner) layout_popup_postCheckIn.findViewById(R.id.popup_spinner_classes);
                    btn_postCheckIn = (Button) layout_popup_postCheckIn.findViewById(R.id.popup_btn_postCheckIn);
                    btn_postCheckIn.setOnClickListener(clickListener);

                    ArrayAdapter<CharSequence> adapter_courseSpinner_sendCheckIn = new ArrayAdapter(mContext,
                            android.R.layout.simple_spinner_item, list_currTeacherCourses);
                    adapter_courseSpinner_sendCheckIn.setDropDownViewResource(R.layout.cell_postcheckin_coursespinner_dropview);
                    coursesSpinner.setAdapter(adapter_courseSpinner_sendCheckIn);

                    final MainBmobQuery mainBmobQuery = new MainBmobQuery(mContext);
                    coursesSpinner.setTag(classesSpinner);
                    coursesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Log.i(TAG, "onItemSelectebd: " + position);
                            if (isAutoSelectCourse) {
                                view.setVisibility(View.INVISIBLE);
                                view.setSelected(true);
                                isAutoSelectCourse = false;
                                return;
                            }

                            if (position == 0) {
                                view.setVisibility(View.VISIBLE);
                            }

                            final Spinner classesSpinner = (Spinner) parent.getTag();
                            TextView textV_selectCourse = (TextView) view;
                            String text_selectCourse = textV_selectCourse.getText().toString();

                            mainBmobQuery.querySpecialCourseClasses(teacherNum, text_selectCourse
                                    , new MainBmobQuery.SpecialCourseClassesQueryListener() {
                                        @Override
                                        public void Success(List<String> list_classes) {
                                            ArrayAdapter<CharSequence> adapter_classesSpinner_sendCheckIn =
                                                    new ArrayAdapter(mContext,
                                                            android.R.layout.simple_spinner_item, list_classes);
                                            adapter_classesSpinner_sendCheckIn.setDropDownViewResource(
                                                    R.layout.cell_postcheckin_coursespinner_dropview);
                                            classesSpinner.setAdapter(adapter_classesSpinner_sendCheckIn);
                                            proBar_postCheckIn.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void Error(String msg) {

                                        }
                                    });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    btn_postCheckIn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView selectCourseView = (TextView) coursesSpinner.getSelectedView();
                            TextView selectClassView = (TextView) classesSpinner.getSelectedView();

                            if (selectCourseView != null && selectClassView != null) {
                                Log.i(TAG, "onClick: ");
                                proBar_postCheckIn.setVisibility(View.VISIBLE);
                                String selectCourse = selectCourseView.getText().toString();
                                String selectClass = selectClassView.getText().toString();
                                if ("".equals(selectCourse) && "".equals(selectClass)) {
                                    selectCourse = "";
                                    selectClass = "";
                                }
                                proBar_postCheckIn.setVisibility(View.VISIBLE);
                                mainBmobQuery.insertPostCheckIn(teacherNum, selectCourse, selectClass,
                                        new MainBmobQuery.InsertPostCheckInListener() {
                                            @Override
                                            public void msg(String msg) {
                                                if ("发布签到成功".equals(msg)) {
                                                    proBar_postCheckIn.setVisibility(View.INVISIBLE);
                                                    ppWindow_postCheckIn.dismiss();
                                                }
                                            }
                                        });
                            }
                            Log.i(TAG, "onClick: ");
                        }
                    });

                    ppWindow_postCheckIn = new PopupWindow(layout_popup_postCheckIn,
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ppWindow_postCheckIn.setFocusable(true);
                    ppWindow_postCheckIn.setOutsideTouchable(true);
                    ppWindow_postCheckIn.setBackgroundDrawable(new BitmapDrawable());
                    final int[] loc_mainViewPager = new int[2];
                    mainViewPager.getLocationOnScreen(loc_mainViewPager);

                    WindowManager windowManager = getWindowManager();
                    Display defaultDisplay = windowManager.getDefaultDisplay();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    defaultDisplay.getMetrics(displayMetrics);

                    int screenW = displayMetrics.widthPixels;
                    int screenH = displayMetrics.heightPixels;

                    int leftBottomX_popOfDependence = loc_mainViewPager[0];
                    int leftBottomY_popOfDependence = loc_mainViewPager[1];

                    int w_popOfDependence = mainViewPager.getWidth();
                    int h_popOfDependence = mainViewPager.getHeight();

                    int w_pop = (int) getResources().getDimensionPixelOffset(R.dimen.x212);
                    int h_pop = (int) getResources().getDimensionPixelOffset(R.dimen.y60);
                    Log.i("pp", "pp值: " + w_pop + "|" + h_pop);

                    int pointX = -w_pop;
                    int pointY = -4 * h_pop;
                    Log.i("pp", "pp左上值: " + pointX + "|" + pointY);

                    View decorView = findViewById(android.R.id.content);

                    ppWindow_postCheckIn.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            Log.i(TAG, "onDismiss: ");
                            isAutoSelectCourse = true;
                        }
                    });
                    ppWindow_postCheckIn.showAsDropDown(mFaBtn_sendCheckIn, pointX,
                            pointY);

                    break;

                case R.id.popup_btn_postCheckIn:
                    String selectCourse = (String) coursesSpinner.getSelectedItem();
                    String selectClass = (String) classesSpinner.getSelectedItem();
                    if ((selectCourse != null) && (selectClass != null)) {
                        MainBmobQuery bmobQuery = new MainBmobQuery(mContext);
                        bmobQuery.insertPostCheckIn(teacherNum, selectCourse, selectClass,
                                new MainBmobQuery.InsertPostCheckInListener() {
                                    @Override
                                    public void msg(String msg) {
                                        if ("发布签到成功".equals(msg)) {
                                            ppWindow_postCheckIn.dismiss();
                                        } else {
                                            //其他错误信息通过SeekBar打印
                                        }
                                    }
                                });
                    }
                    break;

            }
        }
    };



    private void delegate(boolean isNight) {
        ViewUtil viewUtil = new ViewUtil(mContext);
        if (isNight) {
            ViewUtil.setBackColor(mToolBar, R.color.night_toolBarBackColor);
            ViewUtil.setBackColor(mainTabLayout, R.color.night_baseColor);
            ViewUtil.setBackColor(mainViewPager, R.color.night_viewPagerBackColor);
            ViewUtil.setBackColor(extraFunction_rootLayout, R.color.night_extraFunctionBackColor);
            ViewUtil.setBackColor(extraFunction_topImage, R.color.night_extraFunctionTopImageBackColor);
        } else {
            ViewUtil.setBackColor(mToolBar, R.color.default_toolBarBackColor);
            ViewUtil.setBackColor(mainTabLayout, R.color.default_baseColor);
            ViewUtil.setBackColor(mainViewPager, R.color.default_viewPagerBackColor);
            ViewUtil.setBackImage(extraFunction_rootLayout, R.color.default_extraFunctionBackColor);
            ViewUtil.setBackImage(extraFunction_topImage, R.mipmap.extrafunction_topimage);
        }
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int percentage = (Math.abs(verticalOffset)) * 100 / mMaxScrollSize;

        if (percentage >= PERCENTAGE_TO_ANIMATE_AVATAR && mIsAvatarShown) {
            mIsAvatarShown = false;
            mProfileImage.animate().scaleY(0).scaleX(0).setDuration(200).start();
        }

        if (percentage <= PERCENTAGE_TO_ANIMATE_AVATAR && !mIsAvatarShown) {
            mIsAvatarShown = true;

            mProfileImage.animate()
                    .scaleY(1).scaleX(1)
                    .start();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return false;
    }

    public class JumpActivityBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive:1 ");
            if (intent != null) {
                Log.i(TAG, "onReceive:2 ");
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    Log.i(TAG, "onReceive:3 ");
                    Intent jumpActivityIntent = new Intent(AllActivity.this, ClassCheckInInfoActivity.class);
                    jumpActivityIntent.putExtra("classCheckInInfo", bundle);
                    startActivity(jumpActivityIntent);
                }
            }
        }

    }

    private View.OnClickListener extraFunctionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.extraFunction_message:

                    break;
                case R.id.extraFunction_changeCheckIn:
                    Intent intentChange = new Intent(mContext, ChangeCheckInActivity.class);
                    startActivity(intentChange);
                    break;
                case R.id.extraFunction_setting:
                    Intent intentSetting = new Intent(mContext, SettingActivity.class);
                    intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intentSetting);
                    break;

                case R.id.extraFunction_nightDelegate:
                    boolean isNight = !MainApplication.mAppConfig.getNightModeSwitch();
                    int resId = isNight == true ? R.style.nightTheme : R.style.DefaultTheme;
                    MainApplication.mAppConfig.setNightModeSwitch(isNight);
                    //整体app换为夜间
                    MainApplication app = (MainApplication) getApplication();
                    app.setTheme(resId);

                    //即时切换模式(白天和黑夜),改变ToolBar和内容区域的颜色、图片和文字
                    SendInModuleUtil.sendMsgByHandler(2, isNight, mainHandler);
                    break;
            }
        }
    };
*/
}
