package com.shee.safety.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {
    private static final String TAG = "AlarmService";
    private static final String CHANNEL_ID = "alarm_service_channel";
    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "======= AlarmService CREATED =======");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "======= AlarmService STARTED =======");
        
        // Create notification channel
        createNotificationChannel();
        
        // Start as foreground service (required for Android 8.0+)
        Notification notification = createNotification();
        startForeground(1001, notification);
        
        // Start the alarm sound
        startAlarmSound();
        
        return START_STICKY; // Restart if killed
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Emergency Alarm Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Keeps alarm running");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d(TAG, "Notification channel created");
            }
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🚨 Emergency Alarm Active")
            .setContentText("Alarm is sounding...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true);

        return builder.build();
    }

    private void startAlarmSound() {
        try {
            Log.d(TAG, "Starting alarm sound...");
            
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Try to get alarm sound
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                Log.d(TAG, "No alarm sound, trying ringtone");
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
            if (alarmUri == null) {
                Log.d(TAG, "No ringtone, trying notification");
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            if (alarmUri == null) {
                Log.e(TAG, "No sound URI found!");
                return;
            }

            Log.d(TAG, "Using sound URI: " + alarmUri);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(this, alarmUri);
            
            // Set to alarm stream with max volume
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
            mediaPlayer.setAudioAttributes(audioAttributes);
            
            // Set volume to maximum
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if (audioManager != null) {
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0);
                Log.d(TAG, "Set alarm volume to max: " + maxVolume);
            }
            
            mediaPlayer.setLooping(true); // Loop continuously
            mediaPlayer.prepare();
            mediaPlayer.start();
            
            Log.d(TAG, "✓✓✓ ALARM SOUND PLAYING (LOOPING) ✓✓✓");
            
        } catch (Exception e) {
            Log.e(TAG, "Error starting alarm sound", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "======= AlarmService DESTROYED =======");
        
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                Log.d(TAG, "MediaPlayer stopped and released");
            } catch (Exception e) {
                Log.e(TAG, "Error stopping MediaPlayer", e);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}