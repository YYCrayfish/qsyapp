package com.manyu.videoshare.view.TextWaterMark;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.manyu.videoshare.util.CalcUtil;
import com.manyu.videoshare.util.ImageUtil;
import com.manyu.videoshare.util.universally.LOG;
import com.manyu.videoshare.view.scraw.ScrawlBoardView;

import java.util.ArrayList;
import java.util.List;

/**
 * 水印布局
 */
public class WaterMarkLayout extends RelativeLayout {

    // 水印组件列表
    private List<WaterMark> waterMarkList = new ArrayList<>();

    private ScrawlBoardView scrawlBoardView;
    // 当前的水印
    private WaterMark currentWaterMark;
    // 触摸的判断 是否按下
    private boolean touchDown = false;
    // 拖动产生的水印 X Y
    private int touchX;
    private int touchY;

    private float currentAngle = 0;
    private float lastAngle = 0;
    private float scale = 0;

    // 第一次触摸按下
    private float firstTouchX = 0;
    private float firstTouchY = 0;

    private float moveX = 0;
    private float moveY = 0;
    private float currentMarkCenterX;
    private float currentMarkCenterY;


    public WaterMarkLayout(Context context) {
        this(context, null);
    }

    public WaterMarkLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scrawlBoardView = new ScrawlBoardView(context);
        addView(scrawlBoardView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public ScrawlBoardView getScrawlBoardView() {
        return scrawlBoardView;
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

        currentWaterMark = waterMark;
    }

    public void addWaterMark(final WaterMark waterMark) {
        // 重载
        addWaterMark(waterMark, 0, 0);
        post(new Runnable() {
            @Override
            public void run() {
                int x = (getWidth() - waterMark.getWidth()) / 2;
                int y = (getHeight() - waterMark.getHeight()) / 2;
                waterMark.setX(x);
                waterMark.setY(y);
            }
        });
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
        if (scrawlBoardView.isEnabled()) {
            return scrawlBoardView.onTouchEvent(event);
        }
        // 获取拖动事件的发生位置
        final float moveX = event.getX();
        final float moveY = event.getY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_MOVE:
                boolean isAbleMove = CalcUtil.spacing(firstTouchX, firstTouchY, event.getX(), event.getY()) > 20;
                if (!(currentWaterMark == null) && isAbleMove) {
                    // 压住了缩放按钮
                    if (touchDown) {
                        currentWaterMark.measure(0, 0);
                        // 水印中心点 和 当前压按点的距离
                        final float newDist = (float) CalcUtil.spacing(currentMarkCenterX, currentMarkCenterY, moveX, moveY);
                        if (newDist != 0) {
                            currentWaterMark.setScale(newDist);
                        }
                        post(new Runnable() {
                            @Override
                            public void run() {
                                float diffAngle = CalcUtil.degree(currentWaterMark.getWidth(), currentWaterMark.getHeight());
                                currentAngle = CalcUtil.angleBetweenPoints(moveX, moveY, currentMarkCenterX, currentMarkCenterY) - diffAngle;
                                Log.d("test---------->", "angle = " + diffAngle + "; currentAngle = " + currentAngle);
                                currentWaterMark.setRotation(currentAngle);
                                currentWaterMark.setX(currentMarkCenterX - (currentWaterMark.getWidth() / 2));
                                currentWaterMark.setY(currentMarkCenterY - (currentWaterMark.getHeight() / 2));
                            }
                        });
                    } else {
                        currentWaterMark.setX(currentWaterMark.getX() + (moveX - this.moveX));
                        currentWaterMark.setY(currentWaterMark.getY() + (moveY - this.moveY));
                    }
                    this.moveX = event.getX();
                    this.moveY = event.getY();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                LOG.showE("普通的触摸的点击");
                firstTouchX = event.getX();
                firstTouchY = event.getY();
                this.moveX = firstTouchX;
                this.moveY = firstTouchY;
                if (currentWaterMark != null) {
                    currentMarkCenterX = currentWaterMark.getX() + currentWaterMark.getWidth() / 2;
                    currentMarkCenterY = currentWaterMark.getY() + currentWaterMark.getHeight() / 2;
                    currentWaterMark.setBaseSize((float) CalcUtil.spacing(currentMarkCenterX, currentMarkCenterY, moveX, moveY));
                }
                break;
            case MotionEvent.ACTION_UP:
                lastAngle = currentAngle;

                // 因为抬起来时如果和按下去的触点不同，不会受到抬起事件，所以需要最大范围的触屏监听中赋值
                touchDown = false;
                break;
        }
        return true;
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

    public boolean hasMark() {
        return waterMarkList.size() != 0 || scrawlBoardView.pathCount() > 0;
    }

    public void setSaveMode(boolean isSaveMode) {
        for (WaterMark waterMark : waterMarkList) {
            waterMark.setControlBtnVisible(!isSaveMode);
        }
    }
}
