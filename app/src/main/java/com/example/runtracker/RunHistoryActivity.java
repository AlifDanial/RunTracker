package com.example.runtracker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.runtracker.provider.MyContentProvider;

import java.util.ArrayList;
import java.util.List;

public class RunHistoryActivity extends AppCompatActivity {

    private static List<Runs> data;
    private static CustomAdapter adapter;
    DBHandler dbHandler;
    ListView runsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_history);

        getSupportActionBar().hide();
        dbHandler = new DBHandler(this,null,null, AppContract.DATABASE_VERSION);
        runsList = findViewById(R.id.runsList);

        //onclick listener for listview

        runsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Runs run = data.get(i);
                Intent intent = new Intent(getApplicationContext(), RunView.class);
                intent.putExtra("id", Integer.toString(run.getRunID()));
                intent.putExtra("duration", run.getRunDuration());
                intent.putExtra("distance", run.getRunDistance());
                intent.putExtra("date", run.getRunDate());
                intent.putExtra("elevation", run.getRunElevation());
                startActivityForResult(intent, 1);
            }
        });

        displayDBContents();

    }//end oncreate

    private void displayDBContents() {
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

        data = new ArrayList<Runs>();

        while (cursor.moveToNext()) {
            Runs run = new Runs(
                    Integer.parseInt(cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_DURATION)),
                    cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_DISTANCE)),
                    cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(AppContract.COLUMN_ELEVATION)),
                    cursor.getBlob(cursor.getColumnIndex(AppContract.COLUMN_MAP))
            );
            data.add(run);
        }

        adapter = new CustomAdapter(this, data);
        runsList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0 || requestCode == 1) {
                displayDBContents(); // update list at every return to the activity
            }
        }
    }


}
