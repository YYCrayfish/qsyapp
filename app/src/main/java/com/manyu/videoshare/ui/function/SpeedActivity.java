package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.FFmpegUtil;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.VideoViewTool;
import com.manyu.videoshare.view.CustomSeekBar;
import com.manyu.videoshare.util.ToolUtils;

import java.io.File;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

/**
 * 视频变速
 */

public class SpeedActivity extends BaseVideoActivity implements View.OnClickListener {
    private CustomSeekBar seekBar;
    private String videoPath;
    private float speed = 1f;
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
        setContentView(R.layout.activity_speed);
        start(this);
    }

    @Override
    protected String getTitleTv() {
        return "视频变速";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
        MediaController mediaController = new MediaController(this);
        mediaController.setVisibility(View.GONE);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setOnProgressChangedListener(new CustomSeekBar.OnProgressChangedListener() {
            @Override
            public void onChanged(CustomSeekBar seekBar, boolean fromUser, boolean isFinished) {
                if (seekBar.getProgress() <= 2) {
                    speed = 0.5f + seekBar.getProgress() * 0.25f;
                } else {
                    speed = seekBar.getProgress() * 0.5f;
                }
            }
        });
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
                videoViewTool.videoPuase();
                changeSpeed(videoPath);
                break;
        }
    }

    private void changeSpeed(final String path) {
        if (speed == 1f) {
            ToastUtils.show("请先选择播放倍数", 500);
            return;
        }
        newPath = newPath + "bs_" + speed + "x_" + UriToPathUtil.getFileNameByPath(path);
        String[] commands = FFmpegUtil.videoSpeed(path, newPath, speed);

        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                progressEnd();
                Log.e("ffmpeg_result", "成功");
                PreviewActivity.start(SpeedActivity.this, newPath);
                newPath = getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                setProgressBarValue(progress);
                if (speed <= 1) {
                    setProgressBarValue((int) (progress / (1 / speed)));
                } else {
                    setProgressBarValue((int) (progress * speed));
                }
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
        videoViewTool.videoResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        videoViewTool.videoPuase();
    }

    @Override
    public void onDestroy() {
        ;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            finish();
            return;
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Uri uri = data.getData();
                videoPath = UriToPathUtil.getRealFilePath(this, uri);
                videoViewTool.init(this, null, uri);
            }
        } else if (resultCode == 100) {
            finish();
        }
    }
}
