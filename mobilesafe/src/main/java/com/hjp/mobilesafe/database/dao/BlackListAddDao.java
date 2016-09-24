package com.hjp.mobilesafe.database.dao;

import android.content.ContentValues;
import android.content.Context;

import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.database.BlackListBaseDao;
import com.hjp.mobilesafe.database.BlackListPhoneNumberInfo;

/**
 * Created by HJP on 2016/9/8 0008.
 */

public class BlackListAddDao extends BlackListBaseDao {

    public BlackListAddDao(Context context) {
        super(context);
    }

    public long addBlackListPart(BlackListPhoneNumberInfo d) {
        ContentValues cv = new ContentValues();
        cv.put(Constant.KEY_BLACKLIST_PHONE, d.getPhoneNumber());
        cv.put(Constant.KEY_BLACKLIST_HOLDMODE, d.getHoldMode());

        long insert = sd.insert(Constant.DATATABLE_BALCKLIST_NAME, null, cv);

        return insert;
    }
}
