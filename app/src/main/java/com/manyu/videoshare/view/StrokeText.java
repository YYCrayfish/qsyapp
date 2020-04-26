package com.manyu.videoshare.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manyu.videoshare.R;

/*
 * StrokeTextView的目标是给文字描边
 * 实现方法是两个TextView叠加,只有描边的TextView为底,实体TextView叠加在上面
 * 看上去文字就有个不同颜色的边框了
 */
public class StrokeText extends android.support.v7.widget.AppCompatTextView {

    private TextView borderText = null;///用于描边的TextView
    private int strokeColor;
    private int shadowColor = 0;
    private boolean openStroke;
    private float textSize = 15;
    private TextPaint paint;

    public StrokeText(Context context) {
        this(context, null);
    }

    public StrokeText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeText(Context context, AttributeSet attrs,
                      int defStyle) {
        super(context, attrs, defStyle);
        borderText = new TextView(context, attrs, defStyle);
        paint = borderText.getPaint();
        paint.setStyle(Style.STROKE);
        borderText.setSingleLine();
        init(attrs);
    }

    public void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StrokeText);
        if (typedArray != null) {
            strokeColor = typedArray.getColor(R.styleable.StrokeText_strokeColor, getResources().getColor(R.color.tran));
            openStroke = typedArray.getBoolean(R.styleable.StrokeText_openStroke, false);
            typedArray.recycle();
        }
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        super.setLayoutParams(params);
        borderText.setLayoutParams(params);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        CharSequence tt = borderText.getText();
        //两个TextView上的文字必须一致
        if (tt == null || !tt.equals(this.getText())) {
            borderText.setText(getText());
            this.postInvalidate();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        borderText.measure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        borderText.layout(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (openStroke) {
            borderText.setTextColor(strokeColor);
            borderText.draw(canvas);
        }
        super.onDraw(canvas);
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        invalidate();
    }

    public boolean isOpenStroke() {
        return openStroke;
    }

    public void setOpenStroke(boolean openStroke) {
        this.openStroke = openStroke;
        invalidate();
    }

    public void setScale(float textSize, float f) {
        borderText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize * f);
        paint.setStrokeWidth((textSize / 8) * f);                                  //设置描边宽度
        borderText.setGravity(getGravity());
        if (shadowColor != 0) {
            borderText.setShadowLayer((textSize / 6) * f, (textSize / 6) * f, (textSize / 6) * f, shadowColor);
        }
        invalidate();
    }

    public void setShadow(float f, float textSize, String colorStr) {
        shadowColor = Color.parseColor("#" + colorStr);
        borderText.setShadowLayer((textSize / 6) * f, (textSize / 6) * f, (textSize / 6) * f, Color.parseColor("#" + colorStr));
        invalidate();
    }
}