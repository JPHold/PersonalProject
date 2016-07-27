package com.hjp.projectone.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.hjp.projectone.R;
import com.hjp.projectone.adapter.BusinessAdapter;
import com.hjp.projectone.custom.DividerItemDecoration;

/**
 * Created by HJP on 2016/7/12 0012.
 */

public class BusinessActivity extends AppCompatActivity {

    public static final String BANKNAME = "bankName";
    private Toolbar mToolbar;
    private RecyclerView mBusinessList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business);
        initView();
    }

    private void initView() {
        mContext = getApplicationContext();

        mToolbar = (Toolbar) findViewById(R.id.business_toolbar);
        mBusinessList = (RecyclerView) findViewById(R.id.business_list);
        mBusinessList.setLayoutManager(new LinearLayoutManager(mContext));
        mBusinessList.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));

        Intent intent = getIntent();
        if (intent != null) {
            String bankName = intent.getStringExtra(BANKNAME);
            if (bankName != null && bankName.length() > 0) {
                mToolbar.setTitle(bankName);
                Resources resources = getResources();
                String[] mTitle = resources.getStringArray(R.array.businessPieceTitle);
                String[] mMsg = resources.getStringArray(R.array.businessMsg);
                String s = mMsg[mMsg.length - 1];
                int i = s.indexOf("[");
                StringBuilder sb = new StringBuilder(s);
                sb.insert(i+1, bankName);
                mMsg[mMsg.length-1]=sb.toString();
                String[] data = mMsg;

                BusinessAdapter businessAdapter = new BusinessAdapter(mContext, bankName, mTitle, mMsg);
                mBusinessList.setAdapter(businessAdapter);
            }
        }
        setSupportActionBar(mToolbar);
    }
}
