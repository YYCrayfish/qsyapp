package com.manyu.videoshare.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.base.MyVideoView;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import static com.manyu.videoshare.util.ToolUtils.formatTime;

public class FullVideoActivity extends BaseActivity implements View.OnClickListener {
    private ImageView btnFull;
    private MyVideoView videoView;
    private TextView currentText;
    private TextView totalText;
    private SeekBar seekBar;
    private ImageView btnStart;
    private RelativeLayout btnLayout;
    private ImageView btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_video);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        btnFull = findViewById(R.id.imageView_fullscreen);
        videoView = findViewById(R.id.full_videview);
        currentText = findViewById(R.id.textView_playtime);
        totalText = findViewById(R.id.textView_totaltime);
        seekBar = findViewById(R.id.seekbar);
        btnStart = findViewById(R.id.main_surface_start);
        btnLayout = findViewById(R.id.full_screen);
        btnBack = findViewById(R.id.full_img_back);

        btnStart.setOnClickListener(this);
        btnFull.setOnClickListener(this);
        btnLayout.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        int videowid = intent.getIntExtra("width",0);
        int videohei = intent.getIntExtra("heigh",0);
        int[] denisty = ToolUtils.scaleToScreen(videowid,videohei);
        videowid = denisty[0];
        videohei = denisty[1];
        videoView.setVideoSize(videowid,videohei);
        btnFull.setImageDrawable(getResources().getDrawable(R.drawable.full_back));
        String url = getIntent().getStringExtra("url");
        initMediaPlayer(url);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            // 表示手指拖动seekbar完毕，手指离开屏幕会触发以下方法
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // 让计时器延时执行
                videoView.start();
                handReset();
            }

            // 在手指正在拖动seekBar，而手指未离开屏幕触发的方法
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // 让计时器取消计时
                videoView.pause();
                //timer.cancel();
                handler.removeCallbacks(runs);
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                if (fromUser) {
                    Globals.log(videoView.getDuration()+"");
                    int playtime = progress * videoView.getDuration() / 100;
                    Globals.log(formatTime(playtime));
                    videoView.seekTo(playtime);
                    currentText.setText(formatTime(playtime));
                    seekBar.setProgress(progress);
                    Globals.log(formatTime(videoView.getCurrentPosition()));
                }

            }
        });
    }
    long his_time = 0;
    Runnable runs = new Runnable() {
        @Override
        public void run() {
            if (videoView != null) {
                int currentPlayer = videoView.getCurrentPosition();
                if (currentPlayer > 0) {
                    videoView.getCurrentPosition();
                    currentText.setText(formatTime(currentPlayer));

                    // 让seekBar也跟随改变
                    int progress = (int) ((currentPlayer / (float) videoView
                            .getDuration()) * 100);

                    seekBar.setProgress(progress);
                    if(formatTime(currentPlayer).equals(formatTime(videoView
                            .getDuration())) || his_time == currentPlayer){
                        btnStart.setVisibility(View.VISIBLE);
                        btnStart.setImageResource(R.drawable.main_video_stop);
                    }else{
                        his_time = currentPlayer;
                        handReset();
                    }
                } else {
                    currentText.setText("00:00");
                    seekBar.setProgress(0);
                    btnStart.setImageResource(R.drawable.main_video_stop);
                }
            }
        }
    };
    public void handReset(){
        handler.postDelayed(runs,1000);
    }
    private void initMediaPlayer(String filePath) {
        Globals.log("videoUrl",filePath);
        videoView.setZOrderMediaOverlay(true);
        videoView.setVideoPath(filePath);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                totalText.setText(formatTime(mp.getDuration()));
                /*int videohei = mp.getVideoHeight();
                int videowid = mp.getVideoWidth();
                int[] denisty = ToolUtils.scaleToScreen(videowid,videohei);
                videowid = denisty[0];
                videohei = denisty[1];

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(videowid,videohei);
                //layoutParams.setMargins((videowid - scrwid) / 2,0,0,0);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                videoView.setLayoutParams(layoutParams);*/
                if(videoView.isPlaying()){
                    btnStart.setImageResource(R.drawable.main_video_stop);
                    videoView.pause();
                    handler.removeCallbacks(runs);
                }else {
                    videoView.start();
                    currentText.setText(formatTime(videoView.getCurrentPosition()));
                    int progress = (int) ((videoView.getCurrentPosition() / (float) videoView
                            .getDuration()) * 100);
                    seekBar.setProgress(progress);
                    btnStart.setVisibility(View.GONE);
                    btnStart.setImageResource(R.drawable.main_video_play);
                    handReset();
                }
            }
        });

    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    String message = msg.getData().getString("msg");
                    ToastUtils.showShort(message);
                    break;
                case 1:
                    ToastUtils.showShort(msg.getData().getString("path"));
                    break;
            }
            return false;
        }
    });
    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.imageView_fullscreen:
                finish();
                break;
            case R.id.full_screen:
                btnStart.setVisibility(View.VISIBLE);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(videoView.isPlaying()){
                            btnStart.setVisibility(View.GONE);
                        }
                    }
                },1500);
                break;
            case R.id.main_surface_start:
                if(videoView.isPlaying()){
                    btnStart.setImageResource(R.drawable.main_video_stop);
                    videoView.pause();
                    handler.removeCallbacks(runs);
                }else {
                    videoView.start();
                    currentText.setText(formatTime(videoView.getCurrentPosition()));
                    int progress = (int) ((videoView.getCurrentPosition() / (float) videoView
                            .getDuration()) * 100);
                    seekBar.setProgress(progress);
                    btnStart.setVisibility(View.GONE);
                    btnStart.setImageResource(R.drawable.main_video_play);
                    handReset();
                }
                break;
            case R.id.full_img_back:
                finish();
                break;
        }
    }
}
