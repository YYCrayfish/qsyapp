package com.manyu.videoshare.base;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Created by admin on 2017/3/8.
 */
public class BaseSharePerence {
    private static final String TAG = BaseSharePerence.class.getSimpleName();


    public static final String NAME_HOUSE_SHARE = "name_house";   //缓存文件名


    private final SharedPreferences mSharedPreferences;
    private static BaseSharePerence mInstance;
    private static Object mSyncLock = new Object();

    private Context mContext;

    public static BaseSharePerence getInstance() {
        synchronized (mSyncLock) {

            if (mInstance == null) {
                mInstance = new BaseSharePerence(BaseApplication.getContext());
            }
        }
        return mInstance;
    }

    private BaseSharePerence(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(NAME_HOUSE_SHARE, Context.MODE_PRIVATE);
    }


    public void removeInstance() {
        if (mInstance != null) {
            mInstance.removeInstance();
            mInstance = null;
        }
    }


    /**
     * 是否第一次打开
     *
     * @param firstTime
     */
    public void setFirst(boolean firstTime) {
        mSharedPreferences.edit().putBoolean("setFirst", firstTime).commit();
    }

    public boolean getFirst() {
        return mSharedPreferences.getBoolean("setFirst", true);
    }


    /**
     * 是否刚安装
     *
     * @param isInstall
     */
    public void setIsInstall(boolean isInstall) {
        mSharedPreferences.edit().putBoolean("isInstall", isInstall).commit();
    }

    public boolean getIsInstall() {
        return mSharedPreferences.getBoolean("isInstall", true);
    }

    /**
     * 保存登录请求的key
     *
     * @param loginKey
     */
    public void setLoginKey(String loginKey) {
        mSharedPreferences.edit().putString("setLoginKey", loginKey).commit();
    }

    public String getLoginKey() {
        return mSharedPreferences.getString("setLoginKey", "0");
    }

    public String getUserUA(){
        return mSharedPreferences.getString("setUserUA","");
    }

    public void setUserUA(String ua){
        mSharedPreferences.edit().putString("setUserUA",ua).commit();
    }

    /**
     * 保存登录成功的时间
     *
     * @param loginTime
     */
    public void setLoginTime(long loginTime) {
        mSharedPreferences.edit().putLong("setLoginTime", loginTime).commit();
    }

    public long getLoginTime() {
        return mSharedPreferences.getLong("setLoginTime", (long) 0.0);
    }



    /**
     * 保存登录账号
     *
     * @param loginName
     */
    public void setLoginName(String loginName) {
        mSharedPreferences.edit().putString("setLoginName", loginName).commit();
    }

    public String getLoginName() {
        return mSharedPreferences.getString("setLoginName", "");
    }


 /**
     * 保存登录密码
     *
     * @param loginPwd
     */
    public void setLoginPwd(String loginPwd) {
        mSharedPreferences.edit().putString("setLoginPwd", loginPwd).commit();
    }

    public String getLoginPwd() {
        return mSharedPreferences.getString("setLoginPwd", "");
    }



    /**
     * 保存用户协议
     *
     * @param content
     */
    public void setUserAgree(String content) {
        mSharedPreferences.edit().putString("userAgree", content).commit();
    }

    public String getUserAgree() {
        return mSharedPreferences.getString("userAgree", "");
    }


    /**
     * 保存用户协议
     *
     * @param content
     */
    public void setPrivacyPolicy(String content) {
        mSharedPreferences.edit().putString("privacyPolicy", content).commit();
    }

    public String getPrivacyPolicy() {
        return mSharedPreferences.getString("privacyPolicy", "");
    }



    /**
     * 保存任务类型图片路径
     *
     * @param channelStr
     *//*
    public void setTaskImgUrl(String channelStr) {
        mSharedPreferences.edit().putString("setTaskImgUrl", channelStr).commit();
    }

    public String getTaskImgUrl() {
        return mSharedPreferences.getString("setTaskImgUrl", "");
    }




    *//**
     * 保存渠道标识
     *
     * @param channelStr
     *//*
    public void setChannel(String channelStr) {
        mSharedPreferences.edit().putString("setChannel", channelStr).commit();
    }

    public String getChannel() {
        return mSharedPreferences.getString("setChannel", "");
    }


    *//**
     * 保存友盟回调设备号
     *
     * @param deviceId
     *//*
    public void setDeviceId(String deviceId) {
        mSharedPreferences.edit().putString("setDeviceId", deviceId).commit();
    }

    public String getDeviceId() {
        return mSharedPreferences.getString("setDeviceId", "");
    }


    *//**
     * 保存用户个人信息
     *//*
    public void setLoginInfo(String LoginInfo) {
        mSharedPreferences.edit().putString("setLoginInfo", LoginInfo).commit();
    }

    public UseInfo.DataBean getLoginInfo() {
        UseInfo.DataBean userInfoBean = null;
        String infoStr = mSharedPreferences.getString("setLoginInfo", "");
        if (infoStr.length() > 2) {
            UseInfo useInfo = JSONObject.parseObject(infoStr, UseInfo.class);
            userInfoBean = useInfo.getData();
        }
        return userInfoBean;
    }



    *//**
     * 保存版本升级
     *//*
    public void setVersion(String LoginInfo) {
        mSharedPreferences.edit().putString("setVersion", LoginInfo).commit();
    }

    public VersionJson.DataBean getVersion() {
        VersionJson.DataBean version = null;
        String infoStr = mSharedPreferences.getString("setVersion", "");
        if (infoStr.length() > 2) {
            VersionJson userInfoBean = JSONObject.parseObject(infoStr, VersionJson.class);
            version = userInfoBean.getData();
        }
        return version;
    }*/


}
