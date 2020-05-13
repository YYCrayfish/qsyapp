package com.manyu.videoshare.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DrawableUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.gyf.immersionbar.ImmersionBar;
import com.manyu.videoshare.R;
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
import com.manyu.videoshare.util.universally.LOG;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    //    private TextView version;
    private RoundProgressBar mCounterBar;
    private InitAppBean bean = null;
    private int progress = 0;
    private boolean starts = true;
    private ImageView launchAdImageView;
    private TextView launchLogoText;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        ImmersionBar.with(this).fullScreen(true).init();
        ToolUtils.setBar(this);
        initView();
        initData();
    }

    private void hideSystemBar() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }


    //TODO 前往首页
    public void starts() {
        if (starts) {
            starts = false;
            IntentUtils.JumpActivity(this, MainActivity.class);
            finish();
        }
    }

    public void initView() {
        context = this;
//        version = findViewById(R.id.about_version);
        mCounterBar = findViewById(R.id.launch_layout_start_progress);
        launchAdImageView = findViewById(R.id.launch_layout_advert_image);
        launchLogoText = findViewById(R.id.launch_layout_logo_txt);

//        String versionName = ToolUtils.getVersionName(StartActivity.this);
//        version.setText("V" + versionName);
        mCounterBar.setOnClickListener(this);
        launchAdImageView.setOnClickListener(this);
//        //TODO 加载广告图片
//        try {
//            String destFileDir = Environment.getExternalStorageDirectory() + "/manyu/";
//            File dir = new File(destFileDir);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            final String path = Environment.getExternalStorageDirectory() + "/manyu/";
//            String md5 = "start.png";
//            final String name = path + md5;
//            //TODO 生成文件
//            File file = new File(name);
//            //TODO 判断APP是否第一次启动
////            if (!BaseSharePerence.getInstance().getFirst()) {
////                //TODO 若广告图片存在 则加载显示广告
////                if (file.exists()) {
////                    Bitmap bitmap = BitmapFactory.decodeFile(name);
////                    if (null != bitmap) {
////                        launchAdImageView.setImageBitmap(bitmap);
////                        toggleLogoInfo(false);
////                    }
////                } else {
////                    launchAdImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
////                }
////            } else {
////                launchAdImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
////            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            toggleLogoInfo(true);
//        }

    }

    //TODO LOGO和广告图互斥
    private void toggleLogoInfo(boolean toggle) {
        if (toggle) {
            launchLogoText.setVisibility(View.VISIBLE);
        } else {
            launchLogoText.setVisibility(View.GONE);
        }
    }

    private Thread runs;

    public void startTime(int times) {
        final int add = 100 / (times * 10);
        runs = new Thread(new Runnable() {

            @Override
            public void run() {
                while (progress < 100) {
                    progress += add;
                    if (progress > 100) {
                        progress = 100;
                    }
                    System.out.println(progress);
                    mCounterBar.setProgress(progress);
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
        initApplication();
        if (null != bean) {
            starts = true;
            startTime(bean.getDatas().getAd_step());
        }
    }

    public void initData() {
        mCounterBar.setCircleColor(getResources().getColor(R.color.hint_color));//设置圆环的颜色
        mCounterBar.setCircleProgressColor(getResources().getColor(R.color.start_bar));//设置圆环进度的颜色
        mCounterBar.setTextColor(getResources().getColor(R.color.start_jump));//设置中间进度百分比的字符串的颜色
        mCounterBar.setRoundWidth(ToolUtils.dip2px(2));//设置圆环的宽度
        mCounterBar.setTextSize(ToolUtils.dip2px(12));
    }


    //TODO 初始化操作
    private void initApplication() {
        HttpUtils.httpString(Constants.INITAPP, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
                ToastUtils.showShort("APP初始化失败，连接不到服务器");
                toggleLogoInfo(true);
                startTime(3);
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                try {
                    bean = gson.fromJson(resultData, InitAppBean.class);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                    startTime(3);
                    return;
                }
                if (bean != null && bean.getDatas() != null && bean.getDatas().getStart_page() != null) {
                    BaseSharePerence.getInstance().setUserAgree(bean.getDatas().getAgreement().getContent());
                    BaseSharePerence.getInstance().setPrivacyPolicy(bean.getDatas().getPrivacy().getContent());
                    Globals.log(bean.getMsg());
                    if (bean.getDatas() == null || bean.getDatas().getCopy_app().size() == 0) {
                        startTime(3);
                        return;
                    }
                    startTime(bean.getDatas().getAd_step());
                    if (BaseSharePerence.getInstance().getFirst()) {
                        BaseSharePerence.getInstance().setFirst(false);
                        launchAdImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else if (bean.getDatas().getStart_page() != null) {
                        mCounterBar.setVisibility(View.VISIBLE);
                        final String path = Environment.getExternalStorageDirectory() + "/manyu/";
                        String md5 = "start.png";
                        final String name = path + md5;
                        File file = new File(name);
                        toggleLogoInfo(false);
                        GlideUtils.loadImgWithout(context, bean.getDatas().getStart_page().getUrl(), launchAdImageView);
                        if (bean.getDatas().getStart_page() != null && !TextUtils.isEmpty(bean.getDatas().getStart_page().getImage())){
                            Glide.with(StartActivity.this).load(bean.getDatas().getStart_page().getImage()).into(launchAdImageView);
                        }else{
                            toggleLogoInfo(true);
                        }
                        okHttpDownLoadApk(bean.getDatas().getStart_page().getImage());
                    }
                }
                LoadingDialog.closeLoadingDialog();
            }
        });
    }

    public void okHttpDownLoadApk(String url) {
        String destFileDir = Environment.getExternalStorageDirectory() + "/manyu/";
        File dir = new File(destFileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        final String path = Environment.getExternalStorageDirectory() + "/manyu/";
        String md5 = "start.png";
//        final String name = path + md5;
//        File file = new File(name);

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
                        LOG.showE("图片加载完成："+response.getAbsolutePath());
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
        switch (v.getId()) {
            case R.id.start_progress:
            case R.id.launch_layout_start_progress:
                //跳过
                starts();
                break;
            case R.id.start_img:
                //点击了广告图
                try {
                    if (null != bean && null != bean.getDatas() && null != bean.getDatas() && bean.getDatas().getStart_page() != null) {
                        starts = false;
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(bean.getDatas().getStart_page().getUrl()));
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
