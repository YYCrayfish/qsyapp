package com.manyu.videoshare.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.manyu.videoshare.util.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义缩放、移动框
 */

public class ScreenShotZoomView extends View {

    /*屏幕像素密度*/
    private float mDensity = getContext().getResources().getDisplayMetrics().density;
    /*自定框相关*/
    /*矩形四个边角坐标*/
    private ArrayList<AreaHolder> areas = new ArrayList<>();
    /* 创建框的阈值 */
    private float createAreaThreshold = 10f * mDensity;
    /* 当前正在操作的框 */
    private AreaHolder currentArea;
    /* 第一次按下的点 */
    private PointF startPressPoint = new PointF();

    public ScreenShotZoomView(Context context) {
        this(context, null);
    }

    public ScreenShotZoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);//触摸获取焦点
        setEnabled(false); // 默认不可操作
    }

    /**
     * 绘制框相关元素
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {
        for (AreaHolder area : areas) {
            area.onDraw(canvas);
        }
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
                for (int i = areas.size() - 1; i >= 0; i--) {
                    AreaHolder area = areas.get(i);
                    if (area.toolPointIsInRect(touchX, touchY, ImageUtil.dp2px(getContext(), 5))) {
                        currentArea = area;
                        currentArea.onDown(touchX, touchY);
                        break;
                    }
                }
                startPressPoint.set(touchX, touchY);
                break;
            /*移动*/
            case MotionEvent.ACTION_MOVE:
                if (currentArea != null) {
                    currentArea.onMove(touchX, touchY);
                } else if (Math.abs(touchX - startPressPoint.x) > createAreaThreshold || Math.abs(touchY - startPressPoint.y) > createAreaThreshold) {
                    currentArea = new AreaHolder(this);
                    currentArea.setIsDrawCloseButton(true);
                    currentArea.setIsDrawMapLine(false);
                    currentArea.setIsDrawBg(true);
                    currentArea.initWithCornerDragMode(startPressPoint.x, startPressPoint.y);
                    currentArea.setOnClosingListener(new AreaHolder.Function() {
                        @Override
                        public void invoke(AreaHolder area) {
                            areas.remove(area);
                            invalidate();
                        }
                    });
                    areas.add(currentArea);
                }
                break;
            /*松开*/
            case MotionEvent.ACTION_UP:
                if (currentArea != null) {
                    currentArea.onUp();
                    currentArea = null;
                }
                break;
            default:
                break;
        }
        return true;
    }

    public List<RectF> getAreaRectF() {
        ArrayList<RectF> result = new ArrayList<>();
        for (AreaHolder area : areas) {
            result.add(area.getContainerRect());
        }
        return result;
    }

}