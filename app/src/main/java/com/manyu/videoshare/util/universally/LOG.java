package com.manyu.videoshare.util.universally;

import android.util.Log;

/**
 * Author：xushiyong
 * Date：2020/4/28
 * Descript：
 */
public class LOG {

    public static final String TAG = "xushiyong";

    public static void showE(String text){
        Log.e(TAG,text);
    }

    public static void showI(String text){
        Log.i(TAG,text);
    }
}
