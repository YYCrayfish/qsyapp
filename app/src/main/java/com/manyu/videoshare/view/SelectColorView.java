package com.manyu.videoshare.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.manyu.videoshare.intefaces.UDataCallBack;

public class SelectColorView extends View {

    private float areaW = 65;
    private float areaH = 50;
    private Paint paint;
    private int select = 8;
    private int paddingLeft = 32;
    private int paddingRight = 32;
    private String[] mColors = new String[]{
            "FC9A00",
            "FCF30D",
            "00E700",
            "14F8EA",
            "0082F8",
            "7131FF",
            "F748B5",
            "FF2441",
            "FFFFFF",
            "000000"};

    public SelectColorView(Context context) {
        this(context, null);
    }

    public SelectColorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMyMeasureWidth(widthMeasureSpec);
        int height = getMyMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
        areaW = width / mColors.length;
        areaH = areaW * 0.8f;
    }

    private int getMyMeasureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            // matchparent 或者 固定大小 view最小应为 paddingBottom + paddingTop + bitmapHeight + 10 否则显示不全
            size = (int) Math.max(size, areaW);
        } else {
            //wrap content
            int height = (int) areaW;
            size = Math.min(size, height);
        }
        return size;
    }

    private int getMyMeasureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            size = getMeasuredWidth() - paddingLeft - paddingRight;
        } else {
            //wrap content
            int width = (int) (areaW * mColors.length);
            size = Math.min(size, width);
        }
        return size;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mColors.length; i++) {
            paint.setColor(Color.parseColor("#" + mColors[i]));
            if (i == select) {
                canvas.drawRect(new RectF(i * areaW, 0, (i + 1) * areaW, areaW), paint);
            } else {
                canvas.drawRect(new RectF(i * areaW, (areaW - areaH) / 2, (i + 1) * areaW, areaH + (areaW - areaH) / 2), paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //事件机制
        super.onTouchEvent(event);
        float nowX = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                select = (int) (nowX / areaW);
                if (nowX > areaW * mColors.length) {
                    select = mColors.length - 1;
                }
                if (nowX < 0) {
                    select = 0;
                }
                dataCallBack.onDataReceive(mColors[select]);
                invalidate();
                break;
            //手指抬起
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    private UDataCallBack dataCallBack;

    public void setOnSelectListener(UDataCallBack onSelectListener) {
        this.dataCallBack = onSelectListener;
    }


//    private onSelectListener onSelectListener;
//
//    public void setOnSelectListener(SelectColorView.onSelectListener onSelectListener) {
//        this.onSelectListener = onSelectListener;
//    }
//
//    public interface onSelectListener {
//        void onSelect(String colorStr);
//    }
}
