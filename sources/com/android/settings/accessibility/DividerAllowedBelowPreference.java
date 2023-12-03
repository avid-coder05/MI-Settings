package com.android.settings.accessibility;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class DividerAllowedBelowPreference extends Preference {
    public DividerAllowedBelowPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public DividerAllowedBelowPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.setDividerAllowedBelow(true);
    }
}
