package com.manyu.videoshare.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.VerifyBean;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class FeedBackActivity extends BaseActivity implements View.OnClickListener {
    private EditText feedText;
    private EditText feedNum;
    private Button btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("意见反馈");
        findViewById(R.id.title_back).setOnClickListener(this);
        feedText = findViewById(R.id.feedback_edit_opinion);
        feedNum = findViewById(R.id.feedback_edit_num);
        btnConfirm = findViewById(R.id.feedback_btn_confirm);

        btnConfirm.setOnClickListener(this);
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
            case R.id.feedback_btn_confirm:
                String content = feedText.getText().toString();
                String nums = feedNum.getText().toString();
                if(TextUtils.isEmpty(content)){
                    ToastUtils.showShort("反馈意见不能为空");
                    return;
                }
                confirmFeedBack(content,nums);
                break;
        }
    }
    private void confirmFeedBack(String content,String contact){

        Map<String,String> params = new HashMap<>();

        params.put("content",content);
        if(!TextUtils.isEmpty(contact)) {
            params.put("contact", contact);
        }

        HttpUtils.httpString(Constants.FEEDBACK,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("提交失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                VerifyBean bean = gson.fromJson(resultData,VerifyBean.class);
                Globals.log(bean.getMsg());
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort(bean.getMsg());
                if(bean.getCode() == 200){
                    finish();
                }

            }
        });
    }
}
