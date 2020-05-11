package com.manyu.videoshare.ui.function;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import com.gyf.barlibrary.ImmersionBar;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.MD5Utils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.VideoViewTool;
import com.manyu.videoshare.util.universally.ConfigureParameter;
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
 * Descript：修改视频文件的 MD5值
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
    // 临时文件的路径，如果进入下一步保存的话，就用这个文件
    private String tempVideoPath;
    private VideoViewTool videoViewTool = new VideoViewTool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_md5);
        unbinder = ButterKnife.bind(this);
        // 加横线
        txtOldMd5.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        ImmersionBar.with(this).statusBarDarkFont(false).statusBarColorInt(Color.BLACK).init();
        setToolBarColor(Color.BLACK);
        // 配置跳到选择视频的系统页
        showVideoSelect();
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
                // 设置旧的md5
                settingMD5(new File(videoPath));
                // 生成新的md5
                modifyMd5();
            }
        }
    }

    private void settingMD5(File file) {
        String md5 = MD5Utils.getFileMD5(file);
//        LOG.showE("旧  MD5值：" + md5);
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

    // 下一步的点击操作
    @OnClick(R.id.title_right)
    public void onNestClicked() {
        if (tempVideoPath != null && !"".equals(tempVideoPath))
            PreviewActivity.start(ModifyMD5Activity.this, tempVideoPath);
        else
            ToastUtils.showShort(R.string.mod_tip04);
    }

    //
    @OnClick(R.id.btnRefresh)
    public void onViewClicked() {
        modifyMd5();
    }

    public int getSHOW_VIDEO() {
        return SHOW_VIDEO;
    }

    /**
     * 修改Md5的处理代码
     */
    private void modifyMd5() {
        ModifyMD5Task modifyMD5Task = new ModifyMD5Task();
        modifyMD5Task.execute("");

//        setProgressBarValue(1);
//        //文件
//        File file = new File(videoPath);
//        setProgressBarValue(20);
//
//        //切割文件
//        FileUtil.getSplitFile(file,1*1024*1024 );
//        setProgressBarValue(40);
//
//        // 合并文件 获取原文件的后缀名拼接
//        String merFileName = "tempVideo"+FileUtil.suffixName(file);//自定义合并文件名字
//        // 创建合并文件路径
//        tempVideoPath = ConfigureParameter.SYSTEM_CAMERA_PATH + merFileName;//Environment.getExternalStorageDirectory().getPath()+"/"+merFileName;
//        setProgressBarValue(50);
//
//        // 把视频文件分割成临时文件包，再组合拼接成完整的视频文件，过程中随机写入一个字节，这样内容发生改变，MD5值就会改变，视频也不会因此无法正常播放
//        // 再合并文件  把原视频的路径传进去，让新生成的视频文件直接覆盖掉原文件
//        FileUtil.merge(tempVideoPath,file,1*1024*1024);
//        setProgressBarValue(80);
//
//        // 这里读取的时新文件的 MD5
//        String newMd5 = MD5Utils.getFileMD5(new File(tempVideoPath));
//        txtNewMd5.setText(newMd5);
//        setProgressBarValue(100);
//        progressEnd();
    }

    private class ModifyMD5Task extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            publishProgress(1);
            //文件
            File file = new File(videoPath);
            //切割文件
            FileUtil.getSplitFile(file, 1 * 1024 * 1024);
            publishProgress(20);
            // 合并文件 获取原文件的后缀名拼接
            String merFileName = "tempVideo" + FileUtil.suffixName(file);//自定义合并文件名字
            publishProgress(25);
            // 创建合并文件路径
            tempVideoPath = ConfigureParameter.SYSTEM_CAMERA_PATH + merFileName;//Environment.getExternalStorageDirectory().getPath()+"/"+merFileName;
            // 把视频文件分割成临时文件包，再组合拼接成完整的视频文件，过程中随机写入一个字节，这样内容发生改变，MD5值就会改变，视频也不会因此无法正常播放
            // 再合并文件  把原视频的路径传进去，让新生成的视频文件直接覆盖掉原文件
            FileUtil.merge(tempVideoPath, file, 1 * 1024 * 1024);
            publishProgress(60);
            // 这里读取的时新文件的 MD5
            String newMd5 = MD5Utils.getFileMD5(new File(tempVideoPath));
            publishProgress(100);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(101);

            return newMd5;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            txtNewMd5.setText(o.toString());
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            super.onProgressUpdate(values);
            int progress = (int) values[0];
            LOG.showE("进度更新：" + progress);
            if (progress <= 100)
                setProgressBarValue(progress);
            else
                progressEnd();
        }
    }
}
