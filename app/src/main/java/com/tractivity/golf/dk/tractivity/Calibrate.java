package com.tractivity.golf.dk.tractivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.tractivity.golf.dk.tractivity.Aggregators.FileAggregator;
import com.tractivity.golf.dk.tractivity.Widgets.AccelerometerWidget;

public class Calibrate extends AppCompatActivity {
    private AccelerometerWidget senAccelerometer;
    private FileAggregator fileAggregator;
    private String[] items;
    private Spinner dropdown;

    private Button start;
    private Button stop;
    private Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        senAccelerometer = new AccelerometerWidget(this);
        setContentView(R.layout.activity_calibrate);

        start = (Button) findViewById(R.id.activity_calibrate_start_btn2);
        save = (Button) findViewById(R.id.activity_calibrate_save_btn);
        stop = (Button) findViewById(R.id.activity_calibrate_stop_button);
        dropdown = (Spinner)findViewById(R.id.calibrate_dropdown);
        items = new String[]{"Select a activity", "walking", "running", "standing", "biking"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        dropdown.setSelection(0);

        getSupportActionBar().setTitle("Calibrate");

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
        save.setEnabled(false);
        dropdown.setEnabled(false);


    }

    private void stopClick() {
        senAccelerometer.stopSensors();
        //senAccelerometer.reset();
        Toast.makeText(this,
                "Sensors stopped and reset",
                Toast.LENGTH_SHORT).show();

        save.setEnabled(true);
        dropdown.setEnabled(true);
    }
}
