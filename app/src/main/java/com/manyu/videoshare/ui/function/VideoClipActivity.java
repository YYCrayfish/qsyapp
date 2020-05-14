package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gyf.immersionbar.ImmersionBar;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.FFmpegUtil;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.VideoViewTool;
import com.manyu.videoshare.view.MCustomZoomView;

import java.io.File;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

/**
 * 视频裁剪
 */

public class VideoClipActivity extends BaseVideoActivity implements View.OnClickListener {

    private MCustomZoomView zoomView;
    private RadioGroup mRatioTypeGroup;
    private RadioButton ratioFree;
    private RadioButton ratio1;
    private RadioButton ratio43;
    private RadioButton ratio34;
    private String videoPath;
    private int videoW;
    private int videoH;
    private int videoViewW;//视频控件宽度
    private int videoViewH;//视频控件高度
    private float scale;
    private RectF rect = new RectF();
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
        setContentView(R.layout.activity_video_clip);
        mVideoViewHost = findViewById(R.id.video_view_host);
        ImmersionBar.with(this).statusBarDarkFont(false).statusBarColorInt(Color.BLACK).init();
        setToolBarColor(Color.BLACK);
        start(this);
    }

    @Override
    protected String getTitleTv() {
        return "视频裁剪";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
        zoomView = findViewById(R.id.mczv);
        mRatioTypeGroup = findViewById(R.id.video_clip_ratio_type_group);
        ratioFree = findViewById(R.id.free);
        ratio1 = findViewById(R.id.ratio1);
        ratio43 = findViewById(R.id.ratio43);
        ratio34 = findViewById(R.id.ratio34);
        zoomView.setIsDrawMapLine(true);

        mRatioTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id) {
                    case R.id.ratio1:
                        zoomView.setRatioType(2);
                        break;
                    case R.id.ratio43:
                        zoomView.setRatioType(3);
                        break;
                    case R.id.ratio34:
                        zoomView.setRatioType(4);
                        break;
                    default:
                        //自由比率
                        zoomView.setRatioType(1);
                        break;
                }
            }
        });
    }

    @Override
    public void initData() {
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
                cipVideo(videoPath);
                break;
            case R.id.free:
                zoomView.setRatio(0);
                break;
            case R.id.ratio1:
                zoomView.setRatio(1f);
                break;
            case R.id.ratio43:
                zoomView.setRatio(4 / 3f);
                break;
            case R.id.ratio34:
                zoomView.setRatio(3 / 4f);
                break;

        }
    }

    private void cipVideo(String path) {
        rect.left = zoomView.getAreaRectF().left * scale;
        rect.top = zoomView.getAreaRectF().top * scale;
        rect.right = zoomView.getAreaRectF().right * scale;
        rect.bottom = zoomView.getAreaRectF().bottom * scale;
        if (rect.right <= 0 || rect.bottom <= 0 || (rect.left / scale >= videoViewW) || rect.top / scale > videoViewH) {
            ToastUtils.showShort("请在视频范围内裁剪！");
            return;
        }
        if (rect.top >= rect.bottom || rect.left >= rect.right) {
            ToastUtils.showShort("请重新选择裁剪区域！");
            return;
        }
        videoViewTool.videoPuase();
        newPath = newPath + "jq_" + (int) rect.left + "_" + (int) rect.top + "_" + UriToPathUtil.getFileNameByPath(path);
        String[] commands = FFmpegUtil.resizeVideo(path, newPath, rect);

        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                progressEnd();
                PreviewActivity.start(VideoClipActivity.this, newPath);
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

    private CardView mVideoViewHost;

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

                                ViewGroup.LayoutParams layoutParams = zoomView.getLayoutParams();
                                layoutParams.width = videoViewTool.videoView.getWidth();
                                layoutParams.height = videoViewTool.videoView.getHeight();
                                zoomView.setLayoutParams(layoutParams);

                                videoViewTool.videoSeekBar.reset();
                                //TODO 这里正在对视频控件的宽高做处理，没经过测量和布局是拿不到宽度的
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        videoViewH = videoViewTool.videoView.getMeasuredHeight();
                                        videoViewW = videoViewTool.videoView.getMeasuredWidth();
                                        //FixMe 获取视频资源的宽度
                                        videoW = mp.getVideoWidth();
                                        videoH = mp.getVideoHeight();
                                        scale = (float) videoW / (float) videoViewW;

                                        // 宽高初始化完成后，才允许拖动
                                        zoomView.setEnabled(true);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }
}
