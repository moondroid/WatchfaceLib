package it.moondroid.androidwearwatchface;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

/**
 * Created by marco.granatiero on 30/01/2015.
 */
public class DataLayerListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DataLayerListenerServic";

    public static final String SEND_MESSAGE_PATH = "/send-message";
    public static final String MESSAGE_SWEEP_SECONDS = "/sweep-seconds";

    private static final String IMAGE_PATH = "/image";
    private static final String KEY_BACKGROUND = "BACKGROUND";
    private static final String KEY_HAND_HOURS = "HAND_HOURS";
    private static final String KEY_HAND_MINUTES = "HAND_MINUTES";
    private static final String KEY_HAND_SECONDS = "HAND_SECONDS";

    private static final int TIMEOUT_MS = 5000;

    GoogleApiClient mGoogleApiClient;
    private static MessageListener listener;

    public interface MessageListener {
        public void onMessageReceived(MessageEvent messageEvent);
        public void onDrawableReceived(DrawableType type, Drawable drawable);
    }

    public enum DrawableType {
        NONE,
        BACKGROUND,
        HAND_HOURS,
        HAND_MINUTES,
        HAND_SECONDS
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDestroy() {
        if(mGoogleApiClient != null){
            if(mGoogleApiClient.isConnected()){
                mGoogleApiClient.disconnect();
                Log.v(TAG, "Google API client disconnected");
            }
        }

        super.onDestroy();
    }

    @Override //GoogleApiClient.ConnectionCallbacks
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected(): Successfully connected to Google API client");
        //Wearable.DataApi.addListener(mGoogleApiClient, this);
        //Wearable.MessageApi.addListener(mGoogleApiClient, this);
        //Wearable.NodeApi.addListener(mGoogleApiClient, this);
    }

    @Override //GoogleApiClient.ConnectionCallbacks
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended(): Connection to Google API client was suspended");
    }

    @Override //GoogleApiClient.OnConnectionFailedListener
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed(): Failed to connect, with result: " + connectionResult);
    }


    @Override
    public void onPeerConnected(Node peer) {
        Log.d(TAG, "onPeerConnected: " + peer);
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "onPeerDisconnected: " + peer);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);

        if(listener != null){
            listener.onMessageReceived(messageEvent);
        }

        if (messageEvent.getPath().equals(SEND_MESSAGE_PATH)) {
            //TODO
            Toast.makeText(this, "Message Received", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals(IMAGE_PATH)) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());

                Asset profileAsset = null;
                DrawableType drawableType = DrawableType.NONE;

                if (dataMapItem.getDataMap().containsKey(KEY_BACKGROUND)) {
                    profileAsset = dataMapItem.getDataMap().getAsset(KEY_BACKGROUND);
                    drawableType = DrawableType.BACKGROUND;
                }
                if (dataMapItem.getDataMap().containsKey(KEY_HAND_HOURS)) {
                    profileAsset = dataMapItem.getDataMap().getAsset(KEY_HAND_HOURS);
                    drawableType = DrawableType.HAND_HOURS;
                }
                if (dataMapItem.getDataMap().containsKey(KEY_HAND_MINUTES)) {
                    profileAsset = dataMapItem.getDataMap().getAsset(KEY_HAND_MINUTES);
                    drawableType = DrawableType.HAND_MINUTES;
                }
                if (dataMapItem.getDataMap().containsKey(KEY_HAND_SECONDS)) {
                    profileAsset = dataMapItem.getDataMap().getAsset(KEY_HAND_SECONDS);
                    drawableType = DrawableType.HAND_SECONDS;
                }

                if (listener != null) {
                    Bitmap bitmap = loadBitmapFromAsset(profileAsset);
                    Drawable d = new BitmapDrawable(getResources(), bitmap);
                    listener.onDrawableReceived(drawableType, d);
                }


            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    public static void setListener(MessageListener listener){
        DataLayerListenerService.listener = listener;
    }

}
