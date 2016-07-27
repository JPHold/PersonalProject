package com.CustomView;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by HJP on 2016/6/14 0014.
 */
public class PressSelectTabLayout extends TabLayout {

    private boolean isCanSelect=true;

    public PressSelectTabLayout(Context context) {
        this(context, null);
    }

    public PressSelectTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PressSelectTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCanSelect(boolean canSelect) {
        isCanSelect = canSelect;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanSelect) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isCanSelect) {
            return true;
        }
        return false;
    }
}
