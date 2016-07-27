package com.hjp.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Map;

/**
 * Created by HJP on 2015/10/24 0024.
 */
public class AppDataBase extends SQLiteOpenHelper {
    public static final String QUERY_ERROR = "QUERY_ERROR";
    private Context mcontext;
    private SQLiteDatabase sd;
    //表sql
    public String CREATE_TABLE;

    public AppDataBase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * @param table 操作的数据表(是一条sql语句,如"create table if not exists userstate
     *              (createdAt Text,userName Text,loginState)"
     */
    public void setProcessTable(String table) {
        CREATE_TABLE = table;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.i("shelp", "创建表失败");
        }
    }


    @Override
    public SQLiteDatabase getWritableDatabase() {
        sd = super.getWritableDatabase();
        return sd;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        sd = super.getReadableDatabase();
        return sd;
    }

    private void checkDataBaseOpenState() {
        try {
            getWritableDatabase();
        } catch (SQLException e) {
            getReadableDatabase();
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    /**
     * @param table          要操作的表名
     * @param nullColumnHack 一般为null
     * @param map_data       存放要插入到数据表的键值组(键要跟数据表的字段相等)
     * @return
     */

    public long insert(String table, String nullColumnHack, Map<String, String> map_data) {
        long num = 0;
        if (sd != null) {
            ContentValues values = new ContentValues();
            for (String key : map_data.keySet()) {
                values.put(key, map_data.get(key));
            }

            num = sd.insert(table, nullColumnHack, values);
            Log.i("ease", "保存数据" + num);
            if (num == -1) {
                close();
                return 0;
            }
            num=1;
        } else {
            checkDataBaseOpenState();
            num = insert(table, nullColumnHack, map_data);
        }
        //数据库关闭
        close();
        return num;
    }

    public String select(String table, String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy, String limit) {
      checkDataBaseOpenState();
        Cursor cursor = sd.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
        String result = "";
        if (cursor.moveToNext()) {
            result = cursor.getString(cursor.getColumnIndex("userName"));
        } else {
            result = QUERY_ERROR;
        }
        close();
        return result;
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
