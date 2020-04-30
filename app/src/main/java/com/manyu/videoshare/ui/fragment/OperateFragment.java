package com.manyu.videoshare.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseFragment;
import com.manyu.videoshare.intefaces.UDataCallBack;
import com.manyu.videoshare.view.SelectColorView;
import com.manyu.videoshare.view.SelectView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class OperateFragment extends BaseFragment {
    private View view;
    private TextView degree;
    private SelectView selectView;
    private SelectColorView selectColorView;

    public static OperateFragment newInstance(String title, int type) {
        Bundle args = new Bundle();
        args.putInt("type", type);

        OperateFragment fragment = new OperateFragment();
        fragment.setmFragmentTitle(title);
        fragment.setArguments(args);
        return fragment;
    }

    public BaseFragment getFragment(){
        return this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_operate, container, false);
        initView();
        return view;
    }

    private void initView() {
        // 生成刻度值
        List<String> list = new ArrayList<>();
        for (int i = 10; i <= 100; i++) {
            list.add("" + i);
        }

        degree = view.findViewById(R.id.tv_degree);
        selectView = view.findViewById(R.id.selectView);
        selectColorView = view.findViewById(R.id.selectColorView);

        // 设置事件处理
        selectColorView.setOnSelectListener(new FragmentCallBack(1));
        selectView.showValue(list, new FragmentCallBack(2));
    }

    private class FragmentCallBack implements UDataCallBack{

        private int dataType;// 1|颜色  2|透明度

        public FragmentCallBack(int type){
            this.dataType = type;
        }

        @Override
        public void onDataReceive(String data) {
            if(dataType == 1){
                onSelectListener.onSelect(data);
            }else{
                onSelectListener.onSelectItem(data);
                degree.setText(data + "%");
            }
        }
    }

    private onSelectListener onSelectListener;

    public void setOnSelectListener(onSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public interface onSelectListener {
        void onSelectItem(String value);//透明度
        void onSelect(String colorStr);//颜色
    }
}
