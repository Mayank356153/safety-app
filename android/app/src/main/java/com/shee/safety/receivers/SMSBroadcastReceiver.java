// // // // // package com.shee.safety.receivers;

// // // // // import android.content.BroadcastReceiver;
// // // // // import android.content.Context;
// // // // // import android.content.Intent;
// // // // // import android.content.SharedPreferences;
// // // // // import android.os.Bundle;
// // // // // import android.os.Vibrator;
// // // // // import android.telephony.SmsMessage;
// // // // // import android.util.Log;
// // // // // import android.widget.Toast;
// // // // // import android.app.NotificationChannel;
// // // // // import android.app.NotificationManager;
// // // // // import android.app.PendingIntent;
// // // // // import androidx.core.app.NotificationCompat;
// // // // // import com.shee.safety.MainActivity;

// // // // // public class SMSBroadcastReceiver extends BroadcastReceiver {
// // // // //     private static final String TAG = "SMSBroadcastReceiver";
// // // // //     private static final String CHANNEL_ID = "sms_alerts";
// // // // //     @Override
// // // // //     public void onReceive(Context context, Intent intent) {
// // // // //         Log.d(TAG, "===== SMS BROADCAST RECEIVER TRIGGERED =====");

// // // // //         if (intent.getAction() == null || !intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
// // // // //         return;
// // // // //     }
    
// // // // //     SharedPreferences prefs = context.getSharedPreferences("SMSReceiver", Context.MODE_PRIVATE);
// // // // //     boolean isListening = prefs.getBoolean("isListening", false);
// // // // //     String monitoredNumber = prefs.getString("monitoredNumber", "");
    
// // // // //     if (!isListening) return;
    
// // // // //     Bundle bundle = intent.getExtras();
// // // // //     if (bundle != null) {
// // // // //         Object[] pdus = (Object[]) bundle.get("pdus");
// // // // //         if (pdus != null) {
// // // // //             for (Object pdu : pdus) {
// // // // //                 SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
// // // // //                 String sender = smsMessage.getDisplayOriginatingAddress();
// // // // //                 String messageBody = smsMessage.getMessageBody(); // messageBody is defined here

// // // // //                 if (isNumberMatch(sender, monitoredNumber)) {

// // // // //                     // MOVE THE PARSING LOGIC HERE (Inside onReceive)
// // // // //                     if (messageBody.contains("Alert corrdinate =")) {
// // // // //                         try {
// // // // //                             String coordsPart = messageBody.split("=")[1].trim();
// // // // //                             String[] latLong = coordsPart.split(",");
// // // // //                             String lat = latLong[0].trim();
// // // // //                             String lon = latLong[1].trim();

// // // // //                             // Update Database and Service
// // // // //                             updateDatabase(context,lat, lon);

// // // // //                             Intent serviceIntent = new Intent(context, com.shee.safety.services.AlertService.class);
// // // // //                             serviceIntent.putExtra("lat", lat);
// // // // //                             serviceIntent.putExtra("lon", lon);
                            
// // // // //                             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
// // // // //                                 context.startForegroundService(serviceIntent);
// // // // //                             } else {
// // // // //                                 context.startService(serviceIntent);
// // // // //                             }
// // // // //                         } catch (Exception e) {
// // // // //                             Log.e(TAG, "Error parsing alert coordinates", e);
// // // // //                         }
// // // // //                     }

// // // // //                     vibratePhone(context);
// // // // //                     showNotification(context, sender, messageBody);
// // // // //                 }
// // // // //             }
// // // // //         }
// // // // //     }
// // // // // }

// // // // // // RESTORE isNumberMatch to its original purpose (only comparing numbers)
// // // // // private boolean isNumberMatch(String sender, String monitored) {
// // // // //         if (monitored == null || monitored.isEmpty()) return false;

// // // // //         String cleanSender = sender.replaceAll("[^0-9]", "");
// // // // //         String cleanMonitored = monitored.replaceAll("[^0-9]", "");
        
// // // // //         if (cleanSender.length() >= 10 && cleanMonitored.length() >= 10) {
// // // // //             String senderLast10 = cleanSender.substring(cleanSender.length() - 10);
// // // // //             String monitoredLast10 = cleanMonitored.substring(cleanMonitored.length() - 10);
// // // // //             return senderLast10.equals(monitoredLast10);
// // // // //         }
// // // // //         return cleanSender.equals(cleanMonitored);
// // // // //     }
// // // // //   private void vibratePhone(Context context) {
// // // // //     Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// // // // //     if (v != null && v.hasVibrator()) {
// // // // //         if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
// // // // //             // Vibrate for 500ms at default amplitude
// // // // //             v.vibrate(android.os.VibrationEffect.createOneShot(500, android.os.VibrationEffect.DEFAULT_AMPLITUDE));
// // // // //         } else {
// // // // //             // Deprecated in API 26 
// // // // //             v.vibrate(500);
// // // // //         }
// // // // //     }
// // // // // }
// // // // //     private void showNotification(Context context, String sender, String message) {
// // // // //         try {
// // // // //             Log.d(TAG, "Creating notification...");
// // // // //             NotificationManager notificationManager = 
// // // // //             (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
// // // // //             // Create notification channel (required for Android 8.0+)
// // // // //             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
// // // // //                 NotificationChannel channel = new NotificationChannel(
// // // // //                     CHANNEL_ID,
// // // // //                     "SMS Alerts",
// // // // //                     NotificationManager.IMPORTANCE_HIGH
// // // // //                 );
// // // // //                 channel.setDescription("Alerts for SMS from monitored numbers");
// // // // //                 channel.enableVibration(true);
// // // // //                 notificationManager.createNotificationChannel(channel);
// // // // //                 Log.d(TAG, "Notification channel created");
// // // // //             }

// // // // //             // Create intent to open app when notification is clicked
// // // // //             Intent notificationIntent = new Intent(context, MainActivity.class);
// // // // //             notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            
// // // // //             PendingIntent pendingIntent = PendingIntent.getActivity(
// // // // //                 context, 
// // // // //                 0, 
// // // // //                 notificationIntent, 
// // // // //                 PendingIntent.FLAG_IMMUTABLE
// // // // //             );

// // // // //             // Build notification
// // // // //             NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
// // // // //                 .setSmallIcon(android.R.drawable.ic_dialog_email)
// // // // //                 .setContentTitle("🚨 Alert: SMS from " + sender)
// // // // //                 .setContentText(message)
// // // // //                 .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
// // // // //                 .setPriority(NotificationCompat.PRIORITY_HIGH)
// // // // //                 .setAutoCancel(true)
// // // // //                 .setContentIntent(pendingIntent)
// // // // //                 .setVibrate(new long[]{0, 500, 200, 500});
                
// // // // //                 notificationManager.notify(1, builder.build());
// // // // //                 Log.d(TAG, "Notification shown successfully");
// // // // //             } catch (Exception e) {
// // // // //             Log.e(TAG, "Error showing notification", e);
// // // // //         }
// // // // //     }

// // // // //     private void saveSMS(Context context, String sender, String message, long timestamp) {
// // // // //         try {
// // // // //             SharedPreferences prefs = context.getSharedPreferences("SMSReceiver", Context.MODE_PRIVATE);
// // // // //             SharedPreferences.Editor editor = prefs.edit();
            
// // // // //             // Save last received SMS
// // // // //             editor.putString("lastSMSSender", sender);
// // // // //             editor.putString("lastSMSMessage", message);
// // // // //             editor.putLong("lastSMSTimestamp", timestamp);
// // // // //             editor.apply();
            
// // // // //             Log.d(TAG, "SMS saved to SharedPreferences");
// // // // //         } catch (Exception e) {
// // // // //             Log.e(TAG, "Error saving SMS", e);
// // // // //         }
// // // // //     }
        
// // // // //     private void updateDatabase(Context context, String lat, String lon) {
// // // // //         new Thread(() -> {
// // // // //             try {
// // // // //             // Use your backend endpoint (e.g., from your api.js file)
// // // // //             java.net.URL url = new java.net.URL("https://your-api-url.com/api/pos/by-location");
// // // // //             java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
// // // // //             conn.setRequestMethod("POST");
// // // // //             conn.setRequestProperty("Content-Type", "application/json; utf-8");
// // // // //             conn.setDoOutput(true);
            
// // // // //             String jsonInputString = String.format("{\"latitude\":\"%s\", \"longitude\":\"%s\"}", lat, lon);
// // // // //             try (java.io.OutputStream os = conn.getOutputStream()) {
// // // // //                 byte[] input = jsonInputString.getBytes("utf-8");
// // // // //                 os.write(input, 0, input.length);           
// // // // //             }
// // // // //             conn.getResponseCode(); 
// // // // //         } catch (Exception e) {
// // // // //             Log.e(TAG, "DB update failed", e);
// // // // //         }
// // // // //     }).start();
// // // // // }
    





// // // // // }


// // // // // //    @Override
// // // // // //    public void onReceive(Context context, Intent intent) {
// // // // // //        Log.d(TAG, "===== SMS BROADCAST RECEIVER TRIGGERED =====");
// // // // // //        Log.d(TAG, "Action: " + intent.getAction());
// // // // // //
// // // // // //        // Show toast for debugging
// // // // // //        Toast.makeText(context, "SMS Receiver triggered!", Toast.LENGTH_SHORT).show();
// // // // // //
// // // // // //        if (intent.getAction() == null || !intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
// // // // // //            Log.d(TAG, "Not an SMS_RECEIVED action, ignoring");
// // // // // //            return;
// // // // // //        }
// // // // // //
// // // // // //        SharedPreferences prefs = context.getSharedPreferences("SMSReceiver", Context.MODE_PRIVATE);
// // // // // //        boolean isListening = prefs.getBoolean("isListening", false);
// // // // // //        String monitoredNumber = prefs.getString("monitoredNumber", "");
// // // // // //
// // // // // //        Log.d(TAG, "Is listening: " + isListening);
// // // // // //        Log.d(TAG, "Monitored number: " + monitoredNumber);
// // // // // //
// // // // // //        if (!isListening) {
// // // // // //            Log.d(TAG, "SMS monitoring is disabled");
// // // // // //            Toast.makeText(context, "SMS monitoring is OFF", Toast.LENGTH_SHORT).show();
// // // // // //            return;
// // // // // //        }
// // // // // //
// // // // // //        Bundle bundle = intent.getExtras();
// // // // // //        if (bundle != null) {
// // // // // //            Object[] pdus = (Object[]) bundle.get("pdus");
// // // // // //            if (pdus != null) {
// // // // // //                Log.d(TAG, "Processing " + pdus.length + " SMS messages");
// // // // // //
// // // // // //                for (Object pdu : pdus) {
// // // // // //                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
// // // // // //                    String sender = smsMessage.getDisplayOriginatingAddress();
// // // // // //                    String messageBody = smsMessage.getMessageBody();
// // // // // //                    long timestamp = smsMessage.getTimestampMillis();
// // // // // //
// // // // // //                    Log.d(TAG, "SMS received from: " + sender);
// // // // // //                    Log.d(TAG, "Message: " + messageBody);
// // // // // //
// // // // // //                    // Check if sender matches monitored number
// // // // // //                    if (isNumberMatch(sender, monitoredNumber)) {
// // // // // //                        Log.d(TAG, "✓✓✓ SMS FROM MONITORED NUMBER DETECTED! ✓✓✓");
// // // // // //
// // // // // //                        Toast.makeText(context, "Alert! SMS from monitored number: " + sender, Toast.LENGTH_LONG).show();
// // // // // //
// // // // // //                        // Vibrate phone
// // // // // //                        vibratePhone(context);
// // // // // //
// // // // // //                        // Show notification
// // // // // //                        showNotification(context, sender, messageBody);
// // // // // //
// // // // // //                        // Save SMS to local storage
// // // // // //                        saveSMS(context, sender, messageBody, timestamp);
// // // // // //                    } else {
// // // // // //                        Log.d(TAG, "Sender doesn't match monitored number");
// // // // // //                        Log.d(TAG, "Sender: " + sender + " | Monitored: " + monitoredNumber);
// // // // // //                        Toast.makeText(context, "SMS from: " + sender + " (not monitored)", Toast.LENGTH_SHORT).show();
// // // // // //                    }
// // // // // //                }
// // // // // //            } else {
// // // // // //                Log.d(TAG, "No PDUs in bundle");
// // // // // //            }
// // // // // //        } else {
// // // // // //            Log.d(TAG, "No extras in intent");
// // // // // //        }
// // // // // //    }
// // // // // //
// // // // // //    private boolean isNumberMatch(String sender, String monitored) {
// // // // // //        if (messageBody.contains("Alert corrdinate =")) {
// // // // // //        try {
// // // // // //            String coordsPart = messageBody.split("=")[1].trim();
// // // // // //            String[] latLong = coordsPart.split(",");
// // // // // //            String lat = latLong[0].trim();
// // // // // //            String lon = latLong[1].trim();
// // // // // //
// // // // // //            // 2. ADD THIS: Call the database update helper
// // // // // //            updateDatabase(context, lat, lon);
// // // // // //
// // // // // //            // 3. ADD THIS: Start the foreground AlertService
// // // // // //            Intent serviceIntent = new Intent(context, com.shee.safety.services.AlertService.class);
// // // // // //            serviceIntent.putExtra("lat", lat);
// // // // // //            serviceIntent.putExtra("lon", lon);
// // // // // //
// // // // // //            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
// // // // // //                context.startForegroundService(serviceIntent);
// // // // // //            } else {
// // // // // //                context.startService(serviceIntent);
// // // // // //            }
// // // // // //        } catch (Exception e) {
// // // // // //            Log.e(TAG, "Error parsing alert coordinates", e);
// // // // // //        }
// // // // // //    }
// // // // // //        if (monitored == null || monitored.isEmpty()) {
// // // // // //            Log.d(TAG, "Monitored number is empty");
// // // // // //            return false;
// // // // // //        }
// // // // // //
// // // // // //        // Remove all non-digit characters for comparison
// // // // // //        String cleanSender = sender.replaceAll("[^0-9]", "");
// // // // // //        String cleanMonitored = monitored.replaceAll("[^0-9]", "");
// // // // // //
// // // // // //        Log.d(TAG, "Comparing - Clean sender: " + cleanSender + " | Clean monitored: " + cleanMonitored);
// // // // // //
// // // // // //        // Check if numbers match (last 10 digits)
// // // // // //        if (cleanSender.length() >= 10 && cleanMonitored.length() >= 10) {
// // // // // //            String senderLast10 = cleanSender.substring(cleanSender.length() - 10);
// // // // // //            String monitoredLast10 = cleanMonitored.substring(cleanMonitored.length() - 10);
// // // // // //
// // // // // //            Log.d(TAG, "Last 10 digits - Sender: " + senderLast10 + " | Monitored: " + monitoredLast10);
// // // // // //
// // // // // //            return senderLast10.equals(monitoredLast10);
// // // // // //        }
// // // // // //
// // // // // //        return cleanSender.equals(cleanMonitored);
// // // // // //    }


// // // // package com.shee.safety.receivers;

// // // // import android.app.*;
// // // // import android.content.*;
// // // // import android.graphics.*;
// // // // import android.os.*;
// // // // import android.provider.Settings;
// // // // import android.telephony.SmsMessage;
// // // // import android.util.Log;
// // // // import android.view.*;
// // // // import android.widget.TextView;

// // // // import androidx.core.app.NotificationCompat;

// // // // import com.shee.safety.MainActivity;
// // // // import com.shee.safety.R;

// // // // public class SMSBroadcastReceiver extends BroadcastReceiver {

// // // //     private static final String TAG = "SMSBroadcastReceiver";
// // // //     private static final String CHANNEL_ID = "sms_alerts";

// // // //     @Override
// // // //     public void onReceive(Context context, Intent intent) {

// // // //         if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
// // // //             return;
// // // //         }

// // // //         Log.d(TAG, "===== SMS RECEIVED =====");

// // // //         SharedPreferences prefs =
// // // //                 context.getSharedPreferences("SMSReceiver", Context.MODE_PRIVATE);

// // // //         boolean isListening = prefs.getBoolean("isListening", false);
// // // //         String monitoredNumber = prefs.getString("monitoredNumber", "");

// // // //         if (!isListening || monitoredNumber.isEmpty()) return;

// // // //         Bundle bundle = intent.getExtras();
// // // //         if (bundle == null) return;

// // // //         Object[] pdus = (Object[]) bundle.get("pdus");
// // // //         if (pdus == null) return;

// // // //         for (Object pdu : pdus) {

// // // //             SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
// // // //             String sender = sms.getDisplayOriginatingAddress();
// // // //             String body = sms.getMessageBody();

// // // //             if (!isNumberMatch(sender, monitoredNumber)) continue;

// // // //             Log.d(TAG, "Matched SMS from monitored number");

// // // //             vibratePhone(context);
// // // //             showNotification(context, sender, body);

// // // //             if (Settings.canDrawOverlays(context)) {
// // // //                 showOverlay(context, body);
// // // //             } else {
// // // //                 Log.w(TAG, "Overlay permission NOT granted");
// // // //             }

// // // //             if (body.contains("alert coordinates=")) {
// // // //                 parseAndSend(body, context);
// // // //             }
// // // //         }
// // // //     }

// // // //     /* ---------------- HELPERS ---------------- */

// // // //     private boolean isNumberMatch(String sender, String monitored) {
// // // //         String s = sender.replaceAll("[^0-9]", "");
// // // //         String m = monitored.replaceAll("[^0-9]", "");
// // // //         if (s.length() >= 10 && m.length() >= 10) {
// // // //             return s.substring(s.length() - 10)
// // // //                     .equals(m.substring(m.length() - 10));
// // // //         }
// // // //         return s.equals(m);
// // // //     }

// // // //    private void vibratePhone(Context context) {
// // // //     try {
// // // //         Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

// // // //         if (v == null || !v.hasVibrator()) return;

// // // //         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
// // // //             v.vibrate(
// // // //                 VibrationEffect.createWaveform(
// // // //                     new long[]{0, 600, 200, 600, 200, 600},
// // // //                     -1
// // // //                 )
// // // //             );
// // // //         } else {
// // // //             v.vibrate(new long[]{0, 600, 200, 600}, -1);
// // // //         }
// // // //     } catch (Exception e) {
// // // //         Log.e("SMSBroadcastReceiver", "Vibration failed", e);
// // // //     }
// // // // }


// // // //     private void showNotification(Context context, String sender, String msg) {

// // // //         NotificationManager nm =
// // // //                 (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// // // //         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
// // // //             NotificationChannel channel = new NotificationChannel(
// // // //                     CHANNEL_ID,
// // // //                     "SMS Alerts",
// // // //                     NotificationManager.IMPORTANCE_HIGH
// // // //             );
// // // //             channel.enableVibration(true);
// // // //             nm.createNotificationChannel(channel);
// // // //         }

// // // //         Intent i = new Intent(context, MainActivity.class);
// // // //         PendingIntent pi = PendingIntent.getActivity(
// // // //                 context, 0, i, PendingIntent.FLAG_IMMUTABLE);

// // // //         Notification n = new NotificationCompat.Builder(context, CHANNEL_ID)
// // // //                 .setSmallIcon(android.R.drawable.ic_dialog_alert)
// // // //                 .setContentTitle("🚨 Emergency SMS")
// // // //                 .setContentText(msg)
// // // //                 .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
// // // //                 .setPriority(NotificationCompat.PRIORITY_HIGH)
// // // //                 .setAutoCancel(true)
// // // //                 .setContentIntent(pi)
// // // //                 .build();

// // // //         nm.notify(1001, n);
// // // //     }

// // // //     /* ---------------- OVERLAY ---------------- */

// // // //     private void showOverlay(Context context, String message) {

// // // //         WindowManager wm =
// // // //                 (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

// // // //         TextView view = new TextView(context);
// // // //         view.setText("🚨 EMERGENCY ALERT\n\n" + message);
// // // //         view.setTextColor(Color.WHITE);
// // // //         view.setBackgroundColor(Color.parseColor("#D32F2F"));
// // // //         view.setPadding(40, 40, 40, 40);
// // // //         view.setTextSize(18f);

// // // //         WindowManager.LayoutParams params = new WindowManager.LayoutParams(
// // // //                 WindowManager.LayoutParams.MATCH_PARENT,
// // // //                 WindowManager.LayoutParams.WRAP_CONTENT,
// // // //                 Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
// // // //                         ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
// // // //                         : WindowManager.LayoutParams.TYPE_PHONE,
// // // //                 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
// // // //                         | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
// // // //                         | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
// // // //                 PixelFormat.TRANSLUCENT
// // // //         );

// // // //         params.gravity = Gravity.TOP;

// // // //         wm.addView(view, params);

// // // //         new Handler(Looper.getMainLooper()).postDelayed(
// // // //                 () -> {
// // // //                     try {
// // // //                         wm.removeView(view);
// // // //                     } catch (Exception ignored) {}
// // // //                 },
// // // //                 6000
// // // //         );
// // // //     }

// // // //     /* ---------------- PARSE + API ---------------- */

// // // //     private void parseAndSend(String body, Context context) {
// // // //         try {
// // // //             String coords = body.split("=")[1].trim();
// // // //             String[] parts = coords.split(",");
// // // //             sendToServer(parts[0].trim(), parts[1].trim());
// // // //         } catch (Exception e) {
// // // //             Log.e(TAG, "Parse failed", e);
// // // //         }
// // // //     }

// // // //     private void sendToServer(String lat, String lon) {
// // // //         new Thread(() -> {
// // // //             try {
// // // //                 java.net.URL url =
// // // //                         new java.net.URL("https://your-api-url.com/api/pos/by-location");

// // // //                 java.net.HttpURLConnection conn =
// // // //                         (java.net.HttpURLConnection) url.openConnection();

// // // //                 conn.setRequestMethod("POST");
// // // //                 conn.setRequestProperty("Content-Type", "application/json");
// // // //                 conn.setConnectTimeout(8000);
// // // //                 conn.setReadTimeout(8000);
// // // //                 conn.setDoOutput(true);

// // // //                 String body =
// // // //                         "{\"latitude\":\"" + lat + "\",\"longitude\":\"" + lon + "\"}";

// // // //                 try (java.io.OutputStream os = conn.getOutputStream()) {
// // // //                     os.write(body.getBytes());
// // // //                 }

// // // //                 conn.getResponseCode();

// // // //             } catch (Exception e) {
// // // //                 Log.e(TAG, "DB update failed", e);
// // // //             }
// // // //         }).start();
// // // //     }
// // // // }


// // // package com.shee.safety.receivers;

// // // import android.app.*;
// // // import android.content.*;
// // // import android.graphics.*;
// // // import android.os.*;
// // // import android.provider.Settings;
// // // import android.telephony.SmsMessage;
// // // import android.util.Log;
// // // import android.view.*;
// // // import android.widget.TextView;
// // // import java.net.URL;
// // // import java.net.HttpURLConnection;

// // // import androidx.core.app.NotificationCompat;
// // // import android.net.Uri;
// // // import java.io.OutputStream;
// // // import com.shee.safety.MainActivity;

// // // public class SMSBroadcastReceiver extends BroadcastReceiver {

// // //     private static final String TAG = "SMSBroadcastReceiver";
// // //     private static final String CHANNEL_ID = "sms_alerts";

// // //     @Override
// // //     public void onReceive(Context context, Intent intent) {

// // //         if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {

// // //             return;
// // //         }

// // //         Log.d(TAG, "===== SMS RECEIVED =====");

// // //         SharedPreferences prefs =
// // //                 context.getSharedPreferences("SMSReceiver", Context.MODE_PRIVATE);

// // //         boolean isListening = prefs.getBoolean("isListening", false);
// // //         String monitoredNumber = prefs.getString("monitoredNumber", "");

// // //         if (!isListening || monitoredNumber.isEmpty()) return;

// // //         Bundle bundle = intent.getExtras();
// // //         if (bundle == null) return;

// // //         Object[] pdus = (Object[]) bundle.get("pdus");
// // //         if (pdus == null) return;

// // //         for (Object pdu : pdus) {

// // //             SmsMessage sms =
// // //                     SmsMessage.createFromPdu((byte[]) pdu);

// // //             String sender = sms.getDisplayOriginatingAddress();
// // //             String body = sms.getMessageBody();

// // //             if (!isNumberMatch(sender, monitoredNumber)) continue;

// // //             Log.d(TAG, "Matched SMS: " + body);

// // //             vibratePhone(context);
// // //             showNotification(context, body);

// // //             if (Settings.canDrawOverlays(context)) {
// // //                 showOverlay(context, body);
// // //             }

// // //             if (body.toLowerCase().contains("alert coordinates=")) {
// // //                 parseAndSend(body);
// // //             }
// // //         }
// // //     }

// // //     /* ---------------- NUMBER MATCH ---------------- */

// // //     private boolean isNumberMatch(String sender, String monitored) {
// // //         String s = sender.replaceAll("[^0-9]", "");
// // //         String m = monitored.replaceAll("[^0-9]", "");
// // //         if (s.length() >= 10 && m.length() >= 10) {
// // //             return s.substring(s.length() - 10)
// // //                     .equals(m.substring(m.length() - 10));
// // //         }
// // //         return s.equals(m);
// // //     }

// // //     /* ---------------- VIBRATION ---------------- */

// // //   private void vibratePhone(Context context) {
// // //     try {
// // //         Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// // //         if (v == null) return;

// // //         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
// // //             v.vibrate(
// // //                 VibrationEffect.createWaveform(
// // //                     new long[]{0, 800, 400, 800, 400, 800},
// // //                     -1
// // //                 )
// // //             );
// // //         } else {
// // //             v.vibrate(new long[]{0, 800, 400, 800}, -1);
// // //         }
// // //     } catch (Exception e) {
// // //         Log.e(TAG, "Vibration failed", e);
// // //     }
// // // }


// // //     /* ---------------- NOTIFICATION ---------------- */

// // //    private void showNotification(Context context, String msg) {

// // //     NotificationManager nm =
// // //             (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// // //     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
// // //         NotificationChannel channel = new NotificationChannel(
// // //                 CHANNEL_ID,
// // //                 "Emergency Alerts",
// // //                 NotificationManager.IMPORTANCE_HIGH
// // //         );
// // //         channel.enableVibration(true);
// // //         channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
// // //         channel.setBypassDnd(true); // 🔥 IMPORTANT
// // //         nm.createNotificationChannel(channel);
// // //     }

// // //     // 🔥 Deep link to React route
// // //     Intent intent = new Intent(
// // //             Intent.ACTION_VIEW,
// // //             android.net.Uri.parse("app://alert")
// // //     );
// // //     intent.setClass(context, MainActivity.class);
// // //     intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

// // //     PendingIntent fullScreenIntent = PendingIntent.getActivity(
// // //             context,
// // //             0,
// // //             intent,
// // //             PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
// // //     );

// // //     Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
// // //             .setSmallIcon(android.R.drawable.ic_dialog_alert)
// // //             .setContentTitle("🚨 EMERGENCY ALERT")
// // //             .setContentText(msg)
// // //             .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
// // //             .setPriority(NotificationCompat.PRIORITY_MAX)
// // //             .setCategory(NotificationCompat.CATEGORY_CALL)
// // //             .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
// // //             .setAutoCancel(true)
// // //             .setVibrate(new long[]{0, 1000, 500, 1000}) // 🔥 vibration
// // //             .setFullScreenIntent(fullScreenIntent, true) // 🔥 AUTO OPEN
// // //             .build();

// // //     nm.notify(9999, notification);
// // // }

// // //     /* ---------------- OVERLAY ---------------- */

// // //     private void showOverlay(Context context, String message) {

// // //         WindowManager wm =
// // //                 (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

// // //         TextView view = new TextView(context);
// // //         view.setText("🚨 EMERGENCY ALERT\n\n" + message);
// // //         view.setTextColor(Color.WHITE);
// // //         view.setBackgroundColor(Color.parseColor("#D32F2F"));
// // //         view.setPadding(40, 40, 40, 40);
// // //         view.setTextSize(18f);

// // //         WindowManager.LayoutParams params = new WindowManager.LayoutParams(
// // //                 WindowManager.LayoutParams.MATCH_PARENT,
// // //                 WindowManager.LayoutParams.WRAP_CONTENT,
// // //                 Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
// // //                         ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
// // //                         : WindowManager.LayoutParams.TYPE_PHONE,
// // //                 WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
// // //                         | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
// // //                 PixelFormat.TRANSLUCENT
// // //         );

// // //         params.gravity = Gravity.TOP;

// // //         wm.addView(view, params);

// // //         new Handler(Looper.getMainLooper()).postDelayed(() -> {
// // //             try {
// // //                 wm.removeView(view);
// // //             } catch (Exception ignored) {}
// // //         }, 6000);
// // //     }

// // //     /* ---------------- PARSE + API ---------------- */

// // //     private void parseAndSend(String body) {
// // //         try {
// // //             String coords =
// // //                     body.toLowerCase().split("alert coordinates=")[1].trim();

// // //             String[] parts = coords.split(",");

// // //             if (parts.length == 2) {
// // //                 sendToServer(parts[0].trim(), parts[1].trim());
// // //             }
// // //         } catch (Exception e) {
// // //             Log.e(TAG, "Coordinate parse failed", e);
// // //         }
// // //     }

// // //     private void sendToServer(String lat, String lon) {
// // //         new Thread(() -> {
// // //             try {
// // //                 URL url = new URL("https://your-api-url.com/api/pos/by-location");
// // //                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();

// // //                 conn.setRequestMethod("POST");
// // //                 conn.setRequestProperty("Content-Type", "application/json");
// // //                 conn.setConnectTimeout(8000);
// // //                 conn.setReadTimeout(8000);
// // //                 conn.setDoOutput(true);

// // //                 String body =
// // //                         "{\"latitude\":\"" + lat + "\",\"longitude\":\"" + lon + "\"}";

// // //                 try (OutputStream os = conn.getOutputStream()) {
// // //                     os.write(body.getBytes());
// // //                 }

// // //                 conn.getResponseCode();
// // //             } catch (Exception e) {
// // //                 Log.e(TAG, "Server update failed", e);
// // //             }
// // //         }).start();
// // //     }
// // // }


// // package com.shee.safety.receivers;

// // import android.content.BroadcastReceiver;
// // import android.content.Context;
// // import android.content.Intent;
// // import android.content.SharedPreferences;
// // import android.os.Bundle;
// // import android.os.Vibrator;
// // import android.telephony.SmsMessage;
// // import android.util.Log;
// // import android.media.Ringtone;
// // import android.media.RingtoneManager;
// // import android.net.Uri;
// // import android.app.NotificationChannel;
// // import android.app.NotificationManager;
// // import android.app.PendingIntent;
// // import androidx.core.app.NotificationCompat;
// // import com.shee.safety.MainActivity;

// // import org.json.JSONObject;
// // import java.util.regex.Matcher;
// // import java.util.regex.Pattern;

// // public class SMSBroadcastReceiver extends BroadcastReceiver {
// //     private static final String TAG = "SMSBroadcastReceiver";
// //     private static final String CHANNEL_ID = "emergency_alerts";
// //     private static final String EMERGENCY_PREFS = "EmergencyAlerts";

// //     @Override
// //     public void onReceive(Context context, Intent intent) {
// //         Log.d(TAG, "===== SMS BROADCAST RECEIVER TRIGGERED =====");
        
// //         if (intent.getAction() == null || !intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
// //             return;
// //         }

// //         SharedPreferences prefs = context.getSharedPreferences("SMSReceiver", Context.MODE_PRIVATE);
// //         boolean isListening = prefs.getBoolean("isListening", false);
// //         String monitoredNumber = prefs.getString("monitoredNumber", "");

// //         Log.d(TAG, "Is listening: " + isListening);
// //         Log.d(TAG, "Monitored number: " + monitoredNumber);

// //         if (!isListening) {
// //             Log.d(TAG, "SMS monitoring is disabled");
// //             return;
// //         }

// //         Bundle bundle = intent.getExtras();
// //         if (bundle != null) {
// //             Object[] pdus = (Object[]) bundle.get("pdus");
// //             if (pdus != null) {
// //                 for (Object pdu : pdus) {
// //                     SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
// //                     String sender = smsMessage.getDisplayOriginatingAddress();
// //                     String messageBody = smsMessage.getMessageBody();
// //                     long timestamp = smsMessage.getTimestampMillis();

// //                     Log.d(TAG, "SMS from: " + sender);
// //                     Log.d(TAG, "Message: " + messageBody);

// //                     if (isNumberMatch(sender, monitoredNumber)) {
// //                         Log.d(TAG, "✓✓✓ EMERGENCY SMS DETECTED! ✓✓✓");
                        
// //                         // Extract coordinates
// //                         CoordinateData coords = extractCoordinates(messageBody);
                        
// //                         if (coords != null) {
// //                             Log.d(TAG, "Coordinates found: " + coords.latitude + ", " + coords.longitude);
                            
// //                             // Trigger emergency alert
// //                             triggerEmergencyAlert(context, sender, messageBody, coords, timestamp);
// //                         } else {
// //                             Log.d(TAG, "No coordinates found in message");
// //                             // Still trigger alert but without coordinates
// //                             triggerEmergencyAlert(context, sender, messageBody, null, timestamp);
// //                         }
// //                     }
// //                 }
// //             }
// //         }
// //     }

// //     private void triggerEmergencyAlert(Context context, String sender, String message, 
// //                                       CoordinateData coords, long timestamp) {
// //         // Save emergency data
// //         saveEmergencyData(context, sender, message, coords, timestamp);
        
// //         // Vibrate continuously
// //         vibrateEmergency(context);
        
// //         // Play alarm sound
// //         playAlarmSound(context);
        
// //         // Show emergency notification
// //         showEmergencyNotification(context, sender, message, coords);
        
// //         // Launch app to alert page
// //         launchEmergencyAlert(context, coords);
// //     }

// //     private CoordinateData extractCoordinates(String message) {
// //         // Pattern for various coordinate formats:
// //         // "Lat: 28.7041, Lon: 77.1025"
// //         // "28.7041,77.1025"
// //         // "Location: 28.7041, 77.1025"
// //         // "http://maps.google.com/maps?q=28.7041,77.1025"
        
// //         CoordinateData coords = new CoordinateData();
        
// //         // Try pattern 1: "Lat: XX.XXX, Lon: XX.XXX"
// //         Pattern pattern1 = Pattern.compile("(?i)lat(?:itude)?[:\\s]+([\\-]?\\d+\\.\\d+)[,\\s]+lon(?:gitude)?[:\\s]+([\\-]?\\d+\\.\\d+)");
// //         Matcher matcher1 = pattern1.matcher(message);
// //         if (matcher1.find()) {
// //             coords.latitude = matcher1.group(1);
// //             coords.longitude = matcher1.group(2);
// //             coords.fullText = message;
// //             return coords;
// //         }
        
// //         // Try pattern 2: Two decimal numbers separated by comma
// //         Pattern pattern2 = Pattern.compile("([\\-]?\\d{1,2}\\.\\d{4,})[,\\s]+([\\-]?\\d{1,3}\\.\\d{4,})");
// //         Matcher matcher2 = pattern2.matcher(message);
// //         if (matcher2.find()) {
// //             coords.latitude = matcher2.group(1);
// //             coords.longitude = matcher2.group(2);
// //             coords.fullText = message;
// //             return coords;
// //         }
        
// //         // Try pattern 3: Google Maps link
// //         Pattern pattern3 = Pattern.compile("maps\\.google\\.com/maps\\?q=([\\-]?\\d+\\.\\d+),([\\-]?\\d+\\.\\d+)");
// //         Matcher matcher3 = pattern3.matcher(message);
// //         if (matcher3.find()) {
// //             coords.latitude = matcher3.group(1);
// //             coords.longitude = matcher3.group(2);
// //             coords.fullText = message;
// //             return coords;
// //         }
        
// //         return null;
// //     }

// //     private void saveEmergencyData(Context context, String sender, String message, 
// //                                    CoordinateData coords, long timestamp) {
// //         try {
// //             SharedPreferences prefs = context.getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
// //             SharedPreferences.Editor editor = prefs.edit();
            
// //             JSONObject emergencyData = new JSONObject();
// //             emergencyData.put("sender", sender);
// //             emergencyData.put("message", message);
// //             emergencyData.put("timestamp", timestamp);
            
// //             if (coords != null) {
// //                 emergencyData.put("latitude", coords.latitude);
// //                 emergencyData.put("longitude", coords.longitude);
// //                 emergencyData.put("hasCoordinates", true);
// //             } else {
// //                 emergencyData.put("hasCoordinates", false);
// //             }
            
// //             editor.putString("lastEmergency", emergencyData.toString());
// //             editor.putBoolean("hasActiveEmergency", true);
// //             editor.putLong("emergencyTimestamp", timestamp);
// //             editor.apply();
            
// //             Log.d(TAG, "Emergency data saved: " + emergencyData.toString());
// //         } catch (Exception e) {
// //             Log.e(TAG, "Error saving emergency data", e);
// //         }
// //     }

// //     private void vibrateEmergency(Context context) {
// //         try {
// //             Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
// //             if (vibrator != null && vibrator.hasVibrator()) {
// //                 // Emergency pattern: Long vibrations
// //                 long[] pattern = {0, 1000, 500, 1000, 500, 1000, 500, 1000};
// //                 vibrator.vibrate(pattern, 0); // 0 = repeat
                
// //                 Log.d(TAG, "Emergency vibration started");
// //             }
// //         } catch (Exception e) {
// //             Log.e(TAG, "Error vibrating phone", e);
// //         }
// //     }

// //     private void playAlarmSound(Context context) {
// //         try {
// //             Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
// //             if (alarmUri == null) {
// //                 alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
// //             }
            
// //             Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
// //             ringtone.play();
            
// //             // Store ringtone reference to stop it later
// //             SharedPreferences prefs = context.getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
// //             prefs.edit().putBoolean("alarmPlaying", true).apply();
            
// //             Log.d(TAG, "Alarm sound playing");
// //         } catch (Exception e) {
// //             Log.e(TAG, "Error playing alarm", e);
// //         }
// //     }

// //     private void showEmergencyNotification(Context context, String sender, String message, CoordinateData coords) {
// //         try {
// //             NotificationManager notificationManager = 
// //                 (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// //             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
// //                 NotificationChannel channel = new NotificationChannel(
// //                     CHANNEL_ID,
// //                     "Emergency Alerts",
// //                     NotificationManager.IMPORTANCE_HIGH
// //                 );
// //                 channel.setDescription("Critical emergency alerts");
// //                 channel.enableVibration(true);
// //                 channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
// //                 notificationManager.createNotificationChannel(channel);
// //             }

// //             Intent notificationIntent = new Intent(context, MainActivity.class);
// //             notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
// //             notificationIntent.putExtra("openEmergencyAlert", true);
            
// //             if (coords != null) {
// //                 notificationIntent.putExtra("latitude", coords.latitude);
// //                 notificationIntent.putExtra("longitude", coords.longitude);
// //             }
            
// //             PendingIntent pendingIntent = PendingIntent.getActivity(
// //                 context, 
// //                 0, 
// //                 notificationIntent, 
// //                 PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
// //             );

// //             String title = "🚨 EMERGENCY ALERT 🚨";
// //             String text = coords != null 
// //                 ? "Emergency from " + sender + " with location!" 
// //                 : "Emergency from " + sender;

// //             NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
// //                 .setSmallIcon(android.R.drawable.ic_dialog_alert)
// //                 .setContentTitle(title)
// //                 .setContentText(text)
// //                 .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
// //                 .setPriority(NotificationCompat.PRIORITY_MAX)
// //                 .setCategory(NotificationCompat.CATEGORY_ALARM)
// //                 .setAutoCancel(false)
// //                 .setOngoing(true)
// //                 .setContentIntent(pendingIntent)
// //                 .setVibrate(new long[]{0, 1000, 500, 1000});

// //             notificationManager.notify(999, builder.build());
// //             Log.d(TAG, "Emergency notification shown");
// //         } catch (Exception e) {
// //             Log.e(TAG, "Error showing notification", e);
// //         }
// //     }

// //     private void launchEmergencyAlert(Context context, CoordinateData coords) {
// //         try {
// //             Intent launchIntent = new Intent(context, MainActivity.class);
// //             launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
// //             launchIntent.putExtra("openEmergencyAlert", true);
            
// //             if (coords != null) {
// //                 launchIntent.putExtra("latitude", coords.latitude);
// //                 launchIntent.putExtra("longitude", coords.longitude);
// //             }
            
// //             context.startActivity(launchIntent);
// //             Log.d(TAG, "Emergency alert activity launched");
// //         } catch (Exception e) {
// //             Log.e(TAG, "Error launching activity", e);
// //         }
// //     }

// //     private boolean isNumberMatch(String sender, String monitored) {
// //         if (monitored == null || monitored.isEmpty()) {
// //             return false;
// //         }
        
// //         String cleanSender = sender.replaceAll("[^0-9]", "");
// //         String cleanMonitored = monitored.replaceAll("[^0-9]", "");
        
// //         if (cleanSender.length() >= 10 && cleanMonitored.length() >= 10) {
// //             String senderLast10 = cleanSender.substring(cleanSender.length() - 10);
// //             String monitoredLast10 = cleanMonitored.substring(cleanMonitored.length() - 10);
// //             return senderLast10.equals(monitoredLast10);
// //         }
        
// //         return cleanSender.equals(cleanMonitored);
// //     }

// //     private static class CoordinateData {
// //         String latitude;
// //         String longitude;
// //         String fullText;
// //     }
// // }

// package com.shee.safety.receivers;

// import android.content.BroadcastReceiver;
// import android.content.Context;
// import android.content.Intent;
// import android.content.SharedPreferences;
// import android.os.Bundle;
// import android.os.Vibrator;
// import android.os.PowerManager;
// import android.telephony.SmsMessage;
// import android.util.Log;
// import android.media.MediaPlayer;
// import android.media.AudioManager;
// import android.app.NotificationChannel;
// import android.app.NotificationManager;
// import android.app.PendingIntent;
// import androidx.core.app.NotificationCompat;
// import com.shee.safety.MainActivity;
// import com.shee.safety.R;

// import org.json.JSONObject;
// import java.util.regex.Matcher;
// import java.util.regex.Pattern;

// public class SMSBroadcastReceiver extends BroadcastReceiver {
//     private static final String TAG = "SMSBroadcastReceiver";
//     private static final String CHANNEL_ID = "emergency_alerts";
//     private static final String EMERGENCY_PREFS = "EmergencyAlerts";

//     @Override
//     public void onReceive(Context context, Intent intent) {
//         Log.d(TAG, "===== SMS BROADCAST RECEIVER TRIGGERED =====");
        
//         if (intent.getAction() == null || !intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//             return;
//         }

//         SharedPreferences prefs = context.getSharedPreferences("SMSReceiver", Context.MODE_PRIVATE);
//         boolean isListening = prefs.getBoolean("isListening", false);
//         String monitoredNumber = prefs.getString("monitoredNumber", "");

//         Log.d(TAG, "Is listening: " + isListening);
//         Log.d(TAG, "Monitored number: " + monitoredNumber);

//         if (!isListening) {
//             Log.d(TAG, "SMS monitoring is disabled");
//             return;
//         }

//         Bundle bundle = intent.getExtras();
//         if (bundle != null) {
//             Object[] pdus = (Object[]) bundle.get("pdus");
//             if (pdus != null) {
//                 for (Object pdu : pdus) {
//                     SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
//                     String sender = smsMessage.getDisplayOriginatingAddress();
//                     String messageBody = smsMessage.getMessageBody();
//                     long timestamp = smsMessage.getTimestampMillis();

//                     Log.d(TAG, "SMS from: " + sender);
//                     Log.d(TAG, "Message: " + messageBody);

//                     if (isNumberMatch(sender, monitoredNumber)) {
//                         Log.d(TAG, "✓✓✓ EMERGENCY SMS DETECTED! ✓✓✓");
                        
//                         // Extract coordinates
//                         CoordinateData coords = extractCoordinates(messageBody);
                        
//                         if (coords != null) {
//                             Log.d(TAG, "Coordinates found: " + coords.latitude + ", " + coords.longitude);
//                         }
                        
//                         // Trigger emergency alert
//                         triggerEmergencyAlert(context, sender, messageBody, coords, timestamp);
//                     }
//                 }
//             }
//         }
//     }

//     private void triggerEmergencyAlert(Context context, String sender, String message, 
//                                       CoordinateData coords, long timestamp) {
//         // Save emergency data
//         saveEmergencyData(context, sender, message, coords, timestamp);
        
//         // Wake up and unlock screen
//         wakeUpScreen(context);
        
//         // Start continuous vibration
//         startContinuousVibration(context);
        
//         // Start continuous alarm sound
//         startContinuousAlarm(context);
        
//         // Show emergency notification
//         showEmergencyNotification(context, sender, message, coords);
        
//         // Launch app to alert page
//         launchEmergencyAlert(context, coords);
//     }

//     private void wakeUpScreen(Context context) {
//         try {
//             PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            
//             // Wake up screen
//             PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
//                 PowerManager.FULL_WAKE_LOCK | 
//                 PowerManager.ACQUIRE_CAUSES_WAKEUP | 
//                 PowerManager.ON_AFTER_RELEASE,
//                 "EmergencyAlert:WakeLock"
//             );
//             wakeLock.acquire(10000); // 10 seconds
            
//             Log.d(TAG, "Screen woken up");
//         } catch (Exception e) {
//             Log.e(TAG, "Error waking screen", e);
//         }
//     }

//     private void startContinuousVibration(Context context) {
//         try {
//             Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//             if (vibrator != null && vibrator.hasVibrator()) {
//                 // Continuous emergency pattern
//                 long[] pattern = {0, 1000, 500, 1000, 500, 1000, 500};
//                 vibrator.vibrate(pattern, 0); // 0 = repeat indefinitely
                
//                 // Save that vibration is active
//                 SharedPreferences prefs = context.getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
//                 prefs.edit().putBoolean("vibrationActive", true).apply();
                
//                 Log.d(TAG, "Continuous vibration started");
//             }
//         } catch (Exception e) {
//             Log.e(TAG, "Error starting vibration", e);
//         }
//     }

//     private void startContinuousAlarm(Context context) {
//         try {
//             // Start alarm service
//             Intent alarmIntent = new Intent(context, AlarmService.class);
//             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                 context.startForegroundService(alarmIntent);
//             } else {
//                 context.startService(alarmIntent);
//             }
            
//             // Save that alarm is active
//             SharedPreferences prefs = context.getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
//             prefs.edit().putBoolean("alarmActive", true).apply();
            
//             Log.d(TAG, "Continuous alarm service started");
//         } catch (Exception e) {
//             Log.e(TAG, "Error starting alarm", e);
//         }
//     }

//     private CoordinateData extractCoordinates(String message) {
//         CoordinateData coords = new CoordinateData();
        
//         // Pattern 1: "Lat: XX.XXX, Lon: XX.XXX"
//         Pattern pattern1 = Pattern.compile("(?i)lat(?:itude)?[:\\s]+([\\-]?\\d+\\.\\d+)[,\\s]+lon(?:gitude)?[:\\s]+([\\-]?\\d+\\.\\d+)");
//         Matcher matcher1 = pattern1.matcher(message);
//         if (matcher1.find()) {
//             coords.latitude = matcher1.group(1);
//             coords.longitude = matcher1.group(2);
//             coords.fullText = message;
//             return coords;
//         }
        
//         // Pattern 2: Two decimal numbers separated by comma
//         Pattern pattern2 = Pattern.compile("([\\-]?\\d{1,2}\\.\\d{4,})[,\\s]+([\\-]?\\d{1,3}\\.\\d{4,})");
//         Matcher matcher2 = pattern2.matcher(message);
//         if (matcher2.find()) {
//             coords.latitude = matcher2.group(1);
//             coords.longitude = matcher2.group(2);
//             coords.fullText = message;
//             return coords;
//         }
        
//         // Pattern 3: Google Maps link
//         Pattern pattern3 = Pattern.compile("maps\\.google\\.com/maps\\?q=([\\-]?\\d+\\.\\d+),([\\-]?\\d+\\.\\d+)");
//         Matcher matcher3 = pattern3.matcher(message);
//         if (matcher3.find()) {
//             coords.latitude = matcher3.group(1);
//             coords.longitude = matcher3.group(2);
//             coords.fullText = message;
//             return coords;
//         }
        
//         return null;
//     }

//     private void saveEmergencyData(Context context, String sender, String message, 
//                                    CoordinateData coords, long timestamp) {
//         try {
//             SharedPreferences prefs = context.getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
//             SharedPreferences.Editor editor = prefs.edit();
            
//             JSONObject emergencyData = new JSONObject();
//             emergencyData.put("sender", sender);
//             emergencyData.put("message", message);
//             emergencyData.put("timestamp", timestamp);
            
//             if (coords != null) {
//                 emergencyData.put("latitude", coords.latitude);
//                 emergencyData.put("longitude", coords.longitude);
//                 emergencyData.put("hasCoordinates", true);
//             } else {
//                 emergencyData.put("hasCoordinates", false);
//             }
            
//             editor.putString("lastEmergency", emergencyData.toString());
//             editor.putBoolean("hasActiveEmergency", true);
//             editor.putLong("emergencyTimestamp", timestamp);
//             editor.apply();
            
//             Log.d(TAG, "Emergency data saved");
//         } catch (Exception e) {
//             Log.e(TAG, "Error saving emergency data", e);
//         }
//     }

//     private void showEmergencyNotification(Context context, String sender, String message, CoordinateData coords) {
//         try {
//             NotificationManager notificationManager = 
//                 (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

//             if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                 NotificationChannel channel = new NotificationChannel(
//                     CHANNEL_ID,
//                     "Emergency Alerts",
//                     NotificationManager.IMPORTANCE_HIGH
//                 );
//                 channel.setDescription("Critical emergency alerts");
//                 channel.enableVibration(true);
//                 notificationManager.createNotificationChannel(channel);
//             }

//             Intent notificationIntent = new Intent(context, MainActivity.class);
//             notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//             notificationIntent.putExtra("openEmergencyAlert", true);
            
//             if (coords != null) {
//                 notificationIntent.putExtra("latitude", coords.latitude);
//                 notificationIntent.putExtra("longitude", coords.longitude);
//             }
            
//             PendingIntent pendingIntent = PendingIntent.getActivity(
//                 context, 
//                 0, 
//                 notificationIntent, 
//                 PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
//             );

//             String title = "🚨 EMERGENCY ALERT 🚨";
//             String text = coords != null 
//                 ? "Emergency from " + sender + " with location!" 
//                 : "Emergency from " + sender;

//             NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                 .setSmallIcon(android.R.drawable.ic_dialog_alert)
//                 .setContentTitle(title)
//                 .setContentText(text)
//                 .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
//                 .setPriority(NotificationCompat.PRIORITY_MAX)
//                 .setCategory(NotificationCompat.CATEGORY_ALARM)
//                 .setAutoCancel(false)
//                 .setOngoing(true)
//                 .setContentIntent(pendingIntent);

//             notificationManager.notify(999, builder.build());
//             Log.d(TAG, "Emergency notification shown");
//         } catch (Exception e) {
//             Log.e(TAG, "Error showing notification", e);
//         }
//     }

//     private void launchEmergencyAlert(Context context, CoordinateData coords) {
//         try {
//             Intent launchIntent = new Intent(context, MainActivity.class);
//             launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
//                                  Intent.FLAG_ACTIVITY_CLEAR_TOP | 
//                                  Intent.FLAG_ACTIVITY_SINGLE_TOP);
//             launchIntent.putExtra("openEmergencyAlert", true);
            
//             if (coords != null) {
//                 launchIntent.putExtra("latitude", coords.latitude);
//                 launchIntent.putExtra("longitude", coords.longitude);
//             }
            
//             context.startActivity(launchIntent);
//             Log.d(TAG, "Emergency alert activity launched");
//         } catch (Exception e) {
//             Log.e(TAG, "Error launching activity", e);
//         }
//     }

//     private boolean isNumberMatch(String sender, String monitored) {
//         if (monitored == null || monitored.isEmpty()) {
//             return false;
//         }
        
//         String cleanSender = sender.replaceAll("[^0-9]", "");
//         String cleanMonitored = monitored.replaceAll("[^0-9]", "");
        
//         if (cleanSender.length() >= 10 && cleanMonitored.length() >= 10) {
//             String senderLast10 = cleanSender.substring(cleanSender.length() - 10);
//             String monitoredLast10 = cleanMonitored.substring(cleanMonitored.length() - 10);
//             return senderLast10.equals(monitoredLast10);
//         }
        
//         return cleanSender.equals(cleanMonitored);
//     }

//     private static class CoordinateData {
//         String latitude;
//         String longitude;
//         String fullText;
//     }
// }

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

                    if (isNumberMatch(sender, monitoredNumber)) {
                        
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
    // private void launchEmergencyAlert(Context context, CoordinateData coords) {
    //     try {
    //         Log.d(TAG, "launchEmergencyAlert: Starting...");
            
    //         Intent launchIntent = new Intent(context, MainActivity.class);
    //         launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //         launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //         launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    //         launchIntent.putExtra("openEmergencyAlert", true);
            
    //         if (coords != null) {
    //             launchIntent.putExtra("latitude", coords.latitude);
    //             launchIntent.putExtra("longitude", coords.longitude);
    //             Log.d(TAG, "launchEmergencyAlert: Added coordinates to intent");
    //         }
            
    //         context.startActivity(launchIntent);
            
    //         Log.d(TAG, "launchEmergencyAlert: Activity start command sent");
    //         Toast.makeText(context, "Opening app...", Toast.LENGTH_SHORT).show();
            
    //     } catch (Exception e) {
    //         Log.e(TAG, "launchEmergencyAlert: Error", e);
    //         Toast.makeText(context, "Error launching app: " + e.getMessage(), Toast.LENGTH_LONG).show();
    //     }
    // }

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

    private static class CoordinateData {
        String latitude;
        String longitude;
        String fullText;
    }
}