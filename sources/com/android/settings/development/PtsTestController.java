package com.android.settings.development;

import android.content.Context;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.search.tree.DevelopmentSettingsTree;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class PtsTestController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener {
    static final String BLUETOOTH_ENABLE_PTS_TEST_PROPERTY = "debug.vendor.service.bt.pts_test";

    public PtsTestController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return DevelopmentSettingsTree.BLUETOOTH_ENABLE_PTS_TEST_KEY;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return SystemProperties.get("ro.soc.model").equals("SM8450");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        SystemProperties.set(BLUETOOTH_ENABLE_PTS_TEST_PROPERTY, booleanValue ? "true" : "false");
        Log.d("PtsTestController", "PTS state is " + booleanValue);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        ((SwitchPreference) this.mPreference).setChecked(SystemProperties.getBoolean(BLUETOOTH_ENABLE_PTS_TEST_PROPERTY, false));
    }
}
