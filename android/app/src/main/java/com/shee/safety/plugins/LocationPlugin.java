package com.shee.safety.plugins;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import com.shee.safety.services.LocationUpdateService;

import android.util.Log;

@CapacitorPlugin(
    name = "LocationTracker",
    permissions = {
        @Permission(
            strings = { 
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            },
            alias = "location"
        )
    }
)
public class LocationPlugin extends Plugin {
    private static final String TAG = "LocationPlugin";
    private LocationManager locationManager;

    @PluginMethod
    public void checkLocationPermission(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("granted", hasLocationPermission());
        call.resolve(ret);
    }

    @PluginMethod
    public void requestLocationPermission(PluginCall call) {
        if (hasLocationPermission()) {
            JSObject ret = new JSObject();
            ret.put("granted", true);
            call.resolve(ret);
        } else {
            requestPermissionForAlias("location", call, "locationPermissionCallback");
        }
    }

    @PermissionCallback
    private void locationPermissionCallback(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("granted", hasLocationPermission());
        call.resolve(ret);
    }

    @PluginMethod
    public void getCurrentLocation(PluginCall call) {
        Log.d(TAG, "getCurrentLocation called");
        
        if (!hasLocationPermission()) {
            call.reject("Location permission not granted");
            return;
        }

        try {
            locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            
            // Try to get last known location first
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            if (location != null) {
                Log.d(TAG, "Got location: " + location.getLatitude() + ", " + location.getLongitude());
                
                JSObject ret = new JSObject();
                ret.put("latitude", location.getLatitude());
                ret.put("longitude", location.getLongitude());
                ret.put("accuracy", location.getAccuracy());
                ret.put("timestamp", location.getTime());
                call.resolve(ret);
            } else {
                Log.d(TAG, "No last known location, requesting fresh location");
                // Request fresh location
                requestFreshLocation(call);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security exception", e);
            call.reject("Location permission denied", e);
        } catch (Exception e) {
            Log.e(TAG, "Error getting location", e);
            call.reject("Error getting location", e);
        }
    }

    @PluginMethod
    public void startLocationUpdates(PluginCall call) {
        Log.d(TAG, "startLocationUpdates called");
        
        if (!hasLocationPermission()) {
            call.reject("Location permission not granted");
            return;
        }

        try {
            Intent serviceIntent = new Intent(getContext(), LocationUpdateService.class);
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                getContext().startForegroundService(serviceIntent);
                Log.d(TAG, "Started foreground service");
            } else {
                getContext().startService(serviceIntent);
                Log.d(TAG, "Started service");
            }

            JSObject ret = new JSObject();
            ret.put("success", true);
            ret.put("message", "Location updates started");
            call.resolve(ret);
        } catch (Exception e) {
            Log.e(TAG, "Error starting location updates", e);
            call.reject("Error starting location updates", e);
        }
    }

    @PluginMethod
    public void stopLocationUpdates(PluginCall call) {
        try {
            Intent serviceIntent = new Intent(getContext(), LocationUpdateService.class);
            getContext().stopService(serviceIntent);

            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error stopping location updates", e);
        }
    }

    private void requestFreshLocation(PluginCall call) {
        try {
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "Fresh location: " + location.getLatitude() + ", " + location.getLongitude());
                    
                    JSObject ret = new JSObject();
                    ret.put("latitude", location.getLatitude());
                    ret.put("longitude", location.getLongitude());
                    ret.put("accuracy", location.getAccuracy());
                    ret.put("timestamp", location.getTime());
                    call.resolve(ret);
                    
                    locationManager.removeUpdates(this);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {}
            };

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0,
                locationListener
            );

            // Timeout after 10 seconds
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(() -> {
                locationManager.removeUpdates(locationListener);
                call.reject("Location timeout");
            }, 10000);

        } catch (SecurityException e) {
            call.reject("Location permission denied", e);
        } catch (Exception e) {
            call.reject("Error requesting location", e);
        }
    }

    @PluginMethod
public void isServiceRunning(PluginCall call) {
    android.app.ActivityManager manager = (android.app.ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
    
    boolean isRunning = false;
    
    for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
        if ("com.shee.safety.services.LocationUpdateService".equals(service.service.getClassName())) {
            isRunning = true;
            break;
        }
    }
    
    JSObject ret = new JSObject();
    ret.put("isRunning", isRunning);
    call.resolve(ret);
}
    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(
            getContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }
}