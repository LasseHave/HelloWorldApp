package com.helloworld.golf.dk.helloworld;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private CameraManager camManager;
    private boolean flashOn = false;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;

    private TextView textView_x;
    private TextView textView_y;
    private TextView textView_z;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (this.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            setupFlashButton();
        }

        initAccelerometer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void initAccelerometer() {
        textView_x = (TextView) findViewById(R.id.activity_main_sensor_x);
        textView_y = (TextView) findViewById(R.id.activity_main_sensor_y);
        textView_z = (TextView) findViewById(R.id.activity_main_sensor_z);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void setupFlashButton() {
        final Button flashBtn = (Button) findViewById(R.id.activity_main_flash_btn);
        camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        flashBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    toggleFlash();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void toggleFlash() throws CameraAccessException {
        flashOn = !flashOn;
        String cameraId = camManager.getCameraIdList()[0]; // Usually front camera is at 0 position.
        camManager.setTorchMode(cameraId, flashOn);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            textView_x.setText(Float.toString(event.values[0]));
            textView_y.setText(Float.toString(event.values[1]));
            textView_z.setText(Float.toString(event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
