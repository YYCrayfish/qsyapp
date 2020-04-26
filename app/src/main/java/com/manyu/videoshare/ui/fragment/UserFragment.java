package com.manyu.videoshare.ui.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseFragment;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.ui.FeedBackActivity;
import com.manyu.videoshare.ui.MainActivity;
import com.manyu.videoshare.ui.ShareActivity;
import com.manyu.videoshare.ui.account.AccountSafityAcitivty;
import com.manyu.videoshare.ui.account.LoginAcitivty;
import com.manyu.videoshare.ui.account.SettingActivity;
import com.manyu.videoshare.ui.user.UserMessageActivity;
import com.manyu.videoshare.ui.vip.RechargeActivity;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.GlideUtils;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class UserFragment extends BaseFragment implements View.OnClickListener {
    private View view;
    private TextView btnLogin;
    private LinearLayout btnSetting;
    private LinearLayout btnSafity;
    private LinearLayout btnRecharge;
    private LinearLayout btnFeedBack;
    private UserBean userBean;
    private ImageView headIcon;
    private MainActivity activity;
    private ImageView userVip;
    private TextView userDownCount;
    private TextView userVipExpire;
    private LinearLayout btnShare;
    private LinearLayout layoutCounts;
    private LinearLayout layoutInvite;
    private LinearLayout layoutHead;
    private TextView textVipKnow;
    public UserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user, container, false);
        activity = (MainActivity) getActivity();
        initView();
        initData();
        return view;
    }

    private void initView(){
        btnLogin = view.findViewById(R.id.user_btn_login);
        btnSetting = view.findViewById(R.id.user_btn_setting);
        btnSafity = view.findViewById(R.id.user_btn_safity);
        btnRecharge = view.findViewById(R.id.user_btn_recharge);
        btnFeedBack = view.findViewById(R.id.user_btn_feedback);
        headIcon = view.findViewById(R.id.head_icon);
        userVip = view.findViewById(R.id.user_img_vip);
        userDownCount = view.findViewById(R.id.user_count);
        userVipExpire = view.findViewById(R.id.user_expire);
        btnShare = view.findViewById(R.id.user_btn_share);
        layoutInvite = view.findViewById(R.id.user_layout_invite);
        layoutCounts = view.findViewById(R.id.user_layout_counts);
        layoutHead = view.findViewById(R.id.user_layout_head);
        textVipKnow = view.findViewById(R.id.user_text_vipknow);

        layoutHead.setOnClickListener(this);
        btnFeedBack.setOnClickListener(this);
        btnRecharge.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnSafity.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        layoutInvite.setOnClickListener(this);
    }

    private void initData(){
        int wid = ToolUtils.getScreenWidth() - ToolUtils.dip2px(32);
        LinearLayout.LayoutParams layoutParamsInvite = new LinearLayout.LayoutParams(wid/2,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutInvite.setLayoutParams(layoutParamsInvite);
        layoutCounts.setLayoutParams(layoutParamsInvite);
    }
    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(getActivity());
        switch (v.getId()){
            case R.id.user_layout_invite:
                if(!BaseSharePerence.getInstance().getLoginKey().equals("0") && userBean != null && null == userBean.getDatas().getVip_end_time()){
                    IntentUtils.JumpActivity(getActivity(), RechargeActivity.class);
                }
                break;
            case R.id.user_layout_head:
                if(BaseSharePerence.getInstance().getLoginKey().equals("0")){
                    IntentUtils.JumpActivity(getActivity(), LoginAcitivty.class);
                }else{
                    IntentUtils.JumpActivity(getActivity(), UserMessageActivity.class);
                }
                break;
            /*case R.id.user_btn_login:
                if(btnLogin.getText().toString().trim().equals("点击登录")) {
                    IntentUtils.JumpActivity(getActivity(), LoginAcitivty.class);
                }
                break;*/
            case R.id.user_btn_setting:
                IntentUtils.JumpActivity(getActivity(), SettingActivity.class);
                break;
            case R.id.user_btn_safity:
                if(BaseSharePerence.getInstance().getLoginKey().equals("0")){
                    IntentUtils.JumpActivity(getActivity(), LoginAcitivty.class);
                }else {
                    IntentUtils.JumpActivity(getActivity(), AccountSafityAcitivty.class);
                }
                break;
            case R.id.user_btn_recharge:
                if(BaseSharePerence.getInstance().getLoginKey().equals("0")){
                    IntentUtils.JumpActivity(getActivity(), LoginAcitivty.class);
                }else {
                    IntentUtils.JumpActivity(getActivity(), RechargeActivity.class);
                }
                break;
            case R.id.user_btn_feedback:
                IntentUtils.JumpActivity(getActivity(),FeedBackActivity.class);
                break;
            case R.id.user_message:
                IntentUtils.JumpActivity(getActivity(),UserMessageActivity.class);
                break;
            case R.id.user_btn_share:
                if(BaseSharePerence.getInstance().getLoginKey().equals("0")){
                    IntentUtils.JumpActivity(getActivity(), LoginAcitivty.class);
                }else {
                    IntentUtils.JumpActivity(getActivity(), ShareActivity.class);
                }
                break;
            /*case R.id.head_icon:
                if(!BaseSharePerence.getInstance().getLoginKey().equals("0")) {
                    IntentUtils.JumpActivity(getActivity(), UserMessageActivity.class);
                }
                break;*/
                default:
                    break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != activity && activity.isShowMain()){
            return;
        }
        checkLogin();
        getUserMessage();
    }
    public void checkLogin(){
        String token = BaseSharePerence.getInstance().getLoginKey();
        if(!token.equals("0")){
            userVip.setVisibility(View.GONE);
        }else{
            headIcon.setImageDrawable(getResources().getDrawable(R.drawable.user_head_default));
            btnLogin.setText("点击登录");
            userVip.setVisibility(View.GONE);
            userBean = null;
            textVipKnow.setText("会员到期时间");
            userVipExpire.setText("-");
            userDownCount.setText("-");
        }
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
                setMessage(userBean);


                LoadingDialog.closeLoadingDialog();
            }
        });
    }
    private void setMessage(UserBean bean){
        if(null == bean.getDatas()){
            return;
        }
        if(!TextUtils.isEmpty(bean.getDatas().getNickname())){
            btnLogin.setText(bean.getDatas().getNickname());
        }
        if(bean.getDatas().getAvatar().contains("http")) {
            GlideUtils.loadCircleImage(getActivity(),bean.getDatas().getAvatar(), headIcon);
        }
        userDownCount.setText(bean.getDatas().getParse_times()+"");
        if(null != bean.getDatas().getVip_end_time()){
            //layoutVip.setVisibility(View.VISIBLE);
            userVip.setImageDrawable(getResources().getDrawable(R.drawable.isvip));
            userVipExpire.setText(ToolUtils.dataFormate(Integer.valueOf(bean.getDatas().getVip_end_time())));
            textVipKnow.setText("会员到期时间");
        }else{
            userVipExpire.setText("开通VIP会员");
            //layoutVip.setVisibility(View.GONE);
            textVipKnow.setText("专享无限解析次数");
            userVip.setImageDrawable(getResources().getDrawable(R.drawable.unvip));
        }
    }
}
