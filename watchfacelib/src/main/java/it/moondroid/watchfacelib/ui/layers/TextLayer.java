package it.moondroid.watchfacelib.ui.layers;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import it.moondroid.watchfacelib.ui.WatchFace;
import it.moondroid.watchfacelib.ui.WatchFace.WatchState;


public class TextLayer extends BasicLayer {
    private int mActiveColor;
    private float mAnchorX;
    private float mAnchorY;
    private boolean mBaseLineAnchor;
    private int mDimmedColor;
    private final TextPaint mPaint;
    private boolean mRecalculateRect;
    private String mText;
    private final Rect mTextRect;

    public TextLayer() {
        this.mTextRect = new Rect();
        this.mPaint = new TextPaint();
        this.mAnchorX = 0.5f;
        this.mAnchorY = 0.5f;
        this.mBaseLineAnchor = false;
        this.mRecalculateRect = true;
        this.mActiveColor = -1;
        this.mDimmedColor = -1;
        this.mPaint.setFlags(129);
    }

    public float getTextAscent() {
        return this.mPaint.ascent();
    }

    public float getTextDescent() {
        return this.mPaint.descent();
    }

    public float measureText() {
        return this.mPaint.measureText(this.mText);
    }

    public float measureText(String text) {
        return this.mPaint.measureText(text);
    }

    public void setTextColor(int color) {
        this.mDimmedColor = color;
        this.mActiveColor = color;
    }

    public void setBold(boolean isBoldOn) {
        if (isBoldOn) {
            this.mPaint.setFlags(this.mPaint.getFlags() | Paint.FAKE_BOLD_TEXT_FLAG);
        } else {
            this.mPaint.setFlags(this.mPaint.getFlags() & -33);
        }
    }

    public void setActiveColor(int color) {
        this.mActiveColor = color;
    }

    public void setDimmedColor(int color) {
        this.mDimmedColor = color;
    }

    public void setTextSize(float size) {
        if (size != this.mPaint.getTextSize()) {
            this.mPaint.setTextSize(size);
            this.mRecalculateRect = true;
        }
    }

    public void setTypeface(Typeface typeface) {
            this.mPaint.setTypeface(typeface);
            this.mRecalculateRect = true;

    }

    public void setText(String text) {
            this.mText = text;
            this.mRecalculateRect = true;

    }

    public void setTextScaleX(float scale) {
        if (this.mPaint.getTextScaleX() != scale) {
            this.mPaint.setTextScaleX(scale);
            this.mRecalculateRect = true;
        }
    }

    public String getText() {
        return this.mText;
    }

    public void setAnchor(float x, float y) {
        setAnchorX(x);
        setAnchorY(y);
    }

    public void setAnchorX(float x) {
        this.mAnchorX = clamp(0.0f, x, 1.0f);
    }

    public void setAnchorY(float y) {
        this.mAnchorY = clamp(0.0f, y, 1.0f);
        this.mBaseLineAnchor = false;
    }

    public TextPaint getPaint() {
        return new TextPaint(this.mPaint);
    }

    public void setBaseLineAnchor(boolean isOn) {
        this.mBaseLineAnchor = isOn;
    }

    public void onDraw(WatchFace watchFace, Canvas canvas) {
        super.onDraw(watchFace, canvas);
        if (this.mText != null) {
            if (this.mRecalculateRect) {
                onMeasureText(this.mText, this.mTextRect);
                this.mRecalculateRect = false;
            }
            PointF center = watchFace.getWatchFaceCenter();
            float x = center.x - (this.mAnchorX * ((float) this.mTextRect.width()));
            float y = this.mBaseLineAnchor ? center.y : (center.y - (this.mAnchorY * ((float) this.mTextRect.height()))) - this.mPaint.ascent();
            this.mPaint.setColor(watchFace.getWatchState() == WatchState.ON ? this.mActiveColor : this.mDimmedColor);
            canvas.drawText(this.mText, x, y, this.mPaint);
        }
    }

    protected void onMeasureText(String text, Rect rect) {
        rect.set(0, 0, (int) this.mPaint.measureText(text), (int) (this.mPaint.descent() - this.mPaint.ascent()));
    }

    public void setAdjustedTextSize(int initSize, int value, float step_reduce, float watchFaceScale) {
        setText(String.valueOf(value));
        setTextSize(((float) initSize) * watchFaceScale);
        if (value >= 10000) {
            setTextSize(((((((float) initSize) * watchFaceScale) * step_reduce) * step_reduce) * step_reduce) * step_reduce);
        } else if (value >= 1000) {
            setTextSize((((((float) initSize) * watchFaceScale) * step_reduce) * step_reduce) * step_reduce);
        } else if (value >= 100) {
            setTextSize(((((float) initSize) * watchFaceScale) * step_reduce) * step_reduce);
        } else if (value >= 10) {
            setTextSize((((float) initSize) * watchFaceScale) * step_reduce);
        }
    }
}
