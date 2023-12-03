package com.android.settings.device.controller;

import android.content.Context;
import androidx.preference.PreferenceScreen;
import com.android.settings.Utils;
import miui.os.Build;

/* loaded from: classes.dex */
public class MiuiLegalInfoController extends BaseDeviceInfoController {
    public MiuiLegalInfoController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (Build.IS_INTERNATIONAL_BUILD) {
            preferenceScreen.findPreference(getPreferenceKey());
            Utils.updatePreferenceToSpecificActivityOrRemove(this.mContext, preferenceScreen, "team", 1);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "container";
    }
}
