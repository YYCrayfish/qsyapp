package com.manyu.videoshare.ui.account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.Idcardbean;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.bean.VerifyBean;
import com.manyu.videoshare.dialog.AgreementDialog;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class AutonymActivity extends BaseActivity implements View.OnClickListener {

    private TextView userAgree;
    private TextView privacyPolicy;
    private TextView submit;
    private EditText etName;
    private EditText etIdCard;
    private String regex = "(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx])|([1−9]\\d5\\d2((0[1−9])|(10|11|12))(([0−2][1−9])|10|20|30|31)\\d2[0−9Xx])|([1−9]\\d5\\d2((0[1−9])|(10|11|12))(([0−2][1−9])|10|20|30|31)\\d2[0−9Xx])";
    private AgreementDialog agreementDialog;
    private VerifyBean bean;
    private String msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonym);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("实名认证");
        ImageView back = findViewById(R.id.title_back);
        userAgree = findViewById(R.id.user_agree);
        privacyPolicy = findViewById(R.id.privacy_policy);
        submit = findViewById(R.id.submission);
        etName = findViewById(R.id.name);
        etIdCard = findViewById(R.id.identity_number);
        back.setOnClickListener(this);
        userAgree.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
        submit.setOnClickListener(this);
        initListener();
    }

    @Override
    public void initData() {
        msg = getIntent().getStringExtra("msg");
        AlertDialog.Builder builder  = new AlertDialog.Builder(AutonymActivity.this);
        builder.setTitle("提示" ) ;
        builder.setMessage(msg) ;
        builder.setPositiveButton("确认" ,  null );
        builder.setCancelable(false);
        builder.show();
    }

    private void initListener() {
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnColor();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        etIdCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnColor();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void btnColor() {
        submit.setEnabled(false);
        try {
            if (etName.getText().toString().trim().length() < 2) {
                return;
            }
            if (!etIdCard.getText().toString().trim().matches(regex)) {
                return;
            }
            submit.setEnabled(true);
        } finally {
        }
    }

    public static void start(final Context context) {

        HttpUtils.httpString(Constants.GETIDCARD,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                Idcardbean bean = gson.fromJson(resultData, Idcardbean.class);
                if(bean.getData().getIs_authentication() != 2){
                    context.startActivity(new Intent(context, AutonymActivity.class).putExtra("msg", bean.getData().getMsg()));
                }else {
                    context.startActivity(new Intent(context, RealNameStyleActivity.class).putExtra("data", bean.getData()));
                }
                LoadingDialog.closeLoadingDialog();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                Intent intent = new Intent();
                setResult(1,intent);
                finish();
                break;
            case R.id.user_agree:
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
            case R.id.submission:
                if (etName.getText().toString().trim().length() < 2) {
                    ToastUtils.showShort(this, "姓名长度不能少于2位!");
                    return;
                }
                if (etName.getText().toString().trim().matches("[0-9a-zA-Z]*")) {
                    ToastUtils.showShort(this, "请输入中文名!");
                    return;
                }

                if (!etIdCard.getText().toString().trim().matches(regex)) {
                    ToastUtils.showShort(this, "请输入正确的身份证号码!");
                    return;
                }
                submitID();
                break;
        }
    }

    private void submitID() {
        Map<String,String> params = new HashMap<>();
        params.put("name", etName.getText().toString().trim());
        params.put("id_card", etIdCard.getText().toString().trim());
        HttpUtils.httpString(Constants.AUTONYM,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("提交失败");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                bean = gson.fromJson(resultData, VerifyBean.class);
                LoadingDialog.closeLoadingDialog();
                AlertDialog.Builder builder  = new AlertDialog.Builder(AutonymActivity.this);
                builder.setTitle("提示" ) ;
                builder.setMessage(bean.getMsg()) ;
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });
    }
}
