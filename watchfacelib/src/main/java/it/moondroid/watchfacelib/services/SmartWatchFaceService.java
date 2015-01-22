package it.moondroid.watchfacelib.services;

/**
 * Created by marco.granatiero on 22/01/2015.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.Time;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import it.moondroid.watchfacelib.ui.WatchFace;
import it.moondroid.watchfacelib.ui.WatchFace.WatchState;
import it.moondroid.watchfacelib.ui.WatchFaceLayer;
import it.moondroid.watchfacelib.ui.layers.CompoundBasicLayer;


abstract public class SmartWatchFaceService extends CanvasWatchFaceService {

    private Engine mEngine;
    private boolean mIsRound;

    @Override
    public void onCreate() {
        super.onCreate();
        this.mEngine = new Engine();
        this.mIsRound = false;
    }

    @Override
    public Engine onCreateEngine() {
        return this.mEngine;
    }

    protected void onWatchStateChanged(WatchState state) {
    }

    protected final WatchFace getWatchFace() {
        return this.mEngine;
    }

    protected void setRefreshRate(WatchFace.RefreshRate refreshRate) {
        this.mEngine.setRefreshRate(refreshRate);
    }

    protected abstract void onApplyWindowInsets(WindowInsets windowInsets);

    public WatchState getWatchState() {
        return this.mEngine.mWatchState;
    }

    public boolean isRound() {
        return this.mIsRound;
    }

    private class Engine extends CanvasWatchFaceService.Engine implements WatchFace {
        static final int MSG_UPDATE_TIME = 0;

        final CompoundBasicLayer mRootLayer;
        float mWidth;
        float mHeight;
        final PointF mWatchCenter;
        WatchState mWatchState;

        boolean mVisible;
        boolean mAmbient;
        Time mTime;
        final Calendar mCalendar;

        RefreshRate mRefreshRate;
        RefreshRate mAmbientRefreshRate;

        /**
         * Handler to update the time once a second in interactive mode.
         */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME,
                                    Engine.this.isInAmbientMode() ? Engine.this.mAmbientRefreshRate.getDelay(System.currentTimeMillis())
                                            : Engine.this.mRefreshRate.getDelay(System.currentTimeMillis()));

                        }
                        break;
                }
            }
        };

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mTime.clear(intent.getStringExtra("time-zone"));
                mTime.setToNow();

                mCalendar.setTimeZone(TimeZone.getTimeZone(intent.getStringExtra("time-zone")));
                mCalendar.setTimeInMillis(System.currentTimeMillis());
            }
        };
        boolean mRegisteredTimeZoneReceiver = false;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        private Engine(){
            super();
            this.mWatchState = WatchState.OFF;
            this.mRootLayer = new CompoundBasicLayer();
            this.mWatchCenter = new PointF();
            this.mCalendar = Calendar.getInstance();

            this.mRefreshRate = RefreshRate.FPS_15;
            this.mAmbientRefreshRate = RefreshRate.FPS_15;

        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SmartWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            mTime = new Time();
            this.mRootLayer.init(this);

        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            this.mRootLayer.onDestroy(this);
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                updateWatchState(this.mAmbient, this.mVisible);

                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            mTime.setToNow();
            mCalendar.setTimeInMillis(System.currentTimeMillis());

            this.mWidth = (float) Math.max(canvas.getWidth(), canvas.getHeight());
            this.mHeight = this.mWidth;
            this.mWatchCenter.set(this.mWidth / 2.0f, this.mHeight / 2.0f);

            canvas.drawColor(Color.BLACK);
            this.mRootLayer.onDraw(this, canvas);

        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            SmartWatchFaceService.this.mIsRound = insets.isRound();
            SmartWatchFaceService.this.onApplyWindowInsets(insets);
            invalidate();

        }

        @Override
        public void addLayer(WatchFaceLayer layer) {
            this.mRootLayer.addLayer(layer);
        }

        @Override
        public WatchState getWatchState() {
            return this.mWatchState;
        }

        @Override
        public Calendar getCalendar() {
            return mCalendar;
        }

        @Override
        public Context getContext() {
            //TODO
            return null;
        }

        @Override
        public long getCurrentTime() {
            //return this.mCalendar.getTimeInMillis();
            return mTime.toMillis(true);
        }

        @Override
        public PointF getWatchFaceCenter() {
            return this.mWatchCenter;
        }

        @Override
        public float getWatchFaceHeight() {
            return this.mHeight;
        }

        @Override
        public float getWatchFaceWidth() {
            return this.mWidth;
        }

        @Override
        public void invalidateWatchFace() {
            invalidate();
        }

        @Override
        public void postInvalidateWatchFace() {
            postInvalidate();
        }

        public void setRefreshRate(RefreshRate refreshRate) {
            this.mRefreshRate = refreshRate;
            this.mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            this.mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        }

        public void setAmbientRefreshRate(RefreshRate refreshRate) {
            this.mAmbientRefreshRate = refreshRate;
            this.mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            this.mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
        }

        private void updateWatchState(boolean isAmbient, boolean isVisible) {
            WatchState newState = isVisible ? isAmbient ? WatchState.AMBIENT : WatchState.ON : WatchState.OFF;
            if (newState != this.mWatchState) {
                this.mWatchState = newState;
                if (this.mWatchState == WatchState.ON) {
                    notifyWatchAwake();
                }
                this.mRootLayer.onWatchStateChanged(this, this.mWatchState);
                SmartWatchFaceService.this.onWatchStateChanged(newState);
            }
        }

        private void notifyWatchAwake() {
            //TODO
//            Wearable.NodeApi.getConnectedNodes(this.mGoogleApiClient).setResultCallback(new ResultCallback<GetConnectedNodesResult>() {
//                public void onResult(GetConnectedNodesResult result) {
//                    for (Node node : result.getNodes()) {
//                        Wearable.MessageApi.sendMessage(SmartWatchEngine.this.mGoogleApiClient, node.getId(), PATH_WATCH_AWAKE, null);
//                    }
//                }
//            });
        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            this.mVisible = visible;
            updateWatchState(this.mAmbient, this.mVisible);
            invalidate();

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mTime.clear(TimeZone.getDefault().getID());
                mTime.setToNow();

                mCalendar.setTimeZone(TimeZone.getDefault());
                mCalendar.setTimeInMillis(System.currentTimeMillis());

            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SmartWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SmartWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }
    }
}
