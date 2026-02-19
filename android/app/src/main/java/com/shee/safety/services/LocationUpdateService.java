// package com.shee.safety.services;

// import android.app.Notification;
// import android.app.NotificationChannel;
// import android.app.NotificationManager;
// import android.app.Service;
// import android.content.Context;
// import android.content.Intent;
// import android.content.SharedPreferences;
// import android.location.Location;
// import android.location.LocationListener;
// import android.location.LocationManager;
// import android.os.Bundle;
// import android.os.IBinder;
// import android.util.Log;
// import androidx.core.app.NotificationCompat;

// import org.json.JSONArray;
// import org.json.JSONObject;
// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.io.OutputStream;
// import java.net.HttpURLConnection;
// import java.net.URL;

// public class LocationUpdateService extends Service implements LocationListener {
//     private static final String TAG = "LocationUpdateService";
//     private static final String CHANNEL_ID = "location_service";
//     private static final long UPDATE_INTERVAL = 1000; // 1 minute
//     private static final float MIN_DISTANCE = 0; // 50 meters

//     private LocationManager locationManager;
//     private String serverUrl;
//     private String userId;

//     @Override
//     public void onCreate() {
//         super.onCreate();
//         Log.d(TAG, "Location Update Service Created");
        
//         SharedPreferences prefs = getSharedPreferences("LocationSettings", MODE_PRIVATE);
//         serverUrl = prefs.getString("serverUrl", "https://safety-app-server-6p3a.onrender.com");
//         userId = prefs.getString("userId", "");
        
//         Log.d(TAG, "Server URL: " + serverUrl);
//         Log.d(TAG, "User ID: " + userId);
//     }
// @Override
// public int onStartCommand(Intent intent, int flags, int startId) {
//     Log.d(TAG, "");
//     Log.d(TAG, "========================================");
//     Log.d(TAG, "==== LOCATION SERVICE STARTED ====");
//     Log.d(TAG, "========================================");
    
//     loadSettings();
    
//     Log.d(TAG, "Configuration:");
//     Log.d(TAG, "  Server URL: " + (serverUrl.isEmpty() ? "❌ EMPTY!" : serverUrl));
//     Log.d(TAG, "  User ID: " + (userId.isEmpty() ? "❌ EMPTY!" : userId));
//     Log.d(TAG, "  Update Interval: " + (UPDATE_INTERVAL / 1000) + " seconds");
//     Log.d(TAG, "  Min Distance: " + MIN_DISTANCE + " meters");
    
//     if (serverUrl.isEmpty() || userId.isEmpty()) {
//         Log.e(TAG, "");
//         Log.e(TAG, "❌❌❌ CRITICAL ERROR ❌❌❌");
//         Log.e(TAG, "Server URL or User ID is empty!");
//         Log.e(TAG, "Service will NOT send location updates!");
//         Log.e(TAG, "Please register the user first!");
//         Log.e(TAG, "");
//     }
    
//     createNotificationChannel();
//     Notification notification = createNotification();
//     startForeground(2001, notification);
//     Log.d(TAG, "✅ Service running in foreground");
    
//     startLocationUpdates();
    
//     Log.d(TAG, "========================================");
//     Log.d(TAG, "");
    
//     return START_STICKY;
// }

// private void startLocationUpdates() {
//     try {
//         locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
//         Log.d(TAG, "Requesting location updates from:");
//         Log.d(TAG, "  - GPS Provider");
//         Log.d(TAG, "  - Network Provider");
        
//         locationManager.requestLocationUpdates(
//             LocationManager.GPS_PROVIDER,
//             UPDATE_INTERVAL,
//             MIN_DISTANCE,
//             this
//         );
        
//         locationManager.requestLocationUpdates(
//             LocationManager.NETWORK_PROVIDER,
//             UPDATE_INTERVAL,
//             MIN_DISTANCE,
//             this
//         );
        
//         Log.d(TAG, "✅ Location updates registered successfully");
        
//         // Try to get last known location immediately
//         Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//         if (lastKnown == null) {
//             lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//         }
        
//         if (lastKnown != null) {
//             Log.d(TAG, "📍 Last known location: " + lastKnown.getLatitude() + ", " + lastKnown.getLongitude());
//             Log.d(TAG, "Age: " + ((System.currentTimeMillis() - lastKnown.getTime()) / 1000) + " seconds old");
            
//             // Send last known location to server immediately
//             if (!serverUrl.isEmpty() && !userId.isEmpty()) {
//                 Log.d(TAG, "Sending last known location to server...");
//                 sendLocationToServer(lastKnown);
//             }
//         } else {
//             Log.d(TAG, "⚠️ No last known location available");
//             Log.d(TAG, "Waiting for first GPS fix...");
//         }
        
//     } catch (SecurityException e) {
//         Log.e(TAG, "❌ LOCATION PERMISSION NOT GRANTED!", e);
//     } catch (Exception e) {
//         Log.e(TAG, "❌ Error starting location updates", e);
//     }
// }

// @Override
// public void onLocationChanged(Location location) {
//     Log.d(TAG, "========================================");
//     Log.d(TAG, "📍 LOCATION CHANGED!");
//     Log.d(TAG, "Latitude: " + location.getLatitude());
//     Log.d(TAG, "Longitude: " + location.getLongitude());
//     Log.d(TAG, "Accuracy: " + location.getAccuracy());
//     Log.d(TAG, "User ID: " + userId);
//     Log.d(TAG, "Server URL: " + serverUrl);
//     Log.d(TAG, "========================================");
    
//     saveLocationLocally(location);
    
//     if (!serverUrl.isEmpty() && !userId.isEmpty()) {
//         Log.d(TAG, "✅ Sending to server...");
//         sendLocationToServer(location);
//     } else {
//         Log.e(TAG, "❌ Cannot send - serverUrl or userId is empty!");
//     }
// }

//     // private void startLocationUpdates() {
//     //     try {
//     //         locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            
//     //         locationManager.requestLocationUpdates(
//     //             LocationManager.GPS_PROVIDER,
//     //             UPDATE_INTERVAL,
//     //             MIN_DISTANCE,
//     //             this
//     //         );
            
//     //         locationManager.requestLocationUpdates(
//     //             LocationManager.NETWORK_PROVIDER,
//     //             UPDATE_INTERVAL,
//     //             MIN_DISTANCE,
//     //             this
//     //         );
            
//     //         Log.d(TAG, "✅ Location updates started (every 2 minutes)");
//     //     } catch (SecurityException e) {
//     //         Log.e(TAG, "Location permission not granted", e);
//     //     }
//     // }



//     private void saveLocationLocally(Location location) {
//         try {
//             SharedPreferences prefs = getSharedPreferences("UserLocation", MODE_PRIVATE);
//             SharedPreferences.Editor editor = prefs.edit();
            
//             editor.putString("latitude", String.valueOf(location.getLatitude()));
//             editor.putString("longitude", String.valueOf(location.getLongitude()));
//             editor.putFloat("accuracy", location.getAccuracy());
//             editor.putLong("timestamp", location.getTime());
//             editor.apply();
            
//             Log.d(TAG, "Location saved locally");
//         } catch (Exception e) {
//             Log.e(TAG, "Error saving location locally", e);
//         }
//     }

//     private void sendLocationToServer(Location location) {
//         new Thread(() -> {
//             try {
//                 URL url = new URL(serverUrl + "/api/location/update");
//                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                 conn.setRequestMethod("POST");
//                 conn.setRequestProperty("Content-Type", "application/json");
//                 conn.setDoOutput(true);
//                 conn.setConnectTimeout(10000);
//                 conn.setReadTimeout(10000);

//                 JSONObject jsonData = new JSONObject();
//                 jsonData.put("userId", userId);
//                 jsonData.put("latitude", location.getLatitude());
//                 jsonData.put("longitude", location.getLongitude());
//                 jsonData.put("accuracy", location.getAccuracy());
//                 jsonData.put("timestamp", location.getTime());

//                 OutputStream os = conn.getOutputStream();
//                 os.write(jsonData.toString().getBytes());
//                 os.flush();
//                 os.close();

//                 int responseCode = conn.getResponseCode();
//                 Log.d(TAG, "Server response: " + responseCode);

//                 if (responseCode == 200) {
//                     // Read response
//                     BufferedReader br = new BufferedReader(
//                         new InputStreamReader(conn.getInputStream())
//                     );
//                     StringBuilder response = new StringBuilder();
//                     String line;
//                     while ((line = br.readLine()) != null) {
//                         response.append(line);
//                     }
//                     br.close();

//                     Log.d(TAG, "Response: " + response.toString());

//                     // Check for nearby alerts
//                     JSONObject responseJson = new JSONObject(response.toString());
//                     if (responseJson.has("nearbyAlerts")) {
//                         JSONArray nearbyAlerts = responseJson.getJSONArray("nearbyAlerts");
                        
//                         if (nearbyAlerts.length() > 0) {
//                             Log.d(TAG, "🚨🚨🚨 NEARBY ALERT DETECTED! 🚨🚨🚨");
                            
//                             // Trigger emergency alert
//                             for (int i = 0; i < nearbyAlerts.length(); i++) {
//                                 JSONObject alert = nearbyAlerts.getJSONObject(i);
//                                 triggerEmergencyAlert(alert);
//                             }
//                         }
//                     }
//                 }

//                 conn.disconnect();
//             } catch (Exception e) {
//                 Log.e(TAG, "Error sending location to server", e);
//             }
//         }).start();
//     }

//     private void triggerEmergencyAlert(JSONObject alert) {
//         try {
//             Log.d("AlertTrigger", alert.toString());
//             String sender = alert.getString("sender");
//             String message = alert.getString("message");
//             double distance = alert.getDouble("distance");
//             String alertLatitude = alert.getString("latitude");
//             String alertLongitude = alert.getString("longitude");

//             Log.d(TAG, "Triggering alert from: " + sender);
//             Log.d(TAG, "Distance: " + distance + "km");

//             // Save alert data
//             SharedPreferences prefs = getSharedPreferences("EmergencyAlerts", MODE_PRIVATE);
//             prefs.edit()
//                 .putString("lastEmergency", alert.toString())
//                 .putBoolean("hasActiveEmergency", true)
//                 .putLong("emergencyTimestamp", System.currentTimeMillis())
//                 .apply();

//             // Start alarm service
//             Intent alarmIntent = new Intent(this, com.shee.safety.receivers.AlarmService.class);
//             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                 startForegroundService(alarmIntent);
//             } else {
//                 startService(alarmIntent);
//             }

//             // Start vibration
//             android.os.Vibrator vibrator = (android.os.Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//             if (vibrator != null) {
//                 long[] pattern = {0, 1000, 500, 1000, 500, 1000, 500};
//                 vibrator.vibrate(pattern, 0);
//             }

//             // Show notification
//             showEmergencyNotification(sender, message, distance);

//             // Launch app
//             Intent launchIntent = new Intent(this, com.shee.safety.MainActivity.class);
//             launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//             launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//             launchIntent.putExtra("openEmergencyAlert", true);
//             launchIntent.putExtra("latitude", alertLatitude);
//             launchIntent.putExtra("longitude", alertLongitude);
//             startActivity(launchIntent);

//             Log.d(TAG, "✅ Emergency alert triggered successfully!");

//         } catch (Exception e) {
//             Log.e(TAG, "Error triggering emergency alert", e);
//         }
//     }

    
//     private void showEmergencyNotification(String sender, String message, double distance) {
//         try {
//             NotificationManager notificationManager = 
//                 (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                 NotificationChannel channel = new NotificationChannel(
//                     "emergency_alerts",
//                     "Emergency Alerts",
//                     NotificationManager.IMPORTANCE_HIGH
//                 );
//                 notificationManager.createNotificationChannel(channel);
//             }

//             NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "emergency_alerts")
//                 .setSmallIcon(android.R.drawable.ic_dialog_alert)
//                 .setContentTitle("🚨 EMERGENCY NEARBY!")
//                 .setContentText(String.format("Emergency %.2fkm away from you!", distance))
//                 .setStyle(new NotificationCompat.BigTextStyle()
//                     .bigText(String.format("From: %s\nDistance: %.2fkm\nMessage: %s", 
//                         sender, distance, message)))
//                 .setPriority(NotificationCompat.PRIORITY_MAX)
//                 .setCategory(NotificationCompat.CATEGORY_ALARM)
//                 .setAutoCancel(false)
//                 .setOngoing(true);

//             notificationManager.notify(999, builder.build());
//         } catch (Exception e) {
//             Log.e(TAG, "Error showing notification", e);
//         }
//     }

//     @Override
//     public void onStatusChanged(String provider, int status, Bundle extras) {}

//     @Override
//     public void onProviderEnabled(String provider) {
//         Log.d(TAG, "Provider enabled: " + provider);
//     }

//     @Override
//     public void onProviderDisabled(String provider) {
//         Log.d(TAG, "Provider disabled: " + provider);
//     }

//     private void createNotificationChannel() {
//         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//             NotificationChannel channel = new NotificationChannel(
//                 CHANNEL_ID,
//                 "Location Tracking",
//                 NotificationManager.IMPORTANCE_LOW
//             );
//             NotificationManager manager = getSystemService(NotificationManager.class);
//             manager.createNotificationChannel(channel);
//         }
//     }

//     private Notification createNotification() {
//         return new NotificationCompat.Builder(this, CHANNEL_ID)
//             .setContentTitle("📍 Location Tracking Active")
//             .setContentText("Monitoring for nearby emergencies...")
//             .setSmallIcon(android.R.drawable.ic_menu_mylocation)
//             .setPriority(NotificationCompat.PRIORITY_LOW)
//             .setOngoing(true)
//             .build();
//     }

//     @Override
//     public void onDestroy() {
//         super.onDestroy();
//         if (locationManager != null) {
//             locationManager.removeUpdates(this);
//         }
//         Log.d(TAG, "Location Update Service Destroyed");
//     }

//     @Override
//     public IBinder onBind(Intent intent) {
//         return null;
//     }
// }

package com.shee.safety.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationUpdateService extends Service implements LocationListener {
    private static final String TAG = "LocationUpdateService";
    private static final String CHANNEL_ID = "location_service";
    
    // *** CHANGED: Update every 1 second ***
    private static final long UPDATE_INTERVAL = 1000; // 1 second (was 120000)
    private static final float MIN_DISTANCE = 0; // 0 meters - update on any movement
    
    private LocationManager locationManager;
    private String serverUrl;
    private String userId;
    private int updateCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "========================================");
        Log.d(TAG, "Location Update Service Created");
        Log.d(TAG, "========================================");
        
        loadSettings();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "");
        Log.d(TAG, "========================================");
        Log.d(TAG, "==== LOCATION SERVICE STARTED ====");
        Log.d(TAG, "========================================");
        
        loadSettings();
        
        Log.d(TAG, "Configuration:");
        Log.d(TAG, "  Server URL: " + (serverUrl.isEmpty() ? "❌ EMPTY!" : serverUrl));
        Log.d(TAG, "  User ID: " + (userId.isEmpty() ? "❌ EMPTY!" : userId));
        Log.d(TAG, "  Update Interval: 1 SECOND"); // Updated message
        Log.d(TAG, "  Min Distance: 0 meters (continuous)"); // Updated message
        
        if (serverUrl.isEmpty() || userId.isEmpty()) {
            Log.e(TAG, "");
            Log.e(TAG, "❌❌❌ CRITICAL ERROR ❌❌❌");
            Log.e(TAG, "Server URL or User ID is empty!");
            Log.e(TAG, "Service will NOT send location updates!");
            Log.e(TAG, "Please register the user first!");
            Log.e(TAG, "");
        }
        
        createNotificationChannel();
        Notification notification = createNotification();
        startForeground(2001, notification);
        Log.d(TAG, "✅ Service running in foreground");
        
        startLocationUpdates();
        
        Log.d(TAG, "========================================");
        Log.d(TAG, "");
        
        return START_STICKY;
    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences("LocationSettings", MODE_PRIVATE);
        serverUrl = prefs.getString("serverUrl", "");
        userId = prefs.getString("userId", "");
    }

    private void startLocationUpdates() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            
            Log.d(TAG, "Requesting CONTINUOUS location updates from:");
            Log.d(TAG, "  - GPS Provider (every 1 second)");
            Log.d(TAG, "  - Network Provider (every 1 second)");
            
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                UPDATE_INTERVAL, // 1 second
                MIN_DISTANCE,    // 0 meters
                this
            );
            
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                UPDATE_INTERVAL, // 1 second
                MIN_DISTANCE,    // 0 meters
                this
            );
            
            Log.d(TAG, "✅ Location updates registered successfully");
            
            // Send last known location immediately
            Location lastKnown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnown == null) {
                lastKnown = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            
            if (lastKnown != null) {
                Log.d(TAG, "📍 Last known location: " + lastKnown.getLatitude() + ", " + lastKnown.getLongitude());
                if (!serverUrl.isEmpty() && !userId.isEmpty()) {
                    sendLocationToServer(lastKnown);
                }
            } else {
                Log.d(TAG, "⚠️ No last known location available");
            }
            
        } catch (SecurityException e) {
            Log.e(TAG, "❌ LOCATION PERMISSION NOT GRANTED!", e);
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting location updates", e);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        updateCount++;
        
        Log.d(TAG, "");
        Log.d(TAG, "========================================");
        Log.d(TAG, "📍 LOCATION UPDATE #" + updateCount);
        Log.d(TAG, "========================================");
        Log.d(TAG, "Latitude: " + location.getLatitude());
        Log.d(TAG, "Longitude: " + location.getLongitude());
        Log.d(TAG, "Accuracy: " + location.getAccuracy() + "m");
        Log.d(TAG, "Provider: " + location.getProvider());
        Log.d(TAG, "Speed: " + location.getSpeed() + " m/s");
        Log.d(TAG, "Time: " + new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(location.getTime())));
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Server URL: " + serverUrl);
        Log.d(TAG, "========================================");
        
        saveLocationLocally(location);
        
        if (!serverUrl.isEmpty() && !userId.isEmpty()) {
            Log.d(TAG, "✅ Sending to server...");
            sendLocationToServer(location);
        } else {
            Log.e(TAG, "❌ Cannot send - serverUrl or userId is empty!");
        }
    }

    private void saveLocationLocally(Location location) {
        try {
            SharedPreferences prefs = getSharedPreferences("UserLocation", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putString("latitude", String.valueOf(location.getLatitude()));
            editor.putString("longitude", String.valueOf(location.getLongitude()));
            editor.putFloat("accuracy", location.getAccuracy());
            editor.putLong("timestamp", location.getTime());
            editor.apply();
            
            Log.d(TAG, "✅ Location saved locally");
        } catch (Exception e) {
            Log.e(TAG, "Error saving location locally", e);
        }
    }

    private void sendLocationToServer(Location location) {
        new Thread(() -> {
            try {
                Log.d(TAG, "→ HTTP POST to: " + serverUrl + "/api/location/update");
                
                URL url = new URL(serverUrl + "/api/location/update");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);

                JSONObject jsonData = new JSONObject();
                jsonData.put("userId", userId);
                jsonData.put("latitude", location.getLatitude());
                jsonData.put("longitude", location.getLongitude());
                jsonData.put("accuracy", location.getAccuracy());
                jsonData.put("timestamp", location.getTime());

                OutputStream os = conn.getOutputStream();
                os.write(jsonData.toString().getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "← Server response code: " + responseCode);

                if (responseCode == 200) {
                    BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                    );
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                    br.close();

                    String responseString = response.toString();
                    Log.d(TAG, "← Response: " + responseString);

                    JSONObject responseJson = new JSONObject(responseString);
                    
                    if (responseJson.has("nearbyAlerts")) {
                        JSONArray nearbyAlerts = responseJson.getJSONArray("nearbyAlerts");
                        
                        Log.d(TAG, "← Nearby alerts count: " + nearbyAlerts.length());
                        
                        if (nearbyAlerts.length() > 0) {
                            Log.d(TAG, "");
                            Log.d(TAG, "🚨🚨🚨 NEARBY ALERT DETECTED! 🚨🚨🚨");
                            Log.d(TAG, "Number of alerts: " + nearbyAlerts.length());
                            
                            for (int i = 0; i < nearbyAlerts.length(); i++) {
                                JSONObject alert = nearbyAlerts.getJSONObject(i);
                                Log.d(TAG, "Alert " + (i+1) + ": " + alert.toString());
                                triggerEmergencyAlert(alert);
                            }
                            
                            Log.d(TAG, "🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨🚨");
                            Log.d(TAG, "");
                        } else {
                            Log.d(TAG, "✅ No nearby alerts");
                        }
                    }
                } else {
                    Log.e(TAG, "❌ Server returned error code: " + responseCode);
                }

                conn.disconnect();
                Log.d(TAG, "========================================");
                Log.d(TAG, "");
                
            } catch (Exception e) {
                Log.e(TAG, "❌ Error sending location to server", e);
                e.printStackTrace();
            }
        }).start();
    }

    private void triggerEmergencyAlert(JSONObject alert) {
        try {
            String alertId = alert.getString("alertId");
            String sender = alert.getString("sender");
            String message = alert.getString("message");
            double distance = alert.getDouble("distance");
            String alertLatitude = String.valueOf(alert.getDouble("latitude"));
            String alertLongitude = String.valueOf(alert.getDouble("longitude"));

            Log.d(TAG, "========================================");
            Log.d(TAG, "TRIGGERING EMERGENCY ALERT!");
            Log.d(TAG, "Alert ID: " + alertId);
            Log.d(TAG, "From: " + sender);
            Log.d(TAG, "Distance: " + distance + "km");
            Log.d(TAG, "Message: " + message);
            Log.d(TAG, "========================================");

            SharedPreferences prefs = getSharedPreferences("EmergencyAlerts", MODE_PRIVATE);
            
            JSONObject emergencyData = new JSONObject();
            emergencyData.put("sender", sender);
            emergencyData.put("message", message);
            emergencyData.put("latitude", alertLatitude);
            emergencyData.put("longitude", alertLongitude);
            emergencyData.put("timestamp", System.currentTimeMillis());
            emergencyData.put("distance", distance);
            
            prefs.edit()
                .putString("lastEmergency", emergencyData.toString())
                .putBoolean("hasActiveEmergency", true)
                .putLong("emergencyTimestamp", System.currentTimeMillis())
                .putString("latitude", alertLatitude)
                .putString("longitude", alertLongitude)
                .putBoolean("hasCoordinates", true)
                .apply();

            Log.d(TAG, "✅ Emergency data saved");

            // Start alarm service
            Intent alarmIntent = new Intent(this, com.shee.safety.receivers.AlarmService.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                startForegroundService(alarmIntent);
            } else {
                startService(alarmIntent);
            }
            Log.d(TAG, "✅ Alarm service started");

            // Start vibration
            android.os.Vibrator vibrator = (android.os.Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                long[] pattern = {0, 1000, 500, 1000, 500, 1000, 500};
                vibrator.vibrate(pattern, 0);
                Log.d(TAG, "✅ Vibration started");
            }

            // Show notification
            showEmergencyNotification(sender, message, distance);
            Log.d(TAG, "✅ Notification shown");

            // Launch app
            Intent launchIntent = new Intent(this, com.shee.safety.MainActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            launchIntent.putExtra("openEmergencyAlert", true);
            launchIntent.putExtra("latitude", alertLatitude);
            launchIntent.putExtra("longitude", alertLongitude);
            startActivity(launchIntent);
            
            Log.d(TAG, "✅ App launched");
            Log.d(TAG, "========================================");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error triggering emergency alert", e);
            e.printStackTrace();
        }
    }

    private void showEmergencyNotification(String sender, String message, double distance) {
        try {
            NotificationManager notificationManager = 
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    "emergency_alerts",
                    "Emergency Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
            }

            Intent notificationIntent = new Intent(this, com.shee.safety.MainActivity.class);
            notificationIntent.putExtra("openEmergencyAlert", true);
            
            PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "emergency_alerts")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("🚨 EMERGENCY NEARBY!")
                .setContentText(String.format("Emergency %.2fkm away from you!", distance))
                .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(String.format("From: %s\nDistance: %.2fkm\nMessage: %s", 
                        sender, distance, message)))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

            notificationManager.notify(999, builder.build());
        } catch (Exception e) {
            Log.e(TAG, "Error showing notification", e);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "Provider enabled: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "Provider disabled: " + provider);
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Location Tracking",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Continuous location updates every second");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, com.shee.safety.MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("📍 Real-Time Tracking")
            .setContentText("Updating every second...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "Task removed - restarting service");
        
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        
        PendingIntent restartPendingIntent = PendingIntent.getService(
            getApplicationContext(), 
            1, 
            restartServiceIntent, 
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        
        android.app.AlarmManager alarmService = (android.app.AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
            android.app.AlarmManager.ELAPSED_REALTIME,
            android.os.SystemClock.elapsedRealtime() + 1000,
            restartPendingIntent
        );
        
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
        Log.d(TAG, "Location Update Service Destroyed - Will restart");
        
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.shee.safety.RestartLocationService");
        broadcastIntent.setClass(this, com.shee.safety.receivers.LocationServiceRestarter.class);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}