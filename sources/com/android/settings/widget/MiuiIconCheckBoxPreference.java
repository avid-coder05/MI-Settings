package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;
import com.android.settingslib.miuisettings.preference.PreferenceFeature;

/* loaded from: classes2.dex */
public class MiuiIconCheckBoxPreference extends CheckBoxPreference implements PreferenceFeature {
    public MiuiIconCheckBoxPreference(Context context) {
        super(context);
    }

    public MiuiIconCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MiuiIconCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MiuiIconCheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // com.android.settingslib.miuisettings.preference.PreferenceFeature
    public boolean hasIcon() {
        return true;
    }
}
