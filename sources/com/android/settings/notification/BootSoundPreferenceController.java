package com.android.settings.notification;

import android.content.Context;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

/* loaded from: classes2.dex */
public class BootSoundPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin {
    static final String PROPERTY_BOOT_SOUNDS = "persist.sys.bootanim.play_sound";

    public BootSoundPreferenceController(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (isAvailable()) {
            ((SwitchPreference) preferenceScreen.findPreference("boot_sounds")).setChecked(SystemProperties.getBoolean(PROPERTY_BOOT_SOUNDS, true));
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "boot_sounds";
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if ("boot_sounds".equals(preference.getKey())) {
            SystemProperties.set(PROPERTY_BOOT_SOUNDS, ((SwitchPreference) preference).isChecked() ? "1" : "0");
            return false;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mContext.getResources().getBoolean(R.bool.has_boot_sounds);
    }
}
