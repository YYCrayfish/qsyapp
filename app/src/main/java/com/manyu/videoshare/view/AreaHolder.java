package com.manyu.videoshare.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.manyu.videoshare.R;
import com.manyu.videoshare.util.ImageUtil;

public class AreaHolder {

    private static final int LEFT = 0;
    private static final int TOP = 1;
    private static final int RIGHT = 2;
    private static final int BOTTOM = 3;

    private static final int LEFT_TOP = 0;
    private static final int LEFT_BOTTOM = 1;
    private static final int RIGHT_TOP = 2;
    private static final int RIGHT_BOTTOM = 3;

    private static final int IDLE = -1;
    private static final int DRAG = 1;
    private static final int CORNER_DRAG = 2;
    private static final int LINE_DRAG = 3;

    /*边角线长度*/
    private float mCornerLength;

    /*边角线偏移值*/
    private float mCornerOffset;

    private RectF containerRect = new RectF();

    private RectF cacheRect = new RectF();

    // 外边框画笔
    private Paint mBorderPaint;
    // 四个顶角画笔
    private Paint mCornerPaint;
    /*测绘线画笔*/
    private Paint mMappingLinePaint;
    /* 背景 */
    private Paint mBgPaint;

    /*是否绘制测绘线*/
    private boolean mIsDrawMapLine;

    /* 是否绘制关闭按钮 */
    private boolean mIsDrawCloseButton;

    /* 是否绘制背景 */
    private boolean mIsDrawBg;

    /**
     * 矩形操作状态 0-不动 1-拖动 2-边角缩放 3-边框缩放
     */
    private int mOperatingStatus = IDLE;

    /**
     * 边框线点击-操作状态 0-左 1-上 2-右 3-下
     */
    private int mBorderlineStatus = IDLE;
    /**
     * 拖动的角 0-左上 1-左下 2-右上 3-右下
     */
    private int touchCorner = IDLE;

    /* 比例类型: 0->自由 1->1:1 2->3:4 3->4:3 */
    private int mRatioType = 0;

    // 宽高比
    private float ratio = 0;

    private View parent;

    /* 上一次按下的点 */
    private PointF lastPressPoint = new PointF();

    /* 第一次按下的点 */
    private PointF startPressPoint = new PointF();

    /* 视作拖动的阈值 */
    private float dragThreshold;

    // 是否有进行拖动的标记
    private boolean isDragFlag = false;

    // 关闭按钮的点击事件
    private Function onClosingListener;

    // 关闭按钮的图片
    private Bitmap deleteIcon;

    /* 关闭按钮的图片尺寸  */
    private float mDeleteIconSize;

    AreaHolder(View parent) {
        this.parent = parent;
        init();
        initPaint();
    }

    private void init() {
        this.mCornerLength = ImageUtil.dp2px(parent.getContext(), 15);
        this.mCornerOffset = ImageUtil.dp2px(parent.getContext(), 1);
        this.dragThreshold = ImageUtil.dp2px(parent.getContext(), 10);
        this.mDeleteIconSize = ImageUtil.dp2px(parent.getContext(), 20);
        Drawable drawble = ContextCompat.getDrawable(parent.getContext(), R.drawable.watermark_controller_delete);
        if (drawble instanceof BitmapDrawable) {
            deleteIcon = ((BitmapDrawable) drawble).getBitmap();
        }
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        /*边框画笔*/
        /*初始化*/
        mBorderPaint = new Paint();
        /*设置画笔颜色*/
        mBorderPaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.white));
        /*设置画笔样式*/
        mBorderPaint.setStyle(Paint.Style.STROKE);
        /*设置画笔粗细*/
        mBorderPaint.setStrokeWidth(ImageUtil.dp2px(parent.getContext(), 1));
        /*使用抗锯齿*/
        mBorderPaint.setAntiAlias(true);
        /*使用防抖动*/
        mBorderPaint.setDither(true);
        /*设置笔触样式-圆*/
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        /*设置结合处为圆弧*/
        mBorderPaint.setStrokeJoin(Paint.Join.ROUND);

        /*边角画笔*/
        /*初始化*/
        mCornerPaint = new Paint();
        /*设置画笔颜色*/
        mCornerPaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.white));
        /*设置画笔样式*/
        mCornerPaint.setStyle(Paint.Style.FILL);
        /*设置画笔粗细*/
        mCornerPaint.setStrokeWidth(ImageUtil.dp2px(parent.getContext(), 3));
        /*使用抗锯齿*/
        mCornerPaint.setAntiAlias(true);
        /*使用防抖动*/
        mCornerPaint.setDither(true);

        /*测绘线画笔*/
        /*初始化*/
        mMappingLinePaint = new Paint();
        /*设置画笔颜色*/
        mMappingLinePaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.white));
        /*设置画笔样式*/
        mMappingLinePaint.setStyle(Paint.Style.FILL);
        /*设置画笔粗细*/
        mMappingLinePaint.setStrokeWidth(ImageUtil.dp2px(parent.getContext(), 1));
        /*使用抗锯齿*/
        mMappingLinePaint.setAntiAlias(true);
        /*使用防抖动*/
        mMappingLinePaint.setDither(true);
        /*设置笔触样式-圆*/
        mMappingLinePaint.setStrokeCap(Paint.Cap.ROUND);
        /*设置结合处为圆弧*/
        mMappingLinePaint.setStrokeJoin(Paint.Join.ROUND);

        mBgPaint = new Paint();
        mBgPaint.setColor(ContextCompat.getColor(parent.getContext(), R.color.black));
        mBgPaint.setAntiAlias(true);
        mBgPaint.setDither(true);
        mBgPaint.setStyle(Paint.Style.FILL);
    }

    RectF getContainerRect() {
        return containerRect;
    }

    void setIsDrawMapLine(boolean isDrawMapLine) {
        this.mIsDrawMapLine = isDrawMapLine;
    }

    void setIsDrawCloseButton(boolean isDrawCloseButton) {
        this.mIsDrawCloseButton = isDrawCloseButton;
    }

    void setIsDrawBg(boolean mIsDrawBg) {
        this.mIsDrawBg = mIsDrawBg;
    }

    void setOnClosingListener(Function onClosingListener) {
        this.onClosingListener = onClosingListener;
    }

    void setRatioType(int mRatioType) {
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

    void setRatio(float ratio) {
        this.mRatioType = -1;
        this.ratio = ratio;
        if (containerRect.isEmpty()) {
            return;
        }
        float centerX = containerRect.centerX();
        float centerY = containerRect.centerY();
        float width = containerRect.width();
        float height = containerRect.height();
        if (width / height == ratio) {
            // 比例是对的，直接返回
            return;
        }
        // 宽度保持不变，调整高度
        height = width / ratio;
        // 判断高是否超出控件高度
        int maxHeight = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
        if (height > maxHeight) {
            height = maxHeight;
            // 超过高度，重新设置宽度
            width = height * ratio;
        }
        containerRect.set(centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2);
        // fix框的位置
        autoFixRectByTransform(containerRect);
        parent.invalidate();
    }

    /**
     * 以入参为左上角坐标，初始化为边角拖动模式
     */
    void initWithCornerDragMode(float left, float top) {
        cacheRect.set(left, top, left + mCornerLength * 2, top + mCornerLength * 2);
        autoFixRectByTransform(cacheRect);
        containerRect.set(cacheRect);

        touchCorner = RIGHT_BOTTOM;
        mOperatingStatus = CORNER_DRAG;

        startPressPoint.set(left, top);
    }

    public void onDraw(Canvas canvas) {

        if (mIsDrawBg) {
            // 绘制背景
            canvas.drawRect(containerRect, mBgPaint);
        }

        /*绘制边框*/
        canvas.drawRect(containerRect, mBorderPaint);

        /*绘制边角*/
        /*左上-横*/
        canvas.drawLine(containerRect.left - mCornerOffset, containerRect.top,
                containerRect.left + mCornerLength, containerRect.top, mCornerPaint);
        /*左上-竖*/
        canvas.drawLine(containerRect.left, containerRect.top - mCornerOffset
                , containerRect.left, containerRect.top + mCornerLength, mCornerPaint);
        /*左下-横*/
        canvas.drawLine(containerRect.left - mCornerOffset, containerRect.bottom
                , containerRect.left + mCornerLength, containerRect.bottom, mCornerPaint);
        /*左下-竖*/
        canvas.drawLine(containerRect.left, containerRect.bottom - mCornerLength
                , containerRect.left, containerRect.bottom + mCornerOffset, mCornerPaint);
        /*右上-横*/
        canvas.drawLine(containerRect.right - mCornerLength, containerRect.top
                , containerRect.right + mCornerOffset, containerRect.top, mCornerPaint);
        /*右上-竖*/
        canvas.drawLine(containerRect.right, containerRect.top - mCornerOffset
                , containerRect.right, containerRect.top + mCornerLength, mCornerPaint);
        /*右下-横*/
        canvas.drawLine(containerRect.right - mCornerLength, containerRect.bottom
                , containerRect.right + mCornerOffset, containerRect.bottom, mCornerPaint);
        /*右下-竖*/
        canvas.drawLine(containerRect.right, containerRect.bottom - mCornerLength
                , containerRect.right, containerRect.bottom + mCornerOffset, mCornerPaint);

        if (mIsDrawMapLine) {
            toolDrawMapLine(canvas);
        }

        if (mIsDrawCloseButton && deleteIcon != null) {
            // 绘制删除按钮
            cacheRect.set(
                    containerRect.right - mDeleteIconSize / 2,
                    containerRect.top - mDeleteIconSize / 2,
                    containerRect.right + mDeleteIconSize / 2,
                    containerRect.top + mDeleteIconSize / 2
            );
            canvas.drawBitmap(deleteIcon, null, cacheRect, null);
        }
    }

    void onDown(float touchX, float touchY) {
        /*判断按下的点是否在边角上*/
        if (toolPointIsInCorner(touchX, touchY)) {
            mOperatingStatus = CORNER_DRAG;//边角的范围是一个长宽等于边角线长的矩形范围内
        }
        /*判断按下的点是都在边界线上*/
        else if (toolPointIsInBorderline(touchX, touchY)) {
            mOperatingStatus = LINE_DRAG;
        }
        /*判断按下的点是否在矩形内*/
        else if (toolPointIsInRect(touchX, touchY, 0)) {
            mOperatingStatus = DRAG;
        }
        lastPressPoint.set(touchX, touchY);
        startPressPoint.set(touchX, touchY);
    }

    void onMove(float touchX, float touchY) {
        if (!isDragFlag) {
            isDragFlag = Math.abs(touchX - startPressPoint.x) > dragThreshold || Math.abs(touchY - startPressPoint.y) > dragThreshold;
        }

        /*移动-改变矩形四个点坐标*/
        if (mOperatingStatus == DRAG) {
            containerRect.offsetTo(touchX - containerRect.width() / 2, touchY - containerRect.width() / 2);
            autoFixRectByTransform(containerRect);
            parent.invalidate();
        }
        /*边角缩放*/
        else if (mOperatingStatus == CORNER_DRAG) {
            onCornerDrag(touchX, touchY);
        }
        /*边框缩放*/
        else if (mOperatingStatus == LINE_DRAG) {
            onBorderDrag(touchX, touchY);
        }
        lastPressPoint.set(touchX, touchY);
    }

    void onUp() {
        /*恢复静止*/
        mOperatingStatus = IDLE;
        mBorderlineStatus = IDLE;
        if (!isDragFlag) {
            // 没有进行过拖动，判断触摸点是否落在关闭按钮的位置上
            cacheRect.set(
                    containerRect.right - mDeleteIconSize / 2,
                    containerRect.top - mDeleteIconSize / 2,
                    containerRect.right + mDeleteIconSize / 2,
                    containerRect.top + mDeleteIconSize / 2
            );
            if (cacheRect.contains(lastPressPoint.x, lastPressPoint.y)) {
                // 触发关闭按钮的点击事件
                if (onClosingListener != null) {
                    onClosingListener.invoke(this);
                }
            }
        }
        isDragFlag = false;
    }


    /**
     * 判断按下的点是否在矩形内
     */
    boolean toolPointIsInRect(float x, float y, float overBound) {
        cacheRect.set(containerRect);
        cacheRect.inset(-overBound, -overBound);
        return cacheRect.contains(x, y);
    }

    /**
     * 边角拖动
     */
    private void onCornerDrag(float touchX, float touchY) {
        boolean baseWithLeft = touchCorner == RIGHT_TOP || touchCorner == RIGHT_BOTTOM;
        boolean baseWithTop = touchCorner == LEFT_BOTTOM || touchCorner == RIGHT_BOTTOM;
        // fix触摸点的坐标，使其不超过边界
        touchX = autoFixTouchX(touchX, baseWithLeft);
        touchY = autoFixTouchY(touchY, baseWithTop);

        cacheRect.set(containerRect);
        float x = baseWithLeft ? cacheRect.left : cacheRect.right;
        float y = baseWithTop ? cacheRect.top : cacheRect.bottom;
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
        cacheRect.set(
                baseWithLeft ? cacheRect.left : cacheRect.right - newW,
                baseWithTop ? cacheRect.top : cacheRect.bottom - newH,
                baseWithLeft ? cacheRect.left + newW : cacheRect.right,
                baseWithTop ? cacheRect.top + newH : cacheRect.bottom
        );

        // fix框的宽高
        autoFixRectByScale(cacheRect, baseWithLeft, baseWithTop);
        containerRect.set(cacheRect);
        parent.invalidate();
    }

    /**
     * 边框拖动
     */
    private void onBorderDrag(float touchX, float touchY) {
        boolean isVertical = mBorderlineStatus == TOP || mBorderlineStatus == BOTTOM;
        boolean baseWithLeft = mBorderlineStatus != LEFT;
        boolean baseWithTop = mBorderlineStatus != TOP;
        // fix触摸点的坐标，使其不超过边界
        touchX = autoFixTouchX(touchX, baseWithLeft);
        touchY = autoFixTouchY(touchY, baseWithTop);

        cacheRect.set(containerRect);
        float x = baseWithLeft ? cacheRect.left : cacheRect.right;
        float y = baseWithTop ? cacheRect.top : cacheRect.bottom;
        if (mRatioType == 0) {
            // 自由模式，单边缩放，前面限制过触摸点的坐标了，不用做额外的处理
            if (isVertical) {
                if (baseWithTop) {
                    cacheRect.bottom = touchY;
                } else {
                    cacheRect.top = touchY;
                }
            } else {
                if (baseWithLeft) {
                    cacheRect.right = touchX;
                } else {
                    cacheRect.left = touchX;
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
            cacheRect.set(
                    baseWithLeft ? cacheRect.left : cacheRect.right - newW,
                    baseWithTop ? cacheRect.top : cacheRect.bottom - newH,
                    baseWithLeft ? cacheRect.left + newW : cacheRect.right,
                    baseWithTop ? cacheRect.top + newH : cacheRect.bottom
            );
            autoFixRectByScale(cacheRect, baseWithLeft, baseWithTop);
        }

        containerRect.set(cacheRect);
        parent.invalidate();
    }

    private void autoFixRectByTransform(RectF rectF) {
        float newLeft = rectF.left;
        float newTop = rectF.top;
        newLeft = Math.max(parent.getPaddingLeft(), Math.min(newLeft, parent.getWidth() - parent.getPaddingRight() - rectF.width()));
        newTop = Math.max(parent.getPaddingTop(), Math.min(newTop, parent.getHeight() - parent.getPaddingBottom() - rectF.height()));
        rectF.offsetTo(newLeft, newTop);
    }

    private void autoFixRectByScale(RectF rectF, boolean baseWithLeft, boolean baseWithTop) {
        int minLeft = parent.getPaddingLeft();
        int minTop = parent.getPaddingTop();
        int maxRight = parent.getWidth() - parent.getPaddingRight();
        int maxBottom = parent.getHeight() - parent.getPaddingBottom();

        // 原来的宽高比
        float originRatio = rectF.width() / rectF.height();

        float offsetX;
        float offsetY;
        if (baseWithLeft) {
            offsetX = maxRight - rectF.right;
        } else {
            offsetX = rectF.left - minLeft;
        }
        if (baseWithTop) {
            offsetY = maxBottom - rectF.bottom;
        } else {
            offsetY = rectF.top - minTop;
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
                rectF.right = maxRight;
            } else {
                rectF.left = minLeft;
            }
            float newH = rectF.width() / originRatio;
            if (baseWithTop) {
                rectF.bottom = rectF.top + newH;
            } else {
                rectF.top = rectF.bottom - newH;
            }
            if (baseWithTop) {
                offsetY = maxBottom - rectF.bottom;
            } else {
                offsetY = rectF.top - minTop;
            }
        }

        if (offsetX >= 0 && offsetY >= 0) {
            return;
        }

        if (offsetY < 0) {
            if (baseWithTop) {
                rectF.bottom = maxBottom;
            } else {
                rectF.top = minTop;
            }
            float newW = rectF.height() * originRatio;
            if (baseWithLeft) {
                rectF.right = rectF.left + newW;
            } else {
                rectF.left = rectF.bottom - newW;
            }
            if (baseWithLeft) {
                offsetX = maxRight - rectF.right;
            } else {
                offsetX = rectF.left - minLeft;
            }
        }

        if (offsetX >= 0 && offsetY >= 0) {
            return;
        }

        // 前面两步后如果还不成立，在执行一次横向的缩放
        if (offsetX < 0) {
            if (baseWithLeft) {
                rectF.right = maxRight;
            } else {
                rectF.left = minLeft;
            }
            float newH = rectF.width() / originRatio;
            if (baseWithTop) {
                rectF.bottom = rectF.top + newH;
            } else {
                rectF.top = rectF.bottom - newH;
            }
        }
    }

    private float autoFixTouchX(float touchX, boolean baseWithLeft) {
        if (baseWithLeft) {
            touchX = Math.max(containerRect.left + mCornerLength * 2, touchX);
            return Math.min(parent.getWidth() - parent.getPaddingRight(), touchX);
        } else {
            touchX = Math.min(containerRect.right - mCornerLength * 2, touchX);
            return Math.max(parent.getPaddingLeft(), touchX);
        }
    }

    private float autoFixTouchY(float touchY, boolean baseWithTop) {
        if (baseWithTop) {
            touchY = Math.max(containerRect.top + mCornerLength * 2, touchY);
            return Math.min(parent.getHeight() - parent.getPaddingBottom(), touchY);
        } else {
            touchY = Math.min(containerRect.bottom - mCornerLength * 2, touchY);
            return Math.max(parent.getPaddingTop(), touchY);
        }
    }

    /**
     * 判断按下的点是否在边角范围内
     */
    private boolean toolPointIsInCorner(float x, float y) {
        float touchOffset = mCornerLength / 2;

        // 左上
        cacheRect.set(containerRect);
        cacheRect.right = cacheRect.left + mCornerLength;
        cacheRect.bottom = cacheRect.top + mCornerLength;
        cacheRect.offset(-touchOffset, -touchOffset);
        if (cacheRect.contains(x, y)) {
            touchCorner = LEFT_TOP;
            return true;
        }

        // 左下
        cacheRect.set(containerRect);
        cacheRect.right = cacheRect.left + mCornerLength;
        cacheRect.top = cacheRect.bottom - mCornerLength;
        cacheRect.offset(-touchOffset, touchOffset);
        if (cacheRect.contains(x, y)) {
            touchCorner = LEFT_BOTTOM;
            return true;
        }

        // 右上
        cacheRect.set(containerRect);
        cacheRect.left = cacheRect.right - mCornerLength;
        cacheRect.bottom = cacheRect.top + mCornerLength;
        cacheRect.offset(touchOffset, -touchOffset);
        if (cacheRect.contains(x, y)) {
            touchCorner = RIGHT_TOP;
            return true;
        }

        // 右下
        cacheRect.set(containerRect);
        cacheRect.left = cacheRect.right - mCornerLength;
        cacheRect.top = cacheRect.bottom - mCornerLength;
        cacheRect.offset(touchOffset, touchOffset);
        if (cacheRect.contains(x, y)) {
            touchCorner = RIGHT_BOTTOM;
            return true;
        }

        return false;
    }

    /**
     * 判断按下的点是否在边框线范围内
     */
    private boolean toolPointIsInBorderline(float x, float y) {
        if (x > containerRect.left && x < (containerRect.left + dragThreshold)) {
            mBorderlineStatus = LEFT;
            return true;
        }

        if (y > containerRect.top && y < (containerRect.top + dragThreshold)) {
            mBorderlineStatus = TOP;
            return true;
        }

        if (x < containerRect.right && x > (containerRect.right - dragThreshold)) {
            mBorderlineStatus = RIGHT;
            return true;
        }

        if (y < containerRect.bottom && y > (containerRect.bottom - dragThreshold)) {
            mBorderlineStatus = BOTTOM;
            return true;
        }

        return false;
    }

    /**
     * 绘制测绘线
     */
    private void toolDrawMapLine(Canvas canvas) {
        float column1X = containerRect.left + containerRect.width() / 3f;
        float column2X = containerRect.left + containerRect.width() * 2f / 3f;
        float row1Y = containerRect.top + containerRect.height() / 3f;
        float row2Y = containerRect.top + containerRect.height() * 2f / 3f;
        canvas.drawLine(column1X, containerRect.top, column1X, containerRect.bottom, mMappingLinePaint);
        canvas.drawLine(column2X, containerRect.top, column2X, containerRect.bottom, mMappingLinePaint);
        canvas.drawLine(containerRect.left, row1Y, containerRect.right, row1Y, mMappingLinePaint);
        canvas.drawLine(containerRect.left, row2Y, containerRect.right, row2Y, mMappingLinePaint);
    }

    public interface Function {
        void invoke(AreaHolder area);
    }
}
