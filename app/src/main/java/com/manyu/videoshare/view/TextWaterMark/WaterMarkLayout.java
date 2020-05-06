package com.manyu.videoshare.view.TextWaterMark;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.manyu.videoshare.util.CalcUtil;
import com.manyu.videoshare.util.universally.LOG;

import java.util.ArrayList;
import java.util.List;

/**
 * 水印布局
 */
public class WaterMarkLayout extends RelativeLayout {

    // 水印组件列表
    private List<WaterMark> waterMarkList = new ArrayList<>();

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
    // 缩放限制
    private float maxScan = 60;
    private float minScan = 8;

    // 第一次触摸按下
    private float firstTouchX = 0;
    private float firstTouchY = 0;


    public WaterMarkLayout(Context context) {
        super(context);
    }

    public WaterMarkLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void addWaterMark(WaterMark waterMark, int x, int y) {
        // 重置X Y数据
        waterMark.setX(x);
        waterMark.setY(y);
        // 设置一个唯一ID 后面删除时需要用到 PS:只有触摸生效时才能获得真正的currentWaterMark，直接点击删除按钮可能删除的是最后一次触摸的其他水印
        waterMark.setWaterMarkId(System.currentTimeMillis());

        // 存入容器
        waterMarkList.add(waterMark);

        // 设置水印的拖拉按钮的触摸事件监听
        waterMark.setOnTouchListener(onTouchListener);
        waterMark.getBtnControl().setOnTouchListener(onTouchWaterButtonListener);
        waterMark.getBtnDelete().setOnClickListener(new OnClickDelete(waterMark.getWaterMarkId()));

        // 添加本布局中
        addView(waterMark);
    }

    public void addWaterMark(WaterMark waterMark) {
        int x = (getWidth() - waterMark.getWidth()) / 2;
        int y = (getHeight() - waterMark.getHeight()) / 2;

        // 重载
        addWaterMark(waterMark, x, y);
    }

    public void setWaterMarkShadowColor(String colorStr) {
        if (currentWaterMark != null)
            currentWaterMark.setWaterMarkShadowColor(colorStr);
    }

    public void setWaterMarkShadowAlpha(int alpha) {
        if (currentWaterMark != null)
            currentWaterMark.setWaterMarkShadowAlpha(alpha);
    }

    public void setWaterMarkColor(String colorStr) {
        if (currentWaterMark != null)
            currentWaterMark.setWaterMarkColor(colorStr);
    }

    public void setWaterMarkAlpha(int alpha) {
        if (currentWaterMark != null)
            currentWaterMark.setWaterMarkAlpha(alpha);
    }

    // 设置字体颜色
    public void setWaterMarkTextColor(String colorStr) {
        if (currentWaterMark != null) {
            currentWaterMark.setWaterMarkTextColor(colorStr);
        }
    }

    // 设置透明度
    public void setWaterMarkTextAlpha(int alpha) {
        if (currentWaterMark != null)
            currentWaterMark.setWaterMarkTextAlpha(alpha);
    }

    // 设置边框字体颜色
    public void setWaterMarkBorderColor(String colorStr) {
        if (currentWaterMark != null) {
            currentWaterMark.setWaterMarkBorderColor(colorStr);
        }
    }

    // 设置边框透明度
    public void setWaterMarkBorderAlpha(int alpha) {
        if (currentWaterMark != null)
            currentWaterMark.setWaterMarkBorderAlpha(alpha);
    }

    // 处理点击水印按钮事件
    OnTouchListener onTouchWaterButtonListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDown = true;
                    touchX = ((int) motionEvent.getX());
                    touchY = ((int) motionEvent.getY());
                    LOG.showE("水印的按钮的点击");
                    break;
                case MotionEvent.ACTION_UP:
                    touchDown = false;
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
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    LOG.showE("水印的点击");
                    break;
                case MotionEvent.ACTION_UP:
                    touchDown = false;
                    break;
            }
            return false;
        }
    };

    // 点击删除水印的自定义事件 PS:为了接收一个水印ID，便于删除对应的水印
    private class OnClickDelete implements OnClickListener {

        long waterMarkId;

        public OnClickDelete(long waterMarkId) {
            this.waterMarkId = waterMarkId;
        }

        @Override
        public void onClick(View view) {
            // 删除水印
            deleteWaterMark(waterMarkId);
        }
    }

    private void showLog(String tex) {
        Log.e("xushiyong", tex);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取拖动事件的发生位置
        float moveX = event.getX();
        float moveY = event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                boolean isAbleMove = CalcUtil.spacing(firstTouchX, firstTouchY, event.getX(), event.getY()) > 20;
                if (!(currentWaterMark == null) && isAbleMove) {

                    // 压住了缩放按钮
                    if (touchDown) {
                        currentWaterMark.measure(0, 0);
                        float centerX = currentWaterMark.getX() + currentWaterMark.getMeasuredWidth() / 2;
                        float centerY = currentWaterMark.getY() + currentWaterMark.getMeasuredHeight() / 2;

                        currentAngle = CalcUtil.angleBetweenLines(centerX, centerY, firstTouchX - touchX, firstTouchY - touchY, centerX, centerY, event.getX() - touchX, event.getY() - touchY) + lastAngle;
                        currentWaterMark.setRotation(currentAngle);

                        // 水印中心点 和 当前压按点的距离
                        float newDist = (float) CalcUtil.spacing(centerX, centerY, event.getX(), event.getY());
                        if (oldDist != 0) {
                            scale = newDist / oldDist;
                        }

//                        缩放值：2.6468241(newDist:221.5906 -- oldDist:83.71943)
//                        缩放值：0.41843542(newDist:92.72136 -- oldDist:221.5906)
//                        缩放值：2.3968143(newDist:222.23589 -- oldDist:92.72136)
//                        缩放值：0.35420856(newDist:78.71785 -- oldDist:222.23589)
//                        缩放值：2.9475708(newDist:232.02643 -- oldDist:78.71785)
//                        缩放值：0.48130453(newDist:111.67537 -- oldDist:232.02643)
//                        缩放值：1.9769487(newDist:220.77647 -- oldDist:111.67537)
//                        缩放值：0.37807494(newDist:83.470055 -- oldDist:220.77647)
//                        缩放值：2.7917287(newDist:233.02576 -- oldDist:83.470055)
//                        缩放值：0.4274602(newDist:99.60924 -- oldDist:233.02576)
//                        缩放值：2.3150952(newDist:230.60487 -- oldDist:99.60924)
//                        缩放值：0.33751738(newDist:77.83315 -- oldDist:230.60487)
                        //showLog("当前距离："+newDist+"  距离："+oldDist+"  缩放："+scale);

                        if (newDist > oldDist + 1) {
                            zoom(scale);
                            oldDist = newDist;
                        }
                        if (newDist < oldDist - 1) {
                            zoom(scale);
                            oldDist = newDist;
                        }
                        currentWaterMark.measure(0, 0);
                        currentWaterMark.setX(centerX - currentWaterMark.getMeasuredWidth() / 2);
                        currentWaterMark.setY(centerY - currentWaterMark.getMeasuredHeight() / 2);
                    } else {
                        // 因为手机屏幕是第二区间，所以这里减扣掉控件的宽高这样就可以在拖动时可以看完完整的文字内容
                        float tempX = event.getX() - currentWaterMark.getWidth() / 2;
                        float tempY = event.getY() - currentWaterMark.getHeight() / 2;
                        // 避免出屏幕界区
                        //if((event.getX() > 10 && event.getY() > 10 )){
                        currentWaterMark.setX(tempX);
                        currentWaterMark.setY(tempY);
                        //}
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                LOG.showE("普通的触摸的点击");
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


    // 缩放的重新定义实现
    private void zoom(float f) {
        // 避免出现缩放的范围过大或过小
        float tempTextSize = textSize * (f);
        if (tempTextSize > maxScan)
            tempTextSize = maxScan;
        else if (tempTextSize < minScan)
            tempTextSize = minScan;

        currentWaterMark.setTextSize(tempTextSize);
        // 这里很关键
        textSize = tempTextSize;
    }

    /**
     * 删除水印
     *
     * @param waterMarkID
     * @return
     */
    private void deleteWaterMark(long waterMarkID) {
        for (int i = 0; i < waterMarkList.size(); i++) {
            WaterMark wm = waterMarkList.get(i);
            if (wm.getWaterMarkId() == waterMarkID) {
                waterMarkList.remove(i);
                removeView(wm);
                break;
            }
        }
    }
}
