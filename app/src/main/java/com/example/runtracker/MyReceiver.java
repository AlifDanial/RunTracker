package com.example.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        //When broadcast intent is received regarding location change, the broadcast receiver displays a Toast message
        if(intent.getAction().matches("android.location.PROVIDERS_CHANGED")){
            String message = "Broadcast intent detected " + "Location Providers Changed";

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }


    }
}
