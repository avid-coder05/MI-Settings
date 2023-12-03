package com.android.settings.wifi.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.util.ToastUtil;
import com.android.settingslib.wifi.SlaveWifiUtils;

/* loaded from: classes2.dex */
public class WifiP2pPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnPause, OnResume {
    private final IntentFilter mFilter;
    private final IntentFilter mLocationFilter;
    private final LocationManager mLocationManager;
    final BroadcastReceiver mLocationReceiver;
    final BroadcastReceiver mReceiver;
    private Preference mWifiDirectPref;
    private Preference.OnPreferenceClickListener mWifiDirectPrefClickListner;
    private final WifiManager mWifiManager;

    public WifiP2pPreferenceController(Context context, Lifecycle lifecycle, WifiManager wifiManager) {
        super(context);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.p2p.WifiP2pPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                WifiP2pPreferenceController.this.togglePreferences();
            }
        };
        this.mFilter = new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED");
        this.mLocationReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.p2p.WifiP2pPreferenceController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (WifiP2pPreferenceController.this.mWifiDirectPref != null) {
                    WifiP2pPreferenceController wifiP2pPreferenceController = WifiP2pPreferenceController.this;
                    wifiP2pPreferenceController.updateState(wifiP2pPreferenceController.mWifiDirectPref);
                }
            }
        };
        this.mLocationFilter = new IntentFilter("android.location.MODE_CHANGED");
        this.mWifiDirectPrefClickListner = new Preference.OnPreferenceClickListener() { // from class: com.android.settings.wifi.p2p.WifiP2pPreferenceController.3
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public boolean onPreferenceClick(Preference preference) {
                if (WifiP2pPreferenceController.this.isHotSpotOn()) {
                    ToastUtil.show(((AbstractPreferenceController) WifiP2pPreferenceController.this).mContext, R.string.wifi_direct_close_hotspot_hint, 1);
                    return true;
                } else if (new SlaveWifiUtils(((AbstractPreferenceController) WifiP2pPreferenceController.this).mContext).isSlaveWifiEnabled()) {
                    ToastUtil.show(((AbstractPreferenceController) WifiP2pPreferenceController.this).mContext, R.string.wifi_direct_close_slave_wifi_hint, 1);
                    return true;
                } else {
                    ((AbstractPreferenceController) WifiP2pPreferenceController.this).mContext.startActivity(new Intent().setClassName("com.android.settings", "com.android.settings.Settings$WifiP2pSettingsActivity"));
                    return true;
                }
            }
        };
        this.mWifiManager = wifiManager;
        lifecycle.addObserver(this);
        this.mLocationManager = (LocationManager) context.getSystemService("location");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isHotSpotOn() {
        int wifiApState = this.mWifiManager.getWifiApState();
        return wifiApState == 12 || wifiApState == 13;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void togglePreferences() {
        Preference preference = this.mWifiDirectPref;
        if (preference != null) {
            preference.setEnabled(this.mWifiManager.isWifiEnabled() && this.mLocationManager.isLocationEnabled());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("wifi_direct");
        this.mWifiDirectPref = findPreference;
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(this.mWifiDirectPrefClickListner);
        }
        togglePreferences();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "wifi_direct";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mContext.unregisterReceiver(this.mLocationReceiver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        this.mContext.registerReceiver(this.mReceiver, this.mFilter);
        this.mContext.registerReceiver(this.mLocationReceiver, this.mLocationFilter);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(this.mLocationManager.isLocationEnabled() && this.mWifiManager.isWifiEnabled());
    }
}
