package com.example.runtracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Runs> {

    //initialization of widget variables
    List<Runs> runsList;
    TextView runDate;
    TextView runDuration;
    TextView runDistance;
    TextView runName;
    String NAME;
    String DURATION;
    String DISTANCE;
    float pace;

    //custom adapter constructor
    public CustomAdapter(Context context, List<Runs> runsList) {
        super(context, R.layout.activity_custom_adapter, runsList);
        this.runsList = runsList;
    }

    @Override   //method to inflate the list for all content
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customList = inflater.inflate(R.layout.activity_custom_adapter, parent, false);

        // get run details
        Runs item = getItem(position);
        runDate = customList.findViewById(R.id.runDate);
        runDuration = customList.findViewById(R.id.runDuration);
        runDistance = customList.findViewById(R.id.runDistance);
        runName = customList.findViewById(R.id.runName);

        // update UI element values
        runDate.setText(item.getRunDate());
        runDuration.setText(item.getRunDuration());
        runDistance.setText(item.getRunDistance());

        //calculate the pace dynamically to determine activity was a walk or run
        DURATION = item.getRunDuration();
        DISTANCE = item.getRunDistance();
        String t = DURATION;
        String[] h1=t.split(":");

        int hour=Integer.parseInt(h1[0]);
        int minute=Integer.parseInt(h1[1]);
        int second=Integer.parseInt(h1[2]);

        int time;
        time = minute + (second/60) + (hour * 60);
        String distance = DISTANCE.substring(0, DISTANCE.length() - 2);
        pace = time / Float.parseFloat(distance);

        if(pace > 13){
            NAME = "Walking Activity";
        }
        else{
            NAME = "Running Activity";
        }

        runName.setText(NAME);

        //return custom adapter list into runhistory class
        return customList;
    }

    @Override
    public void add(@Nullable Runs object) {
        super.add(object);
    }

    @Override
    public void insert(@Nullable Runs object, int index) {
        super.insert(object, index);
    }

    @Override
    public void remove(@Nullable Runs object) {
        super.remove(object);
    }

    @Override
    public void clear() {
        super.clear();
    }

} // end of ContentAdapter.java



