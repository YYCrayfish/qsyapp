package com.manyu.videoshare.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.manyu.videoshare.base.BaseSharePerence;
import com.manyu.videoshare.ui.account.LoginAcitivty;
import com.manyu.videoshare.ui.vip.RechargeActivity;
import com.manyu.videoshare.util.IntentUtils;
import com.manyu.videoshare.util.ToolUtils;

import java.util.List;


/**
 * Created by qwe on 2017/12/18.
 */

public class RollPagerAdapter extends StaticPagerAdapter {
    private List<Integer> mUrlList;
    private Context context;
    public RollPagerAdapter(List<Integer> mUrlList,Context context){
        this.mUrlList = mUrlList;
        this.context = context;
    }
    @Override
    public View getView(ViewGroup viewGroup, final int position) {
       final ImageView view = new ImageView(viewGroup.getContext());
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.setImageDrawable(context.getResources().getDrawable(mUrlList.get(position)));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolUtils.havingIntent(context);
                if(position == 1){
                    if(BaseSharePerence.getInstance().getLoginKey().equals("0")){
                        IntentUtils.JumpActivity(context,LoginAcitivty.class);
                    }else{
                        IntentUtils.JumpActivity(context,RechargeActivity.class);
                    }
                }
            }
        });
        return view;
    }
    @Override
    public int getCount() {
        return mUrlList.size();
    }
}
