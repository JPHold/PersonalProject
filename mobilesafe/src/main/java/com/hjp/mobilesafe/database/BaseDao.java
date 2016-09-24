package com.hjp.mobilesafe.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hjp.mobilesafe.constant.Constant;

/**
 * Created by HJP on 2016/9/8 0008.
 */

public class BaseDao {

    private Context mContetx;

    private String create_table;


    public BlackListDataBase mBlackListDataBase;
    public SQLiteDatabase sd;

    public BaseDao(Context context, String createTable) {
        mContetx = context;
        create_table = createTable;
        getBlackListDataBase();
    }

    private void getBlackListDataBase() {
        if (mBlackListDataBase == null) {
            mBlackListDataBase = new BlackListDataBase(mContetx, Constant.DATABASE_BALCKLIST_NAME, null, 1);
        }
        mBlackListDataBase.setProcessTable(create_table);

        if (sd == null) {
            sd = mBlackListDataBase.getDatabase();
        }

    }

    public void close() {
        if (sd != null) {
            sd.close();
        }
        if (mBlackListDataBase != null) {
            mBlackListDataBase.close();
        }
    }
}
