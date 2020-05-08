package com.manyu.videoshare.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.manyu.videoshare.R;

import butterknife.ButterKnife;

/**
 *
 */

public class DialogIncomeTipUtil extends Dialog {

    private Context mContext;

    private Runnable mConfirmRunnable, mCancelRunnable;

    private DialogIncomeTipUtil instance;

    private String mContentStr, mTitleStr;
    private String mConfirmButtonStr;
    private TextView mReduceProportion;
    private TextView mReduceTitle;
    private TextView mReduceProportionTip;
    private ImageView mCloseImg;

    public DialogIncomeTipUtil(@NonNull Context context, String copy) {
        super(context, R.style.myDialogTheme);
        this.mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_selttle_account_layout, null);
        mReduceProportionTip = view.findViewById(R.id.mReduceProportionTip);
        mReduceProportion = view.findViewById(R.id.mReduceProportion);
        mReduceTitle = view.findViewById(R.id.mReduceTitle);
        mCloseImg = view.findViewById(R.id.c_dialog_close_image);
        mReduceProportion.setText(copy);
        mCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (instance != null) {
                    instance.dismiss();
                }
                dismiss();
            }
        });
        this.setContentView(view);
        ButterKnife.bind(this, view);
    }

    public DialogIncomeTipUtil(@NonNull Context context, String title, String copy, int themeResId) {
        super(context, R.style.myDialogTheme);
        this.mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_selttle_account_layout, null);
        mReduceProportionTip = view.findViewById(R.id.mReduceProportionTip);
        mReduceProportion = view.findViewById(R.id.mReduceProportion);
        mReduceTitle = view.findViewById(R.id.mReduceTitle);
        mReduceTitle.setText(title);
        mCloseImg = view.findViewById(R.id.c_dialog_close_image);
        mReduceProportion.setText(copy);
        mCloseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (instance != null) {
                    instance.dismiss();
                }
            }
        });
        this.setContentView(view);
        ButterKnife.bind(this, view);
    }

    public DialogIncomeTipUtil build(Runnable mConfirmRunnable) {
        return build(null, mConfirmRunnable, null);
    }

    public DialogIncomeTipUtil build(Runnable mConfirmRunnable, String mConfirmButtonStr) {
        return build(null, mConfirmRunnable, mConfirmButtonStr);
    }

    public DialogIncomeTipUtil displayTitle(boolean titleDisplay) {
        mReduceTitle.setVisibility(titleDisplay ? View.VISIBLE : View.GONE);
        return instance;
    }

    public DialogIncomeTipUtil displayTip(boolean tipDisplay) {
        mReduceProportionTip.setVisibility(tipDisplay ? View.VISIBLE : View.GONE);
        return instance;
    }

    public DialogIncomeTipUtil setTitle(String mTitleStr, boolean isBold) {
        mReduceTitle.getPaint().setFakeBoldText(isBold);
        mReduceTitle.setText(mTitleStr);
        return instance;
    }

    public DialogIncomeTipUtil build(Runnable mCancelRunnable, Runnable mConfirmRunnable, String mConfirmButtonStr) {
        instance = this;
        this.mConfirmRunnable = mConfirmRunnable;
        this.mCancelRunnable = mCancelRunnable;
        WindowManager m = ((Activity) mContext).getWindowManager();
        Display d = m.getDefaultDisplay();  //为获取屏幕宽、高

        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值

//        p.height = (int) (d.getWidth() * 0.57);   //高度设置为屏幕的0.7
        p.width = (int) (d.getWidth() * 0.65);    //宽度设置为屏幕的0.8

        getWindow().setAttributes(p);     //设置生效
        show();
        return instance;
    }


    public interface OnDeleteLabelListener {
        void deleteListener(int position);
    }
}
