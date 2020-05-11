package com.manyu.videoshare.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.adapter.ShareLevelAdapter;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.InviteBean;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.dialog.ShareDialog;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import okhttp3.Call;

public class ShareActivity extends BaseActivity implements View.OnClickListener {
    private ShareLevelAdapter shareLevelAdapter;
    private RecyclerView recyclerView;
    private Button btnShare;
    private TextView btnInvite;
    private ShareActivity context;
    private UserBean userBean;
    private TextView textPhone;
    private TextView textInvite;
    private TextView textInviteCount;
    private InviteBean bean;
    private ImageView img;
    private TextView current;
    private TextView total;
    private TextView currentLevel;
    private TextView nextMsg;
    private TextView mRuleExplain;
    private LinearLayout layoutCounts;
    private LinearLayout layoutInvite;
    private LinearLayout imgZxing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        context = this;
        TextView tv = findViewById(R.id.title_tv);
        tv.setText("邀请有礼");
        recyclerView = findViewById(R.id.share_recycle);
        btnShare = findViewById(R.id.share_btn_invite);
        btnInvite = findViewById(R.id.share_text_invite);
        textPhone = findViewById(R.id.share_text_phone);
        mRuleExplain = findViewById(R.id.share_rule_explain);
        textInviteCount = findViewById(R.id.share_text_invite_count);
        img = findViewById(R.id.share_test);
        current = findViewById(R.id.share_text_current);
        total = findViewById(R.id.share_text_total);
        currentLevel = findViewById(R.id.share_text_level);
        nextMsg = findViewById(R.id.share_text_nextmsg);
        layoutInvite = findViewById(R.id.share_layout_invite);
        layoutCounts = findViewById(R.id.share_layout_counts);
        imgZxing = findViewById(R.id.share_img_zxing);

        findViewById(R.id.title_back).setOnClickListener(this);
        btnShare.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnInvite.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ToolUtils.setClipData(btnInvite.getText().toString());
                ToastUtils.showShort("已经复制邀请码到粘贴板了");
                return false;
            }
        });
        imgZxing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("url", bean.getDatas().getInvite_url());
                bundle.putString("code", userBean.getDatas().getInvite_code());
                IntentUtils.JumpActivity(context, ShareImageActivity.class, bundle);
            }
        });
    }

    @Override
    public void initData() {
        int wid = ToolUtils.getScreenWidth() - ToolUtils.dip2px(32);
        LinearLayout.LayoutParams layoutParamsInvite = new LinearLayout.LayoutParams(wid / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutInvite.setLayoutParams(layoutParamsInvite);
        layoutCounts.setLayoutParams(layoutParamsInvite);
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.share_btn_invite:
                Bundle bundle = new Bundle();
                bundle.putString("url", bean.getDatas().getInvite_url());
                bundle.putString("code", userBean.getDatas().getInvite_code());
                IntentUtils.JumpActivity(context, ShareImageActivity.class, bundle);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserMessage();
        getList();
    }

    private void share() {
        ShareDialog shareDialog = new ShareDialog(this, new ShareDialog.AnalysisUrlListener() {
            @Override
            public void analysis(int position) {
                switch (position) {
                    case 0:
                        if (!ToolUtils.isAvilible(context, "com.tencent.mobileqq")) {
                            ToastUtils.showShort("当前手机没有安装QQ，请先安装后在分享！");
                            return;
                        }
                        ToolUtils.sharedQQ(context);
                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:

                        break;

                }
            }

            @Override
            public void close() {

            }
        });
        shareDialog.show();
    }

    private void getUserMessage() {

        HttpUtils.httpString(Constants.USERMESSAGE, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("获取信息失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                Globals.log(resultData);
                userBean = gson.fromJson(resultData, UserBean.class);
                Globals.log(AuthCode.authcodeDecode(userBean.getData(), Constants.s));
                setMessage(userBean);


                LoadingDialog.closeLoadingDialog();
            }
        });
    }

    private void setMessage(UserBean userBean) {
        String nums = userBean.getDatas().getMobile();
        String text = nums.substring(0, 3) + "****" + nums.substring(7, nums.length());
        textPhone.setText(text);
        textInvite.setText(userBean.getDatas().getInvite_code());

    }


    private void getList() {

        HttpUtils.httpString(Constants.INVITE, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                //LoadingDialog.closeLoadingDialog();
                //ToastUtils.showShort("获取验证码失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                bean = gson.fromJson(resultData, InviteBean.class);
                if (null != bean) {
                    shareLevelAdapter = new ShareLevelAdapter(context, bean.getDatas().getShare_level());
                    recyclerView.setAdapter(shareLevelAdapter);
                    setInvite(bean);

                }
                Globals.log(bean.getMsg());
                //LoadingDialog.closeLoadingDialog();
                //ToastUtils.showShort(bean.getMsg());
            }
        });
    }

    private void setInvite(InviteBean bean) {
        if (bean == null) {
            return;
        }
        current.setText(String.valueOf(bean.getDatas().getInvite_level().getParse_times()));
        total.setText(String.valueOf(bean.getDatas().getInvite_level().getParse_times_total()));
        currentLevel.setText(bean.getDatas().getInvite_level().getCurrent_level());
        nextMsg.setText(bean.getDatas().getInvite_level().getNext_msg());
        mRuleExplain.setText(bean.getDatas().getMsg());
        textInviteCount.setText(String.valueOf(bean.getDatas().getInvite_level().getInvite_count()));
    }
}
