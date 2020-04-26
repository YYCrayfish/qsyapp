package com.manyu.videoshare.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import com.manyu.videoshare.R;

public class TimeCutBar extends View {
    private static final int DEFAULT_PROGRESS_WIDTH = 2;
    private static final int DEFAULT_PROGRESS_COLOR = 0xFFFFFFFF;
    private Context context;
    private int progressHight;
    private int totalTime;
    private int mProgressColor = DEFAULT_PROGRESS_COLOR;

    public TimeCutBar(Context context) {
        this(context, null);
    }

    public TimeCutBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeCutBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if(null!=attrs){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TimeCutBar);
            progressHight = typedArray.getDimensionPixelSize(R.styleable.TimeCutBar_tcb_progress_height,
                    dip2px(context, DEFAULT_PROGRESS_WIDTH));
            mProgressColor = typedArray.getColor(R.styleable.TimeCutBar_tcb_progress_color,
                    DEFAULT_PROGRESS_COLOR);
            totalTime = typedArray.getInt(R.styleable.TimeCutBar_tcb_time, 0);
            typedArray.recycle();
        }

    }

    private int dip2px(Context ctx, float dpValue) {
        final float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
