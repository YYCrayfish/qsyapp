package com.manyu.videoshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.manyu.videoshare.R;


public class AgreeDialog extends Dialog implements View.OnClickListener {
    private TextView userAgree;
    private OnClickListener listener;
    private TextView privacyPolicy;
    private TextView yes;
    private TextView no;

    public AgreeDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
    }

    public AgreeDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_agree);
        setCanceledOnTouchOutside(false);
        initView();
    }

    private void initView() {
        userAgree = findViewById(R.id.user_agree);
        privacyPolicy = findViewById(R.id.privacy_policy);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        userAgree.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (userAgree == view) {
            listener.onUserAgree();
        } else if (privacyPolicy == view) {
            listener.onPrivacyPolicy();
        } else if (yes == view) {
            listener.onYes();
        } else if (no == view) {
            listener.onNo();
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.listener = onClickListener;
    }

    public interface OnClickListener {
        abstract void onUserAgree();

        abstract void onPrivacyPolicy();

        abstract void onYes();

        abstract void onNo();
    }
}
