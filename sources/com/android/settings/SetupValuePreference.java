package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class SetupValuePreference extends ValuePreference {
    public SetupValuePreference(Context context) {
        super(context);
    }

    public SetupValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SetupValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference, miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setBackgroundResource(R.drawable.setup_list_item_background);
        setShowRightArrow(true);
    }
}
