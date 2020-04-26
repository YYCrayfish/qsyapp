package com.manyu.videoshare.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.bean.Idcardbean;

public class RealNameStyleActivity extends BaseActivity implements View.OnClickListener {
    private TextView hint;
    private TextView name;
    private TextView identity_number;
    private TextView style;
    private Idcardbean.DataBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_style);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("实名认证");
        ImageView back = findViewById(R.id.title_back);
        hint = findViewById(R.id.hint);
        name = findViewById(R.id.name);
        identity_number = findViewById(R.id.identity_number);
        style = findViewById(R.id.style);
        back.setOnClickListener(this);
    }

    @Override
    public void initData() {
        bean = (Idcardbean.DataBean) getIntent().getSerializableExtra("data");
        hint.setText(bean.getMsg());
        name.setText(bean.getName());
        identity_number.setText(bean.getId_card());
        style.setText(getStyleStr(bean.getIs_authentication()));
    }

    public String getStyleStr(int style) {
        if (style == 0) {
            return "未实名";
        }
        if (style == 1) {
            return "审核中";
        }
        if (style == 2) {
            return "已实名";
        }
        return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                Intent intent = new Intent();
                setResult(1, intent);
                finish();
                break;
        }
    }
}
