package com.hjp.mobilesafe.lock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WatchDogOpenHelper extends SQLiteOpenHelper {

	public static final String TABLENAME = "appLocktb";
	public WatchDogOpenHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists appLocktb(_id integer primary key, packageName text not null)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
