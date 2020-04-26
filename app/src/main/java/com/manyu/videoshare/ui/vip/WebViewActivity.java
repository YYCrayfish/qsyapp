package com.manyu.videoshare.ui.vip;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.PayStatuBean;
import com.manyu.videoshare.dialog.SuccessDialog;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class WebViewActivity extends BaseActivity {
    WebView wv =null;
    private WebSettings setting;
    private int position = -1;
    public boolean isHint = false;
    private String order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        ToolUtils.setBar(this);
    }
    public boolean parseScheme(String url) {
        if (url.contains("platformapi/startapp")){
            return true;
        } else if(url.contains("web-other")){
            return false;
        }else {
            return false;
        }
    }
    @Override
    public void initView() {
        wv = (WebView) findViewById(R.id.webview);
        wv.addJavascriptInterface(new JsInteraction(), "yysdk");
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Globals.log("return",url);
                if (url.startsWith("weixin://wap/pay?") || url.contains("alipay")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivityForResult(intent,0);
                    return true;
                }
                return true;
            }
            @Override
            public void onReceivedError(WebView view, int errorCode, String description,
                                        String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = cookieManager.getCookie(url);
                Log.e("cookies", url+"，Cookies = " + CookieStr);
                super.onPageFinished(view, url);
            }
            @SuppressLint("NewApi")
            @Override
            public WebResourceResponse shouldInterceptRequest(final WebView view, final String url) {
                return super.shouldInterceptRequest(view, url);
            }
        });
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
        setting = wv.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(false);
        setting.setSupportMultipleWindows(false);
        setting.setDisplayZoomControls(false);
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setSavePassword(true);
        setting.setSaveFormData(true);
        String userAgentString = setting.getUserAgentString();
        setting.setUserAgentString(userAgentString + " hlygame");

        wv.setInitialScale(1);

        // 开启 DOM storage API 功能
        setting.setDomStorageEnabled(true);
        // 开启 database storage API 功能
        setting.setDatabaseEnabled(true);
        setting.setAppCacheEnabled(true);
        try {
            Method method;
            if (Build.VERSION.SDK_INT <= 5) {
                method = WebView.class.getMethod("emulateShiftHeld", new Class[] { Boolean.TYPE });
                method.invoke(wv, new Object[] { Boolean.valueOf(false) });
            } else {
                method = WebView.class.getMethod("emulateShiftHeld", new Class[0]);
                method.invoke(wv, new Object[0]);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            KeyEvent keyEvent = new KeyEvent(0L, 0L, 0, 59, 0, 0);
            keyEvent.dispatch(wv, wv.getKeyDispatcherState(), wv);
        }
//		wv.setWebViewClient(new InterceptingWebViewClient(this, wv));
        Intent intent = getIntent();
        String launchUrl = intent.getStringExtra("url");
        order = intent.getStringExtra("order");
        position = intent.getIntExtra("position",-1);
        wv.loadUrl(launchUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != 0){
            return;
        }
        switch (requestCode){
            case 0:
                orderQuery();
                break;
        }
    }

    @Override
    public void initData() {

    }
    /**
     * 将url参数转换成map
     *
     * @param param
     *            aa=11&bb=22&cc=33
     * @return
     */
    public static Map<String, String> getUrlParams(String param) {
        Map<String, String> map = new HashMap<String, String>(0);
        if (param==null||"".equals(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if(p[0].equals("data")){
                String value ="";
                for (int j = 1;j<p.length;j++){
                    value += p[j]+"=";
                }
                map.put(p[0], value.substring(0,value.length()-1));
            }else{
                if (p.length == 2) {
                    map.put(p[0], p[1]);
                }
            }
        }
        return map;
    }
    public class JsInteraction {

        @JavascriptInterface
        public void payNotify(String info) {
            if(isHint){
                return;
            }
            if(info.equals("aa")){
                isHint=true;
                SuccessDialog successDialog = new SuccessDialog(WebViewActivity.this, "", new SuccessDialog.AnalysisUrlListener() {
                    @Override
                    public void analysis() {
                        Intent intent = new Intent();
                        setResult(1,intent);
                        finish();
                    }
                });
                successDialog.show();
            }else{
                orderQuery();
            }
        }
        @JavascriptInterface
        public void closeWeb(){
            finish();

        }
        @JavascriptInterface
        public void alert(String info) {
            /*AlertDialog.alert(getContext(), info, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });*/
        }

    }


    public void orderQuery(){
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
                    final int finalBack = back;
                    SuccessDialog successDialog = new SuccessDialog(WebViewActivity.this, text, new SuccessDialog.AnalysisUrlListener() {
                        @Override
                        public void analysis() {
                            Intent intent = new Intent();
                            setResult(finalBack,intent);
                            finish();
                        }
                    });
                    successDialog.show();
                }
                Globals.log(bean.getMsg());
                LoadingDialog.closeLoadingDialog();
                //ToastUtils.showShort(bean.getMsg());
            }
        });
    }
}
