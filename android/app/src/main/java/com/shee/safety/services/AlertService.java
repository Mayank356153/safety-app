package com.shee.safety.services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.*;
import android.widget.TextView;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class AlertService extends Service {

    private WindowManager windowManager;
    private View overlayView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new NotificationCompat.Builder(this, "sms_alerts")
                    .setContentTitle("Alert Active")
                    .setSmallIcon(android.R.drawable.ic_dialog_alert)
                    .build();
            startForeground(2, notification);
        }

        showOverlay(intent.getStringExtra("lat"), intent.getStringExtra("lon"));
        return START_NOT_STICKY;
    }

    private void showOverlay(String lat, String lon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                !android.provider.Settings.canDrawOverlays(this)) return;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        overlayView = LayoutInflater.from(this).inflate(
                android.R.layout.simple_list_item_2,
                null
        );

        TextView text1 = overlayView.findViewById(android.R.id.text1);
        TextView text2 = overlayView.findViewById(android.R.id.text2);

        text1.setText("🚨 EMERGENCY ALERT");
        text2.setText("Lat: " + lat + " | Lon: " + lon);

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP;
        windowManager.addView(overlayView, params);
    }

    @Override
    public void onDestroy() {
        if (overlayView != null) windowManager.removeView(overlayView);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
