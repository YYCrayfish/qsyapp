package com.manyu.videoshare.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.OverScroller;


import com.manyu.videoshare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lumingmin on 16/6/7.
 */
public class SelectView extends View {


    private Paint mPaint;//背景刻度画笔
    private Paint mPaintRed;//中间刻度画笔

    private Paint mPaintText;//文字画笔
    private float mTextSize = 0;
    private float mPointX = 0f;
    private float mPointXoff = 0f;
    private int mPadding = dip2px(1);
    private OverScroller scroller;//控制滑动
    private VelocityTracker velocityTracker;

    private float mUnit = 20;
    private int mMaxValue = 200;
    private int mMinValue = 150;
    private int mMiddleValue = (mMaxValue + mMinValue) / 2;
    private int mUnitLongLine = 5;
    private boolean isCanvasLine = true;

    private int bgColor = Color.rgb(228, 228, 228);
    private int textColor = Color.rgb(151, 151, 151);
    private int textSelectColor = Color.rgb(151, 151, 151);


    private int minvelocity;

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new OverScroller(context);
        minvelocity = ViewConfiguration.get(getContext())
                .getScaledMinimumFlingVelocity();
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SelectView);
        if (typedArray != null) {
            isCanvasLine = typedArray.getBoolean(R.styleable.SelectView_isCanvasLine, true);
            mTextSize = typedArray.getDimension(R.styleable.SelectView_textSize, dip2px(12));
            bgColor = typedArray.getColor(R.styleable.SelectView_bgColor, Color.rgb(228, 228, 228));
            textColor = typedArray.getColor(R.styleable.SelectView_textColor, Color.rgb(151, 151, 151));
            mUnit = typedArray.getDimension(R.styleable.SelectView_unitSize, 13.f);
            mUnitLongLine = typedArray.getInteger(R.styleable.SelectView_unitLongLine, 5);
            textSelectColor = typedArray.getColor(R.styleable.SelectView_textSelectColor, Color.rgb(151, 151, 151));
            typedArray.recycle();
        }
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(bgColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dip2px(2));

        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(textColor);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setStyle(Paint.Style.FILL);

        mPaintRed = new Paint();
        mPaintRed.setAntiAlias(true);
        mPaintRed.setColor(Color.BLUE);
        mPaintRed.setStrokeWidth(dip2px(4));
        mPaintRed.setStyle(Paint.Style.STROKE);

    }


    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        canvasLineAndText(canvas);
        canvasRedLine(canvas);
    }

    private void canvasRedLine(Canvas canvas) {//中间红色刻度
        Path pathRed = new Path();
        pathRed.moveTo(getMeasuredWidth() / 2, 0);
        pathRed.lineTo(getMeasuredWidth() / 2, getMeasuredHeight());
        canvas.drawPath(pathRed, mPaintRed);
    }


    private void canvasLineAndText(Canvas canvas) {

        int current = (int) (Math.rint(mPointX / mUnit));
        float startY = 0;
        for (int i = mMinValue; i <= mMaxValue; i++) {
            int space = mMiddleValue - i;
            float x = getMeasuredWidth() / 2 - space * mUnit + mPointX;
            Log.e("aaaaaaa", x + "");
            if (x > mPadding && x < getMeasuredWidth() - mPadding) {//判断x轴在视图范围内

                float y = getMeasuredHeight() / 3;
                if ((i - 1) % mUnitLongLine == 0) {//画长刻度线 默认每5格一个
                    mPaintText.setColor(textColor);
                    if (Math.abs(mMiddleValue - current - i) < (mUnitLongLine / 2 + 1)) {//计算绝对值在某一区间内文字显示高亮
                        mPaintText.setColor(textSelectColor);
                    }
                    y = getMeasuredHeight() - 10;
                    startY = 10;
                } else {
                    startY = getMeasuredHeight() / 3;
                    y = startY + y;
                }
                if (isCanvasLine) {//画短刻度线
                    canvas.drawLine(x, startY, x, y, mPaint);
                }
            }
        }
    }


    private boolean isActionUp = false;
    private float mLastX;
//    private boolean startAnim = true;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float xPosition = event.getX();

        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        velocityTracker.addMovement(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mScrolleAnim != null)
                    clearAnimation();
                isActionUp = false;
                scroller.forceFinished(true);

                break;
            case MotionEvent.ACTION_MOVE:
                isActionUp = false;
                mPointXoff = xPosition - mLastX;//计算滑动距离
                mPointX = mPointX + mPointXoff;


                postInvalidate();

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isActionUp = true;
                countVelocityTracker(event);//控制快速滑动
                startAnim();
//                startAnim = true;
                return false;
            default:
                break;
        }

        mLastX = xPosition;
        return true;
    }


    @Override
    public void computeScroll() {

        if (scroller.computeScrollOffset()) {
            float mPointXoff = (scroller.getFinalX() - scroller.getCurrX());
            mPointX = mPointX + mPointXoff * functionSpeed();
            float absmPointX = Math.abs(mPointX);
            float mmPointX = (mMaxValue - mMinValue) * mUnit / 2;
            if (absmPointX < mmPointX) {//在视图范围内
                startAnim();
            }
        }
        super.computeScroll();
    }

    /**
     * 控制滑动速度
     *
     * @return
     */
    private float functionSpeed() {
        return 1f;
    }

    private void countVelocityTracker(MotionEvent event) {
        velocityTracker.computeCurrentVelocity(800, 800);
        float xVelocity = velocityTracker.getXVelocity();
        if (Math.abs(xVelocity) > minvelocity) {
            scroller.fling(0, 0, (int) xVelocity, 0, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, 0, 0);
        }
    }

    public int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public float getFontlength(Paint paint, String str) {
        return paint.measureText(str);
    }

    public float getFontHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }


    private ScrolleAnim mScrolleAnim;

    private class ScrolleAnim extends Animation {

        float fromX = 0f;
        float desX = 0f;

        public ScrolleAnim(float d, float f) {
            fromX = f;
            desX = d;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            mPointX = fromX + (desX - fromX) * interpolatedTime;//计算动画每贞滑动的距离
            invalidate();
        }
    }


    private void startAnim() {
        float absmPointX = Math.abs(mPointX);
        float mmPointX = (mMaxValue - mMinValue) * mUnit / 2;
        if (absmPointX > mmPointX) {//超出视图范围
            if (mPointX > 0) {//最左
                moveToX(mMiddleValue - mMinValue, 300);
            } else {//最右
                moveToX(mMiddleValue - mMaxValue, 300);
            }
        } else {
            int space = (int) (Math.rint(mPointX / mUnit));//四舍五入计算出往左还是往右滑动
            moveToX(space, 200);
        }
    }

    private void moveToX(int distance, int time) {
        if (mScrolleAnim != null)
            clearAnimation();
        mScrolleAnim = new ScrolleAnim((distance * mUnit), mPointX);
        mScrolleAnim.setDuration(time);
        startAnimation(mScrolleAnim);
        if (mOnSelect != null)
            if (mMiddleValue - (int) (Math.rint(mPointX / mUnit)) - 1 < 0) {
                mOnSelect.onSelectItem(listValue.get(0));
            } else if (mMiddleValue - (int) (Math.rint(mPointX / mUnit)) - 1 > listValue.size() - 1) {
                mOnSelect.onSelectItem(listValue.get(listValue.size() - 1));
            } else {
                mOnSelect.onSelectItem(listValue.get(mMiddleValue - (int) (Math.rint(mPointX / mUnit)) - 1));
            }
    }

    private List<String> listValue = new ArrayList<>();
    private onSelect mOnSelect = null;

    public void showValue(List<String> list, onSelect monSelect) {
        mOnSelect = monSelect;
        listValue.clear();
        listValue.addAll(list);
        mMaxValue = listValue.size();
        mMinValue = 1;
        mMiddleValue = (mMaxValue + mMinValue) / 2;
        mPointX = -((mMaxValue - mMinValue) * mUnit / 2);
    }

    public interface onSelect {
        void onSelectItem(String value);
    }
}
