package com.manyu.videoshare.ui.function;

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
import android.widget.TextView;
import android.widget.VideoView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.FFmpegUtil;
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
                removeWM(videoPath);
                break;
        }
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
            if (list.get(i).right > videoW) {
                list.get(i).right = videoW;
            }
            if (list.get(i).bottom > videoH) {
                list.get(i).bottom = videoH;
            }
        }
        newPath = newPath + "jq_" + (int) rect.left + "_" + (int) rect.top + "_" + UriToPathUtil.getFileNameByPath(path);
        String[] commands = FFmpegUtil.removeWaterMark(path, newPath, list);

        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                proessEnd();
                Log.e("ffmpeg_result", "成功");
                PreviewActivity.start(RemoveWatermarkActivity.this, newPath);
                list.clear();
                newPath = getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                setProess(progress);
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
                videoViewTool.init(this,null,uri);
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
