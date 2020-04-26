package com.manyu.videoshare.base;

import android.app.Application;
import android.content.Context;

import com.manyu.videoshare.util.Constants;
import com.umeng.commonsdk.UMConfigure;

import io.microshow.rxffmpeg.RxFFmpegInvoke;

public class BaseApplication extends Application {
    private static BaseApplication instance;
    private static Context mContext;

    public static BaseApplication getInstance() {
        if (null == instance) {
            instance = new BaseApplication();
        }
        return instance;
    }
    public static Context getContext()
    {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
        if(Constants.LOG_Controll) {
            UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
        }else {
            UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");
        }
        RxFFmpegInvoke.getInstance().setDebug(true);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
