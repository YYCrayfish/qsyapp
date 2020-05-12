package com.manyu.videoshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.manyu.videoshare.R;


public class ExitDialog extends Dialog {
    private Context mContext;

    public ExitDialog(Context context,String text,AnalysisUrlListener  analysisUrlListener) {
        super(context, R.style.dialog);
        this.mContext = context;
        this.analysisUrlListener = analysisUrlListener;
        initView(text);
    }


    private void initView(String text)  {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_exit, null);
        setContentView(view);
        setCancelable(false);
        TextView textView = view.findViewById(R.id.dialog_clip_text);
        TextView cancle = view.findViewById(R.id.dialog_clip_cancle);
        TextView analysis = view.findViewById(R.id.dialog_clip_analysis);
        if(text != null) {
            textView.setText(text);
        }
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analysisUrlListener !=null){
                    analysisUrlListener.clean();
                }
                dismiss();
            }
        });

        analysis.setOnClickListener(new View.OnClickListener() {
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
        void clean();
    }

}
