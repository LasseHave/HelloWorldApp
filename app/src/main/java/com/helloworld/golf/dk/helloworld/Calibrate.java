package com.helloworld.golf.dk.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.helloworld.golf.dk.helloworld.Aggregators.FileAggregator;
import com.helloworld.golf.dk.helloworld.Widgets.AccelerometerWidget;

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
        Button stop = (Button) findViewById(R.id.activity_calibrate_stop_button);
        dropdown = (Spinner)findViewById(R.id.calibrate_dropdown);
        items = new String[]{"walking", "running", "standing", "biking"};
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

        stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopClick();
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
        senAccelerometer.reset();

    }

    private void startClick() {
        fileAggregator.createArff(items[dropdown.getSelectedItemPosition()], this);
        senAccelerometer.startSensors();

    }

    private void stopClick() {
        senAccelerometer.stopSensors();
        senAccelerometer.reset();
        Toast.makeText(this,
                "Sensors stopped and reset",
                Toast.LENGTH_SHORT).show();
    }
}
