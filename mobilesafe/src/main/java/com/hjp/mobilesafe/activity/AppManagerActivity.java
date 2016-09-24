package com.hjp.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.adapter.AppListAdapter;
import com.hjp.mobilesafe.utils.AppControl;
import com.hjp.mobilesafe.utils.AppInfoProvider;
import com.hjp.mobilesafe.utils.RomAvailSpaceUtil;
import com.hjp.mobilesafe.utils.PopupUtil;
import com.hjp.mobilesafe.utils.ScreenUtils;
import com.hjp.mobilesafe.utils.SendInModuleUtil;
import com.stericson.RootTools.RootTools;

import java.util.List;

/**
 * Created by HJP on 2016/9/13 0013.
 */

public class AppManagerActivity extends Activity implements View.OnClickListener {

    private Context mContext;
    private TextView textV_localAvailSpace;
    private TextView textV_sdAvailSpace;
    private TextView textV_appList_topTitle;
    private RecyclerView recyV_appList;
    private AppListAdapter mAppListAdapter;
    private ProgressDialog pd;
    public int myPosition;
    public static String TAG = "AppManagerActivity";
    private float screenDensity = -1;
    private List<AppInfoProvider.AppInfo> appData;
    private PackageManager mPackageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        initView();
    }

    private void initView() {
        mContext = getApplicationContext();
        textV_localAvailSpace = (TextView) findViewById(R.id.textV_localAvailSpace);
        textV_sdAvailSpace = (TextView) findViewById(R.id.textV_sdAvailSpace);

        boolean rootAvailable = RootTools.isRootAvailable();
        Log.i(TAG, "initView: "+rootAvailable);

        textV_appList_topTitle = (TextView) findViewById(R.id.textV_appList_topTitle);
        recyV_appList = (RecyclerView) findViewById(R.id.recyV_appList);
        final LinearLayoutManager llManager_appList = new LinearLayoutManager(mContext);
        recyV_appList.setLayoutManager(llManager_appList);
        recyV_appList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int firstVisibleItemPosition = llManager_appList.findFirstVisibleItemPosition();
                String currTitle = textV_appList_topTitle.getText().toString();
                if (firstVisibleItemPosition > myPosition) {
                    if (!currTitle.equals("系统应用"))
                        textV_appList_topTitle.setText("系统应用");
                } else if (firstVisibleItemPosition < myPosition) {
                    if (!currTitle.equals("用户应用"))
                        textV_appList_topTitle.setText("用户应用");
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        String localSpace = RomAvailSpaceUtil.getAvailSpace(mContext, Environment.getDataDirectory().getAbsolutePath());
        String sdSpace = RomAvailSpaceUtil.getAvailSpace(mContext, Environment.getExternalStorageDirectory().getAbsolutePath());
        textV_localAvailSpace.append(localSpace);
        textV_sdAvailSpace.append(sdSpace);

        pd = new ProgressDialog(AppManagerActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("扫描所有app");
        pd.show();
        AppInfoProvider.getAllAppInfos(mContext, new AppInfoProvider.DataListener() {
            @Override
            public void onData(int myPosition, List<AppInfoProvider.AppInfo> data) {
                AppManagerActivity.this.myPosition = myPosition;
                appData = data;
                mAppListAdapter = new AppListAdapter(mContext, myPosition, data);
                SendInModuleUtil.sendMsgByHandler(MSGSIGN_SETDATA, null, handler);
            }

            @Override
            public void onTrafficData(List<AppInfoProvider.AppInfo> data) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    private static final int MSGSIGN_SETDATA = 2;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case MSGSIGN_SETDATA:
                    //设置item的长按监听
                    mAppListAdapter.setLongPressListener(longPressListener);
                    recyV_appList.setAdapter(mAppListAdapter);
                    pd.dismiss();
                    break;
            }
        }
    };

    private int appSelectPosition;
    private AppListAdapter.onLongPressListener longPressListener = new AppListAdapter.onLongPressListener() {
        @Override
        public void onLongPress(int position, final float x, final float y) {
            appSelectPosition = position;
            final View pop_appList_function = View.inflate(mContext, R.layout.pop_applist_function, null);
            //得到详细信息\启动\分享\卸载的控制view
            TextView textV_appInfo = (TextView) pop_appList_function.findViewById(R.id.textV_appInfo);
            TextView textV_appStart = (TextView) pop_appList_function.findViewById(R.id.textV_appStart);
            TextView textV_appShare = (TextView) pop_appList_function.findViewById(R.id.textV_appShare);
            TextView textV_appDel = (TextView) pop_appList_function.findViewById(R.id.textV_appDel);

            textV_appInfo.setOnClickListener(AppManagerActivity.this);
            textV_appStart.setOnClickListener(AppManagerActivity.this);
            textV_appShare.setOnClickListener(AppManagerActivity.this);
            textV_appDel.setOnClickListener(AppManagerActivity.this);

            if (ScreenUtils.getScreenDensity() == -1) {
                ScreenUtils.initScreen(AppManagerActivity.this);
                screenDensity = ScreenUtils.getScreenDensity();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "run: " + x + "," + y);
                    int screenH = ScreenUtils.getScreenH();
                    if (screenH == -1) {
                        ScreenUtils.initScreen(AppManagerActivity.this);
                        screenH = ScreenUtils.getScreenH();
                    }

                    //这两种方式都可以,千万不要在模拟器运行,会显示不正常
                /*    PopupUtil.showPopup(mContext, recyV_appList, pop_appList_function
                            , (int) (screenDensity * 200), (int) (screenDensity * 50), (int) (x), -(int) (screenH-y));*/
                    PopupUtil.showPopup(mContext, recyV_appList, pop_appList_function
                            , (int) (screenDensity * 200), (int) (screenDensity * 50),
                            (int) (x - 10 * screenDensity), (int) (y - 60 * screenDensity));
                }
            });

//            Looper.prepare();//但是加上这个,一直执行这个
            runOnUiThread(new Runnable() {//直接在handler显示pop,会出现 java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
                @Override
                public void run() {
                }
            });
        }
    };

    private static final int REQUESTCODE_APPDEL = 0;
    private static final int RESPONSECODE_APPDEL = 1;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.textV_appInfo:

               /* <action android:name="android.settings.APPLICATION_DETAILS_SETTINGS" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />*/
                AppInfoProvider.AppInfo appInfo = appData.get(appSelectPosition);//获取应用的包名
                hideJump2Activity(false, -1, "android.settings.APPLICATION_DETAILS_SETTINGS", new String[]{"android.intent.category.DEFAULT"}
                        , null, "package:" + appInfo.getPkgName(), null, null);
                break;
            case R.id.textV_appStart://启动app
                AppInfoProvider.AppInfo startApp = appData.get(appSelectPosition);//获取应用的包名
                String pkgName = startApp.getPkgName();
                AppControl.startApp(mContext, pkgName);

                break;
            case R.id.textV_appShare:

         /*       <action android:name="android.intent.action.SEND" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:mimeType="text/plain" />
*/

                AppInfoProvider.AppInfo appShare = appData.get(appSelectPosition);
                hideJump2Activity(false, -1, "android.intent.action.SEND", new String[]{"android.intent.category.DEFAULT"}
                        ,  "text/plain",null, SmsManager.EXTRA_MMS_DATA, "下载吧:" + appShare.getAppName() + ",地址：" + appShare.getPkgName());
                break;
            case R.id.textV_appDel://跳转到卸载app页面进行手动卸载,如何实现静默卸载

               /* <action android:name="android.intent.action.VIEW" />
                <action android:name="android.intent.action.DELETE" />
                <category android:name="android.intent.category.DEFAULT" />
                <data android:scheme="package" />
*/
                hideJump2Activity(true, REQUESTCODE_APPDEL, "android.intent.action.DELETE"
                        , new String[]{"android.intent.category.DEFAULT"}, null, "package:" + getPackageName(), null, null);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_APPDEL) {
            if (resultCode == Activity.RESULT_OK) {//卸载了app
                appData.remove(appSelectPosition);
                mAppListAdapter.notifyItemMoved(appSelectPosition, appSelectPosition);
            }
        }
    }

    public void hideJump2Activity(boolean needReturn, int requestCode, String action, String[] category
            , String mimeType, String data, String extraKey, Object extraData) {
        Intent intent = new Intent();
        intent.setAction(action);
        for (String c : category) {
            intent.addCategory(c);
        }
        if (!TextUtils.isEmpty(mimeType)) {
            intent.setType(mimeType);
        }
        if (!TextUtils.isEmpty(data)) {
            intent.setData(Uri.parse(data));
        }
        if (!TextUtils.isEmpty(extraKey)) {
            if (extraData instanceof String) {
                intent.putExtra(extraKey, (String) extraData);
            }
        }

        try{
            if (!needReturn) {
                startActivity(intent);
            } else {
                startActivityForResult(intent, requestCode);
            }
        }catch (ActivityNotFoundException e){
            SendInModuleUtil.showToast(mContext,"无法启动应用");
        }

    }
}
