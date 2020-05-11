package com.manyu.videoshare.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.permission.requestresult.IRequestPermissionsResult;
import com.manyu.videoshare.permission.requestresult.RequestPermissionsResultSetApp;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ShareImageActivity extends BaseActivity implements View.OnClickListener {
    private ImageView zxing;
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理
    String code;
    Bitmap myBitmap;
    private ImageView imgsave;
    private ImageView imgcopy;
    private String url;
    private ImageView btnBack;
    private TextView mShredMineInviteCode;
    private LinearLayout mShredInviteWayLayout;
    private RelativeLayout mSharedRootView;
    private String invite;
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_image);
        ToolUtils.setBar(this);
    }

    @Override
    public void initView() {
        zxing = findViewById(R.id.shareimg_zxing);
        imgcopy = findViewById(R.id.shareimg_copy);
        imgsave = findViewById(R.id.shareimg_save);
        btnBack = findViewById(R.id.shareimg_img_back);
        mShredMineInviteCode = findViewById(R.id.share_mine_invite_code);
        mShredInviteWayLayout = findViewById(R.id.share_invite_way_layout);
        mSharedRootView = findViewById(R.id.share_root_layout);
        imgsave.setOnClickListener(this);
        imgcopy.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void initData() {
        //高的0.2大小
        //0.16   0.07
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        code = intent.getStringExtra("code");
        invite = "我的邀请码：" + code;
        mShredMineInviteCode.setText(invite);
        int height = ToolUtils.getScreenHeigh();
        int width = ToolUtils.getScreenWidth();
        int nums = (int) (height * 0.2);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        myBitmap = ToolUtils.createQRCode(url, nums, nums, logo);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(nums, nums);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(ToolUtils.dip2px(27), 0, 0, (int) (height * 0.165));
        zxing.setLayoutParams(params);
        zxing.setImageBitmap(myBitmap);

    }


    //用户授权操作结果（可能授权了，也可能未授权）
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户给APP授权的结果
        //判断grantResults是否已全部授权，如果是，执行相应操作，如果否，提醒开启权限
        if (requestPermissionsResult.doRequestPermissionsResult(this, permissions, grantResults)) {
            //请求的权限全部授权成功，此处可以做自己想做的事了
            //输出授权结果
            saveMyBitmap(String.valueOf(System.currentTimeMillis()), myBitmap);
        } else {
            //输出授权结果
        }
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
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()) {
            case R.id.shareimg_copy:
                ToolUtils.setClipData(url);
                ToastUtils.showShort("已经复制到粘贴板");
                break;
            case R.id.shareimg_save:
                if (!requestPermissions()) {
                    return;
                }
                toggleSharedView(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap mBitmap = createViewBitmap(mSharedRootView);
                        saveMyBitmap(String.valueOf(System.currentTimeMillis()), mBitmap);
                    }
                }, 200);
                break;
            case R.id.shareimg_img_back:
                finish();
                break;
        }
    }

    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    //使用IO流将bitmap对象存到本地指定文件夹
    public void saveMyBitmap(final String bitName, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory().getPath();
                File file = new File(filePath + "/DCIM/Camera/" + bitName + ".png");
                try {
                    file.createNewFile();
                    FileOutputStream fOut = null;
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    Message msg = Message.obtain();
                    msg.obj = file.getPath();
                    handler.sendMessage(msg);
                    //Toast.makeText(PayCodeActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String picFile = (String) msg.obj;
            toggleSharedView(false);
            // 最后通知图库更新
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + picFile)));
            Toast.makeText(ShareImageActivity.this, "图片保存图库成功", Toast.LENGTH_LONG).show();
        }
    };

    private void toggleSharedView(boolean toggle) {
        if (toggle) {
            mShredMineInviteCode.setVisibility(View.VISIBLE);
            mShredInviteWayLayout.setVisibility(View.GONE);
            btnBack.setVisibility(View.GONE);
        } else {
            mShredMineInviteCode.setVisibility(View.GONE);
            mShredInviteWayLayout.setVisibility(View.VISIBLE);
            btnBack.setVisibility(View.VISIBLE);
        }
    }
}
