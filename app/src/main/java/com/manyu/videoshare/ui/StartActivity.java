package com.manyu.videoshare.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.base.RoundProgressBar;
import com.manyu.videoshare.bean.InitAppBean;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.GlideUtils;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class StartActivity extends BaseActivity implements View.OnClickListener {
    private TextView version;
    private RoundProgressBar bar;
    private InitAppBean bean = null;
    private int progress = 0;
    private boolean starts = true;
    private ImageView btnAd;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ToolUtils.setBar(this);
    }
    private void setActivity(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    starts();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    public void starts(){
        if(starts) {
            starts = false;
            IntentUtils.JumpActivity(this, MainActivity.class);
            finish();
        }
    }

    @Override
    public void initView() {
        context = this;
        View decor = this.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //setActivity();
        version = findViewById(R.id.about_version);
        bar = findViewById(R.id.start_progress);
        btnAd = findViewById(R.id.start_img);

        String versionName = ToolUtils.getVersionName(StartActivity.this);
        version.setText("V" + versionName);
        bar.setOnClickListener(this);
        btnAd.setOnClickListener(this);
        try {
            String destFileDir = Environment.getExternalStorageDirectory() + "/manyu/";
            File dir = new File(destFileDir);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            final String path = Environment.getExternalStorageDirectory() + "/manyu/";
            String md5 = "start.png";
            final String name = path + md5;
            File file = new File(name);

            if(!BaseSharePerence.getInstance().getFirst()){
                if(file.exists()){
                    Bitmap bitmap = BitmapFactory.decodeFile(name);
                    if(null != bitmap)
                        btnAd.setImageBitmap(bitmap);
                }else{
                    btnAd.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            }else{
                btnAd.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private Thread runs;
    public void startTime(int times){
        final int add = 100 / (times * 10);
        runs = new Thread(new Runnable() {

            @Override
            public void run() {
                while(progress < 100){
                    progress += add;
                    if(progress >100){
                        progress = 100;
                    }
                    System.out.println(progress);
                    bar.setProgress(progress);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                starts();

            }
        });
        runs.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAgreement();
        if(null != bean) {
            starts = true;
            startTime(bean.getDatas().getAd_step());
        }
    }

    @Override
    public void initData() {

        bar.setCircleColor(getResources().getColor(R.color.hint_color));//设置圆环的颜色
        bar.setCircleProgressColor(getResources().getColor(R.color.start_bar));//设置圆环进度的颜色
        bar.setTextColor(getResources().getColor(R.color.start_jump));//设置中间进度百分比的字符串的颜色
        bar.setRoundWidth(ToolUtils.dip2px(2));//设置圆环的宽度
        bar.setTextSize(ToolUtils.dip2px(12));
    }
    private void getAgreement(){


        HttpUtils.httpString(Constants.INITAPP,null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("APP初始化失败，连接不到服务器");
                if(bean == null || bean.getData() == null||bean.getData().length() == 0){
                    startTime(3);
                    return;
                }
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                bean = gson.fromJson(resultData,InitAppBean.class);
                BaseSharePerence.getInstance().setUserAgree(bean.getDatas().getAgreement().getContent());
                BaseSharePerence.getInstance().setPrivacyPolicy(bean.getDatas().getPrivacy().getContent());
                Globals.log(bean.getMsg());
                if(bean.getData() == null ||bean.getData().length() == 0){
                    startTime(3);
                    return;
                }
                startTime(bean.getDatas().getAd_step());
                if(BaseSharePerence.getInstance().getFirst()){
                    //btnAd.setImageDrawable(getResources().getDrawable(R.drawable.start));
                    BaseSharePerence.getInstance().setFirst(false);
                    btnAd.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }else if(bean.getDatas().getStart_page() != null){
                    bar.setVisibility(View.VISIBLE);
                    final String path = Environment.getExternalStorageDirectory() + "/manyu/";
                    String md5 = "start.png";
                    final String name = path + md5;
                    File file = new File(name);
                    if(!file.exists())
                        GlideUtils.loadImgWithout(context,bean.getDatas().getStart_page().getImage(),btnAd);
                    okHttpDownLoadApk(bean.getDatas().getStart_page().getImage());
                }
                //

                LoadingDialog.closeLoadingDialog();
                //ToastUtils.showShort(bean.getMsg());
            }
        });
    }
    public void okHttpDownLoadApk(String url) {
        String destFileDir = Environment.getExternalStorageDirectory() + "/manyu/";
        File dir = new File(destFileDir);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        final String path = Environment.getExternalStorageDirectory() + "/manyu/";
        String md5 = "start.png";
        final String name = path + md5;
        File file = new File(name);

        /*File myCaptureFile = new File(path, md5);
        try {
            myCaptureFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(path, md5) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(File response, int id) {

                    }

                    @Override
                    public void inProgress(final float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                    }
                });
    }
    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.start_progress:
                starts();
                break;
            case R.id.start_img:
                try {
                    if(null != bean && null != bean.getData() && null != bean.getDatas() && bean.getDatas().getStart_page() != null) {
                        starts = false;
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        Uri content_url = Uri.parse(bean.getDatas().getStart_page().getUrl());
                        intent.setDataAndType(content_url, "text/html");
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
