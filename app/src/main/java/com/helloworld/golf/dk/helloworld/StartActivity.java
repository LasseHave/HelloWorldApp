package com.helloworld.golf.dk.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.helloworld.golf.dk.helloworld.Aggregators.AccelerationAggregator;
import com.helloworld.golf.dk.helloworld.Interpreters.MovementInterpreter;
import com.helloworld.golf.dk.helloworld.Models.StatisticsData;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends AppCompatActivity {
    private MovementInterpreter movementInterpreter;
    private TextView activityLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

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

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        updateActivity();
    }

    public void updateActivity() {
        StatisticsData results = AccelerationAggregator.getInstance().getResults().get(0);

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                List<StatisticsData> results = AccelerationAggregator.getInstance().getResults();
                StatisticsData result = results.get(results.size() - 1);

                try {
                    activityLabel.setText(movementInterpreter.classify(result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,1000);
    }
}
