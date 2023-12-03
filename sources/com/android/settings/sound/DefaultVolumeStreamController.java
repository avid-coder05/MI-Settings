package com.android.settings.sound;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import miuix.preference.DropDownPreference;

/* loaded from: classes2.dex */
public class DefaultVolumeStreamController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public DefaultVolumeStreamController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int parseInt = Integer.parseInt((String) obj);
        Settings.System.putInt(contentResolver, "default_vol_stream", parseInt);
        ((DropDownPreference) preference).setValue(String.valueOf(parseInt));
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        int i = Settings.System.getInt(this.mContext.getContentResolver(), "default_vol_stream", 3);
        if (i != 3 && i != 2) {
            i = 3;
        }
        ((DropDownPreference) preference).setValue(String.valueOf(i));
    }
}
