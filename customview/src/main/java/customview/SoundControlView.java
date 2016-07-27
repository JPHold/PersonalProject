package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.hjp.customview.R;

/**
 * Created by HJP on 2016/1/28 0028.
 */
public class SoundControlView extends View {
    private final static String TAG = "SoundControlView";
    private int dotBgColor;
    private int dotCloudColor;
    private Bitmap centerPhoto;
    private int arcWidth;

    private Paint dotPaint;
    private Paint centerPhotoPaint;
    private Paint dotVolumePaint;

    private int viewRim_Width;
    private int viewRim_height;
    private float centerX;
    private float centerY;
    private float arcOfCircle_radius;

    private RectF dotRect;
    private float gapAngle;
    private float dotStartAngle_multiple;

    private float squareOut_Circleofradius;
    private float circleInner_SquareLength;

    private RectF centerPhoto_Rect;
    /**
     * 设置了音量时，标识音量点
     */
    private boolean isCloudDraw = false;
    private int currentVolume=0;
    private float currentDot_EndAngle;

    public SoundControlView(Context context) {
        this(context, null);
    }

    public SoundControlView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SoundControlView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SoundControlView, defStyleAttr, 0);
        arcWidth = typedArray.getInteger(R.styleable.ColorCircleAnimationView_arcWidth, 15);
        dotBgColor = typedArray.getColor(R.styleable.SoundControlView_dotBgColor, Color.BLACK);
        dotCloudColor = typedArray.getColor(R.styleable.SoundControlView_dotControlColor, Color.WHITE);
        centerPhoto = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.SoundControlView_centerPhoto
                , R.mipmap.kunfupanda));
        typedArray.recycle();

        dotPaint = new Paint();
        dotPaint.setStyle(Paint.Style.STROKE);
        dotPaint.setStrokeWidth(arcWidth);
        dotPaint.setColor(dotBgColor);
        dotPaint.setAntiAlias(true);
        dotPaint.setStrokeCap(Paint.Cap.ROUND);

        centerPhotoPaint = new Paint();
        centerPhotoPaint.setStyle(Paint.Style.STROKE);
        centerPhotoPaint.setAntiAlias(true);

    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewRim_Width = MeasureSpec.getSize(widthMeasureSpec);
        viewRim_height = MeasureSpec.getSize(heightMeasureSpec);
        centerX = viewRim_Width / 2;
        centerY = viewRim_height / 2;

        //计算椭圆圆弧所在圆的半径(只减去了画笔笔尖宽度的一半)
        arcOfCircle_radius = centerX - arcWidth / 2;
        measureDot();
        measureCircleInnerPhoto();
    }

    private void measureCircleInnerPhoto() {
        //椭圆圆弧所在圆去掉画笔宽度之后的圆,计算其半径
        squareOut_Circleofradius = arcOfCircle_radius - arcWidth / 2;
        //根据这个圆,计算其内正方形的边长:
        circleInner_SquareLength = (float) (Math.sqrt(2) * squareOut_Circleofradius);
        //其正方形边框
        centerPhoto_Rect = new RectF(centerX - circleInner_SquareLength / 2, centerY - circleInner_SquareLength / 2,
                centerX + circleInner_SquareLength / 2, centerY + circleInner_SquareLength / 2);
    }

    private void measureDot() {
        dotRect = new RectF(arcWidth / 2, arcWidth / 2, viewRim_Width - arcWidth / 2, viewRim_height - arcWidth / 2);
        //小椭圆点的角度是间隙的2倍
        //180度可以分为29个间隙角度,总共10个小椭圆点,计算间隙角度
        gapAngle = 180 / 29;
        //计算所有小椭圆点的开始绘制角度之间相差的数
        //通过规律:
        // (0,12.4)
        // (37.2,49.6)
        // (55.8)
        //发现这个数是间隙角度的三倍
        dotStartAngle_multiple = 3 * gapAngle;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        //设置了音量时
        if (isCloudDraw && currentVolume >0) {
            dotPaint.setColor(dotCloudColor);
        }else{
            dotPaint.setColor(dotBgColor);
        }
        //画第一个音量点
        canvas.drawArc(dotRect, -180, 2 * gapAngle, false, dotPaint);


        //画左边椭圆圆弧
        for (int i = 2; i <= 5; i++) {
            //在音量值以内，标识音量点
            if (i <= currentVolume) {
                dotPaint.setColor(dotCloudColor);
            } else {
                dotPaint.setColor(dotBgColor);
            }
            canvas.drawArc(dotRect, -180 + (i - 1) * (2 * gapAngle) + (i - 1) * gapAngle, 2 * gapAngle, false, dotPaint);
        }

        //计算真实序数
        int j = 0;
        if (currentVolume > 5) {
            j = currentVolume - 5;
        }

        //画右边椭圆圆弧
        for (int i = 1; i <= 4; i++) {
            //在真实序数以内，标识音量点
            if (i <= j) {
                dotPaint.setColor(dotCloudColor);
            } else {
                dotPaint.setColor(dotBgColor);
            }
            canvas.drawArc(dotRect, 0 - ((5 - i) * (2 * gapAngle) + (5 - i) * gapAngle), -2 * gapAngle, false, dotPaint);
        }

        //设置了音量，但还不是最大音量
        if ( currentVolume != 10) {
            dotPaint.setColor(dotBgColor);
        }
        //画第10个音量点
        canvas.drawArc(dotRect, 0, -2 * gapAngle, false, dotPaint);

        canvas.drawBitmap(centerPhoto, null, centerPhoto_Rect, centerPhotoPaint);

    }

    public void setCurrentVolume(int volume) {
        currentVolume = volume;
        isCloudDraw = true;
        postInvalidate();
    }

}
