package com.manyu.videoshare.ui.function;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;

import com.google.gson.Gson;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.AnalysisTimeBean;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.FFmpegUtil;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.VideoViewTool;
import com.manyu.videoshare.view.ScreenShotZoomView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;
import okhttp3.Call;

/**
 * 去除水印
 */

public class RemoveWatermarkActivity extends BaseVideoActivity implements View.OnClickListener {
    private ScreenShotZoomView zoomView;
    private String videoPath;
    private int videoW;
    private int videoH;
    private int videoViewW;
    private int videoViewH;
    private float scale;
    private RectF rect = new RectF();
    private List<RectF> list;
    private String newPath = Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;
    private VideoViewTool videoViewTool = new VideoViewTool();
    private IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求

    public void start(Context context) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_watermark);
        start(this);
    }

    @Override
    protected String getTitleTv() {
        return "抹除水印";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
        MediaController mediaController = new MediaController(this);
        mediaController.setVisibility(View.GONE);
        zoomView = findViewById(R.id.sszv);
        zoomView.setOnTransformListener(new ScreenShotZoomView.onTransformListener() {
            @Override
            public void onTransform(float left, float top, float right, float bottom) {
                rect.left = left * scale;
                rect.top = top * scale;
                rect.right = right * scale;
                rect.bottom = bottom * scale;
            }
        });
    }

    @Override
    public void initData() {
        list = new ArrayList<>();
        newPath = getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(videoPath)) {
            videoViewTool.videoResume();
//            type = 0;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        videoViewTool.videoPuase();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_right:
                if (!requestPermissions()) {
                    return;
                }
                removeWM(videoPath);
                //TODO 上报水印去除成功
                succeedRemoveWaterMark();
                break;
        }
    }

    //请求权限
    private boolean requestPermissions() {
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                this,
                permissions,
                PermissionUtils.ResultCode1);
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

    private void removeWM(String path) {
        if (rect.right <= 0 || rect.bottom <= 0 || (rect.left / scale >= videoViewW) || rect.top / scale > videoViewH) {
            ToastUtils.showShort("请在视频范围内裁剪！");
            return;
        }
        if (rect.top >= rect.bottom || rect.left >= rect.right) {
            ToastUtils.showShort("请重新选择水印区域！");
            return;
        }
        videoViewTool.videoPuase();
        list.add(rect);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).left < 0) {
                list.get(i).left = 0;
            }
            if (list.get(i).top < 0) {
                list.get(i).top = 0;
            }
            if (list.get(i).right > videoW - 1) {
                list.get(i).right = videoW - 1;
            }
            if (list.get(i).bottom > videoH - 1) {
                list.get(i).bottom = videoH - 1;
            }
        }
        newPath = newPath + "jq_" + (int) rect.left + "_" + (int) rect.top + "_" + UriToPathUtil.getFileNameByPath(path);
        String[] commands = FFmpegUtil.removeWaterMark(path, newPath, list);

        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                progressEnd();
                Log.e("ffmpeg_result", "成功");
                PreviewActivity.start(RemoveWatermarkActivity.this, newPath);
                list.clear();
                newPath = getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                setProgressBarValue(progress);
                Log.e("ffmpeg_result", progress + "");
            }

            @Override
            public void onCancel() {
                Log.e("ffmpeg_result", "取消");
            }

            @Override
            public void onError(String message) {
                Log.e("ffmpeg_result", "失败");
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0 || resultCode == 100) {
            finish();
            return;
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                videoPath = UriToPathUtil.getRealFilePath(this, uri);
                videoViewTool.init(this, null, uri);
                videoViewTool.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoViewTool.videoSeekBar.reset();
                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                videoViewH = videoViewTool.videoView.getMeasuredHeight();
                                videoViewW = videoViewTool.videoView.getMeasuredWidth();
                                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) zoomView.getLayoutParams();
                                params.height = videoViewH;
                                params.width = videoViewW;
                                zoomView.setLayoutParams(params);
                                //FixMe 获取视频资源的宽度
                                videoW = mp.getVideoWidth();
                                //FixMe 获取视频资源的高度
                                videoH = mp.getVideoHeight();
                                scale = (float) videoW / (float) videoViewW;
//                        refreshPortraitScreen(showVideoHeight == 0 ? DensityUtil.dip2px(context, 300) : showVideoHeight);
                            }
                        });
                    }
                });
            }
        }
    }
}
