package com.manyu.videoshare.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.base.BaseFragment;
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
        initData();
        return view;
    }

    private void initView() {
        List<String> list = new ArrayList<>();
        for (int i = 10; i <= 100; i++) {
            list.add("" + i);
        }
        degree = view.findViewById(R.id.tv_degree);
        selectView = view.findViewById(R.id.selectView);
        selectColorView = view.findViewById(R.id.selectColorView);
        selectColorView.setOnSelectListener(new SelectColorView.onSelectListener() {
            @Override
            public void onSelect(String colorStr) {
                onSelectListener.onSelect(colorStr);
            }
        });
        selectView.showValue(list, new SelectView.onSelect() {
            @Override
            public void onSelectItem(String value) {
                onSelectListener.onSelectItem(value);
                degree.setText(value + "%");
            }
        });
    }

    private void initData() {

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
