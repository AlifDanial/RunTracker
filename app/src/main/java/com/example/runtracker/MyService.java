package com.example.runtracker;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import static com.example.runtracker.MyNotification.CHANNEL_ID;

public class MyService extends Service {

    MapsActivity mapsActivity;
    private final IBinder myBinder = new MyLocalBinder();


    //executes when startService is called and starts the service and foreground notification
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            String content = mapsActivity.getState();

            final Intent notificationIntent = new Intent(this, MapsActivity.class);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("New Activity")
                    .setContentText(content)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1, notification);

        return START_NOT_STICKY;
    } // end of method onStartCommand()

    //Binding methods
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class MyLocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    //method to stop the service
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}