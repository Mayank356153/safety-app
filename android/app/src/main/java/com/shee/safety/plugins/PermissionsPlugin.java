package com.shee.safety.plugins;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(
    name = "Permissions",
    permissions = {
        @Permission(
            strings = { 
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.VIBRATE,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            },
            alias = "all"
        )
    }
)
public class PermissionsPlugin extends Plugin {

    @PluginMethod
    public void checkAllPermissions(PluginCall call) {
        JSObject ret = new JSObject();
        
        ret.put("sms", hasSMSPermissions());
        ret.put("notifications", hasNotificationPermission());
        ret.put("overlay", hasOverlayPermission());
        ret.put("vibrate", hasVibratePermission());
        ret.put("wakelock", true); // Wake lock doesn't need runtime permission
        ret.put("audio", true); // Modify audio doesn't need runtime permission
        
        call.resolve(ret);
    }

    @PluginMethod
    public void requestAllPermissions(PluginCall call) {
        if (!hasSMSPermissions() || !hasNotificationPermission() || !hasVibratePermission()) {
            requestPermissionForAlias("all", call, "permissionCallback");
        } else {
            JSObject ret = new JSObject();
            ret.put("granted", true);
            call.resolve(ret);
        }
    }

    @PermissionCallback
    private void permissionCallback(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("sms", hasSMSPermissions());
        ret.put("notifications", hasNotificationPermission());
        ret.put("vibrate", hasVibratePermission());
        ret.put("overlay", hasOverlayPermission());
        call.resolve(ret);
    }

    @PluginMethod
    public void checkOverlayPermission(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("granted", hasOverlayPermission());
        call.resolve(ret);
    }

    @PluginMethod
    public void requestOverlayPermission(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getContext().getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
                
                JSObject ret = new JSObject();
                ret.put("success", true);
                ret.put("message", "Opening overlay permission settings");
                call.resolve(ret);
            } else {
                JSObject ret = new JSObject();
                ret.put("granted", true);
                call.resolve(ret);
            }
        } else {
            JSObject ret = new JSObject();
            ret.put("granted", true);
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void openAppSettings(PluginCall call) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Error opening settings", e);
        }
    }

    @PluginMethod
    public void checkNotificationPermission(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("granted", hasNotificationPermission());
        call.resolve(ret);
    }

    @PluginMethod
    public void requestNotificationPermission(PluginCall call) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasNotificationPermission()) {
                requestPermissionForAlias("all", call, "notificationCallback");
            } else {
                JSObject ret = new JSObject();
                ret.put("granted", true);
                call.resolve(ret);
            }
        } else {
            JSObject ret = new JSObject();
            ret.put("granted", true);
            call.resolve(ret);
        }
    }

    @PermissionCallback
    private void notificationCallback(PluginCall call) {
        JSObject ret = new JSObject();
        ret.put("granted", hasNotificationPermission());
        call.resolve(ret);
    }

    private boolean hasSMSPermissions() {
        return ContextCompat.checkSelfPermission(
            getContext(),
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            getContext(),
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Not needed for older versions
    }

    private boolean hasOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(getContext());
        }
        return true; // Not needed for older versions
    }

    private boolean hasVibratePermission() {
        return ContextCompat.checkSelfPermission(
            getContext(),
            Manifest.permission.VIBRATE
        ) == PackageManager.PERMISSION_GRANTED;
    }
}