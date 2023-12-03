package com.android.settingslib.miuisettings.preference;

import android.content.Context;
import android.util.AttributeSet;

/* loaded from: classes2.dex */
public class IconPreference extends Preference implements PreferenceFeature {
    public IconPreference(Context context) {
        super(context);
    }

    public IconPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public IconPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public IconPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    public IconPreference(Context context, AttributeSet attributeSet, int i, int i2, boolean z) {
        super(context, attributeSet, i, i2, z);
    }

    public IconPreference(Context context, AttributeSet attributeSet, int i, boolean z) {
        super(context, attributeSet, i, z);
    }

    public IconPreference(Context context, AttributeSet attributeSet, boolean z) {
        super(context, attributeSet, z);
    }

    public IconPreference(Context context, boolean z) {
        super(context, z);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFeature
    public boolean hasIcon() {
        return true;
    }
}
