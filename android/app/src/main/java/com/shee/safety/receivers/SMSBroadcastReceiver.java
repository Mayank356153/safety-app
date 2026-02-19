package com.shee.safety.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;
import com.shee.safety.MainActivity;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SMSBroadcastReceiver";
    private static final String CHANNEL_ID = "emergency_alerts";
    private static final String EMERGENCY_PREFS = "EmergencyAlerts";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "========================================");
        Log.d(TAG, "SMS BROADCAST RECEIVER TRIGGERED");
        Log.d(TAG, "========================================");
        
        Toast.makeText(context, "SMS Receiver Called!", Toast.LENGTH_LONG).show();
        
        if (intent.getAction() == null || !intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d(TAG, "Not an SMS_RECEIVED action");
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences("SMSReceiver", Context.MODE_PRIVATE);
        boolean isListening = prefs.getBoolean("isListening", false);
        String monitoredNumber = prefs.getString("monitoredNumber", "");

        Log.d(TAG, "Is listening: " + isListening);
        Log.d(TAG, "Monitored number: " + monitoredNumber);

        if (!isListening) {
            Log.d(TAG, "SMS monitoring is disabled");
            Toast.makeText(context, "Monitoring is OFF", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus != null) {
                Log.d(TAG, "Processing " + pdus.length + " SMS messages");
                
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    String sender = smsMessage.getDisplayOriginatingAddress();
                    String messageBody = smsMessage.getMessageBody();
                    long timestamp = smsMessage.getTimestampMillis();

                    Log.d(TAG, "========================================");
                    Log.d(TAG, "SMS DETAILS:");
                    Log.d(TAG, "From: " + sender);
                    Log.d(TAG, "Message: " + messageBody);
                    Log.d(TAG, "========================================");

                    Toast.makeText(context, "SMS from: " + sender, Toast.LENGTH_LONG).show();
                    String bodyLower = messageBody.toLowerCase();
                      boolean hasKeyword = bodyLower.contains("emergency") || 
                        bodyLower.contains("help") || 
                        bodyLower.contains("bachao");
                    if (isNumberMatch(sender, monitoredNumber) && hasKeyword) {
                        
                        
                        Log.d(TAG, "✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓");
                        Log.d(TAG, "EMERGENCY SMS DETECTED!!!");
                        Log.d(TAG, "✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓✓");
                        
                        Toast.makeText(context, "🚨 EMERGENCY DETECTED! 🚨", Toast.LENGTH_LONG).show();
                        
                        // Extract coordinates
                        CoordinateData coords = extractCoordinates(messageBody);
                        
                        if (coords != null) {
                            Log.d(TAG, "Coordinates found: " + coords.latitude + ", " + coords.longitude);
                        } else {
                            Log.d(TAG, "No coordinates found");
                        }
                        
                        // Trigger emergency alert
                        triggerEmergencyAlert(context, sender, messageBody, coords, timestamp);
                        
                    } else {
                        Log.d(TAG, "❌ Sender does NOT match monitored number");
                        Log.d(TAG, "Sender: " + sender);
                        Log.d(TAG, "Monitored: " + monitoredNumber);
                        Toast.makeText(context, "Not from monitored number", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Log.d(TAG, "No PDUs in bundle");
            }
        } else {
            Log.d(TAG, "No extras in intent");
        }
    }

    private void triggerEmergencyAlert(Context context, String sender, String message, 
                                      CoordinateData coords, long timestamp) {
        Log.d(TAG, ">>> triggerEmergencyAlert() called");
        
        // Save emergency data
        saveEmergencyData(context, sender, message, coords, timestamp);

        // Send to server
        sendAlertToServer(context, sender, message, coords);
        
        // Wake up and unlock screen
        Log.d(TAG, ">>> Waking screen...");
        wakeUpScreen(context);
        
        // Start continuous vibration
        Log.d(TAG, ">>> Starting vibration...");
        startContinuousVibration(context);
        
        // Start continuous alarm sound
        Log.d(TAG, ">>> Starting alarm...");
        startContinuousAlarm(context);
        
        // Show emergency notification
        Log.d(TAG, ">>> Showing notification...");
        showEmergencyNotification(context, sender, message, coords);
        
        // Launch app to alert page
        Log.d(TAG, ">>> Launching app...");
        launchEmergencyAlert(context, coords);
        
        Log.d(TAG, ">>> triggerEmergencyAlert() completed");
    }

    private void wakeUpScreen(Context context) {
        try {
            Log.d(TAG, "wakeUpScreen: Starting...");
            
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            
            // Wake up screen with highest flags
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK | 
                PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "EmergencyAlert:WakeLock"
            );
            wakeLock.acquire(30000); // 30 seconds
            
            Log.d(TAG, "wakeUpScreen: WakeLock acquired");
            
            // Also try to dismiss keyguard
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            if (keyguardManager != null) {
                Log.d(TAG, "wakeUpScreen: Keyguard manager available");
            }
            
            Toast.makeText(context, "Screen woken up!", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "wakeUpScreen: Error", e);
        }
    }

    private void startContinuousVibration(Context context) {
    try {
        Log.d(TAG, "startContinuousVibration: Starting...");
        
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        
        if (vibrator == null) {
            Log.e(TAG, "startContinuousVibration: Vibrator is NULL");
            return;
        }
        
        if (!vibrator.hasVibrator()) {
            Log.e(TAG, "startContinuousVibration: Device has no vibrator");
            return;
        }
        
        // Pattern: wait, vibrate, wait, vibrate... (in milliseconds)
        // This will vibrate: 0ms wait, 1000ms vibrate, 500ms wait, 1000ms vibrate, 500ms wait...
        long[] pattern = {
            0,      // Start immediately
            1000,   // Vibrate for 1 second
            500,    // Pause for 0.5 seconds
            1000,   // Vibrate for 1 second
            500,    // Pause for 0.5 seconds
            1000,   // Vibrate for 1 second
            500     // Pause for 0.5 seconds
        };
        
        // The second parameter is the index to repeat from
        // 0 means repeat from the start (continuous loop)
        vibrator.vibrate(pattern, 0);
        
        Log.d(TAG, "startContinuousVibration: ✓✓✓ CONTINUOUS VIBRATION STARTED ✓✓✓");
        Toast.makeText(context, "Vibration started (continuous)!", Toast.LENGTH_SHORT).show();
        
        // Save state
        SharedPreferences prefs = context.getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putBoolean("vibrationActive", true).apply();
        
    } catch (Exception e) {
        Log.e(TAG, "startContinuousVibration: Error", e);
    }
}



    private void startContinuousAlarm(Context context) {
        try {
            Log.d(TAG, "startContinuousAlarm: Starting...");
            
            // Start alarm service
            Intent alarmIntent = new Intent(context, AlarmService.class);
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                Log.d(TAG, "startContinuousAlarm: Starting foreground service (Android O+)");
                context.startForegroundService(alarmIntent);
            } else {
                Log.d(TAG, "startContinuousAlarm: Starting service (Android < O)");
                context.startService(alarmIntent);
            }
            
            Log.d(TAG, "startContinuousAlarm: Service start command sent");
            Toast.makeText(context, "Alarm started!", Toast.LENGTH_SHORT).show();
            
            // Save state
            SharedPreferences prefs = context.getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
            prefs.edit().putBoolean("alarmActive", true).apply();
            
        } catch (Exception e) {
            Log.e(TAG, "startContinuousAlarm: Error", e);
            Toast.makeText(context, "Error starting alarm: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void launchEmergencyAlert(Context context, CoordinateData coords) {
    try {
        Log.d(TAG, "launchEmergencyAlert: Starting...");
        
        // Create intent with all necessary flags to bring app to foreground
        Intent launchIntent = new Intent(context, MainActivity.class);
        
        // These flags will bring the app to foreground even if locked
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        
        launchIntent.putExtra("openEmergencyAlert", true);
        launchIntent.putExtra("emergencyTimestamp", System.currentTimeMillis());
        
        if (coords != null) {
            launchIntent.putExtra("latitude", coords.latitude);
            launchIntent.putExtra("longitude", coords.longitude);
            Log.d(TAG, "launchEmergencyAlert: Added coordinates to intent");
        }
        
        context.startActivity(launchIntent);
        
        Log.d(TAG, "launchEmergencyAlert: ✓✓✓ ACTIVITY LAUNCHED ✓✓✓");
        Toast.makeText(context, "Opening emergency alert...", Toast.LENGTH_SHORT).show();
        
    } catch (Exception e) {
        Log.e(TAG, "launchEmergencyAlert: Error", e);
        Toast.makeText(context, "Error launching app: " + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}
      
  
    private CoordinateData extractCoordinates(String message) {
        CoordinateData coords = new CoordinateData();
        
        // Pattern 1: "Lat: XX.XXX, Lon: XX.XXX"
        Pattern pattern1 = Pattern.compile("(?i)lat(?:itude)?[:\\s]+([\\-]?\\d+\\.\\d+)[,\\s]+lon(?:gitude)?[:\\s]+([\\-]?\\d+\\.\\d+)");
        Matcher matcher1 = pattern1.matcher(message);
        if (matcher1.find()) {
            coords.latitude = matcher1.group(1);
            coords.longitude = matcher1.group(2);
            coords.fullText = message;
            return coords;
        }
        
        // Pattern 2: Two decimal numbers
        Pattern pattern2 = Pattern.compile("([\\-]?\\d{1,2}\\.\\d{4,})[,\\s]+([\\-]?\\d{1,3}\\.\\d{4,})");
        Matcher matcher2 = pattern2.matcher(message);
        if (matcher2.find()) {
            coords.latitude = matcher2.group(1);
            coords.longitude = matcher2.group(2);
            coords.fullText = message;
            return coords;
        }
        
        return null;
    }

    private void saveEmergencyData(Context context, String sender, String message, 
                                   CoordinateData coords, long timestamp) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            JSONObject emergencyData = new JSONObject();
            emergencyData.put("sender", sender);
            emergencyData.put("message", message);
            emergencyData.put("timestamp", timestamp);
            
            if (coords != null) {
                emergencyData.put("latitude", coords.latitude);
                emergencyData.put("longitude", coords.longitude);
                emergencyData.put("hasCoordinates", true);
            } else {
                emergencyData.put("hasCoordinates", false);
            }
            
            editor.putString("lastEmergency", emergencyData.toString());
            editor.putBoolean("hasActiveEmergency", true);
            editor.putLong("emergencyTimestamp", timestamp);
            editor.apply();
            
            Log.d(TAG, "Emergency data saved: " + emergencyData.toString());
        } catch (Exception e) {
            Log.e(TAG, "Error saving emergency data", e);
        }
    }

    private void showEmergencyNotification(Context context, String sender, String message, CoordinateData coords) {
        try {
            Log.d(TAG, "showEmergencyNotification: Starting...");
            
            NotificationManager notificationManager = 
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Emergency Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription("Critical emergency alerts");
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
                Log.d(TAG, "showEmergencyNotification: Channel created");
            }

            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                0, 
                notificationIntent, 
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("🚨 EMERGENCY ALERT 🚨")
                .setContentText("From: " + sender)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

            notificationManager.notify(999, builder.build());
            Log.d(TAG, "showEmergencyNotification: Notification shown");
        } catch (Exception e) {
            Log.e(TAG, "showEmergencyNotification: Error", e);
        }
    }

    private boolean isNumberMatch(String sender, String monitored) {
        if (monitored == null || monitored.isEmpty()) {
            Log.d(TAG, "isNumberMatch: Monitored number is empty");
            return false;
        }
        
        String cleanSender = sender.replaceAll("[^0-9]", "");
        String cleanMonitored = monitored.replaceAll("[^0-9]", "");
        
        Log.d(TAG, "isNumberMatch: Comparing...");
        Log.d(TAG, "  Clean Sender: " + cleanSender);
        Log.d(TAG, "  Clean Monitored: " + cleanMonitored);
        
        if (cleanSender.length() >= 10 && cleanMonitored.length() >= 10) {
            String senderLast10 = cleanSender.substring(cleanSender.length() - 10);
            String monitoredLast10 = cleanMonitored.substring(cleanMonitored.length() - 10);
            
            Log.d(TAG, "  Last 10 - Sender: " + senderLast10);
            Log.d(TAG, "  Last 10 - Monitored: " + monitoredLast10);
            
            boolean matches = senderLast10.equals(monitoredLast10);
            Log.d(TAG, "  Result: " + (matches ? "MATCH" : "NO MATCH"));
            
            return matches;
        }
        
        boolean matches = cleanSender.equals(cleanMonitored);
        Log.d(TAG, "  Result: " + (matches ? "MATCH" : "NO MATCH"));
        return matches;
    }

   
    private void sendAlertToServer(Context context, String sender, String message, CoordinateData coords) {
    new Thread(() -> {
        try {
            SharedPreferences locationPrefs = context.getSharedPreferences("LocationSettings", Context.MODE_PRIVATE);
            String serverUrl = locationPrefs.getString("serverUrl", "https://safety-app-server-6p3a.onrender.com");
            String userId = locationPrefs.getString("userId", "");

            org.json.JSONObject jsonData = new org.json.JSONObject();
            jsonData.put("sender", sender);
            jsonData.put("senderId", userId);
            jsonData.put("message", message);
            jsonData.put("timestamp", System.currentTimeMillis());
            
            if (coords != null) {
                jsonData.put("latitude", coords.latitude);
                jsonData.put("longitude", coords.longitude);
                jsonData.put("hasCoordinates", true);
            } else {
                jsonData.put("hasCoordinates", false);
            }

            URL url = new URL(serverUrl + "/api/emergency/alert");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            java.io.OutputStream os = conn.getOutputStream();
            os.write(jsonData.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Alert sent to server. Response: " + responseCode);

            conn.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "Error sending alert to server", e);
        }
    }).start();
}






private static class CoordinateData {
        String latitude;
        String longitude;
        String fullText;
    }
}