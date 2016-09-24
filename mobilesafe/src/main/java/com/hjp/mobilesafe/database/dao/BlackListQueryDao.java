package com.hjp.mobilesafe.database.dao;

import android.content.Context;
import android.database.Cursor;

import com.hjp.mobilesafe.constant.Constant;
import com.hjp.mobilesafe.database.BlackListBaseDao;
import com.hjp.mobilesafe.database.BlackListPhoneNumberInfo;
import com.hjp.mobilesafe.listener.DataListener;
import com.hjp.mobilesafe.listener.OnCallListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HJP on 2016/9/7 0007.
 */

public class BlackListQueryDao extends BlackListBaseDao {

    public BlackListQueryDao(Context context) {
        super(context);
    }

    public void queryBlackListPart(int whereType, String start, String limit, final DataListener dataListener) {
        final List<BlackListPhoneNumberInfo> list_blackList_limit = new ArrayList<>();

        String where;
        where = whereType == 1 ? ">?" : "<?";

        final Cursor cursor = sd.query(Constant.DATATABLE_BALCKLIST_NAME, new String[]{Constant.KEY_BLACKLIST_PHONE, Constant.KEY_BLACKLIST_HOLDMODE}
                , Constant.KEY_BLACKLIST_ID + where, new String[]{start}, null, null, "_id DESC", limit);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (cursor.moveToNext()) {

                    String phone = cursor.getString(cursor.getColumnIndex(Constant.KEY_BLACKLIST_PHONE));
                    String holdMode = cursor.getString(cursor.getColumnIndex(Constant.KEY_BLACKLIST_HOLDMODE));

                    BlackListPhoneNumberInfo blackListPhoneNumberInfo = new BlackListPhoneNumberInfo(phone, holdMode);
                    if (cursor.isLast()) {
                        Cursor cursor_id = sd.query(Constant.DATATABLE_BALCKLIST_NAME, new String[]{Constant.KEY_BLACKLIST_ID}
                                , Constant.KEY_BLACKLIST_PHONE + "=?" + " and " + Constant.KEY_BLACKLIST_HOLDMODE + "=?"
                                , new String[]{phone, holdMode}, null, null, null, "1");
                        if (cursor_id.moveToNext()) {
                            int _id = cursor_id.getInt(cursor_id.getColumnIndex(Constant.KEY_BLACKLIST_ID));
                            blackListPhoneNumberInfo.set_id(_id);
                        }
                    }
                    list_blackList_limit.add(blackListPhoneNumberInfo);
                }
                if (cursor != null) {
                    cursor.close();
                }
                dataListener.onData(list_blackList_limit);
            }
        }).start();

    }


    /**
     * 查询某个号码是否在黑名单中
     *
     * @param phoneNumber
     * @return
     */
    public String queryBlackListPartExist(String phoneNumber) {
        String exist_sign = null;

        Cursor cursor = sd.query(Constant.DATATABLE_BALCKLIST_NAME, new String[]{Constant.KEY_BLACKLIST_HOLDMODE}
                , Constant.KEY_BLACKLIST_HOLDMODE + " =?", new String[]{phoneNumber}, null, null, null, "1");
        if (cursor.moveToNext()) {
            exist_sign = cursor.getString(cursor.getColumnIndex(Constant.KEY_BLACKLIST_HOLDMODE));
        }

        cursor.close();
        sd.close();
        return exist_sign;
    }

}
