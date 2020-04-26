package com.manyu.videoshare.ui.user;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.manyu.videoshare.util.FileProviderUtils;
import com.manyu.videoshare.util.ToolUtils;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.compress.CompressConfig;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.LubanOptions;
import org.devio.takephoto.model.TakePhotoOptions;

import java.io.File;


/**
 * - 支持通过相机拍照获取图片
 * - 支持从相册选择图片
 * - 支持从文件选择图片
 * - 支持多图选择
 * - 支持批量图片裁切
 * - 支持批量图片压缩
 * - 支持对图片进行压缩
 * - 支持对图片进行裁剪
 * - 支持对裁剪及压缩参数自定义
 * - 提供自带裁剪工具(可选)
 * - 支持智能选取及裁剪异常处理
 * - 支持因拍照Activity被回收后的自动恢复
 * Author: crazycodeboy
 * Date: 2016/9/21 0007 20:10
 * Version:4.0.0
 * 技术博文：http://www.devio.org
 * GitHub:https://github.com/crazycodeboy
 * Email:crazycodeboy@gmail.com
 */
public class CustomHelper {
    private Activity context;
    public static CustomHelper of(View rootView,Activity Context) {
        return new CustomHelper(Context);
    }

    private CustomHelper(Activity context) {
        this.context = context;
    }


    public void onClick(TakePhoto takePhoto) {
        String destFileDir = Environment.getExternalStorageDirectory() + "/manyu/";
        File dir = new File(destFileDir);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        File outputFile = new File(Environment.getExternalStorageDirectory() + "/manyu/head_out.png");
        Uri output =  FileProviderUtils.uriFromFile(context, outputFile);
        configCompress(takePhoto);
        configTakePhotoOption(takePhoto);
        /*switch (view.getId()) {
            case R.id.btnPickBySelect:*/
                int limit = 1;
                if (limit > 1) {
                    //if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {
                        takePhoto.onPickMultipleWithCrop(limit, getCropOptions());
                    /*} else {
                        takePhoto.onPickMultiple(limit);
                    }*/
                    return;
                }
                /*if (rgFrom.getCheckedRadioButtonId() == R.id.rbFile) {
                    if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {
                        takePhoto.onPickFromDocumentsWithCrop(imageUri, getCropOptions());
                    } else {
                        takePhoto.onPickFromDocuments();
                    }
                    return;
                } else {
                    if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {*/
                        takePhoto.onPickFromGalleryWithCrop(output, getCropOptions());
                    /*} else {
                        takePhoto.onPickFromGallery();
                    }
                }*/
                /*break;
            case R.id.btnPickByTake:
                if (rgCrop.getCheckedRadioButtonId() == R.id.rbCropYes) {
                    takePhoto.onPickFromCaptureWithCrop(imageUri, getCropOptions());
                } else {
                    takePhoto.onPickFromCapture(imageUri);
                }
                break;
            default:
                break;
        }*/
    }

    private void configTakePhotoOption(TakePhoto takePhoto) {
        TakePhotoOptions.Builder builder = new TakePhotoOptions.Builder();
        //if (rgPickTool.getCheckedRadioButtonId() == R.id.rbPickWithOwn) {
            builder.setWithOwnGallery(true);
        //}
        /*if (rgCorrectTool.getCheckedRadioButtonId() == R.id.rbCorrectYes) {
            builder.setCorrectImage(true);
        }*/
        takePhoto.setTakePhotoOptions(builder.create());

    }

    private void configCompress(TakePhoto takePhoto) {
        //if (rgCompress.getCheckedRadioButtonId() != R.id.rbCompressYes) {
            takePhoto.onEnableCompress(null, false);
            return;
        //}
        /*int maxSize = Integer.parseInt(etSize.getText().toString());
        int width = Integer.parseInt(etCropWidth.getText().toString());
        int height = Integer.parseInt(etHeightPx.getText().toString());
        boolean showProgressBar = rgShowProgressBar.getCheckedRadioButtonId() == R.id.rbShowYes ? true : false;
        boolean enableRawFile = rgRawFile.getCheckedRadioButtonId() == R.id.rbRawYes ? true : false;
        CompressConfig config;
        if (rgCompressTool.getCheckedRadioButtonId() == R.id.rbCompressWithOwn) {
            config = new CompressConfig.Builder().setMaxSize(maxSize)
                .setMaxPixel(width >= height ? width : height)
                .enableReserveRaw(enableRawFile)
                .create();
        } else {
            LubanOptions option = new LubanOptions.Builder().setMaxHeight(height).setMaxWidth(width).setMaxSize(maxSize).create();
            config = CompressConfig.ofLuban(option);
            config.enableReserveRaw(enableRawFile);
        }
        takePhoto.onEnableCompress(config, showProgressBar);*/


    }

    private CropOptions getCropOptions() {
        CropOptions.Builder builder = new CropOptions.Builder();

        //builder.setAspectX(1).setAspectY(1);
        builder.setOutputX(ToolUtils.dip2px(150)).setOutputY(ToolUtils.dip2px(150));
        builder.setWithOwnCrop(true);
        return builder.create();
    }

}
