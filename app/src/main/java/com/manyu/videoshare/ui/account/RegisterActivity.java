package com.manyu.videoshare.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.manyu.videoshare.bean.InitAppBean;
import com.manyu.videoshare.bean.RegisterBean;
import com.manyu.videoshare.bean.VerifyBean;
import com.manyu.videoshare.dialog.AgreementDialog;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText phoneNum;
    private EditText verify;
    private EditText password;
    private TextView btnVerify;
    private Button btnRegister;
    private CheckBox agreement;
    private InitAppBean bean = null;
    private TextView btnAgreement;
    private TextView tvUserAgreement;
    private TextView tvPrivacyPolicy;
    private EditText invite;
    private AgreementDialog agreementDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText(getResources().getText(R.string.login_btn_register));
        ImageView back = findViewById(R.id.title_back);
        phoneNum = findViewById(R.id.register_edit_phone);
        password = findViewById(R.id.register_edit_password);
        verify = findViewById(R.id.register_edit_verify);
        btnVerify = findViewById(R.id.register_btn_getverify);
        btnRegister = findViewById(R.id.register_btn_register);
        agreement = findViewById(R.id.register_checkbox);
        btnAgreement = findViewById(R.id.register_btn_agreement);
        invite = findViewById(R.id.register_edit_invite);
        tvUserAgreement = findViewById(R.id.user_agreement);
        tvPrivacyPolicy = findViewById(R.id.privacy_policy);

        btnVerify.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnAgreement.setOnClickListener(this);
        tvUserAgreement.setOnClickListener(this);
        tvPrivacyPolicy.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void initData() {
        getAgreement();
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()) {
            case R.id.title_back:
                Intent intent = new Intent();
                setResult(1, intent);
                finish();
                break;
            case R.id.register_btn_register:
                String ph = phoneNum.getText().toString().trim();
                String ver = verify.getText().toString().trim();
                String ps = password.getText().toString().trim();
                if (TextUtils.isEmpty(ph) || ph.length() != 11) {
                    ToastUtils.showShort("请输入正确的手机号码");
                    return;
                }
                if (TextUtils.isEmpty(ver)) {
                    ToastUtils.showShort("验证码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(ps)) {
                    ToastUtils.showShort("密码不能为空");
                    return;
                }
                if (ps.length() < 6 || ps.length() > 20) {
                    ToastUtils.showShort("密码长度只能设置6-20位");
                    return;
                }
                if (!agreement.isChecked()) {
                    ToastUtils.showShort("必须同意用户协议才能注册!");
                    return;
                }
                register(ph, ver, ps);
                break;
            case R.id.register_btn_getverify:
                String phones = phoneNum.getText().toString().trim();
                if (TextUtils.isEmpty(phones) || phones.length() != 11) {
                    ToastUtils.showShort("请输入正确的手机号码");
                    return;
                }
                if (!ToolUtils.isChinaPhoneLegal(phones)) {
                    ToastUtils.showShort("请输入正确的手机号码");
                    return;
                }

                getVerify(phones);
                break;
            case R.id.register_btn_agreement:
            case R.id.user_agreement:
                if (null != bean) {
                    agreementDialog = new AgreementDialog(this,bean.getDatas().getAgreement().getTitle(), bean.getDatas().getAgreement().getContent());
                    agreementDialog.show();
                } else {
                    ToastUtils.showShort("APP初始化失败");
                }
                break;
            case R.id.privacy_policy:
                if (null != bean) {
                    agreementDialog = new AgreementDialog(this,bean.getDatas().getPrivacy().getTitle(), bean.getDatas().getPrivacy().getContent());
                    agreementDialog.show();
                } else {
                    ToastUtils.showShort("APP初始化失败");
                }
                break;
            default:
                break;
        }
    }

    Handler handler = new Handler();

    public void register(final String phone, String verify, final String password) {
        Map<String, String> params = new HashMap<>();

        params.put("mobile", phone);
        params.put("member_pass", password);
        params.put("smscode", verify);
        String invites = invite.getText().toString().trim();
        if (!TextUtils.isEmpty(invites)) {
            params.put("invite_code", invites);
        }
        LoadingDialog.showLoadingDialog(this);
        HttpUtils.httpString(Constants.REGISTER, params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("注册失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                LoadingDialog.closeLoadingDialog();
                Gson gson = new Gson();
                RegisterBean bean = gson.fromJson(resultData, RegisterBean.class);
                if (bean.getCode() == 200) {
                    ToastUtils.showShort("注册成功");
                    BaseSharePerence.getInstance().setLoginKey(bean.getData().getToken());
                    BaseSharePerence.getInstance().setLoginTime(System.currentTimeMillis());
                    BaseSharePerence.getInstance().setLoginName(phone);
                    BaseSharePerence.getInstance().setLoginPwd(password);
                    Intent intent = new Intent();
                    setResult(0, intent);
                    finish();
                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
                Globals.log(bean.getMsg());
            }
        });
    }

    private void getVerify(String phone) {

        Map<String, String> params = new HashMap<>();

        params.put("mobile", phone);
        params.put("smstype", "1");

        HttpUtils.httpString(Constants.GETVERIFY, params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("获取验证码失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                VerifyBean bean = gson.fromJson(resultData, VerifyBean.class);
                if (bean.getCode() == 200) {
                    ToolUtils.setVerifyText(Constants.WAITINGTIME, btnVerify, handler);
                }
                Globals.log(bean.getMsg());
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort(bean.getMsg());
            }
        });
    }

    private void getAgreement() {


        HttpUtils.httpString(Constants.INITAPP, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("APP初始化失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                bean = gson.fromJson(resultData, InitAppBean.class);
                Globals.log(bean.getMsg());
                LoadingDialog.closeLoadingDialog();
                //ToastUtils.showShort(bean.getMsg());
            }
        });
    }
}
