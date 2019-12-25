package com.example.runtracker;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    MyService mapService;
    Button runButton;
    Button stopBtn;
    TextView distanceText;
    TextView timeText;
    DBHandler dbHandler;
    SimpleDateFormat date;
    Calendar cal = Calendar.getInstance();
    boolean isBound = false;

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener inactiveLocationListener;
    private LocationListener activeLocationListener;
    private Location newLocation;
    boolean clicked = false;
    private long startTime;
    private long stopTime;

    private final long MIN_TIME = 1; //5 seconds
    private final long MIN_DIST = 1; //5 meters
    private double StartLat;
    private double StartLong;
    private double Lat;
    private double Long;
    private double Alt;
    private double StartAlt;
    private double LastAlt;
    private double LastLat;
    private double LastLong;
    private float distance = 0;
    private double altitude;
    private LatLng LastLatLng;
    private LatLng latLng;
    private Polyline gpsTrack;
    private int  seconds = 0;
    public static boolean startRun;
    private boolean serviceStatus = false;
    public static byte[] img;
    public float zoom;
    BroadcastReceiver receiver;


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
        configureReceiver();

    }//end onCreate

    private void configureReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.runtracker");
        filter.addAction("android.location.PROVIDERS_CHANGED");
        receiver = new MyReceiver();
        registerReceiver(receiver, filter);
        updateLocation();
    }

    //initialized to bind the activity and the service
    private ServiceConnection myConnection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyLocalBinder binder = (MyService.MyLocalBinder) service;
            mapService = binder.getService();
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };


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

        //initialize class MyService
        Intent intent = new Intent(this, MyService.class);
        if(bindService(intent,myConnection,0)){
            startService(intent);
        }

        if (clicked == false) {
            clicked = true;
            locationManager.removeUpdates(inactiveLocationListener);
            runButton.setText("Pause");
            runButton.setBackground(getDrawable(R.drawable.button_rounded_gray));
            stopBtn.setVisibility(View.GONE);
            startRun = true;
            serviceStatus = true;
            startTime = cal.getTimeInMillis();


            //checks if network provider is enabled
            if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                try{locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DIST, activeLocationListener = new LocationListener() {
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
                try{locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, activeLocationListener = new LocationListener() {
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
    }//end onStartButtonClicked()

    public void onStopButtonClicked(View v){
        if(LastLatLng != null){
            locationManager.removeUpdates(activeLocationListener);
        }
        if(latLng != null){
            locationManager.removeUpdates(inactiveLocationListener);
        }
        updateDatabase();
        Intent intent = new Intent(this, MyService.class);
        stopService(intent);
        unregisterReceiver(receiver);
        startActivity(new Intent(this, MainActivity.class));

    }

    public void exit(){
        if(serviceStatus == true){
            locationManager.removeUpdates(activeLocationListener);
            Intent intent = new Intent(this, MyService.class);
            stopService(intent);
        }
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
        if(LastLatLng != null){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LastLatLng, getZoom()));
        locationManager.removeUpdates(activeLocationListener);
        }
        updateLocation();
    }

    public float getZoom(){
        if(distance < 1.0){
            zoom = 16.2f;
            return zoom;
        }
        else if(distance < 2.0){
            zoom = 15.2f;
            return zoom;
        }
        else if(distance < 4.0) {
            zoom = 14.2f;
            return zoom;
        }
        else if(distance < 5.0){
            zoom = 13.2f;
            return zoom;
        }
        else if(distance > 10.0){
            zoom = 12.2f;
            return zoom;
        }
        else
            return zoom;
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

    public static String getState(){
        if(startRun == true){
            return "Activity is being tracked";
        }
        else{
            return "Activity paused";
        }
    }

    public void updateLocation(){
        //checks if network provider is enabled
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            try {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, inactiveLocationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        StartLat = location.getLatitude();
                        StartLong = location.getLongitude();
                        StartAlt = location.getAltitude();
                        latLng = new LatLng(StartLat, StartLong);
                            LastLat = StartLat;
                            LastLong = StartLong;
                            LastAlt = StartAlt;
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoom()));
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, inactiveLocationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            StartLat = location.getLatitude();
                            StartLong = location.getLongitude();
                            StartAlt = location.getAltitude();
                            latLng = new LatLng(StartLat, StartLong);
                                LastLat = StartLat;
                                LastLong = StartLong;
                                LastAlt = StartAlt;
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoom()));
                            Log.d("zoom value", zoom + "");
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
            else
            Log.d("runTracker", "Location Disabled" );
    }//end updateLocation


    private void updateDatabase() {

        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap=null;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                try {
                    int newHeight = 322;
                    int newWidth = 315;

                    int width = snapshot.getWidth();
                    int height = snapshot.getHeight();

                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;

                    Matrix matrix = new Matrix();

                    matrix.postScale(scaleWidth, scaleHeight);

                    Bitmap resizedBitmap = Bitmap.createBitmap(snapshot, 0, 0, width, height, matrix, false);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    img = bos.toByteArray();

                    date = new SimpleDateFormat("dd.MM.yyyy");
                    String Date = date.format(new Date());
                    Runs run = new Runs(0, timeText.getText().toString(), String.format("%.02f",distance) + "km" , Date, String.format("%.02f", altitude), img);
                    dbHandler.addRun(run);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }//end onSnapshotReady()

        };

        mMap.snapshot(callback);

    }//end updateDatabase()

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(MapsActivity.this)
                .setTitle("End Session")
                .setMessage("Would you like to exit workout?")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        unregisterReceiver(receiver);
                        exit();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();



    }


}
