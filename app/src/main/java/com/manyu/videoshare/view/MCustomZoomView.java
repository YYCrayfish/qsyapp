package com.manyu.videoshare.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.manyu.videoshare.R;

/**
 * 自定义缩放、移动框
 */

public class MCustomZoomView extends View {

    /*屏幕像素密度*/private float mDensity = getContext().getResources().getDisplayMetrics().density;

    // 宽高比
    private float ratio = 0;

    /*上下文*/private Context mContext;

    /*矩形四个边角坐标*/private RectF zoomContainerRect = new RectF();
    /*矩形四个边角坐标(拖动确认前缓存)*/private RectF cacheZoomContainerRect = new RectF();
    /*矩形四个边角坐标*/private RectF cacheRect = new RectF();
    /*边角线长度*/private float mCornerLength = 30f * mDensity;

    /*边角线偏移值*/private float mCornerOffset = mDensity;

    /*画笔*/
    /*边框画笔*/private Paint mRectPaint;
    /*边角线画笔*/private Paint mCornerPaint;
    /*测绘线画笔*/private Paint mMappingLinePaint;

    /**
     * 矩形操作状态 0-不动 1-拖动 2-边角缩放 3-边框缩放
     */
    private int mOperatingStatus = 0;

    /**
     * 边框线点击-操作状态 0-左 1-上 2-右 3-下
     */
    private int mBorderlineStatus = -1;
    /**
     * 拖动的角 0-左上 1-左下 2-右上 3-右下
     */
    private int touchCorner;

    /*是否绘制测绘线*/private boolean mISDrawMapLine;

    /* 上一次按下的点 */
    private PointF lastPressPoint = new PointF();
    /* 第一次按下的点 */
    private PointF startPressPoint = new PointF();

    private int mRatioType = 0;

    public MCustomZoomView(Context context) {
        this(context, null);
    }

    public MCustomZoomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);//触摸获取焦点
        initPaint();
        setRatioType(0);
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
        zoomContainerRect.set(
                (w - mRectLength) / 2, (h - mRectLength) / 2,
                (w + mRectLength) / 2, (h + mRectLength) / 2
        );

        onTransformListener.onTransform(
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
        /*绘制边框*/
        canvas.drawRect(zoomContainerRect, mRectPaint);

        /*绘制边角*/
        /*左上-横*/
        canvas.drawLine(zoomContainerRect.left - mCornerOffset, zoomContainerRect.top,
                zoomContainerRect.left + mCornerLength, zoomContainerRect.top, mCornerPaint);
        /*左上-竖*/
        canvas.drawLine(zoomContainerRect.left, zoomContainerRect.top - mCornerOffset
                , zoomContainerRect.left, zoomContainerRect.top + mCornerLength, mCornerPaint);
        /*左下-横*/
        canvas.drawLine(zoomContainerRect.left - mCornerOffset, zoomContainerRect.bottom
                , zoomContainerRect.left + mCornerLength, zoomContainerRect.bottom, mCornerPaint);
        /*左下-竖*/
        canvas.drawLine(zoomContainerRect.left, zoomContainerRect.bottom - mCornerLength
                , zoomContainerRect.left, zoomContainerRect.bottom + mCornerOffset, mCornerPaint);
        /*右上-横*/
        canvas.drawLine(zoomContainerRect.right - mCornerLength, zoomContainerRect.top
                , zoomContainerRect.right + mCornerOffset, zoomContainerRect.top, mCornerPaint);
        /*右上-竖*/
        canvas.drawLine(zoomContainerRect.right, zoomContainerRect.top - mCornerOffset
                , zoomContainerRect.right, zoomContainerRect.top + mCornerLength, mCornerPaint);
        /*右下-横*/
        canvas.drawLine(zoomContainerRect.right - mCornerLength, zoomContainerRect.bottom
                , zoomContainerRect.right + mCornerOffset, zoomContainerRect.bottom, mCornerPaint);
        /*右下-竖*/
        canvas.drawLine(zoomContainerRect.right, zoomContainerRect.bottom - mCornerLength
                , zoomContainerRect.right, zoomContainerRect.bottom + mCornerOffset, mCornerPaint);

        if (mISDrawMapLine) {
            toolDrawMapLine(canvas);
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        /*边框画笔*/
        /*初始化*/
        mRectPaint = new Paint();
        /*设置画笔颜色*/
        mRectPaint.setColor(ContextCompat.getColor(mContext, R.color.white));
        /*设置画笔样式*/
        mRectPaint.setStyle(Paint.Style.STROKE);
        /*设置画笔粗细*/
        mRectPaint.setStrokeWidth(1 * mDensity);
        /*使用抗锯齿*/
        mRectPaint.setAntiAlias(true);
        /*使用防抖动*/
        mRectPaint.setDither(true);
        /*设置笔触样式-圆*/
        mRectPaint.setStrokeCap(Paint.Cap.ROUND);
        /*设置结合处为圆弧*/
        mRectPaint.setStrokeJoin(Paint.Join.ROUND);

        /*边角画笔*/
        /*初始化*/
        mCornerPaint = new Paint();
        /*设置画笔颜色*/
        mCornerPaint.setColor(ContextCompat.getColor(mContext, R.color.white));
        /*设置画笔样式*/
        mCornerPaint.setStyle(Paint.Style.FILL);
        /*设置画笔粗细*/
        mCornerPaint.setStrokeWidth(3 * mDensity);
        /*使用抗锯齿*/
        mCornerPaint.setAntiAlias(true);
        /*使用防抖动*/
        mCornerPaint.setDither(true);
        /*设置笔触样式-圆*/
        mRectPaint.setStrokeCap(Paint.Cap.ROUND);
        /*设置结合处为圆弧*/
        mRectPaint.setStrokeJoin(Paint.Join.ROUND);

        /*测绘线画笔*/
        /*初始化*/
        mMappingLinePaint = new Paint();
        /*设置画笔颜色*/
        mMappingLinePaint.setColor(ContextCompat.getColor(mContext, R.color.white));
        /*设置画笔样式*/
        mMappingLinePaint.setStyle(Paint.Style.FILL);
        /*设置画笔粗细*/
        mMappingLinePaint.setStrokeWidth(1 * mDensity);
        /*使用抗锯齿*/
        mMappingLinePaint.setAntiAlias(true);
        /*使用防抖动*/
        mMappingLinePaint.setDither(true);
        /*设置笔触样式-圆*/
        mMappingLinePaint.setStrokeCap(Paint.Cap.ROUND);
        /*设置结合处为圆弧*/
        mMappingLinePaint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void setRatio(float ratio) {
        this.mRatioType = -1;
        this.ratio = ratio;
        if (zoomContainerRect.isEmpty()) {
            return;
        }
        float centerX = zoomContainerRect.centerX();
        float centerY = zoomContainerRect.centerY();
        float width = zoomContainerRect.width();
        float height = zoomContainerRect.height();
        if (width / height == ratio) {
            // 比例是对的，直接返回
            return;
        }
        // 宽度保持不变，调整高度
        height = width / ratio;
        // 判断高是否超出控件高度
        int maxHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (height > maxHeight) {
            height = maxHeight;
            // 超过高度，重新设置宽度
            width = height * ratio;
        }
        zoomContainerRect.set(centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2);
        // fix框的位置
        autoFixRectByTransform(zoomContainerRect);
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*当前按下的X坐标*/
        float touchX = event.getX();
        /*当前按下的Y坐标*/
        float touchY = event.getY();
        switch (event.getAction()) {
            /*按下*/
            case MotionEvent.ACTION_DOWN:
                /*判断按下的点是否在边角上*/
                if (toolPointIsInCorner(touchX, touchY)) {
                    mOperatingStatus = 2;//边角的范围是一个长宽等于边角线长的矩形范围内
                }
                /*判断按下的点是都在边界线上*/
                else if (toolPointIsInBorderline(touchX, touchY)) {
                    mOperatingStatus = 3;
                }
                /*判断按下的点是否在矩形内*/
                else if (toolPointIsInRect(touchX, touchY)) {
                    mOperatingStatus = 1;
                }
                lastPressPoint.set(touchX, touchY);
                startPressPoint.set(touchX, touchY);
                break;
            /*移动*/
            case MotionEvent.ACTION_MOVE:
                /*移动-改变矩形四个点坐标*/
                if (mOperatingStatus == 1) {
                    zoomContainerRect.offsetTo(touchX - zoomContainerRect.width() / 2, touchY - zoomContainerRect.width() / 2);
                    autoFixRectByTransform(zoomContainerRect);
                    invalidate();
                }
                /*边角缩放*/
                else if (mOperatingStatus == 2) {
                    onCornerDrag(touchX, touchY);
                }
                /*边框缩放*/
                else if (mOperatingStatus == 3) {
                    onBorderDrag(touchX, touchY);
                }
                /*保存上一次按下的点*/
                lastPressPoint.set(touchX, touchY);
                break;
            /*松开*/
            case MotionEvent.ACTION_UP:
                /*恢复静止*/
                mOperatingStatus = 0;
                mBorderlineStatus = -1;
                onTransformListener.onTransform(
                        zoomContainerRect.left,
                        zoomContainerRect.top,
                        zoomContainerRect.right,
                        zoomContainerRect.bottom
                );
                break;
            default:

                break;
        }
        return true;
    }

    /**
     * 边角拖动
     */
    private void onCornerDrag(float touchX, float touchY) {
        boolean baseWithLeft = touchCorner == 2 || touchCorner == 3;
        boolean baseWithTop = touchCorner == 1 || touchCorner == 3;
        // fix触摸点的坐标，使其不超过边界
        touchX = autoFixTouchX(touchX, baseWithLeft);
        touchY = autoFixTouchY(touchY, baseWithTop);

        cacheZoomContainerRect.set(zoomContainerRect);
        float x = baseWithLeft ? cacheZoomContainerRect.left : cacheZoomContainerRect.right;
        float y = baseWithTop ? cacheZoomContainerRect.top : cacheZoomContainerRect.bottom;
        // 设置新的宽高
        float newW = Math.abs(touchX - x);
        float newH = Math.abs(touchY - y);
        if (ratio != 0 && newW / newH != ratio) {
            // 如果不是自由模式，需要重新计算另外一边
            if (newW > newH) {
                newW = newH * ratio;
            } else {
                newH = newW / ratio;
            }
        }
        cacheZoomContainerRect.set(
                baseWithLeft ? cacheZoomContainerRect.left : cacheZoomContainerRect.right - newW,
                baseWithTop ? cacheZoomContainerRect.top : cacheZoomContainerRect.bottom - newH,
                baseWithLeft ? cacheZoomContainerRect.left + newW : cacheZoomContainerRect.right,
                baseWithTop ? cacheZoomContainerRect.top + newH : cacheZoomContainerRect.bottom
        );

        // fix框的宽高
        autoFixRectByScale(cacheZoomContainerRect, baseWithLeft, baseWithTop);
        zoomContainerRect.set(cacheZoomContainerRect);
        invalidate();
    }

    /**
     * 边框拖动
     */
    private void onBorderDrag(float touchX, float touchY) {
        boolean isVertical = mBorderlineStatus == 1 || mBorderlineStatus == 3;
        boolean baseWithLeft = mBorderlineStatus != 0;
        boolean baseWithTop = mBorderlineStatus != 1;
        // fix触摸点的坐标，使其不超过边界
        touchX = autoFixTouchX(touchX, baseWithLeft);
        touchY = autoFixTouchY(touchY, baseWithTop);

        cacheZoomContainerRect.set(zoomContainerRect);
        float x = baseWithLeft ? cacheZoomContainerRect.left : cacheZoomContainerRect.right;
        float y = baseWithTop ? cacheZoomContainerRect.top : cacheZoomContainerRect.bottom;
        if (mRatioType == 0) {
            // 自由模式，单边缩放，前面限制过触摸点的坐标了，不用做额外的处理
            if (isVertical) {
                if (baseWithTop) {
                    cacheZoomContainerRect.bottom = touchY;
                } else {
                    cacheZoomContainerRect.top = touchY;
                }
            } else {
                if (baseWithLeft) {
                    cacheZoomContainerRect.right = touchX;
                } else {
                    cacheZoomContainerRect.left = touchX;
                }
            }
        } else {
            // 设置新的宽高
            float newW = Math.abs(touchX - x);
            float newH = Math.abs(touchY - y);
            if (isVertical) {
                newW = newH * ratio;
            } else {
                newH = newW / ratio;
            }
            cacheZoomContainerRect.set(
                    baseWithLeft ? cacheZoomContainerRect.left : cacheZoomContainerRect.right - newW,
                    baseWithTop ? cacheZoomContainerRect.top : cacheZoomContainerRect.bottom - newH,
                    baseWithLeft ? cacheZoomContainerRect.left + newW : cacheZoomContainerRect.right,
                    baseWithTop ? cacheZoomContainerRect.top + newH : cacheZoomContainerRect.bottom
            );
            autoFixRectByScale(cacheZoomContainerRect, baseWithLeft, baseWithTop);
        }

        zoomContainerRect.set(cacheZoomContainerRect);
        invalidate();
    }

    private void autoFixRectByTransform(RectF rect) {
        float newLeft = rect.left;
        float newTop = rect.top;
        newLeft = Math.max(getPaddingLeft(), Math.min(newLeft, getWidth() - getPaddingRight() - rect.width()));
        newTop = Math.max(getPaddingTop(), Math.min(newTop, getHeight() - getPaddingBottom() - rect.height()));
        rect.offsetTo(newLeft, newTop);
    }

    private void autoFixRectByScale(RectF rect, boolean baseWithLeft, boolean baseWithTop) {
        int minLeft = getPaddingLeft();
        int minTop = getPaddingTop();
        int maxRight = getWidth() - getPaddingRight();
        int maxBottom = getHeight() - getPaddingBottom();

        // 原来的宽高比
        float originRatio = rect.width() / rect.height();

        float offsetX;
        float offsetY;
        if (baseWithLeft) {
            offsetX = maxRight - rect.right;
        } else {
            offsetX = rect.left - minLeft;
        }
        if (baseWithTop) {
            offsetY = maxBottom - rect.bottom;
        } else {
            offsetY = rect.top - minTop;
        }

        if (offsetX >= 0 && offsetY >= 0) {
            return;
        }

        /*
            1.都未超出边界
            2.宽超出边界，宽fix后高超出边界
            3.宽超出边界，宽fix后正常
            4.高超出边界，高fix后宽超出边界
            5.高超出边界，高fix后正常
         */
        if (offsetX < 0) {
            if (baseWithLeft) {
                rect.right = maxRight;
            } else {
                rect.left = minLeft;
            }
            float newH = rect.width() / originRatio;
            if (baseWithTop) {
                rect.bottom = rect.top + newH;
            } else {
                rect.top = rect.bottom - newH;
            }
            if (baseWithTop) {
                offsetY = maxBottom - rect.bottom;
            } else {
                offsetY = rect.top - minTop;
            }
        }

        if (offsetX >= 0 && offsetY >= 0) {
            return;
        }

        if (offsetY < 0) {
            if (baseWithTop) {
                rect.bottom = maxBottom;
            } else {
                rect.top = minTop;
            }
            float newW = rect.height() * originRatio;
            if (baseWithLeft) {
                rect.right = rect.left + newW;
            } else {
                rect.left = rect.bottom - newW;
            }
            if (baseWithLeft) {
                offsetX = maxRight - rect.right;
            } else {
                offsetX = rect.left - minLeft;
            }
        }

        if (offsetX >= 0 && offsetY >= 0) {
            return;
        }

        // 前面两步后如果还不成立，在执行一次横向的缩放
        if (offsetX < 0) {
            if (baseWithLeft) {
                rect.right = maxRight;
            } else {
                rect.left = minLeft;
            }
            float newH = rect.width() / originRatio;
            if (baseWithTop) {
                rect.bottom = rect.top + newH;
            } else {
                rect.top = rect.bottom - newH;
            }
        }
    }

    private float autoFixTouchX(float touchX, boolean baseWithLeft) {
        if (baseWithLeft) {
            touchX = Math.max(zoomContainerRect.left + mCornerLength * 2, touchX);
            return Math.min(getWidth() - getPaddingRight(), touchX);
        } else {
            touchX = Math.min(zoomContainerRect.right - mCornerLength * 2, touchX);
            return Math.max(getPaddingLeft(), touchX);
        }
    }

    private float autoFixTouchY(float touchY, boolean baseWithTop) {
        if (baseWithTop) {
            touchY = Math.max(zoomContainerRect.top + mCornerLength * 2, touchY);
            return Math.min(getHeight() - getPaddingBottom(), touchY);
        } else {
            touchY = Math.min(zoomContainerRect.bottom - mCornerLength * 2, touchY);
            return Math.max(getPaddingTop(), touchY);
        }
    }

    public void setRatioType(int mRatioType) {
        switch (mRatioType) {
            case 2:
                setRatio(1);
                break;
            case 3:
                setRatio(3f / 4f);
                break;
            case 4:
                setRatio(4f / 3f);
                break;
            default:
                this.mRatioType = 0;
                return;
        }
        this.mRatioType = mRatioType;
    }

    /**
     * 判断按下的点是否在矩形内
     */
    private boolean toolPointIsInRect(float x, float y) {
        return zoomContainerRect.contains(x, y);
    }

    /**
     * 绘制测绘线
     */
    private void toolDrawMapLine(Canvas canvas) {
        float column1X = zoomContainerRect.left + zoomContainerRect.width() / 3f;
        float column2X = zoomContainerRect.left + zoomContainerRect.width() * 2f / 3f;
        float row1Y = zoomContainerRect.top + zoomContainerRect.height() / 3f;
        float row2Y = zoomContainerRect.top + zoomContainerRect.height() * 2f / 3f;
        canvas.drawLine(column1X, zoomContainerRect.top, column1X, zoomContainerRect.bottom, mMappingLinePaint);
        canvas.drawLine(column2X, zoomContainerRect.top, column2X, zoomContainerRect.bottom, mMappingLinePaint);
        canvas.drawLine(zoomContainerRect.left, row1Y, zoomContainerRect.right, row1Y, mMappingLinePaint);
        canvas.drawLine(zoomContainerRect.left, row2Y, zoomContainerRect.right, row2Y, mMappingLinePaint);
    }

    /**
     * 判断按下的点是否在边角范围内
     */
    private boolean toolPointIsInCorner(float x, float y) {
        float touchOffset = mCornerLength / 2;

        // 左上
        cacheRect.set(zoomContainerRect);
        cacheRect.right = cacheRect.left + mCornerLength;
        cacheRect.bottom = cacheRect.top + mCornerLength;
        cacheRect.offset(-touchOffset, -touchOffset);
        if (cacheRect.contains(x, y)) {
            touchCorner = 0;
            return true;
        }

        // 左下
        cacheRect.set(zoomContainerRect);
        cacheRect.right = cacheRect.left + mCornerLength;
        cacheRect.top = cacheRect.bottom - mCornerLength;
        cacheRect.offset(-touchOffset, touchOffset);
        if (cacheRect.contains(x, y)) {
            touchCorner = 1;
            return true;
        }

        // 右上
        cacheRect.set(zoomContainerRect);
        cacheRect.left = cacheRect.right - mCornerLength;
        cacheRect.bottom = cacheRect.top + mCornerLength;
        cacheRect.offset(touchOffset, -touchOffset);
        if (cacheRect.contains(x, y)) {
            touchCorner = 2;
            return true;
        }

        // 右下
        cacheRect.set(zoomContainerRect);
        cacheRect.left = cacheRect.right - mCornerLength;
        cacheRect.top = cacheRect.bottom - mCornerLength;
        cacheRect.offset(touchOffset, touchOffset);
        //noinspection RedundantIfStatement
        if (cacheRect.contains(x, y)) {
            touchCorner = 3;
            return true;
        }

        return false;
    }

    /**
     * 判断按下的点是否在边框线范围内
     */
    private boolean toolPointIsInBorderline(float x, float y) {
        if (x > zoomContainerRect.left && x < (zoomContainerRect.left + 10 * mDensity)) {
            mBorderlineStatus = 0;
            return true;
        }

        if (y > zoomContainerRect.top && y < (zoomContainerRect.top + 10 * mDensity)) {
            mBorderlineStatus = 1;
            return true;
        }

        if (x < zoomContainerRect.right && x > (zoomContainerRect.right - 10 * mDensity)) {
            mBorderlineStatus = 2;
            return true;
        }

        if (y < zoomContainerRect.bottom && y > (zoomContainerRect.bottom - 10 * mDensity)) {
            mBorderlineStatus = 3;
            return true;
        }

        return false;
    }

    public void setISDrawMapLine(boolean mISDrawMapLine) {
        this.mISDrawMapLine = mISDrawMapLine;
        invalidate();
    }

    /**
     * 写个接口 用来传递裁剪框的rect
     */
    private onTransformListener onTransformListener;

    public interface onTransformListener {
        void onTransform(float left, float top, float right, float bottom);
    }

    public void setOnTransformListener(MCustomZoomView.onTransformListener onTransformListener) {
        this.onTransformListener = onTransformListener;
    }
}