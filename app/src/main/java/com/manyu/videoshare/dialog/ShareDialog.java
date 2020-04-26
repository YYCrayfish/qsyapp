package com.manyu.videoshare.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manyu.videoshare.R;


public class ShareDialog extends Dialog implements View.OnClickListener {
    private Context mContext;

    public ShareDialog(Context context,AnalysisUrlListener  analysisUrlListener) {
        super(context, R.style.dialog_clip);
        this.mContext = context;
        this.analysisUrlListener = analysisUrlListener;
        initView();
    }


    private void initView()  {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_share, null);
        setContentView(view);
        setCancelable(false);
        LinearLayout qq = view.findViewById(R.id.dialog_share_qq);
        LinearLayout mm = view.findViewById(R.id.dialog_share_mm);
        LinearLayout wb = view.findViewById(R.id.dialog_share_wb);
        LinearLayout down = view.findViewById(R.id.dialog_share_save);
        LinearLayout copy = view.findViewById(R.id.dialog_share_copy);
        TextView textView = view.findViewById(R.id.dialog_share_cancle);

        textView.setOnClickListener(this);
        qq.setOnClickListener(this);
        mm.setOnClickListener(this);
        wb.setOnClickListener(this);
        down.setOnClickListener(this);
        copy.setOnClickListener(this);

    }

    private AnalysisUrlListener  analysisUrlListener;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.dialog_share_qq:
                analysisUrlListener.analysis(0);
                dismiss();
                break;
            case R.id.dialog_share_mm:
                analysisUrlListener.analysis(1);
                dismiss();
                break;
            case R.id.dialog_share_wb:
                analysisUrlListener.analysis(2);
                dismiss();
                break;
            case R.id.dialog_share_save:
                analysisUrlListener.analysis(3);
                dismiss();
                break;
            case R.id.dialog_share_copy:
                analysisUrlListener.analysis(4);
                dismiss();
                break;
            case R.id.dialog_share_cancle:
                dismiss();
                break;

        }
    }


    public interface AnalysisUrlListener{
        void analysis(int position);
        void close();
    }

}
