package com.helloworld.golf.dk.helloworld.Widgets;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.helloworld.golf.dk.helloworld.Models.Acceleration;

public class GPSWidget implements SensorEventListener {
    LocationManager locManager;
    LocationListener li;
    Acktivity thisAck;

    public GPSWidget(Activity activity) {
        thisAck = activity;
        locManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        li = new speed();
        locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, li);        
    }

    public void onLocationChanged(Location loc) {
        Float thespeed=loc.getSpeed();
        Toast.makeText(thisAck ,String.valueOf(thespeed), Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderDisabled(String arg0) {}
    @Override
    public void onProviderEnabled(String arg0) {}
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {}

}