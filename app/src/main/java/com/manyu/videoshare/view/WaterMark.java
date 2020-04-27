package com.manyu.videoshare.view;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manyu.videoshare.R;

public class WaterMark extends RelativeLayout {

    private TextView text;
    private ImageView btnControl;
    private ImageView btnDelete;
    long waterMarkId;

    public WaterMark(Context context){
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.view_water_mark,null);
        addView(view);

        text = findViewById(R.id.waterText);
        btnControl = findViewById(R.id.waterButtonControl);
        btnDelete = findViewById(R.id.waterButtonDelete);
    }

    public void setText(String content){
        if(!(text == null))
            text.setText(content);
    }

    public ImageView getBtnControl(){
        return btnControl;
    }

    public ImageView getBtnDelete(){return btnDelete;}

    public void setTextSize(float textSize){
        if(text != null)
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
    }

    public float getTextSize(){
        return text!=null ? text.getTextSize():0;
    }

    // 隐藏按钮 在生成视频前需要先隐藏掉，不然视频上的水印图片会带有这两个小控制按钮
    public void hideButton(){
        if(btnControl != null)
            btnControl.setVisibility(View.GONE);
        if(btnDelete != null)
            btnDelete.setVisibility(View.GONE);
    }

    public void setWaterMarkId(long id){
        waterMarkId = id;
    }

    public long getWaterMarkId(){
        return waterMarkId;
    }
}
