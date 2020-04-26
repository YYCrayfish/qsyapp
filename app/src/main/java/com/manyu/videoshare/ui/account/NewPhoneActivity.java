package com.manyu.videoshare.ui.account;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseSharePerence;
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

public class NewPhoneActivity extends BaseActivity implements View.OnClickListener {
    private EditText textPhoneNum;
    private TextView btnVerify;
    private EditText editVerify;
    private Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_phone);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("更换手机号");
        textPhoneNum = findViewById(R.id.changephone_edit_phone);
        btnVerify = findViewById(R.id.changephone_btn_getverify);
        editVerify = findViewById(R.id.changephone_edit_verify);
        btnNext = findViewById(R.id.changephone_btn_next);

        btnNext.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.title_back:
                Intent intent = new Intent();
                setResult(1,intent);
                finish();
                break;
            case R.id.changephone_btn_getverify:
                String phones = textPhoneNum.getText().toString().trim();
                if(TextUtils.isEmpty(phones) || phones.length() != 11){
                    ToastUtils.showShort("请输入正确的手机号码");
                    return;
                }
                if(!ToolUtils.isChinaPhoneLegal(phones)){
                    ToastUtils.showShort("请输入正确的手机号码");
                    return;
                }
                getVerify(phones);
                //ToolUtils.setVerifyText(Constants.WAITINGTIME,btnVerify,handler);
                break;
            case R.id.changephone_btn_next:
                String phone = textPhoneNum.getText().toString().trim();
                String verify = editVerify.getText().toString().trim();
                if(TextUtils.isEmpty(verify)){
                    ToastUtils.showShort("请输入验证码");
                    return;
                }

                changePhone(phone,verify);
                break;
        }
    }
    Handler handler = new Handler();
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 0){
            finish();
        }
    }
    private void getVerify(String phone){

        Map<String,String> params = new HashMap<>();

        params.put("mobile",phone);
        params.put("smstype","3");

        HttpUtils.httpString(Constants.GETVERIFY,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("获取验证码失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                VerifyBean bean = gson.fromJson(resultData,VerifyBean.class);
                if(bean.getCode() == 200){
                    ToolUtils.setVerifyText(Constants.WAITINGTIME,btnVerify,handler);
                }
                Globals.log(bean.getMsg());
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort(bean.getMsg());
            }
        });
    }
    private void changePhone(String phone,String verify){

        Map<String,String> params = new HashMap<>();

        params.put("mobile",phone);
        params.put("smscode",verify);

        HttpUtils.httpString(Constants.CHANGEPHONE,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("修改失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                VerifyBean bean = gson.fromJson(resultData,VerifyBean.class);
                LoadingDialog.closeLoadingDialog();

                if(bean.getCode() == 200){
                    ToastUtils.showShort("更换成功，请重新登录");
                    BaseSharePerence.getInstance().setLoginKey("0");
                    Intent intent = new Intent();
                    setResult(0,intent);
                    finish();
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });
    }
}
