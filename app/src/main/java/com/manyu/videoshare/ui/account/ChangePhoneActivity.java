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
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.bean.VerifyBean;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ChangePhoneActivity extends BaseActivity implements View.OnClickListener {
    private TextView textPhoneNum;
    private TextView btnVerify;
    private EditText editVerify;
    private Button btnNext;
    private UserBean bean = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("更换手机号");
        textPhoneNum = findViewById(R.id.changephone_edit_phonenum);
        btnVerify = findViewById(R.id.changephone_btn_getverify);
        editVerify = findViewById(R.id.changephone_edit_verify);
        btnNext = findViewById(R.id.changephone_btn_next);

        btnNext.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
    }

    @Override
    public void initData() {
        getUserMessage();
    }
    Handler handler = new Handler();

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
                if(null != bean) {
                    getVerify(bean.getDatas().getMobile());
                    //ToolUtils.setVerifyText(Constants.WAITINGTIME,btnVerify,handler);
                }
                break;
            case R.id.changephone_btn_next:
                String verify = editVerify.getText().toString().trim();
                if(TextUtils.isEmpty(verify)){
                    ToastUtils.showShort("请输入验证码");
                    return;
                }

                checkVerify(verify,bean.getDatas().getMobile());
                break;
        }
    }

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
        params.put("smstype","4");

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
    private void getUserMessage(){

        HttpUtils.httpString(Constants.USERMESSAGE,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("获取信息失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                Globals.log(resultData);
                bean = gson.fromJson(resultData,UserBean.class);
                Globals.log(AuthCode.authcodeDecode(bean.getData(),Constants.s));
                String nums = bean.getDatas().getMobile();
                String text = nums.substring(0,3) + "****" + nums.substring(7,nums.length());
                textPhoneNum.setText(text);
                LoadingDialog.closeLoadingDialog();
            }
        });
    }
    private void checkVerify(String verify,String phone){

        Map<String,String> params = new HashMap<>();

        params.put("mobile",phone);
        params.put("smstype","4");
        params.put("smscode",verify);

        HttpUtils.httpString(Constants.CHECKVERIFY,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("验证失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                VerifyBean bean = gson.fromJson(resultData,VerifyBean.class);
                Globals.log(bean.getMsg());
                LoadingDialog.closeLoadingDialog();
                if(bean.getCode() == 200){
                    jumps();
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }

            }
        });
    }
    public void jumps(){
        IntentUtils.JumpActivity(this,NewPhoneActivity.class,0);
    }
}
