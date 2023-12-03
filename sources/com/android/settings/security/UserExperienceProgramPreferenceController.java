package com.android.settings.security;

import android.content.Context;
import android.provider.MiuiSettings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.report.InternationalCompat;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class UserExperienceProgramPreferenceController extends AbstractPreferenceController implements Preference.OnPreferenceChangeListener {
    private CheckBoxPreference mPreference;

    public UserExperienceProgramPreferenceController(Context context) {
        super(context);
    }

    private void updateUserExperienceProgramPreference(boolean z) {
        MiuiSettings.Secure.enableUserExperienceProgram(this.mContext.getContentResolver(), z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (CheckBoxPreference) preferenceScreen.findPreference("user_experience_program");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "user_experience_program";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return true;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if ("user_experience_program".equals(preference.getKey())) {
            InternationalCompat.trackReportSwitchStatus("setting_Passwords_sec_user_experience", obj);
            updateUserExperienceProgramPreference(((Boolean) obj).booleanValue());
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference.setChecked(MiuiSettings.Secure.isUserExperienceProgramEnable(this.mContext.getContentResolver()));
    }
}
