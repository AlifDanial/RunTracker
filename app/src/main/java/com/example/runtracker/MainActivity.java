package com.example.runtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.runtracker.provider.MyContentProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static List<Runs> data;
    public int i = 0;
    Button runButton;
    ImageButton runHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        runButton = findViewById(R.id.runButton);
        runHistory = findViewById(R.id.workoutHistoryBtn);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

       runList();
    }//end oncreate

    public void onRunButtonClicked(View v){
        startActivity(new Intent(this, MapsActivity.class));
        runList();
    }

    public void onRunHistoryButtonClicked(View v){
        runList();
        if(i>0){
            startActivity(new Intent(this, RunHistoryActivity.class));
        }
        else{
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("No workouts saved")
                    .setMessage("Would you like to go for a run?")

                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LaunchMapsActivity();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }


    }

    public void lastRun(View v){
        runList();
        if(i>0){
        Runs run = data.get(0);
        Intent intent = new Intent(getApplicationContext(), RunView.class);
        intent.putExtra("id", Integer.toString(run.getRunID()));
        intent.putExtra("duration", run.getRunDuration());
        intent.putExtra("distance", run.getRunDistance());
        intent.putExtra("date", run.getRunDate());
        intent.putExtra("elevation", run.getRunElevation());
        intent.putExtra("map", run.getRunMap());
        startActivity(intent);
        }

        else{
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("No workouts saved")
                    .setMessage("Would you like to go for a run?")

                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            LaunchMapsActivity();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }

    public void runList(){

        String[] ArrayProjection = new String[] {
                AppContract.COLUMN_ID,
                AppContract.COLUMN_DURATION,
                AppContract.COLUMN_DISTANCE,
                AppContract.COLUMN_DATE,
                AppContract.COLUMN_ELEVATION,
                AppContract.COLUMN_MAP

        };

        Cursor cursor = getContentResolver().query(MyContentProvider.CONTENT_URI, ArrayProjection,
                null, null, null);

        data = new ArrayList<>();

        if(cursor.moveToLast()) {
            Runs run = new Runs(
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_DURATION)),
                    cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_DISTANCE)),
                    cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_ELEVATION)),
                    cursor.getBlob(cursor.getColumnIndex(AppContract.COLUMN_MAP))
            );
            data.add(run);
            i++;
            Log.d("movedToLast",i+"");
        }

    }

    public void LaunchMapsActivity(){
        startActivity(new Intent(this, MapsActivity.class));
    }




}
