package com.android.settings.display;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.MiuiUtils;
import miuix.animation.Folme;

/* loaded from: classes.dex */
public class TopImagePreference extends Preference {
    public TopImagePreference(Context context) {
        super(context);
    }

    public TopImagePreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public TopImagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        if (MiuiUtils.isMiuiSdkSupportFolme()) {
            Folme.clean(preferenceViewHolder.itemView);
        }
        preferenceViewHolder.itemView.setBackgroundColor(0);
        preferenceViewHolder.itemView.setEnabled(false);
    }
}
