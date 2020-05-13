package com.manyu.videoshare.view.TextWaterMark;

/**
 * Author：xushiyong
 * Date：2020/5/5
 * Descript：
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.manyu.videoshare.util.ImageUtil;

/**
 * Created by lp on 2016/9/21.
 */
public class TextViewBorder extends FrameLayout {

    private RectF cacheRect = new RectF();
    private TextView borderText;///用于描边的TextView
    private TextView realText;///用于描边的TextView
    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public TextViewBorder(Context context, AttributeSet attrs) {
        super(context, attrs);
        borderText = new TextView(context);
        realText = new TextView(context);
        initTextView(borderText);
        initTextView(realText);
        borderText.setTextColor(Color.TRANSPARENT);
        realText.setTextColor(Color.WHITE);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(borderText, lp);
        addView(realText, lp);

        bgPaint.setColor(Color.TRANSPARENT);
        bgPaint.setStyle(Paint.Style.FILL);

        TextPaint paint = borderText.getPaint();
        paint.setStrokeWidth(ImageUtil.dp2px(context, 5));
        paint.setStyle(Paint.Style.STROKE);
        borderText.setTextColor(Color.TRANSPARENT);
    }

    private void initTextView(TextView tv) {
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine(true);
        tv.setTextSize(22);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bgPaint.getColor() >> 24 != 0) {
            int w = this.getWidth();
            int h = this.getHeight();
            cacheRect.set(getPaddingLeft(), getPaddingTop(), w - getPaddingRight(), h - getPaddingBottom());
            canvas.drawRoundRect(cacheRect, 5, 5, bgPaint);
        }
        super.onDraw(canvas);
    }

    public void setTextColor(int newColor) {
        realText.setTextColor(newColor);
        invalidate();
    }

    public void setBgColor(int newColor) {
        bgPaint.setColor(newColor);
        invalidate();
    }

    public void setBorderColor(int newColor) {
        borderText.setTextColor(newColor);
        invalidate();
    }

    public void setShadowLayer(float radius, float dx, float dy, int color) {
        realText.setShadowLayer(radius, dx, dy, color);
    }

    public void setTextSize(int complexUnitDip, float textSize) {
        realText.setTextSize(complexUnitDip, textSize);
        borderText.setTextSize(complexUnitDip, textSize);
    }

    public float getTextSize() {
        return realText.getTextSize();
    }

    public void setText(String content) {
        realText.setText(content);
        borderText.setText(content);
    }
}

