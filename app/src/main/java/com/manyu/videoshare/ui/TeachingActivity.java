package com.manyu.videoshare.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.util.ToolUtils;

public class TeachingActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgStepOne;
    private ImageView imgStepTwo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teaching);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("简单教程");
        imgStepOne = findViewById(R.id.teaching_img_a);
        imgStepTwo = findViewById(R.id.teaching_img_b);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    @Override
    public void initData() {
        ToolUtils.setImageMatchScreenWidth(imgStepOne,32);
        ToolUtils.setImageMatchScreenWidth(imgStepTwo,32);
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
        }
    }
}
