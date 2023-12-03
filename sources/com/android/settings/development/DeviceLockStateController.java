package com.android.settings.development;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemProperties;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R;
import com.android.settings.bootloader.BootloaderStatusActivity;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class DeviceLockStateController extends DeveloperOptionsPreferenceController {
    private Activity mActivity;

    public DeviceLockStateController(Activity activity) {
        super(activity);
        this.mActivity = activity;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ValuePreference valuePreference = (ValuePreference) this.mPreference;
        if (valuePreference != null) {
            valuePreference.setShowRightArrow(true);
            if ("locked".equals(SystemProperties.get("ro.secureboot.lockstate", ""))) {
                valuePreference.setValue((String) null);
            } else {
                valuePreference.setValue(R.string.bootloader_status_unlocked);
            }
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "device_lock_state";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals("device_lock_state", preference.getKey())) {
            this.mActivity.startActivity(new Intent(this.mActivity, BootloaderStatusActivity.class));
            return true;
        }
        return false;
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return 1 == SystemProperties.getInt("ro.secureboot.devicelock", 0);
    }
}
