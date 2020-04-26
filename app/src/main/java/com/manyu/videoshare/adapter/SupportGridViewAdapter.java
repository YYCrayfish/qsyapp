package com.manyu.videoshare.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.bean.SupportBean;
import com.manyu.videoshare.util.GlideUtils;

import java.util.List;

public class SupportGridViewAdapter extends RecyclerView.Adapter<SupportGridViewAdapter.TextHolder> {
    private Activity context;
    private List<SupportBean.DataBean> datas;
    public SupportGridViewAdapter(Activity context,List<SupportBean.DataBean> datas) {
        this.context = context;
        this.datas = datas;
    }

    @NonNull
    @Override
    public TextHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TextHolder(context.getLayoutInflater().inflate(R.layout.gridview_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TextHolder holder, int position) {
        GlideUtils.loadImg(context,datas.get(position).getThumbnail(), holder.icon);
        holder.name.setText(datas.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }
    public class TextHolder extends RecyclerView.ViewHolder {
        public ImageView icon;
        public TextView name;

        public TextHolder(View itemView) {
            super(itemView);
//            绑定xml布局中的控件
            icon =  itemView.findViewById(R.id.gridview_icon);
            name =  itemView.findViewById(R.id.gridview_name);
        }
    }
}
