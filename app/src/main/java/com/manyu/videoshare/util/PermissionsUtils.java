package com.manyu.videoshare.util;

import android.app.Activity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.manyu.videoshare.base.BaseApplication;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by Leo on 2018/7/9.
 */

public class PermissionsUtils {

    public static int checkPermisson(String permission)
    {
        final int[] checkType = {0};
        Disposable subscribe = new RxPermissions((Activity) BaseApplication.getContext()).requestEach(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_CALENDAR,
//                Manifest.permission.READ_CALL_LOG,
//                Manifest.permission.READ_CONTACTS,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.READ_SMS,
//                Manifest.permission.RECORD_AUDIO,
//                Manifest.permission.CAMERA,
//                Manifest.permission.CALL_PHONE
                permission

        ).subscribe(new Consumer<Permission>() {
            @Override
            public void accept(Permission permission) throws Exception {
                if (permission.granted) {
                    // 用户已经同意该权限
                    checkType[0] = 1;
                } else if (permission.shouldShowRequestPermissionRationale) {
                    // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                    checkType[0] = 2;
                } else {
                    // 用户拒绝了该权限，并且选中『不再询问』
                    checkType[0] = 3;
                }
            }
        });

        subscribe.dispose();
        return checkType[0];

    }


}
