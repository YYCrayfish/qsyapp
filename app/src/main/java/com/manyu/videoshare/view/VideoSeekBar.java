package com.manyu.videoshare.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.manyu.videoshare.R;
import com.manyu.videoshare.util.ImageUtil;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

import static com.manyu.videoshare.util.ToolUtils.dip2px;
import static com.manyu.videoshare.util.ToolUtils.sp2px;

public class VideoSeekBar extends View {

    /**
     * 光标 的图片
     */
    private Bitmap bitmapCursor;
    /**
     * 图标（游标） 高度
     */
    private int bitmapHeight;
    /**
     * 图标（游标） 宽度
     */
    /**
     * 游标 图片宽度
     */
    private int imageWidth;
    /**
     * 游标 图片高度
     */
    private int imageHeight;
    private int bitmapWidth;
    private Paint bitmapPaint;
    private int cursorX = 150;
    private float imgX;
    private int insetsW;//图片宽 高 一样
    private Bitmap bitmap;
    private Paint textPaint;
    private int textColor = Color.GRAY;
    private int paddingLeft = 50;
    private int paddingTop = 20;
    private int paddingBottom = 60;
    private boolean isMoving;
    private float distance = 0;//点击位置与第一张图片距离
    private float x = 0;//光标与第一张图片距离
    private ObjectAnimator animator;
    private float progress = 0;
    private int bigValue = 0;//总时长
    private long currentTime = 0;//当前时长
    private int layoutW;//view总宽度

    private List<Bitmap> list = new ArrayList<>();

    public VideoSeekBar(Context context) {
        this(context, null);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.VideoSeekBar);
            imageHeight = (int) typedArray.getDimension(R.styleable.VideoSeekBar_imageheight, dip2px(getContext(), 20));
            imageWidth = (int) typedArray.getDimension(R.styleable.VideoSeekBar_imagewidth, dip2px(getContext(), 10));
            typedArray.recycle();
        }

        if (bitmapCursor == null) {
            bitmapCursor = BitmapFactory.decodeResource(getResources(), R.drawable.ic_cursor);
        }

        animator = ObjectAnimator.ofFloat(this, "progress", 0f, 1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new LinearInterpolator());

        /**游标图片的真实高度 之后通过缩放比例可以把图片设置成想要的大小*/
        bitmapHeight = bitmapCursor.getHeight();
        bitmapWidth = bitmapCursor.getWidth();
        int newWidth = imageWidth;
        int newHeight = imageHeight;
        float scaleWidth = ((float) newWidth) / bitmapWidth;
        float scaleHeight = ((float) newHeight) / bitmapHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        insetsW = imageHeight - 10;
        imgX = cursorX + imageWidth / 2;
        bitmapCursor = Bitmap.createBitmap(bitmapCursor, 0, 0, bitmapWidth, bitmapHeight, matrix, true);

        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(sp2px(context, 12));
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMyMeasureWidth(widthMeasureSpec);
        int height = getMyMeasureHeight(heightMeasureSpec);
        layoutW = width;
        setMeasuredDimension(width, height);
    }

    private int getMyMeasureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            // matchparent 或者 固定大小 view最小应为 paddingBottom + paddingTop + bitmapHeight + 10 否则显示不全
            size = Math.max(size, paddingBottom + paddingTop + bitmapHeight + 40);
        } else {
            //wrap content
            int height = paddingBottom + paddingTop + bitmapHeight + 40;
            size = Math.min(size, height);
        }
        return size;
    }

    private int getMyMeasureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {

            size = Math.max(size, paddingLeft + bitmapWidth * 2);

        } else {
            //wrap content
            int width = paddingLeft + bitmapWidth * 2;
            size = Math.min(size, width);
        }
        return size;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (animator.isRunning()) {
            imgX = (int) (cursorX - x - (insetsW * 8 - x) * progress);
        }
        // Y轴 坐标
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Bitmap bitmap = list.get(i);
                canvas.drawBitmap(ImageUtil.centerSquareScaleBitmap(bitmap, insetsW), imgX + i * insetsW, (bitmapCursor.getHeight() - insetsW) / 2 + paddingTop, bitmapPaint);
            }
        }
        if (bitmapCursor != null) {
            canvas.drawBitmap(bitmapCursor, cursorX, paddingTop, bitmapPaint);
        }
        canvas.drawText(stringForTime((long) bigValue), layoutW - textPaint.measureText(stringForTime((long) bigValue)) - 20, paddingTop + imageHeight + dip2px(14), textPaint);
        if ((bigValue - currentTime) * progress + currentTime <= 0) {
            canvas.drawText(stringForTime(0), cursorX - textPaint.measureText(stringForTime((long) bigValue)) / 2, paddingTop + imageHeight + dip2px(14), textPaint);
        } else {
            canvas.drawText(stringForTime((long) ((bigValue - currentTime) * progress + currentTime)), cursorX - textPaint.measureText(stringForTime((long) bigValue)) / 2, paddingTop + imageHeight  + dip2px(14), textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //事件机制
        super.onTouchEvent(event);
        float nowX = event.getX();
        float nowY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下 在线（进度条）范围上
                if ((nowX <= imgX + insetsW * 8 && nowX >= imgX) && (((Math.abs(nowY) >= (bitmapCursor.getHeight() - insetsW) / 2 + paddingTop) && (Math.abs(nowY) <= (bitmapCursor.getHeight() - insetsW) / 2 + paddingTop + insetsW)))) {
                    animator.cancel();
                    onRangeListener.onVideoPause();
                    distance = nowX - imgX;
                    isMoving = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMoving) {
                    imgX = (int) (nowX - distance);
                    if (imgX > cursorX + imageWidth / 2) {
                        imgX = cursorX + imageWidth / 2;
                    }
                    if (imgX + insetsW * 8 < cursorX + imageWidth / 2) {
                        imgX = cursorX + imageWidth / 2 - insetsW * 8;
                    }
                    x = cursorX - imgX;
                    progress = x / (insetsW * 8);
                    currentTime = (long) (progress * bigValue);
                    invalidate();
                }
                break;
            //手指抬起
            case MotionEvent.ACTION_UP:
                if (isMoving) {

                    animator.cancel();
                    animator.setDuration(bigValue - currentTime);
                    onRangeListener.onVideoStart(currentTime);
                    animator.start();
                    isMoving = false;
                }
                break;
            default:
                break;
        }
        return true;
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

    public void setBigValue(int bigValue) {
        this.bigValue = bigValue;
        animator.setDuration(bigValue);
        invalidate();
    }

    // 创建 getter 方法
    public float getProgress() {
        return progress;
    }

    // 创建 setter 方法
    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
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
//        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        return new Formatter().format("%02d:%02d:%02d", hour, minute, second).toString();
    }

    /**
     * 写个接口 用来传递最大最小值
     */
    public interface onRangeListener {
        void onVideoPause();

        void onVideoStart(long duration);
    }

    private onRangeListener onRangeListener;

    public void setOnRangeListener(onRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }

    public void pause() {
        if (animator != null) {
            animator.pause();
        }
    }

    public void resume() {
        if (animator != null) {
            if (animator.isPaused()) {
                animator.resume();
            } else {
                reset();
            }
        }
    }

    public void reset() {
        if (animator != null) {
            animator.cancel();
            imgX = cursorX + imageWidth / 2;
            x = 0;
            currentTime = 0;
            progress = 0;
            animator.setDuration(bigValue);
            animator.start();
            invalidate();
        }
    }
}
