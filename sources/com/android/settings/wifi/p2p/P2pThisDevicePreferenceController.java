package com.android.settings.wifi.p2p;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes2.dex */
public class P2pThisDevicePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private ValuePreference mPreference;

    public P2pThisDevicePreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ValuePreference valuePreference = (ValuePreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = valuePreference;
        if (valuePreference != null) {
            valuePreference.setShowRightArrow(true);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "p2p_this_device";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public void setEnabled(boolean z) {
        ValuePreference valuePreference = this.mPreference;
        if (valuePreference != null) {
            valuePreference.setEnabled(z);
        }
    }

    public void updateDeviceName(WifiP2pDevice wifiP2pDevice) {
        if (this.mPreference == null || wifiP2pDevice == null) {
            return;
        }
        if (TextUtils.isEmpty(wifiP2pDevice.deviceName)) {
            this.mPreference.setValue(wifiP2pDevice.deviceAddress);
        } else {
            this.mPreference.setValue(wifiP2pDevice.deviceName);
        }
    }
}
