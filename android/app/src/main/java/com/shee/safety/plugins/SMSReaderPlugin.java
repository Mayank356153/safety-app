package com.shee.safety.plugins;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import androidx.core.content.ContextCompat;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

@CapacitorPlugin(
    name = "SMSReader",
    permissions = {
        @Permission(
            strings = { 
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS 
            },
            alias = "sms"
        )
    }
)
public class SMSReaderPlugin extends Plugin {

    @PluginMethod
    public void checkPermissions(PluginCall call) {
        JSObject ret = new JSObject();
        
        if (hasRequiredPermissions()) {
            ret.put("sms", "granted");
        } else {
            ret.put("sms", "denied");
        }
        
        call.resolve(ret);
    }

    @PluginMethod
    public void requestPermissions(PluginCall call) {
        if (hasRequiredPermissions()) {
            JSObject ret = new JSObject();
            ret.put("sms", "granted");
            call.resolve(ret);
        } else {
            requestPermissionForAlias("sms", call, "permissionCallback");
        }
    }

    @PermissionCallback
    private void permissionCallback(PluginCall call) {
        JSObject ret = new JSObject();
        
        if (hasRequiredPermissions()) {
            ret.put("sms", "granted");
        } else {
            ret.put("sms", "denied");
        }
        
        call.resolve(ret);
    }

    @PluginMethod
    public void getMessages(PluginCall call) {
        if (!hasRequiredPermissions()) {
            call.reject("Permission not granted");
            return;
        }

        String box = call.getString("box", "inbox");
        Integer maxCount = call.getInt("maxCount", 50);
        
        try {
            JSArray messages = new JSArray();
            Uri uri = Uri.parse("content://sms/" + box);
            String[] projection = new String[] { "_id", "address", "body", "date", "type" };
            
            Cursor cursor = getContext().getContentResolver().query(
                uri, 
                projection, 
                null, 
                null, 
                "date DESC LIMIT " + maxCount
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    JSObject sms = new JSObject();
                    sms.put("id", cursor.getString(0));
                    sms.put("address", cursor.getString(1));
                    sms.put("body", cursor.getString(2));
                    sms.put("date", cursor.getLong(3));
                    sms.put("type", cursor.getInt(4));
                    messages.put(sms);
                }
                cursor.close();
            }

            JSObject ret = new JSObject();
            ret.put("messages", messages);
            call.resolve(ret);
            
        } catch (Exception e) {
            call.reject("Error reading SMS: " + e.getMessage());
        }
    }
    @Override
    public boolean hasRequiredPermissions() {
        return ContextCompat.checkSelfPermission(
            getContext(),
            Manifest.permission.READ_SMS
        ) == PackageManager.PERMISSION_GRANTED;
    }
}