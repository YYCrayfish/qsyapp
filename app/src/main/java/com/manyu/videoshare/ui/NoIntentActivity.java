package com.manyu.videoshare.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.io.IOException;

public class NoIntentActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_intent);
        ToolUtils.setBar(this);
    }

    public void initView() {
        ImageView imageView = findViewById(R.id.nointent_btn);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ToolUtils.havingIntents()){
                    finish();
                }else{
                    ToastUtils.showShort("当前连接不到服务器，请检查网络");
                }
            }
        });
    }

    public void initData() {

    }
}
