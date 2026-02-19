package com.shee.safety.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shee.safety.services.LocationUpdateService;

public class LocationServiceRestarter extends BroadcastReceiver {
    private static final String TAG = "LocationRestarter";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Service restart triggered");
        
        Intent serviceIntent = new Intent(context, LocationUpdateService.class);
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
        
        Log.d(TAG, "Location service restarted");
    }
}