package com.android.settingslib.widget;

import androidx.preference.PreferenceScreen;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.SetPreferenceScreen;

/* loaded from: classes2.dex */
public class FooterPreferenceMixinCompat implements LifecycleObserver, SetPreferenceScreen {
    private FooterPreference mFooterPreference;

    @Override // com.android.settingslib.core.lifecycle.events.SetPreferenceScreen
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        FooterPreference footerPreference = this.mFooterPreference;
        if (footerPreference != null) {
            preferenceScreen.addPreference(footerPreference);
        }
    }
}
