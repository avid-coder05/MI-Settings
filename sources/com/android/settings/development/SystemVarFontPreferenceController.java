package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import androidx.fragment.app.Fragment;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class SystemVarFontPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private final Fragment mFragment;
    private CheckBoxPreference mSystemVarFont;

    public SystemVarFontPreferenceController(Context context, Fragment fragment) {
        super(context);
        this.mFragment = fragment;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSystemVarFont = (CheckBoxPreference) preferenceScreen.findPreference("system_var_font");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "system_var_font";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        SystemProperties.set("persist.sys.miui_var_font", String.valueOf(true));
        this.mSystemVarFont.setChecked(true);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        SystemVarFontRebootDialog.show(this.mFragment);
        return true;
    }

    public void onSystemVarFontDialogConfirmed() {
        SystemProperties.set("persist.sys.miui_var_font", String.valueOf(this.mSystemVarFont.isChecked()));
    }

    public void onSystemVarFontDialogDismissed() {
        this.mSystemVarFont.setChecked(!r1.isChecked());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        this.mSystemVarFont.setChecked(SystemProperties.getBoolean("persist.sys.miui_var_font", true));
    }
}
