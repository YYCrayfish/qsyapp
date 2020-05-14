package com.manyu.videoshare.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义缩放、移动框
 */

public class MCustomZoomView extends View {

    private AreaHolder areaHolder;

    public MCustomZoomView(Context context) {
        this(context, null);
    }

    public MCustomZoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);//触摸获取焦点
        areaHolder = new AreaHolder(this);
        areaHolder.setIsDrawMapLine(true);
        areaHolder.setIsDrawCloseButton(false);
        areaHolder.setIsDrawBg(false);
        setRatioType(0);
        setEnabled(false); // 默认不可操作
    }

    /**
     * 获取控件宽、高
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /*自定框相关*/
        /*矩形边长*/
        float mRectLength = Math.min(w, h) / 2f;
        /*初始化矩形四边角坐标*/
        areaHolder.getContainerRect().set(
                (w - mRectLength) / 2, (h - mRectLength) / 2,
                (w + mRectLength) / 2, (h + mRectLength) / 2
        );
    }

    /**
     * 绘制框相关元素
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        areaHolder.onDraw(canvas);
    }

    public void setRatio(float ratio) {
        areaHolder.setRatio(ratio);
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        /*当前按下的X坐标*/
        float touchX = event.getX();
        /*当前按下的Y坐标*/
        float touchY = event.getY();
        switch (event.getAction()) {
            /*按下*/
            case MotionEvent.ACTION_DOWN:
                areaHolder.onDown(touchX, touchY);
                break;
            /*移动*/
            case MotionEvent.ACTION_MOVE:
                areaHolder.onMove(touchX, touchY);
                break;
            /*松开*/
            case MotionEvent.ACTION_UP:
                /*恢复静止*/
                areaHolder.onUp();

                break;
        }
        return true;
    }

    public void setRatioType(int mRatioType) {
        areaHolder.setRatioType(mRatioType);
    }

    public void setIsDrawMapLine(boolean mISDrawMapLine) {
        areaHolder.setIsDrawMapLine(mISDrawMapLine);
        invalidate();
    }

    public RectF getAreaRectF() {
        return areaHolder.getContainerRect();
    }
}