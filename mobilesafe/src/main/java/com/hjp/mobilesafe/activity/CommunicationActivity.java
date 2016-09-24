package com.hjp.mobilesafe.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.adapter.PhoneBlackListAdapter;
import com.hjp.mobilesafe.database.BlackListPhoneNumberInfo;
import com.hjp.mobilesafe.database.dao.BlackListAddDao;
import com.hjp.mobilesafe.database.dao.BlackListQueryDao;
import com.hjp.mobilesafe.listener.DataListener;
import com.hjp.mobilesafe.utils.ScreenUtils;
import com.hjp.mobilesafe.utils.SendInModuleUtil;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CommunicationActivity extends Activity {


    private Context mContetx;
    private ListView listV_blackList;
    private TextView textV_blackList_phoneNumber;
    private TextView textV_blackList_holdMode;
    private ImageView imgV_blackList_delete;

    private PhoneBlackListAdapter phoneBlackListAdapter;
    private BlackListQueryDao blackListQueryDao;
    private List<BlackListPhoneNumberInfo> blackListPhoneNumberInfos;
    private int position_newly;

    private static final int msgSign_refreshBlackList = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
        initView();
    }

    private void initView() {
        mContetx = getApplicationContext();
        listV_blackList = (ListView) findViewById(R.id.listV_blackList);
        //加载10条黑名单数据
        //监听listV_blackList滚到底部，实现上拉刷新
        listV_blackList.setOnScrollListener(onScrollListener);
        //设置号码黑名单适配器
        blackListQueryDao = new BlackListQueryDao(mContetx);
        blackListQueryDao.queryBlackListPart(1, "1", "10", new DataListener() {
            @Override
            public void onData(Object data) {
                blackListPhoneNumberInfos = (List<BlackListPhoneNumberInfo>) data;
                position_newly = blackListPhoneNumberInfos.size();
                if (position_newly > 0) {
                    phoneBlackListAdapter = new PhoneBlackListAdapter(mContetx, blackListPhoneNumberInfos);
                    listV_blackList.setAdapter(phoneBlackListAdapter);
                }
            }
        });

    }

    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //滚动停止时，判断当前屏幕中底部项是否为最后一项，是则加载往下20条数据
            int lastVisiblePosition = view.getLastVisiblePosition();
            int size = blackListPhoneNumberInfos.size();
            if (lastVisiblePosition != size - 1) {
                return;
            }

            //查询最前一个号码的_id
            BlackListPhoneNumberInfo newlyPhoneInfo = blackListPhoneNumberInfos.get(size-1);
            int id = newlyPhoneInfo.get_id();

            blackListQueryDao.queryBlackListPart(0, String.valueOf(id), "10", new DataListener() {
                @Override
                public void onData(Object data) {
                    List<BlackListPhoneNumberInfo> newDatas = (List<BlackListPhoneNumberInfo>) data;
                    if (newDatas.size() > 0) {
                        blackListPhoneNumberInfos.addAll(newDatas);
                        SendInModuleUtil.sendMsgByHandler(msgSign_refreshBlackList, null, handler);
                    }
                }
            });


        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case msgSign_refreshBlackList:
                    phoneBlackListAdapter.notifyDataSetChanged();
                    break;

            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_communication, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.add_blackList:
                View view = View.inflate(mContetx, R.layout.phone_blacklist_add, null);
                ScreenUtils.initScreen(CommunicationActivity.this);
                float screenDensity = ScreenUtils.getScreenDensity();
                final PopupWindow ppWindow = new PopupWindow(view, (int) (300 * screenDensity), (int) (300 * screenDensity), true);
                ppWindow.setBackgroundDrawable(new BitmapDrawable());
                ppWindow.setOutsideTouchable(true);

                View btn_addBlackList = view.findViewById(R.id.btn_addBlackList);
                final EditText editT_inputPhone = (EditText) view.findViewById(R.id.editT_phone);
                final RadioGroup radioG_holdMode = (RadioGroup) view.findViewById(R.id.radioG_holdMode);


                btn_addBlackList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNumber = editT_inputPhone.getText().toString();
                        int whichHoldMode = radioG_holdMode.getCheckedRadioButtonId();
                        String holdMode = null;
                        if (whichHoldMode == R.id.radioBtn_holdMode1) {
                            holdMode = "电话";
                        }
                        if (whichHoldMode == R.id.radioBtn_holdMode2) {
                            holdMode = "短信";
                        }
                        if (whichHoldMode == R.id.radioBtn_holdMode3) {
                            holdMode = "电话+短信";
                        }
                        if (TextUtils.isEmpty(phoneNumber)) {
                            return;
                        }

                        if (TextUtils.isEmpty(holdMode)) {
                            return;
                        }

                        BlackListPhoneNumberInfo blackListPhoneNumberInfo = new BlackListPhoneNumberInfo(phoneNumber, holdMode);
                        BlackListAddDao blackListAddDao = new BlackListAddDao(mContetx);
                        long insert = blackListAddDao.addBlackListPart(blackListPhoneNumberInfo);

                        if (insert == -1) {
                            Toast.makeText(mContetx, "插入失败-请检查权限", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContetx, "插入成功", Toast.LENGTH_LONG).show();
                            BlackListPhoneNumberInfo numberInfo = new BlackListPhoneNumberInfo(phoneNumber, holdMode);
                            blackListPhoneNumberInfos.add(0, numberInfo);
                            phoneBlackListAdapter.notifyDataSetChanged();
                        }
                        ppWindow.dismiss();

                    }
                });

                ppWindow.showAsDropDown(listV_blackList, (int) (100 * screenDensity), (int) (-600 * screenDensity));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
