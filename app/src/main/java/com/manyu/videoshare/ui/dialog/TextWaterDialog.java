package com.manyu.videoshare.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.manyu.videoshare.R;

public class TextWaterDialog extends Dialog {
    private EditText editText;
    private TextView textView;
    private OnClickListener listener;

    public TextWaterDialog(@NonNull Context context) {
        super(context, R.style.DialogTheme);
    }

    public TextWaterDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_text_water);
        initView();
    }

    private void initView() {
        editText = findViewById(R.id.et_content);
        textView = findViewById(R.id.yes);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.getText(editText.getText().toString());
                dismiss();
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.listener = onClickListener;
    }

    public interface OnClickListener {
        abstract void getText(String content);
    }
}
