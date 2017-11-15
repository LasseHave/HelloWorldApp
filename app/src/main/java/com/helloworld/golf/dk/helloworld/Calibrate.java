package com.helloworld.golf.dk.helloworld;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.helloworld.golf.dk.helloworld.Aggregators.AccelerationAggregator;
import com.helloworld.golf.dk.helloworld.Aggregators.FileAggregator;
import com.helloworld.golf.dk.helloworld.Models.Acceleration;
import com.helloworld.golf.dk.helloworld.Models.StatisticsData;
import com.helloworld.golf.dk.helloworld.Widgets.AccelerometerWidget;

import java.io.FileOutputStream;
import java.util.List;

public class Calibrate extends AppCompatActivity {
    private AccelerometerWidget senAccelerometer;
    private FileAggregator fileAggregator;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        senAccelerometer = new AccelerometerWidget(this);
        setContentView(R.layout.activity_calibrate);

        Button start = (Button) findViewById(R.id.activity_calibrate_start_btn2);
        Button save = (Button) findViewById(R.id.activity_calibrate_save_btn);
        save.setClickable(senAccelerometer.ReadyForSave);

        senAccelerometer = new AccelerometerWidget(this);
        fileAggregator = new FileAggregator();

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveClick();
            }
        });
    }

    private void saveClick() {
        fileAggregator.saveExternalFile(this);

    }
}
