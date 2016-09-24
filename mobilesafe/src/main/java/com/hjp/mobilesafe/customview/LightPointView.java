package com.hjp.mobilesafe.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by HJP on 2016/8/20 0020.
 */

public class LightPointView extends View {

    private static final String TAG = "LightPointView";

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
    private int count_point = 4;
    /**
     * 小圆点之间的间隙
     */
    private int space_point = 10;
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
    private int dark_circle_point = Color.GRAY;
    /**
     * 小圆点的亮色(当前聚焦点)
     */
    private int light_circle_point = Color.WHITE;
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

    /**
     * 横向绘制宽度和小圆点s在横向上的间隔空间
     */
    private int space_horDrawWidth_and_circlePoints = 0;

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

        currViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        currViewHeight = MeasureSpec.getSize(heightMeasureSpec);

        Log.i(TAG, "onMeasure: 宽度------------------------");
        if (widthMeasureSpec == MeasureSpec.AT_MOST) {
            if (count_point == 0) {
                return;
            }
            //有小圆点，则用间隙长度作为小圆点的半径，
            // 公式：间隙长度*(小圆点个数-1)+所有小圆点的直径+paddingLeft+paddingRight
            Log.i(TAG, "onMeasure:space_point：" + space_point);
            diameter_circle_point = space_point * 2;
            Log.i(TAG, "onMeasure: diameter_circle_point：" + diameter_circle_point);
            currViewWidth = space_point * (count_point - 1) + count_point * diameter_circle_point
                    + paddingLeft + paddingRight;
            Log.i(TAG, "onMeasure: currViewWidth：" + currViewWidth);

        } else {
            //根据宽度，计算小圆点的直径、各个小圆点之间的距离
            //因为我采取"小圆点的半径跟它们之间距离相等"的idea

            int allCirclePoint_length = (3 * count_point - 1) * space_point;
            int valueHorDrawHeight = currViewWidth - paddingLeft - paddingRight;
            if (allCirclePoint_length > (valueHorDrawHeight)) {
                //所有小圆点的整个宽度大于横向绘制宽度，则根据横向绘制宽度平均分配每个小圆点
                // 重新计算小圆点的大小，公式：(宽度-paddingleft-paddingright)/(小圆点个数)*2+(所有小圆点之间的间隔数)
                space_point = (valueHorDrawHeight) / (3 * count_point - 1);
            } else if (allCirclePoint_length < valueHorDrawHeight) {
                //所有小圆点的整个宽度小于横向绘制宽度，则在横向绘制宽度内，居中显示小圆点s
                space_horDrawWidth_and_circlePoints = (valueHorDrawHeight - allCirclePoint_length) / 2;
            }
            diameter_circle_point = space_point * 2;
        }

        Log.i(TAG, "onMeasure: 长度-----------------------------");
        if (heightMeasureSpec == MeasureSpec.AT_MOST) {
            //前面未计算
            if (diameter_circle_point == 0) {
                diameter_circle_point = space_point * 2;
                Log.i(TAG, "onMeasure: diameter_circle_point：" + diameter_circle_point);
            }
            Log.i(TAG, "onMeasure: currViewHeight：" + currViewHeight);
            currViewHeight = diameter_circle_point + paddingTop + paddingBottom;
        } else {
            //如果(view的高度-paddingTop-paddingBottom)比根据宽度计算出来的直径小，则以(直径+paddingTop+paddingBottom)为view的高度
            if ((currViewHeight - paddingTop - paddingBottom) < diameter_circle_point) {
                currViewHeight = diameter_circle_point + paddingTop + paddingBottom;
            }
        }
        setMeasuredDimension(currViewWidth, currViewHeight);
        //小圆点的半径
        radius_circle_point = diameter_circle_point / 2;
        Log.i(TAG, "onMeasure: radius_circle_point：" + radius_circle_point);
        //小圆点的圆心纵坐标
        //如果高度减去上下padding，仍有大量空间给小圆点，所以会造成竖直方向小圆点没居中。所以我采取高度减去上下padding的距离/2-小圆点的半径算出居中的距离
        int valueVerDrawHeight = currViewHeight - paddingTop - paddingBottom;//绘制的区域(竖向)
        y_currStart = paddingTop + valueVerDrawHeight / 2;
        Log.i(TAG, "onMeasure: y_currStart：" + y_currStart);

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
            x_currStart = (int) (space_horDrawWidth_and_circlePoints + (i + 0.5) * diameter_circle_point + space_point * i);
            Log.i(TAG, "draw:x_currStart：" + x_currStart);
            canvas.drawCircle(x_currStart, y_currStart, radius_circle_point, paint_circlePoint);
        }
    }

    /**
     * 点亮指定小圆点
     */
    public void setCurrLightPosition(int position) {
        if (position == position_currpoint) {
            return;
        }
        position_currpoint = position;
        postInvalidate();
    }

}
