package com.manyu.videoshare.ui.function;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gyf.immersionbar.ImmersionBar;
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
import com.manyu.videoshare.view.TextWaterMark.WaterMark;
import com.manyu.videoshare.view.TextWaterMark.WaterMarkLayout;
import com.manyu.videoshare.view.scraw.ScrawlBoardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.microshow.rxffmpeg.RxFFmpegInvoke;
import io.microshow.rxffmpeg.RxFFmpegSubscriber;

/**
 * 添加水印
 */

public class AddWaterActivity extends BaseVideoActivity implements View.OnClickListener {
    public static int TABINDEX = -1;
    private ImageView imgWater;
    private ImageView tvWater;
    private ScrawlBoardView scrawl;
    private LinearLayout tvToolbar;
    private LinearLayout color_tool;
    private MyViewPager viewPager;
    private TabLayout tabLayout;
    private View scrawlToolBar;

    private WaterMarkLayout layoutWaterMark;

    private View iv_scrawl;
    private View iv_close_scrawl;
    private View iv_ok_scrawl;
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
    private IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    private TextWaterDialog dialog;

    private int type = 0;
    private String tvColorStr = "ffffff";
    private String tranStr = "";
    private List<BaseFragment> listFragment;
    private VideoViewTool videoViewTool = new VideoViewTool();
    private Unbinder unbinder;

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

    private CardView mVideoViewHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_water);
        unbinder = ButterKnife.bind(this);
        mVideoViewHost = findViewById(R.id.video_view_host);
        mVideoViewHost.setCardBackgroundColor(Color.TRANSPARENT);
        mVideoViewHost.setRadius(ImageUtil.dp2px(this, 12));
        ImmersionBar.with(this).statusBarDarkFont(false).statusBarColorInt(Color.BLACK).init();
        setToolBarColor(Color.BLACK);
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
        layoutWaterMark = findViewById(R.id.layoutWaterMark);
        scrawl = layoutWaterMark.getScrawlBoardView();
        scrawl.setEnabled(false);
//        tvRl = findViewById(R.id.tvRl);
        color_tool = findViewById(R.id.color_tool);
        recycler = findViewById(R.id.recycler);

        iv_close = findViewById(R.id.iv_close);
        iv_edit = findViewById(R.id.iv_edit);
        iv_color = findViewById(R.id.iv_color);
        tv_Aa = findViewById(R.id.tv_Aa);
        iv_ok = findViewById(R.id.iv_ok);
        iv_scrawl = findViewById(R.id.iv_scrawl);
        iv_close_scrawl = findViewById(R.id.iv_close_scrawl);
        iv_ok_scrawl = findViewById(R.id.iv_ok_scrawl);

        iv_ok_scrawl.setOnClickListener(this);
        iv_close_scrawl.setOnClickListener(this);
        iv_scrawl.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        iv_edit.setOnClickListener(this);
        iv_color.setOnClickListener(this);
        tv_Aa.setOnClickListener(this);
        iv_ok.setOnClickListener(this);

        viewPager = findViewById(R.id.viewpage);
        tabLayout = findViewById(R.id.tabLayout);
        imgWater.setOnClickListener(this);
        tvWater.setOnClickListener(this);
        tvToolbar = findViewById(R.id.tv_bottom_toolbar);

        layoutWaterMark.setOnMarkerClickListener(new WaterMarkLayout.OnMarkerClickListener() {
            @Override
            public void onMarkerClick(WaterMark mark) {
                // 点击文字的水印时，显示文字工具
                if (mark == null) {
                    hideAllToolBar();
                } else if (mark.isTextMarker()) {
                    showTvToolBar();
                }
            }
        });

        // 涂鸦相关
        scrawlToolBar = findViewById(R.id.scrawl_bottom_toolbar);
        OperateFragment fragment = OperateFragment.newInstance("", OperateFragment.TYPE_IN_SCRAWL);
        fragment.setOnRevokeListener(new Runnable() {
            @Override
            public void run() {
                scrawl.cancelPath();
            }
        });
        fragment.setOnSelectListener(new OperateFragment.onSelectListener() {
            @Override
            public void onSelectItem(int eventType, String value) {
                int alpha = (int) (255 * (Integer.parseInt(value) / 100.0f));
                scrawl.setPaintAlpha(alpha);
            }

            @Override
            public void onSelect(int eventType, String colorStr) {
                scrawl.setPaintColor(Color.parseColor("#" + colorStr));
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.scrawl_color_tool, fragment).commit();
    }

    @Override
    public void initData() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        // 不能指定路径为data下，不然会无法读取到视频
        outPath = Environment.getExternalStorageDirectory().getPath() + "/qsymy" + File.separator;//getBaseContext().getCacheDir().getAbsolutePath() + File.separator;
        File temp = new File(outPath);
        if (!temp.exists())
            temp.mkdirs();
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
        listFragment.add(OperateFragment.newInstance("底色", 2));
        listFragment.add(OperateFragment.newInstance("阴影", 3));

        for (int i = 0; i < listFragment.size(); i++) {
            ((OperateFragment) listFragment.get(i)).setOnSelectListener(new FragmentDataReceive());
        }

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), listFragment);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(0);
    }

    // Fragment页面的数据回调汇总处理
    private class FragmentDataReceive implements OperateFragment.onSelectListener {

        @Override
        public void onSelectItem(int eventType, String value) {
            int alpha = (int) (255 * (Integer.parseInt(value) / 100.0f));
            if (eventType == 0)
                layoutWaterMark.setWaterMarkTextAlpha(alpha);
            else if (eventType == 1)
                layoutWaterMark.setWaterMarkBorderAlpha(alpha);
            else if (eventType == 2)
                layoutWaterMark.setWaterMarkAlpha(alpha);
            else if (eventType == 3)
                layoutWaterMark.setWaterMarkShadowAlpha(alpha);
        }

        @Override
        public void onSelect(int eventType, String colorStr) {
            if (eventType == 0)
                layoutWaterMark.setWaterMarkTextColor(colorStr);
            else if (eventType == 1)
                layoutWaterMark.setWaterMarkBorderColor(colorStr);
            else if (eventType == 2)
                layoutWaterMark.setWaterMarkColor(colorStr);
            else if (eventType == 3)
                layoutWaterMark.setWaterMarkShadowColor(colorStr);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 0x233 && resultCode == RESULT_CANCELED) {
            finish();
        } else if (resultCode == RESULT_OK) {
            if (requestCode == 0x233) {
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
                final VideoView videoView = videoViewTool.videoView;
                final View videoCover = findViewById(R.id.vv_cover);
                final FrameLayout.LayoutParams mLayoutWaterMarkParams = (FrameLayout.LayoutParams) layoutWaterMark.getLayoutParams();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                            @Override
                            public void onVideoSizeChanged(final MediaPlayer mp, int width, int height) {
                                //获取视频资源的宽度
                                videoW = mp.getVideoWidth();
                                //获取视频资源的高度
                                videoH = mp.getVideoHeight();
                                View parent = (View) mVideoViewHost.getParent();

                                // 按原视频的比例，缩放至视频的最长边和容器的最短边相等
                                ConstraintLayout.LayoutParams videoLp = (ConstraintLayout.LayoutParams) mVideoViewHost.getLayoutParams();
                                if ((1f * videoW / videoH) > (1f * parent.getWidth() / parent.getHeight())) {
                                    videoLp.dimensionRatio = "h," + videoW + ":" + videoH;
                                } else {
                                    videoLp.dimensionRatio = "w," + videoW + ":" + videoH;
                                }
                                mVideoViewHost.setLayoutParams(videoLp);

                                ViewGroup.LayoutParams layoutParams = layoutWaterMark.getLayoutParams();
                                layoutParams.width = videoViewTool.videoView.getWidth();
                                layoutParams.height = videoViewTool.videoView.getHeight();
                                layoutWaterMark.setLayoutParams(layoutParams);

                                videoViewTool.videoSeekBar.reset();
                                videoCover.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        videoCover.setVisibility(View.GONE);
                                    }
                                }, 500);
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
        if (unbinder != null)
            unbinder.unbind();
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
        // 这里把Text文本转为了图片 因为添加水印时，需要传一个水印地图地址的参数
        tvToImg();
        imagePath = outPath + "qsy_aw_tv.png";//文字转图片暂存路径
        overlay = "overlay=" + 0 + ":" + 0;

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
                    progressEnd();
                    Log.e("ffmpeg_result", "成功");
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
        } catch (Exception e) {
            Log.e("xushiyong", "抛出异常~~" + e.toString());
        }
    }

    private void tvToImg() {
        layoutWaterMark.hideAllController();
        FileOutputStream outputStream = null;
        Bitmap b = ImageUtil.getBitmap(layoutWaterMark);
        Bitmap ret = Bitmap.createScaledBitmap(b, videoW, videoH, true);
        try {
            outputStream = new FileOutputStream(outPath + "qsy_aw_tv.png");
            ret.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
                b.recycle();
                ret.recycle();
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
            layoutWaterMark.setVisibility(View.VISIBLE);
            WaterMark waterMark = new WaterMark(AddWaterActivity.this);
            waterMark.setImage(imagPath);
            layoutWaterMark.addWaterMark(waterMark);
            scrawl.setEnabled(false);
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
                    Toast.makeText(this, "请添加水印后再次点击", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!layoutWaterMark.hasMark()) {
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
                startActivityForResult(intent, 0x233);
                break;
            case R.id.iv_edit:
            case R.id.text:
                initDialog();
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
            case R.id.iv_scrawl:
                showScrawToolBar();
                break;
            case R.id.iv_close:
            case R.id.iv_ok:
            case R.id.iv_close_scrawl:
            case R.id.iv_ok_scrawl:
                hideAllToolBar();
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
                        showTvToolBar();
                        WaterMark waterMark = new WaterMark(AddWaterActivity.this);
                        waterMark.setText(content);
                        layoutWaterMark.addWaterMark(waterMark);
                    }
                }
            });
        }
        dialog.show();
    }

    private void showScrawToolBar() {
        tvToolbar.setVisibility(View.GONE);
        scrawlToolBar.setVisibility(View.VISIBLE);
        layoutWaterMark.setVisibility(View.VISIBLE);
        scrawl.setEnabled(true);
        type = 3;
    }

    private void showTvToolBar() {
        type = 1;
        tvToolbar.setVisibility(View.VISIBLE);
        scrawlToolBar.setVisibility(View.GONE);
        layoutWaterMark.setVisibility(View.VISIBLE);
        scrawl.setEnabled(false);
    }

    private void hideAllToolBar() {
        tvToolbar.setVisibility(View.GONE);
        scrawlToolBar.setVisibility(View.GONE);
        layoutWaterMark.hideAllController();
        layoutWaterMark.showAllMark();
        scrawl.setEnabled(false);
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
