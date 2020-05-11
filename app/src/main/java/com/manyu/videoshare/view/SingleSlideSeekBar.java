package com.manyu.videoshare.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.manyu.videoshare.R;
import com.manyu.videoshare.util.ImageUtil;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import io.reactivex.annotations.Nullable;

public class SingleSlideSeekBar extends View {
    /**
     * 线条（进度条）的宽度
     */
    private int lineWidth;
    /**
     * 线条（进度条）的长度
     */
    private int lineLength = 400;
    /**
     * 游标 图片宽度
     */
    private int imageWidth;
    /**
     * 游标 图片高度
     */
    private int imageHeight;

    /**
     * 左边的游标是否在动
     */
    private boolean isLowerMoving;
    /**
     * 左边图标的图片
     */
    private Bitmap bitmapLow;

    /**
     * 左边图标所在X轴的位置
     */
    private int slideLowX;
    /**
     * 图标（游标） 高度
     */
    private int bitmapHeight;
    /**
     * 图标（游标） 宽度
     */
    private int bitmapWidth;
    /**
     * 加一些padding 大小酌情考虑 为了我们的自定义view可以显示完整
     */
    private int paddingLeft = 50;
    private int paddingRight = 50;
    private int paddingTop = 20;
    private int paddingBottom = 60;
    /**
     * 线（进度条） 开始的位置
     */
    private int lineStart = paddingLeft;
    /**
     * 线的Y轴位置
     */
    private int lineY;
    /**
     * 线（进度条）的结束位置
     */
    private int lineEnd = lineLength + paddingLeft;
    /**
     * 选择器的最大值
     */
    private int bigValue = 0;
    /**
     * 选择器的最小值
     */
    private int smallValue = 0;
    /**
     * 选择器的当前最小值
     */
    private float smallRange;


    private Paint bitmapPaint;
    private List<Bitmap> list;
    private int insetsW;
    private Bitmap bitmap;
    private Paint textPaint;
    private int textColor = Color.GRAY;
    private int textHeight;

    public SingleSlideSeekBar(Context context) {
        this(context, null);
    }

    public SingleSlideSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SingleSlideSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DoubleSlideSeekBar, defStyleAttr, 0);
        int size = typedArray.getIndexCount();
        for (int i = 0; i < size; i++) {
            int type = typedArray.getIndex(i);
            switch (type) {
                case R.styleable.DoubleSlideSeekBar_lineHeight:
                    lineWidth = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
                    break;
                case R.styleable.DoubleSlideSeekBar_imageLow:
                    bitmapLow = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(type, 0));
                    break;
                case R.styleable.DoubleSlideSeekBar_imageheight:
                    imageHeight = (int) typedArray.getDimension(type, dip2px(getContext(), 20));
                    break;
                case R.styleable.DoubleSlideSeekBar_imagewidth:
                    imageWidth = (int) typedArray.getDimension(type, dip2px(getContext(), 10));
                    break;
                case R.styleable.DoubleSlideSeekBar_bigValue:
                    bigValue = typedArray.getInteger(type, 0);
                    break;
                case R.styleable.DoubleSlideSeekBar_smallValue:
                    smallValue = typedArray.getInteger(type, 0);
                    break;
                default:
                    break;
            }
        }
        typedArray.recycle();
        init(context);
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void init(Context context) {
        /**游标的默认图*/
        list = new ArrayList<>();
        if (bitmapLow == null) {
            bitmapLow = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cursor);
        }
        /**游标图片的真实高度 之后通过缩放比例可以把图片设置成想要的大小*/
        bitmapHeight = bitmapLow.getHeight();
        bitmapWidth = bitmapLow.getWidth();
        // 设置想要的大小
        int newWidth = imageWidth;
        int newHeight = imageHeight;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / bitmapWidth;
        float scaleHeight = ((float) newHeight) / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Matrix matrixCursor = new Matrix();
        matrixCursor.postScale(1, scaleHeight);
        /**缩放图片*/
        bitmapLow = Bitmap.createBitmap(bitmapLow, 0, 0, bitmapWidth, bitmapHeight, matrix, true);
        /**重新获取游标图片的宽高*/
        bitmapHeight = bitmapLow.getHeight();
        bitmapWidth = bitmapLow.getWidth();
        /**初始化两个游标的位置*/
        slideLowX = lineStart;
        smallRange = smallValue;

        //画游标

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(ImageUtil.dp2px(context, 12));
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMyMeasureWidth(widthMeasureSpec);
        int height = getMyMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int getMyMeasureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            // matchparent 或者 固定大小 view最小应为 paddingBottom + paddingTop + bitmapHeight + 10 否则显示不全
            size = Math.max(size, paddingBottom + paddingTop + bitmapHeight + 10);
        } else {
            //wrap content
            int height = paddingBottom + paddingTop + bitmapHeight + 10;
            size = Math.min(size, height);
        }
        return size;
    }

    private int getMyMeasureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {

            size = Math.max(size, paddingLeft + paddingRight + bitmapWidth * 2);

        } else {
            //wrap content
            int width = paddingLeft + paddingRight + bitmapWidth * 2;
            size = Math.min(size, width);
        }
        // match parent 或者 固定大小 此时可以获取线（进度条）的长度
        lineLength = size - paddingLeft - paddingRight - bitmapWidth;
        //线（进度条）的结束位置
        lineEnd = lineLength + paddingLeft + bitmapWidth / 2;
        //线（进度条）的开始位置
        lineStart = paddingLeft + bitmapWidth / 2;
        //初始化 游标位置
        insetsW = (lineEnd - lineStart + 10) / 8;
        slideLowX = lineStart + bitmapWidth;
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Y轴 坐标
        lineY = getHeight() - paddingBottom - bitmapHeight / 2;
        textHeight = lineY - bitmapHeight / 2 - 10;
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Bitmap bitmap = list.get(i);
                canvas.drawBitmap(ImageUtil.centerSquareScaleBitmap(bitmap, insetsW), lineStart + (insetsW * i), lineY - (ImageUtil.centerSquareScaleBitmap(bitmap, insetsW).getHeight() / 2), bitmapPaint);
            }
        }

        canvas.drawBitmap(bitmapLow, slideLowX, lineY - bitmapHeight / 2, bitmapPaint);
        canvas.drawText("总长" + stringForTime((long) bigValue), lineEnd - textPaint.measureText("总长" + stringForTime((long) bigValue)) + 20, lineY + lineWidth + 10, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //事件机制
        super.onTouchEvent(event);
        float nowX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下 在左边游标上
                boolean lowSlide = Math.abs(nowX - slideLowX) < bitmapWidth * 2;
                if (lowSlide) {
                    isLowerMoving = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //左边游标是运动状态
                if (isLowerMoving) {
                    //当前 X坐标在线上 且在右边游标的左边
                    if (nowX <= lineEnd - bitmapWidth && nowX >= lineStart + bitmapWidth) {
                        slideLowX = (int) nowX;
                        if (slideLowX < lineStart + bitmapWidth) {
                            slideLowX = lineStart + bitmapWidth;
                        }
                        invalidate();
                    }
                }
                break;
            //手指抬起
            case MotionEvent.ACTION_UP:
                isLowerMoving = false;
                //更新进度
                updateRange();
                break;
            default:
                break;
        }
        return true;
    }

    private void updateRange() {
        //当前 左边游标数值
        smallRange = computRange(slideLowX);
        //接口 实现值的传递
        if (onRangeListener != null) {
            onRangeListener.onRange(smallRange);
        }
    }

    private Bitmap changeBitmapSize(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Log.e("width", "width:" + width);
        Log.e("height", "height:" + height);
        //设置想要的大小
        int newWidth = insetsW;
        int newHeight = insetsW;

        //计算压缩的比率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        //获取想要缩放的matrix
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        //获取新的bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        bitmap.getWidth();
        bitmap.getHeight();
        Log.e("newWidth", "newWidth" + bitmap.getWidth());
        Log.e("newHeight", "newHeight" + bitmap.getHeight());
        return bitmap;
    }


    /**
     * 获取当前值
     */
    private float computRange(float range) {
        return (range - lineStart) * (bigValue - smallValue) / lineLength + smallValue;
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public static String stringForTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        return new Formatter().format("%02d:%02d:%02d", hour, minute, second).toString();
    }

    public void setBigValue(int bigValue) {
        this.bigValue = bigValue;
        invalidate();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        list.add(bitmap);
        invalidate();
    }

    public long getBigValue() {
        return bigValue;
    }


    /**
     * 写个接口 用来传递最大最小值
     */
    public interface onRangeListener {
        void onRange(float low);

    }

    private onRangeListener onRangeListener;

    public void setOnRangeListener(onRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }
}
