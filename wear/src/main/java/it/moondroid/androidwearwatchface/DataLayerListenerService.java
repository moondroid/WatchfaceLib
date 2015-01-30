package it.moondroid.androidwearwatchface;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by marco.granatiero on 30/01/2015.
 */
public class DataLayerListenerService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DataLayerListenerServic";

    public static final String SEND_MESSAGE_PATH = "/send-message";
    public static final String MESSAGE_SWEEP_SECONDS = "/sweep-seconds";

    GoogleApiClient mGoogleApiClient;
    private static MessageListener listener;

    public interface MessageListener {
        public void onMessageReceived(MessageEvent messageEvent);
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

    public static void setMessageListener(MessageListener messageListener){
        listener = messageListener;
    }
}
