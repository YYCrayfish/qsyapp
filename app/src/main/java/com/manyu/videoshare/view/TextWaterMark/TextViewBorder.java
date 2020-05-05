package com.manyu.videoshare.view.TextWaterMark;

/**
 * Author：xushiyong
 * Date：2020/5/5
 * Descript：
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.manyu.videoshare.R;

/**
 * Created by lp on 2016/9/21.
 */
public class TextViewBorder extends android.support.v7.widget.AppCompatTextView {
    private static final int STROKE_WIDTH = 4;
    private int borderCol;

    private Paint borderPaint;
    private boolean isShowBGColor = false;
    private int bgColor = -1;

    public TextViewBorder(Context context, AttributeSet attrs) {
        super(context, attrs);

//        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
//                R.styleable.TextViewBorder, 0, 0);
//        try {
//            borderCol = a.getInteger(R.styleable.TextViewBorder_borderColor, 0);//0 is default
//        } finally {
//            a.recycle();
//        }

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(STROKE_WIDTH);
        borderPaint.setAntiAlias(true);

        setBorderColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (0 == this.getText().toString().length())
            return;

        borderPaint.setColor(borderCol);

        int w = this.getMeasuredWidth();
        int h = this.getMeasuredHeight();

        RectF r = new RectF(2, 2, w - 2, h - 2);
        canvas.drawRoundRect(r, 5, 5, borderPaint);

        if(isShowBGColor){
            Paint paint = new Paint();
            paint.setColor(this.bgColor);
            RectF rf = new RectF(4, 4, w - 4, h - 4);
            canvas.drawRoundRect(rf, 5, 5, paint);
        }

        super.onDraw(canvas);
    }

    public void setShadow(int color){
        setShadowLayer(10, 15, 15, color);
    }

//    public int getBordderColor() {
//        return borderCol;
//    }

    public void setBGColor(boolean isShow,int bgColor){
        this.bgColor = bgColor;
        isShowBGColor = isShow;
        invalidate();
        requestLayout();
    }

    public void setBorderColor(int newColor) {
        borderCol = newColor;
        invalidate();
        requestLayout();
    }
}

