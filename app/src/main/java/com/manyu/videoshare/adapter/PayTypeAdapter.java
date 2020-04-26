package com.manyu.videoshare.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.manyu.videoshare.R;
import com.manyu.videoshare.bean.PayTypeBean;
import com.manyu.videoshare.util.GlideUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class PayTypeAdapter extends CommonAdapter<PayTypeBean.DataBean> {
    private Context context;
    private int selection = 0;
    private RadioButton oldRb;
    private LinearLayout layout;
    public PayTypeAdapter(Context context, List<PayTypeBean.DataBean> datas) {
        super(context, R.layout.layouy_paytype, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, PayTypeBean.DataBean bean, final int position) {
        holder.setText(R.id.layout_paytype_text,bean.getRealname());
        final RadioButton rb = holder.getView(R.id.layout_paytype_selection);
        LinearLayout layout = holder.getView(R.id.layout_paytype_title);
        GlideUtils.loadImg(context,bean.getImage2(), (ImageView) holder.getView(R.id.layout_paytype_icon));
        if(position == 0){
            selection = 0;
            oldRb = rb;
            rb.setChecked(true);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selection != position) {
                    rb.setChecked(true);
                    oldRb.setChecked(false);
                    oldRb = rb;
                    selection = position;
                }

            }
        });
    }

    public int getSelection() {
        return selection;
    }
}
