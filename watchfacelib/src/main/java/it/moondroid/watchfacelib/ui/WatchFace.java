package it.moondroid.watchfacelib.ui;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
import android.content.Context;
import android.graphics.PointF;
import java.util.Calendar;

public interface WatchFace {

    static final long DELAY_15_FPS = 66;
    static final long DELAY_30_FPS = 33;

    public enum WatchState {
        ON,
        OFF,
        AMBIENT;

//        static {
//            ON = new co.smartwatchface.library.ui.WatchFace.WatchState("ON", 0);
//            OFF = new co.smartwatchface.library.ui.WatchFace.WatchState("OFF", 1);
//            AMBIENT = new co.smartwatchface.library.ui.WatchFace.WatchState("AMBIENT", 2);
//            ENUM$VALUES = new co.smartwatchface.library.ui.WatchFace.WatchState[]{ON, OFF, AMBIENT};
//        }
    }

    public enum RefreshRate {
        NEVER {
            public long getDelay(long currentTime) {
                return -1;
            }
        },
        ONCE_PER_MINUTE {
            public long getDelay(long currentTime) {
                return -1;
            }
        },
        ONCE_PER_SECOND {
            public long getDelay(long currentTime) {
                return 1000 - (currentTime % 1000);
            }
        },
        FPS_15 {
            public long getDelay(long currentTime) {
                return DELAY_15_FPS;
            }
        },
        FPS_30 {
            public long getDelay(long currentTime) {
                return DELAY_30_FPS;
            }
        };

        abstract public long getDelay(long delay);
    }

    void addLayer(WatchFaceLayer watchFaceLayer);

    Calendar getCalendar();

    Context getContext();

    long getCurrentTime();

    //ScaledDrawable getScaledDrawable();//TODO

    PointF getWatchFaceCenter();

    float getWatchFaceHeight();

    float getWatchFaceWidth();

    WatchState getWatchState();

    void invalidateWatchFace();

    void postInvalidateWatchFace();
}
