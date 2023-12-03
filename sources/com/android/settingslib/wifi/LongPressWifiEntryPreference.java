package com.android.settingslib.wifi;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceViewHolder;

/* loaded from: classes2.dex */
public class LongPressWifiEntryPreference extends WifiEntryPreference {
    private final Fragment mFragment;

    @Override // com.android.settingslib.wifi.WifiEntryPreference, com.android.settingslib.miuisettings.preference.RadioButtonPreference, miuix.preference.RadioButtonPreference, androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        Fragment fragment = this.mFragment;
        if (fragment != null) {
            preferenceViewHolder.itemView.setOnCreateContextMenuListener(fragment);
            preferenceViewHolder.itemView.setTag(this);
            preferenceViewHolder.itemView.setLongClickable(true);
        }
    }
}
