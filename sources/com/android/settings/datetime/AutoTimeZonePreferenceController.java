package com.android.settings.datetime;

import android.content.Context;
import android.provider.MiuiSettings;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.miui.enterprise.RestrictionsHelper;
import miuix.util.Log;

/* loaded from: classes.dex */
public class AutoTimeZonePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final UpdateTimeAndDateCallback mCallback;
    private final boolean mIsFromSUW;

    public AutoTimeZonePreferenceController(Context context, UpdateTimeAndDateCallback updateTimeAndDateCallback, boolean z) {
        super(context);
        this.mCallback = updateTimeAndDateCallback;
        this.mIsFromSUW = z;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_zone";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return (Utils.isWifiOnly(this.mContext) || this.mIsFromSUW) ? false : true;
    }

    public boolean isEnabled() {
        return isAvailable() && Settings.Global.getInt(this.mContext.getContentResolver(), "auto_time_zone", 0) > 0;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "auto_time_zone", ((Boolean) obj).booleanValue() ? 1 : 0);
        this.mCallback.updateTimeAndDateDisplay(this.mContext);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof SwitchPreference) {
            ((SwitchPreference) preference).setChecked(isEnabled());
            if (MiuiSettings.Secure.isTimeChangeDisallow(this.mContext.getContentResolver())) {
                Log.d("DateTimeSettings", "Time change disallowed");
                preference.setEnabled(false);
            }
            if (RestrictionsHelper.hasRestriction(preference.getContext(), "disallow_timeset")) {
                ((RestrictedSwitchPreference) preference).setChecked(true);
                Settings.Global.putInt(preference.getContext().getContentResolver(), "auto_time_zone", 1);
            }
        }
    }
}
