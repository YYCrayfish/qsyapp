package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.FFmpegUtil;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.view.DoubleSlideSeekBar;

import java.io.File;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

/**
 * 裁切时长
 */
public class TimeCutActivity extends BaseVideoActivity implements View.OnClickListener {
    private VideoView videoView;
    private TextView start;
    private DoubleSlideSeekBar doubleSeekBar;
    private String videoPath;
    private float startTime = 0;
    private float endTime;
    private String newPath = Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;
    private MediaMetadataRetriever metadataRetriever;

    public void start(Context context) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_cut);
        start(this);
    }

    @Override
    protected String getTitleTv() {
        return "裁切时长";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
        start = findViewById(R.id.start);
        videoView = findViewById(R.id.vv);
        start.setOnClickListener(this);
        MediaController mediaController = new MediaController(this);
        mediaController.setVisibility(View.GONE);
        videoView.setEnabled(false);
        videoView.setMediaController(mediaController);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                start.setText("播放");
            }
        });
        doubleSeekBar = findViewById(R.id.doubleSeekBar);
        doubleSeekBar.setOnRangeListener(new DoubleSlideSeekBar.onRangeListener() {
            @Override
            public void onRange(float low, float big) {
                startTime = low;
                endTime = big;
            }

            @Override
            public void onEndPlay() {
                videoView.seekTo((int) startTime);
                videoView.start();
            }

            @Override
            public void onStartPlay() {
                videoView.seekTo((int) startTime);
                videoView.start();
            }
        });
    }

    @Override
    public void initData() {
        metadataRetriever = new MediaMetadataRetriever();
        newPath = getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_right:
                if (videoView.canPause()) {
                    videoView.pause();
                    doubleSeekBar.pause(0);
                }
                timeCut(videoPath);
                break;
            case R.id.start:
                if (videoView.isPlaying()) {
                    videoView.pause();
                    start.setText("播放");
                    doubleSeekBar.pause(1);
                } else {
                    videoView.seekTo((int) startTime);
                    videoView.start();
                    start.setText("暂停");
                    doubleSeekBar.pause(2);
                }
                break;
        }
    }

    private void timeCut(String path) {
        newPath = newPath + "cq_" + startTime + "_" + endTime + "_" + UriToPathUtil.getFileNameByPath(path);
        String[] commands = FFmpegUtil.cutVideo(path, (long) startTime, (long) (endTime - startTime), newPath);

        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                progressEnd();
                Log.e("ffmpeg_result", "成功");
                doubleSeekBar.pause(0);
                PreviewActivity.start(TimeCutActivity.this, newPath);
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
        if (!TextUtils.isEmpty(videoPath)) {
            videoView.setVideoPath(videoPath);
            videoView.seekTo((int) startTime);
            videoView.start();
            doubleSeekBar.pause(2);
//            type = 0;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
        doubleSeekBar.pause(1);
    }

    @Override
    public void onDestroy() {
        doubleSeekBar.pause(0);
        metadataRetriever.release();
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
                metadataRetriever.setDataSource(videoPath);
                metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                handler.sendEmptyMessageDelayed(1, 100);
                videoView.setVideoURI(uri);
                doubleSeekBar.setBigValue(getLocalVideoDuration(videoPath));
                doubleSeekBar.invalidate();
                doubleSeekBar.pause(2);
                start.setText("暂停");
            }
        } else if (resultCode == 100) {
            finish();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                for (int j = 0; j < 8; j++) {
                    Bitmap bitmap = null;
                    for (long i = j * (getLocalVideoDuration(videoPath) / 8); i < getLocalVideoDuration(videoPath); i += 1000) {
                        bitmap = metadataRetriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                        if (bitmap != null) {
                            break;
                        }
                    }
                    doubleSeekBar.setBitmap(bitmap);
                    if (j == 1) {
                        videoView.start();
                    }
                }
            }
        }
    };


    public static int getLocalVideoDuration(String videoPath) {
        int duration;
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(videoPath);
            duration = Integer.parseInt(mmr.extractMetadata
                    (MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return duration;
    }
}
