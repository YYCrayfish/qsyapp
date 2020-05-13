package com.manyu.videoshare.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.view.VideoSeekBar;

public class VideoViewTool implements View.OnClickListener {

    private ImageView start;
    private ImageView reset;
    public VideoView videoView;
    public VideoSeekBar videoSeekBar;

    private int videoH;
    private int videoW;
    private String videoPath;
    private MediaMetadataRetriever metadataRetriever;

    public void init(Context context, View view, Uri uri) {
        videoPath = UriToPathUtil.getRealFilePath(context, uri);
        if (view != null) {

        } else {
            metadataRetriever = new MediaMetadataRetriever();
            videoView = ((Activity) context).findViewById(R.id.vv);
            Log.e("Logger", "videoView == null ? " + (videoView == null));
            videoSeekBar = ((Activity) context).findViewById(R.id.videoSeekBar);
            start = ((Activity) context).findViewById(R.id.start);
            reset = ((Activity) context).findViewById(R.id.reset);
            start.setOnClickListener(this);
            reset.setOnClickListener(this);
            MediaController mediaController = new MediaController(context);
            mediaController.setVisibility(View.GONE);
            videoView.setEnabled(false);
            videoView.setMediaController(mediaController);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    start.setImageResource(R.mipmap.icon_video_pause);
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoSeekBar.reset();
                    mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                        @Override
                        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                            //FixMe 获取视频资源的宽度
                            videoW = mp.getVideoWidth();
                            //FixMe 获取视频资源的高度
                            videoH = mp.getVideoHeight();
                        }
                    });
                }
            });
            videoSeekBar.setOnRangeListener(new VideoSeekBar.onRangeListener() {
                @Override
                public void onVideoPause() {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                    }
                }

                @Override
                public void onVideoStart(long duration) {
                    videoView.seekTo((int) duration);
                    videoView.start();
                }
            });
            metadataRetriever.setDataSource(videoPath);
            metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            handler.sendEmptyMessageDelayed(1, 100);
            videoSeekBar.setBigValue(getLocalVideoDuration(videoPath));
            start.setImageResource(R.mipmap.icon_video_pause);
            videoView.setVideoURI(uri);
            videoView.start();
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    for (int j = 0; j < 8; j++) {
                        Bitmap bitmap = null;
                        for (long i = j * (getLocalVideoDuration(videoPath) / 8); i < getLocalVideoDuration(videoPath); i += 1000) {
                            bitmap = metadataRetriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                            if (bitmap != null) {
                                break;
                            }
                        }
                        videoSeekBar.setBitmap(bitmap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    public int getVideoH() {
        return videoH;
    }

    public void setVideoH(int videoH) {
        this.videoH = videoH;
    }

    public int getVideoW() {
        return videoW;
    }

    public void setVideoW(int videoW) {
        this.videoW = videoW;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start:
                if (videoView.isPlaying()) {
                    videoView.pause();
                    videoSeekBar.pause();
                    start.setImageResource(R.mipmap.icon_video_play);
                } else {
                    videoView.start();
                    videoSeekBar.resume();
                    start.setImageResource(R.mipmap.icon_video_pause);
                }
                break;
            case R.id.reset:
                videoView.seekTo(0);
                videoView.start();
                videoSeekBar.reset();
                break;
        }
    }

    public void videoPuase() {
        if (videoView != null) {
            videoView.pause();
            videoSeekBar.pause();
        }
    }

    public void videoResume() {
        if (videoView != null && videoPath != null && !"".equals(videoPath)) {
            videoView.start();
            videoSeekBar.resume();
        }
    }

    public void videoStart() {
        if (videoView != null) {
            videoView.start();
            videoSeekBar.reset();
        }
    }
}
