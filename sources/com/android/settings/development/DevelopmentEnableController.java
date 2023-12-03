package com.android.settings.development;

import android.content.Context;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.Utils;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.development.DevelopmentSettingsEnabler;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

/* loaded from: classes.dex */
public class DevelopmentEnableController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    private OnMainSwitchChangeListener mOnSwitchChangeListener;

    public DevelopmentEnableController(Context context, OnMainSwitchChangeListener onMainSwitchChangeListener) {
        super(context);
        this.mOnSwitchChangeListener = onMainSwitchChangeListener;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "development_enable";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        ((CheckBoxPreference) this.mPreference).setChecked(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchEnabled() {
        super.onDeveloperOptionsSwitchEnabled();
        ((CheckBoxPreference) this.mPreference).setChecked(true);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (!TextUtils.equals(preference.getKey(), "development_enable") || Utils.isMonkeyRunning()) {
            return true;
        }
        this.mOnSwitchChangeListener.onSwitchChanged(null, ((Boolean) obj).booleanValue());
        return true;
    }

    public void setChecked(boolean z) {
        ((CheckBoxPreference) this.mPreference).setChecked(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        ((CheckBoxPreference) preference).setChecked(DevelopmentSettingsEnabler.isDevelopmentSettingsEnabled(preference.getContext()));
    }
}
