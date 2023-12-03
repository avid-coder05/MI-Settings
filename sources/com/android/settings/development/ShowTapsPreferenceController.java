package com.android.settings.development;

import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class ShowTapsPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SETTING_VALUE_OFF = 0;
    static final int SETTING_VALUE_ON = 1;
    private String SHOW_TOUCHES_PREVENTRECORDER;

    public ShowTapsPreferenceController(Context context) {
        super(context);
        this.SHOW_TOUCHES_PREVENTRECORDER = "show_touches_preventrecord";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "show_touches";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        Settings.System.putInt(this.mContext.getContentResolver(), "show_touches", 0);
        Settings.System.putInt(this.mContext.getContentResolver(), this.SHOW_TOUCHES_PREVENTRECORDER, 0);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Settings.System.putInt(this.mContext.getContentResolver(), "show_touches", booleanValue ? 1 : 0);
        Settings.System.putInt(this.mContext.getContentResolver(), this.SHOW_TOUCHES_PREVENTRECORDER, booleanValue ? 1 : 0);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.System.getInt(this.mContext.getContentResolver(), "show_touches", 0) != 0);
    }
}
