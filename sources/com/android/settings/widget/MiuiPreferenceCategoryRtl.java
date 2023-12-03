package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceViewHolder;

/* loaded from: classes2.dex */
public class MiuiPreferenceCategoryRtl extends PreferenceCategory {
    public MiuiPreferenceCategoryRtl(Context context) {
        super(context);
    }

    public MiuiPreferenceCategoryRtl(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public MiuiPreferenceCategoryRtl(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public MiuiPreferenceCategoryRtl(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // androidx.preference.PreferenceCategory, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        ((TextView) preferenceViewHolder.findViewById(16908310)).setTextDirection(5);
    }
}
