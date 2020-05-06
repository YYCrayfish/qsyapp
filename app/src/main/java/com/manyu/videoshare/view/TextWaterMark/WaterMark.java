package com.manyu.videoshare.view.TextWaterMark;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.view.TextWaterMark.TextViewBorder;

public class WaterMark extends RelativeLayout {

    private TextViewBorder text;
    private ImageView btnControl;
    private ImageView btnDelete;
    long waterMarkId;
    // 当前水印文字颜色 默认白色
    String currentColor = "ffffff";
    // 当前水印文字透明度 默认不透明
    int currentAlpha = 255;
    // 当前水印边框颜色 默认白色
    String currentBorderColor = "ffffff";
    // 当前水印边框透明度 默认不透明
    int currentBorderAlpha = 255;
    // 当前水印颜色
    String currentWaterMarkColor = "00ffffff";
    // 当前水印透明度
    int currentWaterMarkAlpha = 255;
    // 当前水印颜色
    String currentWaterMarkShadowColor = "00ffffff";
    // 当前水印透明度
    int currentWaterMarkShadowAlpha = 255;

    // 默认的颜色和透明度
    String defaultTextColor = "ffffff";
    int defaultTextAlpha = 255;
    String defaultBorderColor = "ffffff";
    int defaultBorderAlpha = 255;
    String defaultWaterMarkColor = "00ffffff";
    int defaultWaterMarkAlpha = 255;
    String defaultWaterMarkShadowColor = "ffffff";
    int defaultWaterMarkShadowAlpha = 255;

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

    public void setWaterMarkShadowColor(String colorStr){
        defaultWaterMarkShadowColor = colorStr;
        text.setShadow(getIntColor(defaultWaterMarkShadowAlpha,defaultWaterMarkShadowColor));
    }

    public void setWaterMarkShadowAlpha(int alpha){
        defaultWaterMarkShadowAlpha = alpha;
        text.setShadow(getIntColor(defaultWaterMarkShadowAlpha,defaultWaterMarkShadowColor));
    }

    public void setWaterMarkColor(String colorStr){
        currentWaterMarkColor = colorStr;
        text.setBGColor(true,getIntColor(currentWaterMarkAlpha,currentWaterMarkColor));
    }

    public void setWaterMarkAlpha(int alpha){
        currentWaterMarkAlpha = alpha;
        text.setBGColor(true,getIntColor(currentWaterMarkAlpha,currentWaterMarkColor));
    }

    /**
     * 设置水印文字颜色
     * @param colorStr  格式例如：ffffff 白色  前面不带#号
     */
    public void setWaterMarkTextColor(String colorStr){
        currentColor = colorStr;
        setTextColorWithAlpha();
    }

    /**
     * 设置水印文字透明度
     * @param alpha
     */
    public void setWaterMarkTextAlpha(int alpha){
        currentAlpha = alpha;

        // 设置背景透明
        View view = findViewById(R.id.waterText);
        if(view != null)
            view.getBackground().setAlpha(alpha);

        // 设置文字透明
        setTextColorWithAlpha();
    }

    /**
     * 设置边框水印文字颜色
     * @param colorStr  格式例如：ffffff 白色  前面不带#号
     */
    public void setWaterMarkBorderColor(String colorStr){
        currentBorderColor = colorStr;
        text.setBorderColor(getIntColor(currentBorderAlpha,currentBorderColor));
    }

    private int getIntColor(int alpha, String color){
        String hex = Integer.toHexString(alpha);
        int tempColor = Color.parseColor("#"+hex+color);
        return tempColor;
    }

    /**
     * 设置水印边框透明度
     * @param alpha
     */
    public void setWaterMarkBorderAlpha(int alpha){
        currentBorderAlpha = alpha;
        String hex = Integer.toHexString(currentBorderAlpha);
        int tempColor = Color.parseColor("#"+hex+currentBorderColor);
        text.setBorderColor(tempColor);
    }

    /**
     *   设置文字颜色和透明度
    */
    private void setTextColorWithAlpha(){
        if(text != null){
            // 解析拼接出一个带透明度的颜色值 PS:因为Color.argb似乎没有效果，只能替换这种方式来更变文字透明度
            String hex = Integer.toHexString(currentAlpha);
            int tempColor = Color.parseColor("#"+hex+currentColor);
            text.setTextColor(tempColor);
        }
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
