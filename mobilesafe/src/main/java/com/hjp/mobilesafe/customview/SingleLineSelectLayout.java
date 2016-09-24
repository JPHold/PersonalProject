package com.hjp.mobilesafe.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjp.mobilesafe.R;
import com.hjp.mobilesafe.listener.OnCallListener;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class SingleLineSelectLayout extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "SingleLineSelectLayout";
    private Context mContext;
    private SingleLineSelectLayout mSingleLineLayout;
    private RelativeLayout mSingleLineContentLayout;
    private TextView mTextV_title;
    private TextView mTextV_summary;

    /**
     * 单行设置中的主标题
     */
    private String mMainTitle;
    /**
     * 单行设置中的副标题
     */
    private String mSummaryTitle;
    /**
     * 单行设置中需要CheckBox来记录开启or关闭状态
     */
    private boolean mIsSettingOpen;

    /**
     * 开启和关闭的字符串
     */
    public static final String OPENSTARTSTRING = "开启";
    public static final String CLOSESTARTSTRING = "关闭";
    /**
     * 第二级设置跳转的通知
     */
    public static final String SECONDSETORDER = "secondSet";


    private ColorFilter mColorFilter;
    private boolean mHasColorFilter = false;
    private boolean mColorMod = false;
    private Drawable mDrawable;
    //    private ImageViewBitmapDrawable mRecycleableBitmapDrawable = null;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private boolean mHasDrawableTint = false;
    private boolean mHasDrawableTintMode = false;
    private int height_mainTitle = 0;
    private int height_summaryTitle = 0;
    private float y_mainTitle = 0;
    private float y_summaryTitle = 0;
    private OnCallListener mOnCallListener;

    public SingleLineSelectLayout(Context context) {
        this(context, null);
    }

    public SingleLineSelectLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleLineSelectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        //加载属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SingleLineSelectLayout, defStyleAttr, 0);
        mMainTitle = typedArray.getString(R.styleable.SingleLineSelectLayout_mainTitle);
        mSummaryTitle = typedArray.getString(R.styleable.SingleLineSelectLayout_summaryTitle);

        typedArray.recycle();

        //将布局挂在此Layout
        mSingleLineLayout = (SingleLineSelectLayout) View.inflate(context, R.layout.singlelineselect, this);
        mSingleLineContentLayout = (RelativeLayout) mSingleLineLayout.findViewById(R.id.singleLineSelectContentLayout);
        mTextV_title = (TextView) mSingleLineLayout.findViewById(R.id.textV_singleLineSelect_title);
        mTextV_summary = (TextView) mSingleLineLayout.findViewById(R.id.textV_singleLineSelect_summary);
        //设置标题
        setMainTitle(mMainTitle);
        setSummaryTitle(mSummaryTitle);
        //点击this-layout，修改开启or关闭设置
        mSingleLineContentLayout.setOnClickListener(this);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.i(TAG, "onLayout: ");
        height_mainTitle = mTextV_title.getHeight();
        y_mainTitle = mTextV_title.getY();
        Log.i(TAG, "onLayout: height_mainTitle" + height_mainTitle);
        Log.i(TAG, "onLayout: y_mainTitle" + y_mainTitle);
    }

    /**
     * 设置主标题
     *
     * @param mainTitle 标题名
     */
    public void setMainTitle(String mainTitle) {
        boolean empty = TextUtils.isEmpty(mainTitle);
        if (empty) {
            return;
        }

        boolean equals = mainTitle.equals(mTextV_title.getText().toString());
        if (equals) {
            return;
        }
        //不相等，才重新赋值
        mMainTitle = mainTitle;
        mTextV_title.setText(mMainTitle);
    }

    /**
     * 设置副标题
     *
     * @param summaryTitle 副标题
     */
    public void setSummaryTitle(String summaryTitle) {
        boolean empty = TextUtils.isEmpty(summaryTitle);
        if (empty) {
            return;
        }

        boolean equals = summaryTitle.equals(mTextV_summary.getText().toString());
        if (equals) {
            return;
        }
        //不相等，才重新赋值
        mSummaryTitle = summaryTitle;
        mTextV_summary.setText(mSummaryTitle);
    }

    /**
     * this-layout被被点击时，开启状态的改变
     */
    public void onClickStateChange() {
        View childView = mSingleLineContentLayout.getChildAt(2);
        if (childView instanceof CheckBox) {
            Log.i(TAG, "onClickStateChange:CheckBox ");
            mIsSettingOpen = !mIsSettingOpen;

            if (!TextUtils.isEmpty(mSummaryTitle)) {
                mSummaryTitle = mIsSettingOpen == true ? mSummaryTitle.replace(CLOSESTARTSTRING, OPENSTARTSTRING)
                        : mSummaryTitle.replace(OPENSTARTSTRING, CLOSESTARTSTRING);//替换开启或者关闭
                //设置副标题，显示设置是开启还是关闭
                mTextV_summary.setText(mSummaryTitle);
            }

            ((CheckBox) childView).setChecked(mIsSettingOpen);
            //通知Activity保存设置状态
            mOnCallListener.onCall(this, mIsSettingOpen == true ? OPENSTARTSTRING : CLOSESTARTSTRING);
            Log.i(TAG, "onClickStateChange: "+mIsSettingOpen);
        } else if (childView instanceof ImageView) {
            //通知Activity替换第二级设置布局
            mOnCallListener.onCall(this, SECONDSETORDER);
        }
    }

    /**
     * 当前设置如果是有第二级设置，则不显示checkBox而是显示箭头图片,反之则显示checkBox而不显示箭头图片
     *
     * @param hasSecondSetting
     */
    public void setSecondSettingState(final boolean hasSecondSetting) {
        Log.i(TAG, "setSettingState: ");
       /* //清空设置状态view
        int childCount = mSingleLineContentLayout.getChildCount();
        Log.i(TAG, "setSettingState:childCount：" + childCount);
        if (childCount == 3) {
            mSingleLineContentLayout.removeViewAt(childCount - 1);
        }*/

        //提前获知两个标题view的位置参数
        ViewTreeObserver viewTreeObserver_mainTitle = mTextV_title.getViewTreeObserver();
        viewTreeObserver_mainTitle.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mTextV_title.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                height_mainTitle = mTextV_title.getHeight();
                y_mainTitle = mTextV_title.getY();
                ViewTreeObserver viewTreeObserver_summaryTitle = mTextV_summary.getViewTreeObserver();
                viewTreeObserver_summaryTitle.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mTextV_summary.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        height_summaryTitle = mTextV_summary.getHeight();
                        y_summaryTitle = mTextV_summary.getY();

                        //两个标题view中心之间的垂直距离
                        //公式1：(y_summaryTitle-y_mainTitle-height_mainTitle/2)+height_summaryTitle/2
                        int verLength_twoTitleView_centerPoint = (int) ((y_summaryTitle - y_mainTitle - height_mainTitle / 2) + height_summaryTitle / 2);
                        Log.i(TAG, "setSettingState: verLength_twoTitleView_centerPoint：" + verLength_twoTitleView_centerPoint);
                        //计算上面的中心垂直距离用来放置设置状态view，其左上角的纵坐标
                        //公式2：((公式1)/4+height_mainTitle/2)+y_mainTitle。计算出设置状态view在this-layout中，跟两个标题view看起来是统一居中
                        int y_settingStateView = (int) ((verLength_twoTitleView_centerPoint / 4 + height_mainTitle / 2) + y_mainTitle);
                        Log.i(TAG, "setSettingState: y_settingStateView：" + y_settingStateView);

                        RelativeLayout.LayoutParams lParams_setting = new RelativeLayout.LayoutParams(30, 30);
                        lParams_setting.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        lParams_setting.setMargins(0, y_settingStateView, 10, 0);

                        if (hasSecondSetting) {
                            ImageView imgV_jump2SecondSetting = new ImageView(mContext);
                            imgV_jump2SecondSetting.setImageResource(R.mipmap.singlelineselect_secondsetting);
                            Log.i(TAG, "onGlobalLayout: ImageView");
                            mSingleLineContentLayout.addView(imgV_jump2SecondSetting, lParams_setting);
                        } else {
                            CheckBox checkBox = new CheckBox(mContext);
                            checkBox.setChecked(mIsSettingOpen);
                            checkBox.setGravity(Gravity.CENTER);
                            mSingleLineContentLayout.addView(checkBox, lParams_setting);
                        }
                    }
                });

            }
        });

    }

    /**
     * 是否使用当前设置，checkBox相应地勾选or不勾选
     *
     * @param isOpen
     */
    public void setOpenState(boolean isOpen) {
        mIsSettingOpen = isOpen;
    }

    public boolean getOpenState() {
        return mIsSettingOpen;
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "onClick: ");
        onClickStateChange();
    }

    public void setConnect(OnCallListener onCallListener) {
        mOnCallListener = onCallListener;
    }

 /*   private void applyImageTint() {
        if (mDrawable != null && (mHasDrawableTint || mHasDrawableTintMode)) {
            mDrawable = mDrawable.mutate();

            if (mHasDrawableTint) {
                mDrawable.setTintList(mDrawableTintList);
            }

            if (mHasDrawableTintMode) {
                mDrawable.setTintMode(mDrawableTintMode);
            }

            // The drawable (or one of its children) may not have been
            // stateful before applying the tint, so let's try again.
            if (mDrawable.isStateful()) {
                mDrawable.setState(getDrawableState());
            }
        }
    }

    private void configureBounds() {
        if (mDrawable == null || !mHaveFrame) {
            return;
        }

        int dwidth = mDrawableWidth;
        int dheight = mDrawableHeight;

        int vwidth = getWidth() - mPaddingLeft - mPaddingRight;
        int vheight = getHeight() - mPaddingTop - mPaddingBottom;

        boolean fits = (dwidth < 0 || vwidth == dwidth) &&
                (dheight < 0 || vheight == dheight);

        if (dwidth <= 0 || dheight <= 0 || ImageView.ScaleType.FIT_XY == mScaleType) {
            *//* If the drawable has no intrinsic size, or we're told to
                scaletofit, then we just fill our entire view.
            *//*
            mDrawable.setBounds(0, 0, vwidth, vheight);
            mDrawMatrix = null;
        } else {
            // We need to do the scaling ourself, so have the drawable
            // use its native size.
            mDrawable.setBounds(0, 0, dwidth, dheight);

            if (ImageView.ScaleType.MATRIX == mScaleType) {
                // Use the specified matrix as-is.
                if (mMatrix.isIdentity()) {
                    mDrawMatrix = null;
                } else {
                    mDrawMatrix = mMatrix;
                }
            } else if (fits) {
                // The bitmap fits exactly, no transform needed.
                mDrawMatrix = null;
            } else if (ImageView.ScaleType.CENTER == mScaleType) {
                // Center bitmap in view, no scaling.
                mDrawMatrix = mMatrix;
                mDrawMatrix.setTranslate(Math.round((vwidth - dwidth) * 0.5f),
                        Math.round((vheight - dheight) * 0.5f));
            } else if (ImageView.ScaleType.CENTER_CROP == mScaleType) {
                mDrawMatrix = mMatrix;

                float scale;
                float dx = 0, dy = 0;

                if (dwidth * vheight > vwidth * dheight) {
                    scale = (float) vheight / (float) dheight;
                    dx = (vwidth - dwidth * scale) * 0.5f;
                } else {
                    scale = (float) vwidth / (float) dwidth;
                    dy = (vheight - dheight * scale) * 0.5f;
                }

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate(Math.round(dx), Math.round(dy));
            } else if (ImageView.ScaleType.CENTER_INSIDE == mScaleType) {
                mDrawMatrix = mMatrix;
                float scale;
                float dx;
                float dy;

                if (dwidth <= vwidth && dheight <= vheight) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) vwidth / (float) dwidth,
                            (float) vheight / (float) dheight);
                }

                dx = Math.round((vwidth - dwidth * scale) * 0.5f);
                dy = Math.round((vheight - dheight * scale) * 0.5f);

                mDrawMatrix.setScale(scale, scale);
                mDrawMatrix.postTranslate(dx, dy);
            } else {
                // Generate the required transform.
                mTempSrc.set(0, 0, dwidth, dheight);
                mTempDst.set(0, 0, vwidth, vheight);

                mDrawMatrix = mMatrix;
                mDrawMatrix.setRectToRect(mTempSrc, mTempDst, scaleTypeToScaleToFit(mScaleType));
            }
        }
    }


    private void updateDrawable(Drawable d) {
        if (d != mRecycleableBitmapDrawable && mRecycleableBitmapDrawable != null) {
            mRecycleableBitmapDrawable.setBitmap(null);
        }

        if (mDrawable != null) {
            mDrawable.setCallback(null);
            unscheduleDrawable(mDrawable);
        }

        mDrawable = d;

        if (d != null) {
            d.setCallback(this);
            d.setLayoutDirection(getLayoutDirection());
            if (d.isStateful()) {
                d.setState(getDrawableState());
            }
            d.setVisible(getVisibility() == VISIBLE, true);
            d.setLevel(mLevel);
            mDrawableWidth = d.getIntrinsicWidth();
            mDrawableHeight = d.getIntrinsicHeight();
            applyImageTint();
            applyColorMod();

            configureBounds();
        } else {
            mDrawableWidth = mDrawableHeight = -1;
        }
    }


    *//**
     * Sets a drawable as the content of this ImageView.
     *
     * @param drawable the Drawable to set, or {@code null} to clear the
     *                 content
     *//*
    public void setImageDrawable(@Nullable Drawable drawable) {
        if (mDrawable != drawable) {
            mResource = 0;
            mUri = null;

            final int oldWidth = mDrawableWidth;
            final int oldHeight = mDrawableHeight;

            updateDrawable(drawable);

            if (oldWidth != mDrawableWidth || oldHeight != mDrawableHeight) {
                requestLayout();
            }
            invalidate();
        }
    }


    *//**
     * 初始化drawable
     *//*
    public void a() {
        // Hacky fix to force setImageDrawable to do a full setImageDrawable
        // instead of doing an object reference comparison
        mDrawable = null;
        if (mRecycleableBitmapDrawable == null) {
            mRecycleableBitmapDrawable = new ImageViewBitmapDrawable(
                    mContext.getResources(), bm);
        } else {
            mRecycleableBitmapDrawable.setBitmap(bm);
        }
        setImageDrawable(mRecycleableBitmapDrawable);
    }

    *//**
     * @param color
     * @param mode
     *//*
    public final void setColorFilter(int color, PorterDuff.Mode mode) {
        setColorFilter(new PorterDuffColorFilter(color, mode));
    }

    *//**
     * Apply an arbitrary colorfilter to the image.
     *
     * @param cf the colorfilter to apply (may be null)
     *//*
    public void setColorFilter(ColorFilter cf) {
        if (mColorFilter != cf) {
            mColorFilter = cf;
            mHasColorFilter = true;
            mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }


    private void applyColorMod() {
        // Only mutate and apply when modifications have occurred. This should
        // not reset the mColorMod flag, since these filters need to be
        // re-applied if the Drawable is changed.
        if (mDrawable != null && mColorMod) {
            mDrawable = mDrawable.mutate();
            if (mHasColorFilter) {
                mDrawable.setColorFilter(mColorFilter);
            }
            *//*mDrawable.setXfermode(mXfermode);
            mDrawable.setAlpha(mAlpha * mViewAlphaScale >> 8);*//*
        }
    }

    private static class ImageViewBitmapDrawable extends BitmapDrawable {
        public ImageViewBitmapDrawable(Resources res, Bitmap bitmap) {
            super(res, bitmap);
        }

        @Override
        public void setBitmap(Bitmap bitmap) {
            super.setBitmap(bitmap);
        }

    }*/
}
