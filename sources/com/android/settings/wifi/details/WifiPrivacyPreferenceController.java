package com.android.settings.wifi.details;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.slices.SliceBackgroundWorker;
import com.android.settings.wifi.WifiDialog;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;

/* loaded from: classes2.dex */
public class WifiPrivacyPreferenceController extends BasePreferenceController implements Preference.OnPreferenceChangeListener, WifiDialog.WifiDialogListener {
    private static final String KEY_WIFI_PRIVACY = "privacy";
    private static final int PREF_RANDOMIZATION_NONE = 1;
    private static final int PREF_RANDOMIZATION_PERSISTENT = 0;
    private boolean mIsEphemeral;
    private boolean mIsPasspoint;
    private Preference mPreference;
    private WifiConfiguration mWifiConfiguration;
    private WifiManager mWifiManager;

    public WifiPrivacyPreferenceController(Context context) {
        super(context, KEY_WIFI_PRIVACY);
        this.mIsEphemeral = false;
        this.mIsPasspoint = false;
        this.mWifiConfiguration = null;
        this.mWifiManager = (WifiManager) context.getSystemService("wifi");
    }

    public static int translateMacRandomizedValueToPrefValue(int i) {
        return i == 1 ? 0 : 1;
    }

    public static int translatePrefValueToMacRandomizedValue(int i) {
        return i == 0 ? 1 : 0;
    }

    private void updateSummary(DropDownPreference dropDownPreference, int i) {
        dropDownPreference.setSummary(dropDownPreference.getEntries()[translateMacRandomizedValueToPrefValue(i)]);
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ void copy() {
        super.copy();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mWifiManager.isConnectedMacRandomizationSupported() ? 0 : 2;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class<? extends SliceBackgroundWorker> getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    int getRandomizationValue() {
        WifiConfiguration wifiConfiguration = this.mWifiConfiguration;
        if (wifiConfiguration != null) {
            return wifiConfiguration.macRandomizationSetting;
        }
        return 1;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isCopyableSlice() {
        return super.isCopyableSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public /* bridge */ /* synthetic */ void onForget(WifiDialog wifiDialog) {
        super.onForget(wifiDialog);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        WifiConfiguration wifiConfiguration = this.mWifiConfiguration;
        if (wifiConfiguration != null) {
            wifiConfiguration.macRandomizationSetting = Integer.parseInt((String) obj);
            this.mWifiManager.updateNetwork(this.mWifiConfiguration);
            WifiInfo connectionInfo = this.mWifiManager.getConnectionInfo();
            if (connectionInfo != null && connectionInfo.getNetworkId() == this.mWifiConfiguration.networkId) {
                this.mWifiManager.disconnect();
            }
        }
        updateSummary((DropDownPreference) preference, Integer.parseInt((String) obj));
        return true;
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public /* bridge */ /* synthetic */ void onScan(WifiDialog wifiDialog, String str) {
        super.onScan(wifiDialog, str);
    }

    @Override // com.android.settings.wifi.WifiDialog.WifiDialogListener
    public void onSubmit(WifiDialog wifiDialog) {
        WifiConfiguration config;
        WifiConfiguration wifiConfiguration;
        int i;
        if (wifiDialog.getController() == null || (config = wifiDialog.getController().getConfig()) == null || (wifiConfiguration = this.mWifiConfiguration) == null || (i = config.macRandomizationSetting) == wifiConfiguration.macRandomizationSetting) {
            return;
        }
        this.mWifiConfiguration = config;
        onPreferenceChange(this.mPreference, String.valueOf(i));
    }

    public void setIsEphemeral(boolean z) {
        this.mIsEphemeral = z;
    }

    public void setIsPasspoint(boolean z) {
        this.mIsPasspoint = z;
    }

    public void setWifiConfiguration(WifiConfiguration wifiConfiguration) {
        this.mWifiConfiguration = wifiConfiguration;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        DropDownPreference dropDownPreference = (DropDownPreference) preference;
        int randomizationValue = getRandomizationValue();
        dropDownPreference.setValue(Integer.toString(randomizationValue));
        updateSummary(dropDownPreference, randomizationValue);
        if (this.mIsEphemeral || this.mIsPasspoint) {
            preference.setSelectable(false);
            dropDownPreference.setSummary(R.string.wifi_privacy_settings_ephemeral_summary);
        }
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
