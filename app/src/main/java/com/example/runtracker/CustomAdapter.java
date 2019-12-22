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

    List<Runs> runsList;
    TextView runDate;
    TextView runDuration;
    TextView runDistance;

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

        // update UI element values
        runDate.setText(item.getRunDate());
        runDuration.setText(item.getRunDuration());
        runDistance.setText(item.getRunDistance());

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



