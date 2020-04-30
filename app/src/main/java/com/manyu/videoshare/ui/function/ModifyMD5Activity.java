package com.manyu.videoshare.ui.function;

import android.content.Intent;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;
import android.widget.VideoView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.MD5Utils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.VideoViewTool;
import com.manyu.videoshare.util.universally.FileUtil;
import com.manyu.videoshare.util.universally.LOG;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author：xushiyong
 * Date：2020/4/30
 * Descript：
 */
public class ModifyMD5Activity extends BaseVideoActivity implements MediaPlayer.OnPreparedListener {

    @BindView(R.id.vv)
    VideoView videoView;
    @BindView(R.id.txtOldMd5)
    TextView txtOldMd5;
    @BindView(R.id.txtNewMd5)
    TextView txtNewMd5;

    private Unbinder unbinder;
    private final int SHOW_VIDEO = 1;
    private String videoPath;
    private VideoViewTool videoViewTool = new VideoViewTool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_md5);
        unbinder = ButterKnife.bind(this);
        // 加横线
        txtOldMd5.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

        showVideoSelect();

        String filePath = Environment.getExternalStorageDirectory().getPath()+"/"+"gsplay";
        String newMd5 = MD5Utils.getFileMD5(new File(filePath));
        LOG.showE("新的 MD5 = "+newMd5);
    }


    // 展示选择内容
    private void showVideoSelect() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, SHOW_VIDEO);
    }

    @Override
    protected String getTitleTv() {
        return "修改MD5";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            finish();
        } else if (resultCode == RESULT_OK) {
            // 选择视频
            if (requestCode == SHOW_VIDEO) {
                Uri uri = data.getData();
                videoPath = UriToPathUtil.getRealFilePath(this, uri);
                videoViewTool.init(ModifyMD5Activity.this, null, uri);
                // 设置预备监听
                videoViewTool.videoView.setOnPreparedListener(this);
                settingMD5(new File(videoPath));
            }
        }
    }

    private void settingMD5(File file) {
        String md5 = MD5Utils.getFileMD5(file);
        LOG.showE("旧  MD5值：" + md5);
        txtOldMd5.setText(md5);
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        videoViewTool.videoSeekBar.reset();
//        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
//            @Override
//            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoViewTool != null) {
            videoViewTool.videoResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoViewTool != null) {
            videoViewTool.videoPuase();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null)
            unbinder.unbind();
    }

    @OnClick(R.id.layoutView)
    public void onViewClicked() {
        LOG.showE("开始获取MD5");
        //文件
        File file = new File(videoPath);
        //切割文件
        FileUtil.getSplitFile(file,1*1024*1024 );

        //合并文件
        String merFileName = "gsplay";//自定义合并文件名字
        //创建合并文件路径
        String filePath = Environment.getExternalStorageDirectory().getPath()+"/"+merFileName;

        FileUtil.merge(filePath,file,1*1024*1024);

        String newMd5 = MD5Utils.getFileMD5(new File(filePath));
        LOG.showE("新的 MD5 = "+newMd5);
        txtNewMd5.setText(newMd5);
    }
}
