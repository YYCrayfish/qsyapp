package com.manyu.videoshare.ui.account;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.bean.VersionBean;
import com.manyu.videoshare.dialog.AgreementDialog;
import com.manyu.videoshare.dialog.ExitDialog;
import com.manyu.videoshare.dialog.UpdateDialog;
import com.manyu.videoshare.ui.AboutUsActivity;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import okhttp3.Call;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private Button btnExit;
    private LinearLayout btnVersion;
    private LinearLayout btnAbout;
    private LinearLayout btnPrivacyPolicy;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ImmersionBar.with(this).statusBarColorInt(getResources().getColor(R.color.white)).statusBarDarkFont(true).init();
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        context = this;
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("设置");
        ImageView back = findViewById(R.id.title_back);
        btnExit = findViewById(R.id.setting_btn_exit);
        btnVersion = findViewById(R.id.setting_btn_version);
        btnAbout = findViewById(R.id.setting_btn_about);
        btnPrivacyPolicy = findViewById(R.id.setting_privacy_policy);
        back.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnVersion.setOnClickListener(this);
        btnAbout.setOnClickListener(this);
        btnPrivacyPolicy.setOnClickListener(this);
    }

    @Override
    public void initData() {
        if (BaseSharePerence.getInstance().getLoginKey().equals("0")) {
            btnExit.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()) {
            case R.id.setting_btn_exit:
                ExitDialog exitDialog = new ExitDialog(context, null, new ExitDialog.AnalysisUrlListener() {
                    @Override
                    public void analysis() {
                        BaseSharePerence.getInstance().setLoginKey("0");
                        finish();
                    }

                    @Override
                    public void clean() {

                    }
                });
                exitDialog.show();
                break;
            case R.id.title_back:
                finish();
                break;
            case R.id.setting_btn_version:
                checkVersion();
                break;
            case R.id.setting_btn_about:
                IntentUtils.JumpActivity(this, AboutUsActivity.class);
                break;
            case R.id.setting_privacy_policy:
                if (null != BaseSharePerence.getInstance().getPrivacyPolicy()) {
                    AgreementDialog agreementDialog = new AgreementDialog(this, "隐私政策", BaseSharePerence.getInstance().getPrivacyPolicy());
                    agreementDialog.show();
                } else {
                    ToastUtils.showShort("APP初始化失败");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    public void checkVersion() {
        HttpUtils.httpString(Constants.VERSION, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                VersionBean bean = gson.fromJson(resultData, VersionBean.class);
                if (bean.getCode() == 200) {
                    String versionName = ToolUtils.getVersionName(SettingActivity.this);
                    VersionBean.DataBean dataBean = bean.getDatas();
                    if (null == dataBean) {
                        ToastUtils.showShort("当前已经是最新版本了！");
                        return;
                    }
                    int compare = versionName.compareTo(dataBean.getVersions());
                    if (compare < 0) {
                        //ToastUtils.showShort("有最新版本！");
                        UpdateDialog updateDialog = new UpdateDialog(SettingActivity.this, dataBean, 0);
                        updateDialog.show();
                    }
                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });
    }
}
