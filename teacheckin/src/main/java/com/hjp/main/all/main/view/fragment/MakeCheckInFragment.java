package com.hjp.main.all.main.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hjp.R;
import com.hjp.main.all.main.presenter.MakeCheckInPresenter;
import com.hjp.main.all.main.service.bean.MakeCheckIn;
import com.hjp.main.all.main.view.MakeCheckInView;
import com.hjp.main.all.main.view.adapter.MakeCheckInAdapter;
import com.hjp.main.all.main.view.fragment.base.BaseFragment;
import com.hjp.main.all.view.RelativeView;
import com.hjp.others.app.MainApplication;

import java.util.List;

/**
 * Created by HJP on 2016/12/12.
 */

public class MakeCheckInFragment extends BaseFragment implements MakeCheckInView {


    private RelativeView mRelativeView;
    private String mCurrCourseId;
    private MakeCheckInPresenter mMakeCheckInPresenter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private MakeCheckInAdapter mMakeCheckInAdapter;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        mCurrCourseId = (String) args.get("courseId");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mRelativeView = (RelativeView) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isPrepared = true;
        mProgressBar = (ProgressBar) mMakeCheckInLayout.findViewById(R.id.main_makeCheckIn_progressBar);
        mRecyclerView = (RecyclerView) mMakeCheckInLayout.findViewById(R.id.main_makeCheckIn_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false ));
    }

    @Override
    public void doThing() {
        mMakeCheckInPresenter.findHistoryMakeCheckIn(mCurrCourseId);
    }


    private void initView() {
        mMakeCheckInPresenter = new MakeCheckInPresenter(this, MainApplication.getContext());
    }

    @Override
    public void startObtainMakeCheckIn() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void successObtainMakeCheckIn(List<MakeCheckIn> data) {
        mMakeCheckInAdapter=new MakeCheckInAdapter(mContext,data);
        mRecyclerView.setAdapter(mMakeCheckInAdapter);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void errorObtainMakeCheckIn(int status, String msg) {
        Toast.makeText(mContext, status + "msg", Toast.LENGTH_LONG).show();
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
