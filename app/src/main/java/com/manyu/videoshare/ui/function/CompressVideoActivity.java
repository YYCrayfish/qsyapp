package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.FFmpegUtil;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.VideoViewTool;

import java.io.File;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class CompressVideoActivity extends BaseVideoActivity implements View.OnClickListener {

    private RadioGroup rg;
    private int type = 0;//1 低质量，2 中质量，3 高质量
    private CardView mVideoViewHost;
    private int crf;
    private String videoPath;
    private String newPath = Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;
    private VideoViewTool videoViewTool = new VideoViewTool();
    private int videoW;
    private int videoH;
    private int videoViewW;//视频控件宽度
    private int videoViewH;//视频控件高度
    private float scale;

    public void start(Context context) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compress_video);
        start(this);
    }

    @Override
    protected String getTitleTv() {
        return "视频压缩";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
        rg = findViewById(R.id.rg);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.low:
                        type = 1;
                        crf = 42;
                        break;
                    case R.id.center:
                        type = 2;
                        crf = 36;
                        break;
                    case R.id.high:
                        type = 3;
                        crf = 30;
                        break;
                }
            }
        });
        rg.check(R.id.center);
    }

    @Override
    public void initData() {
        newPath = getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_right:
                compressVideo(videoPath);
                break;
        }
    }

    private void compressVideo(String path) {
        if (type == 0) {
            ToastUtils.showShort("请选择压缩质量！");
            return;
        }
        newPath = newPath + "ys_" + crf + "_" + UriToPathUtil.getFileNameByPath(path);
        String[] commands = FFmpegUtil.compressVideo(path, newPath, crf);

        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                progressEnd();
                Log.e("ffmpeg_result:", "成功");
                PreviewActivity.start(CompressVideoActivity.this, newPath);
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
    public void onResume() {
        super.onResume();
        videoViewTool.videoStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoViewTool.videoPuase();
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
