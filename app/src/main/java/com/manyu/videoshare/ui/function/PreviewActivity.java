package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.universally.ConfigureParameter;
import com.manyu.videoshare.util.universally.FileUtil;
import com.manyu.videoshare.util.universally.LOG;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * 视频预览
 */

public class PreviewActivity extends BaseVideoActivity implements View.OnClickListener {

    private String path;
    private TextView save;
    private VideoView videoView;
    private int type;
    private String newPath = ConfigureParameter.SYSTEM_CAMERA_PATH;

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
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

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
                setResult(100);
                finish();
                break;
            case R.id.save:
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
