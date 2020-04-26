package com.manyu.videoshare.dialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.bean.VersionBean;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.permission.requestresult.IRequestPermissionsResult;
import com.manyu.videoshare.permission.requestresult.RequestPermissionsResultSetApp;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;


public class UpdateDialog extends Dialog {
    private Activity mContext;
    private TextView textpercent;
    private SeekBar seekBar;
    private VersionBean.DataBean bean;
    private LinearLayout layoutBar;
    private LinearLayout layoutText;
    private TextView textVersion;
    private TextView textContent;
    private Button btnConfirm;
    private Button btnCancle;
    //type = 1为强制更新，无法取消，其它可取消
    private int type = 0;
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理
    public UpdateDialog(Activity context, VersionBean.DataBean bean,int type) {
        super(context, R.style.dialog_clip);
        this.mContext = context;
        this.bean = bean;
        this.type = type;
        initView();
    }


    private void initView()  {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_update, null);
        setContentView(view);
        setCancelable(false);
        textpercent = view.findViewById(R.id.dialog_update_text);
        seekBar = view.findViewById(R.id.dialog_update_seekbar);
        layoutBar = view.findViewById(R.id.dialog_update_barlayout);
        layoutText = view.findViewById(R.id.dialog_update_textlayout);
        textVersion = view.findViewById(R.id.dialog_update_version);
        textContent = view.findViewById(R.id.dialog_update_content);
        btnConfirm = view.findViewById(R.id.dialog_update_confirm);
        btnCancle = view.findViewById(R.id.dialog_update_cancle);


        if(type != 1){
            btnCancle.setVisibility(View.VISIBLE);
        }
        textVersion.setText("最新版本：" + bean.getVersions());
        textContent.setText("更新内容：" + bean.getContent());

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!requestPermissions()){
                    return;
                }
                ToolUtils.havingIntent(mContext);
                layoutBar.setVisibility(View.VISIBLE);
                layoutText.setVisibility(View.GONE);
                okHttpDownLoadApk(bean.getUrl());
            }
        });
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
    //请求权限
    private boolean requestPermissions(){
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                mContext,
                permissions,
                PermissionUtils.ResultCode1);
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    int pro = msg.getData().getInt("msg");
                    seekBar.setProgress(pro);
                    textpercent.setText(pro + "%");
                    break;
            }
            return false;
        }
    });
    public void okHttpDownLoadApk(String url) {
        String destFileDir = Environment.getExternalStorageDirectory() + "/manyu/";
        File dir = new File(destFileDir);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        final String path = Environment.getExternalStorageDirectory() + "/manyu/";
        String md5 = "去水印_" + bean.getVersions() + ".apk";
        final String name = path + md5;
        File file = new File(name);
        if(file.exists()){
            ToastUtils.showShort("APP已存在，无须重新下载");
            layoutBar.setVisibility(View.GONE);
            layoutText.setVisibility(View.VISIBLE);
            ToolUtils.installApk(name,mContext);
            return;
        }
        /*File myCaptureFile = new File(path, md5);
        try {
            myCaptureFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(path, md5) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onResponse(File response, int id) {

                    }

                    @Override
                    public void inProgress(final float progress, long total, int id) {
                        super.inProgress(progress, total, id);
                        int pro = (int) (100 * progress);
                        Message msg = new Message();
                        msg.what = 0;
                        Bundle b = new Bundle();
                        b.putInt("msg",pro);
                        msg.setData(b);
                        handler.sendMessage(msg);
                        if(pro>=100) {
                            layoutBar.setVisibility(View.GONE);
                            layoutText.setVisibility(View.VISIBLE);
                            ToolUtils.installApk(name,mContext);
                        }
                    }
                });
    }

}
