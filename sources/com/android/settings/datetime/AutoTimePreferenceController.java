package com.android.settings.datetime;

import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.miui.enterprise.RestrictionsHelper;

/* loaded from: classes.dex */
public class AutoTimePreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final UpdateTimeAndDateCallback mCallback;

    public AutoTimePreferenceController(Context context, UpdateTimeAndDateCallback updateTimeAndDateCallback) {
        super(context);
        this.mCallback = updateTimeAndDateCallback;
    }

    private RestrictedLockUtils.EnforcedAdmin getEnforcedAdminProperty() {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_date_time", UserHandle.myUserId());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "auto_time";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public boolean isEnabled() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "auto_time", 0) > 0;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Log.d("PrefControllerMixin", "auto_time enabled changed : " + booleanValue);
        Settings.System.putInt(this.mContext.getContentResolver(), "time_set_by_settings", 1);
        Settings.Global.putInt(this.mContext.getContentResolver(), "auto_time", booleanValue ? 1 : 0);
        this.mCallback.updateTimeAndDateDisplay(this.mContext);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (preference instanceof RestrictedSwitchPreference) {
            RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preference;
            if (!restrictedSwitchPreference.isDisabledByAdmin()) {
                restrictedSwitchPreference.setDisabledByAdmin(getEnforcedAdminProperty());
            }
            restrictedSwitchPreference.setChecked(isEnabled());
            if (RestrictionsHelper.hasRestriction(preference.getContext(), "disallow_timeset")) {
                ((RestrictedSwitchPreference) preference).setChecked(true);
                Settings.Global.putInt(preference.getContext().getContentResolver(), "auto_time", 1);
            }
        }
    }
}
