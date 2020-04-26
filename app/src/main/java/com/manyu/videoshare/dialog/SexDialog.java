package com.manyu.videoshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.manyu.videoshare.R;


public class SexDialog extends Dialog {
    private Context mContext;

    public SexDialog(Context context, AnalysisUrlListener  analysisUrlListener) {
        super(context, R.style.dialog_clip);
        this.mContext = context;
        this.analysisUrlListener = analysisUrlListener;
        initView();
    }


    private void initView()  {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_sex, null);
        setContentView(view);
        setCancelable(false);
        TextView man = view.findViewById(R.id.dialog_sex_man);
        TextView woman = view.findViewById(R.id.dialog_sex_woman);
        TextView secrecy = view.findViewById(R.id.dialog_sex_secrecy);

        man.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analysisUrlListener !=null){
                    analysisUrlListener.analysis(1);
                }
                dismiss();
            }
        });

        woman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analysisUrlListener !=null){
                    analysisUrlListener.analysis(2);
                }
                dismiss();
            }
        });
        secrecy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analysisUrlListener !=null){
                    analysisUrlListener.analysis(0);
                }
                dismiss();
            }
        });
    }

    private AnalysisUrlListener  analysisUrlListener;


    public interface AnalysisUrlListener{
        void analysis(int num);
    }

}
