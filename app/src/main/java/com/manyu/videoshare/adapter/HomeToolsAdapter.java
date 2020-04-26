package com.manyu.videoshare.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.bean.HomeItemBean;
import com.manyu.videoshare.bean.InviteBean;
import com.manyu.videoshare.util.GlideUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class HomeToolsAdapter extends CommonAdapter<HomeItemBean> {
    private Context context;
    public HomeToolsAdapter(Context context, List<HomeItemBean> datas) {
        super(context, R.layout.item_home_item, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, HomeItemBean bean, int position) {
        holder.setText(R.id.title,bean.getTitle());
        holder.setImageResource(R.id.tool_img,bean.getImg());
    }
}
