package it.moondroid.androidwearwatchface;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.WindowInsets;

import com.google.android.gms.wearable.MessageEvent;

import it.moondroid.watchfacelib.services.SmartWatchFaceService;
import it.moondroid.watchfacelib.ui.ScaledDrawable;
import it.moondroid.watchfacelib.ui.WatchFace;
import it.moondroid.watchfacelib.ui.layers.DrawableLayer;
import it.moondroid.watchfacelib.ui.layers.HourHandLayer;
import it.moondroid.watchfacelib.ui.layers.MinuteHandLayer;
import it.moondroid.watchfacelib.ui.layers.SecondsHandLayer;
import it.moondroid.watchfacelib.ui.layers.TextLayer;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
public class MyWatchService extends SmartWatchFaceService implements DataLayerListenerService.MessageListener {

    private WatchFace mWatchFace;
    private DrawableLayer mBackgroundLayer;
    private ScaledDrawable mScaledDrawable;
    private DrawableLayer mInnerIndicators;
    private HourHandLayer mHourHandLayer;
    private MinuteHandLayer mMinuteHandLayer;
    private SecondsHandLayer mSecondsHandLayer;

    private boolean mIsRound;

    public MyWatchService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        mWatchFace = getWatchFace();
        mScaledDrawable = ScaledDrawable.getInstance(this);

        mBackgroundLayer = new DrawableLayer();
        mBackgroundLayer.setDrawable(mScaledDrawable.getScaledDrawable(R.drawable.bg));
        mWatchFace.addLayer(mBackgroundLayer);

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

        mHourHandLayer = new HourHandLayer();
        mHourHandLayer.setDrawable(mScaledDrawable.getScaledDrawable(R.drawable.hour_hand));
        mWatchFace.addLayer(mHourHandLayer);

        mMinuteHandLayer = new MinuteHandLayer();
        mWatchFace.addLayer(mMinuteHandLayer);

        DrawableLayer middleLayer = new DrawableLayer();
        middleLayer.setDrawable(mScaledDrawable.getScaledDrawable(R.drawable.middle));
        mWatchFace.addLayer(middleLayer);

        mSecondsHandLayer = new SecondsHandLayer();
        mSecondsHandLayer.setDrawable(mScaledDrawable.getScaledDrawable(R.drawable.seconds_hand));
        //secondsHandLayer.setDrawable(new LineDrawable.Builder().length(0.8f).width(3.0f).color(Color.WHITE).build());
        mSecondsHandLayer.setVisibleWhenDimmed(false);
        mSecondsHandLayer.setSweepSeconds(false);
        mWatchFace.addLayer(mSecondsHandLayer);

        setRefreshRate(WatchFace.RefreshRate.ONCE_PER_SECOND);

        updateGraphics();

        DataLayerListenerService.setListener(this);
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

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if(messageEvent.getPath().equals(DataLayerListenerService.SEND_MESSAGE_PATH +
                DataLayerListenerService.MESSAGE_SWEEP_SECONDS)){

            boolean sweepSeconds = messageEvent.getData()[0] == 1? true : false;

            if(sweepSeconds){
                mSecondsHandLayer.setSweepSeconds(true);
                setRefreshRate(WatchFace.RefreshRate.FPS_30);
            }else {
                mSecondsHandLayer.setSweepSeconds(false);
                setRefreshRate(WatchFace.RefreshRate.ONCE_PER_SECOND);
            }
        }
    }

    @Override
    public void onDrawableReceived(DataLayerListenerService.DrawableType type, Drawable drawable) {

        switch (type){
            case BACKGROUND:
                mBackgroundLayer.setDrawable(drawable);
                break;
            case HAND_HOURS:
                mHourHandLayer.setDrawable(drawable);
                break;
            case HAND_MINUTES:
                mMinuteHandLayer.setDrawable(drawable);
                break;
            case HAND_SECONDS:
                mSecondsHandLayer.setDrawable(drawable);
                break;
        }
    }
}
