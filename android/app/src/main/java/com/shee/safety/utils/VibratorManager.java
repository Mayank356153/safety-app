package com.shee.safety.utils;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

public class VibratorManager {
    private static final String TAG = "VibratorManager";
    private static VibratorManager instance;
    private Vibrator vibrator;
    private boolean isVibrating = false;

    private VibratorManager() {}

    public static synchronized VibratorManager getInstance() {
        if (instance == null) {
            instance = new VibratorManager();
        }
        return instance;
    }

    public void startVibration(Context context) {
        try {
            if (isVibrating) {
                Log.d(TAG, "Already vibrating, stopping first...");
                stopVibration();
            }

            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            
            if (vibrator != null && vibrator.hasVibrator()) {
                long[] pattern = {0, 1000, 500};
                vibrator.vibrate(pattern, 0);
                isVibrating = true;
                Log.d(TAG, "✅ Vibration started");
            } else {
                Log.e(TAG, "❌ No vibrator available");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting vibration", e);
        }
    }

    public void stopVibration() {
        try {
            Log.d(TAG, "========================================");
            Log.d(TAG, "STOPPING VIBRATION");
            Log.d(TAG, "Is vibrating: " + isVibrating);
            Log.d(TAG, "Vibrator null: " + (vibrator == null));
            
            if (vibrator != null) {
                vibrator.cancel();
                Log.d(TAG, "✅ Vibrator.cancel() called");
            }
            
            vibrator = null;
            isVibrating = false;
            
            Log.d(TAG, "✅ Vibration stopped");
            Log.d(TAG, "========================================");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error stopping vibration", e);
            e.printStackTrace();
        }
    }

    public boolean isVibrating() {
        return isVibrating;
    }
}