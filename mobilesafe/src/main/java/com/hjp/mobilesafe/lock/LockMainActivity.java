package com.hjp.mobilesafe.lock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hjp.mobilesafe.R;

import java.util.List;

public class LockMainActivity extends Activity implements OnItemClickListener {

	private ListView listView;
	private Context context;
	private AppInfoDao infoDao;
	private List<String> unLockedDatas;
	private List<String> lockedDatas;
	private List<String> adapterDatas;
	private TextView tvTitle;
	private LockAdapter adapter;
	private WatchDogDao watchDogDao;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_lock);
		Intent intent = new Intent(this, WatchDogService.class);
		startService(intent);
		watchDogDao = new WatchDogDao(context);
		updateData();
		initTitle();
		initListView();
	}

	private void initTitle() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText("未加锁");
		adapterDatas = unLockedDatas;
		tvTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (tvTitle.getText().toString().equals("未加锁")) {
					tvTitle.setText("已加锁");
					// TODO: 去 已加锁界面
					adapterDatas = lockedDatas;
				} else {
					tvTitle.setText("未加锁");
					// TODO：去 未加锁界面
					adapterDatas = unLockedDatas;
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	private void updateData() {
		infoDao = new AppInfoDao();
		unLockedDatas = infoDao.getAllApps(context);
		lockedDatas = watchDogDao.queryAllInfos();
		for (String text : lockedDatas) {
			if (unLockedDatas.contains(text))
				unLockedDatas.remove(text);
		}
	}

	private void initListView() {
		setTitle("程序锁功能");
		listView = (ListView) findViewById(R.id.lockListView);
		adapter = new LockAdapter();
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	private class LockAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return adapterDatas.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new TextView(context);
			}
			TextView textView = (TextView) convertView;
			textView.setPadding(8, 8, 8, 8);
			textView.setTextSize(18);
			textView.setText(adapterDatas.get(position));
			return textView;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
		System.out.println("position： " + position);
		System.out.println("id： " + id);
		vtoast("position： " + position + "\n id： " + id);
		if (tvTitle.getText().toString().equals("未加锁")) {
			String removedPackageName = unLockedDatas.remove(position);
			watchDogDao.insert(removedPackageName);
			lockedDatas.add(removedPackageName);
			adapter.notifyDataSetInvalidated();
		} else {
			String removedPackageName = lockedDatas.remove(position);
			watchDogDao.delete(removedPackageName);
			unLockedDatas.add(removedPackageName);
			adapter.notifyDataSetInvalidated();
		}

	}

	protected void vtoast(String text) {
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
	}
}
