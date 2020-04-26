package com.manyu.videoshare.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToolUtils;

public class AccountSafityAcitivty extends BaseActivity implements View.OnClickListener {
    private LinearLayout btnChangePS;
    private LinearLayout btnChangePhone;
    private LinearLayout btnAutonym;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_safity_acitivty);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("账号安全");
        ImageView back = findViewById(R.id.title_back);
        btnChangePS = findViewById(R.id.safity_btn_changepassword);
        btnChangePhone = findViewById(R.id.safity_btn_changephone);
        btnAutonym = findViewById(R.id.safity_btn_autonym);
        back.setOnClickListener(this);
        btnChangePhone.setOnClickListener(this);
        btnChangePS.setOnClickListener(this);
        btnAutonym.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()) {
            case R.id.safity_btn_changepassword:
                IntentUtils.JumpActivity(this, ChangePassowrdActivity.class, 0);
                break;
            case R.id.safity_btn_changephone:
                IntentUtils.JumpActivity(this, ChangePhoneActivity.class, 0);
                break;
            case R.id.safity_btn_autonym:
                AutonymActivity.start(this);
                break;
            case R.id.title_back:
                finish();
                break;
        }
    }
}
