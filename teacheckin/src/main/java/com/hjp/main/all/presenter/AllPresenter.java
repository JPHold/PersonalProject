package com.hjp.main.all.presenter;

import android.content.Context;

import com.hjp.main.all.service.AllService;
import com.hjp.main.all.service.impl.AllServiceImpl;
import com.hjp.main.all.view.AllView;
import com.hjp.others.util.SecureUtil;

import static com.hjp.main.gesture.view.activity.GestureLockActivity.KEYSTORE_FILE;
import static com.hjp.main.gesture.view.activity.GestureLockActivity.KEYSTORE_PASSWORD;

/**
 * Created by HJP on 2016/12/4.
 */

public class AllPresenter {

    public AllView mAllView;
    private Context mContext;
    private AllService mAllService;

    public AllPresenter(AllView allView, Context context) {
        this.mAllView = allView;
        this.mContext=context;
        mAllService =new AllServiceImpl();
    }

    public void dissectionKeyPwd(){
        if (mAllView.getGestureState()) {
            //解密
            SecureUtil.dissection(mContext, "PKCS12", KEYSTORE_FILE, KEYSTORE_PASSWORD, mAllView.getKeyGesturePassword(), "ISO-8859-1",
                    new SecureUtil.OnResultListener() {
                        @Override
                        public void onResult(String password) {
                            mAllView.setKeyPwd2GestureView(password);
                        }

                        @Override
                        public void onError(String data) {

                        }
                    });
        }

    }
}
