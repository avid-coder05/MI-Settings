package com.android.settings.device.controller;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class MiuiDeviceSecurityPatchController extends BaseDeviceInfoController {
    private final String KEY_SECURITY_PATCH;
    private String mSecurityPatch;

    public MiuiDeviceSecurityPatchController(Context context) {
        super(context);
        this.KEY_SECURITY_PATCH = "security_patch";
        this.mSecurityPatch = Build.VERSION.SECURITY_PATCH;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ValuePreference valuePreference = (ValuePreference) preferenceScreen.findPreference(getPreferenceKey());
        if (valuePreference != null) {
            setValueSummary(valuePreference, this.mSecurityPatch);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "security_patch";
    }

    @Override // com.android.settings.device.controller.BaseDeviceInfoController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return super.isAvailable() && !TextUtils.isEmpty(this.mSecurityPatch);
    }
}
