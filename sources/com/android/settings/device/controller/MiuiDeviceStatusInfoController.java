package com.android.settings.device.controller;

import android.content.Context;
import androidx.preference.Preference;

/* loaded from: classes.dex */
public class MiuiDeviceStatusInfoController extends BaseDeviceInfoController {
    public MiuiDeviceStatusInfoController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "status_info";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        return super.handlePreferenceTreeClick(preference);
    }
}
