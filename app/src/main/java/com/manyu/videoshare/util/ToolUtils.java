package com.manyu.videoshare.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.jaeger.library.StatusBarUtil;
import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseApplication;
import com.manyu.videoshare.ui.NoIntentActivity;
import com.manyu.videoshare.ui.user.UserMessageActivity;
import com.meituan.android.walle.WalleChannelReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ToolUtils {
    /**
     * 判断是否是正确的手机号
     * @param str
     * @return
     * @throws PatternSyntaxException
     * 176，177，178,
     * 180，181，182,183,184,185，186，187,188。，189。
     * 145，147,149
     * 130，131，132，133，134,135,136,137, 138,139
     * 150,151, 152,153，155，156，157,158,159,
     * 19
     */
    public static boolean isChinaPhoneLegal(String str)
            throws PatternSyntaxException {
        String regExp = "^((13[0-9])|(15[^4])|(18[0-9])|(17[0-8])|(14[5,7,9])|(19[0-9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
        //return str.matches(regExp);
    }
    public static String getChannel(){
        String channel="xiaomi";
        try{
            channel = WalleChannelReader.getChannel(BaseApplication.getContext());
            if(channel==null){
                channel="default";
            }
        }catch (Exception e){
            channel="default";
        }
        return channel;
    }
    /**
     * unicode转中文
     * @param unicodeStr
     * @return
     */
    public static String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }

    /**
     * 时间格式化
     * @param time
     * @return
     */
    public static String timeFormate(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(new Date(time));
    }

    /**
     * 将dp转换成px
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        final float scale = BaseApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取当前屏幕宽度
     * @return
     */
    public static int getScreenWidth() {
        WindowManager manager = (WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
    /**
     * 获取当前屏幕宽度
     * @return
     */
    public static int getScreenHeigh() {
        WindowManager manager = (WindowManager) BaseApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }
    /**
     * 获取粘贴板上的内容
     * @return
     */
    public static String getClipData(){
        ClipboardManager cm = (ClipboardManager) BaseApplication.getContext().getSystemService(CLIPBOARD_SERVICE);
        ClipData data = cm.getPrimaryClip();
        if(data != null && data.getItemCount() > 0) {
            ClipData.Item item = data.getItemAt(0);
            if(null != item)
                return item.getText().toString();
        }
        return null;
    }
    public static String cleanClipData(){
        ClipboardManager cm = (ClipboardManager) BaseApplication.getContext().getSystemService(CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", "");
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        return null;
    }
    public static String setClipData(String text){
        ClipboardManager cm = (ClipboardManager) BaseApplication.getContext().getSystemService(CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", text);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        return null;
    }
    /**
     * 获取版本号
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 设置图片宽度，扣除间距后的宽度
     * @param imageView
     * @param dp
     */
    public  static void setImageMatchScreenWidth(ImageView imageView,int dp){
        BitmapDrawable  bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        if(bitmapDrawable == null)
            return;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        if(bitmap == null)
            return;
        int defaultWidth = getScreenWidth() - dip2px(dp);
        float scale = (float) defaultWidth / (float) bitmap.getWidth();
        int defaultHeight = Math.round(bitmap.getHeight() * scale);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = defaultWidth;
        params.height = defaultHeight;
        imageView.setLayoutParams(params);
    }

    /**
     * 设置重新获取验证码
     * @param times
     * @param tv
     * @param handler
     */
    public static void setVerifyText(final int times,final TextView tv,final Handler handler){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tv.setClickable(false);
                tv.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.hint_color));
                tv.setText("重新获取(" +times + "秒)");
                if((times - 1) > 0){
                    setVerifyText(times-1,tv,handler);
                }else{
                    tv.setClickable(true);
                    tv.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.login_blue));
                    tv.setText("获取验证码");
                }
            }
        },1000);
    }
    public static Uri geturi(Context context,android.content.Intent intent) {
        Uri uri = intent.getData();
        String type = intent.getType();
        if (uri.getScheme().equals("file") && (type.contains("image/"))) {
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = context.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=")
                        .append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[] { MediaStore.Images.ImageColumns._ID },
                        buff.toString(), null, null);
                int index = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    // set _id value
                    index = cur.getInt(index);
                }
                if (index == 0) {
                    // do nothing
                } else {
                    Uri uri_temp = Uri
                            .parse("content://media/external/images/media/"
                                    + index);
                    if (uri_temp != null) {
                        uri = uri_temp;
                    }
                }
            }
        }
        return uri;
    }
    public static String getRealFilePath(Context context, Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * 设置activity全屏显示并将状态栏浸染成当前背景色
     * @param activity
     */
    public static void setBar(Activity activity){
        StatusBarUtil.setTranslucentForImageViewInFragment(activity,0,null);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        View decor = activity.getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    public static void copyFile(String oldPath, String newPath, Handler handler) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) {//文件存在时
                InputStream inStream = new FileInputStream(oldPath);//读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                int value = 0 ;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;//字节数 文件大小
                    value ++ ;  //计数
                    fs.write(buffer, 0, byteread);
                    Message msg  = new Message(); //创建一个msg对象
                    msg.what =110 ;
                    msg.arg1 = value ; //当前的value
                    handler.sendMessage(msg) ;

                    Thread.sleep(10);//每隔10ms发送一消息，也就是说每隔10ms value就自增一次，将这个value发送给主线程处理
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
    public static String formatTime(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    /**
     * 这是获取字符串长度，包含中文的双字符计算
     * @param text
     * @return
     */
    public static int getTextLengh(String text){
        int size = 0;
        for(int i = 0 ; i < text.length(); i++){
            char item = text.charAt(i);
            if(item  < 128){
                size++;
            }else{
                size+=2;
            }
        }

        return size;
    }
    public static void installApk(String url,Activity activity) {

        //Environment.getExternalStorageDirectory() 保存的路径
        Intent intent;

        if (Build.VERSION.SDK_INT >= 24) {
//            Globals.log("log xwj installApk1");
            File fileLocation = new File(url);

            Globals.log("log xwj Build.VERSION.SDK_INT" + Environment.getExternalStorageDirectory()  +"      "+ BaseApplication.getContext().getPackageName());
            Uri apkUri = FileProviderUtils.uriFromFile(activity, fileLocation);//FileProvider.getUriForFile(BaseApplication.getContext(), BaseApplication.getContext().getPackageName() +".FileProvider", fileLocation);//在AndroidManifest中的android:authorities值
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
//            Globals.log("log xwj installApk2");
            File fileLocation = new File(url);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.parse(Uri.fromFile(fileLocation) + "");
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        BaseApplication.getContext().startActivity(intent);
    }
    public static String dataFormate(long time){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");/* HH:mm:ss*/
        return formatter.format(new Date(time * 1000));
    }
    public static boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
    public static void sharedQQ(Activity context){
        File file = new File(Environment.getExternalStorageDirectory() + "/manyu/head.png");
        Uri uri = FileProviderUtils.uriFromFile(context, file); /*Uri.parse(MediaStore.Images.Media.insertImage(
                context.getContentResolver(), BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher), null, null));*/
        Intent imageIntent = new Intent(Intent.ACTION_SEND);
        imageIntent.setPackage("com.tencent.mobileqq");
        imageIntent.setType("image/*");
        imageIntent.putExtra(Intent.EXTRA_STREAM, uri);
        imageIntent.putExtra(Intent.EXTRA_TEXT,"您的好友邀请您使用去水印");
        imageIntent.putExtra(Intent.EXTRA_TITLE,"去水印");
        context.startActivity(imageIntent);
    }

        /**
         * 创建二维码
         *
         * @param content   content
         * @param widthPix  widthPix
         * @param heightPix heightPix
         * @param logoBm    logoBm
         * @return 二维码
         */
        public static Bitmap createQRCode(String content, int widthPix, int heightPix, Bitmap logoBm) {
            try {
                if (content == null || "".equals(content)) {
                    return null;
                }
                // 配置参数
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                // 容错级别
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                hints.put(EncodeHintType.MARGIN,0);
                // 图像数据转换，使用了矩阵转换
                BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, widthPix,
                        heightPix, hints);
                int[] pixels = new int[widthPix * heightPix];
                // 下面这里按照二维码的算法，逐个生成二维码的图片，
                // 两个for循环是图片横列扫描的结果
                int wids = 0;
                for (int y = wids; y < heightPix - wids; y++) {
                    for (int x = wids; x < widthPix - wids; x++) {
                        if (bitMatrix.get(x, y)) {
                            pixels[y * widthPix + x] = 0xff000000;
                        } else {
                            pixels[y * widthPix + x] = 0xffffffff;
                        }
                    }
                }
                // 生成二维码图片的格式，使用ARGB_8888
                Bitmap bitmap = Bitmap.createBitmap(widthPix, heightPix, Bitmap.Config.ARGB_8888);
                bitmap.setPixels(pixels, 0, widthPix, 0, 0, widthPix, heightPix);
                if (logoBm != null) {
                    bitmap = addLogo(bitmap, logoBm);
                }
                //必须使用compress方法将bitmap保存到文件中再进行读取。直接返回的bitmap是没有任何压缩的，内存消耗巨大！
                return bitmap;
            } catch (WriterException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 在二维码中间添加Logo图案
         */
        private static Bitmap addLogo(Bitmap src, Bitmap logo) {
            if (src == null) {
                return null;
            }
            if (logo == null) {
                return src;
            }
            //获取图片的宽高
            int srcWidth = src.getWidth();
            int srcHeight = src.getHeight();
            int logoWidth = logo.getWidth();
            int logoHeight = logo.getHeight();
            if (srcWidth == 0 || srcHeight == 0) {
                return null;
            }
            if (logoWidth == 0 || logoHeight == 0) {
                return src;
            }
            //logo大小为二维码整体大小的1/5
            float scaleFactor = srcWidth * 1.0f / 5 / logoWidth;
            Bitmap bitmap = Bitmap.createBitmap(srcWidth, srcHeight, Bitmap.Config.ARGB_8888);
            try {
                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(src, 0, 0, null);
                canvas.scale(scaleFactor, scaleFactor, srcWidth / 2, srcHeight / 2);
                canvas.drawBitmap(logo, (srcWidth - logoWidth) / 2, (srcHeight - logoHeight) / 2, null);
                canvas.save();
                canvas.restore();
            } catch (Exception e) {
                bitmap = null;
                e.getStackTrace();
            }
            return bitmap;
        }
    public static boolean saveBitmap(Bitmap bm) {
        String path = Environment.getExternalStorageDirectory()
                + File.separator + Environment.DIRECTORY_DCIM
                +File.separator+"Camera"+File.separator;
        String md5 = "去水印_share" + ".png";
        String name = path + md5;
        File dirs = new File((path));
        if(!dirs.exists()){
            dirs.mkdirs();
        }
        File file = new File(name);
        if (file.exists()) {
            file.delete();
        }
        File myCaptureFile = new File(path, md5);
        try {
            myCaptureFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    public static Bitmap imageScale(Bitmap bitmap, int dst_w, int dst_h) {
        int src_w = bitmap.getWidth();
        int src_h = bitmap.getHeight();
        float scale_w = ((float) dst_w) / src_w;
        float scale_h = ((float) dst_h) / src_h;
        Matrix matrix = new Matrix();
        matrix.postScale(scale_w, scale_h);
        Bitmap dstbmp = Bitmap.createBitmap(bitmap, 0, 0, src_w, src_h, matrix,
                true);
        return dstbmp;
    }
    public static void havingIntent(Context context){
//        try {
//            String com = "/system/bin/ping -c 1 -w 100 www.baidu.com" ;
//            Process process = Runtime.getRuntime().exec(com);
//            int status = process.waitFor();
//            Globals.log("intentStatus",status+"");
//            if(status == 0){
//                return;
//            }
//            IntentUtils.JumpActivity(context,NoIntentActivity.class);
//            return;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        IntentUtils.JumpActivity(context,NoIntentActivity.class);
    }
    public static boolean havingIntents(){
//        try {
//            String com = "/system/bin/ping -c 1 -w 100 www.baidu.com" ;
//            Process process = Runtime.getRuntime().exec(com);
//            int status = process.waitFor();
//            Globals.log("intentStatus",status+"");
//            if(status == 0){
//                return true;
//            }
//            return false;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
         return true;
    }

    /**
     * 按比例将当前界面缩放至合适大小
     * @param videowid
     * @param videohei
     * @return
     */
    public static int[] scaleToScreen(int videowid,int videohei){
        Globals.log("oldVideoScreen",videowid + "___"  + videohei);
            int[] denisty = new int[]{videowid,videohei};
            int scrhei = ToolUtils.getScreenHeigh();
            int scrwid = ToolUtils.getScreenWidth();
            //if(videohei < scrhei){
            videowid = scrhei * videowid / videohei;
            videohei = scrhei;
            if(videowid < scrwid){
                videohei = scrwid *scrhei / videowid;
                videowid = scrwid;
            }
            /*}else{

            }

            if(videohei * 10 / videowid > scrhei * 10 / scrwid){
                videowid = scrhei * videowid / videohei;
                videohei = scrhei;
                if(videowid < scrwid){
                    videohei = scrhei * videohei / videowid;
                    videowid = scrwid;
                }
            }*/
        Globals.log("oldVideoScreen",videowid + "___"  + videohei);
            denisty[0] = videowid;
            denisty[1] = videohei;
            return denisty;
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
