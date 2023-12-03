package com.android.settings.sound;

import android.content.Context;
import android.provider.MiuiSettings;
import android.provider.Settings;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;

/* loaded from: classes2.dex */
public class MiuiUnmuteSoundController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public MiuiUnmuteSoundController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        MiuiSettings.Global.putBoolean(this.mContext.getContentResolver(), "unmute_sound_enabled", ((Boolean) obj).booleanValue());
        return true;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int i = Settings.Global.getInt(this.mContext.getContentResolver(), "unmute_sound_enabled", 1);
        boolean z = i;
        if (i != 0) {
            z = 1;
        }
        ((CheckBoxPreference) preference).setChecked(z);
    }
}
