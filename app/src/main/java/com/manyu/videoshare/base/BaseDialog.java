package com.manyu.videoshare.base;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import com.manyu.videoshare.R;

import java.util.HashMap;

/**
 * Created by Leo on 2017/11/6.
 */

public abstract class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        super(context, R.style.dialog_style);
    }



    public abstract void initView(Context context);
    public abstract void initData(HashMap<String,Object> hashMap);

}
