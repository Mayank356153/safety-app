package com.shee.safety.plugins;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.shee.safety.receivers.AlarmService;

import org.json.JSONObject;

@CapacitorPlugin(name = "EmergencyAlert")
public class EmergencyAlertPlugin extends Plugin {
    private static final String EMERGENCY_PREFS = "EmergencyAlerts";
    private Vibrator vibrator;

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
    public void clearEmergency(PluginCall call) {
        try {
            SharedPreferences prefs = getContext().getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                .putBoolean("hasActiveEmergency", false)
                .putBoolean("alarmActive", false)
                .putBoolean("vibrationActive", false)
                .apply();
            
            // Stop alarm and vibration
            stopAlarmAndVibration();
            
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error clearing emergency", e);
        }
    }

    @PluginMethod
    public void stopAlarm(PluginCall call) {
        try {
            stopAlarmAndVibration();
            
            SharedPreferences prefs = getContext().getSharedPreferences(EMERGENCY_PREFS, Context.MODE_PRIVATE);
            prefs.edit()
                .putBoolean("alarmActive", false)
                .putBoolean("vibrationActive", false)
                .apply();
            
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error stopping alarm", e);
        }
    }

    private void stopAlarmAndVibration() {
        try {
            // Stop alarm service
            Intent alarmIntent = new Intent(getContext(), AlarmService.class);
            getContext().stopService(alarmIntent);
            
            // Stop vibration
            if (vibrator == null) {
                vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            }
            if (vibrator != null) {
                vibrator.cancel();
            }
        } catch (Exception e) {
            // Ignore
        }
    }
}