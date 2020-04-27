package com.manyu.videoshare.ui.function;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseFragment;
import com.manyu.videoshare.base.BaseVideoActivity;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.ui.dialog.TextWaterDialog;
import com.manyu.videoshare.ui.fragment.OperateFragment;
import com.manyu.videoshare.util.FFmpegUtil;
import com.manyu.videoshare.util.ImageUtil;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.util.UriToPathUtil;
import com.manyu.videoshare.util.VideoViewTool;
import com.manyu.videoshare.view.MyViewPager;
import com.manyu.videoshare.view.StrokeText;
import com.manyu.videoshare.view.TextRelativeLayout;
import com.manyu.videoshare.view.WaterMark;
import com.manyu.videoshare.view.WaterMarkLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

/**
 * 添加水印
 */

public class AddWaterActivity extends BaseVideoActivity implements View.OnClickListener {
    public static int TABINDEX = -1;
    private ImageView imgWater;
    private ImageView tvWater;
    private RelativeLayout rlImg;
    private RelativeLayout rltv;
    private LinearLayout tvToolbar;
    private LinearLayout color_tool;
    private ImageView moveIv;
    private StrokeText moveTv;
    private MyViewPager viewPager;
    private TabLayout tabLayout;

    private WaterMarkLayout layoutWaterMark;

    private ImageView iv_close;
    private ImageView iv_edit;
    private ImageView iv_color;
    private TextView tv_Aa;
    private ImageView iv_ok;
    private RecyclerView recycler;

//    private TextRelativeLayout tvRl;

    private int screenWidth;
    private int screenHeight;
    private int videoW;
    private int videoH;
    private int tvW;
    private int tvH;
    private String imagePath;//图片水印路径
    private String videoPath;//原视频路径
    private String outPath;//视频输出暂存路径
    private String overlay = "overlay=main_w/2-overlay_w/2:main_h/2-overlay_h/2";
    private Bitmap bitmap = null;
    private IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    private TextWaterDialog dialog;

    private int type = 0;
    private String tvColorStr = "ffffff";
    private String tranStr = "";
    private List<BaseFragment> listFragment;
    private VideoViewTool videoViewTool = new VideoViewTool();

    public void start(Context context) {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(intent, 2);
    }

    //请求权限
    private boolean requestPermissions() {
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                this,
                permissions,
                PermissionUtils.ResultCode1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_water);
        start(this);
    }

    @Override
    protected String getTitleTv() {
        return "添加水印";
    }

    @Override
    public void initView() {
        ToolUtils.setBar(this);
        imgWater = findViewById(R.id.img);
        tvWater = findViewById(R.id.text);
        rlImg = findViewById(R.id.rl_image);
        rltv = findViewById(R.id.rl_text);
        layoutWaterMark = findViewById(R.id.layoutWaterMark);
//        tvRl = findViewById(R.id.tvRl);
        color_tool = findViewById(R.id.color_tool);
        recycler = findViewById(R.id.recycler);

        iv_close = findViewById(R.id.iv_close);
        iv_edit = findViewById(R.id.iv_edit);
        iv_color = findViewById(R.id.iv_color);
        tv_Aa = findViewById(R.id.tv_Aa);
        iv_ok = findViewById(R.id.iv_ok);

        iv_close.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        iv_color.setOnClickListener(this);
        tv_Aa.setOnClickListener(this);
        iv_ok.setOnClickListener(this);

        viewPager = findViewById(R.id.viewpage);
        tabLayout = findViewById(R.id.tabLayout);
        imgWater.setOnClickListener(this);
        tvWater.setOnClickListener(this);
        moveIv = findViewById(R.id.move_iv);
        moveTv = findViewById(R.id.move_tv);
        tvToolbar = findViewById(R.id.tv_bottom_toolbar);
    }

    @Override
    public void initData() {
        List<String> list = new ArrayList<>();
        for (int i = 10; i <= 100; i++) {
            list.add("" + i);
        }
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        // 不能指定路径为data下，不然会无法读取到视频
        outPath = Environment.getExternalStorageDirectory().getPath()+"/qsymy"+File.separator;//getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
        File temp = new File(outPath);
        if(!temp.exists())
            temp.mkdirs();
        moveIv = findViewById(R.id.move_iv);
        moveIv.setOnTouchListener(movingEventListener);
        moveTv.setOnTouchListener(movingEventListener);
        if (TABINDEX != -1) {
            viewPager.setCurrentItem(TABINDEX);
        }
        initFragment();
    }

    public void initFragment() {
        if (listFragment == null) {
            listFragment = new ArrayList<>();
        }
        listFragment.add(OperateFragment.newInstance("文字", 0));
        listFragment.add(OperateFragment.newInstance("边框", 1));
        listFragment.add(OperateFragment.newInstance("底色", 1));
        listFragment.add(OperateFragment.newInstance("阴影", 1));

        for (int i = 0; i < listFragment.size(); i++) {
            ((OperateFragment) listFragment.get(i)).setOnSelectListener(new OperateFragment.onSelectListener() {
                @Override
                public void onSelectItem(String value) {
                    tranStr = Integer.toHexString((int) (255 * (Double.parseDouble(value) / 100)));
//                    tvRl.addTextView(videoW / 2, videoH / 2, "", viewPager.getCurrentItem(), tranStr + tvColorStr);
                }

                @Override
                public void onSelect(String colorStr) {
                    tvColorStr = colorStr;
//                    tvRl.addTextView(videoW / 2, videoH / 2, "", viewPager.getCurrentItem(), tranStr + tvColorStr);
                }
            });
        }
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), listFragment);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }

    private View.OnTouchListener movingEventListener = new View.OnTouchListener() {
        int lastX, lastY, x, y;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int dx = (int) event.getRawX() - lastX;
                    int dy = (int) event.getRawY() - lastY;

                    int left = v.getLeft() + dx;
                    int top = v.getTop() + dy;
                    int right = v.getRight() + dx;
                    int bottom = v.getBottom() + dy;
                    // 设置不能出界
                    if (left < 0) {
                        left = 0;
                        right = left + v.getWidth();
                    }

                    if (right > screenWidth) {
                        right = screenWidth;
                        left = right - v.getWidth();
                    }

                    if (top < 0) {
                        top = 0;
                        bottom = top + v.getHeight();
                    }

                    if (bottom > screenHeight) {
                        bottom = screenHeight;
                        top = bottom - v.getHeight();
                    }

                    v.layout(left, top, right, bottom);
                    tvW = right - left;
                    tvH = bottom - top;
                    lastX = (int) event.getRawX();
                    lastY = (int) event.getRawY();
                    if (videoW > videoH) {
                        if (videoW > screenWidth) {
                            overlay = "overlay=" + (left * (videoW / screenWidth)) + ":" + (top * (videoW / screenWidth));
                        } else {
                            overlay = "overlay=" + left + ":" + top;
                        }
                    } else {
                        if (videoH > screenHeight) {
                            overlay = "overlay=" + (left * (videoH / screenHeight)) + ":" + (top * (videoH / screenHeight));
                        } else {
                            overlay = "overlay=" + left + ":" + top;
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    //检测移动的距离，如果很微小可以认为是点击事件
                    if (Math.abs(event.getRawX() - x) < 10 && Math.abs(event.getRawY() - y) < 10) {
                        try {
                            Field field = View.class.getDeclaredField("mListenerInfo");
                            field.setAccessible(true);
                            Object object = field.get(v);
                            field = object.getClass().getDeclaredField("mOnClickListener");
                            field.setAccessible(true);
                            object = field.get(object);
                            if (object != null && object instanceof View.OnClickListener) {
                                ((View.OnClickListener) object).onClick(v);
                            }
                        } catch (Exception e) {
                        }
                    }
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0) {
            finish();
            return;
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                /**
                 * 判断手机版本，因为在4.4版本都手机处理图片返回的方法就不一样了
                 * 4.4以后返回的不是真实的uti而是一个封装过后的uri 所以要对封装过后的uri进行解析
                 */
                setHandW();
                if (Build.VERSION.SDK_INT >= 19) {
                    //4.4系统一上用该方法解析返回图片
                    handleImageOnKitKat(data);
                } else {
                    //4.4一下用该方法解析图片的获取
                    handleImageBeforeKitKat(data);
                }
            } else if (requestCode == 2) {
                Uri uri = data.getData();
                videoPath = UriToPathUtil.getRealFilePath(this, uri);
                videoViewTool.init(AddWaterActivity.this, null, uri);
                setHandW();
                videoViewTool.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        videoViewTool.videoSeekBar.reset();
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
            }
        } else if (resultCode == 100) {
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoViewTool != null) {
            videoViewTool.videoResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoViewTool != null) {
            videoViewTool.videoPuase();
        }
    }

    private void addWater(String path) {
        if (type == 1) {

            // 这里把Text文本转为了图片 因为添加水印时，需要传一个水印地图地址的参数
            tvToImg();

            imagePath = outPath + "qsy_aw_tv.png";//文字转图片暂存路径
            overlay = "overlay=" + 0 + ":" + 0;
        }
        outPath = outPath + "aw_" + UriToPathUtil.getFileNameByPath(path);
        ///storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1587654132226.mp4,
        // //data/user/0/com.manyu.videoshare/cache/qsy_aw_tv.png,
        // overlay=0:0,
        // /data/user/0/com.manyu.videoshare/cache/aw_wx_camera_1587654132226.mp4
        String[] commands = FFmpegUtil.addWaterMark(path, imagePath, overlay, outPath);

        try {
            RxFFmpegInvoke.getInstance().runCommandRxJava(commands).subscribe(new RxFFmpegSubscriber() {
                @Override
                public void onFinish() {

                    PreviewActivity.start(AddWaterActivity.this, outPath);
                    UriToPathUtil.deleteSingleFile(imagePath);
                    imagePath = "";
                    outPath = getCacheDir().getAbsolutePath() + File.separator;
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
        }catch (Exception e){
            Log.e("xushiyong","抛出异常~~"+e.toString());
        }
    }

    private void tvToImg() {
//        Bitmap b = ImageUtil.getBitmap(tvRl);
        Bitmap b = ImageUtil.getBitmap(layoutWaterMark);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outPath + "qsy_aw_tv.png");
            b.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setHandW() {
        ViewTreeObserver vto = videoViewTool.videoView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                videoViewTool.videoView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                screenWidth = videoViewTool.videoView.getMeasuredWidth();
                screenHeight = videoViewTool.videoView.getMeasuredHeight();
            }
        });
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
            type = 2;
            bitmap = BitmapFactory.decodeFile(imagPath);
            moveIv.setImageBitmap(bitmap);
            rlImg.setVisibility(View.VISIBLE);
            videoViewTool.videoStart();
        } else {
            Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.title_right:
                if (type == 0) {
                    return;
                }
                if (bitmap == null && type == 2) {
                    return;
                }
                if (!requestPermissions()) {
                    return;
                }
                videoViewTool.videoPuase();
                addWater(videoPath);
                break;
            case R.id.img:
                videoViewTool.videoPuase();
                Intent intent = new Intent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                //根据版本号不同使用不同的Action
                if (Build.VERSION.SDK_INT < 19) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                startActivityForResult(intent, 1);
                break;
            case R.id.iv_edit:
            case R.id.text:
                initDialog();
                break;

            case R.id.iv_close:
                tvToolbar.setVisibility(View.GONE);
                break;
            case R.id.iv_color:
                iv_color.setBackground(getResources().getDrawable(R.drawable.shape_323232_co4));
                tv_Aa.setBackgroundColor(getResources().getColor(R.color.tran));
                color_tool.setVisibility(View.VISIBLE);
                recycler.setVisibility(View.GONE);
                break;
            case R.id.tv_Aa:
                tv_Aa.setBackground(getResources().getDrawable(R.drawable.shape_323232_co4));
                iv_color.setBackgroundColor(getResources().getColor(R.color.tran));
                color_tool.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_ok:
                tvToolbar.setVisibility(View.GONE);
                break;
        }
    }

    private void initDialog() {
        if (dialog == null) {
            dialog = new TextWaterDialog(this);
            dialog.setOnClickListener(new TextWaterDialog.OnClickListener() {
                @Override
                public void getText(String content) {
                    if (!TextUtils.isEmpty(content)) {
                        type = 1;
                        tvToolbar.setVisibility(View.VISIBLE);
//                        tvRl.setVisibility(View.VISIBLE);
//                        tvRl.addTextView(videoW / 2, videoH / 2, content, 0, tvColorStr);
                        layoutWaterMark.setVisibility(View.VISIBLE);
                        WaterMark waterMark = new WaterMark(AddWaterActivity.this);
                        waterMark.setText(content);
                        layoutWaterMark.addWaterMark(waterMark);
                    }
                }
            });
        }
        dialog.show();
    }

    public static class PagerAdapter extends FragmentPagerAdapter {
        List<BaseFragment> mFragments;

        public PagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragments) {
            super(fragmentManager);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).getmFragmentTitle();
        }
    }
}
