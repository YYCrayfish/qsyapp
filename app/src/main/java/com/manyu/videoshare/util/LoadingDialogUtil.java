package com.manyu.videoshare.util;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;

import com.manyu.videoshare.R;


/**
 * TODO loading
 */

public class LoadingDialogUtil extends Dialog {

    private static Context context;

    private LoadingDialogUtil instance;

    public LoadingDialogUtil(@NonNull Context context) {
        super(context, R.style.dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public LoadingDialogUtil(@NonNull Context context, int themeResId) {
        super(context, R.style.dialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    public static LoadingDialogUtil getInstance(Context context) {
        LoadingDialogUtil instance = new LoadingDialogUtil(context);
        View v = View.inflate(context, R.layout.loading_dialog_layout, null);
        instance.setContentView(v);
        return instance;
    }

}
