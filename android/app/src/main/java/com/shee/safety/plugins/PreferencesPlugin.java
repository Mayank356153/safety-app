package com.shee.safety.plugins;

import android.content.Context;
import android.content.SharedPreferences;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "AppPreferences")
public class PreferencesPlugin extends Plugin {

    @PluginMethod
    public void saveLocationSettings(PluginCall call) {
        try {
            String userId = call.getString("userId");
            String serverUrl = call.getString("serverUrl");

            SharedPreferences prefs = getContext().getSharedPreferences("LocationSettings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            
            editor.putString("userId", userId);
            editor.putString("serverUrl", serverUrl);
            editor.apply();

            android.util.Log.d("PreferencesPlugin", "✅ Saved - User ID: " + userId + ", Server: " + serverUrl);

            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error saving preferences", e);
        }
    }

    @PluginMethod
    public void getLocationSettings(PluginCall call) {
        try {
            SharedPreferences prefs = getContext().getSharedPreferences("LocationSettings", Context.MODE_PRIVATE);
            
            String userId = prefs.getString("userId", "");
            String serverUrl = prefs.getString("serverUrl", "");

            JSObject ret = new JSObject();
            ret.put("userId", userId);
            ret.put("serverUrl", serverUrl);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error getting preferences", e);
        }
    }
}