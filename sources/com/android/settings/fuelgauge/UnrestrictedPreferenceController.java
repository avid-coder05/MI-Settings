package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.fuelgauge.BatteryOptimizeUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.RadioButtonPreference;

/* loaded from: classes.dex */
public class UnrestrictedPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    String KEY_UNRESTRICTED_PREF;
    BatteryOptimizeUtils mBatteryOptimizeUtils;

    public UnrestrictedPreferenceController(Context context, int i, String str) {
        super(context);
        this.KEY_UNRESTRICTED_PREF = "unrestricted_pref";
        this.mBatteryOptimizeUtils = new BatteryOptimizeUtils(context, i, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY_UNRESTRICTED_PREF;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (this.KEY_UNRESTRICTED_PREF.equals(preference.getKey())) {
            this.mBatteryOptimizeUtils.setAppUsageState(BatteryOptimizeUtils.AppUsageState.UNRESTRICTED);
            Log.d("UNRESTRICTED_PREF", "Set unrestricted");
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
            Log.d("UNRESTRICTED_PREF", "invalid package name, disable pref");
            preference.setEnabled(false);
            return;
        }
        preference.setEnabled(true);
        if (this.mBatteryOptimizeUtils.isSystemOrDefaultApp()) {
            Log.d("UNRESTRICTED_PREF", "is system or default app, unrestricted states only");
            ((RadioButtonPreference) preference).setChecked(true);
        } else if (this.mBatteryOptimizeUtils.getAppUsageState() != BatteryOptimizeUtils.AppUsageState.UNRESTRICTED) {
            ((RadioButtonPreference) preference).setChecked(false);
        } else {
            Log.d("UNRESTRICTED_PREF", "is unrestricted states");
            ((RadioButtonPreference) preference).setChecked(true);
        }
    }
}
