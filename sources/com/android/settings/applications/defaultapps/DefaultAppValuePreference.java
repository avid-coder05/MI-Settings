package com.android.settings.applications.defaultapps;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes.dex */
public class DefaultAppValuePreference extends ValuePreference {
    public DefaultAppValuePreference(Context context) {
        super(context);
        setShowRightArrow(true);
    }

    public DefaultAppValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setShowRightArrow(true);
    }

    public DefaultAppValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setShowRightArrow(true);
    }

    @Override // androidx.preference.Preference
    public void setSummary(int i) {
        setValue(i);
    }

    @Override // androidx.preference.Preference
    public void setSummary(CharSequence charSequence) {
        setValue(charSequence != null ? charSequence.toString() : "");
    }
}
