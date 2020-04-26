package com.manyu.videoshare.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;


import com.manyu.videoshare.R;

import java.util.HashMap;
import java.util.List;


/**
 * Created by Leo on 2017/7/10.
 */

public class LoadingDialog extends BaseDialog {

    private LayoutInflater mInflater;

    private static LoadingDialog mLoadingDialog;

    public static void showLoadingDialog(Context context) {

        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.cancel();
                mLoadingDialog = null;
            }
            if (context != null) {
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing()) {  //存在
                        mLoadingDialog = new LoadingDialog(context);
                        mLoadingDialog.show();
                    }

                } else if (context instanceof Service) {
                    if (isServiceExisted(context, context.getClass().getName())) {
                        mLoadingDialog = new LoadingDialog(context);
                        mLoadingDialog.show();
                    }
                } else if (context instanceof Application) {
                    mLoadingDialog = new LoadingDialog(context);
                    mLoadingDialog.show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void closeLoadingDialog() {

        try {
            if (mLoadingDialog != null) {
                mLoadingDialog.cancel();
                mLoadingDialog = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public LoadingDialog(Context context) {
        super(context);
        initView(context);
    }


    @Override
    public void initView(Context context) {

        mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.dialog_loading, null);
        setContentView(view);
//        ImageView imgView = (ImageView) view.findViewById(R.id.loading);
//
//        Glide.with(context).load(R.drawable.loading_list).asGif().into(imgView);
//
//
//        AnimationSet  inDefaultAnim = new AnimationSet(true);
//        ScaleAnimation scaleAnimation = new ScaleAnimation(2, 1f, 2,
//                1f, context.getResources().getDisplayMetrics().widthPixels * 0.5f,  context.getResources().getDisplayMetrics().heightPixels * 0.45f);
//        inDefaultAnim.addAnimation(scaleAnimation);
//        imgView.setImageDrawable(context.getResources().getDrawable(R.drawable.load_e));
//        imgView.startAnimation(inDefaultAnim);
    }


    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            ActivityManager.RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;

            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void initData(HashMap<String, Object> hashMap) {

    }
}
