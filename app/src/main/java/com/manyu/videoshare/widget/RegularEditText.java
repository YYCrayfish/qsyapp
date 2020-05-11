package com.manyu.videoshare.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.manyu.videoshare.util.TypefaceHelper;


/**
 * PingFang常规体
 */

public class RegularEditText extends android.support.v7.widget.AppCompatEditText {

    private Context mContext = null;

    private Typeface mTypeface;

    public RegularEditText(Context context) {
        super(context);
        if (mContext == null) {
            this.mContext = context;
        }
    }

    public RegularEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (mContext == null) {
            this.mContext = context;
        }
        if (mTypeface == null) {
            mTypeface = TypefaceHelper.get(context, "PingFang-SC-Regular.ttf");
        }
        this.setTypeface(mTypeface);
    }

    public RegularEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (mContext == null) {
            this.mContext = context;
        }
    }
}
