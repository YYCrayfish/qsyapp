package com.manyu.videoshare.view.scraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ScrawlBoardView extends View {

    private Paint paint, eraserPaint;
    private float startX, startY, endX, endY;
    private int currentPaintColor = 0xFF000000;

    boolean isEraser;
    List<DrawPathBean> drawPathList = new ArrayList<>();
    private Path currentPath;

    public ScrawlBoardView(Context context) {
        this(context, null);
    }

    public ScrawlBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        paint.setColor(currentPaintColor);
        paint.setStrokeWidth(10);

        eraserPaint = new Paint();
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeWidth(20);
        eraserPaint.setColor(Color.TRANSPARENT);
        Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        eraserPaint.setXfermode(xfermode);
    }

    public ScrawlBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = drawPathList.size() - 1; i >= 0; i--) {
            DrawPathBean entry = drawPathList.get(i);
            int paintColor = entry.getPaintColor();
            Paint pathPaint;
            if (paintColor == Color.TRANSPARENT) {
                pathPaint = eraserPaint;
            } else {
                pathPaint = paint;
            }
            pathPaint.setColor(paintColor);
            canvas.drawPath(entry.getPath(), pathPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();

                DrawPathBean bean = new DrawPathBean();
                currentPath = new Path();
                currentPath.moveTo(startX, startY);
                bean.setPath(currentPath);
                bean.setEraser(isEraser);
                bean.setPaintColor(isEraser ? eraserPaint.getColor() : currentPaintColor);
                bean.setPaintStrokeWidth(paint.getStrokeWidth());
                drawPathList.add(bean);
                break;
            case MotionEvent.ACTION_MOVE:
                endX = event.getX();
                endY = event.getY();
                currentPath.quadTo(startX, startY, endX, endY);
                startX = endX;
                startY = endY;
                postInvalidate();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 设置画笔线条的粗细
     *
     * @param size
     */
    public void getStrokeWidth(float size) {
        paint.setStrokeWidth(size);
        eraserPaint.setStrokeWidth(size);
    }

    /**
     * 设置画笔颜色及橡皮擦
     */
    public void setPaintColor(int color) {
        if (color == Color.TRANSPARENT) {
            isEraser = true;
            return;
        }
        currentPaintColor = color;
        Log.d("test==========>", "reset color alpha = " + (color >>> 24));
    }

    /**
     * 获取画笔颜色
     */
    public int getPaintColor() {
        return currentPaintColor;
    }

    /**
     * 撤销操作
     */
    public void cancelPath() {
        if (drawPathList == null || drawPathList.size() == 0) {
            return;
        }
        drawPathList.remove(drawPathList.size() - 1);
        postInvalidate();
    }


    /**
     * 清空涂鸦
     */
    public void clearScrawlBoard() {
        drawPathList.clear();
        postInvalidate();
    }

    public int pathCount() {
        return drawPathList.size();
    }
}
