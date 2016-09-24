package com.hjp.mobilesafe.database.dao;

import android.content.Context;
import android.text.TextUtils;

import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.database.BlackListBaseDao;

/**
 * Created by HJP on 2016/9/8 0008.
 */

public class BlackListDeleteDao extends BlackListBaseDao {


    public BlackListDeleteDao(Context context) {
        super(context);
    }

    public int deleteBlackListPart(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return 0;
        }

        int delete = sd.delete(Constant.DATATABLE_BALCKLIST_NAME, Constant.KEY_BLACKLIST_PHONE + " =?", new String[]{phoneNumber});
        close();
        return delete;
    }

}
