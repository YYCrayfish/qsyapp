package com.manyu.videoshare.ui;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseActivity;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.permission.requestresult.IRequestPermissionsResult;
import com.manyu.videoshare.permission.requestresult.RequestPermissionsResultSetApp;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;

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
        int height = ToolUtils.getScreenHeigh();
        int width = ToolUtils.getScreenWidth();
        int nums = (int) (height * 0.2);
        Bitmap logo = BitmapFactory.decodeResource(getResources(),R.drawable.logo);
        myBitmap = ToolUtils.createQRCode(url,nums,nums,logo);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(nums,nums);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.setMargins(ToolUtils.dip2px(27),0,0,(int)(height * 0.165));
        zxing.setLayoutParams(params);
        zxing.setImageBitmap(myBitmap);

    }

    private void saveShare(String code,Bitmap bitmap){

        Bitmap src = BitmapFactory.decodeResource(getResources(),R.drawable.shareming_back);
        int srcWidth =  src.getWidth();
        int srcHeight = src.getHeight();
        int logWidth = bitmap.getWidth();
        bitmap = ToolUtils.imageScale(bitmap,(int)(srcHeight * 0.2),(int)(srcHeight * 0.2));
        Bitmap back = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(back);
        canvas.drawBitmap(src, 0, 0, null);


        String invite = "我的邀请码：" + code;
        Paint in = new Paint(Paint.ANTI_ALIAS_FLAG);
        in.setDither(true);
        in.setFilterBitmap(true);//过滤一些
        in.setColor(Color.WHITE);
        in.setTypeface(Typeface.DEFAULT_BOLD);
        in.setTextSize(ToolUtils.dip2px(24));
        Rect bound = new Rect();
        in.getTextBounds(invite, 0, invite.length(), bound);
        int inviteLeft = (srcWidth - bound.width()) / 2;
        int inviteTop = (srcHeight - ToolUtils.dip2px(27));
        canvas.drawText(invite,inviteLeft,inviteTop,in);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);//空心矩形框
        paint.setColor(Color.WHITE);
        int left = inviteLeft - ToolUtils.dip2px(16);
        int top = inviteTop - ToolUtils.dip2px(9) - bound.height();
        int right = left + bound.width() + ToolUtils.dip2px(32);
        int bottom = inviteTop + ToolUtils.dip2px(12);
        canvas.drawRect(new RectF(left, top, right, bottom), paint);

        Paint know = new Paint(Paint.ANTI_ALIAS_FLAG);
        know.setDither(true);
        know.setFilterBitmap(true);//过滤一些
        know.setColor(Color.WHITE);
        know.setTextSize(ToolUtils.dip2px(14));
        Rect bounds = new Rect();
        String mText = "长按识别二维码";
        know.getTextBounds(mText, 0, mText.length(), bounds);
        int textKnowLeft = ToolUtils.dip2px(27) + (bitmap.getWidth() - bounds.width()) / 2;//left - ToolUtils.dip2px(12);//(int) ((int)(srcWidth * 0.07)  + (srcHeight * 0.2 - bounds.width())/2) - 10;
        int textKnowTop = (int)(srcHeight *0.64) + bitmap.getHeight() + ToolUtils.dip2px(8);//top - ToolUtils.dip2px(28);//((int)(srcHeight * 0.79) + ToolUtils.dip2px(8)) + 75;
        canvas.drawText(mText,textKnowLeft,textKnowTop,know);


        canvas.drawBitmap(bitmap, ToolUtils.dip2px(27),(int)(srcHeight *0.64) - ToolUtils.dip2px(8), null);



        canvas.save();
        canvas.restore();
        if(ToolUtils.saveBitmap(back)){
            ToastUtils.showShort("保存分享照片到相册成功");
        }else{
            ToastUtils.showShort("保存失败");
        }

    }
    //用户授权操作结果（可能授权了，也可能未授权）
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //用户给APP授权的结果
        //判断grantResults是否已全部授权，如果是，执行相应操作，如果否，提醒开启权限
        if(requestPermissionsResult.doRequestPermissionsResult(this, permissions, grantResults)){
            //请求的权限全部授权成功，此处可以做自己想做的事了
            //输出授权结果
            saveShare(code,myBitmap);
        }else{
            //输出授权结果
        }
    }
    //请求权限
    private boolean requestPermissions(){
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                this,
                permissions,
                PermissionUtils.ResultCode1);
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(this);
        switch (v.getId()){
            case R.id.shareimg_copy:
                ToolUtils.setClipData(url);
                ToastUtils.showShort("已经复制到粘贴板");
                break;
            case R.id.shareimg_save:
                if(!requestPermissions()){
                    return;
                }
                saveShare(code,myBitmap);
                break;
            case R.id.shareimg_img_back:
                finish();
                break;
        }
    }
}
