package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class HandyModeGuidePreference extends Preference {
    public HandyModeGuidePreference(Context context, AttributeSet attributeSet) {
        this(context, null, 0);
    }

    public HandyModeGuidePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setLayoutResource(R.layout.handy_mode_guide);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
    }
}
