package com.manyu.videoshare.view.TextWaterMark;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.manyu.videoshare.R;
import com.manyu.videoshare.util.ImageUtil;

public class WaterMark extends RelativeLayout {

    private TextView text;
    private ImageView image;
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
    String currentWaterMarkColor = "-1";
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
    // 缩放限制
    private float maxWidth;
    private float maxHeight;
    private float minWidth;
    private float minHeight;

    // 开始触摸时的对角线长度的一半，作为计算缩放的基准
    private float baseSize;
    // 开始触摸时的字体大小
    private float baseTextSize;
    // 开始触摸时的图片宽度
    private int baseWidth;
    // 开始触摸时的图片高度
    private int baseHeight;

    public WaterMark(Context context) {
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.view_water_mark, null);
        addView(view);

        float density = Resources.getSystem().getDisplayMetrics().density;
        maxWidth = (int) (400 * density + 0.5f);
        maxHeight = (int) (400 * density + 0.5f);
        minWidth = (int) (20 * density + 0.5f);
        minHeight = (int) (20 * density + 0.5f);

        text = findViewById(R.id.waterText);
        image = findViewById(R.id.waterImage);
        resetMarkBg();
        btnControl = findViewById(R.id.waterButtonControl);
        btnDelete = findViewById(R.id.waterButtonDelete);
    }

    public void setImage(String bmPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bmPath, options);
        options.inSampleSize = calculateInSampleSize(options, ((int) maxWidth / 2), ((int) maxHeight / 2));
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(bmPath, options);

        if (image != null) {
            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(bm);
            ViewGroup.LayoutParams lp = image.getLayoutParams();
            lp.width = bm.getWidth();
            lp.height = bm.getHeight();
        }
        if (text != null) {
            text.setVisibility(View.GONE);
        }
    }

    private int calculateInSampleSize(final BitmapFactory.Options options, final int maxWidth, final int maxHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while (height > maxHeight || width > maxWidth) {
            height >>= 1;
            width >>= 1;
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

    public void setText(String content) {
        if (text != null) {
            text.setText(content);
            text.setVisibility(View.VISIBLE);
        }
        if (image != null) {
            image.setVisibility(View.GONE);
        }
    }

    public void setWaterMarkShadowColor(String colorStr) {
        defaultWaterMarkShadowColor = colorStr;
        int color = getIntColor(defaultWaterMarkShadowAlpha, defaultWaterMarkShadowColor);
        text.setShadowLayer(10, 15, 15, color);
    }

    public void setWaterMarkShadowAlpha(int alpha) {
        defaultWaterMarkShadowAlpha = alpha;
        int color = getIntColor(defaultWaterMarkShadowAlpha, defaultWaterMarkShadowColor);
        text.setShadowLayer(10, 15, 15, color);
    }

    /**
     * 设置水印底色颜色
     *
     * @param colorStr
     */
    public void setWaterMarkColor(String colorStr) {
        currentWaterMarkColor = colorStr;
        resetMarkBg();
    }

    /**
     * 设置水印底色透明度
     *
     * @param alpha
     */
    public void setWaterMarkAlpha(int alpha) {
        currentWaterMarkAlpha = alpha;
        resetMarkBg();
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
        btnDelete.setRotation(-rotation);
        btnControl.setRotation(-rotation);
    }

    /**
     * 设置水印文字颜色
     *
     * @param colorStr 格式例如：ffffff 白色  前面不带#号
     */
    public void setWaterMarkTextColor(String colorStr) {
        currentColor = colorStr;
        setTextColorWithAlpha();
    }

    /**
     * 设置水印文字透明度
     *
     * @param alpha
     */
    public void setWaterMarkTextAlpha(int alpha) {
        currentAlpha = alpha;

        // 设置背景透明
        View view = findViewById(R.id.waterText);
        if (view != null)
            view.getBackground().setAlpha(alpha);

        // 设置文字透明
        setTextColorWithAlpha();
    }

    /**
     * 设置边框水印文字颜色
     *
     * @param colorStr 格式例如：ffffff 白色  前面不带#号
     */
    public void setWaterMarkBorderColor(String colorStr) {
        currentBorderColor = colorStr;
        resetMarkBg();
    }

    /**
     * 设置水印边框透明度
     *
     * @param alpha
     */
    public void setWaterMarkBorderAlpha(int alpha) {
        currentBorderAlpha = alpha;
        resetMarkBg();
    }

    /**
     * 设置文字颜色和透明度
     */
    private void setTextColorWithAlpha() {
        if (text == null) {
            return;
        }
        // 解析拼接出一个带透明度的颜色值 PS:因为Color.argb似乎没有效果，只能替换这种方式来更变文字透明度
        text.setTextColor(getIntColor(currentAlpha, currentColor));
    }

    /**
     * 设置缩放
     *
     * @param size
     */
    public void setScale(float size) {
        float scale = size / baseSize;
        if (text != null && text.getVisibility() == View.VISIBLE) {
            float tempTextSize = baseTextSize * (scale);
            float originTextSize = text.getTextSize();
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX, tempTextSize);
            measure(0, 0);
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            if (measuredWidth > maxWidth ||
                    measuredHeight > maxHeight ||
                    measuredWidth < minWidth ||
                    measuredHeight < minHeight) {
                text.setTextSize(TypedValue.COMPLEX_UNIT_PX, originTextSize);
                return;
            }
        }
        if (image != null && image.getVisibility() == View.VISIBLE) {
            ViewGroup.LayoutParams lp = image.getLayoutParams();
            int sW = (int) (baseWidth * scale);
            int sH = (int) (baseHeight * scale);
            if (sW > maxWidth || sH > maxHeight || sW < minWidth || sH < minHeight) {
                return;
            }
            lp.width = sW;
            lp.height = sH;
            image.setLayoutParams(lp);
        }
    }

    /**
     * 设置基准尺寸（后续的缩放以基准尺寸计算）
     *
     * @param baseSize
     */
    public void setBaseSize(float baseSize) {
        this.baseSize = baseSize;
        baseWidth = image.getWidth();
        baseHeight = image.getHeight();
        baseTextSize = text.getTextSize();
    }

    public ImageView getBtnControl() {
        return btnControl;
    }

    public ImageView getBtnDelete() {
        return btnDelete;
    }

    public void setTextSize(float textSize) {
        if (text != null)
            text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
    }

    public float getTextSize() {
        return text != null ? text.getTextSize() : 0;
    }

    public void setWaterMarkId(long id) {
        waterMarkId = id;
    }

    public long getWaterMarkId() {
        return waterMarkId;
    }

    // 隐藏按钮 在生成视频前需要先隐藏掉，不然视频上的水印图片会带有这两个小控制按钮
    public void setControlBtnVisible(boolean visible) {
        int flag = visible ? View.VISIBLE : View.GONE;
        btnControl.setVisibility(flag);
        btnDelete.setVisibility(flag);
    }

    private void resetMarkBg() {
        GradientDrawable borderBg = new GradientDrawable();
        borderBg.setColor(getIntColor(currentWaterMarkAlpha, currentWaterMarkColor));
        borderBg.setStroke(ImageUtil.dp2px(getContext(), 1), getIntColor(currentBorderAlpha, currentBorderColor));
        image.setBackgroundDrawable(borderBg);
        text.setBackgroundDrawable(borderBg);
        invalidate();
    }

    private int getIntColor(int alpha, String color) {
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 255) {
            alpha = 255;
        }
        int colorWithOutAlpha = Integer.parseInt(color, 16);
        if (colorWithOutAlpha < 0) {
            return Color.TRANSPARENT;
        }
        return alpha << 24 | colorWithOutAlpha;
    }
}
