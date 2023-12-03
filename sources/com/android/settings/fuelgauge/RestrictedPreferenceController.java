package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.fuelgauge.BatteryOptimizeUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.RadioButtonPreference;

/* loaded from: classes.dex */
public class RestrictedPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    String KEY_RESTRICTED_PREF;
    BatteryOptimizeUtils mBatteryOptimizeUtils;

    public RestrictedPreferenceController(Context context, int i, String str) {
        super(context);
        this.KEY_RESTRICTED_PREF = "restricted_pref";
        this.mBatteryOptimizeUtils = new BatteryOptimizeUtils(context, i, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY_RESTRICTED_PREF;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (this.KEY_RESTRICTED_PREF.equals(preference.getKey())) {
            this.mBatteryOptimizeUtils.setAppUsageState(BatteryOptimizeUtils.AppUsageState.RESTRICTED);
            Log.d("RESTRICTED_PREF", "Set restricted");
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        if (!this.mBatteryOptimizeUtils.isValidPackageName()) {
            Log.d("RESTRICTED_PREF", "invalid package name, disable pref");
            preference.setEnabled(false);
            return;
        }
        preference.setEnabled(true);
        if (this.mBatteryOptimizeUtils.isSystemOrDefaultApp()) {
            Log.d("RESTRICTED_PREF", "is system or default app, disable pref");
            ((RadioButtonPreference) preference).setChecked(false);
            preference.setEnabled(false);
        } else if (this.mBatteryOptimizeUtils.getAppUsageState() != BatteryOptimizeUtils.AppUsageState.RESTRICTED) {
            ((RadioButtonPreference) preference).setChecked(false);
        } else {
            Log.d("RESTRICTED_PREF", "is restricted states");
            ((RadioButtonPreference) preference).setChecked(true);
        }
    }
}
