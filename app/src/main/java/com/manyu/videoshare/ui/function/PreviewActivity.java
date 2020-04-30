package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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
    private String newPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Camera/";

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
    public void initData() {
        path = getIntent().getStringExtra("path");
        type = getIntent().getIntExtra("type", 0);
        newPath = newPath + "qsy_" + UriToPathUtil.getFileNameByPath(path);
        LOG.showE("播放预览路径："+path);
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
                if (copyFile(path, newPath)) {
                    ToastUtils.showShort("视频已经成功保存到相册中");
                }
                break;
        }
    }

    public boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File newFile = new File(newPath$Name);
            if (newFile.exists()) {
                ToastUtils.showShort("视频已经存在，无需再次保存。");
                return false;
            }
            File file = new File(oldPath$Name);
            if (!file.exists()) {
                Log.e("--Method--", "copyFile:  oldFile not exist.");
                return false;
            } else if (!file.isFile()) {
                Log.e("--Method--", "copyFile:  oldFile not file.");
                return false;
            } else if (!file.canRead()) {
                Log.e("--Method--", "copyFile:  oldFile cannot read.");
                return false;
            }

        /* 如果不需要打log，可以使用下面的语句
        if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
            return false;
        }
        */

            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);    //读入原文件
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = fileInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
