package com.manyu.videoshare.util;

import android.annotation.SuppressLint;
import android.graphics.RectF;
import android.util.Log;

import java.util.Formatter;
import java.util.List;

import io.microshow.rxffmpeg.RxFFmpegCommandList;

/**
 * ffmpeg工具：拼接命令行处理音视频
 * Created by frank on 2018/1/23.
 */

public class FFmpegUtil {

    /**
     * 使用ffmpeg命令行进行抽取视频-就是获取没有声音的视频
     *
     * @param srcFile    原文件
     * @param targetFile 目标文件
     * @return 抽取后的视频文件
     */
    public static String[] extractVideo(String srcFile, String targetFile) {
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-i");
        cmdList.append(srcFile);
        cmdList.append("-vcodec");
        cmdList.append("copy");
        cmdList.append("-an");
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(targetFile);
        return cmdList.build();
    }


    /**
     * 使用ffmpeg命令行进行视频转码
     *
     * @param srcFile    源文件
     * @param targetFile 目标文件（后缀指定转码格式）
     * @return 转码后的文件
     */
    public static String[] transformVideo(String srcFile, String targetFile) {
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(srcFile);
        cmdList.append("-r");
        cmdList.append("25");
        cmdList.append("-b");
        cmdList.append("200");
        cmdList.append("-s");
        cmdList.append("1080x720");
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(targetFile);
        return cmdList.build();
    }

    /**
     * 使用ffmpeg命令行进行视频剪切
     *
     * @param srcFile    源文件
     * @param startTime  剪切的开始时间(单位为秒)
     * @param duration   剪切时长(单位为秒)
     * @param targetFile 目标文件
     * @return 剪切后的文件
     */
    public static String[] cutVideo(String srcFile, long startTime, long duration, String targetFile) {
//        String cutVideoCmd = "ffmpeg -ss %s -t %s -i %s -vcodec copy -acodec copy %s";
//        String cutVideoCmd = "ffmpeg -ss %s -to %s -accurate_seek -i %s -codec copy -avoid_negative_ts 1 %s";
//        cutVideoCmd = String.format(cutVideoCmd,  stringForTime(startTime), stringForTime(endTime),srcFile, targetFile);
//        return cutVideoCmd.split(" ");//以空格分割为字符串数组
        String cutVideoCmd = "ffmpeg -ss %s -i %s -t %s -c copy %s";
        cutVideoCmd = String.format(cutVideoCmd, stringForTime(startTime), srcFile, stringForTime(duration), targetFile);
        return cutVideoCmd.split(" ");//以空格分割为字符串数组

    }

    /**
     * 使用ffmpeg命令行进行图片合成视频
     *
     * @param srcDir     源文件
     * @param targetFile 目标文件
     * @return 合成的视频文件
     */
    public static String[] image2Video(String srcDir, String format, String targetFile) {
        String command = "ffmpeg -y -loop 1 -framerate 25 -t 10.0 -i %s -ss 5.0 -t 5.04 -accurate_seek -i %s -ss 0.0 -t 5.921 -accurate_seek -i %s -f lavfi -t 10.0 -i anullsrc=channel_layout=stereo:sample_rate=44100 -filter_complex [0:v]scale=260.0:260.0,pad=320:260:30.0:0.0,setdar=320/260[outv0];[1:v]scale=320.0:256.0,pad=320:260:0.0:2.0,setdar=320/260[outv1];[2:v]scale=320.0:180.0,pad=320:260:0.0:40.0,setdar=320/260[outv2];[outv0][outv1][outv2]concat=n=3:v=1:a=0:unsafe=1[outv];[3:a][1:a][2:a]concat=n=3:v=0:a=1[outa] -map [outv] -map [outa] -r 25 -b 1M -f mp4 -t 20.961 -vcodec libx264 -c:a aac -pix_fmt yuv420p -s 320x260 -preset superfast %s";
        command = String.format(command,  format,srcDir, srcDir,targetFile);
        return command.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行视频截图
     *
     * @param srcFile    源文件
     * @param size       图片尺寸大小
     * @param targetFile 目标文件
     * @return 截图后的文件
     */
    public static String[] screenShot(String srcFile, String size, String targetFile) {
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(srcFile);
        cmdList.append("-f");
        cmdList.append("image2");
        cmdList.append("-t");
        cmdList.append("0.001");
        cmdList.append("-s");
        cmdList.append(size);
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(targetFile);
        return cmdList.build();
    }

    /**
     * 使用ffmpeg命令行给视频添加水印
     *
     * @param srcFile    源文件
     * @param waterMark  水印文件路径
     * @param targetFile 目标文件
     * @return 添加水印后的文件
     */
    public static String[] addWaterMark(String srcFile, String waterMark, String overlay, String targetFile) {
        String waterMarkCmd = "ffmpeg -threads 2 -y -i %s -i %s -filter_complex %s -preset ultrafast %s";
        waterMarkCmd = String.format(waterMarkCmd, srcFile, waterMark, overlay, targetFile);
        return waterMarkCmd.split(" ");//以空格分割为字符串数组
    }


    /**
     * 使用ffmpeg命令行进行视频转成Gif动图
     */
    public static String[] generateGif(String srcFile, float startTime, float endTime, int outWidth, int outHeight, String targetFile) {
        //String screenShotCmd = "ffmpeg -threads 2 -y -i %s -ss %f -t %f -s
        // %dx%d -f gif -preset ultrafast %s";
        //

        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(srcFile);
        cmdList.append("-ss");
        cmdList.append(startTime + "");
        cmdList.append("-t");
        cmdList.append(endTime + "");
        cmdList.append("-s");
        cmdList.append(outWidth + "x" + outHeight);
        cmdList.append("-f");
        cmdList.append("gif");
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(targetFile);
        return cmdList.build();
    }

    /**
     * 压缩视频
     */
    public static String[] compressVideo(String inputFile, String targetFile, int crf) {
        String compress = "ffmpeg -i %s -vcodec libx264 -crf %d %s";
        compress = String.format(compress, inputFile, crf, targetFile);
        return compress.split(" ");//以空格分割为字符串数组
    }

    /**
     * 视频反序倒播
     *
     * @param inputFile  输入文件
     * @param targetFile 反序文件
     * @return 视频反序的命令行
     */
    public static String[] upsideVideo(String inputFile, String targetFile) {
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(inputFile);
        cmdList.append("-vf");
        cmdList.append("reverse");
        cmdList.append("-af");
        cmdList.append("areverse");
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(targetFile);
        return cmdList.build();
    }

    /**
     * 视频水平翻转
     *
     * @param inputFile 原视频
     * @param outFile   输出视频
     * @return 命令
     */
    @SuppressLint("DefaultLocale")
    public static String[] videoHorizontalFlip(String inputFile, String outFile) {
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(inputFile);
        cmdList.append("-vf");
        cmdList.append("hflip");
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(outFile);
        return cmdList.build();
    }

    /**
     * 视频垂直翻转
     *
     * @param inputFile 原视频
     * @param outFile   输出视频
     * @return 命令
     */
    public static String[] videoVerticalFlip(String inputFile, String outFile) {
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(inputFile);
        cmdList.append("-vf");
        cmdList.append("vflip");
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(outFile);
        return cmdList.build();
    }

    /**
     * 视频垂直翻转
     *
     * @param inputFile 原视频
     * @param outFile   输出视频
     * @return 命令
     */
    public static String[] videoRotate(String inputFile, String outFile, String rotate) {
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(inputFile);
        cmdList.append("-vf");
        cmdList.append(rotate);
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(outFile);
        return cmdList.build();
    }

    /**
     * 视频变速
     *
     * @param inputFile 原视频
     * @param outFile   输出视频
     * @param speed     视频速度
     * @return 命令
     */
    public static String[] videoSpeed(String inputFile, String outFile, float speed) {
        //视频速度-范围0.25-4 大于1是减速 小于1是加速 所以需要用1来除
        double videoSpeed = 1F / speed;
        //音频速度 范围0-2，如果需要大于2 需要拼接，所以直接开根分成2个
        String voiceCmd;
        if (speed > 2) {
            voiceCmd = "atempo=2.0,atempo=" + (speed - 2.0);
        } else {
            voiceCmd = "atempo=" + speed;
        }
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(inputFile);
        cmdList.append("-filter_complex");
        cmdList.append("[0:v]setpts=" + videoSpeed + "*PTS[v];[0:a]" + voiceCmd + "[a]");
        cmdList.append("-map");
        cmdList.append("[v]");
        cmdList.append("-map");
        cmdList.append("[a]");
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(outFile);
        return cmdList.build();
    }

    /**
     * 去水印
     */
    @SuppressLint("DefaultLocale")
    public static String[] removeWaterMark(String inputFile, String outFile, List<RectF> list) {
        StringBuilder deLogoCmd = new StringBuilder();
        String defaultDeLogoCmd = "delogo=%d:%d:%d:%d,";
        for (RectF rectF : list) {
            deLogoCmd.append(String.format(defaultDeLogoCmd, (int) (rectF.left > rectF.right ? rectF.right : rectF.left) + 1,
                    (int) (rectF.top > rectF.bottom ? rectF.bottom : rectF.top) + 1, (int) Math.abs(rectF.width()) - 1, (int) Math.abs(rectF.height()) - 1));
        }
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(inputFile);
        cmdList.append("-vf");
        cmdList.append(deLogoCmd.toString().substring(0, deLogoCmd.length() - 1));
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(outFile);
        return cmdList.build();
    }

    /**
     * 裁剪视频
     */
    @SuppressLint("DefaultLocale")
    public static String[] resizeVideo(String inputFile, String outFile, RectF rectF) {
        RxFFmpegCommandList cmdList = new RxFFmpegCommandList();
        cmdList.append("-threads");
        cmdList.append("2");
        cmdList.append("-i");
        cmdList.append(inputFile);
        cmdList.append("-vf");
        cmdList.append("crop=" + rectF.width() + ":" + rectF.height() + ":" + rectF.left + ":" + rectF.top);
        cmdList.append("-preset");
        cmdList.append("superfast");
        cmdList.append(outFile);
        return cmdList.build();
    }

    public static String stringForTime(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        return new Formatter().format("%02d:%02d:%02d", hour, minute, second).toString();
    }
}
