package com.android.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.preference.PreferenceViewHolder;

/* loaded from: classes.dex */
public class SetupKeyguardRestrictedPreference extends KeyguardRestrictedPreference {
    private Context mContext;

    public SetupKeyguardRestrictedPreference(Context context) {
        super(context);
        this.mContext = context;
    }

    public SetupKeyguardRestrictedPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mContext = context;
    }

    public SetupKeyguardRestrictedPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public SetupKeyguardRestrictedPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mContext = context;
    }

    @Override // com.android.settings.KeyguardRestrictedPreference, com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
        layoutParams.setMargins(0, 0, 0, this.mContext.getResources().getDimensionPixelOffset(R.dimen.choose_unlock_item_margin));
        preferenceViewHolder.itemView.setLayoutParams(layoutParams);
    }
}
