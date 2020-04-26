package com.manyu.videoshare.ui.account;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.RegisterBean;
import com.manyu.videoshare.bean.VerifyBean;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText editphone;
    private EditText edidverify;
    private EditText editPassword;
    private EditText editRepassowrd;
    private TextView btnVerify;
    private Button btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText(getResources().getText(R.string.forget_title));
        ImageView back = findViewById(R.id.title_back);
        editphone = findViewById(R.id.forger_edit_phone);
        edidverify = findViewById(R.id.forger_edit_verify);
        editPassword = findViewById(R.id.forger_edit_password);
        editRepassowrd = findViewById(R.id.forget_edit_repassword);
        btnVerify = findViewById(R.id.forger_btn_getverify);
        btnConfirm = findViewById(R.id.forger_btn_confirm);

        btnConfirm.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
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
            case R.id.forger_btn_confirm:
                String ph = editphone.getText().toString().trim();
                String ver = edidverify.getText().toString().trim();
                String ps = editPassword.getText().toString().trim();
                String reps = editRepassowrd.getText().toString().trim();
                if(TextUtils.isEmpty(ph) || ph.length() != 11){
                    ToastUtils.showShort("请输入正确的手机号码");
                    return;
                }
                if(TextUtils.isEmpty(ver)){
                    ToastUtils.showShort("验证码不能为空");
                    return;
                }
                if(TextUtils.isEmpty(ps)){
                    ToastUtils.showShort("密码不能为空");
                    return;
                }
                if(ps.length() < 6 || ps.length() > 20){
                    ToastUtils.showShort("密码长度只能设置6-20位");
                    return;
                }
                if(reps.length() < 6 || reps.length() > 20){
                    ToastUtils.showShort("密码长度只能设置6-20位");
                    return;
                }
                if(!ps.equals(reps)){
                    ToastUtils.showShort("前后两次密码不一致！");
                    return;
                }
                register(ph,ver,ps);
                break;
            case R.id.forger_btn_getverify:
                String phones = editphone.getText().toString().trim();
                if(TextUtils.isEmpty(phones) || phones.length() != 11){
                    ToastUtils.showShort("请输入正确的手机号码");
                    return;
                }
                if(!ToolUtils.isChinaPhoneLegal(phones)){
                    ToastUtils.showShort("请输入正确的手机号码");
                    return;
                }
                //ToolUtils.setVerifyText(Constants.WAITINGTIME,btnVerify,handler);
                getVerify(phones);
                break;
        }
    }
    Handler handler = new Handler();
    public void register(String phone,String verify,String password){
        Map<String,String> params = new HashMap<>();

        params.put("mobile",phone);
        params.put("member_pass",password);
        params.put("smscode",verify);
        LoadingDialog.showLoadingDialog(this);
        HttpUtils.httpString(Constants.FORGET,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("注册失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                LoadingDialog.closeLoadingDialog();
                Gson gson = new Gson();
                RegisterBean bean = gson.fromJson(resultData,RegisterBean.class);
                if(bean.getCode() == 200){
                    ToastUtils.showShort(bean.getMsg());
                    BaseSharePerence.getInstance().setLoginKey("0");
                    finish();
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
                Globals.log(bean.getMsg());
            }
        });
    }
    private void getVerify(String phone){

        Map<String,String> params = new HashMap<>();

        params.put("mobile",phone);
        params.put("smstype","2");

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
}
