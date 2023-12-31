package com.android.settings.notification.zen;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes2.dex */
public class ZenModeRepeatCallersPreferenceController extends AbstractZenModePreferenceController implements Preference.OnPreferenceChangeListener {
    private final ZenModeBackend mBackend;
    private final int mRepeatCallersThreshold;

    public ZenModeRepeatCallersPreferenceController(Context context, Lifecycle lifecycle, int i) {
        super(context, "zen_mode_repeat_callers", lifecycle);
        this.mRepeatCallersThreshold = i;
        this.mBackend = ZenModeBackend.getInstance(context);
    }

    private void setRepeatCallerSummary(Preference preference) {
        preference.setSummary(this.mContext.getString(R.string.zen_mode_repeat_callers_summary, Integer.valueOf(this.mRepeatCallersThreshold)));
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        setRepeatCallerSummary(preferenceScreen.findPreference("zen_mode_repeat_callers"));
    }

    @Override // com.android.settings.notification.zen.AbstractZenModePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "zen_mode_repeat_callers";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        if (ZenModeSettingsBase.DEBUG) {
            Log.d("PrefControllerMixin", "onPrefChange allowRepeatCallers=" + booleanValue);
        }
        this.mMetricsFeatureProvider.action(this.mContext, 171, booleanValue);
        this.mBackend.saveSoundPolicy(16, booleanValue);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        SwitchPreference switchPreference = (SwitchPreference) preference;
        int zenMode = getZenMode();
        if (zenMode == 2 || zenMode == 3) {
            switchPreference.setEnabled(false);
            switchPreference.setChecked(false);
            return;
        }
        if (this.mBackend.isPriorityCategoryEnabled(8) && this.mBackend.getPriorityCallSenders() == 0) {
            switchPreference.setEnabled(false);
            switchPreference.setChecked(true);
            return;
        }
        switchPreference.setEnabled(true);
        switchPreference.setChecked(this.mBackend.isPriorityCategoryEnabled(16));
    }
}
