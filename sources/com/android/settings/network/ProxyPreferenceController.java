package com.android.settings.network;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes.dex */
public class ProxyPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    public ProxyPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("proxy_settings");
        if (findPreference != null) {
            findPreference.setEnabled(((DevicePolicyManager) this.mContext.getSystemService("device_policy")).getGlobalProxyAdmin() == null);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "proxy_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return false;
    }
}
