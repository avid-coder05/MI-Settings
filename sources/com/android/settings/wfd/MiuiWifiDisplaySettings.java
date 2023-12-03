package com.android.settings.wfd;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.MiuiUtils;
import com.android.settings.R;
import com.android.settingslib.util.ToastUtil;
import miuix.appcompat.app.AlertDialog;

/* loaded from: classes2.dex */
public class MiuiWifiDisplaySettings extends WifiDisplaySettings {
    /* JADX INFO: Access modifiers changed from: private */
    public void enableWifiDisplay(boolean z) {
        this.mWifiDisplayOnSetting = z;
        Settings.Global.putInt(getContentResolver(), "wifi_display_on", this.mWifiDisplayOnSetting ? 1 : 0);
        ((CheckBoxPreference) findPreference("enable_wifi_display")).setChecked(this.mWifiDisplayOnSetting);
    }

    private void isWfdAvailable(Activity activity) {
        boolean isWifiApEnabled = this.mWifiManager.isWifiApEnabled();
        if (((ConnectivityManager) activity.getSystemService("connectivity")).getNetworkInfo(1).isConnected() && isWifiApEnabled) {
            ToastUtil.show(activity, R.string.toast_sta_ap_works_wifi_display_disable, 0);
        }
    }

    private void renameDevice() {
        WifiP2pManager wifiP2pManager = (WifiP2pManager) getSystemService("wifip2p");
        if (wifiP2pManager != null) {
            wifiP2pManager.setDeviceName(wifiP2pManager.initialize(getActivity(), getActivity().getMainLooper(), null), MiuiUtils.getP2pDeviceName(getActivity()), null);
        }
    }

    private void updateWifiDialog() {
        if (!this.mWifiDisplayOnSetting) {
            removeDialog(10000);
        } else if (this.mWifiManager.isWifiEnabled() || isDialogShowing(10000)) {
        } else {
            showDialog(10000);
            enableWifiDisplay(false);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    public String getName() {
        return MiuiWifiDisplaySettings.class.getName();
    }

    @Override // com.android.settings.wfd.WifiDisplaySettings, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        renameDevice();
        super.onCreate(bundle);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(int i) {
        return i != 10000 ? super.onCreateDialog(i) : new AlertDialog.Builder(getActivity()).setMessage(R.string.wfd_enable_wifi_diag_title).setPositiveButton(getString(R.string.dlg_ok), new DialogInterface.OnClickListener() { // from class: com.android.settings.wfd.MiuiWifiDisplaySettings.2
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                MiuiWifiDisplaySettings.this.mWifiManager.setWifiEnabled(true);
                MiuiWifiDisplaySettings.this.enableWifiDisplay(true);
            }
        }).setNegativeButton(getString(R.string.dlg_cancel), new DialogInterface.OnClickListener() { // from class: com.android.settings.wfd.MiuiWifiDisplaySettings.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialogInterface, int i2) {
                MiuiWifiDisplaySettings.this.enableWifiDisplay(false);
            }
        }).create();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
    }

    @Override // com.android.settings.wfd.WifiDisplaySettings, androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("enable_wifi_display".equals(preference.getKey())) {
            this.mWifiDisplayOnSetting = ((Boolean) obj).booleanValue();
            Settings.Global.putInt(getContentResolver(), "wifi_display_on", this.mWifiDisplayOnSetting ? 1 : 0);
            updateWifiDialog();
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.miuisettings.preference.PreferenceFragment, miuix.preference.PreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        isWfdAvailable(getActivity());
    }

    @Override // com.android.settings.wfd.WifiDisplaySettings, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        updateWifiDialog();
    }
}
