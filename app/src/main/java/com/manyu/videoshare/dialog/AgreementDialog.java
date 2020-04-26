package com.manyu.videoshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.manyu.videoshare.R;


public class AgreementDialog extends Dialog {
    private Context mContext;

    public AgreementDialog(Context context,String title, String text) {
        super(context, R.style.dialog_clip1);
        this.mContext = context;
        initView(title,text);
    }


    private void initView(String title,String text)  {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_argeement, null);
        setContentView(view);
        setCancelable(false);
        TextView textinfo = view.findViewById(R.id.dialog_agreement_text);
        TextView cancle = view.findViewById(R.id.dialog_agreement_cancle);
        TextView tvTitle = view.findViewById(R.id.dialog_agreement_title);
        tvTitle.setText(title);
        textinfo.setText(Html.fromHtml(text));
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private AnalysisUrlListener  analysisUrlListener;


    public interface AnalysisUrlListener{
        void analysis();
    }

}
