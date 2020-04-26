package com.manyu.videoshare.ui.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;

import com.manyu.videoshare.R;
import com.manyu.videoshare.util.FileProviderUtils;

import org.devio.takephoto.app.TakePhoto;
import org.devio.takephoto.app.TakePhotoActivity;
import org.devio.takephoto.model.CropOptions;
import org.devio.takephoto.model.TResult;
import org.devio.takephoto.permission.InvokeListener;

import java.io.File;
import java.io.FileOutputStream;

import static com.manyu.videoshare.util.SystemProgramUtils.REQUEST_CODE_CAIQIE;

public class SelectPhoneActivity extends TakePhotoActivity {
    private CustomHelper customHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(this).inflate(R.layout.activity_select_phone, null);
        setContentView(contentView);
        customHelper = CustomHelper.of(contentView,SelectPhoneActivity.this);
        customHelper.onClick(getTakePhoto());
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        Intent intent = new Intent();
        String path = result.getImages().get(0).getOriginalPath();
        intent.putExtra("image",path);
        setResult(RESULT_OK,intent);
        finish();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
        finish();
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
        finish();
    }
}
