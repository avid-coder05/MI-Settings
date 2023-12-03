package com.android.settings;

import android.content.Context;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.miuisettings.preference.PreferenceCategory;

/* loaded from: classes.dex */
public class AccessiblePreferenceCategory extends PreferenceCategory {
    private String mContentDescription;

    public AccessiblePreferenceCategory(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceCategory, androidx.preference.PreferenceCategory, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setContentDescription(this.mContentDescription);
    }

    public void setContentDescription(String str) {
        this.mContentDescription = str;
    }
}
