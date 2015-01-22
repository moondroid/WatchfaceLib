package it.moondroid.watchfacelib.ui;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
import android.graphics.Canvas;
import it.moondroid.watchfacelib.ui.WatchFace.WatchState;

public interface WatchFaceLayer {
    void init(WatchFace watchFace);

    boolean isVisible(WatchFace watchFace);

    void onDestroy(WatchFace watchFace);

    void onDraw(WatchFace watchFace, Canvas canvas);

    void onWatchStateChanged(WatchFace watchFace, WatchState watchState);
}
