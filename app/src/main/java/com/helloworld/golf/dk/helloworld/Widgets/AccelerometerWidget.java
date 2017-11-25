package com.helloworld.golf.dk.helloworld.Widgets;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.helloworld.golf.dk.helloworld.Aggregators.MovementAggregator;
import com.helloworld.golf.dk.helloworld.Models.Acceleration;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class AccelerometerWidget implements SensorEventListener {
    SensorManager manager;
    Sensor accelerometer;
    Activity activity;

    public boolean ReadyForSave = false;
    private int windowSize = 128; // Stated by the task
    private static CopyOnWriteArrayList<Acceleration> accelerations = new CopyOnWriteArrayList<>();

    private Acceleration gravity;

    public AccelerometerWidget(Activity activity) {
        this.activity = activity;
        gravity = new Acceleration(0.0f, 0.0f, 0.0f);
        manager = (SensorManager) this.activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);

    }

    public void startSensors( ) {
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stopSensors( ) {
        manager.unregisterListener(this);
    }

    public void reset() {
        MovementAggregator.getInstance().resetResults();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // https://stackoverflow.com/questions/20935587/how-to-properly-calculate-linear-acceleration-using-accelerometer-in-android
            final float alpha = 0.8f;
            Acceleration acceleration = new Acceleration(0.0f, 0.0f, 0.0f);

            // A low pass filter is used to retrieve the gravity values
            gravity.setX(alpha * gravity.getX() + (1 - alpha) * event.values[0]);
            gravity.setY(alpha * gravity.getY() + (1 - alpha) * event.values[1]);
            gravity.setZ(alpha * gravity.getZ() + (1 - alpha) * event.values[2]);

            acceleration.setX(event.values[0] - gravity.getX());
            acceleration.setY(event.values[1] - gravity.getY());
            acceleration.setZ(event.values[2] - gravity.getZ());

            // The results of this samples are stored into the history object
            accelerations.add(acceleration);

            if(windowSize < accelerations.size() + 1){
                final ArrayList<Acceleration> dataToWrite = new ArrayList<>();

                for(int i = 0; i < windowSize; i++) {
                    //Appending history to windowSize
                    dataToWrite.add(accelerations.get(i));
                }

                // Initialize a new thread to perform the heavy computation on data..
                Thread processThread = new Thread() {

                    @Override
                    public void run() {

                        // Min, max and standard deviation of the current window are
                        // calculated and stored
                        MovementAggregator.getInstance().processData(dataToWrite);
                    }
                };

                // The overlap is defined to be 50%, thereby we discard the first half of the window:

                for(int u = windowSize / 2; u > 0; u--) {

                    accelerations.remove(0);
                }

                processThread.start();
                ReadyForSave = true;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
