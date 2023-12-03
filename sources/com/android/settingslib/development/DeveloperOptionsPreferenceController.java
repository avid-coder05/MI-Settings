package com.android.settingslib.development;

import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public abstract class DeveloperOptionsPreferenceController extends AbstractPreferenceController {
    protected Preference mPreference;

    public DeveloperOptionsPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    public void onDeveloperOptionsDisabled() {
        if (isAvailable()) {
            onDeveloperOptionsSwitchDisabled();
        }
    }

    public void onDeveloperOptionsEnabled() {
        if (isAvailable()) {
            onDeveloperOptionsSwitchEnabled();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDeveloperOptionsSwitchDisabled() {
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDeveloperOptionsSwitchEnabled() {
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setEnabled(true);
        }
    }
}
