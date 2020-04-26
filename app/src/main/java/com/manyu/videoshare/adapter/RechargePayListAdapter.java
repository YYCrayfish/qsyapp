package com.manyu.videoshare.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.bean.RecharegeTypeBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class RechargePayListAdapter extends CommonAdapter<RecharegeTypeBean.DataBean.ListBean> {
    private LinearLayout layoutOld;
    //private TextView nameOld;
    // TextView valueOld;
    private Context context;
    private int selection = 0;
    public RechargePayListAdapter(Context context, List<RecharegeTypeBean.DataBean.ListBean> datas) {
        super(context, R.layout.layout_recharge_list, datas);
        this.context = context;
    }

    @Override
    protected void convert(ViewHolder holder, RecharegeTypeBean.DataBean.ListBean listBean,final int position) {
        final LinearLayout layout = holder.getView(R.id.recharge_list_title);
        final TextView name = holder.getView(R.id.recharge_list_name);
        final TextView value = holder.getView(R.id.recharge_list_value);
        TextView money = holder.getView(R.id.recharge_list_money);
        name.setText(listBean.getName());
        value.setText(listBean.getPrice_title());
        money.setText(listBean.getPrice_desc());
        money.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG );
        if(position == 0){
            layout.setBackground(context.getResources().getDrawable(R.drawable.recharge_two_back_select));
            //name.setTextColor(context.getResources().getColor(R.color.recharge_line));
            //value.setTextColor(context.getResources().getColor(R.color.recharge_line));
            this.layoutOld = layout;
            //this.nameOld = name;
            //this.valueOld = value;
            selection = position;
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selection != position) {
                    layout.setBackground(context.getResources().getDrawable(R.drawable.recharge_two_back_select));
                    //name.setTextColor(context.getResources().getColor(R.color.recharge_line));
                    //value.setTextColor(context.getResources().getColor(R.color.recharge_line));
                    layoutOld.setBackground(context.getResources().getDrawable(R.drawable.recharge_two_back));
                    //nameOld.setTextColor(context.getResources().getColor(R.color.main_text_support));
                    //valueOld.setTextColor(context.getResources().getColor(R.color.main_text_support));
                    layoutOld = layout;
                    //nameOld = name;
                    //valueOld = value;
                    selection = position;
                }
            }
        });
    }

    public int getSelection() {
        return selection;
    }
}
