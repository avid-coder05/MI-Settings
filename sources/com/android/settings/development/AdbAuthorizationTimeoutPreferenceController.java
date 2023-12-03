package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import miui.mipub.MipubStat;

/* loaded from: classes.dex */
public class AdbAuthorizationTimeoutPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    private final Context mContext;

    public AdbAuthorizationTimeoutPreferenceController(Context context) {
        super(context);
        this.mContext = context;
    }

    private void writeSetting(boolean z) {
        Settings.Global.putLong(this.mContext.getContentResolver(), "adb_allowed_connection_time", !z ? MipubStat.STAT_EXPIRY_DATA : 0L);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "adb_authorization_timeout";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeSetting(false);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeSetting(((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.Global.getLong(this.mContext.getContentResolver(), "adb_allowed_connection_time", MipubStat.STAT_EXPIRY_DATA) == 0);
    }
}
