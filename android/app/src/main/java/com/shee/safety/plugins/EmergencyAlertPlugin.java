
package com.shee.safety.plugins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.shee.safety.utils.VibratorManager;
import org.json.JSONObject;
import org.json.JSONException;

@CapacitorPlugin(name = "EmergencyAlert")
public class EmergencyAlertPlugin extends Plugin {
    private static final String TAG = "EmergencyAlertPlugin";
    private static final String EMERGENCY_PREFS = "EmergencyAlerts";

     @PluginMethod
    public void getEmergencyData(PluginCall call) {
        try {
            SharedPreferences prefs = getContext().getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
            String emergencyJson = prefs.getString("lastEmergency", null);
            boolean hasActive = prefs.getBoolean("hasActiveEmergency", false);
            
            JSObject ret = new JSObject();
            
            if (emergencyJson != null && hasActive) {
                JSONObject data = new JSONObject(emergencyJson);
            
                ret.put("hasEmergency", true);
                ret.put("sender", data.optString("sender", ""));
                ret.put("message", data.optString("message", ""));
                ret.put("timestamp", data.optLong("timestamp", 0));
                ret.put("hasCoordinates", data.optBoolean("hasCoordinates", false));
                ret.put("latitude", data.optString("latitude", ""));
                ret.put("longitude", data.optString("longitude", ""));
                
            } else {
                ret.put("hasEmergency", false);
            }
            
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error getting emergency data", e);
        }
    }

    @PluginMethod
    public void stopAlarm(PluginCall call) {
        try {
            Log.d(TAG, "");
            Log.d(TAG, "========================================");
            Log.d(TAG, "STOPPING ALARM AND VIBRATION");
            Log.d(TAG, "========================================");
            
            // Stop alarm service
            Intent alarmIntent = new Intent(getContext(), com.shee.safety.receivers.AlarmService.class);
            getContext().stopService(alarmIntent);
            Log.d(TAG, "✅ Alarm service stop command sent");
            
            // Stop vibration using singleton manager
            VibratorManager.getInstance().stopVibration();
            
            Log.d(TAG, "========================================");
            Log.d(TAG, "");
            
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            Log.e(TAG, "Error stopping alarm", e);
            call.reject("Error stopping alarm", e);
        }
    }

    @PluginMethod
    public void clearEmergency(PluginCall call) {
        try {
            Log.d(TAG, "");
            Log.d(TAG, "========================================");
            Log.d(TAG, "CLEARING EMERGENCY DATA");
            Log.d(TAG, "========================================");
            
            // Stop alarm service
            Intent alarmIntent = new Intent(getContext(), com.shee.safety.receivers.AlarmService.class);
            getContext().stopService(alarmIntent);
            
            // Stop vibration
            VibratorManager.getInstance().stopVibration();
            
            // Small delay to ensure vibration stops
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            // Clear emergency data
            SharedPreferences prefs = getContext().getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
            prefs.edit().clear().apply();
            Log.d(TAG, "✅ Emergency data cleared");
            
            // Cancel notifications
            android.app.NotificationManager notificationManager = 
                (android.app.NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(999);
            notificationManager.cancel(1001);
            Log.d(TAG, "✅ Notifications cancelled");
            
            Log.d(TAG, "========================================");
            Log.d(TAG, "");
            
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            Log.e(TAG, "Error clearing emergency", e);
            call.reject("Error clearing emergency", e);
        }
    }
}