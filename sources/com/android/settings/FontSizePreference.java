package com.android.settings;

import android.view.View;
import android.widget.TextView;
import com.android.settingslib.miuisettings.preference.RadioButtonPreference;

/* loaded from: classes.dex */
public class FontSizePreference extends RadioButtonPreference {
    private int mPreviewSize;

    @Override // com.android.settingslib.miuisettings.preference.RadioButtonPreference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        ((TextView) view.findViewById(16908310)).setTextSize(1, this.mPreviewSize);
        ((TextView) view.findViewById(16908304)).setTextSize(1, this.mPreviewSize);
    }
}
