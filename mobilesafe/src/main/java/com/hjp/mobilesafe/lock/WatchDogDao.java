package com.hjp.mobilesafe.lock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WatchDogDao {

	public static final String TABLENAME = WatchDogOpenHelper.TABLENAME;
	public static final String PACKAGENAME = "packageName";
	private WatchDogOpenHelper openHelper;

	public WatchDogDao(Context context) {
		openHelper = new WatchDogOpenHelper(context, "watchDog.db", 1);
		// "create table if not exists appLocktb(_id integer primary key, packageName text not null)");
	}

	/**
	 * 插入一条数据，如果数据已经存在，就不插入
	 * 
	 * @param packageName
	 * @return 插入成功.失败
	 */
	public boolean insert(String packageName) {
		boolean insert = false;
		SQLiteDatabase db = openHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery(
				"select * from appLocktb where packageName=?",
				new String[] { packageName });

		if (cursor != null) {
			insert = cursor.moveToNext();
			cursor.close();
		}
		ContentValues values = new ContentValues();
		values.clear();
		if (insert) {
			return insert;
		} else {
			values.put(PACKAGENAME, packageName);
			long data = db.insert(TABLENAME, null, values);
			db.close();
			if (data != -1) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 删除一条数据，如果数据不存在，就不删除
	 * 
	 * @param packageName
	 * @return 删除成功.失败
	 */
	public boolean delete(String packageName) {
		boolean delete = false;
		SQLiteDatabase db = openHelper.getWritableDatabase();

		Cursor cursor = db.rawQuery(
				"select * from appLocktb where packageName=?",
				new String[] { packageName });

		if (cursor != null) {
			delete = cursor.moveToNext();
			cursor.close();
		}
		ContentValues values = new ContentValues();
		values.clear();
		if (delete) {
			// 说明有这条数据，删除
			int data = db.delete(TABLENAME, PACKAGENAME + "=?",
					new String[] { packageName });
			db.close();
			if(data>0){
				return true;
			}else {
				return false;
			}
		} else {
			// 说明没有有这条数据，删除
			return false;
		}
	}
	/**
	 * 查询数据库有没有对应的数据
	 * @param packageName
	 */
	public boolean  query(String packageName){
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from appLocktb where packageName=?",
				new String[] { packageName });
		if(cursor!=null){
			boolean moveToNext = cursor.moveToNext();
			cursor.close();
			db.close();
			if(moveToNext)
				return true;
			else {
				return false;
			}
		}
		return false;
	}
	
	public List<String> queryAllInfos(){
		List<String> packageNames = new ArrayList<String>();
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from appLocktb", null);
		if(cursor!=null){
			while(cursor.moveToNext()){
				String name = cursor.getString(cursor.getColumnIndex(PACKAGENAME));
				packageNames.add(name);
			}
			cursor.close();
		}
		db.close();
		return packageNames;
	}
}
