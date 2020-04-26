package com.manyu.videoshare.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.manyu.videoshare.R;


@SuppressLint("AppCompatCustomView")
public class RatioImageView extends ImageView {

    /**
     * 宽高比例
     */
    private float mRatio = 0f;

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);

        mRatio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0f);
        typedArray.recycle();
    }

    public RatioImageView(Context context) {
        super(context);
    }

    /**
     * 设置ImageView的宽高比
     *
     * @param ratio
     */
    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mRatio != 0) {
            if (width <= 0) {
                float width2 = height * mRatio;
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) width2, MeasureSpec.EXACTLY);
            } else {
                float height2 = width / mRatio;
                heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height2, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }
}
