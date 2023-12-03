package com.android.settings.settingspanel;

import android.os.Bundle;
import android.view.View;
import androidx.preference.Preference;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R;
import com.android.settings.widget.CustomCheckBoxPreference;
import com.android.settings.wifi.MiuiWifiSettings;
import miuix.springback.view.SpringBackLayout;

/* loaded from: classes2.dex */
public class WifiSettingsPanelFragment extends MiuiWifiSettings {
    private WifiStateChangeListener mListener;

    /* loaded from: classes2.dex */
    interface WifiStateChangeListener {
        void onWifiStateChanged(int i);
    }

    private void initView() {
        Preference findPreference = findPreference("wifi_settings");
        if (findPreference != null) {
            getPreferenceScreen().removePreference(findPreference);
        }
        Preference findPreference2 = findPreference("wifi_assist");
        if (findPreference2 != null) {
            getPreferenceScreen().removePreference(findPreference2);
        }
        ((CustomCheckBoxPreference) this.mWifiEnablePreference).setIsDialogStyle(true);
    }

    @Override // com.android.settings.wifi.MiuiWifiSettings, com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        initView();
    }

    @Override // com.android.settings.wifi.MiuiWifiSettings, com.android.settings.network.NetworkProviderSettings, com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setThemeRes(R.style.Theme_Provision_Notitle_WifiSettings);
    }

    @Override // com.android.settings.wifi.MiuiWifiSettings, com.android.settings.network.NetworkProviderSettings, com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        RecyclerView listView = getListView();
        listView.setPadding(listView.getPaddingLeft(), 0, listView.getPaddingRight(), 0);
        View view2 = (View) listView.getParent();
        if (view2 instanceof SpringBackLayout) {
            view2.setEnabled(false);
        }
    }

    @Override // com.android.settings.wifi.MiuiWifiSettings, com.android.settings.network.NetworkProviderSettings, com.android.wifitrackerlib.BaseWifiTracker.BaseWifiTrackerCallback
    /* renamed from: onWifiStateChanged */
    public void lambda$onInternetTypeChanged$4() {
        super.lambda$onInternetTypeChanged$4();
        this.mWifiEnablePreference.setSummary((CharSequence) null);
        int wifiState = this.mWifiPickerTracker.getWifiState();
        WifiStateChangeListener wifiStateChangeListener = this.mListener;
        if (wifiStateChangeListener != null) {
            wifiStateChangeListener.onWifiStateChanged(wifiState == 1 ? 2 : 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerStateListener(WifiStateChangeListener wifiStateChangeListener) {
        this.mListener = wifiStateChangeListener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.wifi.MiuiWifiSettings, com.android.settings.network.NetworkProviderSettings
    public void updateWifiEntryPreferences() {
        super.updateWifiEntryPreferences();
        Preference findPreference = this.mConnectedWifiEntryPreferenceCategory.findPreference("manually_add_network");
        if (findPreference != null) {
            this.mConnectedWifiEntryPreferenceCategory.removePreference(findPreference);
        }
    }
}
