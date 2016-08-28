package com.hjp.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.hjp.activity.R;

/**
 * Created by HJP on 2016/8/20 0020.
 */

public class LightPointView extends View {
    /**
     * padding距离
     */
    private int paddingLeft = 0;
    private int paddingRight = 0;
    private int paddingTop = 0;
    private int paddingBottom = 0;
    /**
     * 小圆点的个数
     */
    private int count_point = 0;
    /**
     * 小圆点之间的间隙
     */
    private int space_point = 20;
    /**
     * 小圆点的直径
     */
    private int diameter_circle_point = 0;
    /**
     * 小圆点的半径
     */
    private int radius_circle_point;
    /**
     * 小圆点的暗色(不是当前聚焦点)
     */
    private int dark_circle_point = getResources().getColor(R.color.colorDarkCirclePoint);
    /**
     * 小圆点的亮色(当前聚焦点)
     */
    private int light_circle_point = getResources().getColor(R.color.colorLightCirclePoint);
    /**
     * 当前聚焦点的位置
     */
    private int position_currpoint = 0;
    /**
     * 小圆点的画笔
     */
    private Paint paint_circlePoint;
    private int currViewWidth = 0;
    private int currViewHeight = 0;

    public LightPointView(Context context) {
        this(context, null);
    }

    public LightPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //画笔初始化
        initPaint();
    }

    private void initPaint() {
        paint_circlePoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint_circlePoint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingTop = getPaddingTop();
        paddingBottom = getPaddingBottom();

        if (widthMeasureSpec == MeasureSpec.AT_MOST) {
            if (count_point == 0) {
                return;
            }
            //有小圆点，则用间隙长度作为小圆点的半径，
            // 公式：间隙长度*(小圆点个数-1)+所有小圆点的直径+paddingLeft+paddingRight
            diameter_circle_point = space_point * 2;
            currViewWidth = space_point * (count_point - 1) + count_point * diameter_circle_point
                    + paddingLeft + paddingRight;
        }
        if (heightMeasureSpec == MeasureSpec.AT_MOST) {
            //前面未计算
            if (diameter_circle_point == 0) {
                diameter_circle_point = space_point * 2;
            }
            currViewHeight = diameter_circle_point + paddingTop + paddingBottom;
        }
        setMeasuredDimension(currViewWidth, currViewHeight);
        //小圆点的圆心纵坐标
        y_currStart = paddingTop + diameter_circle_point / 2;
        //小圆点的半径
        radius_circle_point = diameter_circle_point / 2;
    }


    /**
     * 小圆点的圆心位置
     */
    private int x_currStart;
    private int y_currStart;

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        for (int i = 0; i < count_point; i++) {
            if (i == position_currpoint) {
                paint_circlePoint.setColor(light_circle_point);
            } else {
                paint_circlePoint.setColor(dark_circle_point);
            }
            x_currStart = (int) ((i + 0.5) * diameter_circle_point + space_point * i);
            canvas.drawCircle(x_currStart, y_currStart, radius_circle_point, paint_circlePoint);
        }
    }

    /**
     * 点亮指定小圆点
     */
    public void setCurrLightPosition(int position) {
        position_currpoint = position;
        postInvalidate();
    }

}
