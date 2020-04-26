package com.manyu.videoshare.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.bean.InviteBean;
import com.manyu.videoshare.util.GlideUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class ShareLevelAdapter extends CommonAdapter<InviteBean.DataBean.ShareLevelBean> {
    private Context context;
    public ShareLevelAdapter(Context context, List<InviteBean.DataBean.ShareLevelBean> datas) {
        super(context, R.layout.layout_share_level, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, InviteBean.DataBean.ShareLevelBean bean, int position) {
        holder.setText(R.id.level_name, bean.getName());
        holder.setText(R.id.level_content, bean.getRule());
        ImageView img = holder.getView(R.id.level_icon);
        GlideUtils.loadImg(context,bean.getImage(),img);
    }
}
