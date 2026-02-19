package com.shee.safety;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
public class AlertActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Open Alerts page in React
        getBridge().getWebView()
                .loadUrl("https://localhost/alerts");
    }
}
