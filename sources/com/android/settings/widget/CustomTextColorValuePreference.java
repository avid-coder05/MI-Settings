package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R;
import com.android.settingslib.miuisettings.preference.ValuePreference;

/* loaded from: classes2.dex */
public class CustomTextColorValuePreference extends ValuePreference {
    public CustomTextColorValuePreference(Context context) {
        super(context);
    }

    public CustomTextColorValuePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CustomTextColorValuePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // com.android.settingslib.miuisettings.preference.ValuePreference, miuix.preference.TextPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(16908310);
        if (textView != null) {
            textView.setTextColor(getContext().getColor(R.color.preference_highlight_color));
            textView.setText(getTitle());
        }
    }
}
