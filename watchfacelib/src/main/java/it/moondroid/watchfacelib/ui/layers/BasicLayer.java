package it.moondroid.watchfacelib.ui.layers;

/**
 * Created by marco.granatiero on 22/01/2015.
 */

import android.graphics.Canvas;

import it.moondroid.watchfacelib.ui.WatchFace;
import it.moondroid.watchfacelib.ui.WatchFace.WatchState;
import it.moondroid.watchfacelib.ui.WatchFaceLayer;


public class BasicLayer implements WatchFaceLayer {

    public static final BasicLayer EMPTY;
    private float mPositionX;
    private float mPositionY;
    private float mRotation;
    private boolean mVisibleWhenActive;
    private boolean mVisibleWhenDimmed;

    public BasicLayer() {
        this.mVisibleWhenActive = true;
        this.mVisibleWhenDimmed = true;
        this.mPositionX = 0.5f;
        this.mPositionY = 0.5f;
        this.mRotation = 0.0f;
    }

    static {
        EMPTY = new BasicLayer();
    }

    public void init(WatchFace watchFace) {
    }

    public void onDestroy(WatchFace watchFace) {
    }

    public boolean isVisible(WatchFace watchFace) {
        return watchFace.getWatchState() == WatchState.ON ? this.mVisibleWhenActive : this.mVisibleWhenDimmed;
    }

    public void onDraw(WatchFace watchFace, Canvas canvas) {
        canvas.translate(watchFace.getWatchFaceWidth() * (this.mPositionX - 0.5f), watchFace.getWatchFaceHeight() * (this.mPositionY - 0.5f));
        canvas.rotate(this.mRotation, watchFace.getWatchFaceWidth() / 2.0f, watchFace.getWatchFaceHeight() / 2.0f);
    }

    public void onWatchStateChanged(WatchFace watchFace, WatchState state) {
    }

    public final void setPosition(float x, float y) {
        setPositionX(x);
        setPositionY(y);
    }

    public final void setPositionX(float x) {
        this.mPositionX = clamp(0.0f, x, 1.0f);
    }

    public final void setPositionY(float y) {
        this.mPositionY = clamp(0.0f, y, 1.0f);
    }

    public void setRotation(float degrees) {
        this.mRotation = degrees;
    }

    public final void setVisible(boolean visible) {
        setVisibleWhenActive(visible);
        setVisibleWhenDimmed(visible);
    }

    public final void setVisibleWhenActive(boolean visible) {
        this.mVisibleWhenActive = visible;
    }

    public final void setVisibleWhenDimmed(boolean visible) {
        this.mVisibleWhenDimmed = visible;
    }

    protected static final float clamp(float min, float val, float max) {
        return Math.max(min, Math.min(max, val));
    }
}
