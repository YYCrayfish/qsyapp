package com.manyu.videoshare.util.universally;

import android.os.Environment;

import java.io.File;

/**
 * Author：xushiyong
 * Date：2020/5/1
 * Descript：
 */
public interface ConfigureParameter {
    // 系统相册路径
    String SYSTEM_CAMERA_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Camera"+ File.separator;
    // 分割视频时的临时文件存储地址
    String VIDEO_TEM_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Camera"+ File.separator+"tempFile"+File.separator;
    // 修改视频 MD5 值时的临时视频文件路径
    String MODIFY_MD5_TEMP_VIDEO_FILE = SYSTEM_CAMERA_PATH + "tempVideo.mp4";//Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/Camera"+ File.separator+"tempFile"+File.separator;

}
