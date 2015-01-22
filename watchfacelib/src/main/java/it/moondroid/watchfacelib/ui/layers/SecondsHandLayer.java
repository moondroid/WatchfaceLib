package it.moondroid.watchfacelib.ui.layers;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import it.moondroid.watchfacelib.ui.WatchFace;
import it.moondroid.watchfacelib.ui.WatchFace.WatchState;
import java.util.Calendar;

public class SecondsHandLayer extends BasicLayer {
    private Drawable mActiveDrawable;
    private Drawable mDimmedDrawable;
    private boolean mStopAt12;
    private boolean mSweepSeconds;

    public SecondsHandLayer() {
        this.mSweepSeconds = true;
        this.mStopAt12 = false;
    }

    public void setActiveDrawable(Drawable activeDrawable) {
        this.mActiveDrawable = activeDrawable;
    }

    public void setDimmedDrawable(Drawable dimmedDrawable) {
        this.mDimmedDrawable = dimmedDrawable;
    }

    public void setDrawable(Drawable drawable) {
        setActiveDrawable(drawable);
        setDimmedDrawable(drawable);
    }

    public void setSweepSeconds(boolean sweep) {
        this.mSweepSeconds = sweep;
    }

    public void setStopAt12(boolean stop) {
        this.mStopAt12 = stop;
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
            float degree = (!this.mStopAt12 || watchFace.getWatchState() == WatchState.ON) ? (getCurrentSeconds(watchFace) / 60.0f) * 360.0f : 0.0f;
            PointF watchCenter = watchFace.getWatchFaceCenter();
            float handCenterX = ((float) drawable.getIntrinsicWidth()) / 2.0f;
            float handCenterY = ((float) drawable.getIntrinsicHeight()) / 2.0f;
            canvas.save();
            canvas.rotate(degree, watchCenter.x, watchCenter.y);
            drawable.setBounds((int) (watchCenter.x - handCenterX), (int) (watchCenter.y - handCenterY), (int) (watchCenter.x + handCenterX), (int) (watchCenter.y + handCenterY));
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    private float getCurrentSeconds(WatchFace watchFace) {
        Calendar calendar = watchFace.getCalendar();
        return this.mSweepSeconds ?
                ((float) calendar.get(Calendar.SECOND)) + (((float) calendar.get(Calendar.MILLISECOND)) / 1000.0f)
                : (float) calendar.get(Calendar.SECOND);
    }
}
