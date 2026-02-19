// package com.shee.safety;

// import android.content.Intent;
// import android.content.SharedPreferences;
// import android.os.Bundle;
// import android.util.Log;
// import com.getcapacitor.BridgeActivity;

// import com.shee.safety.plugins.SMSReaderPlugin;
// import com.shee.safety.plugins.SMSReceiverPlugin;
// import com.shee.safety.plugins.EmergencyAlertPlugin;

// public class MainActivity extends BridgeActivity {
//     private static final String TAG = "MainActivity";
//     private static final String EMERGENCY_PREFS = "EmergencyAlerts";

//     @Override
//     public void onCreate(Bundle savedInstanceState) {
//         registerPlugin(SMSReaderPlugin.class);
//         registerPlugin(SMSReceiverPlugin.class);
//         registerPlugin(EmergencyAlertPlugin.class);
        
//         super.onCreate(savedInstanceState);
        
//         Log.d(TAG, "MainActivity onCreate");
//     }

//     @Override
//     public void onResume() {
//         super.onResume();
//         Log.d(TAG, "MainActivity onResume");
        
//         // Check if there's an active emergency when app is opened
//         checkForActiveEmergency();
//     }

//     @Override
//     protected void onNewIntent(Intent intent) {
//         super.onNewIntent(intent);
//         Log.d(TAG, "MainActivity onNewIntent");
//         setIntent(intent);
        
//         // Check for emergency alert flag
//         if (intent.getBooleanExtra("openEmergencyAlert", false)) {
//             Log.d(TAG, "Emergency alert intent received!");
//             navigateToEmergencyAlert();
//         }
//     }

//     private void checkForActiveEmergency() {
//         try {
//             SharedPreferences prefs = getSharedPreferences(EMERGENCY_PREFS, MODE_PRIVATE);
//             boolean hasActiveEmergency = prefs.getBoolean("hasActiveEmergency", false);
            
//             if (hasActiveEmergency) {
//                 Log.d(TAG, "Active emergency detected! Navigating to alert page...");
//                 navigateToEmergencyAlert();
//             }
//         } catch (Exception e) {
//             Log.e(TAG, "Error checking for emergency", e);
//         }
//     }

//     private void navigateToEmergencyAlert() {
//         try {
//             // Use JavaScript to navigate in the web view
//             bridge.getWebView().post(() -> {
//                 bridge.getWebView().evaluateJavascript(
//                     "window.location.href = '/emergency-alert';",
//                     null
//                 );
//             });
//             Log.d(TAG, "Navigation command sent to WebView");
//         } catch (Exception e) {
//             Log.e(TAG, "Error navigating to emergency alert", e);
//         }
//     }
// }
package com.shee.safety;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import com.getcapacitor.BridgeActivity;

import com.shee.safety.plugins.SMSReaderPlugin;
import com.shee.safety.plugins.SMSReceiverPlugin;
import com.shee.safety.plugins.EmergencyAlertPlugin;
import com.shee.safety.plugins.PermissionsPlugin;
import com.shee.safety.plugins.PreferencesPlugin;
import com.shee.safety.plugins.LocationPlugin;  // ADD THIS

public class MainActivity extends BridgeActivity {
    private static final String TAG = "MainActivity";
    private static final String EMERGENCY_PREFS = "EmergencyAlerts";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(SMSReaderPlugin.class);
        registerPlugin(SMSReceiverPlugin.class);
        registerPlugin(EmergencyAlertPlugin.class);
        registerPlugin(PermissionsPlugin.class);
        registerPlugin(PreferencesPlugin.class);
        registerPlugin(LocationPlugin.class);  // ADD THIS
        
        super.onCreate(savedInstanceState);
        
        Log.d(TAG, "MainActivity onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity onResume");
        
        checkForActiveEmergency();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "MainActivity onNewIntent");
        setIntent(intent);
        
        if (intent.getBooleanExtra("openEmergencyAlert", false)) {
            Log.d(TAG, "Emergency alert intent received!");
            navigateToEmergencyAlert();
        }
    }

    private void checkForActiveEmergency() {
        try {
            SharedPreferences prefs = getSharedPreferences(EMERGENCY_PREFS, MODE_PRIVATE);
            boolean hasActiveEmergency = prefs.getBoolean("hasActiveEmergency", false);
            
            if (hasActiveEmergency) {
                Log.d(TAG, "Active emergency detected! Navigating to alert page...");
                navigateToEmergencyAlert();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking for emergency", e);
        }
    }

    private void navigateToEmergencyAlert() {
        try {
            bridge.getWebView().post(() -> {
                bridge.getWebView().evaluateJavascript(
                    "window.location.href = '/emergency-alert';",
                    null
                );
            });
            Log.d(TAG, "Navigation command sent to WebView");
        } catch (Exception e) {
            Log.e(TAG, "Error navigating to emergency alert", e);
        }
    }
}