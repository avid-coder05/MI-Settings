package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class ExtendedPowerMenuPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private CheckBoxPreference mExtendedPowerMenu;

    public ExtendedPowerMenuPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mExtendedPowerMenu = (CheckBoxPreference) preferenceScreen.findPreference("extended_power_menu");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "extended_power_menu";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.Secure.putInt(this.mContext.getContentResolver(), "extended_power_menu", 0);
        this.mExtendedPowerMenu.setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "extended_power_menu", ((Boolean) obj).booleanValue() ? 1 : 0);
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i = Settings.Secure.getInt(this.mContext.getContentResolver(), "extended_power_menu", 0);
        CheckBoxPreference checkBoxPreference = this.mExtendedPowerMenu;
        boolean z = i;
        if (i != 0) {
            z = 1;
        }
        checkBoxPreference.setChecked(z);
    }
}
