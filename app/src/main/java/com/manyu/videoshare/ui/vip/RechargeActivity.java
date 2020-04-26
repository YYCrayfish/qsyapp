package com.manyu.videoshare.ui.vip;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.manyu.videoshare.R;
import com.manyu.videoshare.adapter.PayTypeAdapter;
import com.manyu.videoshare.adapter.RechargePayListAdapter;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.OrderBean;
import com.manyu.videoshare.bean.PayStatuBean;
import com.manyu.videoshare.bean.PayTypeBean;
import com.manyu.videoshare.bean.RecharegeTypeBean;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.dialog.SuccessDialog;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.GlideUtils;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.PayResult;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class RechargeActivity extends BaseActivity implements View.OnClickListener {
    private ImageView imgBannar;
    private RecyclerView payList;
    private RechargePayListAdapter payTypeAdapter;
    private RechargePayListAdapter payTypeAdapterVip;
    private Context context;
    private RecharegeTypeBean.DataBean typeBean = null;
    private RechargePayListAdapter selectTions = null;
    private PayTypeBean payTypeBean = null;
    private Button btnPay;
    private RecyclerView payType;
    private PayTypeAdapter paytypelistAdapter;
    private UserBean userBean;
    private ImageView headIcon;
    private TextView userName;
    private ImageView userVip;
    private TextView userDownCount;
    private TextView userVipExpire;
    private LinearLayout layoutVip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recharge);
        //ToolUtils.setBar(this);
        StatusBarUtil.setTranslucentForImageViewInFragment(RechargeActivity.this,0,null);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void initView() {
        context = this;
        imgBannar = findViewById(R.id.recharge_img_bannar);
        payList = findViewById(R.id.recharge_recycler_paylist);
        btnPay = findViewById(R.id.recharge_btn_pay);
        payType = findViewById(R.id.recharge_recycler_paytype);
        headIcon = findViewById(R.id.head_icon);
        userName = findViewById(R.id.user_name);
        userVip = findViewById(R.id.user_img_vip);
        userDownCount = findViewById(R.id.user_count);
        userVipExpire = findViewById(R.id.user_expire);
        layoutVip = findViewById(R.id.user_layout_vip);

        findViewById(R.id.title_back).setOnClickListener(this);
        btnPay.setOnClickListener(this);
        payType.setLayoutManager(new LinearLayoutManager(this));
        payList.setLayoutManager(new GridLayoutManager(this,4));
    }

    @Override
    public void initData() {
        ToolUtils.setImageMatchScreenWidth(imgBannar,-2);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserMessage();
        initVipGridview();
        initPayType();
    }
    private void startWebActivity(String url,String order) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("order",order);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }
        switch (requestCode){
            case 1:
                ToastUtils.showShort("充值成功");
                break;
            case 2:
                ToastUtils.showShort("订单正在处理中，请稍候查询");
                break;
            case 3:
                ToastUtils.showShort("充值失败");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.title_back:
                finish();
                break;
            case R.id.recharge_btn_pay:
                if(typeBean != null) {
                    order(typeBean.getList().get(selectTions.getSelection()).getId() + "", payTypeBean.getDatas().get(paytypelistAdapter.getSelection()).getPayname());
                }else{
                    ToastUtils.showShort("当前未获取到用户数据，无法购买");
                    getUserMessage();
                    initVipGridview();
                    initPayType();
                }
                //ToastUtils.showShort("当前选中：" + typeBean.getTitle() + "  需要充值多少：" + typeBean.getList().get(selectTions.getSelection()).getPrice_title()
                       // + "\n当前使用" + payTypeBean.getDatas().get(paytypelistAdapter.getSelection()).getRealname());
                break;

        }
    }


    /*private void initCountsGridview(){
        HttpUtils.httpString(Constants.VIPTYPE,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                Globals.log(e.toString());
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                RecharegeTypeBean bean = gson.fromJson(resultData,RecharegeTypeBean.class);
                if(bean.getCode() == 200){
                    if(bean.getDatas().getList().get(0).getList().size() > 1){
                        payTypeAdapter = new RechargePayListAdapter(context,bean.getDatas().getList().get(1).getList());
                        payList.setAdapter(payTypeAdapter);
                        selectTions = payTypeAdapter;
                        typeBean = bean.getDatas().getList().get(1);
                    }
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });
    }*/
    private void initVipGridview(){
        HttpUtils.httpString(Constants.VIPTYPE,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                Globals.log(e.toString());
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                RecharegeTypeBean bean = gson.fromJson(resultData,RecharegeTypeBean.class);
                if(bean.getCode() == 200){
                    if(bean.getDatas().getList().size() > 0){
                        payTypeAdapterVip = new RechargePayListAdapter(context,bean.getDatas().getList());
                        payList.setAdapter(payTypeAdapterVip);
                        selectTions = payTypeAdapterVip;
                        typeBean = bean.getDatas();
                    }
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 2:
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        //showAlert(PayDemoActivity.this, getString(R.string.pay_success) + payResult);
                        ToastUtils.showShort("支付成功");
                        getUserMessage();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        //(PayDemoActivity.this, getString(R.string.pay_failed) + payResult);
                        orderQuery(payResult.getOrder());
                    }
                    break;
            }
            return false;
        }
    });
    private void order(String vipId,String payType){
        Map<String,String> params = new HashMap<>();

        params.put("vip_id",vipId);
        params.put("payway",payType);

        HttpUtils.httpString(Constants.GETORDER,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("获取订单失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                final OrderBean bean = gson.fromJson(resultData,OrderBean.class);
                if(bean.getCode() == 200){
                    final String url = bean.getDatas().getPay_data().getToken();
                    if(url.contains("alipay")){
                        // 必须异步调用
                        Runnable payRunnable = new Runnable() {

                            @Override
                            public void run() {
                                PayTask alipay = new PayTask(RechargeActivity.this);
                                Map <String,String> result = alipay.payV2(url.replace("&amp;","&"),true);
                                result.put("order",bean.getDatas().getOrder_id());
                                Message msg = new Message();
                                msg.what = 2;
                                msg.obj = result;
                                handler.sendMessage(msg);
                            }
                        };
                        Thread payThread = new Thread(payRunnable);
                        payThread.start();
                    }else {
                        startWebActivity(url,bean.getDatas().getOrder_id());
                    }
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
                Globals.log(bean.getMsg());
                LoadingDialog.closeLoadingDialog();
                //ToastUtils.showShort(bean.getMsg());
            }
        });
    }


    private void initPayType(){
        HttpUtils.httpString(Constants.PAYTYPE,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                Globals.log(e.toString());
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                PayTypeBean bean = gson.fromJson(resultData,PayTypeBean.class);
                if(bean.getCode() == 200){
                    payTypeBean = bean;
                    Globals.log(AuthCode.authcodeDecode(bean.getData(),Constants.s));
                    paytypelistAdapter = new PayTypeAdapter(context,bean.getDatas());
                    payType.setAdapter(paytypelistAdapter);
                }else{
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });
    }
    private void setMessage(UserBean bean){
        if(!TextUtils.isEmpty(bean.getDatas().getNickname())){
            userName.setText(bean.getDatas().getNickname());
        }
        if(bean.getDatas().getAvatar().contains("http")) {
            GlideUtils.loadCircleImage(context,bean.getDatas().getAvatar(), headIcon);
        }
        userDownCount.setText(bean.getDatas().getParse_times()+"");
        if(null != bean.getDatas().getVip_end_time()){
            layoutVip.setVisibility(View.VISIBLE);
            userVip.setImageDrawable(getResources().getDrawable(R.drawable.isvip));
            userVipExpire.setText(ToolUtils.dataFormate(Integer.valueOf(bean.getDatas().getVip_end_time())));
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
    public void orderQuery(String order){
        Map<String,String> params = new HashMap<>();

        params.put("order_id",order);

        HttpUtils.httpString(Constants.ORDERQUERY,params, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("查询失败，连接不到服务器");
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                PayStatuBean bean = gson.fromJson(resultData,PayStatuBean.class);
                if(bean.getCode() == 200){
                    int back = -1;
                    String text = "";
                    switch (bean.getDatas().getStatus()){
                        case 1:
                            //ToastUtils.showShort("订单处理中");
                            text = "订单处理中";
                            back = 2;
                            break;
                        case 2:
                            back = 1;
                            text = "充值成功";
                            //ToastUtils.showShort("充值成功");
                            break;
                        case 3:
                            back = 3;
                            text = "充值失败";
                            //ToastUtils.showShort("充值失败");
                            break;
                    }
                    SuccessDialog successDialog = new SuccessDialog(RechargeActivity.this, text, new SuccessDialog.AnalysisUrlListener() {
                        @Override
                        public void analysis() {
                            //finish();
                        }
                    });
                    successDialog.show();
                    getUserMessage();
                }
                Globals.log(bean.getMsg());
                LoadingDialog.closeLoadingDialog();
                //ToastUtils.showShort(bean.getMsg());
            }
        });
    }
}
