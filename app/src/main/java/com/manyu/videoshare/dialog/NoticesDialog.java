package com.manyu.videoshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.manyu.videoshare.R;


public class NoticesDialog extends Dialog {
    private Context mContext;

    public NoticesDialog(Context context,AnalysisUrlListener  analysisUrlListener) {
        super(context, R.style.dialog_clip);
        this.mContext = context;
        this.analysisUrlListener = analysisUrlListener;
        initView();
    }


    private void initView()  {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_notices, null);
        setContentView(view);
        setCancelable(false);
        Button buy = view.findViewById(R.id.dialog_notices_buy);
        Button cancle = view.findViewById(R.id.dialog_notices_cancle);
        Button share = view.findViewById(R.id.dialog_notices_gets);

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analysisUrlListener !=null){
                    analysisUrlListener.analysis();
                }
                dismiss();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (analysisUrlListener !=null){
                    analysisUrlListener.share();
                }
                dismiss();
            }
        });
    }

    private AnalysisUrlListener  analysisUrlListener;


    public interface AnalysisUrlListener{
        void analysis();
        void share();
    }

}
