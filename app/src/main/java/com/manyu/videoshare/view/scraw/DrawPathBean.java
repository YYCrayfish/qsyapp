package com.manyu.videoshare.view.scraw;

import android.graphics.Path;

public class DrawPathBean {
    private Path path;
    private int paintColor;
    private float paintStrokeWidth;
    private boolean isEraser;

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getPaintColor() {
        return paintColor;
    }

    public void setPaintColor(int paintColor) {
        this.paintColor = paintColor;
    }

    public boolean isEraser() {
        return isEraser;
    }

    public void setEraser(boolean eraser) {
        isEraser = eraser;
    }

    public float getPaintStrokeWidth() {
        return paintStrokeWidth;
    }

    public void setPaintStrokeWidth(float paintStrokeWidth) {
        this.paintStrokeWidth = paintStrokeWidth;
    }
}