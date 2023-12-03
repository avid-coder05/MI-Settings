package com.android.settings.bluetooth;

import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes.dex */
public class BluetoothPairingPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    private DashboardFragment mFragment;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "pref_bt_pairing";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if ("pref_bt_pairing".equals(preference.getKey())) {
            new SubSettingLauncher(this.mContext).setDestination(BluetoothPairingDetail.class.getName()).setTitleRes(R.string.bluetooth_pairing_page_title).setSourceMetricsCategory(this.mFragment.getMetricsCategory()).launch();
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }
}
