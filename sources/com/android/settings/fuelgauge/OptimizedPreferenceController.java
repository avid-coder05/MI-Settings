package com.android.settings.fuelgauge;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.fuelgauge.BatteryOptimizeUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.RadioButtonPreference;

/* loaded from: classes.dex */
public class OptimizedPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    String KEY_OPTIMIZED_PREF;
    BatteryOptimizeUtils mBatteryOptimizeUtils;

    public OptimizedPreferenceController(Context context, int i, String str) {
        super(context);
        this.KEY_OPTIMIZED_PREF = "optimized_pref";
        this.mBatteryOptimizeUtils = new BatteryOptimizeUtils(context, i, str);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.KEY_OPTIMIZED_PREF;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (this.KEY_OPTIMIZED_PREF.equals(preference.getKey())) {
            this.mBatteryOptimizeUtils.setAppUsageState(BatteryOptimizeUtils.AppUsageState.OPTIMIZED);
            Log.d("OPTIMIZED_PREF", "Set optimized");
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
            Log.d("OPTIMIZED_PREF", "invalid package name, optimized states only");
            preference.setEnabled(true);
            ((RadioButtonPreference) preference).setChecked(true);
        } else if (this.mBatteryOptimizeUtils.isSystemOrDefaultApp()) {
            Log.d("OPTIMIZED_PREF", "is system or default app, disable pref");
            ((RadioButtonPreference) preference).setChecked(false);
            preference.setEnabled(false);
        } else if (this.mBatteryOptimizeUtils.getAppUsageState() != BatteryOptimizeUtils.AppUsageState.OPTIMIZED) {
            ((RadioButtonPreference) preference).setChecked(false);
        } else {
            Log.d("OPTIMIZED_PREF", "is optimized states");
            ((RadioButtonPreference) preference).setChecked(true);
        }
    }
}
