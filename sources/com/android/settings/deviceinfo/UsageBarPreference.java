package com.android.settings.deviceinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes.dex */
public class UsageBarPreference extends Preference {
    public UsageBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public UsageBarPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public UsageBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(R.layout.preference_memoryusage);
    }

    public void addEntry(int i, float f, int i2) {
    }

    public void clear() {
    }

    public void commit() {
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
    }
}
