package com.manyu.videoshare.base;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.manyu.videoshare.bean.AnalysisTimeBean;
import com.manyu.videoshare.util.Constants;
import com.manyu.videoshare.util.Globals;
import com.manyu.videoshare.util.HttpUtils;
import com.umeng.commonsdk.UMConfigure;

import io.microshow.rxffmpeg.RxFFmpegInvoke;
import okhttp3.Call;

public class BaseApplication extends Application {
    private static BaseApplication instance;
    private static Context mContext;
    private int userAnalysisTime = 0;//设置可保存次数

    public static BaseApplication getInstance() {
        if (null == instance) {
            instance = new BaseApplication();
        }
        return instance;
    }

    public static Context getContext() {
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        if (Constants.LOG_Controll) {
            UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
        } else {
            UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
        }
        RxFFmpegInvoke.getInstance().setDebug(true);
        getAnalysisTime();
    }

    //TODO 查询次数
    public void getAnalysisTime() {
        HttpUtils.httpString(Constants.ANALYTIC, null, new HttpUtils.HttpCallback() {
            @Override
            public void httpError(Call call, Exception e) {
                LoadingDialog.closeLoadingDialog();
            }

            @Override
            public void httpResponse(String resultData) {
                Gson gson = new Gson();
                Globals.log(resultData);
                AnalysisTimeBean timeBean = gson.fromJson(resultData, AnalysisTimeBean.class);
                LoadingDialog.closeLoadingDialog();
                BaseApplication.getInstance().setUserAnalysisTime(timeBean.getData());
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public int getUserAnalysisTime() {
        return userAnalysisTime;
    }

    public void setUserAnalysisTime(int userAnalysisTime) {
        this.userAnalysisTime = userAnalysisTime;
    }
}
