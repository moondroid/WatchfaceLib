package it.moondroid.androidwearwatchface;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.WindowInsets;

import it.moondroid.watchfacelib.services.SmartWatchFaceService;
import it.moondroid.watchfacelib.ui.ScaledDrawable;
import it.moondroid.watchfacelib.ui.WatchFace;
import it.moondroid.watchfacelib.ui.drawables.LineDrawable;
import it.moondroid.watchfacelib.ui.layers.DrawableLayer;
import it.moondroid.watchfacelib.ui.layers.HourHandLayer;
import it.moondroid.watchfacelib.ui.layers.MinuteHandLayer;
import it.moondroid.watchfacelib.ui.layers.SecondsHandLayer;
import it.moondroid.watchfacelib.ui.layers.TextLayer;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
public class DiversWatchService extends SmartWatchFaceService {

    private WatchFace mWatchFace;
    private ScaledDrawable mScaledDrawable;
    private DrawableLayer mInnerIndicators;
    private MinuteHandLayer mMinuteHandLayer;

    private boolean mIsRound;

    public DiversWatchService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWatchFace = getWatchFace();
        mScaledDrawable = ScaledDrawable.getInstance(this);

        DrawableLayer backgroundLayer = new DrawableLayer();
        backgroundLayer.setDrawable(mScaledDrawable.getScaledDrawable(R.drawable.bg));
        mWatchFace.addLayer(backgroundLayer);

        mInnerIndicators = new DrawableLayer();
        mWatchFace.addLayer(mInnerIndicators);

        TextLayer title = new TextLayer();
        title.setActiveColor(Color.WHITE);
        //title.setTypeface(TypefaceFactory.get(getApplicationContext(), Fonts.HELVETICA_NEUE_BOLD));
        title.setText("WATCH");
        title.setTextSize(13.6f * this.mScaledDrawable.getScale());
        title.setPositionY(0.290625f);
        title.setTextScaleX(1.1f);
        title.setBold(true);
        mWatchFace.addLayer(title);

        HourHandLayer hourHandLayer = new HourHandLayer();
        hourHandLayer.setDrawable(mScaledDrawable.getScaledDrawable(R.drawable.hour_hand));
        mWatchFace.addLayer(hourHandLayer);

        mMinuteHandLayer = new MinuteHandLayer();
        mWatchFace.addLayer(mMinuteHandLayer);

        DrawableLayer middleLayer = new DrawableLayer();
        middleLayer.setDrawable(mScaledDrawable.getScaledDrawable(R.drawable.middle));
        mWatchFace.addLayer(middleLayer);

        SecondsHandLayer secondsHandLayer = new SecondsHandLayer();
        secondsHandLayer.setDrawable(mScaledDrawable.getScaledDrawable(R.drawable.seconds_hand));
        //secondsHandLayer.setDrawable(new LineDrawable.Builder().length(0.8f).width(3.0f).color(Color.WHITE).build());
        secondsHandLayer.setVisibleWhenDimmed(false);
        secondsHandLayer.setSweepSeconds(false);
        mWatchFace.addLayer(secondsHandLayer);

        setRefreshRate(WatchFace.RefreshRate.ONCE_PER_SECOND);

        updateGraphics();

    }

    @Override
    protected void onApplyWindowInsets(WindowInsets windowInsets) {
        mIsRound = windowInsets.isRound();
        updateGraphics();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void updateGraphics() {
        mInnerIndicators.setActiveDrawable(getIndicatorsActive(this.mIsRound));
        mInnerIndicators.setDimmedDrawable(getIndicatorsDimmed(this.mIsRound));
        mMinuteHandLayer.setActiveDrawable(this.mScaledDrawable.getScaledDrawable(R.drawable.minute_hand));
        mMinuteHandLayer.setDimmedDrawable(this.mScaledDrawable.getScaledDrawable(R.drawable.minute_ambient));

    }

    private Drawable getIndicatorsActive(boolean isRound) {
        return this.mScaledDrawable.getScaledDrawable(isRound ? R.drawable.indicators_round : R.drawable.indicators_rect);
    }

    private Drawable getIndicatorsDimmed(boolean isRound) {
        return this.mScaledDrawable.getScaledDrawable(isRound ? R.drawable.indicators_round_ambient : R.drawable.indicators_rect_ambient);
    }

}
