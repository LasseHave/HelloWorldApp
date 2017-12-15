package com.tractivity.golf.dk.tractivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tractivity.golf.dk.tractivity.Aggregators.MovementAggregator;
import com.tractivity.golf.dk.tractivity.Interpreters.MovementInterpreter;
import com.tractivity.golf.dk.tractivity.Models.StatisticsData;
import com.tractivity.golf.dk.tractivity.Widgets.AccelerometerWidget;
import com.tractivity.golf.dk.tractivity.Widgets.GPSWidget;
import com.jota.autocompletelocation.AutoCompleteLocation;

import net.steamcrafted.lineartimepicker.dialog.LinearTimePickerDialog;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class StartActivity extends AppCompatActivity implements OnMapReadyCallback, AutoCompleteLocation.AutoCompleteLocationListener, RoutingListener {
    private MovementInterpreter movementInterpreter;
    private TextView activityLabel;
    private TextView hurryLabel;
    private TextView speedLabel;
    private Button setTimBtn;
    private ImageView image;
    private AccelerometerWidget accelerometerWidget;
    private GPSWidget gpsWidget;
    private Boolean accStarted = false;

    private double remainingMinutes;
    private double remainingDistance;
    private Date targetDate;
    private int targetHour = 0;
    private int targetMin = 0;

    private String currentTransportationMethod;
    private LinearTimePickerDialog dialog;

    private List<Polyline> polylines;
    private LatLng start;
    private LatLng end;

    Routing routing;
    MapView mapView;
    GoogleMap map;


    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        polylines = new ArrayList<>();
        remainingMinutes = 0;
        remainingDistance = 0;

        mapView = (MapView) findViewById(R.id.activity_start_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        handler = new Handler();

        AutoCompleteLocation autoCompleteLocation =
                (AutoCompleteLocation) findViewById(R.id.autocomplete_location);
        autoCompleteLocation.setAutoCompleteTextListener(this);

        final int backgroundDark = ResourcesCompat.getColor(getResources(), R.color.background_dark, getTheme());
        final int foregroundDark = ResourcesCompat.getColor(getResources(), R.color.foreground_dark, getTheme());
        final int colorAccent = ResourcesCompat.getColor(getResources(), R.color.colorAccent, getTheme());

        getSupportActionBar().setTitle("Can i make it?");

        dialog = LinearTimePickerDialog.Builder.with(this)
                .setDialogBackgroundColor(foregroundDark)
                .setPickerBackgroundColor(backgroundDark)
                .setLineColor(Color.argb(64, 255, 255, 255))
                .setTextColor(Color.WHITE)
                .setTextBackgroundColor(Color.argb(16, 255, 255, 255))
                .setButtonCallback(new LinearTimePickerDialog.ButtonCallback() {
                    @Override
                    public void onPositive(DialogInterface dialog, int hour, int minutes) {
                        Calendar c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hour);
                        c.set(Calendar.MINUTE, minutes);
                        targetDate = c.getTime();
                        targetHour = hour;
                        targetMin = minutes;
                        updateArrivalText(targetHour, targetMin);
                    }

                    @Override
                    public void onNegative(DialogInterface dialog) {

                    }
                })
                .build();

        activityLabel = (TextView) findViewById(R.id.activity_start_current_activity);
        activityLabel.setText("Standing");
        hurryLabel = (TextView) findViewById(R.id.activity_start_hurry_value);
        hurryLabel.setText("");
        speedLabel = (TextView) findViewById(R.id.activity_start_speed);

        image = findViewById(R.id.activity_start_image);
        image.setImageResource(R.mipmap.standing);

        setTimBtn = (Button) findViewById(R.id.activity_start_activity_arrival_btn);

        setTimBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.show();
            }

        });

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
        updateActivity();
        subscribeToSpeedUpdateEvents();
    }

    private void subscribeToSpeedUpdateEvents() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("speedUpdate"));
    }

    public void updateActivity() {

        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        List<StatisticsData> results = MovementAggregator.getInstance().getResults();
                        List<Double> speeds = MovementAggregator.getInstance().getSpeeds();
                        if (results.size() > 0) {
                            StatisticsData result = results.get(results.size() - 1);
                            Double speed = speeds.get(speeds.size() - 1);
                            try {
                                String identifiedClass = movementInterpreter.classify(result, speed);
                                currentTransportationMethod = identifiedClass;
                                updateSpeedText(speed);
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

    public void updateClassText(String identifiedClass) {
        if(identifiedClass == "standing"){
            image.setImageResource(R.mipmap.standing);
            activityLabel.setText("Standing");
            return;

        }
        else if (identifiedClass == "walking"){
            image.setImageResource(R.mipmap.walking);
            activityLabel.setText("Walking");
            return;

        }
        else if (identifiedClass == "running"){
            image.setImageResource(R.mipmap.running);
            activityLabel.setText("Running");
            return;
        }
        else if (identifiedClass == "biking"){
            image.setImageResource(R.mipmap.biking);
            activityLabel.setText("Biking");
            return;
        }


    }

    public void updateHurryText(String hurry) {
        hurryLabel.setText(hurry);
    }

    public void updateSpeedText(Double speed) {
        speedLabel.setText(String.format("%.2f",speed) + "\nkm/h");
    }

    public void updateArrivalText(int hour, int minute){
        if(minute < 10){
            if(minute == 0){
                setTimBtn.setText("Arrival Time: " + String.valueOf(hour) + ":" + String.valueOf(minute) + "0");
            }
            else{
                setTimBtn.setText("Arrival Time: " + String.valueOf(hour) + ":0" + String.valueOf(minute));
            }
        }
        else{
            setTimBtn.setText("Arrival Time: " + String.valueOf(hour) + ":" + String.valueOf(minute));
        }
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
            if (!accStarted) {
                accelerometerWidget.startSensors();
                accStarted = true;
            }
            String speed = intent.getStringExtra("speed");
            // updateHurryText(speed);
        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setMapType(1);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);
        mapView.onResume();

    }

    @Override
    public void onTextClear() {

    }

    @Override
    public void onItemSelected(Place selectedPlace) {
        end= selectedPlace.getLatLng();

        map.addMarker(new MarkerOptions().position(end));

        if (gpsWidget.getmLastLocation() == null) {
            Toast.makeText(getApplicationContext(), "No GPS signal", Toast.LENGTH_LONG).show();

            return;
        }
        start = new LatLng(gpsWidget.getmLastLocation().getLatitude(), gpsWidget.getmLastLocation().getLongitude());

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(selectedPlace.getLatLng());
        builder.include(new LatLng(gpsWidget.getmLastLocation().getLatitude(), gpsWidget.getmLastLocation().getLongitude()));

        LatLngBounds bounds = builder.build();

        int padding = 50;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        map.animateCamera(cu);

        routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(start, end)
                .build();
        routing.execute();

    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex)
    {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = map.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            remainingMinutes = route.get(i).getDurationValue();
            remainingDistance = route.get(i).getDistanceValue();

            // Start marker
            MarkerOptions options = new MarkerOptions();
            options.position(start);
            map.addMarker(options);

            // End marker
            options = new MarkerOptions();
            options.position(end);
            map.addMarker(options);

            Timer t = new Timer();
            t.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    try {
                        calculateRemainingTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }, 0, 5000);
        }
    }

    @Override
    public void onRoutingCancelled() {

    }

    public void calculateRemainingTime() throws ParseException {
        Location lastLoc = gpsWidget.getmLastLocation();
        final double lastRecSpeed = lastLoc.getSpeed() * 0.27777; //to m/s
        remainingDistance = remainingDistance - (lastRecSpeed * 5);

        Date lastDate = new Date();
        lastDate.setTime(lastLoc.getTime());
        long difference = targetDate.getTime() - lastDate.getTime();
        long minutes = TimeUnit.MINUTES.convert(difference, TimeUnit.MILLISECONDS);

        final double targetSpeed = remainingDistance / (minutes * 60);

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (targetSpeed > (lastRecSpeed *3.6)){
                        setTimBtn.setBackgroundColor(Color.RED);
                        updateHurryText("Run faster or steal a bike!");
                    }
                    else{
                        setTimBtn.setBackgroundColor(Color.GREEN);
                        updateHurryText("Keep on going");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

