package com.android.settings.development;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.content.Context;
import android.os.RemoteException;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class KeepActivitiesPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    static final int SETTING_VALUE_OFF = 0;
    private IActivityManager mActivityManager;

    public KeepActivitiesPreferenceController(Context context) {
        super(context);
    }

    private void writeImmediatelyDestroyActivitiesOptions(boolean z) {
        try {
            this.mActivityManager.setAlwaysFinish(z);
        } catch (RemoteException unused) {
        }
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mActivityManager = getActivityManager();
    }

    IActivityManager getActivityManager() {
        return ActivityManager.getService();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "immediately_destroy_activities";
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        writeImmediatelyDestroyActivitiesOptions(false);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        writeImmediatelyDestroyActivitiesOptions(((Boolean) obj).booleanValue());
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(Settings.Global.getInt(this.mContext.getContentResolver(), "always_finish_activities", 0) != 0);
    }
}
