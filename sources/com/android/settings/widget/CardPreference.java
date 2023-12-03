package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.Preference;

/* loaded from: classes2.dex */
public class CardPreference extends Preference {
    public CardPreference(Context context) {
        this(context, null);
    }

    public CardPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R.attr.cardPreferenceStyle);
    }
}
