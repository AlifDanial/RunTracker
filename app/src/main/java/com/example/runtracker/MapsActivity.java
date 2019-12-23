package com.example.runtracker;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.text.format.DateFormat;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    Button runButton;
    Button stopBtn;
    TextView distanceText;
    TextView timeText;
    DBHandler dbHandler;
    SimpleDateFormat date;
    Calendar cal = Calendar.getInstance();

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener inactivelocationListener;
    private LocationListener activelocationListener;
    private Location newLocation;
    boolean clicked = false;
    private long startTime;
    private long stopTime;

    private final long MIN_TIME = 5; //5 seconds
    private final long MIN_DIST = 5; //5 meters
    private double StartLat;
    private double StartLong;
    private double Lat;
    private double Long;
    private double Alt;
    private double StartAlt;
    private double LastAlt;
    private double LastLat;
    private double LastLong;
    private float distance;
    private double altitude;
    private LatLng LastLatLng;
    private Polyline gpsTrack;
    private int seconds = 0;
    private boolean startRun;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dbHandler = new DBHandler(this,null,null, AppContract.DATABASE_VERSION);

        runButton = findViewById(R.id.runButton);
        timeText = findViewById(R.id.timeText);
        runButton.setClickable(false);
        stopBtn = findViewById(R.id.stopBtn);
        stopBtn.setVisibility(View.GONE);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Timer();
        updateLocation();


    }//end onCreate


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.color(Color.RED);
        polylineOptions.width(6);
        gpsTrack = mMap.addPolyline(polylineOptions);

    }

    public void onStartButtonClicked(View v) {
        if (clicked == false) {
            clicked = true;
            locationManager.removeUpdates(inactivelocationListener);
            runButton.setText("Pause");
            runButton.setBackground(getDrawable(R.drawable.button_rounded_gray));
            stopBtn.setVisibility(View.GONE);
            startRun = true;
            startTime = cal.getTimeInMillis();



            //checks if network provider is enabled
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                try{locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, activelocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Lat = location.getLatitude();
                        Long = location.getLongitude();

                        LastLatLng = new LatLng(Lat,Long);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LastLatLng, 18.2f));
                        newLocation = new Location("newLocation");
                        newLocation.setLatitude(Lat);
                        newLocation.setLongitude(Long);
                        updateTrack();
                        Log.d("runTracker newLocation", location.getLatitude() + " " + location.getLongitude());
                        Log.d("runTracker newaltitude", location.getAltitude() + "");
                        Log.d("runTracker - newdistance", distance + "");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d("runTracker", "onStatusChanged: " + provider + " " + status);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d("runTracker", "onProviderEnabled: " + provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d("runTracker", "onProviderDisabled: " + provider);
                    }
                });}
                catch (SecurityException e){
                    Log.d("runTracker", e.toString());
                }
            }

            //checks if gps provider is enabled
            else if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)){
                try{locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, activelocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Lat = location.getLatitude();
                        Long = location.getLongitude();
                        Alt = location.getAltitude();
                        LastLatLng = new LatLng(Lat,Long);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LastLatLng, 18.2f));
                        newLocation = new Location("newLocation");
                        newLocation.setLatitude(Lat);
                        newLocation.setLongitude(Long);
                        updateTrack();
                        Log.d("runTracker newLocation", location.getLatitude() + " " + location.getLongitude());
                        Log.d("runTracker newaltitude", location.getAltitude() + "");
                        Log.d("runTracker - newdistance", distance + "");
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d("runTracker:", "onStatusChanged: " + provider + " " + status);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d("runTracker", "onProviderEnabled: " + provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d("runTracker", "onProviderDisabled: " + provider);
                    }
                });}
                catch (SecurityException e){
                    Log.d("runTracker", e.toString());
                }
            }
        } else if (clicked) {
            pauseTracking();
            clicked = false;
            runButton.setText("Resume");
            runButton.setBackground(getDrawable(R.drawable.button_rounded));
            stopBtn.setVisibility(View.VISIBLE);
            startRun = false;

        }
    }

    public void onStopButtonClicked(View v){
        locationManager.removeUpdates(activelocationListener);
        date = new SimpleDateFormat("dd.MM.yyyy");
        String Date = date.format(new Date());
        stopTime = cal.getTimeInMillis();
        Runs run = new Runs(0, timeText.getText().toString(), distanceText.getText().toString() + "KM", Date, String.format("%.02f", altitude));
        dbHandler.addRun(run);
        startActivity(new Intent(this, MainActivity.class));
    }

    private void updateTrack() {
        List<LatLng> points = gpsTrack.getPoints();
        points.add(LastLatLng);
        gpsTrack.setPoints(points);
        Location oldLocation = new Location("oldLocation");
        oldLocation.setLatitude(LastLat);
        oldLocation.setLongitude(LastLong);
        distance += oldLocation.distanceTo(newLocation)/1000;
        altitude = (Alt + LastAlt);
        Log.d("runTracker - oldLocation", oldLocation.getLatitude() + "" + oldLocation.getLongitude());
        Log.d("runTracker oldaltitude", LastAlt + "");
        LastAlt = Alt;
        LastLat = Lat;
        LastLong = Long;
        updateDistance();

    }

    public void updateDistance(){
        distanceText = findViewById(R.id.distanceText);
        String sText = String.format("%.02f", distance);
        distanceText.setText(sText);
    }

    public void pauseTracking(){
        locationManager.removeUpdates(activelocationListener);
        updateLocation();

    }

    private void Timer(){
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds/3600;
                int minutes = (seconds%3600)/60;
                int secs = seconds%60;

                String time = String.format("%d:%02d:%02d", hours, minutes, secs);

                timeText.setText(time);

                if(startRun){
                    seconds++;
                }

                handler.postDelayed(this, 1000);
            }
        });

    }

    public void updateLocation(){
            //checks if network provider is enabled
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, inactivelocationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            StartLat = location.getLatitude();
                            StartLong = location.getLongitude();
                            StartAlt = location.getAltitude();
                            LatLng latLng = new LatLng(StartLat, StartLong);
                                LastLat = StartLat;
                                LastLong = StartLong;
                                LastAlt = StartAlt;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.2f));
                            runButton.setClickable(true);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d("runTracker", "onStatusChanged: " + provider + " " + status);
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Log.d("runTracker", "onProviderEnabled: " + provider);
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Log.d("runTracker", "onProviderDisabled: " + provider);
                        }
                    });
                } catch (SecurityException e) {
                    Log.d("runTracker", e.toString());
                }
            }

            //checks if gps provider is enabled
            else if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
                try {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, inactivelocationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            StartLat = location.getLatitude();
                            StartLong = location.getLongitude();
                            StartAlt = location.getAltitude();
                            LatLng latLng = new LatLng(StartLat, StartLong);
                                LastLat = StartLat;
                                LastLong = StartLong;
                                LastAlt = StartAlt;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.2f));
                            runButton.setClickable(true);
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {
                            Log.d("runTracker:", "onStatusChanged: " + provider + " " + status);
                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Log.d("runTracker", "onProviderEnabled: " + provider);
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Log.d("runTracker", "onProviderDisabled: " + provider);
                        }
                    });
                } catch (SecurityException e) {
                    Log.d("runTracker", e.toString());
                }
            }
    }//end updateLocation




}
