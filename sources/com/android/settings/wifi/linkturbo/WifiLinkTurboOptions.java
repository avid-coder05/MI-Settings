package com.android.settings.wifi.linkturbo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import com.android.settingslib.wifi.SlaveWifiUtils;
import miuix.preference.RadioButtonPreference;

/* loaded from: classes2.dex */
public class WifiLinkTurboOptions extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private Context mContext;
    private RadioButtonPreference mCustomize;
    private Preference mDualSim;
    private ValuePreference mDualWifi;
    private CheckBoxPreference mEnableWifiLinkTurbo;
    private String[] mListSummaries;
    private String[] mListValues;
    private FragmentListener mListener;
    private DropDownPreference mMode;
    private PreferenceCategory mOptionsCategory;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settings.wifi.linkturbo.WifiLinkTurboOptions.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action) || "android.net.wifi.WIFI_SLAVE_STATE_CHANGED".equals(action)) {
                WifiLinkTurboOptions.this.updateDualWifiPref();
            } else if ("android.intent.action.SIM_STATE_CHANGED".equals(action) || "android.telephony.action.CARRIER_CONFIG_CHANGED".equals(action) || "android.intent.action.AIRPLANE_MODE".equals(action)) {
                WifiLinkTurboOptions.this.updateDualSimPref();
            }
        }
    };
    private RadioButtonPreference mRecommendation;
    private WifiManager mWifiManager;

    /* loaded from: classes2.dex */
    public interface FragmentListener {
        void enableWifiLinkTurboCallback(boolean z);

        void setLinkTurboOptionsCallback(int i);
    }

    private void initDualWifiAndDualSim() {
        ValuePreference valuePreference = (ValuePreference) findPreference("dual_wifi");
        this.mDualWifi = valuePreference;
        if (valuePreference != null) {
            if (SlaveWifiUtils.getInstance(this.mContext).isUiVisible(this.mContext)) {
                this.mDualWifi.setShowRightArrow(true);
            } else {
                getPreferenceScreen().removePreference(this.mDualWifi);
                this.mDualWifi = null;
            }
        }
        Preference findPreference = findPreference("button_smart_dual_sim_key");
        this.mDualSim = findPreference;
        if (findPreference != null && LinkTurboUtils.shouldHideSmartDualSimButton(this.mContext)) {
            getPreferenceScreen().removePreference(this.mDualSim);
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.WIFI_SLAVE_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.SIM_STATE_CHANGED");
        intentFilter.addAction("android.telephony.action.CARRIER_CONFIG_CHANGED");
        intentFilter.addAction("android.intent.action.AIRPLANE_MODE");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    private void showOrHideOption(boolean z) {
        if (this.mOptionsCategory != null) {
            if (!z) {
                getPreferenceScreen().removePreference(this.mOptionsCategory);
                getPreferenceScreen().removePreference(this.mMode);
                return;
            }
            getPreferenceScreen().addPreference(this.mOptionsCategory);
            getPreferenceScreen().addPreference(this.mMode);
            updateSwitchAndModeSummary();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDualSimPref() {
        Context context = this.mContext;
        if (context == null || this.mDualSim == null) {
            return;
        }
        if (LinkTurboUtils.shouldHideSmartDualSimButton(context)) {
            getPreferenceScreen().removePreference(this.mDualSim);
        } else {
            getPreferenceScreen().addPreference(this.mDualSim);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDualWifiPref() {
        if (this.mContext == null || this.mDualWifi == null) {
            return;
        }
        WifiManager wifiManager = this.mWifiManager;
        this.mDualWifi.setEnabled(wifiManager == null ? false : wifiManager.isWifiEnabled());
        this.mDualWifi.setValue(SlaveWifiUtils.getInstance(this.mContext).isSlaveWifiEnabled() ? R.string.dual_wifi_on : R.string.dual_wifi_off);
    }

    private void updateOptions() {
        int linkTurboOptions = LinkTurboUtils.getLinkTurboOptions(this.mContext);
        if (linkTurboOptions == 0) {
            this.mRecommendation.setChecked(true);
            this.mCustomize.setChecked(false);
        } else if (linkTurboOptions != 1) {
        } else {
            this.mRecommendation.setChecked(false);
            this.mCustomize.setChecked(true);
        }
    }

    private void updateSwitchAndModeSummary() {
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "link_turbo_mode", 0);
        this.mMode.setValue(this.mListValues[i]);
        this.mMode.setSummary(this.mListSummaries[i]);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R.xml.wifi_link_turbo_option);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mWifiManager = (WifiManager) activity.getSystemService("wifi");
        this.mEnableWifiLinkTurbo = (CheckBoxPreference) findPreference("enable_wifi_link_turbo");
        this.mOptionsCategory = (PreferenceCategory) findPreference("wifi_link_turbo_options");
        this.mRecommendation = (RadioButtonPreference) findPreference("wifi_link_turbo_recommendation");
        this.mCustomize = (RadioButtonPreference) findPreference("wifi_link_turbo_customize");
        this.mEnableWifiLinkTurbo.setOnPreferenceChangeListener(this);
        Context context = this.mContext;
        if (context instanceof FragmentListener) {
            this.mListener = (FragmentListener) context;
        }
        this.mListValues = context.getResources().getStringArray(R.array.wifi_link_turbo_mode_values);
        this.mListSummaries = this.mContext.getResources().getStringArray(R.array.wifi_link_turbo_mode_summary_entries);
        DropDownPreference dropDownPreference = (DropDownPreference) findPreference("wifi_link_turbo_mode");
        this.mMode = dropDownPreference;
        dropDownPreference.setOnPreferenceChangeListener(this);
        if (!this.mEnableWifiLinkTurbo.isChecked()) {
            if (this.mOptionsCategory != null) {
                getPreferenceScreen().removePreference(this.mOptionsCategory);
            }
            if (this.mMode != null) {
                getPreferenceScreen().removePreference(this.mMode);
            }
        }
        initDualWifiAndDualSim();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Context context = this.mContext;
        if (context != null) {
            context.unregisterReceiver(this.mReceiver);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference != null) {
            if (!"enable_wifi_link_turbo".equals(preference.getKey())) {
                if ("wifi_link_turbo_mode".equals(preference.getKey())) {
                    Settings.System.putInt(this.mContext.getContentResolver(), "link_turbo_mode", Integer.valueOf((String) obj).intValue());
                    updateSwitchAndModeSummary();
                    return false;
                }
                return false;
            }
            boolean booleanValue = ((Boolean) obj).booleanValue();
            showOrHideOption(booleanValue);
            if (booleanValue) {
                updateOptions();
            }
            FragmentListener fragmentListener = this.mListener;
            if (fragmentListener != null) {
                fragmentListener.enableWifiLinkTurboCallback(booleanValue);
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference != null && "wifi_link_turbo_recommendation".equals(preference.getKey())) {
            LinkTurboUtils.setLinkTurboOptions(this.mContext, 0);
            FragmentListener fragmentListener = this.mListener;
            if (fragmentListener != null) {
                fragmentListener.setLinkTurboOptionsCallback(0);
            }
            return true;
        } else if (preference != null && "wifi_link_turbo_customize".equals(preference.getKey())) {
            LinkTurboUtils.setLinkTurboOptions(this.mContext, 1);
            FragmentListener fragmentListener2 = this.mListener;
            if (fragmentListener2 != null) {
                fragmentListener2.setLinkTurboOptionsCallback(1);
            }
            return true;
        } else if (preference == this.mDualSim) {
            Intent intent = new Intent();
            intent.addFlags(131072);
            intent.setClassName("com.android.phone", "com.android.phone.settings.MiuiConfigureMobileSettings");
            intent.putExtra("extra_from", "com.android.phone");
            startActivity(intent);
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        boolean isWifiLinkTurboEnabled = ((WifiLinkTurboSettings) this.mContext).isWifiLinkTurboEnabled();
        this.mEnableWifiLinkTurbo.setChecked(isWifiLinkTurboEnabled);
        if (isWifiLinkTurboEnabled) {
            updateSwitchAndModeSummary();
            updateOptions();
        } else {
            if (this.mOptionsCategory != null) {
                getPreferenceScreen().removePreference(this.mOptionsCategory);
            }
            if (this.mMode != null) {
                getPreferenceScreen().removePreference(this.mMode);
            }
        }
        updateDualWifiPref();
        updateDualSimPref();
    }
}
