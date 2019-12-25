package com.example.runtracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class RunView extends AppCompatActivity {

    private static String ID;
    private static String NAME;
    private static String DATE;
    private static String DISTANCE;
    private static String DURATION;
    private static String ELEVATION;
    private static String PACE;
    private static byte[] MAP;
    private static float pace;
    DBHandler dbHandler;
    Bitmap bitmap;
    TextView dateText;
    TextView distanceText;
    TextView timeText;
    TextView elevationText;
    TextView paceText;
    TextView nameText;
    ImageView map;
    Button deleteBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_view);

        getSupportActionBar().hide();
        // initialize global variables
        dbHandler = new DBHandler(this, null, null, AppContract.DATABASE_VERSION);
        dateText = findViewById(R.id.dateText);
        distanceText = findViewById(R.id.distanceText);
        timeText = findViewById(R.id.timeText);
        elevationText = findViewById(R.id.elevationText);
        paceText = findViewById(R.id.paceText);
        nameText = findViewById(R.id.activityName);
        deleteBtn = findViewById(R.id.deletebtn);
        map = findViewById(R.id.mapView);


        // get recipe details from intent extras
        Intent intent = getIntent();
        ID = intent.getStringExtra("id");
        DATE = intent.getStringExtra("date");
        DATE = "on the " + DATE;
        DISTANCE = intent.getStringExtra("distance");
        DURATION = intent.getStringExtra("duration");
        ELEVATION = intent.getStringExtra("elevation");
        ELEVATION = ELEVATION + "m";
        MAP = intent.getByteArrayExtra("map");

        String t = DURATION;
        String[] h1=t.split(":");

        int hour=Integer.parseInt(h1[0]);
        int minute=Integer.parseInt(h1[1]);
        int second=Integer.parseInt(h1[2]);

        int time;
        time = minute + (second/60) + (hour * 60);
        String distance = DISTANCE.substring(0, DISTANCE.length() - 2);
        pace = time / Float.parseFloat(distance);

        if(time != 0 && Float.parseFloat(distance) != 0){
        PACE = String.format("%.02f",pace);
        PACE = PACE + "'";
        }else {
        PACE = "0.00";
        PACE = PACE + "'";
        }

        if(pace > 13){
            NAME = "Walking Activity";
        }
        else{
            NAME = "Running Activity";
        }

        if(MAP != null){
        bitmap = BitmapFactory.decodeByteArray(MAP,0,MAP.length);
        }

        map.setImageBitmap(bitmap);
        dateText.setText(DATE);
        distanceText.setText(DISTANCE);
        timeText.setText(DURATION);
        elevationText.setText(ELEVATION);
        paceText.setText(PACE);
        nameText.setText(NAME);

    }//end oncreate


    public void deleteRun(View view) {
        // delete current run
        dbHandler.deleteRun(ID);

        // end activity and return result
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    } // end of deleteRecipeOnClick()


}
