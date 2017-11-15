/*package com.helloworld.golf.dk.helloworld;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AccelerometerWidget implements SensorEventListener {
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private String x;
    private String y;
    private String z;

    public AccelerometerWidget() {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initAccelerometer() {
        
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x.setText(Float.toString(event.values[0]));
            y.setText(Float.toString(event.values[1]));
            z.setText(Float.toString(event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void unregister(AppCompatActivity activity) {
        senSensorManager.unregisterListener(activity);
    }

    public void register(AppCompatActivity activity) {
        senSensorManager.registerListener(activity, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
*/