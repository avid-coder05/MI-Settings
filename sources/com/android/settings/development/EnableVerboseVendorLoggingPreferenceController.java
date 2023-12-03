package com.android.settings.development;

import android.content.Context;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class EnableVerboseVendorLoggingPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    private static final boolean DBG = Log.isLoggable("EnableVerboseVendorLoggingPreferenceController", 3);
    private int mDumpstateHalVersion;

    public EnableVerboseVendorLoggingPreferenceController(Context context) {
        super(context);
        this.mDumpstateHalVersion = -1;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "enable_verbose_vendor_logging";
    }

    boolean getVerboseLoggingEnabled() {
        return false;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return isIDumpstateDeviceV1_1ServiceAvailable();
    }

    boolean isIDumpstateDeviceV1_1ServiceAvailable() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        setVerboseLoggingEnabled(false);
        ((SwitchPreference) this.mPreference).setChecked(false);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        setVerboseLoggingEnabled(((Boolean) obj).booleanValue());
        return true;
    }

    void setVerboseLoggingEnabled(boolean z) {
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(getVerboseLoggingEnabled());
    }
}
