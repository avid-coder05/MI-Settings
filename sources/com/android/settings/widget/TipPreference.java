package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class TipPreference extends Preference {
    public TipPreference(Context context) {
        super(context);
    }

    public TipPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public TipPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public TipPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // com.android.settingslib.miuisettings.preference.Preference, com.android.settingslib.miuisettings.preference.PreferenceApiDiff
    public void onBindView(View view) {
        super.onBindView(view);
        if (view != null) {
            view.setBackgroundColor(0);
        }
    }
}
