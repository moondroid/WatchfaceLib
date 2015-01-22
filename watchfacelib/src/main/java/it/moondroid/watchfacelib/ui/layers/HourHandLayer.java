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

public class HourHandLayer extends BasicLayer {
    private Drawable mActiveDrawable;
    private Drawable mDimmedDrawable;
    private long mTimeOffset;

    public HourHandLayer() {
        this.mTimeOffset = 0;
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

    public void onDraw(WatchFace watchFace, Canvas canvas) {
        super.onDraw(watchFace, canvas);

        Drawable drawable;
        if (watchFace.getWatchState() == WatchState.ON) {
            drawable = this.mActiveDrawable;
        } else {
            drawable = this.mDimmedDrawable;
        }
        if (drawable != null) {
            float degree = (getCurrentHours(watchFace) / 12.0f) * 360.0f;
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

    public void setTimeOffset(long offset) {
        this.mTimeOffset = offset % 86400000;
    }

    public long getTimeOffset() {
        return this.mTimeOffset;
    }

    private float getCurrentHours(WatchFace watchFace) {
        Calendar calendar = watchFace.getCalendar();
        return (((float) calendar.get(Calendar.HOUR_OF_DAY)) + (((float) calendar.get(Calendar.MINUTE)) / 60.0f)) + (((float) this.mTimeOffset) / 3600000.0f);
    }
}
