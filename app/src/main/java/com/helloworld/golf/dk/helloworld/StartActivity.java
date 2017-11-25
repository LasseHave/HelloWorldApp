package com.helloworld.golf.dk.helloworld;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.helloworld.golf.dk.helloworld.Aggregators.MovementAggregator;
import com.helloworld.golf.dk.helloworld.Interpreters.MovementInterpreter;
import com.helloworld.golf.dk.helloworld.Models.StatisticsData;
import com.helloworld.golf.dk.helloworld.Widgets.AccelerometerWidget;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
    private MovementInterpreter movementInterpreter;
    private TextView activityLabel;
    private AccelerometerWidget accelerometerWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        TextView heading = (TextView) findViewById(R.id.activity_start_heading);
        activityLabel = (TextView) findViewById(R.id.activity_start_current_activity);
        TextView hurry_heading = (TextView) findViewById(R.id.activity_start_hurry_heading);
        TextView hurry = (TextView) findViewById(R.id.activity_start_hurry_value);

        Button stopActivity = (Button) findViewById(R.id.activity_start_activity_stop_btn);

        try {
            movementInterpreter = new MovementInterpreter(this);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        accelerometerWidget = new AccelerometerWidget(this);
        accelerometerWidget.startSensors();

        updateActivity();
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
                                updateText(identifiedClass);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }, 1000, 1000);
    }
    public void updateText(String identifiedClass){
        activityLabel.setText(identifiedClass);
    }
}
