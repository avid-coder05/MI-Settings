package com.android.settings.notification;

import android.content.Context;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settingslib.core.lifecycle.Lifecycle;

/* loaded from: classes2.dex */
public class ScreenLockSoundPreferenceController extends SettingPrefController {
    public ScreenLockSoundPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment, Lifecycle lifecycle) {
        super(context, settingsPreferenceFragment, lifecycle);
        this.mPreference = new SettingPref(2, "screen_locking_sounds", "lockscreen_sounds_enabled", 1, new int[0]);
    }

    @Override // com.android.settings.notification.SettingPrefController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R.bool.config_show_screen_locking_sounds);
    }
}
