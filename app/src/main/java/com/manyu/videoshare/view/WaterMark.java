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

    TextView text;
    ImageView button;

    public WaterMark(Context context){
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.view_water_mark,null);
        addView(view);

        text = findViewById(R.id.waterText);
        button = findViewById(R.id.waterButton);
    }

    public void setText(String content){
        if(!(text == null))
            text.setText(content);
    }

    public ImageView getButton(){
        return button;
    }

    public void setTextSize(float textSize){
        if(text != null)
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
    }

    public float getTextSize(){
        return text!=null ? text.getTextSize():0;
    }

    public String getTextString(){
        return text!=null ? text.getText().toString():"";
    }

    public float getXX(){
        return getX();
    }

    public float getYY(){
        return getY();
    }



//    public void setScale(float scale){
//        if(text != null)
//            text.setSca(textSize);
//
//    }
}
