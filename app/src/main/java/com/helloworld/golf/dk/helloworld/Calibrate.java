package com.helloworld.golf.dk.helloworld;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

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
    private String[] items;
    private Spinner dropdown;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        senAccelerometer = new AccelerometerWidget(this);
        setContentView(R.layout.activity_calibrate);

        Button start = (Button) findViewById(R.id.activity_calibrate_start_btn2);
        Button save = (Button) findViewById(R.id.activity_calibrate_save_btn);
        dropdown = (Spinner)findViewById(R.id.calibrate_dropdown);
        items = new String[]{"walking", "running", "driving"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);



        save.setClickable(senAccelerometer.ReadyForSave);

        senAccelerometer = new AccelerometerWidget(this);
        fileAggregator = new FileAggregator();

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startClick();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveClick();
            }
        });
    }

    private void saveClick() {
        fileAggregator.writeFile(this);
    }

    private void startClick() {
        fileAggregator.createArff(items[dropdown.getSelectedItemPosition()], this);
        senAccelerometer.startSensors();

    }
}
