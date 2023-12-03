package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class MiuiTimeFloatingWindowController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    public MiuiTimeFloatingWindowController(Context context) {
        super(context);
    }

    private void setChecked(boolean z) {
        ((SwitchPreference) this.mPreference).setChecked(z);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "miui_time_floating_window";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "miui_time_floating_window", 0, -2);
        setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "miui_time_floating_window", ((Boolean) obj).booleanValue() ? 1 : 0, -2);
        Log.d("MiuiTimeFloatingWindowController", "MiuiTimeFloatingWindowEnable: " + obj);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        setChecked(Settings.System.getIntForUser(this.mContext.getContentResolver(), "miui_time_floating_window", 0, -2) == 1);
    }
}
