package com.manyu.videoshare.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by Maisi on 2015/1/26.
 */
public class Globals {



    /**
     * default priority：Log.DEBUG ；
     * default TAG:OMS-E
     *
     * @param msg
     */
    public static void log(String msg) {

        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        if (Constants.LOG_Controll) {
            return;
        }

        if (msg.length() > 4000) {
            for (int i = 0; i < msg.length(); i+= 4000) {
                if (i +4000 < msg.length()) {
                    Log.e(targetStackTraceElement.getClassName()+targetStackTraceElement.getLineNumber()+"  01  ", msg.substring(i,i+4000));
                } else {

                    Log.e(targetStackTraceElement.getClassName()+targetStackTraceElement.getLineNumber()+"  02  ", msg.substring(i, msg.length()));
                }
            }
        } else {
            Log.e(targetStackTraceElement.getClassName()+targetStackTraceElement.getLineNumber(), msg + "");
        }

//        Log.e(targetStackTraceElement.getClassName()+targetStackTraceElement.getLineNumber(), msg + "");



    }

    /**
     * @param priority eg：log(Log.DEBUG, msg); == Log.d(TAG, msg);
     * @param msg
     */
    public static void log(int priority, String msg) {
        if (Constants.LOG_Controll) {
            return;
        }
        //Log.println(priority, Constants.LOG_TAG, msg + "");
    }

    public static void log(String tag, String msg) {
        StackTraceElement targetStackTraceElement = getTargetStackTraceElement();
        if (Constants.LOG_Controll) {
            return;
        }
        Log.e(targetStackTraceElement.getClassName()+targetStackTraceElement.getLineNumber()+tag, msg + "");
    }

    public static String makeLogTag(Class<?> cls) {
        return cls.getName();
    }

    /**
     * 保存Bitmap到指定目录
     *
     * @param dir      目录
     * @param fileName 文件名
     * @param bitmap
     * @throws IOException
     */
    public static void savaBitmap(File dir, String fileName, Bitmap bitmap) {
//        FileUtils.savaBitmap(dir, fileName, bitmap);
    }

    /**
     * 判断数组中的最大数
     *
     * @param args
     * @return
     */
    public static int getMaxNumber(int args[]) {
        int max = 0;
        for (int
             i = 0; i < args.length; i++) {
            if (args[i] > args[max]) {
                max = i;
            }
        }
        return args[max];
    }

    /**
     * 判断数组中的最小数
     *
     * @param args
     * @return
     */
    public static int getMinNumber(int args[]) {
        int min = 0;
        for (int
             i = 0; i < args.length; i++) {
            if (args[i] < args[min]) {
                min = i;
            }
        }
        return args[min];
    }

    /**
     * 去处url中特殊字符作为文件的名称
     *
     * @param url
     * @return
     */
    public static String parseFileName(String url) {
        // 去处url中特殊字符作为文件的名称
        String urlKey = "";
        String temp[] = url.split("/");
        if (temp.length > 1) {
            urlKey = temp[temp.length - 1];
        }
        return urlKey;
    }

    public static long getApkUpdateTime(Context context) {
        PackageManager pm = context.getPackageManager();
        ZipFile zf = null;
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            zf = new ZipFile(packageInfo.applicationInfo.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            return ze.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            if (zf != null) {
                try {
                    zf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    /**
     * 移除url尾部的/
     * @param url
     * @return
     */
    public static String removeUrlEndSlash(String url) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        if (url.endsWith("/")) {
            try {
                int last = url.lastIndexOf("/");
                return url.substring(0, last);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return url;
    }

//    public static long getApkUpdateTime2(Context context) {
//        ZipFile zf = null;
//        try {
//            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(
//                    context.getPackageName(), 0);
//            zf = new ZipFile(ai.sourceDir);
//            ZipEntry ze = zf.getEntry("META-INF/MANIFEST.MF");
//            return ze.getTime();
//        } catch (Exception e) {
//        } finally {
//            if (zf != null) {
//                try {
//                    zf.close();
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
//        return 0;
//    }

//    public static void restartApp(Context baseContext) {
//        Intent i = baseContext.getPackageManager()
//                .getLaunchIntentForPackage(baseContext.getPackageName());
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        baseContext.startActivity(i);
//    }
//
//    /**
//     * 获取指定包名程序的签名信息
//     *
//     * @param context
//     * @param packName
//     * @author SHANHY
//     */
//    public static void getSingInfo(Context context, String packName) {
//        try {
//            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packName, PackageManager.GET_SIGNATURES);
//            Signature[] signs = packageInfo.signatures;
//            Signature sign = signs[0];
//            parseSignature(sign.toByteArray());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void parseSignature(byte[] signature) {
//        try {
//            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
//            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
//            String pubKey = cert.getPublicKey().toString();
//            String signNumber = cert.getSerialNumber().toString();
//            Log.e("parseSignature", "pubKey = " + pubKey);// 输出的是16进制的公钥
//            Log.e("parseSignature", "signNumber = " + signNumber);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }




    //获取当前线程

    private static StackTraceElement getTargetStackTraceElement() {
        // find the target invoked method
        StackTraceElement targetStackTrace = null;
        boolean shouldTrace = false;
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            boolean isLogMethod = stackTraceElement.getClassName().equals(Globals.class.getName());
            if (shouldTrace && !isLogMethod) {
                targetStackTrace = stackTraceElement;
                break;
            }
            shouldTrace = isLogMethod;
        }
        return targetStackTrace;
    }
}
