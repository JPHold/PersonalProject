package com.extraFunction.Layout.TagCloudLayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hjp.coursecheckin.R;

/**
 * Created by HJP on 2016/6/30 0030.
 */

public class TagCloudLayout extends ViewGroup {
    private int mLineSpacing;
    private int mTagSpacing;
    private BaseAdapter mAdapter;
    private DataSetObserver mObserver;
    private TagClickListener mTagClickListener;

    public TagCloudLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TagCloudLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //TypedArray是否包含为设值的属性,待确认
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.TagCloudLayout, defStyleAttr, 0);
       mLineSpacing=attrs.getAttributeIntValue(R.styleable.TagCloudLayout_lineSpacing,20);
        mTagSpacing=attrs.getAttributeIntValue(R.styleable.TagCloudLayout_tagSpacing,20);
    }
    private void drawLayout() {
        if (mAdapter == null || mAdapter.getCount() == 0) {
            return;
        }
        this.removeAllViews();

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View view = mAdapter.getView(i,null,null);
          /*  final  int  position=i;
          view.setId(position);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                        mTagClickListener.tagClick(position);
                }
            });*/
            this.addView(view);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wantHeight = 0;
        int wantWidth = resolveSize(0, widthMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams params = childView.getLayoutParams();
            childView.measure(
                    getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, params.width),
                    getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, params.height)
            );

            int childHeight = childView.getHeight();
            int childWidth = childView.getWidth();
            lineHeight = Math.max(childHeight, lineHeight);

            if (childLeft + childWidth + paddingRight > wantWidth) {
                childLeft = paddingLeft;
                childTop += mLineSpacing + childHeight;
                lineHeight = childHeight;
            } else {
                childLeft += childWidth + mTagSpacing;
            }


        }
        wantHeight = childTop + lineHeight + paddingBottom;
        setMeasuredDimension(wantWidth, resolveSize(wantHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();

        int childLeft = paddingLeft;
        int childTop = paddingTop;

        int lineHeight = 0;

        for (int i = 0, childCount = getChildCount(); i < childCount; ++i) {

            final View childView = getChildAt(i);

            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            lineHeight = Math.max(childHeight, lineHeight);

            if (childLeft + childWidth + paddingRight > width) {
                childLeft = paddingLeft;
                childTop += mLineSpacing + lineHeight;
                lineHeight = childHeight;
            }

            childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + mTagSpacing;
        }
    }

    public void setAdapter(BaseAdapter adapter){
        if (mAdapter == null){
            mAdapter = adapter;
            drawLayout();
        }
    }
    public interface TagClickListener {
        void tagClick(int position);
    }

    public void setTagClickListener(TagClickListener    tagClickListener){
        mTagClickListener =tagClickListener;
    }


}
