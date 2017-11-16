package com.helloworld.golf.dk.helloworld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TextView heading = (TextView) findViewById(R.id.activity_start_heading);
        TextView activity = (TextView) findViewById(R.id.activity_start_current_activity);
        TextView hurry_heading = (TextView) findViewById(R.id.activity_start_hurry_heading);
        TextView hurry = (TextView) findViewById(R.id.activity_start_hurry_value);

        Button stopActivity = (Button) findViewById(R.id.activity_start_activity_stop_btn);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
}
