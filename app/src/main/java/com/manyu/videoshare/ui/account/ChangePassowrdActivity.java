package com.manyu.videoshare.ui.account;

import android.content.Intent;
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

public class ChangePassowrdActivity extends BaseActivity implements View.OnClickListener {
    private EditText editOldPs;
    private EditText editNewPs;
    private EditText editReNewPs;
    private Button btnConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passowrd);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        findViewById(R.id.title_back).setOnClickListener(this);
        editNewPs = findViewById(R.id.change_edit_password);
        editOldPs = findViewById(R.id.change_edit_oldps);
        editReNewPs = findViewById(R.id.change_edit_repassword);
        btnConfirm = findViewById(R.id.change_btn_confirm);

        btnConfirm.setOnClickListener(this);
        tv.setText("账号安全");
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.change_btn_confirm:
                String oldps = editOldPs.getText().toString().trim();
                String newps = editNewPs.getText().toString().trim();
                String renewps = editReNewPs.getText().toString().trim();
                if(TextUtils.isEmpty(oldps)){
                    ToastUtils.showShort("旧密码不能为空");
                    return;
                }
                if(TextUtils.isEmpty(newps)){
                    ToastUtils.showShort("新密码不能为空");
                    return;
                }
                if(newps.length() < 6 || newps.length() > 20){
                    ToastUtils.showShort("密码长度只能设置6-20位");
                    return;
                }
                if(TextUtils.isEmpty(renewps)){
                    ToastUtils.showShort("请再次输入新密码");
                    return;
                }
                if(renewps.length() < 6 || renewps.length() > 20){
                    ToastUtils.showShort("密码长度只能设置6-20位");
                    return;
                }
                if(!newps.equals(renewps)){
                    ToastUtils.showShort("两次密码不一致，请重新输入");
                    return;
                }
                register(oldps,newps);
                break;
            case R.id.title_back:
                Intent intent = new Intent();
                setResult(1,intent);
                finish();
                return;
        }
    }
    public void register(String oldPs,String newPs){
        Map<String,String> params = new HashMap<>();

        params.put("old_pwd",oldPs);
        params.put("new_pwd",newPs);
        LoadingDialog.showLoadingDialog(this);
        HttpUtils.httpString(Constants.CHANGEPS,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("修改密码失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                LoadingDialog.closeLoadingDialog();
                Gson gson = new Gson();
                VerifyBean bean = gson.fromJson(resultData,VerifyBean.class);
                if(bean.getCode() == 200){
                    ToastUtils.showShort("密码修改成功，请重新登录！");
                    BaseSharePerence.getInstance().setLoginKey("0");
                    Intent intent = new Intent();
                    setResult(0,intent);
                    finish();
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
                Globals.log(bean.getMsg());
            }
        });
    }
}
