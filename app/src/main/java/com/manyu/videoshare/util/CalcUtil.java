package com.manyu.videoshare.util;

import android.graphics.Point;
import android.view.View;

public class CalcUtil {

    /**
     * 返回两个点之间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double spacing(float x1, float y1, float x2, float y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return Math.sqrt((x * x + y * y));
    }

    /**
     * 计算刚开始触摸的两个点构成的直线和滑动过程中两个点构成直线的角度
     *
     * @param fX  初始点一号x坐标
     * @param fY  初始点一号y坐标
     * @param sX  初始点二号x坐标
     * @param sY  初始点二号y坐标
     * @param nfX 终点一号x坐标
     * @param nfY 终点一号y坐标
     * @param nsX 终点二号x坐标
     * @param nsY 终点二号y坐标
     * @return 构成的角度值
     */
    public static float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX,
                                          float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    public static float degree(float lineSize1, float lineSize2) {
        return (float) (Math.atan(lineSize2 / lineSize1) * 180 / Math.PI);
    }

    public static float angleBetweenPoints(float p1x, float p1y, float p2x, float p2y) {
        return (float) -(Math.atan2(p2x - p1x, p2y - p1y) * 180 / Math.PI + 90);
    }

    /**
     * 该方法用于回一个View的终点坐标
     * 如果该View不存在则返回(0,0)
     *
     * @param view
     * @return
     */
    public static Point getViewMidPoint(View view) {
        Point point = new Point();
        if (view != null) {
            float xx = view.getX();
            float yy = view.getY();
            int center_x = (int) (xx + view.getWidth() / 2);
            int center_y = (int) (yy + view.getHeight() / 2);
            point.set(center_x, center_y);
        } else {
            point.set(0, 0);
        }
        return point;
    }
}
