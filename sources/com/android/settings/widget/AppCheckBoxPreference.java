package com.android.settings.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.CheckBoxPreference;

/* loaded from: classes2.dex */
public class AppCheckBoxPreference extends CheckBoxPreference {
    public AppCheckBoxPreference(Context context) {
        super(context);
        setLayoutResource(R.layout.preference_app);
    }

    public AppCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R.layout.preference_app);
    }

    @Override // androidx.preference.CheckBoxPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        LinearLayout linearLayout = (LinearLayout) preferenceViewHolder.findViewById(R.id.summary_container);
        if (linearLayout != null) {
            linearLayout.setVisibility(TextUtils.isEmpty(getSummary()) ? 8 : 0);
        }
    }
}
