package com.manyu.videoshare.ui.function;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.util.FFmpegUtil;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.view.SingleSlideSeekBar;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

public class ModifyCoverActivity extends BaseVideoActivity implements View.OnClickListener {

    private ImageView iv_cover;
    private TextView tv_img;
    private SingleSlideSeekBar singleSlideSeekBar;
    private String videoPath;
    private String imagePath;
    private String newPath = Environment.getExternalStorageDirectory()
            + File.separator + Environment.DIRECTORY_DCIM
            + File.separator + "Camera" + File.separator;
    private MediaMetadataRetriever metadataRetriever;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_cover);
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
        singleSlideSeekBar = findViewById(R.id.singleSlideSeekBar);
        singleSlideSeekBar.setOnRangeListener(new SingleSlideSeekBar.onRangeListener() {
            @Override
            public void onRange(float low) {
                Log.e("ssssss", low + "");
                imagePath = "";
                iv_cover.setImageBitmap(getBitmap((long) low));
            }
        });
    }

    private Bitmap getBitmap(long duration) {
        Bitmap bitmap;
        bitmap = metadataRetriever.getFrameAtTime(duration, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        for (long i = duration; i < getLocalVideoDuration(videoPath); i += 100) {
            bitmap = metadataRetriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bitmap != null) {
                break;
            }
        }
        this.bitmap = bitmap;
        return bitmap;
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
                if (imagePath == null || "".equals(imagePath)) {
                    saveBitmapFile(bitmap);
                    imagePath = newPath + "cover_1.jpg";
                }
                modifyCover();
                break;
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

    public void saveBitmapFile(Bitmap bitmap) {
        File file = new File(newPath + "cover_1.jpg");//将要保存图片的路径
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifyCover() {
        newPath = newPath + "mc_" + UriToPathUtil.getFileNameByPath(videoPath);
        String[] commands = FFmpegUtil.image2Video(videoPath,imagePath,newPath);
        RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
            @Override
            public void onFinish() {

                PreviewActivity.start(ModifyCoverActivity.this, newPath);
                UriToPathUtil.deleteSingleFile(imagePath);
                imagePath = "";
                newPath = getCacheDir().getAbsolutePath() + File.separator;
                proessEnd();
                Log.e("ffmpeg_result", "成功");
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                setProess(progress);
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
            finish();
            return;
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
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
            } else if (requestCode == 1) {
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
            finish();
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
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("" +
                        "content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equals(uri.getScheme())) {
            //如果不是document类型的uri，则使用普通的方式处理
            imagePath = getImagePath(uri, null);
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
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    /**
     * 通过 uri seletion选择来获取图片的真实uri
     *
     * @param uri
     * @param seletion
     * @return
     */
    private String getImagePath(Uri uri, String seletion) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, seletion, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagPath) {
        if (imagPath != null) {
            bitmap = BitmapFactory.decodeFile(imagPath);
            iv_cover.setImageBitmap(bitmap);
        } else {
            Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        metadataRetriever.release();
        super.onDestroy();
    }
}
