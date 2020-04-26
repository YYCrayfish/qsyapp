package com.manyu.videoshare.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.util.ToolUtils;

public class AboutUsActivity extends BaseActivity implements View.OnClickListener {
    private TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("关于我们");
        ImageView back = findViewById(R.id.title_back);
        version = findViewById(R.id.about_version);

        String versionName = ToolUtils.getVersionName(AboutUsActivity.this);
        version.setText("V" + versionName);
        back.setOnClickListener(this);
    }

    @Override
    public void initData() {

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
