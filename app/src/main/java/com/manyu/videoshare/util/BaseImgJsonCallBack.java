package com.manyu.videoshare.util;


import com.zhy.http.okhttp.callback.Callback;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Leo on 2018/6/26.
 */

public class BaseImgJsonCallBack extends Callback<String> implements okhttp3.Callback {

    @Override
    public String parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        if (response.headers().toString().contains("zipState")) {
            if (response.headers().get("zipState").equals("1")) {
                string = GzipUtil.unCompress(string);
//                    Globals.log("log xwj parseNetworkResponse 2" + response.headers().get("zipState"));
                return string;
            }
        }
        return string;
    }

    @Override
    public void onError(Call call, Exception e, int id) {

    }

    @Override
    public void onResponse(String response, int id) {

    }


    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }
}
