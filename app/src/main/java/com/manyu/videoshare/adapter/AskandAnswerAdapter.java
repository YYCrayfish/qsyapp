package com.manyu.videoshare.adapter;

import android.content.Context;
import android.text.Html;

import com.manyu.videoshare.R;
import com.manyu.videoshare.bean.AskandAnswerBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class AskandAnswerAdapter extends CommonAdapter<AskandAnswerBean.DataBean> {
    public AskandAnswerAdapter(Context context, List<AskandAnswerBean.DataBean> datas) {
        super(context, R.layout.layout_ask_adapter, datas);
    }

    @Override
    protected void convert(ViewHolder holder, AskandAnswerBean.DataBean askandAnswerBean, int position) {
        holder.setText(R.id.adapter_ask_question, String.valueOf(Html.fromHtml(askandAnswerBean.getQ())));
        holder.setText(R.id.adapter_ask_answer, String.valueOf(Html.fromHtml(askandAnswerBean.getAnswer())));
    }
}
