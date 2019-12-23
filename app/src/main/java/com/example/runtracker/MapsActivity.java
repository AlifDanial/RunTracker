package com.example.runtracker;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private byte[] img;


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
        captureScreen();
        Runs run = new Runs(0, timeText.getText().toString(), distanceText.getText().toString() + "KM", Date, String.format("%.02f", altitude), img);
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

//    public void captureScreen()
//    {
//        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
//        {
//
//            @Override
//            public void onSnapshotReady(Bitmap snapshot)
//            {
//                    int newHeight = 200;
//                    int newWidth = 300;
//
//                    int width = snapshot.getWidth();
//
//                    int height = snapshot.getHeight();
//
//                    float scaleWidth = ((float) newWidth) / width;
//
//                    float scaleHeight = ((float) newHeight) / height;
//
//                    // create a matrix for the manipulation
//
//                    Matrix matrix = new Matrix();
//
//                    // resize the bit map
//
//                    matrix.postScale(scaleWidth, scaleHeight);
//
//                    // recreate the new Bitmap
//
//                    Bitmap resizedBitmap = Bitmap.createBitmap(snapshot, 0, 0, width, height,
//                            matrix, false);
//
//                ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//                img = bos.toByteArray();
//
//
//            }
//        };
//
//        mMap.snapshot(callback);
//    }

    private void captureScreen() {

            GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
                Bitmap bitmap=null;

                @Override
                public void onSnapshotReady(Bitmap snapshot) {
                    // TODO Auto-generated method stub
                    bitmap = snapshot;
                    try {
                        Bitmap resizedBitmap = Bitmap.createBitmap(snapshot,0,0,300,200);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        img = bos.toByteArray();
                        Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            };

            mMap.snapshot(callback);
        }



}
