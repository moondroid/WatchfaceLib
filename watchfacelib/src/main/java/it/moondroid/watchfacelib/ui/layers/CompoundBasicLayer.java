package it.moondroid.watchfacelib.ui.layers;

/**
 * Created by marco.granatiero on 22/01/2015.
 */
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Arrays;

import it.moondroid.watchfacelib.ui.WatchFace;
import it.moondroid.watchfacelib.ui.WatchFace.WatchState;
import it.moondroid.watchfacelib.ui.WatchFaceLayer;

public class CompoundBasicLayer extends BasicLayer {

    private ArrayList<WatchFaceLayer> mLayers;


    public CompoundBasicLayer() {
        this.mLayers = new ArrayList<WatchFaceLayer>();
    }

    public void setLayers(WatchFaceLayer... layers) {
        this.mLayers = new ArrayList<WatchFaceLayer>(Arrays.asList(layers));
    }

    public void addLayer(WatchFaceLayer layer) {
        if (layer != null) {
            mLayers.add(layer);
        }
    }

    public void init(WatchFace watchFace) {
        super.init(watchFace);

        for(WatchFaceLayer watchFaceLayer : mLayers){
            watchFaceLayer.init(watchFace);
        }
    }

    public void onDestroy(WatchFace watchFace) {
        super.onDestroy(watchFace);

        for(WatchFaceLayer watchFaceLayer : mLayers){
            watchFaceLayer.onDestroy(watchFace);
        }
    }

    public void onDraw(WatchFace watchFace, Canvas canvas) {
        super.onDraw(watchFace, canvas);

        for(WatchFaceLayer watchFaceLayer : mLayers){
            if (watchFaceLayer.isVisible(watchFace)) {
                int saveCount = canvas.save();
                watchFaceLayer.onDraw(watchFace, canvas);
                canvas.restoreToCount(saveCount);
            }
        }

    }

    public void onWatchStateChanged(WatchFace watchFace, WatchState state) {
        super.onWatchStateChanged(watchFace, state);

        for(WatchFaceLayer watchFaceLayer : mLayers){
            watchFaceLayer.onWatchStateChanged(watchFace, state);
        }
    }
}