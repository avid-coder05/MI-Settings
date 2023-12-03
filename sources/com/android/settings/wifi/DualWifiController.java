package com.android.settings.wifi;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;
import com.android.settingslib.wifi.SlaveWifiUtils;

/* loaded from: classes2.dex */
public class DualWifiController extends AbstractPreferenceController {
    private Context mContext;

    public DualWifiController(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "dual_wifi";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return SlaveWifiUtils.getInstance(this.mContext).isUiVisible(this.mContext);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference == null || !TextUtils.equals(preference.getKey(), "dual_wifi")) {
            return;
        }
        ValuePreference valuePreference = (ValuePreference) preference;
        valuePreference.setShowRightArrow(true);
        valuePreference.setEnabled(((WifiManager) this.mContext.getSystemService("wifi")).isWifiEnabled());
        valuePreference.setValue(SlaveWifiUtils.getInstance(this.mContext).isSlaveWifiEnabled() ? R.string.dual_wifi_on : R.string.dual_wifi_off);
    }
}
