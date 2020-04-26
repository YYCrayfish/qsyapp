package com.manyu.videoshare.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.manyu.videoshare.R;
import com.manyu.videoshare.bean.SupportBean;
import com.manyu.videoshare.util.GlideUtils;
import com.manyu.videoshare.util.ToolUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class SupportAdapter extends CommonAdapter<SupportBean.DataBean> {
    private Context context;
    public SupportAdapter(Context context, List<SupportBean.DataBean> datas) {
        super(context, R.layout.gridview_layout, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, SupportBean.DataBean bean, int position) {
        int wid = ToolUtils.getScreenWidth() - ToolUtils.dip2px(66);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(wid/10,wid/10);
        params.setMargins(wid/20,0,wid/20,0);
        holder.getView(R.id.gridview_icon).setLayoutParams(params);
        holder.setText(R.id.gridview_name, bean.getName());
        GlideUtils.loadImg(context,bean.getThumbnail(), (ImageView) holder.getView(R.id.gridview_icon));
    }
}
