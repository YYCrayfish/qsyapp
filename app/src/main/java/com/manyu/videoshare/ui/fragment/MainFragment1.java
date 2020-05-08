package com.manyu.videoshare.ui.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.jude.rollviewpager.RollPagerView;
import com.manyu.videoshare.R;
import com.manyu.videoshare.adapter.AskandAnswerAdapter;
import com.manyu.videoshare.adapter.HomeToolsAdapter;
import com.manyu.videoshare.adapter.RollPagerAdapter;
import com.manyu.videoshare.adapter.SupportAdapter;
import com.manyu.videoshare.base.BaseFragment;
import com.manyu.videoshare.base.LoadingDialog;
import com.manyu.videoshare.bean.HomeItemBean;
import com.manyu.videoshare.bean.UserBean;
import com.manyu.videoshare.bean.VersionBean;
import com.manyu.videoshare.dialog.UpdateDialog;
import com.manyu.videoshare.permission.PermissionUtils;
import com.manyu.videoshare.permission.request.IRequestPermissions;
import com.manyu.videoshare.permission.request.RequestPermissions;
import com.manyu.videoshare.permission.requestresult.IRequestPermissionsResult;
import com.manyu.videoshare.permission.requestresult.RequestPermissionsResultSetApp;
import com.manyu.videoshare.ui.MainActivity;
import com.manyu.videoshare.ui.function.AddWaterActivity;
import com.manyu.videoshare.ui.function.CompressVideoActivity;
import com.manyu.videoshare.ui.function.ModifyCoverActivity;
import com.manyu.videoshare.ui.function.ModifyMD5Activity;
import com.manyu.videoshare.ui.function.RemoveWatermarkActivity;
import com.manyu.videoshare.ui.function.SpeedActivity;
import com.manyu.videoshare.ui.function.TimeCutActivity;
import com.manyu.videoshare.ui.function.UpendActivity;
import com.manyu.videoshare.ui.function.VideoClipActivity;
import com.manyu.videoshare.ui.function.VideoExtractActivity;
import com.manyu.videoshare.util.AuthCode;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.manyu.videoshare.util.MD5Utils;
import com.manyu.videoshare.util.ToastUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.manyu.videoshare.view.TimeCutBar;
import com.umeng.commonsdk.debug.I;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class MainFragment1 extends BaseFragment implements View.OnClickListener {
    private View view;

    private RecyclerView rvTools;
    private HomeToolsAdapter adapter;
    private List<HomeItemBean> beans;
    private CardView videoExtract;
    private CardView removeWatermark;
    private MainActivity activity;
    private UserBean userBean;
    private UpdateDialog updateDialog = null;
    private RollPagerView rollPagerView;
    private RollPagerAdapter rollPagerAdapter;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    IRequestPermissions requestPermissions = RequestPermissions.getInstance();//动态权限请求
    IRequestPermissionsResult requestPermissionsResult = RequestPermissionsResultSetApp.getInstance();//动态权限请求结果处理

    public MainFragment1() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main1, container, false);
        activity = (MainActivity) getActivity();
        initView();
        initData();
        return view;
    }

    //请求权限
    private boolean requestPermissions() {
        //需要请求的权限
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        //开始请求权限
        return requestPermissions.requestPermissions(
                getActivity(),
                permissions,
                PermissionUtils.ResultCode1);
    }

    /* //用户授权操作结果（可能授权了，也可能未授权）
     @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         //用户给APP授权的结果
         //判断grantResults是否已全部授权，如果是，执行相应操作，如果否，提醒开启权限
         if(requestPermissionsResult.doRequestPermissionsResult(getActivity(), permissions, grantResults)){
             //请求的权限全部授权成功，此处可以做自己想做的事了
             //输出授权结果
             //Toast.makeText(UserMessageActivity.this,"授权成功，请重新点击刚才的操作！",Toast.LENGTH_LONG).show();
         }else{
             //输出授权结果
             //Toast.makeText(UserMessageActivity.this,"请给APP授权，否则功能无法正常使用！",Toast.LENGTH_LONG).show();
         }
     }*/
    @Override
    public void onResume() {
        super.onResume();
        initRoll();
    }

    private void initView() {
        rollPagerView = view.findViewById(R.id.rollPagerView);
        rvTools = view.findViewById(R.id.rv_tools);
        videoExtract = view.findViewById(R.id.video_extract);
        removeWatermark = view.findViewById(R.id.remove_watermark);
        videoExtract.setOnClickListener(this);
        removeWatermark.setOnClickListener(this);
    }

    private void initRoll() {
        List<Integer> mRollList = new ArrayList<>();
        mRollList.add(R.drawable.home_banner);
//        mRollList.add(R.drawable.main_bannar_2);
        int wid = ToolUtils.getScreenWidth();
        int hei = wid * 360 / 750;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(wid, hei);
        rollPagerView.setLayoutParams(layoutParams);
        rollPagerAdapter = new RollPagerAdapter(mRollList, getActivity());
        if (mRollList.size() <= 1) {
            rollPagerView.setHintView(null);
        }
        rollPagerView.setAdapter(rollPagerAdapter);
        rollPagerView.setPlayDelay(8000);
    }

    private void initData() {
        beans = new ArrayList<>();
        beans.add(new HomeItemBean("添加水印", R.drawable.ic_add_watermark));
        beans.add(new HomeItemBean("调整画布", R.drawable.ic_tailor_video));
        beans.add(new HomeItemBean("裁切时长", R.drawable.ic_cut_time));
        beans.add(new HomeItemBean("视频变速", R.drawable.ic_change_speed));
        beans.add(new HomeItemBean("修改封面", R.drawable.ic_change_cover));
        beans.add(new HomeItemBean("视频倒放", R.drawable.ic_video_upend));
        beans.add(new HomeItemBean("视频压缩", R.drawable.ic_video_zip));
        beans.add(new HomeItemBean("修改MD5", R.drawable.ic_change_md5));
        adapter = new HomeToolsAdapter(getContext(), beans);
        rvTools.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rvTools.setNestedScrollingEnabled(false);
        rvTools.setAdapter(adapter);
        adapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                if (!checkPermissionREAD_EXTERNAL_STORAGE(getActivity())) {
                    return;
                }
                switch (position) {
                    //添加水印
                    case 0:
                        startActivity(new Intent(getContext(), AddWaterActivity.class));
                        break;

                    //调整画布
                    case 1:
                        startActivity(new Intent(getContext(), VideoClipActivity.class));
                        break;

                    //裁切时长
                    case 2:
                        startActivity(new Intent(getContext(), TimeCutActivity.class));
                        break;

                    //视频变速
                    case 3:
                        startActivity(new Intent(getContext(), SpeedActivity.class));
                        break;

                    //修改封面
                    case 4:
                        startActivity(new Intent(getContext(), ModifyCoverActivity.class));
                        break;

                    //视频倒放
                    case 5:
                        startActivity(new Intent(getContext(), UpendActivity.class));
                        break;

                    //视频压缩
                    case 6:
                        startActivity(new Intent(getContext(), CompressVideoActivity.class));
                        break;

                    //修改MD5
                    case 7:
                        startActivity(new Intent(getContext(), ModifyMD5Activity.class));
                        break;
                }
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        checkVersion();
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        ToolUtils.havingIntent(getActivity());
        switch (v.getId()) {
            case R.id.video_extract:
                startActivity(new Intent(getContext(), VideoExtractActivity.class));
                break;

            case R.id.remove_watermark:
                startActivity(new Intent(getContext(), RemoveWatermarkActivity.class));
                break;
        }
    }


    private static final int BAIDU_READ_PHONE_STATE = 100;

    //请求权限
    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, BAIDU_READ_PHONE_STATE);
        } else {
        }
    }

    //Android6.0申请权限的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case BAIDU_READ_PHONE_STATE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                    //okHttpDownLoadApk(downLoadUrl,handler);
                } else {
                    // 没有获取到权限，做特殊处理
                    ToastUtils.showShort("获取存储权限失败，请手动开启");
                }
                break;
            default:
                break;
        }
    }

    public void checkVersion() {
        HttpUtils.httpString(Constants.VERSION, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
            }

            @Override
            public void httpResponse(String resultData) {
                Globals.log(resultData);
                Gson gson = new Gson();
                VersionBean bean = gson.fromJson(resultData, VersionBean.class);
                if (bean.getCode() == 200) {
                    String versionName = ToolUtils.getVersionName(getActivity());
                    VersionBean.DataBean dataBean = bean.getDatas();
                    if (null == dataBean) {
                        return;
                    }
                    int compare = versionName.compareTo(dataBean.getVersions());
                    if (compare < 0 && dataBean.getType() == 1) {
                        //ToastUtils.showShort("有最新版本！");
                        updateDialog = new UpdateDialog(getActivity(), dataBean, 1);
                        updateDialog.show();
                    }
                }
            }
        });
    }
}

