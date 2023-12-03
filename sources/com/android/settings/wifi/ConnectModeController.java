package com.android.settings.wifi;

import android.content.Context;
import android.net.wifi.MiuiWifiManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.miuisettings.preference.miuix.DropDownPreference;
import miui.provider.ExtraTelephony;
import miui.util.FeatureParser;

/* loaded from: classes2.dex */
public class ConnectModeController extends AbstractPreferenceController implements Preference.OnPreferenceChangeListener {
    private final MiuiWifiManager mMiuiWifiManager;

    public ConnectModeController(Context context) {
        super(context);
        this.mMiuiWifiManager = (MiuiWifiManager) this.mContext.getSystemService("MiuiWifiService");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "connect_mode";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        String str = SystemProperties.get("ro.boot.hwversion");
        String[] split = str != null ? str.split("\\.") : null;
        return FeatureParser.getBoolean("support_choose_connect_mode", false) && (split == null || split.length <= 0 || !split[0].equals(ExtraTelephony.Phonelist.TYPE_CLOUDS_BLACK));
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (TextUtils.equals(preference.getKey(), "connect_mode")) {
            try {
                int parseInt = Integer.parseInt((String) obj);
                preference.setSummary(this.mContext.getResources().getStringArray(R.array.connect_mode_entries)[parseInt]);
                Settings.System.putInt(this.mContext.getContentResolver(), "wireless_compatible_mode", parseInt);
                this.mMiuiWifiManager.setCompatibleMode(parseInt == 0);
            } catch (NumberFormatException unused) {
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (TextUtils.equals(preference.getKey(), "connect_mode")) {
            DropDownPreference dropDownPreference = (DropDownPreference) preference;
            dropDownPreference.setValue(String.valueOf(Settings.System.getInt(this.mContext.getContentResolver(), "wireless_compatible_mode", 1)));
            dropDownPreference.setSummary(dropDownPreference.getEntry());
        }
    }
}
