package it.moondroid.watchfacelib.ui.layers;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import it.moondroid.watchfacelib.ui.WatchFace;
import it.moondroid.watchfacelib.ui.WatchFace.WatchState;

public class DrawableLayer extends BasicLayer {
    private Drawable mActiveDrawable;
    private Drawable mDimmedDrawable;

    public void setActiveDrawable(Drawable drawable) {
        this.mActiveDrawable = drawable;
    }

    public void setDimmedDrawable(Drawable drawable) {
        this.mDimmedDrawable = drawable;
    }

    public void setDrawable(Drawable drawable) {
        setActiveDrawable(drawable);
        setDimmedDrawable(drawable);
    }

    public Drawable getActiveDrawable() {
        return this.mActiveDrawable;
    }

    public void onDraw(WatchFace watchFace, Canvas canvas) {
        super.onDraw(watchFace, canvas);

        Drawable drawable;
        if (watchFace.getWatchState() == WatchState.ON) {
            drawable = this.mActiveDrawable;
        } else {
            drawable = this.mDimmedDrawable;
        }
        if (drawable != null) {
            float drawableCenterX = ((float) drawable.getIntrinsicWidth()) / 2.0f;
            float drawableCenterY = ((float) drawable.getIntrinsicHeight()) / 2.0f;
            float drawingCenterX = watchFace.getWatchFaceWidth() / 2.0f;
            float drawingCenterY = watchFace.getWatchFaceHeight() / 2.0f;
            drawable.setBounds((int) (drawingCenterX - drawableCenterX), (int) (drawingCenterY - drawableCenterY), (int) (drawingCenterX + drawableCenterX), (int) (drawingCenterY + drawableCenterY));
            drawable.draw(canvas);
        }
    }
}
