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
public class AdbInstallPreferenceController extends DeveloperOptionsPreferenceController implements OnActivityResultListener, Preference.OnPreferenceChangeListener {
    private Activity mActivity;
    protected CheckBoxPreference mPreference;

    public AdbInstallPreferenceController(Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    private void updateAdbInstallPreference() {
        AdbUtils.setInstallEnabled(this.mActivity, !AdbUtils.isInstallEnabled(r1));
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
        return "adb_install";
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        String str = Build.DEVICE;
        return ("cappu".equals(str) || "clover".equals(str)) ? false : true;
    }

    @Override // com.android.settings.development.OnActivityResultListener
    public boolean onActivityResult(int i, int i2, Intent intent) {
        if (i == 11) {
            this.mPreference.setChecked(AdbUtils.isInstallEnabled(this.mActivity));
            return true;
        }
        return false;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (TextUtils.equals("adb_install", preference.getKey())) {
            updateAdbInstallPreference();
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        this.mPreference.setChecked(AdbUtils.isInstallEnabled(this.mActivity));
    }
}
