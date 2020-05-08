package com.manyu.videoshare.util;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.manyu.videoshare.base.BaseApplication;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.ui.NoIntentActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtils {

    public static void httpString(final String url, final Map<String, String> params, final HttpCallback httpCallback) {
        if(!ToolUtils.havingIntents()){
            return;
        }
        Globals.log("url",url);
        Log.e("url",url);
        PhoneInfo phoneInfo = new PhoneInfo(BaseApplication.getContext());
        Map<String, String> data = phoneInfo.appPhoneInfo();
        if(params != null) {
            data.put("params", new Gson().toJson(params));
        }
        data = phoneInfo.addSign(data);
        Globals.log(new Gson().toJson(data));
        OkHttpUtils.post().url(url).params(data).build().execute(new BaseJsonCallBack() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //if(!Constants.PATH.equals(Constants.BACKPATH)){

                    /*String ul = Constants.BACKPATH + url.substring(Constants.PATH.length(),url.length());
                    Constants.PATH = Constants.BACKPATH;
                    httpString(ul,params,httpCallback);*/
               // }
                Log.e("Logger", "onError === " + e.getMessage());
                httpCallback.httpError(call, e);
            }

            @Override
            public void onResponse(String result, int id) {
//                String mResult = new String(Base64.decode(result, Base64.DEFAULT));
                Log.e("Logger", "result === " + result);
//                Log.e("Logger", "mResult === " + mResult);
                try {
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    if(jsonObject.getIntValue("code") == 998){//result.contains("\\u767b\\u5f55\\u5df2\\u8fc7\\u671f")){
                        BaseSharePerence.getInstance().setLoginKey("0");
                        //ToastUtils.showShort(jsonObject.getIntValue("msg"));
                    }
                    httpCallback.httpResponse(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 上传图片
     * @param url
     * @param imagePath 图片路径
     * @return 新图片的路径
     */
    public static void uploadImage(String url, String imagePath,final HttpCallback httpCallback) {
        PhoneInfo phoneInfo = new PhoneInfo(BaseApplication.getContext());
        Map<String, String> data = phoneInfo.appPhoneInfo();
        data = phoneInfo.addSign(data);
        Globals.log(new Gson().toJson(data));
        OkHttpClient okHttpClient = new OkHttpClient();
        Log.d("imagePath", imagePath);
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        File file = new File(imagePath);
        if (data != null){
            for (String key : data.keySet()) {
                multipartBodyBuilder.addFormDataPart(key, data.get(key));
            }
        }
        RequestBody image = RequestBody.create(MediaType.parse("image/png"), file);
        multipartBodyBuilder.addFormDataPart("avatar", file.getName(),image);

        RequestBody requestBody = multipartBodyBuilder.build();
                /*.setType(MultipartBody.FORM)
                .addFormDataPart("file", imagePath, image)
                .build();*/
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpCallback.httpError(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Globals.log(response.request().body().toString());
                    httpCallback.httpResponse(response.body().string());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void okHttpDownLoadApk(String url, final Handler handler) {
        final String path = Environment.getExternalStorageDirectory() + "/manyu/";
        OkHttpUtils.get().url(url).build()
                .execute(new FileCallBack(path, ToolUtils.timeFormate(System.currentTimeMillis()) + ".mp4") {
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
                        final String text = "正在下载中......" + pro + "%";
                        Log.e("pro",text);
                        Message msg = new Message();
                        msg.what = 0;
                        Bundle b = new Bundle();
                        b.putString("msg",text);
                        msg.setData(b);
                        handler.sendMessage(msg);
                        if(pro>=100) {
                            Message msg2 = new Message();
                            msg2.what = 1;
                            Bundle b2 = new Bundle();
                            b2.putString("path","下载成功！文件保存在" + path);
                            msg2.setData(b2);
                            handler.sendMessage(msg2);


                        }
                    }
                });
    }
    public abstract static class HttpCallback {
        public abstract void httpError(Call call, Exception e);

        public abstract void httpResponse(String resultData);
    }
}
