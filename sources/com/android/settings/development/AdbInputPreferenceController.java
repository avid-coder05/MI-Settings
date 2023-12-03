package com.android.settings.development;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.security.AdbUtils;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;

/* loaded from: classes.dex */
public class AdbInputPreferenceController extends DeveloperOptionsPreferenceController implements OnActivityResultListener, Preference.OnPreferenceChangeListener {
    private Activity mActivity;
    protected CheckBoxPreference mPreference;

    public AdbInputPreferenceController(Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    private void updateAdbInputPreference() {
        AdbUtils.setInputEnabled(!AdbUtils.isInputEnabled());
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            this.mPreference = (CheckBoxPreference) preferenceScreen.findPreference(getPreferenceKey());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "adb_input";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        String str = Build.DEVICE;
        return ("cappu".equals(str) || "clover".equals(str)) ? false : true;
    }

    @Override // com.android.settings.development.OnActivityResultListener
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i == 12) {
            this.mPreference.setChecked(AdbUtils.isInputEnabled());
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (TextUtils.equals("adb_input", preference.getKey())) {
            updateAdbInputPreference();
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference.setChecked(AdbUtils.isInputEnabled());
    }
}
