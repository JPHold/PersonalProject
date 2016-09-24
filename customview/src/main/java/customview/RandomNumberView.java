package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.com.hjp.mobilesafe.customview.R;

import java.util.Random;

/**
 * Created by HJP on 2016/1/25 0025.
 */
public class RandomNumberView extends View {

    private int numColor;
    private float numSize;
    private String numText;

    private Paint textPaint;
    private Rect textRect;
    private int textRect_width;
    private int textRect_height;

    public RandomNumberView(Context context) {
        this(context, null);
    }

    public RandomNumberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RandomNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RandomNumberView, defStyleAttr, 0);

        numColor = typedArray.getInteger(R.styleable.RandomNumberView_numColor, Color.RED);
        numSize = typedArray.getDimension(R.styleable.RandomNumberView_numSize, 20f);
        numText = typedArray.getString(R.styleable.RandomNumberView_numText);
        typedArray.recycle();

        textPaint = new Paint();
        textRect = new Rect();

        //添加效果:实现随机数字
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                numText = getRandomNumber();
//                postInvalidate();
                //重新计算布局(调用onMeasure())
                //requestLayout()在invalidate()前后都可以
                requestLayout();
                invalidate();

            }
        });
    }

    private String getRandomNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int number = random.nextInt(10);
            stringBuilder.append(number);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measureWidth;
        int measureHeight;
        //因为random的文本长度可能超过View的范围，所以将这个方法放在这,重新计算文本长度
        textPaint.getTextBounds(numText, 0, numText.length(), textRect);
        textRect_width = textRect.width();
        textRect_height = textRect.height();
        Log.i("rand", "文本方框的宽度" + textRect_width);
        Log.i("rand", "文本方框的长度" + textRect_height);

        //指定值或Match模式
        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidth = widthSize;
        }
        //wrap模式，如果不重新计算Measure值，绘制出来的范围跟Match模式一样
        else {
            measureWidth = getPaddingLeft() + textRect_width + getPaddingRight();
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
        } else {
            measureHeight = getPaddingTop() + textRect_height + getPaddingBottom();

        }
        Log.i("rand", "MeasureWidth" + measureWidth);
        Log.i("rand", "MeasureHeight" + measureHeight);


        setMeasuredDimension(measureWidth, measureHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        textPaint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), textPaint);

        textPaint.setColor(numColor);
        textPaint.setTextSize(numSize);
        //参数x为左上坐标，y为左下坐标
        int left_X = getWidth() / 2 - textRect_width / 2;
        int left_Y = getHeight() / 2 + textRect_height / 2;
        canvas.drawText(numText, left_X, left_Y, textPaint);
        Log.i("rand", "文本开始绘制的左上坐标和左下坐标" + left_X + "," + left_Y);
    }

}
