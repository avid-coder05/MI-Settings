package com.android.settings.security;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.AbstractPreferenceController;
import miui.os.Build;

/* loaded from: classes2.dex */
public class AdPreferenceController extends AbstractPreferenceController {
    PreferenceScreen mPreferenceScreen;

    public AdPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceScreen = preferenceScreen;
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "ad_control_settings";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        Bundle bundle;
        boolean z = false;
        if (!Build.IS_INTERNATIONAL_BUILD) {
            try {
                ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo("com.miui.systemAdSolution", 128);
                if (applicationInfo != null && (bundle = applicationInfo.metaData) != null) {
                    z = bundle.getBoolean("SupportPersonalizedAd", false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (z || ((PreferenceCategory) this.mPreferenceScreen.findPreference("security_settings_access_control")) != null) {
            return z;
        }
        return true;
    }
}
