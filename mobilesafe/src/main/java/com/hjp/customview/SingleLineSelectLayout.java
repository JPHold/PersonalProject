package com.hjp.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hjp.activity.R;

/**
 * Created by HJP on 2016/8/25 0025.
 */

public class SingleLineSelectLayout extends ViewGroup {

    private Context mContext;
    private RelativeLayout mSingleLineLayout;
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
    private static final String OPENSTARTSTRING = "开启";
    private static final String CLOSESTARTSTRING = "关闭";
    private ColorFilter mColorFilter;
    private boolean mHasColorFilter = false;
    private boolean mColorMod = false;
    private Drawable mDrawable;
//    private ImageViewBitmapDrawable mRecycleableBitmapDrawable = null;
    private int mDrawableWidth;
    private int mDrawableHeight;
    private boolean mHasDrawableTint=false;
    private boolean mHasDrawableTintMode=false;

    public SingleLineSelectLayout(Context context) {
        this(context, null);
    }

    public SingleLineSelectLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleLineSelectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        //将布局挂在此Layout
        mSingleLineLayout = (RelativeLayout) View.inflate(context, R.layout.singlelineselect, this);
        mTextV_title = (TextView) mSingleLineLayout.findViewById(R.id.textV_singleLineSelect_title);
        mTextV_summary = (TextView) mSingleLineLayout.findViewById(R.id.textV_singleLineSelect_summary);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int childCount = getChildCount();
        View childView;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            //如果是第一个child是用来显示标题
            if (i == 0) {
                ((TextView) childView).setText(mMainTitle);//显示主标题
            } else if (i == 1) {
                ((TextView) childView).setText(mSummaryTitle);//显示副标题
            } else {
                //最右边的child-view记录了单行设置的开启状态
                //①：child-view为checkBox，只设置为勾选，代表开启
                //②：child-view为imageView，代表关闭
                if (childView instanceof CheckBox && mIsSettingOpen) {
                    ((CheckBox) childView).setChecked(true);
                } else if (childView instanceof ImageView && !mIsSettingOpen) {
                    ((ImageView) childView).setImageResource(R.mipmap.singlelineselect_secondsetting);
                }
            }
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    /**
     * this-layout被被点击时，开启状态的改变
     */
    public void onClickStateChange() {
        mIsSettingOpen = !mIsSettingOpen;
        mSummaryTitle = mIsSettingOpen == true ? mSummaryTitle.replace(CLOSESTARTSTRING, OPENSTARTSTRING)
                : mSummaryTitle.replace(OPENSTARTSTRING, CLOSESTARTSTRING);//替换开启或者关闭
        postInvalidate();
    }

    /**
     * 当前设置如果是有第二级设置，则不显示checkBox而是显示箭头图片,反之则显示checkBox而不显示箭头图片
     *
     * @param isSecondSetting
     */
    public void setSettingState(boolean isSecondSetting) {

        //清空设置状态view
        int childCount = mSingleLineLayout.getChildCount();
        if (childCount == 3) {
            mSingleLineLayout.removeViewAt(childCount - 1);
        }

        RelativeLayout.LayoutParams lParams_setting = new RelativeLayout.LayoutParams(30, 30);
        lParams_setting.addRule(RelativeLayout.CENTER_VERTICAL);
        lParams_setting.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (isSecondSetting) {
            ImageView imgV_jump2SecondSetting = new ImageView(mContext);

            imgV_jump2SecondSetting.setImageResource(R.mipmap.singlelineselect_secondsetting);

            mSingleLineLayout.addView(imgV_jump2SecondSetting, lParams_setting);
        } else {
            CheckBox checkBox = new CheckBox(mContext);
            checkBox.setChecked(mIsSettingOpen);
            mSingleLineLayout.addView(checkBox);
        }
    }

    /**
     * 是否使用当前设置，checkBox相应地勾选or不勾选
     *
     * @param isOpen
     */
    public void setOpenState(boolean isOpen) {
        mIsSettingOpen = isOpen;
        postInvalidate();
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
