package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.gyf.immersionbar.ImmersionBar;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseApplication;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.dialog.ExitDialog;
import com.manyu.videoshare.ui.MainActivity;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.DialogIncomeTipUtil;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.universally.ConfigureParameter;
import com.manyu.videoshare.util.universally.FileUtil;
import com.manyu.videoshare.util.universally.LOG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import okhttp3.Call;

import static com.manyu.videoshare.ui.function.ModifyCoverActivity.FILE_PATH;
import static com.manyu.videoshare.util.SystemProgramUtils.REQUEST_CODE_MOVE_WATER_MARK;

/**
 * 视频预览
 */

public class PreviewActivity extends BaseVideoActivity implements View.OnClickListener {

    private String path;
    private Button save;
    private VideoView videoView;
    private int type;
    private String newPath = ConfigureParameter.SYSTEM_CAMERA_PATH;

    private int videoW;
    private int videoH;
    private int videoViewW;//视频控件宽度
    private int videoViewH;//视频控件高度
    private CardView mVideoViewHost;

    public static void start(Context context, String path) {
        ((Activity) context).startActivityForResult(new Intent(context, PreviewActivity.class).putExtra("path", path), 100);
    }

    public static void start(Context context, String path, int type) {
        ((Activity) context).startActivityForResult(new Intent(context, PreviewActivity.class).putExtra("path", path).putExtra("type", type), 100);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        setToolBarColor(Color.BLACK);
        mVideoViewHost = findViewById(R.id.video_view_host);
        ImmersionBar.with(this).statusBarDarkFont(false).statusBarColorInt(Color.BLACK).init();
        type = getIntent().getIntExtra("type", -1);
    }

    @Override
    protected String getTitleTv() {
        return "视频预览";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
        setTitleRight("回到首页");
        save = findViewById(R.id.save);
        save.setOnClickListener(this);
        videoView = findViewById(R.id.vv);
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(final MediaPlayer mp, int width, int height) {

                        //获取视频资源的宽度
                        videoW = mp.getVideoWidth();
                        //获取视频资源的高度
                        videoH = mp.getVideoHeight();
                        View parent = (View) mVideoViewHost.getParent();
                        // 按原视频的比例，缩放至视频的最长边和容器的最短边相等
                        ConstraintLayout.LayoutParams videoLp = (ConstraintLayout.LayoutParams) mVideoViewHost.getLayoutParams();
                        if ((1f * videoW / videoH) > (1f * parent.getWidth() / parent.getHeight())) {
                            //横屏
                            videoLp.dimensionRatio = "h," + videoW + ":" + videoH;
                        } else {
                            //竖屏
                            videoLp.dimensionRatio = "w," + videoW + ":" + videoH;
                        }
                        mVideoViewHost.setLayoutParams(videoLp);
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(99);
        super.onBackPressed();
    }

    @Override
    public void initData() {
        path = getIntent().getStringExtra("path");
        type = getIntent().getIntExtra("type", 0);
        newPath = newPath + "qsy_" + System.currentTimeMillis() + UriToPathUtil.getFileNameByPath(path);
        videoView.setVideoPath(path);
        videoView.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                setResult(99);
                finish();
                break;
            case R.id.title_right:
                new ExitDialog(this, "当前视频还未保存,是否确定返回首页", new ExitDialog.AnalysisUrlListener() {
                    @Override
                    public void analysis() {
                        setResult(100);
                        startActivity(new Intent(PreviewActivity.this, MainActivity.class));
                        finish();
                    }

                    @Override
                    public void clean() {

                    }
                }).show();
                break;
            case R.id.save:
                if (type == REQUEST_CODE_MOVE_WATER_MARK) {
                    if (BaseApplication.getInstance().getUserAnalysisTime() > 0) {
                        //TODO 上报水印去除成功
                        succeedRemoveWaterMark();
                        BaseApplication.getInstance().setUserAnalysisTime(BaseApplication.getInstance().getUserAnalysisTime() - 1);
                    } else {
                        new DialogIncomeTipUtil(this, "可用次数不足，无法保存。").show();
                        return;
                    }
                }
                if (FileUtil.copyFileOnly(path, newPath)) {
                    ToastUtils.showShort("视频已经成功保存到相册中");
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri uri = Uri.fromFile(new File(newPath));
                    intent.setData(uri);
                    sendBroadcast(intent);
                }
                break;
        }
    }


    private void succeedRemoveWaterMark() {
        HttpUtils.httpString(Constants.SUCCEED_REMOVE_WATER_MARK, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                //TODO 上报水印去除 --请求失败
                LoadingDialog.closeLoadingDialog();
            }

            @Override
            public void httpResponse(String resultData) {
                Log.e("Logger", "上报水印去除成功");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(path)) {
            videoView.setVideoPath(path);
            videoView.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.pause();
        if (type == 0) {
            UriToPathUtil.deleteSingleFile(path);
        }
    }
}
