package com.manyu.videoshare.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.RegisterBean;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class ChangeUserNameActivity extends BaseActivity implements View.OnClickListener {
    private EditText editName;
    private TextView textNum;
    private Button btnConfirm;
    private UserBean userBean;
    private ImageView btnDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_name);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("修改昵称");
        editName = findViewById(R.id.username_edit_name);
        textNum = findViewById(R.id.username_text_num);
        btnConfirm = findViewById(R.id.username_btn_confirm);
        btnDelete = findViewById(R.id.username_btn_delete);

        btnConfirm.setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        editName.setText(getIntent().getStringExtra("name"));
        textNum.setText(ToolUtils.getTextLengh(editName.getText().toString())+"");
    }

    @Override
    public void initData() {

        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textNum.setText(ToolUtils.getTextLengh(editName.getText().toString())+"");
                if(editName.getText().toString().length()>0){
                    btnDelete.setVisibility(View.VISIBLE);
                }else{
                    btnDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.username_btn_confirm:
                String name = editName.getText().toString();
                if(TextUtils.isEmpty(name) || ToolUtils.getTextLengh(name) < 6 || ToolUtils.getTextLengh(name) > 16){
                    ToastUtils.showShort("昵称长度必须超过6个字符且不能超过16个字符");
                    return;
                }
                Intent intent = new Intent();
                intent.putExtra("name",name);
                setResult(RESULT_OK,intent);
                finish();
                //changeName(name);
                break;
            case R.id.username_btn_delete:
                editName.setText("");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //getUserMessage();
    }

    public void changeName(String name){
        Map<String,String> params = new HashMap<>();
        params.put("nickname",name);
        params.put("sex",userBean.getDatas().getSex()+"");
        params.put("birthday",userBean.getDatas().getBirthday());
        params.put("signature",userBean.getDatas().getSignature());
        LoadingDialog.showLoadingDialog(this);
        HttpUtils.httpString(Constants.UPDATEMESSAGE,params, new HttpUtils.HttpCallback() {
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
                    ToastUtils.showShort("修改成功");
                    finish();
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
                Globals.log(bean.getMsg());
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
                userBean = gson.fromJson(resultData,UserBean.class);
                Globals.log(AuthCode.authcodeDecode(userBean.getData(),Constants.s));
                editName.setText(userBean.getDatas().getNickname());
                LoadingDialog.closeLoadingDialog();
            }
        });
    }
}
