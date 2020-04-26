package com.manyu.videoshare.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.RegisterBean;
import com.manyu.videoshare.dialog.AgreementDialog;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class LoginAcitivty extends BaseActivity implements View.OnClickListener {
    private EditText phoneNum;
    private EditText password;
    private CheckBox checkBox;
    private TextView forgetPassword;
    private Button login;
    private Button register;
    private TextView tvUserAgreement;
    private TextView tvPrivacyPolicy;
    private AgreementDialog agreementDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_acitivty);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText(getResources().getText(R.string.login_btn_login));
        ImageView back = findViewById(R.id.title_back);
        phoneNum = findViewById(R.id.login_edit_phone);
        password = findViewById(R.id.login_edit_pass);
        checkBox = findViewById(R.id.login_checkbox);
        forgetPassword = findViewById(R.id.login_btn_forget);
        login = findViewById(R.id.login_btn_login);
        register = findViewById(R.id.login_btn_register);
        tvUserAgreement = findViewById(R.id.user_agreement);
        tvPrivacyPolicy = findViewById(R.id.privacy_policy);
        back.setOnClickListener(this);
        forgetPassword.setOnClickListener(this);
        login.setOnClickListener(this);
        register.setOnClickListener(this);
        tvUserAgreement.setOnClickListener(this);
        tvPrivacyPolicy.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if(!TextUtils.isEmpty(BaseSharePerence.getInstance().getLoginName())){
            phoneNum.setText(BaseSharePerence.getInstance().getLoginName());
            password.setFocusable(true);
            password.setFocusableInTouchMode(true);
        }
        if(!TextUtils.isEmpty(BaseSharePerence.getInstance().getLoginPwd())){
            password.setText(BaseSharePerence.getInstance().getLoginPwd());
            password.invalidate();
        }
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.login_btn_forget:
                IntentUtils.JumpActivity(this,ForgetPasswordActivity.class);
                break;
            case R.id.login_btn_login:
                String phone = phoneNum.getText().toString().trim();
                String ps = password.getText().toString().trim();
                if(TextUtils.isEmpty(phone)){
                    ToastUtils.showShort("账号不能为空");
                    return;
                }
                if(TextUtils.isEmpty(ps)){
                    ToastUtils.showShort("密码不能为空");
                    return;
                }
                login(phone,ps);
                break;
            case R.id.login_btn_register:
                IntentUtils.JumpActivity(this,RegisterActivity.class,0);
                break;
            case R.id.user_agreement:
                if (null != BaseSharePerence.getInstance().getUserAgree()) {
                    agreementDialog = new AgreementDialog(this,"用户协议", BaseSharePerence.getInstance().getUserAgree());
                    agreementDialog.show();
                } else {
                    ToastUtils.showShort("APP初始化失败");
                }
                break;
            case R.id.privacy_policy:
                if (null != BaseSharePerence.getInstance().getPrivacyPolicy()) {
                    agreementDialog = new AgreementDialog(this,"隐私政策", BaseSharePerence.getInstance().getPrivacyPolicy());
                    agreementDialog.show();
                } else {
                    ToastUtils.showShort("APP初始化失败");
                }
                break;
                default:
                    break;
        }
    }

    public void login(final String phone,final String ps){
        Map<String,String> params = new HashMap<>();
        params.put("mobile",phone);
        params.put("member_pass",ps);
        LoadingDialog.showLoadingDialog(this);
        HttpUtils.httpString(Constants.LOGIN,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("登录失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                LoadingDialog.closeLoadingDialog();
                Gson gson = new Gson();
                RegisterBean bean = gson.fromJson(resultData,RegisterBean.class);
                if(bean.getCode() == 200){
                    ToastUtils.showShort("登录成功");
                    BaseSharePerence.getInstance().setLoginKey(bean.getData().getToken());
                    BaseSharePerence.getInstance().setLoginTime(System.currentTimeMillis());
                    BaseSharePerence.getInstance().setLoginName(phone);
                    if(checkBox.isChecked()){
                        BaseSharePerence.getInstance().setLoginPwd(ps);
                    }else{
                        BaseSharePerence.getInstance().setLoginPwd("");
                    }
                    finish();
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
                Globals.log(bean.getMsg());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            finish();
        }
    }
}
