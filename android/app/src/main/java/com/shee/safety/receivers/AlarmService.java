package com.shee.safety.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.shee.safety.utils.VibratorManager;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private static final String CHANNEL_ID = "alarm_service";
    
    private MediaPlayer mediaPlayer;
    private boolean isRunning = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning) {
            Log.d(TAG, "Alarm already running, skipping...");
            return START_STICKY;
        }
        
        Log.d(TAG, "========================================");
        Log.d(TAG, "ALARM SERVICE STARTED");
        Log.d(TAG, "========================================");
        
        isRunning = true;
        
        createNotificationChannel();
        startForeground(1001, createNotification());
        
        startContinuousAlarm();
        startContinuousVibration();
        
        return START_STICKY;
    }

    private void startContinuousAlarm() {
        try {
            if (mediaPlayer == null) {
                Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, alarmUri);

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
                mediaPlayer.setAudioAttributes(audioAttributes);

                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(1.0f, 1.0f);
                mediaPlayer.prepare();
                mediaPlayer.start();

                Log.d(TAG, "✅ Continuous alarm started");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting alarm", e);
        }
    }

    private void startContinuousVibration() {
        // Use singleton vibrator manager
        VibratorManager.getInstance().startVibration(this);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "========================================");
        Log.d(TAG, "STOPPING ALARM SERVICE");
        Log.d(TAG, "========================================");
        
        isRunning = false;
        
        // Stop media player
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "✅ Media player stopped");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping media player", e);
            }
        }
        
        // Stop vibration using singleton manager
        VibratorManager.getInstance().stopVibration();
        
        Log.d(TAG, "========================================");
        
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Emergency Alarm",
                NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🚨 Emergency Alarm Active")
            .setContentText("Tap to stop alarm")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}