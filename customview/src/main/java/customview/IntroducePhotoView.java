package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.hjp.customview.R;

/**
 * Created by HJP on 2016/1/26 0026.
 */
public class IntroducePhotoView extends View {

    private final int PHOTO_FILLXY = 0;
    private final int PHOTO_CENTER = 1;

    private int mintroduceColor;
    private float mintroduceSize;
    private String mintroduceText;
    private Bitmap mphoto;
    private int mphotoScaleType;

    private Paint mviewRimPaint;
    private TextPaint mintroduceTextPaint;
    private Paint photoPaint;
    private Rect viewRimRect;
    private Rect photoRimRect;
    private Rect introduceTextRect;

    private int introduceTextLength;
    private int singleWordNum;
    private int textLineNum;

    public IntroducePhotoView(Context context) {
        this(context, null);
    }

    public IntroducePhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IntroducePhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IntroducePhotoView, defStyleAttr, 0);

        mintroduceColor = typedArray.getInteger(R.styleable.IntroducePhotoView_introduceColor, Color.YELLOW);
        mintroduceSize = typedArray.getDimension(R.styleable.IntroducePhotoView_introduceSize, 20.0f);
        mintroduceText = typedArray.getString(R.styleable.IntroducePhotoView_introduceText);
        mphoto = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(R.styleable.IntroducePhotoView_photo, R.mipmap.ic_launcher));
        mphotoScaleType = typedArray.getInteger(R.styleable.IntroducePhotoView_photoScaleType, 0);

        //边框画笔
        mviewRimPaint = new Paint();
        //介绍文本画笔
        mintroduceTextPaint = new TextPaint();
        mintroduceTextPaint.setStyle(Paint.Style.STROKE);
        mintroduceTextPaint.setTextSize(mintroduceSize);
        //图片画笔
        photoPaint = new Paint();
        //View边框
        viewRimRect = new Rect();
        //图片边宽
        photoRimRect = new Rect();
        //介绍文本边框
        introduceTextRect = new Rect();

        typedArray.recycle();
    }

    // 布局设置了visibility="invisible",onMeasure()仍会被调用，但draw()没有被调用绘制,z
    //重写这个方法必须调用setMeasuredDimension(,);
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);

        int measureHeightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        //判断布局设置的Width和Height的值是什么类型
        //是EXACTLY(指定值或Match),则值不变
        if (measureWidthMode == MeasureSpec.EXACTLY) {
        } else {
            measureWidth = getPaddingLeft() + mphoto.getWidth() + getPaddingRight();
        }

        //计算文本的范围总大小
        mintroduceTextPaint.getTextBounds(mintroduceText, 0, mintroduceText.length(), introduceTextRect);
        //计算一个字的大小
        introduceTextLength = mintroduceText.length();
        float eachWordWidth = introduceTextRect.width() / introduceTextLength;
        //计算在图片宽度限制下,最大能显示的字数
        singleWordNum = (int) (mphoto.getWidth() / eachWordWidth);
        //介绍文本需要几行显示,初始为一行
        textLineNum = 1;
        if (measureHeightMode == MeasureSpec.EXACTLY) {
        } else {
            //文本长度在最大显示字数范围内则只需一行
            if (introduceTextLength <= singleWordNum) {
            } else {
                while ((introduceTextLength - textLineNum * singleWordNum) > 0) {
                    textLineNum++;
                }
                //topPad+图片高度+介绍文本高度+bottomPad
                measureHeight = getPaddingTop() + mphoto.getHeight() + textLineNum * introduceTextRect.height() + getPaddingBottom();
            }
        }

        Log.i("text", "文本长度" + introduceTextLength);
        Log.i("text", "每一个字的宽度" + eachWordWidth);
        Log.i("text", "最大能显示的字数" + singleWordNum);
        Log.i("text", "文本需要显示的行数" + textLineNum);

        //View边框大小设置
        viewRimRect.top = 0;
        viewRimRect.left = 0;
        viewRimRect.right = measureWidth;
        viewRimRect.bottom = measureHeight;

        setMeasuredDimension(measureWidth, measureHeight);
    }

    protected void onDraw(Canvas canvas) {
        //画View边框
        mviewRimPaint.setStyle(Paint.Style.STROKE);
        mviewRimPaint.setStrokeWidth(4.3f);
        mviewRimPaint.setColor(Color.BLUE);
        canvas.drawRect(viewRimRect, mviewRimPaint);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        //画padding框
        canvas.drawRect(paddingLeft, paddingTop, viewRimRect.width() - paddingRight, viewRimRect.height() - paddingBottom, mviewRimPaint);

        int introduceTextWidth = introduceTextRect.width();
        int introduceTextHeight = introduceTextRect.height();

        mintroduceTextPaint.setStyle(Paint.Style.STROKE);
        mintroduceTextPaint.setTextSize(mintroduceSize);
        mintroduceTextPaint.setColor(mintroduceColor);
        //绘制图片,如是fillXY,则图片铺满整个View,如是center,则图片居中在padding边界内
        switch (mphotoScaleType) {
            case PHOTO_FILLXY:
                photoPaint.setStyle(Paint.Style.FILL);
                canvas.drawBitmap(mphoto, null, viewRimRect, null);
                break;
            case PHOTO_CENTER:
                //注释的代码,当时的想法是:在padding框内,计算图片的宽度如果小于padding框宽度,则以图片宽度绘制图片,反之则以padding框宽度绘制图片
                /*int photoLeft = viewRimRect.width() / 2 - mphoto.getWidth() / 2;
                int photoTop = viewRimRect.height() / 2 - mphoto.getHeight() / 2;
                int photoRight;
                int photoBottom;
                //如果View的边界宽度的一半减去图片宽度的一半的结果小于paddingLeft距离,则paddingLeft当作photoLeft。photoTop也是如此
                if (photoLeft < paddingLeft) {
                    photoLeft = paddingLeft;
                    photoRight = viewRimRect.width() - paddingRight;
                } else {
                    //如果结果大于paddingLeft距离,photoLeft不变。photoRight则相应做改变
                    // 保证图片在padding框内是居中显示(即图片的上下左右距离padding内容框的边界是一样的)
                    photoRight = viewRimRect.width() - paddingRight - (photoLeft - paddingLeft);
                }
                if (photoTop < paddingTop) {
                    photoTop = paddingTop;
                    photoBottom = viewRimRect.height() - paddingBottom - introduceTextRect.height();
                } else {
                    photoBottom = viewRimRect.height() - paddingBottom - (photoTop - paddingTop);
                }
                photoRimRect.top = photoTop;
                photoRimRect.left = photoLeft;
                photoRimRect.right = photoRight;
                photoRimRect.bottom = photoBottom;
                canvas.drawBitmap(mphoto, null, photoRimRect, null);*/

                photoRimRect.top = paddingTop;
                photoRimRect.left = paddingLeft;
                photoRimRect.right = viewRimRect.width() - paddingRight;
                photoRimRect.bottom = viewRimRect.height() - paddingBottom - textLineNum * introduceTextHeight;
                canvas.drawBitmap(mphoto, null, photoRimRect, null);
                break;
        }

        //绘制文本
        int currentLineNum = 0;
        int introduceTextSingleWordNum;
        while ((--textLineNum) != -1) {

            //会出现介绍文本的字数比最大显示的字数还小的错误,加此判断
            if (introduceTextLength < singleWordNum) {
                introduceTextSingleWordNum = introduceTextLength;
            } else {
                introduceTextSingleWordNum = singleWordNum;
            }
            //绘制第一行
            if (currentLineNum == 0) {
                canvas.drawText(mintroduceText, 0, introduceTextSingleWordNum, paddingLeft,
                        viewRimRect.height() - paddingBottom - textLineNum * introduceTextHeight, mintroduceTextPaint);
            } else {
                //第一行以上的文本绘制
                canvas.drawText(mintroduceText, (currentLineNum * introduceTextSingleWordNum) + 1, 2 * (currentLineNum * introduceTextSingleWordNum) + 1
                        , paddingLeft, viewRimRect.height() - paddingBottom - textLineNum * introduceTextHeight, mintroduceTextPaint);

                currentLineNum++;
            }
        }
    }
}
