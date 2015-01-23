package it.moondroid.watchfacelib.ui.drawables;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by marco.granatiero on 23/01/2015.
 */
public class LineDrawable extends BitmapDrawable {

    private static final int WIDTH = 320;
    private static final int HEIGHT = 320;

    private static final float DEFAULT_LINE_LENGTH = 1.0f;
    private static final float DEFAULT_LINE_WIDTH = 4.0f;
    private static final int DEFAULT_LINE_COLOR = Color.WHITE;

    protected Paint paint;
    private int color = DEFAULT_LINE_COLOR;
    private float length = DEFAULT_LINE_LENGTH;
    private float width = DEFAULT_LINE_WIDTH;

    private LineDrawable() {
    }

    public LineDrawable(Builder builder){

        this.length = builder.length;
        this.width = builder.width;
        this.color = builder.color;

        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
    }

    @Override
    public int getIntrinsicWidth() {
        return WIDTH;
    }

    @Override
    public int getIntrinsicHeight() {
        return HEIGHT;
    }

    @Override
    public void draw(Canvas canvas) {
        int centerX = (int) (canvas.getWidth() / 2.0f);
        int centerY = (int) (canvas.getHeight() / 2.0f);
        canvas.drawLine(centerX, centerY, centerX, (1.0f - length) * centerY, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }


    private static final float clamp(float min, float val, float max) {
        return Math.max(min, Math.min(max, val));
    }


    public static class Builder {
        private int color = DEFAULT_LINE_COLOR;
        private float length = DEFAULT_LINE_LENGTH;
        private float width = DEFAULT_LINE_WIDTH;

        public Builder length(float length){
            this.length = clamp(0.0f, length, 1.0f);
            return this;
        }

        public Builder width(float width){
            this.width = width;
            return this;
        }

        public Builder color(int color){
            this.color = color;
            return this;
        }

        public LineDrawable build() {
            return new LineDrawable(this);
        }
    }
}
