package com.android.settings.widget;

import android.view.View;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.miuisettings.preference.PreferenceFeature;
import com.android.settingslib.miuisettings.preference.SwitchPreference;

/* loaded from: classes2.dex */
public class AppSwitchPreference extends SwitchPreference implements PreferenceFeature {
    @Override // com.android.settingslib.miuisettings.preference.PreferenceFeature
    public boolean hasIcon() {
        return true;
    }

    @Override // com.android.settingslib.miuisettings.preference.SwitchPreference, androidx.preference.SwitchPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        View findViewById = preferenceViewHolder.findViewById(16908352);
        if (findViewById != null) {
            findViewById.setFilterTouchesWhenObscured(true);
        }
    }
}
