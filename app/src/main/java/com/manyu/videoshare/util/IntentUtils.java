package com.manyu.videoshare.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;


/**
 * Created by Leo on 2018/6/6.
 */

public class IntentUtils {

    public static void JumpActivity(Context a, Class b, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(a, b);
        a.startActivity(intent);
    }
//
    public static void JumpActivity(Activity a, Class b, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(a, b);
        a.startActivity(intent);
    }

    public static void JumpActivity(Activity a, Class b) {
        Intent intent = new Intent();
        intent.setClass(a, b);
        a.startActivity(intent);
    }


    public static void JumpActivity(Context a, Class b) {
        Intent intent = new Intent();
        intent.setClass(a, b);
        a.startActivity(intent);
    }


    public static void JumpActivity(Activity a, Class b, Bundle bundle, int result) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(a, b);
        a.startActivityForResult(intent, result);
    }
    public static void JumpActivity(Activity a, Class b, int result) {
        Intent intent = new Intent();
        intent.setClass(a, b);
        a.startActivityForResult(intent, result);
    }
    public static void JumpToTaobao(Context context, String strUri) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        boolean bFindTaobao = false;
        for (int i = 0; i < pinfo.size(); i++) {
            // 循环判断是否存在指定包名
            if (pinfo.get(i).packageName.equalsIgnoreCase("com.taobao.taobao")) {
                bFindTaobao = true;
                //Globals.log("find taobao");
                break;
            }
        }
        if (bFindTaobao == false) return;
        Intent intent = new Intent();
        intent.setAction("Android.intent.action.VIEW");
        Uri uri = Uri.parse(strUri); // 商品地址
        intent.setData(uri);
        intent.setClassName("com.taobao.taobao", "com.taobao.tao.detail.activity.DetailActivity");
        context.startActivity(intent);
    }


    public static void JumpQQ(Context context) {
        try {
//     第一种方式：是可以的跳转到qq主页面，不能跳转到qq聊天界面
            Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.tencent.mobileqq");
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            //ToastUtils.showShort(context, "请检查是否安装QQ");
        }
    }

    public static void JumpQQ(Context context, String qqNum) {
        try {
            //第二种方式：可以跳转到添加好友，如果qq号是好友了，直接聊天
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + qqNum;//uin是发送过去的qq号码
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            //ToastUtils.showShort(context, "请检查是否安装QQ");
        }
    }


    public static void JumpDY(Context context, String activityUrl) {
        String url = "snssdk1128://aweme/detail/" + activityUrl;
//        String url = "snssdk1128://user/profile/95627491133?refer=web&gd_label=click_wap_profile_bottom&type=need_follow&needlaunchlog=1";
//        String webStr = "https://www.iesdouyin.com/share/video/6657758842652331272/?region=CN&mid=6657423986114480910&u_code=f6mf8c3d&titleType=title&timestamp=1553154015&utm_campaign=client_share&app=aweme&utm_medium=ios&tt_from=copy&utm_source=copy&iid=66213395255";
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void JumpBrowser(Context context, String activityUrl) {
        Uri uri = Uri.parse(activityUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }


}
