package com.manyu.videoshare.util;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.manyu.videoshare.R;

import java.io.File;
import java.math.BigDecimal;
import java.util.UUID;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * 在Activity里context上下文要使用getApplicationContext()
 * <p>
 * <p>
 * <p>
 * 2.设置bitmap或者gif
 * .asBitmap();
 * .asGif();
 * <p>
 * <p>
 * 3.设置图片大小
 * .override(int w, int h);
 * <p>
 * <p>
 * 4.加载缩略图
 * <p>
 * thumbnail(0.1f);
 * 1
 * 它是在你into的view中先加载设置的缩略图，然后才会加载大图，注：参数范围为0~1。
 * <p>
 * 5.设置占位图或者加载错误图：
 * <p>
 * .placeholder(R.drawable.placeholder)
 * .error(R.drawable.imagenotfound)
 * <p>
 * <p>
 * 6.加载完成动画
 * <p>
 * .animate(Animator animator);//或者int animationId
 * <p>
 * <p>
 * 7.图片适配scaleType
 * <p>
 * .centerCrop(); // 长的一边撑满
 * .fitCenter(); // 短的一边撑满
 * <p>
 * <p>
 * 8.暂停\回复请求
 * <p>
 * Glide.with(context).resumeRequests();
 * Glide.with(context).pauseRequests();
 * <p>
 * <p>
 * <p>
 * 9.在后台线程当中进行加载和缓存
 * <p>
 * downloadOnly(int width, int height)
 * downloadOnly(Y target)// Y extends Target<File>
 * into(int width, int height)
 * <p>
 * <p>
 * skCacheStrategy(DiskCacheStrategy.ALL) //这个是设置缓存策略。
 * DiskCacheStrategy.NONE：不缓存
 * DiskCacheStrategy.SOURCE：缓存原始图片
 * DiskCacheStrategy.RESULT：缓存压缩过的结果图片
 * DiskCacheStrategy.ALL：两个都缓存
 * <p>
 * <p>
 * Crop
 * 默认：CropTransformation,
 * 圆形：CropCircleTransformation,
 * 方形：CropSquareTransformation,
 * 圆角：RoundedCornersTransformation
 * <p>
 * Color
 * 颜色覆盖：ColorFilterTransformation,
 * 置灰：GrayscaleTransformation
 * <p>
 * Blur
 * 毛玻璃：BlurTransformation
 * <p>
 * 11 Glide自带的一个渐变动画
 * <p>
 * Glide.with(this).load(url).crossFade([duration]).into(iv2);
 * <p>
 * 12加载Gif
 * <p>
 * //普通显示GIF
 * <p>
 * Glide.with( context ).load( gifUrl ).into( iv );
 * <p>
 * //添加GIF检查，如果不是GIF就会显示加载失败位图
 * <p>
 * Glide.with( context ).load( gifUrl ).asGif().into( iv);
 * <p>
 * 13显示本地视频
 * <p>
 * String filePath ="/storage/emulated/0/Pictures/example_video.mp4";
 * Glide
 * .with(context )
 * .load(Uri.fromFile( new File( filePath ) ) )
 * .into( iv );
 * <p>
 * Glid只能加载本地视频，不能从网络中获取
 * <p>
 * File file = new File(Environment.getExternalStorageDirectory() + File.separator +  "image", "image.jpg");
 * Glide.with(this).load(file).into(imageView);
 * <p>
 * <p>
 * 在 lowMemory 的时候，调用 Glide.cleanMemroy() 清理掉所有的内存缓存。
 * 在 App 被置换到后台的时候，调用 Glide.cleanMemroy() 清理掉所有的内存缓存。
 * 在其它情况的 onTrimMemroy() 回调中，直接调用 Glide.trimMemory() 方法来交给 Glide 处理内存情况。
 */
public class GlideUtils {
    private static String reSize = "?x-oss-process=image/resize,h_250,w_250";
    private static Drawable drawable;

    /*public static Drawable getNetImage(Context context, String url) {
        SimpleTarget<Drawable> simpleTarget = new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, GlideAnimation<? super Drawable> glideAnimation) {
                drawable = resource;
            }
        };
        return drawable;
    }*/


    //加载图片
    public static void loadImg(Context context, String url, ImageView myImageView) {

        final ObjectAnimator anim = ObjectAnimator.ofInt(myImageView, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        Glide.with(context.getApplicationContext())
                .load(url)
                .error(R.drawable.logo)
                .placeholder(R.drawable.image_loading)
                .listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        anim.cancel();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        anim.cancel();
                        return false;
                    }
                })
                .into(myImageView);
    }

    public static void loadImgWithout(Context context, String url, ImageView myImageView) {

        final ObjectAnimator anim = ObjectAnimator.ofInt(myImageView, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        Glide.with(context.getApplicationContext())
                .load(url)
                .error(R.drawable.logo)
                //.placeholder(R.drawable.image_loading)
                .listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        anim.cancel();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        anim.cancel();
                        return false;
                    }
                })
                .into(myImageView);
    }
    public static void loadImgHeand(Context context, String url, ImageView myImageView) {

        final ObjectAnimator anim = ObjectAnimator.ofInt(myImageView, "ImageLevel", 0, 10000);
        anim.setDuration(800);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.start();

        Glide.with(context.getApplicationContext())
                .load(url)
                .error(R.drawable.logo)
                .placeholder(R.drawable.user_head_default)
                .listener(new RequestListener() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target, boolean isFirstResource) {
                        anim.cancel();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, DataSource dataSource, boolean isFirstResource) {
                        anim.cancel();
                        return false;
                    }
                })
                .into(myImageView);
    }
    //加载图片-指定图片尺寸
    public static void loadImg(Context context, String url, ImageView myImageView, int with, int height) {
        Glide.with(context.getApplicationContext())
                .load(url)
                .thumbnail(0.1f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.qq_a)
                .override(with, height)
                //.bitmapTransform(new CropCircleTransformation(context))
                .into(myImageView);
    }


    //高斯
    /*public static void loadImg(Context context, String url, ImageView myImageView, int radius) {
        //radius取值1-25,值越大图片越模糊
        Glide.with(context).load(url).bitmapTransform(new BlurTransformation(context, radius)).into(myImageView);
    }

    //高斯--圆形
    public static void loadCircleImg(Context context, String url, ImageView myImageView, int radius) {
        Glide.with(context).load(url).bitmapTransform(new BlurTransformation(context, radius), new CropCircleTransformation(context)).into(myImageView);
    }*/


    /**
     * 圆形加载带边框
     *
     * @param mContext
     * @param path
     * @param imageview
     */
    public static void loadCircleImage(Context mContext, String path, ImageView imageview, int with, int borderColor) {
        Glide.with(mContext.getApplicationContext())
                .load(path)
                .centerCrop()
                //.signature(new StringSignature(UUID.randomUUID().toString())) // 重点在这行
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.qq_a)
                .thumbnail(0.1f)
                //.bitmapTransform(new CropCircleTransformation(mContext))
                .transform(new GlideCircleTransform(mContext, with, borderColor))
                .into(imageview);

    }


    /**
     * 不带白色边框的圆形图片加载
     *
     * @param mContext
     * @param path
     * @param imageview
     */
    public static void loadCircleImage(Context mContext, String path, ImageView imageview) {
        Glide.with(mContext.getApplicationContext())
                .load(path)
                .centerCrop()
                //.signature(new StringSignature(UUID.randomUUID().toString())) // 重点在这行
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //.placeholder(R.drawable.qq_a)
                .thumbnail(0.1f)
                .transform(new GlideCircleTransform(mContext))
                .into(imageview);

    }


    /**
     * 圆角
     *
     * @param context
     * @param url
     * @param myImageView
     * @param radius
     * @param margin
     */
    public static void loadRoundedImage(Context context, String url, ImageView myImageView, int radius, int margin) {
        Glide.with(context)
                .load(url)
                //.bitmapTransform(new RoundedCornersTransformation(context, radius, margin))
                .error(R.drawable.qq_a)
                .thumbnail(0.1f)
                .into(myImageView);
    }


    /**
     * 清除图片所有缓存
     */
    public void clearImageAllCache(Context context) {
        clearImageDiskCache(context);
        clearImageMemoryCache(context);
        String ImageExternalCatchDir = context.getExternalCacheDir() + ExternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR;
        deleteFolderFile(ImageExternalCatchDir, true);
    }


    /**
     * 清除图片磁盘缓存
     */
    public void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context).clearDiskCache();
                        //BusUtil.getBus().post(new GlideCacheClearSuccessEvent());
                    }
                }).start();
            } else {
                Glide.get(context).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除图片内存缓存
     */
    public void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取Glide造成的缓存大小
     *
     * @return CacheSize
     */
    public String getCacheSize(Context context) {
        try {
            return getFormatSize(getFolderSize(new File(context.getCacheDir() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取指定文件夹内所有文件大小的和
     *
     * @param file file
     * @return size
     * @throws Exception
     */
    private long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (File aFileList : fileList) {
                if (aFileList.isDirectory()) {
                    size = size + getFolderSize(aFileList);
                } else {
                    size = size + aFileList.length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 删除指定目录下的文件，这里用于缓存的删除
     *
     * @param filePath       filePath
     * @param deleteThisPath deleteThisPath
     */
    private void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    File files[] = file.listFiles();
                    for (File file1 : files) {
                        deleteFolderFile(file1.getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {
                        file.delete();
                    } else {
                        if (file.listFiles().length == 0) {
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size size
     * @return size
     */
    private static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size + "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }


}