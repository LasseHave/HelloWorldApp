package com.helloworld.golf.dk.helloworld.Widgets;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.helloworld.golf.dk.helloworld.Models.LocationObj;


public class GPSWidget extends Service implements LocationListener {
    private static final String TAG = "MyLocationService";
    private Location mLastLocation;
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 500;
    private static final float LOCATION_DISTANCE = 1f;
    private double speed;
    private Activity activity;

    public GPSWidget(Activity activity) {
        this.activity = activity;
        // Here, thisActivity is the current activity

        onCreate();
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");

        initializeLocationManager();

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    0,
                    this
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: "+ LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) this.activity.getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void broadcastSpeedChanged() {
        Intent intent = new Intent("speedUpdate");
        intent.putExtra("speed", Double.toString(this.speed));
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            LocationObj myLocation = new LocationObj(location);
            this.updateSpeed(myLocation);
            broadcastSpeedChanged();
        }
    }

    private void updateSpeed(LocationObj myLocation) {
        this.speed = myLocation.getSpeed();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

}