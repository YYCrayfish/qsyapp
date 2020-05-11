package com.manyu.videoshare.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.manyu.videoshare.util.TypefaceHelper;


/**
 * PingFang常规体
 */

public class RegularTextView extends android.support.v7.widget.AppCompatTextView {

    private Context mContext = null;

    private Typeface mTypeface;

    public RegularTextView(Context context) {
        super(context);
        if (mContext == null) {
            this.mContext = context;
        }
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (mContext == null) {
            this.mContext = context;
        }
        if (mTypeface == null) {
            mTypeface = TypefaceHelper.get(context, "PingFang-SC-Regular.ttf");
        }
        this.setTypeface(mTypeface);
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (mContext == null) {
            this.mContext = context;
        }
    }
}
