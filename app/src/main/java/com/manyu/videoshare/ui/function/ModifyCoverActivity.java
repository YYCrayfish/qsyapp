package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.FFmpegUtil;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.universally.FileUtil;
import com.manyu.videoshare.util.universally.LOG;
import com.manyu.videoshare.view.SingleSlideSeekBar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

/**
 * add by xushiyong START on 2020/5/1 desc: 修改视频封面的页面
 **/
public class ModifyCoverActivity extends BaseVideoActivity implements View.OnClickListener, SingleSlideSeekBar.onRangeListener {

    private ImageView iv_cover;
    private TextView tv_img;
    private SingleSlideSeekBar singleSlideSeekBar;
    private String videoPath;
    private String imagePath;
    private String newPath = Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;
    private String filePath = Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;
    private MediaMetadataRetriever metadataRetriever;
    private Bitmap bitmap;
    // 选择图片的模式  0|缩略图里选  1|相册中选
    private int selectImageMode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_cover);
        ImmersionBar.with(this).statusBarDarkFont(false).statusBarColorInt(Color.BLACK).init();
        setToolBarColor(Color.BLACK);
        start(this);
    }

    public void start(Context context) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, 1);
    }

    @Override
    protected String getTitleTv() {
        return "修改封面";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
        iv_cover = findViewById(R.id.iv_cover);
        tv_img = findViewById(R.id.tv_img);
        tv_img.setOnClickListener(this);

        // 选择封面图的那个拖拉组件
        singleSlideSeekBar = findViewById(R.id.singleSlideSeekBar);
        singleSlideSeekBar.setOnRangeListener(this);
    }

    @Override
    public void onRange(float low) {
        imagePath = "";
        this.selectImageMode = 0;
        iv_cover.setImageBitmap(getBitmap((long) low));
    }

    private Bitmap getBitmap(long duration) {
        for (long i = duration; i < getLocalVideoDuration(videoPath); i += 100) {
            bitmap = metadataRetriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bitmap != null) {
                break;
            }
        }
        return bitmap;
    }

    @Override
    public void initData() {
        metadataRetriever = new MediaMetadataRetriever();
        /** delete by xushiyong START on 2020/4/28 for: **/
        //newPath = getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            // 下一步的操作
            case R.id.title_right:
                if (imagePath == null || "".equals(imagePath)) {
                    saveBitmapFile();
                    imagePath = newPath + "cover_1.jpg";
                }
                modifyCover();
                break;
            // 自行选择相册的图片来做封面
            case R.id.tv_img:
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                //根据版本号不同使用不同的Action
                if (Build.VERSION.SDK_INT < 19) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                startActivityForResult(intent, 2);
                break;
        }
    }

    public void saveBitmapFile() {
        if (bitmap == null && (imagePath == null || "".equals(imagePath))) return;

        FileOutputStream fileOutputStream = null;

        try {
            Bitmap bitmap;
            if (selectImageMode == 0)
                bitmap = this.bitmap;
            else
                bitmap = FileUtil.loadBitmap(imagePath, true);
            LOG.showE("加载新的位图：" + bitmap);
            // 这里需要处理下，可能相机中的旋转角度问题 导致了合成图片时，根据第一张图的水平
            fileOutputStream = new FileOutputStream(newPath + "cover_1.jpg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {

        }
//
//        File file = new File(newPath + "cover_1.jpg");//将要保存图片的路径
//        try {
//            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            bos.flush();
//            bos.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    // 统一设置进度值，这里方便统一更改，如果后面想改用其他方式显示进度的话
    private void setProgressBar(int progress, long time) {
        setProgressBarValue(progress);
    }

    // 第一步：分离音频
    private void modifyStep1() {
        File file = new File(filePath);
        if (!file.exists())
            file.mkdirs();
        String[] commands = FFmpegUtil.disVoice(videoPath, filePath + "copy.mp3");
        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                LOG.showE("合成分离音频完成");
                progressEnd();
                modifyStep2();
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                setProgressBar(progress, progressTime);
                LOG.showE("合成分离音频进程：" + progress + "  " + progressTime);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(String message) {
                LOG.showE("合成分离音频错误：" + message);
            }
        });
    }

    // 第二步: 分解图片序列帧
    private void modifyStep2() {
        String pagesPath = filePath + "pages" + File.separator;
        File file = new File(pagesPath);
        // 如果不存在路径就创建  如果已存在就删除  清空这个文件夹，避免合成视频时把其他图片也添加进去合成了
        FileUtil.deleteFolder(pagesPath);
        if (!file.exists())
            file.mkdirs();

        String[] commands = FFmpegUtil.disVideoPage(videoPath, pagesPath);
        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                LOG.showE("合成分解图片完成");
                progressEnd();
                modifyStep3();
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                setProgressBar(progress, progressTime);
                LOG.showE("合成分解图片进程：" + progress + "  " + progressTime);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(String message) {
                LOG.showE("合成分解图片错误：" + message);
            }
        });
    }

    // 第三步：替换第一帧图片
    private void modifyStep3() {
        FileUtil.fileCopy(imagePath, filePath + "pages" + File.separator + "0001.jpg", filePath + "pages" + File.separator + "0002.jpg");
        modifyStep4();
        LOG.showE("合成封面文件替换完成");
    }

    // 第四步：合成视频  音频 + 图片序列
    private void modifyStep4() {
        String[] commands = FFmpegUtil.buildFullVideo(filePath + "pages" + File.separator, filePath + "copy.mp3", filePath + "xushiyong.mp4");
        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {
                PreviewActivity.start(ModifyCoverActivity.this, filePath + "xushiyong.mp4");
                progressEnd();
                LOG.showE("合成视频成功");
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                setProgressBar(progress, progressTime);
                LOG.showE("合成视频进度:" + progress);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(String message) {
                LOG.showE("合成视频失败");
            }
        });
    }

    // 修改封面
    private void modifyCover() {
        modifyStep1();
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
                    singleSlideSeekBar.setBitmap(bitmap);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            return;
        } else if (resultCode == RESULT_OK) {
            // 选择图片
            if (requestCode == 2) {
                this.selectImageMode = 1;
                /**
                 * 判断手机版本，因为在4.4版本都手机处理图片返回的方法就不一样了
                 * 4.4以后返回的不是真实的uti而是一个封装过后的uri 所以要对封装过后的uri进行解析
                 */
                if (Build.VERSION.SDK_INT >= 19) {
                    //4.4系统一上用该方法解析返回图片
                    handleImageOnKitKat(data);
                } else {
                    //4.4一下用该方法解析图片的获取
                    handleImageBeforeKitKat(data);
                }
            }
            // 选择视频
            else if (requestCode == 1) {
                Uri uri = data.getData();
                videoPath = UriToPathUtil.getRealFilePath(this, uri);
                singleSlideSeekBar.setBigValue(getLocalVideoDuration(videoPath));
                singleSlideSeekBar.invalidate();
                metadataRetriever.setDataSource(videoPath);
                metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                iv_cover.setImageBitmap(getBitmap(0));
                handler.sendEmptyMessageDelayed(1, 100);
            }
        } else if (resultCode == 100) {
        }
    }

    /**
     * api 19以后
     * 4.4版本后 调用系统相机返回的不在是真实的uri 而是经过封装过后的uri，
     * 所以要对其记性数据解析，然后在调用displayImage方法尽心显示
     *
     * @param data
     */
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            //如果是document类型的uri 则通过id进行解析处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                //解析出数字格式id
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = FileUtil.getImagePath(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("" +
                        "content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = FileUtil.getImagePath(this, contentUri, null);
            }
        } else if ("content".equals(uri.getScheme())) {
            //如果不是document类型的uri，则使用普通的方式处理
            imagePath = FileUtil.getImagePath(this, uri, null);
        }
        displayImage(imagePath);
    }

    /**
     * 4.4版本一下 直接获取uri进行图片处理
     *
     * @param data
     */
    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = FileUtil.getImagePath(this, uri, null);
        this.imagePath = imagePath;
        displayImage(imagePath);
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            bitmap = FileUtil.loadBitmap(imagePath, true);
            iv_cover.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        if (metadataRetriever != null)
            metadataRetriever.release();
        super.onDestroy();
    }

    private class LoadImagesTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
//            /**
//             * 判断手机版本，因为在4.4版本都手机处理图片返回的方法就不一样了
//             * 4.4以后返回的不是真实的uti而是一个封装过后的uri 所以要对封装过后的uri进行解析
//             */
//            if (Build.VERSION.SDK_INT >= 19) {
//                //4.4系统一上用该方法解析返回图片
//                handleImageOnKitKat(data);
//            } else {
//                //4.4一下用该方法解析图片的获取
//                handleImageBeforeKitKat(data);
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
        }
    }

}
