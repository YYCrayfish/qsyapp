package com.manyu.videoshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.manyu.videoshare.R;


public class SuccessDialog extends Dialog {
    private Context mContext;

    public SuccessDialog(Context context, String text, AnalysisUrlListener  analysisUrlListener) {
        super(context, R.style.dialog_clip);
        this.mContext = context;
        this.analysisUrlListener = analysisUrlListener;
        initView(text);
    }


    private void initView(String text)  {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_success, null);
        setContentView(view);
        setCancelable(false);
        TextView cancle = view.findViewById(R.id.dialog_clip_cancle);
        TextView title = view.findViewById(R.id.dialog_title);

        title.setText(text);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analysisUrlListener !=null){
                    analysisUrlListener.analysis();
                }
                dismiss();
            }
        });
    }

    private AnalysisUrlListener  analysisUrlListener;


    public interface AnalysisUrlListener{
        void analysis();
    }

}
