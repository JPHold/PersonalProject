package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.hjp.customview.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by HJP on 2016/1/27 0027.
 */
public class ColorCircleAnimationView extends View {

    private int firstArcColor;
    private int secondArcColor;
    private float arcWidth;
    private int runSpeed;

    private float centerX;
    private float centerY;
    private float radius;
    private float cutWidth;

    private Paint squareRimPaint;
    private Paint circlePaint;

    private int currentProgress = 0;
    private int currentAngle;
    private int temp;
    private boolean isFirstDrawViewRim;

    public ColorCircleAnimationView(Context context) {
        this(context, null);
    }

    public ColorCircleAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorCircleAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ColorCircleAnimationView, defStyleAttr, 0);
        firstArcColor = typedArray.getColor(R.styleable.ColorCircleAnimationView_firstColor, Color.BLUE);
        secondArcColor = typedArray.getColor(R.styleable.ColorCircleAnimationView_secondColor, Color.YELLOW);
        arcWidth = typedArray.getFloat(R.styleable.ColorCircleAnimationView_arcWidth, 42.2f);
        runSpeed = typedArray.getInt(R.styleable.ColorCircleAnimationView_runSpeed, 5);
        typedArray.recycle();

        //View边框和圆弧的内矩形边框的画笔
        squareRimPaint = new Paint();
        squareRimPaint.setStyle(Paint.Style.STROKE);
        squareRimPaint.setStrokeWidth(10);
        squareRimPaint.setAntiAlias(true);
        squareRimPaint.setColor(Color.BLACK);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                currentAngle = currentProgress * runSpeed;
                currentProgress++;
                if (currentAngle >= 360) {
                    currentAngle = 0;
                    currentProgress = 0;
                    //交换背景色和跑动颜色
                    temp = firstArcColor;
                    firstArcColor = secondArcColor;
                    secondArcColor = temp;
                }
                postInvalidate();
            }
        },2000,500);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //View边框(加上画笔笔尖宽度的)
        int viewRim_Width = getWidth();
        int viewRim_Height = getHeight();
        float squareRimPaint_StrokeWidth = squareRimPaint.getStrokeWidth();
       /* //去掉画笔笔尖宽度后,真正的内容绘制边框(还没确定正确不)
        float actualContent_Width = viewRim_Width - squareRimPaint_StrokeWidth;
        float actualContent_Height = viewRim_Height - squareRimPaint_StrokeWidth;*/
        //计算圆点
        centerX = viewRim_Width / 2;
        centerY = viewRim_Height / 2;
        //计算半径,如果不去掉(View的边框画笔笔尖的大小/2),不然画圆和圆弧时,会画在View边框上
        radius = centerX - arcWidth / 2 - squareRimPaint_StrokeWidth / 2;

        //计算圆弧的外矩形区域
        //需要减去的距离
        cutWidth = squareRimPaint_StrokeWidth / 2 + arcWidth / 2;

        //画圆和圆弧的画笔
        circlePaint = new Paint();
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(arcWidth);
        circlePaint.setAntiAlias(true);

        //第一次绘制View边框
        isFirstDrawViewRim = true;
    }

    public void onDraw(Canvas canvas) {

        //怎样局部刷新
        if (isFirstDrawViewRim) {
            //画View的边框
            canvas.drawRect(0, 0, getWidth(), getHeight(), squareRimPaint);
        }
        //画整体圆
        circlePaint.setColor(firstArcColor);
        canvas.drawCircle(centerX, centerY, radius, circlePaint);
        //将在这个矩形框内画圆弧
        RectF rectF = new RectF(cutWidth, cutWidth, getWidth() - cutWidth, getHeight() - cutWidth);
        canvas.drawRect(rectF, squareRimPaint);
        circlePaint.setColor(secondArcColor);
        //顺时针为正,
        canvas.drawArc(rectF, -90, currentAngle, false, circlePaint);
    }
}
