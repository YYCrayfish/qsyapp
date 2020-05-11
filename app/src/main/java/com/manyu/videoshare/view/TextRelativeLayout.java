package com.manyu.videoshare.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.manyu.videoshare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明:
 * 这是个自定义布局View
 * Created by cretin on 15/12/17.
 */
public class TextRelativeLayout extends RelativeLayout {
    private Context context;
    private StrokeText textView;
    private TextViewParams tvParams;
    private ImageView imageView;

    private boolean flag = false;
    private boolean mflag = false;
    private boolean onefinger;
    private boolean tvOneFinger;

    //记录是否为TextView上的单击事件
    private boolean isClick = true;

    public static final int DEFAULT_TEXTSIZE = 20;

    //左边点的偏移量
    float tv_width;
    float tv_height;
    float mTv_width;
    float mTv_height;
    float mIv_width;
    float mIv_height;
    float tv_widths;
    float tv_heights;
    float mTv_widths;
    float mTv_heights;

    //用于保存创建的TextView
    private List<StrokeText> list;
    private List<TextViewParams> listTvParams;
    private List<Double> listDistance;

    private float oldDist = 0;
    private float textSize = 15;
    private int num = 0;

    private int width;
    private int height;
    private float startX;
    private float startY;

    private static final int INVALID_POINTER_ID = -1;
    private float fX, fY, sX, sY;
    private float mfX, mfY, msX, msY;
    private int ptrID1, ptrID2;
    private int mptrID1, mptrID2;
    private float mAngle;
    private float scale = 0;
    private MotionEvent mEvent;

    //记录第一个手指下落时的位置
    private float firstX;
    private float firstY;

    private float defaultAngle;

    //记录当前点击坐标
    private float currentX;
    private float currentY;

    //记录当前设备的缩放倍数
    private double scaleTimes = 1;

    private int shadowColor = 0;

    private boolean isShowRect = false;
    private boolean isClickTv = false;
    private boolean isClickIv = false;
    private Paint mRectPaint;
    private float rectX;
    private float rectY;

    private Rect rect;

    public MyRelativeTouchCallBack getMyRelativeTouchCallBack() {
        return myRelativeTouchCallBack;
    }

    public void setMyRelativeTouchCallBack(MyRelativeTouchCallBack myRelativeTouchCallBack) {
        this.myRelativeTouchCallBack = myRelativeTouchCallBack;
    }


    //接口
    private MyRelativeTouchCallBack myRelativeTouchCallBack;

    /**
     * 处理View上的单击事件 用以添加TextView
     */

    public TextRelativeLayout(Context context) {
        this(context, null, 0);
    }

    public TextRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextRelativeLayout(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;

        testScaleTimes();

        init();
    }

    //计算缩放倍数
    private void testScaleTimes() {
        TextView tv = new TextView(context);
        tv.setTextSize(1);
        scaleTimes = tv.getTextSize();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isShowRect) {
            canvas.save();
            canvas.rotate(mAngle, textView.getX() + textView.getMeasuredWidth() / 2, textView.getY() + textView.getMeasuredHeight() / 2);
            textView.measure(0, 0);
            rect.set((int) textView.getX(), (int) textView.getY(), (int) textView.getX() + textView.getMeasuredWidth() + 5, (int) textView.getY() + textView.getMeasuredHeight() + 5);
            canvas.drawRect(rect, mRectPaint);
            if (imageView == null) {
                imageView = new ImageView(context);
                imageView.setOnTouchListener(new OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent event) {
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_DOWN:
                                mIv_width = event.getX();
                                mIv_height = event.getY();
                                isClickIv = true;
                                break;
                        }
                        return false;
                    }
                });
                imageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_pause));
                imageView.setLayoutParams(new LayoutParams(60, 60));
                universallySetImageXY();
                addView(imageView);
            } else {
//                if (isClickTv) {
                universallySetImageXY();
//                }
                imageView.setVisibility(VISIBLE);

            }
            canvas.restore();
        } else {
            if (imageView != null) {
                imageView.setVisibility(GONE);
            }
        }
    }

    private void universallySetImageXY(){
        Log.e("xushiyong"," 左上右下:"+textView.getLeft()+","+textView.getTop()+","+textView.getRight()+","+textView.getBottom()+"   x-y:"+textView.getX()+"-"+textView.getY());
        imageView.setX((int) textView.getX() + textView.getMeasuredWidth() - 20);
        imageView.setY((int) textView.getY() + textView.getMeasuredHeight() - 20);
//        imageView.setX((int) textView.getRight() - 20);
//        imageView.setY((int) textView.getBottom() - 20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mEvent = event;
        if (textSize == 0 && textView != null) {
            textSize = textView.getTextSize();
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                Log.d("HHHH", "ACTION_DOWN");
                //此时有一个手指头落点
                onefinger = true;
                textView.measure(0, 0);
                width = textView.getMeasuredWidth();
                height = textView.getMeasuredHeight();
                //给第一个手指落点记录落点的位置
                firstX = event.getX();
                firstY = event.getY();

                currentX = event.getX();
                currentY = event.getY();
                oldDist = (float) spacing(textView.getX() + textView.getMeasuredWidth() / 2, textView.getY() + textView.getMeasuredWidth() / 2, currentX, currentY);

                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("HHHH", "ACTION_MOVE");
                if (spacing(currentX, currentY, event.getX(), event.getY()) > 5) {
                    if (textView != null) {
                        if (isClickTv) {
                            //平移操作
                            textView.setX(event.getX() - mTv_width);
                            textView.setY(event.getY() - mTv_height);

                            //universallySetImageXY();

                            invalidate();
                        }

                        //旋转和缩放操作
                        if (isClickIv) {
                            //处理旋转模块
//                            imageView.setX(event.getX() - mIv_width);
//                            imageView.setY(event.getY() - mIv_height);
                            //处理缩放模块
//                            imageView.measure(0, 0);
                            textView.measure(0, 0);
                            float newDist = (float) spacing(textView.getX() + textView.getMeasuredWidth() / 2, textView.getY() + textView.getMeasuredWidth() / 2, event.getX(), event.getY());
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

                            mAngle = angleBetweenLines(textView.getX() + textView.getMeasuredWidth() / 2, textView.getY() + textView.getMeasuredWidth() / 2, currentX - mIv_width, currentY - mIv_height, textView.getX() + textView.getMeasuredWidth() / 2, textView.getY() + textView.getMeasuredWidth() / 2, event.getX() - mIv_width, event.getY() - mIv_height) + defaultAngle;
                            textView.setRotation(mAngle);
                            Log.e("aaaaaa", "   " + mAngle);


                            invalidate();
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (spacing(currentX, currentY, event.getX(), event.getY()) < 5 && !isClickIv) {
                    isShowRect = !isShowRect;
                    invalidate();
                }
                isClickTv = false;
                isClickIv = false;
                defaultAngle = mAngle;

                break;
        }
        return true;
    }

    /**
     * 为自定义View设置背景图片 顺便隐藏VerticalSeekBar
     *
     * @param bitmap
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setBackGroundBitmap(Bitmap bitmap) {
        setBackground(new BitmapDrawable(bitmap));
    }

    /**
     * 添加一个TextView到界面上
     */
    public void addTextView(final float x, final float y, String content, int type, String
            colorStr) {
        if (textView == null) {
            textView = new StrokeText(context);
            textView.setSingleLine();
//            textView.setEms(1);
            textView.setTag(System.currentTimeMillis());
            textView.setText(content);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(params);
            textView.setTextSize(15);
            textView.setTextColor(Color.parseColor("#" + colorStr));
            textView.measure(0, 0);
            textView.setX(x - textView.getMeasuredWidth() / 2);
            textView.setY(y - textView.getMeasuredHeight() / 2);

            textView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    textView = (StrokeText) v;
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            isClickTv = true;
                            mTv_width = event.getX();
                            mTv_height = event.getY();
                            Log.e("aaaaaaaaACTION_DOWN", "tvwidth" + width + "   tvheight" + height + "   w" + mTv_width + "   h" + mTv_height);
                            break;
                    }
                    return false;
                }
            });
            isShowRect = true;
            invalidate();
            list.add(textView);
            //保存并添加到list中
            saveTextViewparams(textView);
            addView(textView);
        } else {
            if (!"".equals(content)) {
                textView.setText(content);
            }
            if (type == 0) {//文字
                textView.setTextColor(Color.parseColor("#" + colorStr));
            } else if (type == 1) {//边框
                textView.setOpenStroke(true);
                textView.setStrokeColor(Color.parseColor("#" + colorStr));
            } else if (type == 2) {//底色
                textView.setBackgroundColor(Color.parseColor("#" + colorStr));
            } else if (type == 3) {//阴影
                if (textView.isOpenStroke()) {
                    textView.setShadow(scale, textSize, colorStr);
                    textView.setShadowLayer(0, 0, 0, R.color.tran);
                } else {
                    shadowColor = Color.parseColor("#" + colorStr);
                    textView.setShadowLayer((textSize / 6) * scale, (textSize / 6) * scale, (textSize / 6) * scale, Color.parseColor("#" + colorStr));
                }
            }
        }
    }

    /**
     * 初始化操作
     */
    private void init() {
        ptrID1 = INVALID_POINTER_ID;
        ptrID2 = INVALID_POINTER_ID;
        mptrID1 = INVALID_POINTER_ID;
        mptrID2 = INVALID_POINTER_ID;

        list = new ArrayList<>();
        listTvParams = new ArrayList<>();
        listDistance = new ArrayList<>();
        /**初始化*/mRectPaint = new Paint();
        /**设置画笔颜色*/mRectPaint.setColor(ContextCompat.getColor(context, R.color.white));
        /**设置画笔样式*/mRectPaint.setStyle(Paint.Style.STROKE);
        /**设置画笔粗细*/mRectPaint.setStrokeWidth(2);
        /**使用抗锯齿*/mRectPaint.setAntiAlias(true);
        /**使用防抖动*/mRectPaint.setDither(true);
        /**设置笔触样式-圆*/mRectPaint.setStrokeCap(Paint.Cap.ROUND);
        /**设置结合处为圆弧*/mRectPaint.setStrokeJoin(Paint.Join.ROUND);
        rect = new Rect();
    }

    /**
     * //对状态进行保存操作
     *
     * @param textView
     * @return
     */
    private void saveTextViewparams(TextView textView) {
        if (textView != null) {
            tvParams = new TextViewParams();
            tvParams.setRotation(0);
            tvParams.setTextSize((float) (textView.getTextSize() / scaleTimes));
            tvParams.setX(textView.getX());
            tvParams.setY(textView.getY());
            tvParams.setWidth(textView.getWidth());
            tvParams.setHeight(textView.getHeight());
            tvParams.setContent(textView.getText().toString());
            tvParams.setMidPoint(getViewMidPoint(textView));
            tvParams.setScale(1);
            tvParams.setTag(String.valueOf((long) textView.getTag()));
            tvParams.setRotation(mAngle);
            tvParams.setTextColor(textView.getCurrentTextColor());
            listTvParams.add(tvParams);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    /**
     * 获取中间点
     *
     * @param p1
     * @param p2
     * @return 返回两个点连线的中点
     */
    private Point getMidPiont(Point p1, Point p2) {
        return new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    }

    /**
     * 获取中间点
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private Point getMidPiont(int x1, int y1, int x2, int y2) {
        return new Point((x1 + x2) / 2, (y1 + y2) / 2);
    }

    /**
     * 该方法用于回一个View的终点坐标
     * 如果该View不存在则返回(0,0)
     *
     * @param view
     * @return
     */
    private Point getViewMidPoint(View view) {
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

    /**
     * 该方法用于判断某一个点是否某一个范围中
     *
     * @param width
     * @param height
     * @param startX
     * @param startY
     * @param point
     * @return
     */
    private boolean ifIsOnView(int width, int height, int startX, int startY, Point point) {
        return (point.x < (width + startX) && point.x > startX && point.y < (startY + height) && point.y > startY) ? true : false;
    }

    /**
     * 该方法用于判断某一个点是否在View上
     *
     * @param view
     * @param point
     * @return
     */
    private boolean ifIsOnView(View view, Point point) {
        int w = view.getWidth();
        int h = view.getHeight();
        float x = view.getX();
        float y = view.getY();
        return (point.x < (w + x) && point.x > x && point.y < (y + h) && point.y > y) ? true : false;
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
    private float angleBetweenLines(float fX, float fY, float sX, float sY, float nfX,
                                    float nfY, float nsX, float nsY) {
        float angle1 = (float) Math.atan2((fY - sY), (fX - sX));
        float angle2 = (float) Math.atan2((nfY - nsY), (nfX - nsX));

        float angle = ((float) Math.toDegrees(angle1 - angle2)) % 360;
        if (angle < -180.f) angle += 360.0f;
        if (angle > 180.f) angle -= 360.0f;
        return -angle;
    }

    //缩放实现
    private void zoom(float f) {
        textView.setTextSize(textSize *= f);
        textView.setScale(textSize, f);
        if (!textView.isOpenStroke() && shadowColor != 0) {
            textView.setShadowLayer((textSize / 6) * scale, (textSize / 6) * scale, (textSize / 6) * scale, shadowColor);
        }
    }

    /**
     * 计算两点之间的距离
     *
     * @param event
     * @return 两点之间的距离
     */
    private float spacing(MotionEvent event, float x1, float y1, float y2, float x2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 求两个一直点的距离
     *
     * @param p1
     * @param p2
     * @return
     */
    private double spacing(Point p1, Point p2) {
        double x = p1.x - p2.x;
        double y = p1.y - p2.y;
        return Math.sqrt((x * x + y * y));
    }

    /**
     * 返回两个点之间的距离
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    private double spacing(float x1, float y1, float x2, float y2) {
        double x = x2 - x1;
        double y = y2 - y1;
        return Math.sqrt((x * x + y * y));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    /**
     * Created by cretin on 15/12/21
     * 用于记录每个TextView的状态
     */
    public class TextViewParams {
        private String tag;
        private float textSize;
        private Point midPoint;
        private float rotation;
        private float scale;
        private String content;
        private int width;
        private int height;
        private float x;
        private float y;
        private int textColor;
        private int bgColor;
        private int shadowColor;
        private boolean openStroke;

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        @Override
        public String toString() {
            return "TextViewParams{" +
                    "tag='" + tag + '\'' +
                    ", textSize=" + textSize +
                    ", midPoint=" + midPoint +
                    ", rotation=" + rotation +
                    ", scale=" + scale +
                    ", content='" + content + '\'' +
                    ", width=" + width +
                    ", height=" + height +
                    ", x=" + x +
                    ", y=" + y +
                    ", textColor=" + textColor +
                    '}';
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public float getTextSize() {
            return textSize;
        }

        public void setTextSize(float textSize) {
            this.textSize = textSize;
        }

        public Point getMidPoint() {
            return midPoint;
        }

        public void setMidPoint(Point midPoint) {
            this.midPoint = midPoint;
        }

        public float getRotation() {
            return rotation;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public boolean isOpenStroke() {
            return openStroke;
        }

        public void setOpenStroke(boolean openStroke) {
            this.openStroke = openStroke;
        }

        public int getBgColor() {
            return bgColor;
        }

        public void setBgColor(int bgColor) {
            this.bgColor = bgColor;
        }

        public int getShadowColor() {
            return shadowColor;
        }

        public void setShadowColor(int shadowColor) {
            this.shadowColor = shadowColor;
        }
    }

    public interface MyRelativeTouchCallBack {
        void touchMoveCallBack(int direction);

        void onTextViewMoving(TextView textView);

        void onTextViewMovingDone();
    }


    public double getActionDegrees(float x, float y, float x1, float y1, float x2, float y2) {

        double a = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        double b = Math.sqrt((x - x2) * (x - x2) + (y - y2) * (y - y2));
        double c = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y));
        // 余弦定理
        double cosA = (b * b + c * c - a * a) / (2 * b * c);
        // 返回余弦值为指定数字的角度，Math函数为我们提供的方法
        double arcA = Math.acos(cosA);
        double degree = arcA * 180 / Math.PI;

        // 接下来我们要讨论正负值的关系了，也就是求出是顺时针还是逆时针。
        // 第1、2象限
        if (y1 < y && y2 < y) {
            if (x1 < x && x2 > x) {// 由2象限向1象限滑动
                return degree;
            }
            // 由1象限向2象限滑动
            else if (x1 >= x && x2 <= x) {
                return -degree;
            }
        }
        // 第3、4象限
        if (y1 > y && y2 > y) {
            // 由3象限向4象限滑动
            if (x1 < x && x2 > x) {
                return -degree;
            }
            // 由4象限向3象限滑动
            else if (x1 > x && x2 < x) {
                return degree;
            }

        }
        // 第2、3象限
        if (x1 < x && x2 < x) {
            // 由2象限向3象限滑动
            if (y1 < y && y2 > y) {
                return -degree;
            }
            // 由3象限向2象限滑动
            else if (y1 > y && y2 < y) {
                return degree;
            }
        }
        // 第1、4象限
        if (x1 > x && x2 > x) {
            // 由4向1滑动
            if (y1 > y && y2 < y) {
                return -degree;
            }
            // 由1向4滑动
            else if (y1 < y && y2 > y) {
                return degree;
            }
        }

        // 在特定的象限内
        float tanB = (y1 - y) / (x1 - x);
        float tanC = (y2 - y) / (x2 - x);
        if ((x1 > x && y1 > y && x2 > x && y2 > y && tanB > tanC)// 第一象限
                || (x1 > x && y1 < y && x2 > x && y2 < y && tanB > tanC)// 第四象限
                || (x1 < x && y1 < y && x2 < x && y2 < y && tanB > tanC)// 第三象限
                || (x1 < x && y1 > y && x2 < x && y2 > y && tanB > tanC))// 第二象限
            return -degree;
        return degree;
    }
}
