package com.helloworld.golf.dk.helloworld;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.helloworld.golf.dk.helloworld.Aggregators.MovementAggregator;
import com.helloworld.golf.dk.helloworld.Interpreters.MovementInterpreter;
import com.helloworld.golf.dk.helloworld.Models.StatisticsData;
import com.helloworld.golf.dk.helloworld.Widgets.AccelerometerWidget;
import com.helloworld.golf.dk.helloworld.Widgets.GPSWidget;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
    private MovementInterpreter movementInterpreter;
    private TextView activityLabel;
    private TextView hurryLabel;
    private AccelerometerWidget accelerometerWidget;
    private GPSWidget gpsWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TextView heading = (TextView) findViewById(R.id.activity_start_heading);
        activityLabel = (TextView) findViewById(R.id.activity_start_current_activity);
        TextView hurry_heading = (TextView) findViewById(R.id.activity_start_hurry_heading);
        hurryLabel = (TextView) findViewById(R.id.activity_start_hurry_value);

        Button stopActivity = (Button) findViewById(R.id.activity_start_activity_stop_btn);

        try {
            movementInterpreter = new MovementInterpreter(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        askUserForPermissionToUseGps();
    }

    private void startSpeedSensing() {
        gpsWidget = new GPSWidget(this);
        accelerometerWidget = new AccelerometerWidget(this);
        accelerometerWidget.startSensors();
        updateActivity();
        subscribeToSpeedUpdateEvents();
    }

    private void subscribeToSpeedUpdateEvents() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("speedUpdate"));
    }

    public void updateActivity() {
        final Handler handler = new Handler();

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<StatisticsData> results = MovementAggregator.getInstance().getResults();
                        if (results.size() > 0) {
                            StatisticsData result = results.get(results.size() - 1);
                            try {
                                String identifiedClass = movementInterpreter.classify(result);
                                updateClassText(identifiedClass);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }, 1000, 1000);
    }
    public void updateClassText(String identifiedClass){
        activityLabel.setText(identifiedClass);
    }
    public void updateHurryText(String hurry){
        hurryLabel.setText(hurry);
    }

    private void askUserForPermissionToUseGps() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        } else {
            //We have permission!
            startSpeedSensing();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted!!
                    startSpeedSensing();
                } else {
                    //Permission denied :-(
                }
                return;
            }
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String speed = intent.getStringExtra("speed");
            updateHurryText(speed);
        }
    };
}
