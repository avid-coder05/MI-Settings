package com.android.settings.wifi;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;
import com.android.settings.R;
import java.util.List;
import miuix.appcompat.app.AppCompatActivity;

/* loaded from: classes2.dex */
public class WifiConfigInfo extends AppCompatActivity {
    private TextView mConfigList;
    private WifiManager mWifiManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // miuix.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mWifiManager = (WifiManager) getSystemService("wifi");
        setContentView(R.layout.wifi_config_info);
        this.mConfigList = (TextView) findViewById(R.id.config_list);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        if (!this.mWifiManager.isWifiEnabled()) {
            this.mConfigList.setText(R.string.wifi_state_disabled);
            return;
        }
        List<WifiConfiguration> configuredNetworks = this.mWifiManager.getConfiguredNetworks();
        StringBuffer stringBuffer = new StringBuffer();
        for (int size = configuredNetworks.size() - 1; size >= 0; size--) {
            stringBuffer.append(configuredNetworks.get(size));
        }
        this.mConfigList.setText(stringBuffer);
    }
}
