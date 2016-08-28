package com.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by HJP on 2016/8/27 0027.
 */

public class ColorFilterLayout extends ViewGroup {

    private ColorDrawable mColorDrawable;

    public ColorFilterLayout(Context context) {
        this(context, null);
    }

    public ColorFilterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorFilterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View.inflate(context, R.layout.test_colorfilter, this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mColorDrawable.draw(canvas);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    public void setColorFilter(int color, PorterDuff.Mode mode) {
        if (mColorDrawable == null) {
            mColorDrawable = new ColorDrawable();
            mColorDrawable.setColorFilter(new PorterDuffColorFilter(color, mode));
            mColorDrawable.setAlpha(255 * 255 >> 8);
            invalidate();
        }
    }
}
