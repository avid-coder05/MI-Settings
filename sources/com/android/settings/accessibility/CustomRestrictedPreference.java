package com.android.settings.accessibility;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.RestrictedPreference;

/* loaded from: classes.dex */
public class CustomRestrictedPreference extends RestrictedPreference {
    public CustomRestrictedPreference(Context context) {
        super(context);
    }

    public CustomRestrictedPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomRestrictedPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public CustomRestrictedPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R.layout.restricted_icon_and_value_preference;
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, com.android.settingslib.miuisettings.preference.Preference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        View findViewById;
        super.onBindViewHolder(preferenceViewHolder);
        View view = preferenceViewHolder.itemView;
        TextView textView = (TextView) view.findViewById(R.id.value_right);
        TextView textView2 = (TextView) view.findViewById(16908304);
        if (getSecondTargetResId() != 0 && (findViewById = view.findViewById(16908312)) != null) {
            findViewById.setVisibility(0);
        }
        if (textView == null || !isSummary2Value()) {
            if (textView == null || TextUtils.isEmpty(getValue())) {
                return;
            }
            textView.setVisibility(0);
            textView.setText(getValue());
            return;
        }
        CharSequence summary = getSummary();
        if (TextUtils.isEmpty(summary)) {
            textView.setVisibility(8);
        } else {
            textView.setText(summary);
            textView.setVisibility(0);
        }
        if (textView2 != null) {
            textView2.setVisibility(8);
        }
    }
}
