package com.hjp.mobilesafe.database;

import android.content.Context;

/**
 * Created by HJP on 2016/9/8 0008.
 */

public class BlackListBaseDao extends BaseDao {

    private static final String
            CREATE_TABLE = "create table if not exists phoneblacklist (_id INTEGER PRIMARY KEY AUTOINCREMENT,phonenumber VARCHAR(11),holdmode VARCHAR(9))";


    public BlackListBaseDao(Context context) {
        super(context, CREATE_TABLE);
    }

}
