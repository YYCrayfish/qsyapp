package com.manyu.videoshare.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manyu.videoshare.util.CalcUtil;
import com.manyu.videoshare.view.TextWaterMark.TextViewParams;

import java.util.ArrayList;
import java.util.List;

/**
 * 水印布局
 */
public class WaterMarkLayout extends RelativeLayout {

    // 水印组件列表
    private List<WaterMark> waterMarkList = new ArrayList<>();
    // 所有水印组件的属性列表
    private List<TextViewParams> listTvParams = new ArrayList<>();

    // 当前的水印
    private WaterMark currentWaterMark;
    // 触摸的判断 是否按下
    private boolean touchDown = false;
    // 拖动产生的水印 X Y
    private int touchX;
    private int touchY;

    private float oldDist = 0;
    private float textSize = 15;
    private float currentAngle = 0;
    private float lastAngle = 0;
    private float scale = 0;

    // 记录当前水印的宽高
    private int waterMakeWidth;
    private int waterMakeHeight;

    //记录当前设备的缩放倍数
    private double scaleTimes = 1;

    // 第一次触摸按下
    private float firstTouchX = 0;
    private float firstTouchY = 0;


    public WaterMarkLayout(Context context) {
        super(context);
    }

    public WaterMarkLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addWaterMark(WaterMark waterMark, int x, int y){
        // 重置X Y数据
        waterMark.setX(x);
        waterMark.setY(y);

        // 存入容器
        waterMarkList.add(waterMark);

        // 设置水印的拖拉按钮的触摸事件监听
        waterMark.setOnTouchListener(onTouchListener);
        waterMark.getButton().setOnTouchListener(onTouchWaterButtonListener);

        // 添加本布局中
        addView(waterMark);
//        saveTextViewparams();
    }

    public void addWaterMark(WaterMark waterMark){
        int x = (getWidth()  - waterMark.getWidth()) / 2;
        int y = (getHeight() - waterMark.getHeight()) / 2;

        // 重载
        addWaterMark(waterMark,x,y);
    }

    OnTouchListener onTouchWaterButtonListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDown = true;
                    touchX = ((int) motionEvent.getX());
                    touchY = ((int) motionEvent.getY());
//                    firstTouchX = motionEvent.getX();
//                    firstTouchY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    touchDown = false;
//                    lastAngle = currentAngle;
                    break;
            }
            return false;
        }
    };

    // 处理水印的点击触摸事件
    OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            // 这个赋值操作的作用是可以在触摸到对应的水印时，控制对应的水印
            currentWaterMark = (WaterMark) view;
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
//                    firstTouchX = motionEvent.getX();
//                    firstTouchY = motionEvent.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    touchDown = false;
//                    lastAngle = currentAngle;
//                    updateTextViewParams(( TextView )view , currentAngle, scale);
                    break;
//                case MotionEvent.ACTION_MOVE:
////                    if(touchDown){
////                        view.setX(motionEvent.getX());
////                        view.setY(motionEvent.getY());
////                        touchX = (int) motionEvent.getX();
////                        touchY = (int) motionEvent.getY();
//                    currentWaterMark.setX(motionEvent.getX() - touchX);
//                    currentWaterMark.setY(motionEvent.getY() - touchY);
////                    }
//                    break;
            }
            return false;
        }
    };

    private void showLog(String tex){
        Log.e("xushiyong",tex);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e("xushiyong","触摸页面"+event.getAction());
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_MOVE:
                boolean isAbleMove = CalcUtil.spacing(firstTouchX, firstTouchY, event.getX(), event.getY()) > 20;
                if(!(currentWaterMark == null) && isAbleMove){

                    // 压住了缩放按钮
                    if(touchDown){
                        currentWaterMark.measure(0, 0);

//                        showLog("当前角度："+newDist+"  距离："+oldDist+"  缩放："+scale);

//                        showLog("进行设置缩放：缩放->"+scale+"  新的距离->"+newDist+"  旧的距离->"+oldDist);

                        currentAngle = CalcUtil.angleBetweenLines(currentWaterMark.getX() + currentWaterMark.getMeasuredWidth() / 2, currentWaterMark.getY() + currentWaterMark.getMeasuredWidth() / 2, firstTouchX - touchX, firstTouchY - touchY, currentWaterMark.getX() + currentWaterMark.getMeasuredWidth() / 2, currentWaterMark.getY() + currentWaterMark.getMeasuredWidth() / 2, event.getX() - touchX, event.getY() - touchY) + lastAngle;
//                        showLog("当前角度："+currentAngle+"  距离："+newDist+"  缩放："+scale);
//                        if(currentAngle - lastAngle < 10){
//                            showLog("这是想缩放");
//                        }
//                        else{
//                            showLog("这是想拖动");
//                        }
//                        lastAngle = currentAngle;
                        currentWaterMark.setRotation(currentAngle);

                        // 水印中心点 和 当前压按点的距离
                        float newDist = (float) CalcUtil.spacing(currentWaterMark.getX() + currentWaterMark.getMeasuredWidth() / 2, currentWaterMark.getY() + currentWaterMark.getMeasuredWidth() / 2, event.getX(), event.getY());
                        if (oldDist != 0) {
                            scale = newDist / oldDist;
                        }

                        if ( newDist > oldDist + 1 ) {
                            zoom(scale);
                            oldDist = newDist;
                        }
                        if ( newDist < oldDist - 1 ) {
                            zoom(scale);
                            oldDist = newDist;
                        }

                        showLog("缩放值："+scale+"(newDist:"+newDist+" -- oldDist:"+oldDist+")"+"  角度:"+currentAngle);

                    }
                    else{
                        // 因为手机屏幕是第二区间，所以这里减扣掉控件的宽高这样就可以在拖动时可以看完完整的文字内容
                        currentWaterMark.setX(event.getX() - currentWaterMark.getWidth());
                        currentWaterMark.setY(event.getY() - currentWaterMark.getHeight());
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                firstTouchX = event.getX();
                firstTouchY = event.getY();
                currentWaterMark.measure(0, 0);
                oldDist = (float) CalcUtil.spacing(currentWaterMark.getX() + currentWaterMark.getMeasuredWidth() / 2, currentWaterMark.getY() + currentWaterMark.getMeasuredWidth() / 2, firstTouchX, firstTouchY);
                break;
            case MotionEvent.ACTION_UP:
                lastAngle = currentAngle;

                // 因为抬起来时如果和按下去的触点不同，不会受到抬起事件，所以需要最大范围的触屏监听中赋值
                touchDown = false;
                break;
        }
        return true;
    }


    //缩放实现
    private void zoom(float f) {
//        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(380,400);
//        currentWaterMark.setLayoutParams(rl);
//        f = f > 1.5f ? 1.5f:f;
//        float temp = textSize * f;
//        temp = temp > 40 ? 40 : temp;
//        showLog("缩放至："+f);
        currentWaterMark.setTextSize(textSize * f);
//        if (!currentWaterMark.isOpenStroke() && shadowColor != 0) {
//            currentWaterMark.setShadowLayer((textSize / 6) * scale, (textSize / 6) * scale, (textSize / 6) * scale, shadowColor);
//        }
    }

//    /**
//     * 对控件进行参数的更新操作
//     *
//     * @param tv
//     */
//    private void updateTextViewParams(TextView tv, float rotation, float scale) {
//        for ( int i = 0; i < listTvParams.size(); i++ ) {
//            TextViewParams param = new TextViewParams();
//            if ( tv.getTag().toString().equals(listTvParams.get(i).getTag()) ) {
//                param.setRotation(rotation);
//                param.setTextSize(( float ) (tv.getTextSize() / scaleTimes));
//                param.setMidPoint(CalcUtil.getViewMidPoint(tv));
//                param.setScale(scale);
//                textSize = tv.getTextSize() / 2;
//                param.setWidth(tv.getWidth());
//                param.setHeight(tv.getHeight());
//                param.setX(tv.getX());
//                param.setY(tv.getY());
//                param.setTag(listTvParams.get(i).getTag());
//                param.setContent(tv.getText().toString());
//                param.setTextColor(tv.getCurrentTextColor());
//                listTvParams.set(i, param);
//                return;
//            }
//        }
//    }
//
//    /**
//     * //对状态进行保存操作
//     *
//     * @param textView
//     * @return
//     */
//    private void saveTextViewparams(WaterMark textView) {
//        if ( textView != null ) {
//            TextViewParams tvParams = new TextViewParams();
//            tvParams.setRotation(0);
//            tvParams.setTextSize(( float ) (textView.getTextSize() / scaleTimes));
//            tvParams.setX(textView.getX());
//            tvParams.setY(textView.getY());
//            tvParams.setWidth(textView.getWidth());
//            tvParams.setHeight(textView.getHeight());
//            tvParams.setContent(textView.getTextString());
//            tvParams.setMidPoint(CalcUtil.getViewMidPoint(textView));
//            tvParams.setScale(1);
//            tvParams.setTag(String.valueOf(( long ) textView.getTag()));
//            tvParams.setRotation(currentAngle);
////            tvParams.setTextColor(textView.getCurrentTextColor());
//            listTvParams.add(tvParams);
//        }
//    }

//    /**
//     * 根据TextView获取到该TextView的配置文件
//     *
//     * @param tv
//     * @return
//     */
//    private TextViewParams getTextViewParams(TextView tv) {
//        for ( int i = 0; i < listTvParams.size(); i++ ) {
//            if ( listTvParams.get(i).getTag().equals(String.valueOf(( long ) tv.getTag())) ) {
//                return listTvParams.get(i);
//            }
//        }
//        return null;
//    }

}
