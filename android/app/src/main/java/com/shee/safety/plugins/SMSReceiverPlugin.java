package com.shee.safety.plugins;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import android.os.Build;
import android.net.Uri;
import android.provider.Settings;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import android.content.Intent;
@CapacitorPlugin(
    name = "SMSReceiver",
    permissions = {
        @Permission(
            alias = "sms",
            strings = { Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS }
        ),
        @Permission(
            alias = "vibrate",
            strings = { Manifest.permission.VIBRATE }
        )
    }
)
public class SMSReceiverPlugin extends Plugin {

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        if (!hasPermissions()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            JSObject ret = new JSObject();
            ret.put("sms", "granted");
            call.resolve(ret);
        }
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (!hasPermissions()) {
            requestAllPermissions(call, "permissionCallback");
        } else {
            JSObject ret = new JSObject();
            ret.put("sms", "granted");
            call.resolve(ret);
        }
    }

    @PermissionCallback
    private void permissionCallback(PluginCall call) {
        JSObject ret = new JSObject();
        
        if (hasPermissions()) {
            ret.put("sms", "granted");
        } else {
            ret.put("sms", "denied");
        }
        
        call.resolve(ret);
    }

    @PluginMethod
    public void startListening(PluginCall call) {
        if (!hasPermissions()) {
            call.reject("Permission not granted");
            return;
        }

        String phoneNumber = call.getString("phoneNumber", "");
        
        getContext().getSharedPreferences("SMSReceiver", 0)
            .edit()
            .putString("monitoredNumber", phoneNumber)
            .putBoolean("isListening", true)
            .apply();

        JSObject ret = new JSObject();
        ret.put("success", true);
        ret.put("message", "Started listening for SMS from " + phoneNumber);
        call.resolve(ret);
    }

    @PluginMethod
    public void stopListening(PluginCall call) {
        getContext().getSharedPreferences("SMSReceiver", 0)
            .edit()
            .putBoolean("isListening", false)
            .apply();

        JSObject ret = new JSObject();
        ret.put("success", true);
        ret.put("message", "Stopped listening for SMS");
        call.resolve(ret);
    }

    @PluginMethod
    public void setMonitoredNumber(PluginCall call) {
        String phoneNumber = call.getString("phoneNumber", "");
        
        getContext().getSharedPreferences("SMSReceiver", 0)
            .edit()
            .putString("monitoredNumber", phoneNumber)
            .apply();

        JSObject ret = new JSObject();
        ret.put("success", true);
        ret.put("phoneNumber", phoneNumber);
        call.resolve(ret);
    }

    @PluginMethod
    public void getMonitoredNumber(PluginCall call) {
        String phoneNumber = getContext().getSharedPreferences("SMSReceiver", 0)
            .getString("monitoredNumber", "");

        JSObject ret = new JSObject();
        ret.put("phoneNumber", phoneNumber);
        call.resolve(ret);
    }

    @PluginMethod
public void requestOverlayPermission(PluginCall call) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!Settings.canDrawOverlays(getContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getContext().getPackageName()));
            getActivity().startActivityForResult(intent, 1234);
            call.resolve();
        } else {
            call.resolve();
        }
    }
}

    public boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(
            getContext(),
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED &&
        ContextCompat.checkSelfPermission(
            getContext(),
            Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED;
    }
}