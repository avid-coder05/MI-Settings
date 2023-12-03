package com.android.settings.wifi;

import android.content.Context;
import android.net.wifi.SoftApConfiguration;
import android.net.wifi.WifiManager;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;

/* loaded from: classes2.dex */
public class WifiTetherAutoOffController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause {
    private Context mContext;
    private SoftApConfiguration mSoftApConfig;
    private CheckBoxPreference mTetherAutoDisable;
    private WifiManager mWifiManager;

    public WifiTetherAutoOffController(Context context, Lifecycle lifecycle, Preference preference) {
        this.mContext = context;
        WifiManager wifiManager = (WifiManager) context.getSystemService("wifi");
        this.mWifiManager = wifiManager;
        this.mSoftApConfig = wifiManager.getSoftApConfiguration();
        this.mTetherAutoDisable = (CheckBoxPreference) preference;
        lifecycle.addObserver(this);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        CheckBoxPreference checkBoxPreference = this.mTetherAutoDisable;
        if (checkBoxPreference != null) {
            checkBoxPreference.setOnPreferenceChangeListener(null);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SoftApConfiguration build = new SoftApConfiguration.Builder(this.mWifiManager.getSoftApConfiguration()).setAutoShutdownEnabled(((Boolean) obj).booleanValue()).build();
        this.mSoftApConfig = build;
        this.mWifiManager.setSoftApConfiguration(build);
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mTetherAutoDisable != null) {
            updateState();
            this.mTetherAutoDisable.setOnPreferenceChangeListener(this);
        }
    }

    public void updateState() {
        this.mTetherAutoDisable.setChecked(this.mSoftApConfig.isAutoShutdownEnabled());
    }
}
